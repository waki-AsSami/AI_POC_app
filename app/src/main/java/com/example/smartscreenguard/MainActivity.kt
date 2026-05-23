package com.example.smartscreenguard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.smartscreenguard.model.VideoPair
import com.example.smartscreenguard.ui.components.RenameDialog
import com.example.smartscreenguard.ui.screens.HomeScreen
import com.example.smartscreenguard.ui.screens.ProcessingScreen
import com.example.smartscreenguard.ui.screens.ResultScreen
import com.example.smartscreenguard.ui.theme.SmartScreenGuardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartScreenGuardTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val packageName = context.packageName

    fun getRawUri(resId: Int): String = "android.resource://$packageName/$resId"

    val initialVideoPairs = listOf(
        VideoPair(1, "input1", "Process: Masking account numbers and balances", getRawUri(R.raw.input1), getRawUri(R.raw.output1)),
        VideoPair(2, "input2", "Process: Hiding personal details and QR codes", getRawUri(R.raw.input2), getRawUri(R.raw.output2)),
        VideoPair(3, "input3", "Process: Detecting and masking 2FA codes", getRawUri(R.raw.input3), getRawUri(R.raw.output3))
    )

    val videoPairs = remember { mutableStateListOf<VideoPair>().apply { addAll(initialVideoPairs) } }
    var showRenameDialog by remember { mutableStateOf(false) }
    var pendingUri by remember { mutableStateOf<Uri?>(null) }

    if (showRenameDialog) {
        RenameDialog(
            onConfirm = { name ->
                pendingUri?.let { uri ->
                    val nextId = (videoPairs.maxOfOrNull { it.id } ?: 0) + 1
                    videoPairs.add(VideoPair(
                        id = nextId,
                        title = name,
                        subtitle = "Process: New video for analysis",
                        inputUri = uri.toString(),
                        outputUri = uri.toString()
                    ))
                }
                showRenameDialog = false
                pendingUri = null
            },
            onDismiss = {
                showRenameDialog = false
                pendingUri = null
            }
        )
    }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            pendingUri = it
            showRenameDialog = true
        }
    }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                videoPairs = videoPairs,
                onVideoSelected = { selectedPair ->
                    navController.navigate("processing/${selectedPair.id}")
                },
                onAddVideo = {
                    videoPickerLauncher.launch("video/*")
                },
                onPlayInput = { selectedPair ->
                    navController.navigate("input_player/${selectedPair.id}")
                }
            )
        }
        composable(
            route = "input_player/{videoId}",
            arguments = listOf(navArgument("videoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val videoId = backStackEntry.arguments?.getInt("videoId") ?: 0
            val inputUri = videoPairs.find { it.id == videoId }?.inputUri ?: ""
            ResultScreen(videoUri = inputUri) {
                navController.popBackStack()
            }
        }
        composable(
            route = "processing/{videoId}",
            arguments = listOf(navArgument("videoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val videoId = backStackEntry.arguments?.getInt("videoId") ?: 0
            ProcessingScreen {
                navController.navigate("result/$videoId") {
                    popUpTo("home")
                }
            }
        }
        composable(
            route = "result/{videoId}",
            arguments = listOf(navArgument("videoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val videoId = backStackEntry.arguments?.getInt("videoId") ?: 0
            val outputUri = videoPairs.find { it.id == videoId }?.outputUri ?: ""
            ResultScreen(videoUri = outputUri) {
                navController.popBackStack()
            }
        }
    }
}
