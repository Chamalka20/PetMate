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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.ac.wlv.petmate.data.model.Vet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisitTypeBottomSheet(
    vet           : Vet,
    onTypeSelected: (Int) -> Unit,
    onDismiss     : () -> Unit
) {
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

            // ── Header ────────────────────────────────────────────────
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier              = Modifier.fillMaxWidth()
            ) {
                Text(
                    text       = "Select Visit Type",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 18.sp
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector        = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text     = "How would you like to see ${vet.name ?: "the vet"}?",
                fontSize = 13.sp,
                color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // ── Visit type options ────────────────────────────────────
            VisitTypeOption(
                icon        = Icons.Default.LocalHospital,
                title       = "Clinic Visit",
                description = "Visit the vet at their clinic",
                onClick     = {
                    onTypeSelected(0)
                    onDismiss()
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            VisitTypeOption(
                icon        = Icons.Default.Home,
                title       = "Home Visit",
                description = "The vet comes to your home",
                onClick     = {
                    onTypeSelected(1)
                    onDismiss()
                }
            )



        }
    }
}

// ── Visit Type Option ─────────────────────────────────────────────────────────

@Composable
private fun VisitTypeOption(
    icon       : ImageVector,
    title      : String,
    description: String,
    color      : Color = MaterialTheme.colorScheme.primary,
    onClick    : () -> Unit
) {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier              = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.06f))
            .border(1.dp, color.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        // ── Icon ──────────────────────────────────────────────────────
        Box(
            contentAlignment = Alignment.Center,
            modifier         = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color.copy(alpha = 0.12f))
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = color,
                modifier           = Modifier.size(24.dp)
            )
        }

        // ── Text ──────────────────────────────────────────────────────
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = title,
                fontWeight = FontWeight.SemiBold,
                fontSize   = 15.sp,
                color      = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text     = description,
                fontSize = 12.sp,
                color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )

        }

        // ── Arrow ─────────────────────────────────────────────────────
        Icon(
            imageVector        = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint               = color,
            modifier           = Modifier.size(16.dp)
        )
    }
}