package uk.ac.wlv.petmate.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.ac.wlv.petmate.data.model.AppointmentStatus

// ── Appointment Status Badge ──────────────────────────────────────────────────

@Composable
fun AppointmentStatusBadge(status: Int) {
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
fun AppointmentInfoRow(
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