package uk.ac.wlv.petmate.screens.appointment

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocalHospital
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import uk.ac.wlv.petmate.components.ErrorRow
import uk.ac.wlv.petmate.components.NetworkCircleImage
import uk.ac.wlv.petmate.core.UiState
import uk.ac.wlv.petmate.core.utils.generateAppointmentDays
import uk.ac.wlv.petmate.data.model.AppointmentDay
import uk.ac.wlv.petmate.data.model.Pet
import uk.ac.wlv.petmate.data.model.Vet
import uk.ac.wlv.petmate.screens.appointment.components.AppointmentTimeSlotShimmer
import uk.ac.wlv.petmate.screens.appointment.components.PetSelectionBottomSheet
import uk.ac.wlv.petmate.screens.vet.Components.LocationSearchBottomSheet
import uk.ac.wlv.petmate.screens.appointment.components.SelectTimeSlotScreenShimmer
import uk.ac.wlv.petmate.screens.appointment.components.TimeSlotGrid
import uk.ac.wlv.petmate.ui.theme.StarYellow
import uk.ac.wlv.petmate.viewmodel.AppointmentViewModel
import uk.ac.wlv.petmate.viewmodel.PetProfileViewModel
import uk.ac.wlv.petmate.viewmodel.VetViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectTimeSlotScreen(
    vetId                 : Int,
    appointmentViewModel: AppointmentViewModel,
    vetViewModel: VetViewModel,
    petProfileViewModel: PetProfileViewModel,
    onBack              : () -> Unit,
    onBookAppointment   : () -> Unit
) {
    val selectedSlot  by appointmentViewModel.selectedSlot.collectAsState()
    val selectedType  by appointmentViewModel.selectedType.collectAsState()
    val slotsState    by appointmentViewModel.slotsState.collectAsState()
    val vetState by vetViewModel.selectedVetState.collectAsState()
    val petListState   by petProfileViewModel.petListState.collectAsState()
    val selectedPet  by appointmentViewModel.selectedPet.collectAsState()

    // ── Generate 7 days ───────────────────────────────────────────────
    var selectedDayIndex by remember { mutableIntStateOf(0) }
    val days = remember(slotsState) {
        val backendSlots = (slotsState as? UiState.Success)?.data?.slots
            ?: emptyList()
        generateAppointmentDays(count = 7, availableSlots = backendSlots)
    }

    val pets = remember(petListState) {
        (petListState as? UiState.Success<List<Pet>>)?.data ?: emptyList()
    }


    var showPetSheet   by remember { mutableStateOf(false) }

    val canBook = when (selectedType) {
        0    -> selectedSlot != null &&  selectedPet != null
        1    -> selectedSlot != null &&
                appointmentViewModel.homeAddress.collectAsState().value.isNotEmpty() &&  selectedPet != null
        else -> false
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
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar         = {
            TopAppBar(
                title = {
                    Text(
                        text       = "Select Time Slot",
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
            // ── Book Button ───────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .navigationBarsPadding()
                    .padding(16.dp)
            ) {
                Button(
                    onClick  = onBookAppointment,
                    enabled  = canBook,
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
                    Text(
                        text       = "Book an appointment",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 16.sp
                    )
                }
            }
        }
    ) { padding ->
        when (val state = vetState) {
            is UiState.Loading -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                SelectTimeSlotScreenShimmer()
            }

            is UiState.Success -> {
                val vet = state.data
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // ── Vet Header ────────────────────────────────────────────
                    item {
                        VetMiniHeader(vet =vet)
                    }

                    // ── Visit Type Badge + Home Address ──────────────────────────────────
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                            // ── Visit type badge ──────────────────────────────────────────
                            Row(
                                verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier              = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                                    .padding(12.dp)
                            ) {
                                Icon(
                                    imageVector = when (selectedType) {
                                        1    -> Icons.Default.Home
                                        else -> Icons.Default.LocalHospital
                                    },
                                    contentDescription = null,
                                    tint     = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text(
                                        text       = when (selectedType) {
                                            0    -> "Clinic Visit"
                                            1    -> "Home Visit"
                                            else -> "Appointment"
                                        },
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize   = 14.sp,
                                        color      = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text     = when (selectedType) {
                                            0    -> "You will visit the clinic"
                                            1    -> "The vet will come to your home"
                                            else -> ""
                                        },
                                        fontSize = 12.sp,
                                        color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            }

                            // ── Home Address Input (only for home visit) ──────────────────
                            if (selectedType == 1) {
                                HomeAddressInput(
                                    appointmentViewModel = appointmentViewModel,
                                    onAddressSelected    = { address, lat, lon ->
                                        appointmentViewModel.updateHomeAddress(
                                            address   = address,
                                            latitude  = lat,
                                            longitude = lon
                                        )
                                    }
                                )
                            }
                        }
                    }


                    // ── Select Date ───────────────────────────────────────────
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Select Date",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    // ── Date Selector ─────────────────────────────────────────
                    item {
                        DateSelector(
                            days = days,
                            selectedDayIndex = selectedDayIndex,
                            onDaySelected = { index ->
                                selectedDayIndex = index
                                appointmentViewModel.selectDate(days[index].date.toString())
                                appointmentViewModel.selectSlot(null.toString())
                                // load slots for selected day
                                appointmentViewModel.loadAvailableSlots(
                                    vetId = vet.id,
                                    date = days[index].date
                                )
                            }
                        )
                    }

                    // ── Select Time ───────────────────────────────────────────
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Select Time",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    // ── Time Slot Grid ────────────────────────────────────────
                    item {
                        when (slotsState) {
                            is UiState.Loading -> {
                                AppointmentTimeSlotShimmer(
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            is UiState.Success -> {

                                TimeSlotGrid(
                                    slots = days[selectedDayIndex].timeSlots,
                                    selectedSlot = selectedSlot,
                                    onSlotSelected = {
                                        appointmentViewModel.selectSlot(it)
                                    }
                                )
                            }

                            is UiState.Error -> {
                                ErrorRow(
                                    onRetry = {
                                        appointmentViewModel.loadAvailableSlots(
                                            vetId = vet.id,
                                            date = days[selectedDayIndex].date
                                        )
                                    }
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

                    item { Spacer(modifier = Modifier.height(8.dp)) }
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

// ── Vet Mini Header ───────────────────────────────────────────────────────────

@Composable
 fun VetMiniHeader(vet: Vet) {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier              = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp)
    ) {
        // ── Avatar ────────────────────────────────────────────────────
        NetworkCircleImage(
            imageUrl           = vet.imageUrl,
            contentDescription = vet.name ?: "",

        )

        // ── Info ──────────────────────────────────────────────────────
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = vet.name ?: "Unknown",
                fontWeight = FontWeight.Bold,
                fontSize   = 15.sp
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement   = Arrangement.spacedBy(8.dp),
                modifier              = Modifier.fillMaxWidth()
            ) {
                vet.services?.forEach { service ->
                    Surface(
                        shape    = RoundedCornerShape(20.dp),
                        color    = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text     = service,
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical   = 6.dp
                            ),
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.primary,

                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector        = Icons.Default.Star,
                    contentDescription = null,
                    tint               = StarYellow,
                    modifier           = Modifier.size(14.dp)
                )
                Text(
                    text     = "${vet.rating}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text     = "(${vet.rewardPoints} Ratings)",
                    fontSize = 11.sp,
                    color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

// ── Date Selector ─────────────────────────────────────────────────────────────

@Composable
private fun DateSelector(
    days            : List<AppointmentDay>,
    selectedDayIndex: Int,
    onDaySelected   : (Int) -> Unit
) {
    // Show 4 per row
    val rows = days.chunked(4)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        rows.forEach { rowDays ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier              = Modifier.fillMaxWidth()
            ) {
                rowDays.forEachIndexed { indexInRow, day ->
                    val globalIndex = days.indexOf(day)
                    val isSelected  = globalIndex == selectedDayIndex
                    val isDisabled  = day.date.isBefore(LocalDate.now())

                    DateChip(
                        day        = day,
                        isSelected = isSelected,
                        isDisabled = isDisabled,
                        onClick    = {
                            if (!isDisabled) onDaySelected(globalIndex)
                        },
                        modifier   = Modifier.weight(1f)
                    )
                }
                // Fill remaining spaces
                repeat(4 - rowDays.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
// ── Date Chip ─────────────────────────────────────────────────────────────────

@Composable
private fun DateChip(
    day       : AppointmentDay,
    isSelected: Boolean,
    isDisabled: Boolean,
    onClick   : () -> Unit,
    modifier  : Modifier = Modifier
) {
    val bgColor   = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isDisabled -> Color(0xFFF5F5F5)
        else       -> Color.White
    }
    val textColor = when {
        isSelected -> Color.White
        isDisabled -> Color(0xFFBBBBBB)
        else       -> MaterialTheme.colorScheme.onSurface
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier            = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .then(
                if (!isSelected && !isDisabled)
                    Modifier.border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                else Modifier
            )
            .clickable(enabled = !isDisabled) { onClick() }
            .padding(vertical = 10.dp, horizontal = 4.dp)
    ) {
        Text(
            text      = day.date.format(DateTimeFormatter.ofPattern("EEE")), // "Mon"
            fontSize  = 11.sp,
            color     = textColor.copy(alpha = if (isSelected) 1f else 0.6f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text       = day.date.dayOfMonth.toString(), // "23"
            fontSize   = 15.sp,
            fontWeight = FontWeight.Bold,
            color      = textColor
        )
        Text(
            text     = day.date.format(DateTimeFormatter.ofPattern("MMM")), // "Sep"
            fontSize = 11.sp,
            color    = textColor.copy(alpha = if (isSelected) 1f else 0.6f)
        )
    }
}

@Composable
fun HomeAddressInput(
    appointmentViewModel: AppointmentViewModel,
    onAddressSelected   : (address: String, lat: Double?, lon: Double?) -> Unit
) {
    val homeAddress    by appointmentViewModel.homeAddress.collectAsState()
    var showSearchSheet by remember { mutableStateOf(false) }

    // ── Address field ─────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(
                width = 1.dp,
                color = if (homeAddress.isEmpty())
                    Color(0xFFE0E0E0)
                else
                    MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { showSearchSheet = true }
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier              = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector        = Icons.Default.LocationOn,
                contentDescription = null,
                tint               = if (homeAddress.isEmpty())
                    Color.Gray
                else
                    MaterialTheme.colorScheme.primary,
                modifier           = Modifier.size(20.dp)
            )
            Text(
                text     = homeAddress.ifEmpty { "Enter your home address" },
                color    = if (homeAddress.isEmpty())
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                else
                    MaterialTheme.colorScheme.onSurface,
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            // ── Clear button ──────────────────────────────────────────
            if (homeAddress.isNotEmpty()) {
                IconButton(
                    onClick  = {
                        appointmentViewModel.updateHomeAddress("", null, null)
                    },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector        = Icons.Default.Close,
                        contentDescription = "Clear",
                        tint               = Color.Gray,
                        modifier           = Modifier.size(16.dp)
                    )
                }
            }
        }
    }

    // ── Location search bottom sheet ──────────────────────────────────
    if (showSearchSheet) {
        LocationSearchBottomSheet(
            vetViewModel       =  hiltViewModel(),
            onLocationSelected = { lat, lon, name ->
                onAddressSelected(name, lat, lon)
                showSearchSheet = false
            },
            onDismiss = { showSearchSheet = false }
        )
    }
}

