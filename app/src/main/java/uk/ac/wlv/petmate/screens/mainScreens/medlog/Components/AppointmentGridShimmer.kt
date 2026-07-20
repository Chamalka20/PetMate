package uk.ac.wlv.petmate.screens.mainScreens.medlog.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import uk.ac.wlv.petmate.components.shimmers.shimmerBrush

@Composable
 fun AppointmentGridShimmer() {
    val brush = shimmerBrush()
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier              = Modifier.fillMaxWidth()
    ) {
        repeat(2) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(brush)
            )
        }
    }
}