package com.prog7314.isitwaterjetpack.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prog7314.isitwaterjetpack.BuildConfig
import com.prog7314.isitwaterjetpack.data.WaterRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WaterUiState(
    val latitude: Double = randomLatitude(),
    val longitude: Double = randomLongitude(),
    val loading: Boolean = false,
    val userGuess: Boolean? = null, // true = guessed water, false = guessed land
    val correctAnswer: Boolean? = null, // actual API result
    val elevation: Double? = null,
    val error: String? = null,
    val roundsPlayed: Int = 0,
    val score: Int = 0,
    val imageUrl: String = randomImageUrl()
) {
    companion object {
        fun randomImageUrl(): String =
            "https://picsum.photos/400/300?random=${System.currentTimeMillis()}"
        fun randomLatitude(): Double = (-90..90).random() + Math.random()
        fun randomLongitude(): Double = (-180..180).random() + Math.random()
    }
}

class WaterViewModel(
    private val repository: WaterRepository = WaterRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(WaterUiState())
    val uiState: StateFlow<WaterUiState> = _uiState

    private var currentJob: Job? = null

    fun makeGuess(guessIsWater: Boolean) {
        val state = _uiState.value
        // Prevent double guessing
        if (state.loading || state.userGuess != null) return
        _uiState.update { it.copy(userGuess = guessIsWater, loading = true, error = null) }
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            val lat = state.latitude
            val lon = state.longitude
            val waterResult = repository.fetchWaterStatus(lat, lon, BuildConfig.RAPID_API_KEY)
            val elevationResult = repository.fetchElevation(lat, lon)
            val correct = waterResult.getOrNull()
            val guess = guessIsWater
            val gained = if (correct != null && guess == correct) 1 else 0
            _uiState.update { s ->
                s.copy(
                    loading = false,
                    correctAnswer = correct,
                    elevation = elevationResult.getOrNull(),
                    error = waterResult.exceptionOrNull()?.message
                        ?: elevationResult.exceptionOrNull()?.message,
                    score = s.score + gained,
                    roundsPlayed = s.roundsPlayed + 1,
                )
            }
        }
    }

    fun nextRound() {
        // Only allow next if previous guess resolved
        if (_uiState.value.loading) return
        _uiState.update {
            it.copy(
                latitude = WaterUiState.randomLatitude(),
                longitude = WaterUiState.randomLongitude(),
                loading = false,
                userGuess = null,
                correctAnswer = null,
                elevation = null,
                error = null,
                imageUrl = WaterUiState.randomImageUrl()
            )
        }
    }

    fun refreshImage() = _uiState.update { it.copy(imageUrl = WaterUiState.randomImageUrl()) }
}
