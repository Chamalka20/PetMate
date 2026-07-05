package uk.ac.wlv.petmate.screens.appointment.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.ac.wlv.petmate.data.model.TimeSlot

@Composable
fun TimeSlotGrid(
    slots: List<TimeSlot>,
    selectedSlot: String?,
    onSlotSelected: (String) -> Unit
) {
    val rows = slots.chunked(4)
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        rows.forEach { rowSlots ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                rowSlots.forEach { slot ->
                    TimeSlotChip(
                        time = slot.time,
                        isSelected = slot.time == selectedSlot,
                        onClick = { onSlotSelected(slot.time) },
                        modifier = Modifier.weight(1f),
                        isBooked =slot.isBooked
                    )
                }

                repeat(4 - rowSlots.size) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

// ─── Individual time chip ─────────────────────────────────────────────────────
@Composable
private fun TimeSlotChip(
    time: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isBooked: Boolean = false
) {
    val bgColor   = when {
        isSelected            -> MaterialTheme.colorScheme.primary
        isBooked            -> Color(0xFFF5F5F5)
        else                  -> Color.White
    }
    val textColor = when {
        isSelected            -> Color.White
        isBooked            -> Color(0xFFBBBBBB)
        else                  -> Color(0xFF555555)
    }
    val border    = when {
        isSelected || isBooked -> null
        else                     -> BorderStroke(1.dp, Color(0xFFE0E0E0))
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .then(
                if (border != null) Modifier.border(border, RoundedCornerShape(10.dp))
                else Modifier
            )
            .clickable(
                enabled = !isBooked,
                onClick = onClick
            )
            .padding(vertical = 10.dp)
    ) {
        Text(
            text = time,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}