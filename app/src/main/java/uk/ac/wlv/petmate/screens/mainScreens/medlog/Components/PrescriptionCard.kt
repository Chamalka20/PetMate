package uk.ac.wlv.petmate.screens.mainScreens.medlog.Components

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.createBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import uk.ac.wlv.petmate.core.utils.toReadableDate
import uk.ac.wlv.petmate.data.model.Prescription
import java.io.File

@Composable
fun PrescriptionCard(
    prescription: Prescription,
    onClick: () -> Unit
) {
    Card(
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier  = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ── Visit type badge ──────────────────────────────────────
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier              = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                    )
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = when (prescription.appointmentType) {
                        1    -> Icons.Default.Home
                        else -> Icons.Default.LocalHospital
                    },
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.primary,
                    modifier           = Modifier.size(14.dp)
                )
                Text(
                    text     = when (prescription.appointmentType) {
                        0    -> "Clinic Appointment"
                        1    -> "In-Home Visit"
                        else -> "Appointment"
                    },
                    fontSize = 12.sp,
                    color    = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── PDF Preview ───────────────────────────────────────────
            prescription.pdfUrl?.let { url ->
                PdfPreviewFromUrl(
                    pdfUrl   = url,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
            )
            Spacer(modifier = Modifier.height(12.dp))

            // ── Vet name ──────────────────────────────────────────────
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector        = Icons.Default.Person,
                    contentDescription = null,
                    tint               = Color.Gray,
                    modifier           = Modifier.size(16.dp)
                )
                Text(
                    text     = prescription.vetName,
                    fontSize = 13.sp,
                    color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // ── Address ───────────────────────────────────────────────
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector        = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint               = Color.Gray,
                    modifier           = Modifier.size(16.dp)
                )
                Text(
                    text     = prescription.clinicAddress ?: "Clinic location",
                    fontSize = 13.sp,
                    color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // ── Pet name ──────────────────────────────────────────────
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector        = Icons.Default.Pets,
                    contentDescription = null,
                    tint               = Color.Gray,
                    modifier           = Modifier.size(16.dp)
                )
                Text(
                    text     = prescription.petName,
                    fontSize = 13.sp,
                    color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // ── Date ──────────────────────────────────────────────────
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector        = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint               = Color.Gray,
                    modifier           = Modifier.size(16.dp)
                )
                Text(
                    text     = prescription.issuedAt.toReadableDate(),
                    fontSize = 13.sp,
                    color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

// ── PDF Preview ───────────────────────────────────────────────────────────────

@Composable
fun PdfPreviewFromUrl(
    pdfUrl: String,
    modifier: Modifier = Modifier
) {
    var bitmap by remember {
        mutableStateOf<Bitmap?>(null)
    }

    LaunchedEffect(pdfUrl) {
        withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient()

                val request = Request.Builder()
                    .url(pdfUrl)
                    .build()

                val response = client.newCall(request).execute()

                if (!response.isSuccessful) {
                    Log.e(
                        "PdfPreview",
                        "HTTP error: ${response.code}"
                    )
                    return@withContext
                }

                val bytes = response.body?.bytes()
                    ?: return@withContext

                val file = File.createTempFile(
                    "preview",
                    ".pdf"
                )

                file.writeBytes(bytes)

                val descriptor =
                    ParcelFileDescriptor.open(
                        file,
                        ParcelFileDescriptor.MODE_READ_ONLY
                    )

                val renderer = PdfRenderer(descriptor)

                if (renderer.pageCount > 0) {

                    val page = renderer.openPage(0)

                    val pageBitmap = createBitmap(page.width, page.height)

                    page.render(
                        pageBitmap,
                        null,
                        null,
                        PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                    )

                    page.close()

                    withContext(Dispatchers.Main) {
                        bitmap = pageBitmap
                    }
                }

                renderer.close()
                descriptor.close()
                file.delete()

            } catch (e: Exception) {
                Log.e(
                    "PdfPreview",
                    "Failed to load PDF",
                    e
                )
            }
        }
    }

    bitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = "PDF Preview",
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    }
}