package uk.ac.wlv.petmate.screens.mainScreens.medlog.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.ac.wlv.petmate.components.AppointmentStatusBadge
import uk.ac.wlv.petmate.components.NetworkCircleImage
import uk.ac.wlv.petmate.core.utils.toReadableDate
import uk.ac.wlv.petmate.data.model.Appointment

@Composable
fun AppointmentGridCard(
    appointment: Appointment,
    onClick    : () -> Unit,
    modifier   : Modifier = Modifier
) {
    Card(
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier  = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // ── Visit type badge ──────────────────────────────────
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier              = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                    )
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = when (appointment.type) {
                        1    -> Icons.Default.Home
                        2    -> Icons.Default.Emergency
                        else -> Icons.Default.LocalHospital
                    },
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.primary,
                    modifier           = Modifier.size(10.dp)
                )
                Text(
                    text     = when (appointment.type) {
                        0    -> "Clinic"
                        1    -> "Home"
                        2    -> "Emergency"
                        else -> "Visit"
                    },
                    fontSize = 10.sp,
                    color    = MaterialTheme.colorScheme.primary
                )
            }

            // ── Vet image + name ──────────────────────────────────
            NetworkCircleImage(
                imageUrl           = appointment.vetImageUrl,
                contentDescription = appointment.vetName,
                size = 44.dp
            )

            Text(
                text       = appointment.vetName,
                fontWeight = FontWeight.Bold,
                fontSize   = 13.sp,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis
            )

            // ── Status badge ──────────────────────────────────────
            AppointmentStatusBadge(status = appointment.status)

            // ── Date ──────────────────────────────────────────────
            Text(
                text     = appointment.appointmentDate.toReadableDate(),
                fontSize = 11.sp,
                color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )

            // ── Price ─────────────────────────────────────────────
            Text(
                text       = "${appointment.totalFee.toInt()} EGP",
                fontSize   = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color      = MaterialTheme.colorScheme.primary
            )
        }
    }
}