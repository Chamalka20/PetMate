package uk.ac.wlv.petmate.screens.vet.Components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.ac.wlv.petmate.components.NetworkCircleImage
import uk.ac.wlv.petmate.data.model.Vet
import uk.ac.wlv.petmate.ui.theme.AvailableGreen
import uk.ac.wlv.petmate.ui.theme.StarYellow

@Composable
fun VetCard(
    vet: Vet,
    onClick: () -> Unit = {},

    onBookAppointment: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(

            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal =  16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            // ── Header ────────────────────────────────────────────────────────
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                NetworkCircleImage(
                    imageUrl = vet.imageUrl,
                    contentDescription = ""
                )

                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = vet.name ?: "Unknown Vet",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${vet.experienceYears ?: 0} years Exp.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    RatingRow(rating = vet.rating ?: 0.0)
                }
                Icon(
                    imageVector = Icons.Outlined.BookmarkBorder,
                    contentDescription = "Bookmark",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ── Availability ──────────────────────────────────────────────────
            AvailabilityBadge(workingTime = vet.workingTime)

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                thickness = 1.dp
            )
            Spacer(modifier = Modifier.height(8.dp))

            // ── Location ──────────────────────────────────────────────────────
            InfoRow(
                icon = Icons.Default.LocationOn,
                text = vet.location ?: "Location not available",
                // primary → PetOrange / PetOrangeDark
                iconTint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(6.dp))

            // ── Price & Wait ──────────────────────────────────────────────────
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                InfoRow(
                    icon = Icons.Default.AttachMoney,
                    text = "Price: ${vet.price ?: 0} EGP",
                    iconTint = MaterialTheme.colorScheme.primary
                )
                InfoRow(
                    icon = Icons.Default.AccessTime,
                    text = "Waiting time: ${vet.waitingTimeMinutes ?: 0} mins",
                    iconTint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ── Service Chips ─────────────────────────────────────────────────
            ServiceChipsRow(services = vet.services ?: emptyList())

            Spacer(modifier = Modifier.height(12.dp))

            // ── Book Button ───────────────────────────────────────────────────
            Button(
                onClick = onBookAppointment,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    // primary → PetOrange / PetOrangeDark
                    containerColor = MaterialTheme.colorScheme.primary,
                    // onPrimary → Color.White (both schemes)
                    contentColor   = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
            ) {
                Text(
                    text = "Book Appointment",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}


// ─── Rating Row ───────────────────────────────────────────────────────────────

@Composable
private fun RatingRow(rating: Double) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Rating",
            tint = StarYellow,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = rating.toString(),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = "(ratings)",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}

// ─── Availability Badge ───────────────────────────────────────────────────────

@Composable
private fun AvailabilityBadge(workingTime: String?) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(AvailableGreen)  // fixed semantic color
        )
        Text(
            text = "Available Today ${workingTime ?: ""}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = AvailableGreen           // fixed semantic color
        )
    }
}

// ─── Info Row ─────────────────────────────────────────────────────────────────

@Composable
private fun InfoRow(
    icon: ImageVector,
    text: String,
    iconTint: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = text,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ─── Service Chips ────────────────────────────────────────────────────────────

@Composable
private fun ServiceChipsRow(services: List<String>) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        services.take(4).forEach { service ->
            ServiceChip(label = service)
        }
    }
}

@Composable
private fun ServiceChip(label: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        // secondaryContainer → tinted chip from PetGreen / PetGreenDark palette
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f ),
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}