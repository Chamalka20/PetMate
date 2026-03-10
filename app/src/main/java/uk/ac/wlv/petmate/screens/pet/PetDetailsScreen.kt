package uk.ac.wlv.petmate.screens.pet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import uk.ac.wlv.petmate.components.NetworkCircleImage
import uk.ac.wlv.petmate.core.UiState
import uk.ac.wlv.petmate.data.model.Pet
import uk.ac.wlv.petmate.ui.theme.PrimaryFontFamily
import uk.ac.wlv.petmate.viewmodel.PetProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetDetailsScreen(
    petId: String,
    petProfileViewModel: PetProfileViewModel,
    navController: NavHostController,
) {

    val petState by petProfileViewModel.selectedPetState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(petId) {
        petProfileViewModel.loadPet(petId)
    }

    when (petState) {

        is UiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is UiState.Error -> {
            val message = (petState as UiState.Error).message
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = message)
            }
        }

        is UiState.Success -> {

            val pet = (petState as UiState.Success<Pet>).data

            Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
                topBar = {
                    TopAppBar(
                        title = { Text(pet.name) },
                        navigationIcon = {
                            IconButton(onClick = {  navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                petProfileViewModel.populatePetForEdit(pet)
                                navController.navigate("petEditScreen/${pet.id}")
                            }) {
                                Icon(Icons.Default.Edit, "Edit")
                            }
                            IconButton(onClick = { showDeleteDialog = true }) {
                                Icon(Icons.Default.Delete, "Delete")
                            }
                        }
                    )
                }
            ) { padding ->

                if (showDeleteDialog) {
                    AlertDialog(
                        onDismissRequest = { showDeleteDialog = false },
                        title = { Text("Delete Pet", style = TextStyle(
                            fontFamily = PrimaryFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 20.sp,
                            color = Color.Black
                        )) },
                        text = { Text("Are you sure you want to delete ${pet.name}'s profile? This action cannot be undone.") },
                        confirmButton = {
                            TextButton(onClick = {
                                showDeleteDialog = false
                                petProfileViewModel.deletePet(pet.id) {
                                    navController.popBackStack()
                                }
                            }) {
                                Text("Delete", color = MaterialTheme.colorScheme.error)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDeleteDialog = false }) {
                                Text("Cancel")
                            }
                        }
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        NetworkCircleImage(
                            imageUrl = pet.imageUrl.ifEmpty {
                                "https://via.placeholder.com/400"
                            },
                            contentDescription = pet.name,
                            size= 150.dp
                        )

                    }

                    Column(modifier = Modifier.padding(16.dp)) {

                        SectionTitle("Basic Information")
                        Spacer(modifier = Modifier.height(8.dp))

                        InfoRow(
                            "Type",
                            pet.type?.name?.lowercase()
                                ?.replaceFirstChar { it.uppercase() }
                                ?: "Not specified"
                        )

                        InfoRow(
                            "Breed",
                            pet.breed.ifEmpty { "Not specified" }
                        )

                        InfoRow(
                            "Age",
                            "${pet.age} years"
                        )

                        InfoRow(
                            "Spayed/Neutered",
                            if (pet.isSpayedNeutered) "Yes" else "No"
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        SectionTitle("Medical Conditions")
                        Spacer(modifier = Modifier.height(8.dp))

                        if (pet.medicalConditions.isEmpty()) {
                            Text(
                                "None",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        } else {
                            pet.medicalConditions.forEach {
                                ChipItem(it)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        SectionTitle("Allergies")
                        Spacer(modifier = Modifier.height(8.dp))

                        if (pet.allergies.isEmpty()) {
                            Text(
                                "None",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        } else {
                            pet.allergies.forEach {
                                ChipItem(it)
                            }
                        }
                    }
                }
            }
        }

        UiState.Idle -> {

            Box(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ChipItem(text: String) {
    Surface(
        modifier = Modifier.padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}