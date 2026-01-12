package uk.ac.wlv.petmate.core

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

sealed class SnackbarType(
    val icon: ImageVector,
    val backgroundColor: Color,
    val textColor: Color,
    val iconColor: Color
) {
    object Success : SnackbarType(
        icon = Icons.Default.CheckCircle,
        backgroundColor = Color(0xFF4CAF50),
        textColor = Color.White,
        iconColor = Color.White
    )

    object Error : SnackbarType(
        icon = Icons.Default.Error,
        backgroundColor = Color(0xFFF44336),
        textColor = Color.White,
        iconColor = Color.White
    )

    object Warning : SnackbarType(
        icon = Icons.Default.Warning,
        backgroundColor = Color(0xFFFF9800),
        textColor = Color.White,
        iconColor = Color.White
    )

    object Info : SnackbarType(
        icon = Icons.Default.Info,
        backgroundColor = Color(0xFF2196F3),
        textColor = Color.White,
        iconColor = Color.White
    )

    object NoInternet : SnackbarType(
        icon = Icons.Default.WifiOff,
        backgroundColor = Color(0xFF607D8B),
        textColor = Color.White,
        iconColor = Color.White
    )

    object Connected : SnackbarType(
        icon = Icons.Default.Wifi,
        backgroundColor = Color(0xFF4CAF50),
        textColor = Color.White,
        iconColor = Color.White
    )
}