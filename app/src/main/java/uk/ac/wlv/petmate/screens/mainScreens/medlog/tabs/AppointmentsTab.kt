package uk.ac.wlv.petmate.screens.mainScreens.medlog.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import uk.ac.wlv.petmate.components.ErrorRow
import uk.ac.wlv.petmate.core.UiState
import uk.ac.wlv.petmate.data.model.Appointment
import uk.ac.wlv.petmate.screens.mainScreens.medlog.Components.AppointmentCard
import uk.ac.wlv.petmate.screens.mainScreens.medlog.Components.AppointmentGridCard
import uk.ac.wlv.petmate.screens.mainScreens.medlog.Components.AppointmentGridShimmer
import uk.ac.wlv.petmate.screens.mainScreens.medlog.Components.AppointmentShimmer
import uk.ac.wlv.petmate.screens.mainScreens.medlog.Components.EmptyAppointments
import kotlin.collections.chunked
import kotlin.collections.forEach

@Composable
 fun AppointmentsTab(
    upcomingState     : UiState<List<Appointment>>,
    historyState      : UiState<List<Appointment>>,
    onAppointmentClick: (Appointment) -> Unit,
    onMapClick        : (Appointment) -> Unit,
    onRescheduleClick : (Appointment) -> Unit,
    isLoadingMoreHistory: Boolean,
    rootNavController:NavController,
    onRetryUpcoming   : () -> Unit,
    onRetryHistory    : () -> Unit
) {
    LazyColumn(
        modifier            = Modifier.fillMaxSize(),
        contentPadding      = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // ── Upcoming Appointments ─────────────────────────────────────
        item {
            Text(
                text       = "Upcoming Appointments",
                fontWeight = FontWeight.Bold,
                fontSize   = 16.sp
            )
        }

        when (upcomingState) {
            is UiState.Loading -> {
                item { AppointmentShimmer() }
            }
            is UiState.Success -> {
                val upcoming = upcomingState.data
                if (upcoming.isEmpty()) {
                    item { EmptyAppointments(label = "No upcoming appointments") }
                } else {
                    items(upcoming, key = { it.id }) { appointment ->
                        AppointmentCard(
                            appointment       = appointment,
                            onClick           = { onAppointmentClick(appointment) },
                            onMapClick        = { onMapClick(appointment) },
                            onRescheduleClick = { onRescheduleClick(appointment) }
                        )
                    }
                }
            }
            is UiState.Error -> {
                item {
                    ErrorRow(onRetry = onRetryUpcoming)
                }
            }
            else -> Unit
        }

        // ── Previous Appointments ─────────────────────────────────────
        item {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier              = Modifier.fillMaxWidth()
            ) {
                Text(
                    text       = "Previous Appointments",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp
                )
                // ── View More ─────────────────────────────────────────────
                TextButton(onClick = {
                    rootNavController.navigate("appointmentHistory")
                } ) {
                    Text(
                        text     = "View More",
                        fontSize = 13.sp,
                        color    = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        imageVector        = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint               = MaterialTheme.colorScheme.primary,
                        modifier           = Modifier.size(12.dp)
                    )
                }
            }
        }


        when (historyState) {
            is UiState.Loading -> {
                item { AppointmentGridShimmer() }
            }
            is UiState.Success -> {
                val history = historyState.data.take(4)  // ← show only 4 in grid
                if (history.isEmpty()) {
                    item { EmptyAppointments(label = "No previous appointments") }
                } else {
                    item {
                        // ── 2 column grid ─────────────────────────────────
                        val rows = history.chunked(2)
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            rows.forEach { rowItems ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier              = Modifier.fillMaxWidth()
                                ) {
                                    rowItems.forEach { appointment ->
                                        AppointmentGridCard(
                                            appointment = appointment,
                                            onClick     = {  },
                                            modifier    = Modifier.weight(1f)
                                        )
                                    }

                                    if (rowItems.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
            is UiState.Error -> {
                item { ErrorRow(onRetry = onRetryHistory) }
            }
            else -> Unit
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}
