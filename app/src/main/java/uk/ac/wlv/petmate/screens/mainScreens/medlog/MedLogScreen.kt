package uk.ac.wlv.petmate.screens.mainScreens.medlog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import uk.ac.wlv.petmate.core.UiState
import uk.ac.wlv.petmate.data.model.Appointment
import uk.ac.wlv.petmate.data.model.CancelledBy
import uk.ac.wlv.petmate.screens.appointment.components.AppointmentDetailsBottomSheet
import uk.ac.wlv.petmate.screens.mainScreens.medlog.tabs.AppointmentsTab
import uk.ac.wlv.petmate.screens.mainScreens.medlog.tabs.PrescriptionsTab
import uk.ac.wlv.petmate.viewmodel.AppointmentViewModel
import uk.ac.wlv.petmate.viewmodel.PrescriptionViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedLogScreen(
    appointmentViewModel: AppointmentViewModel,
    prescriptionViewModel: PrescriptionViewModel,
    rootNavController:NavController
) {
    val upcomingState by appointmentViewModel.upcomingState.collectAsState()
    val historyState  by appointmentViewModel.historyState.collectAsState()
    val cancelState by appointmentViewModel.cancelState.collectAsState()
    val isLoadingMoreHistory by appointmentViewModel.isLoadingMoreHistory.collectAsState()

    // ── Tab state ─────────────────────────────────────────────────────
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Appointments", "Prescriptions", "Lab Results")
    var selectedAppointment by remember { mutableStateOf<Appointment?>(null) }

    LaunchedEffect(Unit) {
        appointmentViewModel.loadUpcomingAppointments()
        appointmentViewModel.loadAppointmentHistory(isRefresh = true)
    }

    LaunchedEffect(cancelState) {
        when (cancelState) {
            is UiState.Success -> {
                selectedAppointment = null
                appointmentViewModel.resetCancelState()
            }
            is UiState.Error -> {
                appointmentViewModel.resetCancelState()
            }
            else -> Unit
        }
    }




    selectedAppointment?.let { appointment ->
        AppointmentDetailsBottomSheet(
            appointment  = appointment,
            onDismiss    = { selectedAppointment = null },
            onReschedule = {

            },
            onCancel     = {
                appointmentViewModel.cancelAppointment(
                    id          = appointment.id,
                    reason      = "Cancelled by user",
                    cancelledBy = CancelledBy.USER.value
                )
            }
        )
    }


    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0.dp),
        topBar         = {
            TopAppBar(
                title = {
                    Text(
                        text       = "MedLog",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 18.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                windowInsets = WindowInsets(0.dp)

            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ── Tab Row ───────────────────────────────────────────────
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor   = MaterialTheme.colorScheme.surface,
                contentColor     = MaterialTheme.colorScheme.primary,
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick  = { selectedTab = index },
                        text     = {
                            Text(
                                text       = title,
                                fontSize   = 13.sp,
                                fontWeight = if (selectedTab == index)
                                    FontWeight.SemiBold
                                else
                                    FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // ── Tab Content ───────────────────────────────────────────
            when (selectedTab) {
                0 -> AppointmentsTab(
                    upcomingState     = upcomingState,
                    historyState      = historyState,
                    onAppointmentClick = { appointment -> selectedAppointment = appointment },
                    onMapClick        = {},
                    onRescheduleClick = {},
                    onRetryUpcoming   = { appointmentViewModel.loadUpcomingAppointments() },
                    onRetryHistory    = { appointmentViewModel.loadAppointmentHistory() },
                    isLoadingMoreHistory = isLoadingMoreHistory,
                    rootNavController = rootNavController
                )
                1 -> PrescriptionsTab(
                    prescriptionViewModel = prescriptionViewModel,

                )
                2 -> ComingSoonTab(label = "Lab Results")
            }
        }
    }
}


// ── Coming Soon Tab ───────────────────────────────────────────────────────────

@Composable
private fun ComingSoonTab(label: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier         = Modifier.fillMaxSize()
    ) {
        Text(
            text     = "$label coming soon",
            fontSize = 14.sp,
            color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
    }
}


