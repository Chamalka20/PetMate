package uk.ac.wlv.petmate.screens.appointment

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.ac.wlv.petmate.components.NetworkCircleImage
import uk.ac.wlv.petmate.core.SnackbarController
import uk.ac.wlv.petmate.core.SnackbarDuration
import uk.ac.wlv.petmate.core.UiState
import uk.ac.wlv.petmate.data.model.BookAppointmentRequest
import uk.ac.wlv.petmate.data.model.Pet
import uk.ac.wlv.petmate.data.model.Vet
import uk.ac.wlv.petmate.viewmodel.AppointmentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentSummaryScreen(
    vet                  : Vet,
    appointmentViewModel : AppointmentViewModel,
    onBack               : () -> Unit,
) {
    val selectedSlot  by appointmentViewModel.selectedSlot.collectAsState()
    val selectedType  by appointmentViewModel.selectedType.collectAsState()
    val selectedDate  by appointmentViewModel.selectedDate.collectAsState()
    val homeAddress   by appointmentViewModel.homeAddress.collectAsState()
    val homeLatitude  by appointmentViewModel.homeLatitude.collectAsState()
    val homeLongitude by appointmentViewModel.homeLongitude.collectAsState()
    val selectedPet   by appointmentViewModel.selectedPet.collectAsState()
    val bookState     by appointmentViewModel.bookState.collectAsState()

    // ── Payment methods ───────────────────────────────────────────────
    val paymentMethods = listOf(
        PaymentMethod("cash",   "Cash on visit",       Icons.Default.Payments),
        PaymentMethod("card",   "Credit / Debit card", Icons.Default.CreditCard),
        PaymentMethod("wallet", "Vodafone cash",       Icons.Default.AccountBalanceWallet)
    )
    var selectedPayment by remember { mutableStateOf("cash") }

    LaunchedEffect(bookState) {
        when (bookState) {
            is UiState.Success -> {
                //onConfirm()
            }
            is UiState.Error -> {

                SnackbarController.showError(
                    message = (bookState as UiState.Error).message
                )
            }
            else -> Unit
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar         = {
            TopAppBar(
                title = {
                    Text(
                        text       = "Appointment Summary",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
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
                    onClick = {
                        val request = BookAppointmentRequest(
                            vetId = vet.id,
                            petId = selectedPet?.id ?: 0,
                            appointmentDate = selectedDate ?: "",
                            timeSlot = selectedSlot ?: "",
                            type = selectedType,
                            serviceType = null,
                            notes = null,
                            paymentMethod = selectedPayment,
                            homeAddress = if (selectedType == 1) homeAddress else null,
                            homeLatitude = if (selectedType == 1) homeLatitude else null,
                            homeLongitude = if (selectedType == 1) homeLongitude else null,
                            homeAddressNotes = null
                        )
                        appointmentViewModel.bookAppointment(request)
                    },
                    enabled  = bookState !is UiState.Loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape  = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor         = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.primary
                            .copy(alpha = 0.4f)
                    )
                ) {
                    if (bookState is UiState.Loading) {
                        CircularProgressIndicator(
                            color    = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text       = "Confirm the appointment",
                            fontWeight = FontWeight.Bold,
                            fontSize   = 16.sp
                        )
                    }
                }
            }
        }
    ) { padding ->

        LazyColumn(
            modifier            = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding      = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ── Vet Header ────────────────────────────────────────────
            item {
                VetMiniHeader(vet = vet)
            }

            item {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                )
            }

            // ── Summary Items ─────────────────────────────────────────
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                    modifier            = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                ) {

                    // ── Service type ──────────────────────────────────
                    SummaryItem(
                        icon = when (selectedType) {
                            1    -> Icons.Default.Home
                            else -> Icons.Default.LocalHospital
                        },
                        label = "Service",
                        value = when (selectedType) {
                            0    -> "Clinic Appointment"
                            1    -> "Home Visit"
                            2    -> "Emergency"
                            else -> "Appointment"
                        }
                    )

                    HorizontalDivider(
                        color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    if (selectedPet != null) {
                        SummaryPetItem(pet = selectedPet!!)

                        HorizontalDivider(
                            color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    // ── Price ─────────────────────────────────────────
                    SummaryItem(
                        icon  = Icons.Default.Payments,
                        label = "Price",
                        value = "${vet.price ?: 0} EGP"
                    )

                    HorizontalDivider(
                        color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // ── Date & Time ───────────────────────────────────
                    SummaryItem(
                        icon  = Icons.Default.CalendarMonth,
                        label = "Date & Time",
                        value = "${selectedDate ?: "Not selected"}\n${selectedSlot ?: "Not selected"}"
                    )

                    HorizontalDivider(
                        color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // ── Location ──────────────────────────────────────
                    SummaryItem(
                        icon  = Icons.Default.LocationOn,
                        label = "Location",
                        value = when (selectedType) {
                            1    -> homeAddress.ifEmpty { "No address selected" }
                            else -> vet.location ?: "Clinic location"
                        }
                    )
                }
            }

            // ── Payment Method ────────────────────────────────────────
            item {
                Text(
                    text       = "Select Payment Method",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                    modifier   = Modifier.padding(top = 4.dp)
                )
            }

            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                    modifier            = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    paymentMethods.forEachIndexed { index, method ->
                        PaymentMethodItem(
                            method     = method,
                            isSelected = selectedPayment == method.id,
                            onClick    = { selectedPayment = method.id }
                        )
                        if (index < paymentMethods.size - 1) {
                            HorizontalDivider(
                                color    = MaterialTheme.colorScheme.onSurface
                                    .copy(alpha = 0.06f),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

// ── Summary Item ──────────────────────────────────────────────────────────────

@Composable
private fun SummaryItem(
    icon : ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier              = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // ── Icon circle ───────────────────────────────────────────────
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

        // ── Text ──────────────────────────────────────────────────────
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text     = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color    = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// ── Payment Method Item ───────────────────────────────────────────────────────

data class PaymentMethod(
    val id   : String,
    val label: String,
    val icon : ImageVector
)

@Composable
private fun PaymentMethodItem(
    method    : PaymentMethod,
    isSelected: Boolean,
    onClick   : () -> Unit
) {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier              = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        // ── Checkbox ──────────────────────────────────────────────────
        Box(
            contentAlignment = Alignment.Center,
            modifier         = Modifier
                .size(20.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primary
                    else            Color.White
                )
                .border(
                    width = 1.5.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary
                    else            Color(0xFFE0E0E0),
                    shape = RoundedCornerShape(4.dp)
                )
        ) {
            if (isSelected) {
                Icon(
                    imageVector        = Icons.Default.Check,
                    contentDescription = null,
                    tint               = Color.White,
                    modifier           = Modifier.size(14.dp)
                )
            }
        }

        // ── Label ─────────────────────────────────────────────────────
        Text(
            text       = method.label,
            fontSize   = 14.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color      = MaterialTheme.colorScheme.onSurface,
            modifier   = Modifier.weight(1f)
        )

        // ── Icon ──────────────────────────────────────────────────────
        Icon(
            imageVector        = method.icon,
            contentDescription = null,
            tint               = if (isSelected) MaterialTheme.colorScheme.primary
            else            Color.Gray,
            modifier           = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SummaryPetItem(pet: Pet) {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier              = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // ── Icon circle ───────────────────────────────────────────────
        Box(
            contentAlignment = Alignment.Center,
            modifier         = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
        ) {
            Icon(
                imageVector        = Icons.Default.Pets,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.primary,
                modifier           = Modifier.size(18.dp)
            )
        }

        // ── Pet info ──────────────────────────────────────────────────
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier              = Modifier.weight(1f)
        ) {
            // ── Pet image ─────────────────────────────────────────────
            NetworkCircleImage(
                imageUrl           = pet.imageUrl,
                contentDescription = pet.name ?: "",
                size = 36.dp
            )
            Column {
                Text(
                    text       = pet.name ?: "Unknown",
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 14.sp,
                    color      = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text     = "${pet.type ?: ""} • ${pet.breed ?: ""}",
                    fontSize = 12.sp,
                    color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}