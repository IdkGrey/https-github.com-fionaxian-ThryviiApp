package com.thryvii.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.thryvii.app.navigation.ThryviiNavHost
import com.thryvii.app.ui.theme.ThryviiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ThryviiTheme {
                ThryviiNavHost()
            }
        }
    }
}
