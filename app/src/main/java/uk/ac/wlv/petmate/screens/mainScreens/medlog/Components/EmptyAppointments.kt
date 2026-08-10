package uk.ac.wlv.petmate.screens.mainScreens.medlog.Components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
 fun EmptyAppointments(label: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier         = Modifier
            .fillMaxWidth()
            .padding(32.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector        = Icons.Default.CalendarMonth,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                modifier           = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text     = label,
                fontSize = 14.sp,
                color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }
}