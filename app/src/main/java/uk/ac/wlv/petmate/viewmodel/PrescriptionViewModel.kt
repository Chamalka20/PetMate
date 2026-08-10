package uk.ac.wlv.petmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uk.ac.wlv.petmate.core.SnackbarController
import uk.ac.wlv.petmate.core.UiState
import uk.ac.wlv.petmate.core.utils.safeApiCall
import uk.ac.wlv.petmate.data.model.Prescription
import uk.ac.wlv.petmate.data.repository.PrescriptionRepository
import kotlin.onFailure

@OptIn(FlowPreview::class)
@HiltViewModel
class PrescriptionViewModel @Inject constructor(
    private val repository: PrescriptionRepository
) : ViewModel() {

    // ── All prescriptions ─────────────────────────────────────────────
    private val _prescriptionsState = MutableStateFlow<UiState<List<Prescription>>>(UiState.Idle)
    val prescriptionsState: StateFlow<UiState<List<Prescription>>> = _prescriptionsState

    // ── Single prescription ───────────────────────────────────────────
    private val _prescriptionState = MutableStateFlow<UiState<Prescription>>(UiState.Idle)
    val prescriptionState: StateFlow<UiState<Prescription>> = _prescriptionState


    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore

    // ── Remove filteredPrescriptions — backend handles filtering ──────
    private val _accumulatedList = mutableListOf<Prescription>()

    private var currentQuery = ""

    val isLastPage get() = repository.isLastPage

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(500)
                .distinctUntilChanged()
                .collect { query ->
                    loadMyPrescriptions(
                        searchQuery = query.ifBlank { null },
                        isRefresh = true
                    )
                }
        }
    }


    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }


    // ── Load all ──────────────────────────────────────────────────────
    fun loadMyPrescriptions(
        searchQuery: String? = null,
        isRefresh: Boolean = true
    ) {
        viewModelScope.launch {
            if (isRefresh) _accumulatedList.clear()
            currentQuery = searchQuery ?: ""
            _prescriptionsState.value = UiState.Loading

            val result = safeApiCall {
                repository.getMyPrescriptions(
                    searchQuery = searchQuery,
                    isRefresh = isRefresh
                )
            }
            result.onSuccess { prescriptions ->
                _accumulatedList.addAll(prescriptions)
                _prescriptionsState.value = UiState.Success(
                    _accumulatedList.toList()
                )
            }.onFailure { exception ->
                _prescriptionsState.value =
                    UiState.Error(exception.message ?: "Failed to load prescriptions")
            }

        }
    }

    // ── Load more ─────────────────────────────────────────────────────
    fun loadMorePrescriptions() {
        if (isLastPage || _isLoadingMore.value) return
        viewModelScope.launch {
            _isLoadingMore.value = true
            val result = safeApiCall {
                repository.getMyPrescriptions(
                    searchQuery = currentQuery.ifBlank { null },
                    isRefresh = false
                )
            }
            result.onSuccess { prescriptions ->
                _accumulatedList.addAll(prescriptions)
                _prescriptionsState.value = UiState.Success(
                    _accumulatedList.toList()
                )
            }.onFailure { exception ->
                _prescriptionsState.value =
                    UiState.Error(exception.message ?: "Failed to load prescriptions")
            }

            _isLoadingMore.value = false
        }
    }

    // ── Load single ───────────────────────────────────────────────────
    fun loadPrescription(id: Int) {
        viewModelScope.launch {
            _prescriptionState.value = UiState.Loading
            val result = safeApiCall {
                repository.getPrescription(id)
            }
            result.onSuccess { prescription->
                _prescriptionState.value = UiState.Success(prescription)
            }
                .onFailure { exception ->
                    _prescriptionState.value =
                        UiState.Error(exception.message ?: "Failed to load prescription")
                }

        }
    }
}

