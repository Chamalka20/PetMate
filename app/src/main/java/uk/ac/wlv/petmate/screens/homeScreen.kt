package uk.ac.wlv.petmate.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import org.koin.androidx.compose.koinViewModel
import uk.ac.wlv.petmate.viewmodel.SessionViewModel

@Composable
fun HomeScreen(sessionViewModel: SessionViewModel = koinViewModel()) {
    val user by sessionViewModel.currentUser
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column (  modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally ){
            Text("Hi! ${user?.name}",textAlign = TextAlign.Center )
            Text("Welcome to PetMate 🐶",textAlign = TextAlign.Center)

        }

    }
}