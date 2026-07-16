package uk.ac.wlv.petmate.screens.vet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import uk.ac.wlv.petmate.components.ErrorRow
import uk.ac.wlv.petmate.components.NetworkCircleImage
import uk.ac.wlv.petmate.core.UiState
import uk.ac.wlv.petmate.core.utils.generateAppointmentDays
import uk.ac.wlv.petmate.data.model.Pet
import uk.ac.wlv.petmate.data.model.Vet
import uk.ac.wlv.petmate.screens.appointment.components.AppointmentTimeSlotPicker
import uk.ac.wlv.petmate.screens.appointment.components.AppointmentTimeSlotShimmer
import uk.ac.wlv.petmate.screens.appointment.components.PetSelectionBottomSheet
import uk.ac.wlv.petmate.screens.vet.Components.MapPreview
import uk.ac.wlv.petmate.screens.vet.Components.VetDetailsShimmer
import uk.ac.wlv.petmate.viewmodel.AppointmentViewModel
import uk.ac.wlv.petmate.viewmodel.PetProfileViewModel
import uk.ac.wlv.petmate.viewmodel.VetViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VetDetailsScreen(vetId:Int,
                     vetViewModel: VetViewModel,
                     navController: NavHostController,
                     appointmentViewModel: AppointmentViewModel,
                     petProfileViewModel: PetProfileViewModel
                     ) {

    val vetState by vetViewModel.selectedVetState.collectAsState()
    val slotsStateState by appointmentViewModel.slotsState.collectAsState()
    val DAY_FORMATTER = DateTimeFormatter.ofPattern("d MMM")
    val isGoingToScreen = remember { mutableStateOf(false) }

    val selectedPet  by appointmentViewModel.selectedPet.collectAsState()
    val petListState   by petProfileViewModel.petListState.collectAsState()
    var showPetSheet   by remember { mutableStateOf(false) }

    val pets = remember(petListState) {
        (petListState as? UiState.Success<List<Pet>>)?.data ?: emptyList()
    }


    val selectedType  by appointmentViewModel.selectedType.collectAsState()
    val selectedSlot  by appointmentViewModel.selectedSlot.collectAsState()
    val selectedDate  by appointmentViewModel.selectedDate.collectAsState()

    val homeAddress by appointmentViewModel.homeAddress.collectAsState()
    val homeLat     by appointmentViewModel.homeLatitude.collectAsState()
    val homeLon     by appointmentViewModel.homeLongitude.collectAsState()

    var selectedDayIndex by remember { mutableIntStateOf(0) }

    DisposableEffect(Unit) {
        onDispose {
            if (isGoingToScreen.value) {
                // ← going to slot screen → keep type
                appointmentViewModel.resetBookState(isChangeType = false, isGoBack = false)
            } else {
                // ← going back → reset everything
                appointmentViewModel.resetBookState(isChangeType = true, clearSelectDate = true)
            }
        }
    }

    val canBook = remember(selectedSlot, selectedType, homeAddress,selectedPet) {
        when (selectedType) {
            0    -> selectedSlot != null && selectedPet != null
            1    -> selectedSlot != null &&
                    homeAddress.isNotEmpty() &&
                    selectedPet != null
            else -> false
        }
    }

    if (showPetSheet) {
        PetSelectionBottomSheet(
            pets          = pets,
            selectedPetId = selectedPet?.id,
            onPetSelected = { pet ->
                appointmentViewModel.selectPet(pet)
            },
            onDismiss     = { showPetSheet = false }
        )
    }

    LaunchedEffect(vetId) {
        vetViewModel.loadVet(vetId)
        appointmentViewModel.loadAvailableSlots(vetId, LocalDate.now());
    }


    fun LocalDate.toTabLabel(): String {
        val today = LocalDate.now()
        val formatted = format(DAY_FORMATTER)
        return when (this) {
            today -> "Today, $formatted"
            today.plusDays(1) -> "Tomorrow, $formatted"
            else -> "${
                dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
            }, $formatted"
        }
    }




    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Vet Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },

                )
        }) { padding ->
        when (val state = vetState) {
            is UiState.Loading -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                VetDetailsShimmer()
            }

            is UiState.Success -> {
                val vet = state.data

                LazyColumn(
                    modifier        = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding  = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // ── Vet Header ────────────────────────────────────────────────
                    item { VetHeader(vet) }

                    // ── Services ──────────────────────────────────────────────────
                    item { ServicesSection(vet.services) }

                    // ── Info Items ────────────────────────────────────────────────
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            VetInfoItem(
                                icon = Icons.Default.AttachMoney,
                                text = "Price: ${vet.price} EGP"
                            )
                            VetInfoItem(
                                icon = Icons.Default.AccessTime,
                                text = "${vet.workingDays}  ${vet.workingTime}"
                            )
                            VetInfoItem(
                                icon = Icons.Default.LocationOn,
                                text = vet.location
                            )
                            VetInfoItem(
                                icon = Icons.Default.Star,
                                text = "You'll get ${vet.rewardPoints} Paw Points with this booking"
                            )
                            VetInfoItem(
                                icon = Icons.Default.Timer,
                                text = "Waiting time: ${vet.waitingTimeMinutes} mins"
                            )
                        }
                    }

                    // ── Clinic Details ────────────────────────────────────────────
                    item {
                        Text(
                            text       = "Clinic Details",
                            fontWeight = FontWeight.Bold,
                            fontSize   = 18.sp
                        )
                    }

                    // ── Map Preview ───────────────────────────────────────────────
                    item {
                        MapPreview(
                            latitude  = vet.latitude,
                            longitude = vet.longitude
                        )
                    }

                    // ── Appointment Slots ─────────────────────────────────────────
                    item {
                        Text(
                            text       = "Appointment",
                            fontWeight = FontWeight.Bold,
                            fontSize   = 18.sp
                        )
                    }

                    item {
                        when (val slotsState = slotsStateState) {
                            is UiState.Loading -> {
                                AppointmentTimeSlotShimmer(
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            is UiState.Success -> {
                                AppointmentTimeSlotPicker(
                                    appointmentViewModel= appointmentViewModel,
                                    days = generateAppointmentDays(count = 2, availableSlots =slotsState.data.slots ),
                                    selectedType   = selectedType,
                                    selectedSlot   = selectedSlot,
                                    selectedDayIndex     = selectedDayIndex,
                                    onDaySelected        = { selectedDayIndex = it },
                                    vetId = vetId,
                                    onTypeSelected = { appointmentViewModel.selectType(it)
                                        appointmentViewModel.resetBookState(isChangeType = false, clearSelectDate = true)},
                                    onSlotSelected = { appointmentViewModel.selectSlot(it)

                                    }
                                )
                            }
                            is UiState.Error -> {
                                ErrorRow(
                                    onRetry = { appointmentViewModel.loadAvailableSlots(vetId, LocalDate.now()) }
                                )
                            }
                            else -> Unit
                        }
                    }

                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text       = "Select Pet",
                                fontWeight = FontWeight.Bold,
                                fontSize   = 16.sp
                            )

                            // ── Tap to open bottom sheet ───────────────────────────────
                            Row(
                                verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier              = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .border(
                                        width = 1.dp,
                                        color = if (selectedPet != null)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            Color(0xFFE0E0E0),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { showPetSheet = true }
                                    .padding(12.dp)
                            ) {
                                if (selectedPet != null) {
                                    // ── Show selected pet ─────────────────────────────
                                    NetworkCircleImage(
                                        imageUrl           = selectedPet?.imageUrl,
                                        contentDescription = selectedPet?.name ?: "",
                                        size = 40.dp
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text       = selectedPet?.name ?: "Unknown",
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize   = 14.sp,
                                            color      = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text     = "${selectedPet?.type ?: ""} • ${selectedPet?.breed ?: ""}",
                                            fontSize = 12.sp,
                                            color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                        )
                                    }
                                    Icon(
                                        imageVector        = Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint               = MaterialTheme.colorScheme.primary
                                    )
                                } else {
                                    // ── No pet selected ───────────────────────────────
                                    Icon(
                                        imageVector        = Icons.Default.Pets,
                                        contentDescription = null,
                                        tint               = Color.Gray,
                                        modifier           = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text     = "Select a pet",
                                        fontSize = 14.sp,
                                        color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                        modifier = Modifier.weight(1f)
                                    )
                                    Icon(
                                        imageVector        = Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint               = Color.Gray
                                    )
                                }
                            }
                        }
                    }

                    // ──  Buttons ───────────────────────────────────────────────
                    item {
                        Row(  modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                            OutlinedButton(
                                onClick  = {isGoingToScreen.value = true
                                    navController.navigate("selectTimeSlot/${vet.id}/$selectedType") },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp),
                                shape  = RoundedCornerShape(12.dp),
                                border = BorderStroke(
                                    width = 1.5.dp,
                                    color = MaterialTheme.colorScheme.primary
                                ),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor         = MaterialTheme.colorScheme.primary,
                                    disabledContentColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                                )
                            ) {
                                Text(
                                    text       = "View all slots",
                                    fontWeight = FontWeight.W400,
                                    fontSize   = 14.sp
                                )
                            }

                            Button(
                                onClick  = {
                                    isGoingToScreen.value = true
                                    navController.navigate("appointmentSummary/${vet.id}")

                                },
                                enabled  = canBook,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp),
                                shape  = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor        = MaterialTheme.colorScheme.primary,
                                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                                )
                            ) {
                                Text(
                                    text       = "Book",
                                    fontWeight = FontWeight.W400,
                                    fontSize   = 14.sp,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }

                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }

            is UiState.Error -> {
                Text(
                    text = "",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            else -> Unit
        }
    }


}

