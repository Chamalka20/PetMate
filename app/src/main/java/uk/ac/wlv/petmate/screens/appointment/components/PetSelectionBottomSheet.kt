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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pets
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.ac.wlv.petmate.components.NetworkCircleImage
import uk.ac.wlv.petmate.data.model.Pet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetSelectionBottomSheet(
    pets          : List<Pet>,
    selectedPetId : Int?,
    onPetSelected : (Pet) -> Unit,
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
                    text       = "Select Pet",
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

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
            )
            Spacer(modifier = Modifier.height(12.dp))

            // ── Pet list ──────────────────────────────────────────────
            if (pets.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier         = Modifier
                        .fillMaxWidth()
                        .padding(32.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector        = Icons.Default.Pets,
                            contentDescription = null,
                            tint               = MaterialTheme.colorScheme.onSurface
                                .copy(alpha = 0.3f),
                            modifier           = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text     = "No pets found",
                            color    = MaterialTheme.colorScheme.onSurface
                                .copy(alpha = 0.4f),
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier            = Modifier.fillMaxWidth()
                ) {
                    items(pets, key = { it.id }) { pet ->
                        PetSelectionItem(
                            pet        = pet,
                            isSelected = pet.id == selectedPetId,
                            onClick    = {
                                onPetSelected(pet)
                                onDismiss()
                            }
                        )
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.onSurface
                                .copy(alpha = 0.06f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ── Pet Selection Item ────────────────────────────────────────────────────────

@Composable
private fun PetSelectionItem(
    pet       : Pet,
    isSelected: Boolean,
    onClick   : () -> Unit
) {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier              = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(
                if (isSelected)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.06f)
                else
                    Color.Transparent
            )
            .padding(vertical = 12.dp, horizontal = 4.dp)
    ) {
        // ── Pet image ─────────────────────────────────────────────────
        Box {
            NetworkCircleImage(
                imageUrl           = pet.imageUrl,
                contentDescription = pet.name,
               size = 4.dp
            )

            // ── Selected checkmark ────────────────────────────────────
            if (isSelected) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier         = Modifier
                        .size(18.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector        = Icons.Default.Check,
                        contentDescription = null,
                        tint               = Color.White,
                        modifier           = Modifier.size(12.dp)
                    )
                }
            }
        }

        // ── Pet info ──────────────────────────────────────────────────
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = pet.name,
                fontWeight = FontWeight.SemiBold,
                fontSize   = 14.sp,
                color      = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface
            )
            Text(
                text     = "${pet.type ?: ""} • ${pet.breed}",
                fontSize = 12.sp,
                color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }

        if (isSelected) {
            Icon(
                imageVector        = Icons.Default.CheckCircle,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.primary,
                modifier           = Modifier.size(20.dp)
            )
        }
    }
}