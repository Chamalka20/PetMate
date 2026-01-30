package uk.ac.wlv.petmate.screens.pet

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import org.koin.androidx.compose.koinViewModel
import uk.ac.wlv.petmate.core.UiState
import uk.ac.wlv.petmate.model.PetType
import uk.ac.wlv.petmate.viewmodel.PetProfileViewModel
import uk.ac.wlv.petmate.R
import uk.ac.wlv.petmate.components.ImageTextButton
import uk.ac.wlv.petmate.viewmodel.SessionViewModel

@Composable
fun PetTypeStep(viewModel: PetProfileViewModel,

) {
    val petType by viewModel.petType.collectAsState()
    val petName by viewModel.petName.collectAsState()
    val savePetState by viewModel.savePetState.collectAsState()
    var showOtherDropdown by remember { mutableStateOf(false) }



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
                text = "Does \"$petName\" meow or woof?",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PetTypeCard(
                    modifier = Modifier.weight(1f),
                    label = "Cat",
                    lottieRes =  R.raw.cat_happy,
                    isSelected = petType == PetType.CAT,
                    onClick = { viewModel.updatePetType(PetType.CAT) }
                )

                PetTypeCard(
                    modifier = Modifier.weight(1f),
                    label = "Dog",
                    lottieRes = R.raw.cute_dog_loading_animation,
                    isSelected = petType == PetType.DOG,
                    onClick = { viewModel.updatePetType(PetType.DOG) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Box {
            OutlinedButton(
                onClick = { showOtherDropdown = !showOtherDropdown },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (petType!=null&& petType != PetType.DOG && petType != PetType.CAT)
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f )
                    else
                        MaterialTheme.colorScheme.background
                ),
                border = BorderStroke(
                    width = 2.dp,
                    color = if (petType != null && petType != PetType.DOG && petType != PetType.CAT)
                        MaterialTheme.colorScheme.primary
                    else
                        Color.LightGray
                )
            ) {
                Text(
                    text = petType
                        ?.takeIf { it != PetType.CAT && it != PetType.DOG }
                        ?.name
                        ?.lowercase()
                        ?.replaceFirstChar { it.uppercase() }
                        ?: "Other..."
                )

                Spacer(modifier = Modifier.weight(1f))

                Icon(Icons.Default.ArrowDropDown, "Dropdown")
            }


                DropdownMenu(
                    expanded = showOtherDropdown,
                    containerColor =  MaterialTheme.colorScheme.surface,
                    onDismissRequest = { showOtherDropdown = false },
                    modifier = Modifier.width(180.dp) // optional
                ) {
                    PetType.entries .filter { it != PetType.CAT && it != PetType.DOG }.forEach { petType ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    petType.name.lowercase()
                                        .replaceFirstChar { it.uppercase() }
                                )
                            },
                            onClick = {
                                viewModel.updatePetType(petType)
                                showOtherDropdown = false
                            }
                        )
                    }
                }
            }

        }
        ImageTextButton(
            text = "Next",
            isLoading = savePetState is UiState.Loading ,
            onClick = {

                    viewModel.savePet()
            },
            backgroundColor = MaterialTheme.colorScheme.primary,
            textColor = Color.White,
            progressIndicatorColor = Color.White,
        )

    }
}

@Composable
fun PetTypeCard(
    modifier: Modifier = Modifier,
    label: String,
    lottieRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(lottieRes)
    )

    Card(
        modifier = modifier.height(180.dp),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f )
            else
                MaterialTheme.colorScheme.background
        ),
        border = BorderStroke(
            width = 2.dp,
            color = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                Color.LightGray
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {


            LottieAnimation(
                composition = composition,
                iterations = if (isSelected)
                    LottieConstants.IterateForever
                else
                    1,
                modifier = Modifier.size(90.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = label,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
