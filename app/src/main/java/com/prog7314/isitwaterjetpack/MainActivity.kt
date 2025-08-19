package com.prog7314.isitwaterjetpack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.prog7314.isitwaterjetpack.ui.IsItWaterScreen
import com.prog7314.isitwaterjetpack.ui.WaterViewModel
import com.prog7314.isitwaterjetpack.ui.theme.IsItWaterJetpackTheme

class MainActivity : ComponentActivity() {
    private val viewModel: WaterViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IsItWaterJetpackTheme {
                IsItWaterScreen(viewModel = viewModel)
            }
        }
    }
}