@Composable
fun VetHeader(vet: Vet) {

    Row(verticalAlignment = Alignment.CenterVertically) {


        NetworkCircleImage(
            imageUrl = vet.imageUrl,
            contentDescription = ""
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {

            vet.name?.let {
                Text(
                    text = it,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            vet.specialization?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            Text(
                text = "${vet.experienceYears} years Exp.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )

            Row(verticalAlignment = Alignment.CenterVertically) {

                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(16.dp)
                )

                Text(" ${vet.rating}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black)
            }
        }
    }
}

@Composable
fun ServicesSection(services: List<String>?) {

    Column {

        Text(
            text = "Services",
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row {

            services?.forEach {

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f ),
                    modifier = Modifier.padding(end = 8.dp)
                ) {

                    Text(
                        text = it,
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 6.dp
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun VetInfoItem(
    icon: ImageVector,
    text: String?
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {

        Icon(
            icon,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(17.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        if (text != null) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )

        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun VetScreenPreview() {
//
//    val vet = Vet(
//        id = 1,
//        name = "Dr. Mostafa Khalid",
//        specialization = "General Veterinarian (VMD)",
//        experienceYears = 20,
//        rating = 5.0,
//        services = listOf("Grooming", "Therapy", "X-rays", "In-Home Visit"),
//        price = 120,
//        workingDays = "Sat - Thurs",
//        workingTime = "6:00 PM - 12:00 PM",
//        location = "Al Gondy Al Maqbool Sq., Al Mansheyah",
//        rewardPoints = 200,
//        waitingTimeMinutes = 10,
//        imageUrl = ""
//    )
//
//    VetDetailsScreen(vet)
//}