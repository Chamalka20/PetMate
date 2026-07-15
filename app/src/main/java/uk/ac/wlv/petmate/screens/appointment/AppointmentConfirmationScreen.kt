package uk.ac.wlv.petmate.screens.vet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.ac.wlv.petmate.core.utils.toReadableDate
import uk.ac.wlv.petmate.data.model.Appointment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentConfirmationScreen(
    appointment    : Appointment,
    onViewMyAppointments: () -> Unit
) {
    var setReminder by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar         = {
            TopAppBar(
                title = {
                    Text(
                        text       = "Appointment Confirmation",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 16.sp,
                        modifier   = Modifier.fillMaxWidth(),
                        textAlign  = TextAlign.Center
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .navigationBarsPadding()
                    .padding(16.dp)
            ) {
                Button(
                    onClick  = onViewMyAppointments,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape  = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text       = "View My Appointments",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 16.sp
                    )
                }
            }
        }
    ) { padding ->

        LazyColumn(
            modifier            = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding      = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Success icon ──────────────────────────────────────────
            item {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier         = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector        = Icons.Default.Check,
                        contentDescription = null,
                        tint               = Color.White,
                        modifier           = Modifier.size(50.dp)
                    )
                }
            }

            // ── Title ─────────────────────────────────────────────────
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text       = "Appointment Booked.",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 20.sp,
                        textAlign  = TextAlign.Center
                    )
                    Text(
                        text       = "Your pet is in good hands!",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 20.sp,
                        textAlign  = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text       = appointment.vetName,
                        fontSize   = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        textAlign  = TextAlign.Center
                    )
                }
            }

            // ── Appointment details card ──────────────────────────────
            item {
                Card(
                    shape    = RoundedCornerShape(16.dp),
                    colors   = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(0.dp),
                        modifier            = Modifier.padding(16.dp)
                    ) {

                        // ── Date & Time ───────────────────────────────
                        ConfirmationInfoRow(
                            icon  = Icons.Default.Schedule,
                            text  = "${appointment.appointmentDate.toReadableDate()} - ${appointment.timeSlot}"
                        )

                        HorizontalDivider(
                            color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f),
                            modifier = Modifier.padding(vertical = 10.dp)
                        )

                        // ── Price ─────────────────────────────────────
                        ConfirmationInfoRow(
                            icon  = Icons.Default.CreditCard,
                            text  = "Price: ${appointment.totalFee.toInt()} EGP (${
                                when (appointment.paymentMethod) {
                                    "cash"   -> "Cash on visit"
                                    "card"   -> "Credit / Debit card"
                                    "wallet" -> "Vodafone cash"
                                    else     -> "Cash on visit"
                                }
                            })"
                        )

                        HorizontalDivider(
                            color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f),
                            modifier = Modifier.padding(vertical = 10.dp)
                        )

                        // ── Location ──────────────────────────────────
                        ConfirmationInfoRow(
                            icon = Icons.Default.LocationOn,
                            text = when (appointment.type) {
                                1    -> appointment.homeAddress ?: "Home address"
                                else -> appointment.clinicAddress ?: "Clinic location"
                            }
                        )
                    }
                }
            }


            // ── Set Reminder ──────────────────────────────────────────
            item {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier              = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(14.dp)
                ) {
                    Checkbox(
                        checked         = setReminder,
                        onCheckedChange = { setReminder = it },
                        colors          = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text       = "Set Reminder",
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // ── Cancellation Policy ───────────────────────────────────
            item {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text       = "Cancellation Policy",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 15.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    PolicyItem(
                        text = "100% refund if cancellation or rescheduling is more than 24 hours."
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    PolicyItem(
                        text = "50% refund if cancellation or rescheduling is 6-24 hours before the session."
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

// ── Confirmation Info Row ─────────────────────────────────────────────────────

@Composable
private fun ConfirmationInfoRow(
    icon: ImageVector,
    text: String
) {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier         = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.primary,
                modifier           = Modifier.size(18.dp)
            )
        }
        Text(
            text     = text,
            fontSize = 13.sp,
            color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.weight(1f)
        )
    }
}

// ── Policy Item ───────────────────────────────────────────────────────────────

@Composable
private fun PolicyItem(text: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier              = Modifier.fillMaxWidth()
    ) {
        Text(
            text     = "•",
            fontSize = 13.sp,
            color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Text(
            text     = text,
            fontSize = 13.sp,
            color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}