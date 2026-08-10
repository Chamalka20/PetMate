package uk.ac.wlv.petmate.screens.mainScreens.medlog.Components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.ac.wlv.petmate.components.NetworkCircleImage
import uk.ac.wlv.petmate.core.utils.toReadableDate
import uk.ac.wlv.petmate.data.model.Appointment
import uk.ac.wlv.petmate.data.model.AppointmentStatus
import uk.ac.wlv.petmate.ui.theme.StarYellow

@Composable
fun AppointmentCard(
    appointment      : Appointment,
    onClick          : () -> Unit = {},
    onMapClick       : () -> Unit = {},
    onRescheduleClick: () -> Unit = {},
    showActions      : Boolean    = true
) {
    Card(
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier  = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ── Visit type badge ──────────────────────────────────────
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier              = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                    )
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = when (appointment.type) {
                        1    -> Icons.Default.Home
                        2    -> Icons.Default.Emergency
                        else -> Icons.Default.LocalHospital
                    },
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.primary,
                    modifier           = Modifier.size(14.dp)
                )
                Text(
                    text     = when (appointment.type) {
                        0    -> "Clinic Appointment"
                        1    -> "In-Home Visit"
                        2    -> "Emergency"
                        else -> "Appointment"
                    },
                    fontSize = 12.sp,
                    color    = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Vet info ──────────────────────────────────────────────
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                NetworkCircleImage(
                    imageUrl           = appointment.vetImageUrl,
                    contentDescription = appointment.vetName,
                    size = 56.dp
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text       = appointment.vetName,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 15.sp
                    )
                    Text(
                        text     = "${appointment.consultationFee.toInt()} years Exp.",
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
                            modifier           = Modifier.size(12.dp)
                        )
                        Text(
                            text       = "5.0",
                            fontSize   = 11.sp,
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

            Spacer(modifier = Modifier.height(8.dp))

            // ── Status badge ──────────────────────────────────────────
            AppointmentStatusBadge(status = appointment.status)

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // ── Location ──────────────────────────────────────────────
            AppointmentInfoRow(
                icon  = Icons.Default.LocationOn,
                text  = when (appointment.type) {
                    1    -> appointment.homeAddress ?: "Home address"
                    else -> appointment.clinicAddress ?: "Clinic location"
                }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // ── Price & Date ──────────────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AppointmentInfoRow(
                    icon = Icons.Default.Payments,
                    text = "Price: ${appointment.totalFee.toInt()} EGP"
                )
                AppointmentInfoRow(
                    icon = Icons.Default.CalendarMonth,
                    text = "${appointment.appointmentDate.toReadableDate()} - ${appointment.timeSlot}"
                )
            }

            // ── Action buttons (only for upcoming) ───────────────────
            if (showActions) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier              = Modifier.fillMaxWidth()
                ) {
                    // ── Map button ────────────────────────────────────
                    OutlinedButton(
                        onClick  = onMapClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        shape  = RoundedCornerShape(10.dp),
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.primary
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector        = Icons.Default.Map,
                            contentDescription = null,
                            modifier           = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Map", fontSize = 13.sp)
                    }

                    // ── Reschedule button ─────────────────────────────
                    Button(
                        onClick  = onRescheduleClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        shape  = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector        = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            modifier           = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Reschedule", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun AppointmentStatusBadge(status: Int) {
    val (label, color) = when (AppointmentStatus.fromValue(status)) {
        AppointmentStatus.PENDING            -> "Pending"         to Color(0xFFFFA000)
        AppointmentStatus.COMPLETED          -> "Completed"       to Color(0xFF4CAF50)
        AppointmentStatus.CANCELLED_BY_USER  -> "Cancelled"       to Color(0xFFE53935)
        AppointmentStatus.CANCELLED_BY_VET   -> "Vet Unavailable" to Color(0xFF9E9E9E)
        AppointmentStatus.CANCELLED_BY_ADMIN -> "Unavailable"     to Color(0xFF9E9E9E)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text       = label,
            fontSize   = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color      = color
        )
    }
}

// ── Appointment Info Row ──────────────────────────────────────────────────────

@Composable
private fun AppointmentInfoRow(
    icon: ImageVector,
    text: String
) {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = MaterialTheme.colorScheme.primary,
            modifier           = Modifier.size(14.dp)
        )
        Text(
            text     = text,
            fontSize = 12.sp,
            color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}
