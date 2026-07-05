package uk.ac.wlv.petmate.screens.vet
import android.util.Log
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import uk.ac.wlv.petmate.components.ErrorScreen
import uk.ac.wlv.petmate.components.PullToRefreshLayout
import uk.ac.wlv.petmate.core.UiState
import uk.ac.wlv.petmate.data.model.Vet
import uk.ac.wlv.petmate.viewmodel.VetViewModel
import uk.ac.wlv.petmate.data.model.SortOption
import uk.ac.wlv.petmate.screens.appointment.components.VisitTypeBottomSheet
import uk.ac.wlv.petmate.screens.vet.Components.VetCard
import uk.ac.wlv.petmate.screens.vet.Components.VetFilterBottomSheet
import uk.ac.wlv.petmate.screens.vet.Components.VetSearchBar
import uk.ac.wlv.petmate.viewmodel.AppointmentViewModel


// ─── Main Screen ──────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VetsListScreen(
    onVetClick: (Vet) -> Unit = {},
    onBookAppointment: (Vet) -> Unit = {},
    navController: NavHostController,
    vetViewModel: VetViewModel,
    appointmentViewModel: AppointmentViewModel

) {
    val vetListState by vetViewModel.filteredVetListState.collectAsState()
    val isLoadingMore by vetViewModel.isLoadingMore.collectAsState()
    val searchQuery by vetViewModel.searchQuery.collectAsState()

    var showFilterSheet by remember { mutableStateOf(false) }
    val filterState    by vetViewModel.filterState.collectAsState()

    val listState    = rememberLazyListState()
    val scope        = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }

    var vetToBook by remember { mutableStateOf<Vet?>(null) }

// Show active filter count badge on Filter chip
    val activeFilterCount = remember(filterState) {
        var count = 0
        if (filterState.selectedServices.isNotEmpty()) count++
        if (filterState.minRating > 0f) count++
        if (filterState.maxPrice < 5000) count++
        if (filterState.maxWaitingTime < 60) count++
        if (filterState.sortBy != SortOption.NONE) count++
        count
    }

    if (vetToBook != null) {
        VisitTypeBottomSheet(
            vet            = vetToBook!!,
            onTypeSelected = { type ->
                appointmentViewModel.selectType(type)
                navController.navigate("selectTimeSlot/${vetToBook!!.id}/$type")
                vetToBook = null
            },
            onDismiss = { vetToBook = null }
        )
    }


    if (showFilterSheet) {
        VetFilterBottomSheet(
            currentFilter = filterState,
            onApply       = { vetViewModel.applyFilter(it) },
            onDismiss     = { showFilterSheet = false }
        )
    }

    // ── Pagination trigger ────────────────────────────────────────────
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
        if (shouldLoadMore && !isLoadingMore) {
            vetViewModel.loadMoreVets()
        } else {
            Log.d(
                "Pagination",
                ">>> Skipped — shouldLoadMore=$shouldLoadMore isLoadingMore=$isLoadingMore"
            )
        }

    }
    LaunchedEffect(Unit) {
        vetViewModel.loadVetList(isfilter = true, isRefresh = true)
    }
    Scaffold(
        topBar = { VetsTopBar(onBackClick = { navController.popBackStack()}) },
        // background → PetBackground (light) / PetBackgroundDark (dark)
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->

        PullToRefreshLayout(
            isRefreshing = isRefreshing,
            onRefresh = {
                scope.launch {
                    isRefreshing = true
                    vetViewModel.loadVetList(isfilter = true, isRefresh = true)
                    isRefreshing = false
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues( vertical = 4.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    VetSearchBar(
                        query = searchQuery,
                        onQueryChange = { vetViewModel.onSearchQueryChange(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                item {
                    FilterSortRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        activeFilterCount = activeFilterCount,
                        onFilterClick = { showFilterSheet = true },
                        navController
                    )
                }

                when (val state = vetListState) {

                    // Loading first page
                    is UiState.Loading -> {
                        item {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    // Success — show list
                    is UiState.Success -> {
                        val vets = state.data

                        if (vets.isEmpty()) {
                            // Empty state
                            item {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = Icons.Default.SearchOff,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                                            modifier = Modifier.size(64.dp)
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "No vets found",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onBackground.copy(
                                                alpha = 0.4f
                                            )
                                        )
                                    }
                                }
                            }
                        } else {
                            item {
                                Text(
                                    text = "Showing ${vets.size} Vets",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                                )
                            }





                            items(
                                items = vets,
                                key = { vet -> vet.id }
                            ) { vet ->
                                VetCard(
                                    vet = vet,
                                    onClick = { onVetClick(vet) },
                                  onBookAppointment = { vetToBook = vet }

                                )
                            }

                            // Load more spinner at bottom
                            if (isLoadingMore) {
                                item {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        CircularProgressIndicator(
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                            }

                            item { Spacer(modifier = Modifier.height(16.dp)) }

                        }
                    }

                    // Error state
                    is UiState.Error -> {
                        item {
                            ErrorScreen(
                                onRetry = { vetViewModel.loadVetList(isfilter = true) }
                            )
                        }
                    }

                    UiState.Idle -> {
                        item {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── Top Bar ──────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VetsTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "Vets List",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                // onSurface → Color.Black (light) / Color.White (dark)
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            // surface → PetSurface (light) / PetSurfaceDark (dark)
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}


// ─── Filter / Sort / Map Row ──────────────────────────────────────────────────

@Composable
private fun FilterSortRow(
    modifier: Modifier = Modifier,
    activeFilterCount: Int = 0,
    onFilterClick: () -> Unit = {},
    rootNavController: NavHostController,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        BadgedBox(
            badge = {
                if (activeFilterCount > 0) {
                    Badge(containerColor = MaterialTheme.colorScheme.primary) {
                        Text(
                            text     = activeFilterCount.toString(),
                            color    = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        ) {
            FilterChipItem(
                icon    = Icons.Default.FilterList,
                label   = "Filter",
                onClick = onFilterClick
            )
        }

        FilterChipItem(icon = Icons.Default.Map, label = "Map", onClick = {rootNavController.navigate(
            "nearbyVetsMapScreen"
        ) })
    }
}

@Composable
private fun FilterChipItem(icon: ImageVector, label: String,onClick: () -> Unit = {}) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = ButtonDefaults.outlinedButtonBorder(enabled = true),
        modifier = Modifier
            .height(36.dp)
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontSize = 13.sp
            )
        }
    }
}


//// ─── Previews ─────────────────────────────────────────────────────────────────
//
//@androidx.compose.ui.tooling.preview.Preview(showBackground = true, name = "Light")
//@Composable
//private fun VetsListLightPreview() {
//    PetMateTheme(darkTheme = false) { VetsListScreen() }
//}
//
//@androidx.compose.ui.tooling.preview.Preview(
//    showBackground = true,
//    name = "Dark",
//    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
//)
//@Composable
//private fun VetsListDarkPreview() {
//    PetMateTheme(darkTheme = true) { VetsListScreen() }
//}