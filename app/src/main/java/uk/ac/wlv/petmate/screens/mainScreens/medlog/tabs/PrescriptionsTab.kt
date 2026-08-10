package uk.ac.wlv.petmate.screens.mainScreens.medlog.tabs

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import uk.ac.wlv.petmate.components.ErrorRow
import uk.ac.wlv.petmate.core.UiState
import uk.ac.wlv.petmate.core.utils.toReadableDate
import uk.ac.wlv.petmate.data.model.Prescription
import uk.ac.wlv.petmate.viewmodel.PrescriptionViewModel
import androidx.core.graphics.createBitmap
import uk.ac.wlv.petmate.screens.mainScreens.medlog.Components.FullPdfScreen
import uk.ac.wlv.petmate.screens.mainScreens.medlog.Components.PrescriptionCard
import java.io.File

@Composable
fun PrescriptionsTab(
    prescriptionViewModel: PrescriptionViewModel,
) {
    val prescriptionsState by prescriptionViewModel.prescriptionsState.collectAsState()
    val isLoadingMore      by prescriptionViewModel.isLoadingMore.collectAsState()
    val searchQuery        by prescriptionViewModel.searchQuery.collectAsState()
    val listState          = rememberLazyListState()

    var selectedPrescription by remember {
        mutableStateOf<Prescription?>(null)
    }
    selectedPrescription?.let { prescription ->

        prescription.pdfUrl
            ?.takeIf { it.isNotBlank() }
            ?.let { url ->

                FullPdfScreen(
                    pdfUrl = url,
                    onBack = {
                        selectedPrescription = null
                    }
                )
            }
    }

    // ── Load on first open ────────────────────────────────────────────
    LaunchedEffect(Unit) {
        prescriptionViewModel.loadMyPrescriptions()
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
        if (shouldLoadMore && !isLoadingMore) {
            prescriptionViewModel.loadMorePrescriptions()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        // ── Search Bar ────────────────────────────────────────────────
        TextField(
            value         = searchQuery,
            onValueChange = { prescriptionViewModel.updateSearchQuery(it) },
            placeholder   = {
                Text(
                    text     = "Search by vet, pet or diagnosis...",
                    fontSize = 13.sp,
                    color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
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
                        onClick = { prescriptionViewModel.clearSearch() }
                    ) {
                        Icon(
                            imageVector        = Icons.Default.Close,
                            contentDescription = "Clear"
                        )
                    }
                }
            },
            singleLine = true,
            shape      = RoundedCornerShape(0.dp),
            colors     = TextFieldDefaults.colors(
                focusedContainerColor   = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor   = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth()
        )

        HorizontalDivider(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
        )

        // ── Content ───────────────────────────────────────────────────
        when (val state = prescriptionsState) {
            is UiState.Loading -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier         = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            is UiState.Success -> {
                val prescriptions = state.data

                if (prescriptions.isEmpty()) {
                    EmptyPrescriptions()
                } else {
                    LazyColumn(
                        state               = listState,
                        contentPadding      = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier            = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = prescriptions,
                            key   = { it.id }
                        ) { prescription ->
                            PrescriptionCard(
                                prescription = prescription,

                                onClick = {
                                    selectedPrescription = prescription

                                }
                            )
                        }

                        // ── Load more spinner ─────────────────────────
                        if (isLoadingMore) {
                            item {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier         = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    CircularProgressIndicator(
                                        color    = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }

                        // ── End of list ───────────────────────────────
                        if (prescriptionViewModel.isLastPage &&
                            prescriptions.isNotEmpty()) {
                            item {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier         = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text     = "No more prescriptions",
                                        fontSize = 12.sp,
                                        color    = MaterialTheme.colorScheme.onSurface
                                            .copy(alpha = 0.4f)
                                    )
                                }
                            }
                        }

                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }

            is UiState.Error -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier         = Modifier.fillMaxSize()
                ) {
                    ErrorRow(
                        onRetry = {
                            prescriptionViewModel.loadMyPrescriptions()
                        }
                    )
                }
            }

            else -> Unit
        }
    }
}


@Composable
private fun EmptyPrescriptions() {
    Box(
        contentAlignment = Alignment.Center,
        modifier         = Modifier.fillMaxSize()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector        = Icons.Default.Medication,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                modifier           = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text     = "No prescriptions yet",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text     = "Your prescriptions will appear here\nafter your appointments",
                fontSize = 13.sp,
                color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}