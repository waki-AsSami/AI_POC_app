package com.example.smartscreenguard.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun ProcessingScreen(
    onProcessingComplete: () -> Unit,
) {
    val steps = listOf(
        "Loading video",
        "PaddleOCR extracting text",
        "Detecting sensitive content",
        "Scanning QR / barcode regions",
        "Stabilizing masks over time",
        "Rendering redacted output"
    )

    var currentStepIndex by remember { mutableIntStateOf(0) }
    var progress by remember { mutableFloatStateOf(0f) }

    val totalDuration = 5000L // 5 seconds for demo purposes
    val stepDuration = totalDuration / steps.size

    LaunchedEffect(Unit) {
        for (i in steps.indices) {
            currentStepIndex = i
            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() - startTime < stepDuration) {
                progress = (i.toFloat() + (System.currentTimeMillis() - startTime).toFloat() / stepDuration) / steps.size
                delay(100)
            }
        }
        progress = 1f
        delay(1000)
        onProcessingComplete()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Analyzing Video",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(32.dp))

            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(120.dp),
                strokeWidth = 8.dp,
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )

            Spacer(modifier = Modifier.height(32.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                steps.forEachIndexed { index, step ->
                    StepItem(
                        text = step,
                        isCompleted = index < currentStepIndex,
                        isCurrent = index == currentStepIndex
                    )
                }
            }
        }
    }
}

@Composable
fun StepItem(text: String, isCompleted: Boolean, isCurrent: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (isCompleted) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(24.dp)
            )
        } else if (isCurrent) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            Box(modifier = Modifier.size(24.dp))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isCurrent) MaterialTheme.colorScheme.onSurface 
                    else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
        )
    }
}
