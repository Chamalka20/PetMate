package uk.ac.wlv.petmate.screens.pet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.ac.wlv.petmate.viewmodel.PetProfileViewModel
import uk.ac.wlv.petmate.R
import uk.ac.wlv.petmate.components.ImageTextButton

@Composable
fun SpayedNeuteredStep(viewModel: PetProfileViewModel) {
    val isSpayedNeutered by viewModel.isSpayedNeutered.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Is your pet spayed / neutered?",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Yes Option
                OptionCard(
                    modifier = Modifier.weight(1f),
                    label = "Yes",
                    icon = R.drawable.ic_cat_happy,
                    isSelected = isSpayedNeutered == true,
                    onClick = { viewModel.updateSpayedNeutered(true) }
                )

                // No Option
                OptionCard(
                    modifier = Modifier.weight(1f),
                    label = "No",
                    icon = R.drawable.ic_cat_neutral,
                    isSelected = isSpayedNeutered == false,
                    onClick = { viewModel.updateSpayedNeutered(false) }
                )
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

@Composable
fun OptionCard(
    modifier: Modifier = Modifier,
    label: String,
    icon: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
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
            Image(
                painter = painterResource(id = icon),
                contentDescription = label,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = label,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}