package uk.ac.wlv.petmate.viewmodel

import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.ResolvableApiException
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uk.ac.wlv.petmate.core.UiState
import uk.ac.wlv.petmate.core.utils.NetworkObserver
import uk.ac.wlv.petmate.core.utils.safeApiCall
import uk.ac.wlv.petmate.data.model.NominatimResult
import uk.ac.wlv.petmate.data.model.Vet
import uk.ac.wlv.petmate.data.model.VetFilterState
import uk.ac.wlv.petmate.data.repository.LocationSearchRepository
import uk.ac.wlv.petmate.data.repository.VetRepository
import uk.ac.wlv.petmate.screens.vet.haversineDistance
import uk.ac.wlv.petmate.services.LocationService

@OptIn(FlowPreview::class)
@HiltViewModel
class VetViewModel @Inject constructor(private val vetRepository: VetRepository, private val locationService: LocationService, private val locationSearchRepository: LocationSearchRepository, private val networkObserver: NetworkObserver) : BaseViewModel() {



    private val accumulatedVets = mutableListOf<Vet>()

    private val _vetListState = MutableStateFlow<UiState<List<Vet>>>(UiState.Idle)
    val vetListState: StateFlow<UiState<List<Vet>>> = _vetListState

    private val _filteredVetListState = MutableStateFlow<UiState<List<Vet>>>(UiState.Idle)
    val filteredVetListState: StateFlow<UiState<List<Vet>>> = _filteredVetListState

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _filterState = MutableStateFlow(VetFilterState())
    val filterState: StateFlow<VetFilterState> = _filterState

    private val _selectedVetState = MutableStateFlow<UiState<Vet>>(UiState.Idle)
    val selectedVetState: StateFlow<UiState<Vet>> = _selectedVetState.asStateFlow()

    private val _userLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    val userLocation: StateFlow<Pair<Double, Double>?> = _userLocation

    private val _locationSearchResults = MutableStateFlow<List<NominatimResult>>(emptyList())
    val locationSearchResults: StateFlow<List<NominatimResult>> = _locationSearchResults

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    init {
        loadVetList(isRefresh = true,isfilter = false)

            // Debounce search — wait 500ms after user stops typing
            viewModelScope.launch {
                if (!checkInternet(networkObserver)) {
                    return@launch
                }
                _searchQuery
                    .debounce(500)
                    .distinctUntilChanged()
                    .filter { it.length >= 2 }
                    .collect { query ->
                        _isSearching.value = true
                        try {
                            _locationSearchResults.value = locationSearchRepository.searchLocation(query)
                        } catch (e: Exception) {
                            _locationSearchResults.value = emptyList()
                        } finally {
                            _isSearching.value = false
                        }
                    }

        }
    }

    fun isGpsEnabled() = locationService.isGpsEnabled()

    fun checkGpsSettings(
        onResolvable: (ResolvableApiException) -> Unit,
        onAlreadyOn : () -> Unit
    ) = locationService.checkGpsSettings(onResolvable, onAlreadyOn)

    suspend fun getBestLocation() = locationService.getBestLocation()


    // ── Nearby vets — recalculates when EITHER vets OR location changes ───
    val nearbyVets: StateFlow<List<Pair<Vet, Double>>> =
        combine(_filteredVetListState, _userLocation) { state, location ->
            val vets = (state as? UiState.Success)?.data ?: return@combine emptyList()
            val loc  = location ?: return@combine emptyList()

            vets.filter { it.latitude != null && it.longitude != null }
                .map { vet ->
                    val dist = haversineDistance(
                        loc.first, loc.second,
                        vet.latitude!!, vet.longitude!!
                    )
                    vet to dist
                }.filter { (_, dist) -> dist <= 50.0 }
                .sortedBy { it.second }
                .take(10)
        }.stateIn(
            scope         = viewModelScope,
            started       = SharingStarted.WhileSubscribed(5000),
            initialValue  = emptyList()
        )

    // ── Fetch location and update state ───────────────────────────────────
    suspend fun fetchUserLocation() {
        getBestLocation()?.let { loc ->
            _userLocation.value = loc.latitude to loc.longitude
        }
    }

    fun updateUserLocation(latitude: Double, longitude: Double) {
        _userLocation.value = latitude to longitude
    }

    fun loadVetList(isRefresh: Boolean = false, isfilter: Boolean ) {
        viewModelScope.launch {
            if (!checkInternet(networkObserver)) {
                return@launch
            }
         if(isfilter) {
            if (isRefresh) {
                accumulatedVets.clear()
                _filteredVetListState.value = UiState.Loading
            } else {
                _isLoadingMore.value = true
            }

            val result = safeApiCall {
                vetRepository.getVetList(
                    isRefresh = isRefresh,
                    filter = _filterState.value,
                    searchQuery = _searchQuery.value
                )
            }

            result.onSuccess { vets ->

                if (isRefresh) {
                    accumulatedVets.clear()
                }

                accumulatedVets.addAll(vets)

                _filteredVetListState.value = UiState.Success(accumulatedVets.toList())

            }.onFailure { exception ->
                _filteredVetListState.value = UiState.Error(exception.message ?: "Failed to load vets")
            }

            _isLoadingMore.value = false
        }else {

            val result = safeApiCall {
                vetRepository.getVetList(
                )
            }

            result.onSuccess { vets ->

                if (isRefresh) {
                    accumulatedVets.clear()
                }

                accumulatedVets.addAll(vets)

                _vetListState.value = UiState.Success(accumulatedVets.toList())

            }.onFailure { exception ->
                _vetListState.value = UiState.Error(exception.message ?: "Failed to load vets")
            }

        }}
    }

    fun applyFilter(filter: VetFilterState) {
        _filterState.value = filter
        loadVetList(isRefresh = true,isfilter = true)
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        loadVetList(isRefresh = true,isfilter = true)
    }

    fun loadMoreVets() {
        if (_isLoadingMore.value) return

        loadVetList(isRefresh = false,isfilter = true)
    }

    fun refresh() = loadVetList(isfilter = true)

    fun loadVet(vetId: Int) {
        viewModelScope.launch {
            if (!checkInternet(networkObserver)) {
                return@launch
            }
            _selectedVetState.value = UiState.Loading
            val result = safeApiCall {vetRepository.getVet( vetId)}
            result.onSuccess { vet -> _selectedVetState.value = UiState.Success(vet) }.onFailure {
                    exception ->
                _selectedVetState.value = UiState.Error(exception.message ?: "Failed to load Vet")
            }
        }
    }


    fun searchLocation(query: String) {
        _searchQuery.value = query
    }

    fun clearLocationSearch() {
        _searchQuery.value          = ""
        _locationSearchResults.value = emptyList()
    }
}