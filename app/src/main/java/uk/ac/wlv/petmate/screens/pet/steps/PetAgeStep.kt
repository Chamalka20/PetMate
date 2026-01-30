package uk.ac.wlv.petmate.screens.pet.steps

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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.ac.wlv.petmate.components.ImageTextButton
import uk.ac.wlv.petmate.core.UiState
import uk.ac.wlv.petmate.viewmodel.PetProfileViewModel

@Composable
fun PetAgeStep(viewModel: PetProfileViewModel) {
    val petAge by viewModel.petAge.collectAsState()
    val petName by viewModel.petName.collectAsState()
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "How old is $petName?",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            OutlinedTextField(
                value = if (petAge > 0) petAge.toString() else "",
                onValueChange = {
                    val age = it.toIntOrNull() ?: 0
                    if (age in 0..50) {
                        viewModel.updatePetAge(age)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter age in years") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        if (petAge > 0) {
                            viewModel.nextStep()
                        }
                    }
                ),
                supportingText = {
                    Text("Age in years (0-50)")
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Quick select buttons
            Text(
                text = "Quick select:",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "< 1 year" to 0,
                    "1-3 years" to 2,
                    "4-7 years" to 5,
                    "8+ years" to 8
                ).forEach { (label, age) ->
                    FilterChip(
                        selected = petAge == age,
                        onClick = { viewModel.updatePetAge(age) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f ),
                            selectedLabelColor = MaterialTheme.colorScheme.primary,

                            ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = petAge == age,
                            selectedBorderColor = MaterialTheme.colorScheme.primary,
                            borderWidth =  1.dp
                        ),
                        label = {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 12.sp,
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),

                    )
                }
            }
        }
        ImageTextButton(
            text = "Next",
            isLoading = false ,
            onClick = { viewModel.nextStep() },
            backgroundColor = MaterialTheme.colorScheme.primary,
            textColor = Color.White,
            progressIndicatorColor = Color.White,
        )

    }
}