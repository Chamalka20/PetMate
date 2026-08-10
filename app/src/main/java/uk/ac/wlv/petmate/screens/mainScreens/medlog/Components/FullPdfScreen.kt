package uk.ac.wlv.petmate.screens.mainScreens.medlog.Components

import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.Surface
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.createBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

@Composable
fun FullPdfScreen(
    pdfUrl: String,
    onBack: () -> Unit
) {
    Dialog(
        onDismissRequest = onBack
    ) {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {

            Column(
                modifier = Modifier.fillMaxSize()
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }

                    Text(
                        text = "Prescription",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                FullPdfFromUrl(
                    pdfUrl = pdfUrl,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun FullPdfFromUrl(
    pdfUrl: String,
    modifier: Modifier = Modifier
) {
    var pages by remember {
        mutableStateOf<List<Bitmap>>(emptyList())
    }

    LaunchedEffect(pdfUrl) {

        withContext(Dispatchers.IO) {

            try {

                val client = OkHttpClient()

                val request = Request.Builder()
                    .url(pdfUrl)
                    .build()

                val response =
                    client.newCall(request).execute()

                if (!response.isSuccessful) {
                    Log.e(
                        "FullPdf",
                        "HTTP error: ${response.code}"
                    )
                    return@withContext
                }

                val bytes =
                    response.body?.bytes()
                        ?: return@withContext

                val file = File.createTempFile(
                    "full_pdf",
                    ".pdf"
                )

                file.writeBytes(bytes)

                val descriptor =
                    ParcelFileDescriptor.open(
                        file,
                        ParcelFileDescriptor.MODE_READ_ONLY
                    )

                val renderer =
                    PdfRenderer(descriptor)

                val bitmapList = mutableListOf<Bitmap>()

                for (pageIndex in 0 until renderer.pageCount) {

                    val page =
                        renderer.openPage(pageIndex)

                    val scale = 1.5f

                    val bitmap = createBitmap(
                        (page.width * scale).toInt(),
                        (page.height * scale).toInt()
                    )

                    val destination = Rect(
                        0,
                        0,
                        bitmap.width,
                        bitmap.height
                    )

                    page.render(
                        bitmap,
                        destination,
                        null,
                        PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                    )

                    bitmapList.add(bitmap)

                    page.close()
                }

                renderer.close()
                descriptor.close()
                file.delete()

                withContext(Dispatchers.Main) {
                    pages = bitmapList
                }

            } catch (e: Exception) {

                Log.e(
                    "FullPdf",
                    "Failed to load PDF",
                    e
                )
            }
        }
    }

    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        items(pages) { bitmap ->

            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "PDF page",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                contentScale = ContentScale.FillWidth
            )
        }
    }
}