package uk.ac.wlv.petmate.screens.appointment.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.koinInject
import uk.ac.wlv.petmate.components.SegmentedToggle
import uk.ac.wlv.petmate.components.SegmentedToggleItem
import uk.ac.wlv.petmate.data.model.AppointmentDay
import uk.ac.wlv.petmate.screens.vet.Components.LocationSearchBottomSheet
import uk.ac.wlv.petmate.viewmodel.AppointmentViewModel
import kotlin.collections.forEachIndexed


@Composable
fun AppointmentTimeSlotPicker(
    appointmentViewModel: AppointmentViewModel,
    days: List<AppointmentDay>,
    selectedType   : Int,
    selectedSlot     : String?,
    onTypeSelected : (Int) -> Unit,
    onSlotSelected : (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Which day tab is active
    var selectedDayIndex by remember { mutableIntStateOf(0) }
    // Which time slot is selected (null = none)

    LaunchedEffect(days) {
        if (days.isNotEmpty()) {
            appointmentViewModel.selectDate(days[0].date.toString())
        }
    }


    Column(modifier = modifier) {

        // ── Visit Type Selector ───────────────────────────────────────
        SegmentedToggle(
            items = listOf(
                SegmentedToggleItem("Clinic Visit", Icons.Default.LocalHospital),
                SegmentedToggleItem("Home Visit",   Icons.Default.Home)
            ),
            selectedIndex  = selectedType,
            onItemSelected = { onTypeSelected(it)
                appointmentViewModel.selectDate(days[selectedDayIndex].date.toString())},
            modifier       = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        // ── Home Address Input (only for home visit) ──────────────────
        if (selectedType == 1) {
            HomeAddressInput(
                onAddressSelected = { address, lat, lon ->
                    appointmentViewModel.updateHomeAddress(
                        address   = address,
                        latitude  = lat,
                        longitude = lon
                    )
                }
            )
            Spacer(Modifier.height(16.dp))
        }

        // ── Day tabs ──────────────────────────────────────────────────
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            days.forEachIndexed { index, day ->
                DayTab(
                    label      = day.label,
                    isSelected = index == selectedDayIndex,
                    onClick    = {
                        selectedDayIndex = index
                        // ← reset slot when day changes
                        onSlotSelected("")
                        appointmentViewModel.resetBookState(isChangeType = false)

                        // ← store selected date in ViewModel
                        appointmentViewModel.selectDate(day.date.toString())



                    }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Time slot grid ────────────────────────────────────────────
        val slots = days[selectedDayIndex].timeSlots
        TimeSlotGrid(
            slots = slots,
            selectedSlot = selectedSlot,
            onSlotSelected = {

                onSlotSelected(it)
            }
        )
    }

}



@Composable
private fun HomeAddressInput(
    onAddressSelected: (address: String, lat: Double?, lon: Double?) -> Unit
) {
    var address by remember { mutableStateOf("") }
    var showSearchSheet by remember { mutableStateOf(false) }

    // ── Address field ─────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
            .clickable { showSearchSheet = true }
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector        = Icons.Default.LocationOn,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.primary,
                modifier           = Modifier.size(20.dp)
            )
            Text(text = address.ifEmpty { "Enter your home address" }, color = if (address.isEmpty())
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                else
                    MaterialTheme.colorScheme.onSurface, fontSize = 13.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }

    // ── Location search bottom sheet ──────────────────────────────────
    if (showSearchSheet) {
        LocationSearchBottomSheet(
            vetViewModel = koinInject(),
            onLocationSelected = { lat, lon, name ->
                address = name
                onAddressSelected(name, lat, lon)
                showSearchSheet = false
            },
            onDismiss = { showSearchSheet = false }
        )
    }
}

// ─── Day tab ─────────────────────────────────────────────────────────────────

@Composable
private fun DayTab(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color(0xFF1A1A2E) else Color(0xFF9E9E9E)
        )
        Spacer(Modifier.height(4.dp))
        // Underline indicator
        if (isSelected) {
            Box(
                modifier = Modifier
                    .height(2.dp)
                    .width(60.dp)
                    .background(Color(0xFF1A1A2E), RoundedCornerShape(1.dp))
            )
        } else {
            Spacer(Modifier.height(2.dp))
        }
    }
}





