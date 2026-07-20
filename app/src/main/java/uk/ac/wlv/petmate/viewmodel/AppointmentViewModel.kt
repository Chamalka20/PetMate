package uk.ac.wlv.petmate.viewmodel

import android.R
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import uk.ac.wlv.petmate.core.SnackbarController
import uk.ac.wlv.petmate.core.UiState
import uk.ac.wlv.petmate.core.utils.safeApiCall
import uk.ac.wlv.petmate.data.model.Appointment
import uk.ac.wlv.petmate.data.model.AppointmentActionResponse
import uk.ac.wlv.petmate.data.model.AvailableSlotsDto
import uk.ac.wlv.petmate.data.model.BookAppointmentRequest
import uk.ac.wlv.petmate.data.model.CancelAppointmentRequest
import uk.ac.wlv.petmate.data.model.Pet
import uk.ac.wlv.petmate.data.model.UpdatePaymentRequest
import uk.ac.wlv.petmate.data.repository.AppointmentRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(FlowPreview::class)
class AppointmentViewModel(
    private val repository: AppointmentRepository
) :  BaseViewModel() {

    // ── Book appointment ──────────────────────────────────────────────
    private val _bookState = MutableStateFlow<UiState<Appointment>>(UiState.Idle)
    val bookState: StateFlow<UiState<Appointment>> = _bookState

    // ── My appointments ───────────────────────────────────────────────
    private val _myAppointmentsState = MutableStateFlow<UiState<List<Appointment>>>(UiState.Idle)
    val myAppointmentsState: StateFlow<UiState<List<Appointment>>> = _myAppointmentsState

    // ── Upcoming appointments ─────────────────────────────────────────
    private val _upcomingState = MutableStateFlow<UiState<List<Appointment>>>(UiState.Idle)
    val upcomingState: StateFlow<UiState<List<Appointment>>> = _upcomingState

    // ── Appointment history ───────────────────────────────────────────
    private val _historyList = mutableListOf<Appointment>()
    private val _historyState = MutableStateFlow<UiState<List<Appointment>>>(UiState.Idle)
    val historyState: StateFlow<UiState<List<Appointment>>> = _historyState

    private val _isLoadingMoreHistory = MutableStateFlow(false)
    val isLoadingMoreHistory: StateFlow<Boolean> = _isLoadingMoreHistory
    val historyIsLastPage get() = repository.historyIsLastPage

    private val _historySearchQuery = MutableStateFlow("")
    val historySearchQuery: StateFlow<String> = _historySearchQuery

    private val _historySelectedDate = MutableStateFlow<String?>(null)
    val historySelectedDate: StateFlow<String?> = _historySelectedDate

    // ── Single appointment ────────────────────────────────────────────
    private val _appointmentState = MutableStateFlow<UiState<Appointment>>(UiState.Idle)
    val appointmentState: StateFlow<UiState<Appointment>> = _appointmentState

    // ── Available slots ───────────────────────────────────────────────
    private val _slotsState = MutableStateFlow<UiState<AvailableSlotsDto>>(UiState.Idle)
    val slotsState: StateFlow<UiState<AvailableSlotsDto>> = _slotsState

    // ── Cancel state ──────────────────────────────────────────────────
    private val _cancelState = MutableStateFlow<UiState<Boolean>>(UiState.Idle)
    val cancelState: StateFlow<UiState<Boolean>> = _cancelState

    // ── Selected date for booking ─────────────────────────────────────
    private val _selectedDate = MutableStateFlow<String?>(null)
    val selectedDate: StateFlow<String?> = _selectedDate

    // ── Selected slot for booking ─────────────────────────────────────
    private val _selectedSlot = MutableStateFlow<String?>(null)
    val selectedSlot: StateFlow<String?> = _selectedSlot

    // ── Selected appointment type ─────────────────────────────────────
    private val _selectedType = MutableStateFlow(0) // 0=Clinic, 1=Home, 2=Emergency
    val selectedType: StateFlow<Int> = _selectedType

    // ── Home visit state ──────────────────────────────────────────────────
    private val _homeAddress = MutableStateFlow("")
    val homeAddress: StateFlow<String> = _homeAddress

    private val _homeLatitude = MutableStateFlow<Double?>(null)
    val homeLatitude: StateFlow<Double?> = _homeLatitude

    private val _homeLongitude = MutableStateFlow<Double?>(null)
    val homeLongitude: StateFlow<Double?> = _homeLongitude

    private val _selectedPet = MutableStateFlow<Pet?>(null)
    val selectedPet: StateFlow<Pet?> = _selectedPet

    init {
        viewModelScope.launch {
            _historySearchQuery
                .debounce(500)
                .distinctUntilChanged()
                .collect { query ->
                    loadAppointmentHistory(
                        isRefresh = true
                    )
                }
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // Book Appointment
    // ─────────────────────────────────────────────────────────────────
    fun bookAppointment(request: BookAppointmentRequest) {
        viewModelScope.launch {
            _bookState.value = UiState.Loading
            val result = safeApiCall {
                repository.bookAppointment(request)
            }
            result
                .onSuccess { appointment ->

                    _bookState.value = UiState.Success(appointment)

                    // ── Refresh upcoming after booking ────────────────────
                    loadUpcomingAppointments()
                }
                .onFailure { exception ->
                    val message = exception.message ?: "Failed to bookAppointment"
                    showError(message)
                    _bookState.value = UiState.Error(message)
                }

        }
    }


    // ─────────────────────────────────────────────────────────────────
    // Load Upcoming Appointments
    // ─────────────────────────────────────────────────────────────────
    fun loadUpcomingAppointments() {
        viewModelScope.launch {
            _upcomingState.value = UiState.Loading

            val result = safeApiCall {
                repository.getUpcomingAppointments()
            }
            result
                .onSuccess { appointments ->

                    _upcomingState.value = UiState.Success(appointments)
                }
                .onFailure { exception ->

                    _myAppointmentsState.value =
                        UiState.Error(exception.message ?: "Failed to load Upcoming Appointments")
                }

        }
    }

    // ─────────────────────────────────────────────────────────────────
    // Load Appointment History
    // ─────────────────────────────────────────────────────────────────
    fun loadAppointmentHistory(isRefresh: Boolean = false ,vetName: String? = null,
                               appointmentDate: String? = null) {
        viewModelScope.launch {
            if (isRefresh) {
                _historyList.clear()

            }
            _historyState.value = UiState.Loading

            val result = safeApiCall {
                repository.getAppointmentHistory( isRefresh = isRefresh,
                    vetName = _historySearchQuery.value.ifBlank { null },
                    appointmentDate = _historySelectedDate.value)
            }
            result.onSuccess { appointments ->
                _historyList.addAll(appointments)
                _historyState.value = UiState.Success(_historyList.toList())
            }.onFailure { exception ->
                _myAppointmentsState.value =
                    UiState.Error(exception.message ?: "Failed to load  Appointments History")
            }
        }
    }

    fun loadMoreHistory() {
        if (historyIsLastPage || _isLoadingMoreHistory.value) return
        viewModelScope.launch {
            _isLoadingMoreHistory.value = true
            val result = safeApiCall {
                repository.getAppointmentHistory(isRefresh = false,  vetName = _historySearchQuery.value.ifBlank { null },
                    appointmentDate = _historySelectedDate.value)
            }
            result.onSuccess { appointments ->
                _historyList.addAll(appointments)
                _historyState.value = UiState.Success(_historyList.toList())
            }.onFailure { exception ->
                _myAppointmentsState.value =
                    UiState.Error(exception.message ?: "Failed to load  Appointments History")
            }
            _isLoadingMoreHistory.value = false
        }
    }

    fun updateHistorySearchQuery(query: String) {
        _historySearchQuery.value = query
    }

    fun updateHistorySelectedDate(date: String?) {

        _historySelectedDate.value = date

        loadAppointmentHistory(
            isRefresh = true
        )
    }

    fun clearHistoryFilters() {
        _historySearchQuery.value   = ""
        _historySelectedDate.value  = null
    }

    // ─────────────────────────────────────────────────────────────────
    // Load Single Appointment
    // ─────────────────────────────────────────────────────────────────
    fun loadAppointment(id: Int) {
        viewModelScope.launch {
            _appointmentState.value = UiState.Loading

            val result = safeApiCall {
                repository.getAppointment(id)
            }
            result
                .onSuccess { appointment ->

                    _appointmentState.value = UiState.Success(appointment)
                }
                .onFailure { exception ->

                    _myAppointmentsState.value =
                        UiState.Error(exception.message ?: "Failed to load  Appointment")
                }


        }
    }

    fun updateHomeAddress(
        address  : String,
        latitude : Double?,
        longitude: Double?
    ) {
        _homeAddress.value   = address
        _homeLatitude.value  = latitude
        _homeLongitude.value = longitude
    }

    // ─────────────────────────────────────────────────────────────────
    // Cancel Appointment
    // ─────────────────────────────────────────────────────────────────
    fun cancelAppointment(id: Int, reason: String,cancelledBy: String) {
        viewModelScope.launch {
            _cancelState.value = UiState.Loading
            val result = safeApiCall {
                repository.cancelAppointment(
                    id = id,
                    request = CancelAppointmentRequest(reason = reason, cancelledBy =cancelledBy )
                )
            }

            result
                .onSuccess {  response->

                    _cancelState.value = UiState.Success(response.success)
                    SnackbarController.showSuccess(
                        response.message
                    )
                    // ── Refresh lists after cancel ────────────────────────
                    loadUpcomingAppointments()
                    loadAppointmentHistory(isRefresh = true)
                }
                .onFailure { exception ->
                    SnackbarController.showError(exception.message ?: "Failed to cancel Appointment")
                    _myAppointmentsState.value =
                        UiState.Error(exception.message ?: "Failed to cancel Appointment")
                }

        }
    }

    // ─────────────────────────────────────────────────────────────────
    // Update Payment
    // ─────────────────────────────────────────────────────────────────
    fun updatePayment(
        id: Int,
        paymentStatus: Int,
        paymentMethod: String?
    ) {
        viewModelScope.launch {
            val result = safeApiCall {
                repository.updatePayment(
                    id = id,
                    request = UpdatePaymentRequest(
                        paymentStatus = paymentStatus,
                        paymentMethod = paymentMethod
                    )
                )
            }

        }
    }

    // ─────────────────────────────────────────────────────────────────
    // Load Available Slots
    // ─────────────────────────────────────────────────────────────────
    fun loadAvailableSlots(vetId: Int, date: LocalDate) {
        viewModelScope.launch {
            _slotsState.value = UiState.Loading
            val dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
            val result = safeApiCall {
                repository.getAvailableSlots(vetId, dateString)
            }

            result
                .onSuccess { slots ->

                    _slotsState.value = UiState.Success(slots)
                }
                .onFailure { exception ->

                    _myAppointmentsState.value =
                        UiState.Error(exception.message ?: "Failed to load Available Slots")
                }

        }
    }

    // ─────────────────────────────────────────────────────────────────
    // UI Selection Helpers
    // ─────────────────────────────────────────────────────────────────
    fun selectDate(date: String) {
        _selectedDate.value = date
    }

    fun selectSlot(slot: String) {
        _selectedSlot.value = slot
    }

    fun selectType(type: Int) {
        _selectedType.value = type
    }

    fun selectPet(pet: Pet) {
        _selectedPet.value  = pet
    }

    fun resetBookState(isChangeType: Boolean = true, isGoBack: Boolean = true,clearSelectDate: Boolean = false) {
        _bookState.value = UiState.Idle
        if(isGoBack) {
            if(clearSelectDate) {
                _selectedDate.value = null
            }
            _selectedSlot.value = null
            _homeAddress.value = ""
            _homeLatitude.value = null
            _homeLongitude.value = null
            _selectedPet.value = null
        }
        if(isChangeType){
            _selectedType.value = 0
        }

    }

    fun resetCancelState() {
        _cancelState.value = UiState.Idle
    }
}
