package uk.ac.wlv.petmate.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import uk.ac.wlv.petmate.core.SnackbarController
import uk.ac.wlv.petmate.core.SnackbarData
import uk.ac.wlv.petmate.core.SnackbarDuration

@Composable
fun CustomSnackbarHost(
    modifier: Modifier = Modifier,
    bottomOffset: Dp = 65.dp){
    val snackbarData by SnackbarController.snackbarState.collectAsState()
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = bottomOffset),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = snackbarData != null,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(300)
            ) + fadeOut()
        ) {
            snackbarData?.let { data ->
                CustomSnackbar(
                    data = data,
                    onDismiss = { SnackbarController.dismissSnackbar() }
                )
            }
        }
    }
}


@Composable
fun CustomSnackbar(
    data: SnackbarData,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Auto-dismiss after duration
    LaunchedEffect(data) {
        if (data.duration != SnackbarDuration.INDEFINITE) {
            delay(data.duration.milliseconds)
            onDismiss()
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),

        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = data.type.backgroundColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = data.type.icon,
                    contentDescription = null,
                    tint = data.type.iconColor,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = data.message,
                    color = data.type.textColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            if (data.actionLabel != null && data.onActionClick != null) {
                Spacer(modifier = Modifier.width(8.dp))

                TextButton(
                    onClick = {
                        data.onActionClick.invoke()
                        onDismiss()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = data.type.textColor
                    )
                ) {
                    Text(
                        text = data.actionLabel,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            // Close icon ONLY for INDEFINITE
            if (data.duration == SnackbarDuration.INDEFINITE) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = data.type.textColor,
                    modifier = Modifier
                        .size(18.dp)
                        .clickable { onDismiss() }
                )
            }
        }
    }
}