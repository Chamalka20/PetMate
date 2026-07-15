package uk.ac.wlv.petmate.screens.appointment.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.ac.wlv.petmate.components.AppointmentInfoRow
import uk.ac.wlv.petmate.components.AppointmentStatusBadge
import uk.ac.wlv.petmate.components.NetworkCircleImage
import uk.ac.wlv.petmate.core.utils.toReadableDate
import uk.ac.wlv.petmate.data.model.Appointment
import uk.ac.wlv.petmate.data.model.AppointmentStatus
import uk.ac.wlv.petmate.ui.theme.StarYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentDetailsBottomSheet(
    appointment      : Appointment,
    onDismiss        : () -> Unit,
    onReschedule     : () -> Unit,
    onCancel         : () -> Unit
) {

    var showCancelDialog by remember { mutableStateOf(false) }

    if (showCancelDialog) {
        CancelAppointmentDialog(
            appointment = appointment,
            onConfirm   = {
                onCancel()
                onDismiss()
            },
            onDismiss   = { showCancelDialog = false }
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor   = MaterialTheme.colorScheme.surface,
        shape            = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        sheetState       = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {

            // ── Title ─────────────────────────────────────────────────
            Text(
                text       = "Appointment Details",
                fontWeight = FontWeight.Bold,
                fontSize   = 18.sp,
                modifier   = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign  = TextAlign.Center
            )

            // ── Visit type badge ──────────────────────────────────────
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier              = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                    .padding(12.dp)
            ) {
                Icon(
                    imageVector = when (appointment.type) {
                        1    -> Icons.Default.Home
                        2    -> Icons.Default.Emergency
                        else -> Icons.Default.LocalHospital
                    },
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.primary,
                    modifier           = Modifier.size(20.dp)
                )
                Text(
                    text       = when (appointment.type) {
                        0    -> "Clinic Appointment"
                        1    -> "Home Visit"
                        2    -> "Emergency"
                        else -> "Appointment"
                    },
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 14.sp,
                    color      = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Vet info ──────────────────────────────────────────────
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                NetworkCircleImage(
                    imageUrl           = appointment.vetImageUrl,
                    contentDescription = appointment.vetName,
                    size = 60.dp
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text       = appointment.vetName,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 16.sp
                    )
                    Text(
                        text     = "20 years Exp.",
                        fontSize = 12.sp,
                        color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector        = Icons.Default.Star,
                            contentDescription = null,
                            tint               = StarYellow,
                            modifier           = Modifier.size(14.dp)
                        )
                        Text(
                            text       = "5.0",
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text     = "(120+ Ratings)",
                            fontSize = 11.sp,
                            color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }
                }

                // ── Bookmark ──────────────────────────────────────────
                Icon(
                    imageVector        = Icons.Outlined.BookmarkBorder,
                    contentDescription = "Bookmark",
                    tint               = MaterialTheme.colorScheme.primary,
                    modifier           = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Status badge ──────────────────────────────────────────
            AppointmentStatusBadge(status = appointment.status)

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
            )
            Spacer(modifier = Modifier.height(12.dp))

            // ── Location ──────────────────────────────────────────────
            AppointmentInfoRow(
                icon = Icons.Default.LocationOn,
                text = when (appointment.type) {
                    1    -> appointment.homeAddress ?: "Home address"
                    else -> appointment.clinicAddress ?: "Clinic location"
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Price & Date ──────────────────────────────────────────
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AppointmentInfoRow(
                    icon = Icons.Default.Payments,
                    text = "Price: ${appointment.totalFee.toInt()} EGP"
                )
                AppointmentInfoRow(
                    icon = Icons.Default.CalendarMonth,
                    text = "${appointment.appointmentDate.toReadableDate()} - ${appointment.timeSlot}"
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Actions — only for upcoming ───────────────────────────
            val isUpcoming = AppointmentStatus.fromValue(appointment.status) == AppointmentStatus.PENDING

            if (isUpcoming) {
                // ── Reschedule button ─────────────────────────────────
                Button(
                    onClick  = {
                        onReschedule()
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape  = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector        = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        modifier           = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text       = "Reschedule Appointment",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ── Cancel button ─────────────────────────────────────
                OutlinedButton(
                    onClick  = {
                        showCancelDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape  = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFE53935)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFE53935)
                    )
                ) {
                    Icon(
                        imageVector        = Icons.Default.Close,
                        contentDescription = null,
                        modifier           = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text       = "Cancel Appointment",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}