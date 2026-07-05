package uk.ac.wlv.petmate.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class SegmentedToggleItem(
    val label: String,
    val icon : ImageVector
)

@Composable
fun SegmentedToggle(
    items          : List<SegmentedToggleItem>,
    selectedIndex  : Int,
    onItemSelected : (Int) -> Unit,
    modifier       : Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F5F5))
            .padding(4.dp)
    ) {
        items.forEachIndexed { index, item ->
            val isSelected = index == selectedIndex

            Box(
                contentAlignment = Alignment.Center,
                modifier         = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .shadow(
                        elevation = if (isSelected) 2.dp else 0.dp,
                        shape     = RoundedCornerShape(10.dp)
                    )
                    .background(
                        if (isSelected) Color.White
                        else            Color.Transparent
                    )
                    .clickable { onItemSelected(index) }
                    .padding(vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector        = item.icon,
                        contentDescription = item.label,
                        tint               = if (isSelected)
                            MaterialTheme.colorScheme.primary
                        else
                            Color.Gray,
                        modifier           = Modifier.size(16.dp)
                    )
                    Text(
                        text       = item.label,
                        fontSize   = 13.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold
                        else            FontWeight.Normal,
                        color      = if (isSelected)
                            MaterialTheme.colorScheme.primary
                        else
                            Color.Gray
                    )
                }
            }
        }
    }
}