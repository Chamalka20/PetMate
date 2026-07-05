package uk.ac.wlv.petmate.screens.appointment.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import uk.ac.wlv.petmate.components.shimmers.shimmerBrush

@Composable
fun AppointmentTimeSlotShimmer(
    modifier: Modifier = Modifier
) {

    val shimmerBrush = shimmerBrush()
    Column(modifier = modifier) {

        // ── Day tabs shimmer ──────────────────────────────────────────
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            repeat(5) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Tab label
                    Box(
                        modifier = Modifier
                            .width(48.dp)
                            .height(14.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(shimmerBrush)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // Underline placeholder
                    Spacer(modifier = Modifier.height(2.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Time slot grid shimmer ────────────────────────────────────
        // 5 rows × 4 chips
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            repeat(5) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    repeat(4) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(shimmerBrush)
                        )
                    }
                }
            }
        }
    }
}