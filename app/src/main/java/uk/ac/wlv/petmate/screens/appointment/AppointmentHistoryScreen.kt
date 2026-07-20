package uk.ac.wlv.petmate.screens.appointment

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterListOff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.ac.wlv.petmate.components.ErrorScreen
import uk.ac.wlv.petmate.core.UiState
import uk.ac.wlv.petmate.data.model.Appointment
import uk.ac.wlv.petmate.screens.appointment.components.AppointmentDetailsBottomSheet
import uk.ac.wlv.petmate.screens.mainScreens.medlog.Components.AppointmentGridCard
import uk.ac.wlv.petmate.viewmodel.AppointmentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentHistoryScreen(
    appointmentViewModel: AppointmentViewModel,
    onBack              : () -> Unit,
    onAppointmentClick  : (Appointment) -> Unit
) {
    val historyState         by appointmentViewModel.historyState.collectAsState()
    val isLoadingMoreHistory by appointmentViewModel.isLoadingMoreHistory.collectAsState()
    val listState: LazyListState = rememberLazyListState()
    val searchQuery          by appointmentViewModel.historySearchQuery.collectAsState()
    val selectedDate         by appointmentViewModel.historySelectedDate.collectAsState()

    var showDatePicker       by remember { mutableStateOf(false) }

    var selectedAppointment by remember { mutableStateOf<Appointment?>(null) }

    // ── Load history ──────────────────────────────────────────────────
    LaunchedEffect(Unit) {
        appointmentViewModel.loadAppointmentHistory(isRefresh = true)
    }

    // ── Pagination ────────────────────────────────────────────────────
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            val totalItems  = listState.layoutInfo.totalItemsCount
            totalItems > 3 &&
                    lastVisible != null &&
                    lastVisible.index >= totalItems - 3
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && !isLoadingMoreHistory) {
            appointmentViewModel.loadMoreHistory()
        }
    }

    // ── Bottom sheet ──────────────────────────────────────────────────
    if (selectedAppointment != null) {
        AppointmentDetailsBottomSheet(
            appointment  = selectedAppointment!!,
            onDismiss    = { selectedAppointment = null },
            onReschedule = { },
            onCancel     = { }
        )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton    = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = java.time.Instant
                                .ofEpochMilli(millis)
                                .atZone(java.time.ZoneId.of("UTC"))
                                .toLocalDate()
                                .toString()
                            appointmentViewModel.updateHistorySelectedDate(date)
                        }
                        showDatePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Appointment History",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (searchQuery.isNotBlank() || selectedDate != null) {
                        IconButton(
                            onClick = {
                                appointmentViewModel.clearHistoryFilters()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterListOff,
                                contentDescription = "Clear filters",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {


            // ============================
            // FILTER SECTION
            // ============================

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp),

                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                // Search TextField
                TextField(
                    value         = searchQuery,
                    onValueChange = { appointmentViewModel.updateHistorySearchQuery(it) },
                    placeholder   = {
                        Text(
                            text  = "Search by vet name...",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon  = {
                        Icon(
                            imageVector        = Icons.Default.Search,
                            contentDescription = null,
                            tint               = MaterialTheme.colorScheme.onSurface
                                .copy(alpha = 0.4f)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { appointmentViewModel.updateHistorySearchQuery("") }
                            ) {
                                Icon(
                                    imageVector        = Icons.Default.Close,
                                    contentDescription = "Clear"
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape      = RoundedCornerShape(12.dp),
                    colors     = TextFieldDefaults.colors(
                        focusedContainerColor   = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedIndicatorColor   = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                )


                // Date filter row
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (selectedDate != null)
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                else
                                    MaterialTheme.colorScheme.surfaceVariant
                            )
                            .border(
                                width = if (selectedDate != null) 1.dp else 0.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { showDatePicker = true }
                            .padding(horizontal = 12.dp, vertical = 14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = if (selectedDate != null)
                                    MaterialTheme.colorScheme.primary
                                else
                                    Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = selectedDate ?: "Filter by date",
                                fontSize = 13.sp,
                                color = if (selectedDate != null)
                                    MaterialTheme.colorScheme.primary
                                else
                                    Color.Gray
                            )
                        }

                    }
                }
            }



            // ============================
            // LIST SECTION
            // ============================

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {


                when(historyState){


                    is UiState.Loading -> {

                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.Center)
                        )

                    }


                    is UiState.Success -> {


                        val history =
                            (historyState as UiState.Success).data


                        if(history.isEmpty()){

                            Text(
                                text = "No appointment history",
                                modifier = Modifier
                                    .align(Alignment.Center)
                            )

                        }
                        else{


                            LazyVerticalGrid(

                                state = listState as? LazyGridState ?: rememberLazyGridState(),

                                columns = GridCells.Fixed(2),

                                contentPadding =
                                    PaddingValues(16.dp),

                                verticalArrangement =
                                    Arrangement.spacedBy(12.dp),

                                horizontalArrangement =
                                    Arrangement.spacedBy(12.dp),

                                modifier = Modifier
                                    .fillMaxSize()

                            ){


                                items(
                                    items = history,
                                    key = {
                                        it.id
                                    }
                                ){ appointment ->


                                    AppointmentGridCard(
                                        appointment = appointment,
                                        onClick = {
                                            selectedAppointment = appointment
                                        }
                                    )

                                }



                                if(isLoadingMoreHistory){

                                    item(
                                        span = {
                                            GridItemSpan(2)
                                        }
                                    ){

                                        CircularProgressIndicator(
                                            modifier = Modifier
                                                .padding(16.dp)
                                        )
                                    }
                                }


                                if(
                                    appointmentViewModel.historyIsLastPage
                                    && history.isNotEmpty()
                                ){

                                    item(
                                        span = {
                                            GridItemSpan(2)
                                        }
                                    ){

                                        Text(
                                            text = "No more appointments",
                                            modifier = Modifier
                                                .padding(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }



                    is UiState.Error -> {

                        ErrorScreen(
                            onRetry = {
                                appointmentViewModel
                                    .loadAppointmentHistory(true)
                            }
                        )
                    }


                    else -> Unit
                }
            }
        }
    }
}