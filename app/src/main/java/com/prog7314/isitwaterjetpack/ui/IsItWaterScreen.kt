package com.prog7314.isitwaterjetpack.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.prog7314.isitwaterjetpack.ui.theme.IsItWaterJetpackTheme

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun IsItWaterScreen(viewModel: WaterViewModel) {
    val state by viewModel.uiState.collectAsState()
    IsItWaterScreenContent(
        state = state,
        onGuessWater = { viewModel.makeGuess(true) },
        onGuessLand = { viewModel.makeGuess(false) },
        onNextRound = viewModel::nextRound
    )
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun IsItWaterScreenContent(
    state: WaterUiState,
    onGuessWater: () -> Unit = {},
    onGuessLand: () -> Unit = {},
    onNextRound: () -> Unit = {}
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Is It Water?", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Round: ${state.roundsPlayed + 1}")
            Text("Score: ${state.score}")
            // Coordinates generated randomly each round (IsItWater, 2025)
            Text("Latitude: ${"%.4f".format(state.latitude)}  Longitude: ${"%.4f".format(state.longitude)}")

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onGuessWater,
                    enabled = !state.loading && state.userGuess == null
                ) { Text("Guess WATER") }
                Button(
                    onClick = onGuessLand,
                    enabled = !state.loading && state.userGuess == null
                ) { Text("Guess LAND") }
            }

            if (state.loading) {
                CircularProgressIndicator()
            }

            if (state.userGuess != null && !state.loading) {
                val correct = state.correctAnswer
                if (correct != null) {
                    val wasRight = state.userGuess == correct
                    Text(
                        text = if (wasRight) "Correct!" else "Incorrect.",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = if (wasRight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                    // Result from IsItWater API (IsItWater, 2025)
                    Text("Actual: ${if (correct) "WATER" else "LAND"}")
                    // Elevation via OpenTopoData (OpenTopoData, 2025)
                    state.elevation?.let { elev ->
                        Text("Elevation: ${"%.2f".format(elev)} m")
                    }
                    Button(onClick = onNextRound) { Text("Next Round") }
                }
            }

            state.error?.let { err ->
                Text(err, color = MaterialTheme.colorScheme.error)
            }

            GlideImage(
                model = state.imageUrl, // Random illustrative image each round (Lorem Picsum, 2025)
                contentDescription = "Random image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )


            Spacer(modifier = Modifier.weight(1f))
            // Win counter at bottom (IsItWater, 2025)
            Text("Wins: ${state.score} / ${state.roundsPlayed}", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Preview
@Composable
private fun PreviewIsItWaterScreen() {
    IsItWaterJetpackTheme {
        IsItWaterScreenContent(
            state = WaterUiState(
                latitude = 12.3456,
                longitude = 78.9123,
                loading = false,
                userGuess = true,
                correctAnswer = true,
                elevation = 5.5,
                error = null,
                score = 3,
                roundsPlayed = 4,
                imageUrl = WaterUiState.randomImageUrl()
            ),
            onGuessWater = {},
            onGuessLand = {},
            onNextRound = {}
        )
    }
}

// Reference list
// IsItWater, 2025. IsItWater API. Available at: https://rapidapi.com (Accessed: 19 Aug 2025).
// OpenTopoData, 2025. OpenTopoData Elevation API (etopo1). Available at: https://www.opentopodata.org (Accessed: 19 Aug 2025).
// Lorem Picsum, 2025. Placeholder image service. Available at: https://picsum.photos (Accessed: 19 Aug 2025).
