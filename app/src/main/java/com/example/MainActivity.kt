package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.calculator.CalculatorScreen
import com.example.ui.launcher.LauncherScreen
import com.example.ui.notepad.NotepadScreen
import com.example.ui.theme.HoyoDeepDark
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.LauncherViewModel
import com.example.viewmodel.ScreenDestination

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = HoyoDeepDark
                ) {
                    HoYoAppRoot()
                }
            }
        }
    }
}

@Composable
fun HoYoAppRoot(
    viewModel: LauncherViewModel = viewModel()
) {
    val context = LocalContext.current
    val currentScreen by viewModel.currentScreen.collectAsState()
    val infoMessage by viewModel.infoMessage.collectAsState()

    LaunchedEffect(infoMessage) {
        infoMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearInfoMessage()
        }
    }

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "screen_navigation"
    ) { screen ->
        when (screen) {
            is ScreenDestination.Launcher -> {
                LauncherScreen(viewModel = viewModel)
            }
            is ScreenDestination.CalculatorApp -> {
                CalculatorScreen(
                    viewModel = viewModel,
                    installState = screen.state
                )
            }
            is ScreenDestination.NotepadApp -> {
                NotepadScreen(
                    viewModel = viewModel,
                    installState = screen.state
                )
            }
        }
    }
}
