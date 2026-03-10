package uk.ac.wlv.petmate.screens.pet
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import uk.ac.wlv.petmate.components.ImageTextButton
import uk.ac.wlv.petmate.components.NetworkCircleImage
import uk.ac.wlv.petmate.core.UiState
import uk.ac.wlv.petmate.data.model.PetType
import uk.ac.wlv.petmate.viewmodel.PetProfileViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.draw.clip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetEditScreen(
    petId: String,
    viewModel: PetProfileViewModel,
    navController: NavHostController
) {
    val savePetState by viewModel.savePetState.collectAsState()

    val petName by viewModel.petName.collectAsState()
    val petAge by viewModel.petAge.collectAsState()
    val petType by viewModel.petType.collectAsState()
    val petImageUri by viewModel.petImageUri.collectAsState()
    val isSpayedNeutered by viewModel.isSpayedNeutered.collectAsState()
    val medicalConditions by viewModel.medicalConditions.collectAsState()
    val allergies by viewModel.allergies.collectAsState()

    var expandedType by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.updatePetImage(it) }
    }

    LaunchedEffect(savePetState) {
        if (savePetState is UiState.Success) {
            viewModel.resetPetState()
            navController.popBackStack()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Edit Pet") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Image Picker
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (petImageUri != null) {
                    Box(
                        modifier = Modifier.size(120.dp)
                    ) {

                        NetworkCircleImage(
                            imageUrl = petImageUri.toString(),
                            contentDescription = "Pet Image",
                            size = 120.dp
                        )

                        Box(
                            modifier = Modifier
                                .size(23.dp)
                                .align(Alignment.BottomEnd)
                                .offset(x = 1.dp, y = (-13).dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Image",
                                modifier = Modifier.size(12.dp),
                                tint = Color.White
                            )
                        }
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = "Add Photo",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Name
            OutlinedTextField(
                value = petName,
                onValueChange = { viewModel.updatePetName(it) },
                label = { Text("Pet Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Type Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedType,
                onExpandedChange = { expandedType = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = petType?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Pet Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedType,
                    onDismissRequest = { expandedType = false }
                ) {
                    PetType.entries.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.name.lowercase().replaceFirstChar { it.uppercase() }) },
                            onClick = {
                                viewModel.updatePetType(type)
                                expandedType = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Age
            OutlinedTextField(
                value = if (petAge > 0) petAge.toString() else "",
                onValueChange = {
                    val age = it.toIntOrNull() ?: 0
                    if (age in 0..50) {
                        viewModel.updatePetAge(age)
                    }
                },
                label = { Text("Age in years") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Spayed/Neutered Checkbox
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isSpayedNeutered ?: false,
                    onCheckedChange = { viewModel.updateSpayedNeutered(it) }
                )
                Text("Spayed/Neutered", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Medical Conditions
            Text(
                text = "Medical Conditions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            androidx.compose.foundation.layout.FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                medicalConditions.forEach { condition ->
                    FilterChip(
                        selected = condition.isSelected,
                        onClick = { viewModel.toggleMedicalCondition(condition.id) },
                        label = { Text(condition.name) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Allergies
            Text(
                text = "Allergies",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            androidx.compose.foundation.layout.FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                allergies.forEach { allergy ->
                    FilterChip(
                        selected = allergy.isSelected,
                        onClick = { viewModel.toggleAllergy(allergy.id) },
                        label = { Text(allergy.name) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Save Button
            ImageTextButton(
                text = "Save Changes",
                isLoading = savePetState is UiState.Loading,
                onClick = { viewModel.updatePet() },
                backgroundColor = MaterialTheme.colorScheme.primary,
                textColor = Color.White,
                progressIndicatorColor = Color.White
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
