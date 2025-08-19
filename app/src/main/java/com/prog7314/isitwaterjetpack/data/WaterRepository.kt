package com.prog7314.isitwaterjetpack.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class WaterRepository(
    private val client: OkHttpClient = OkHttpClient()
) {
    suspend fun fetchWaterStatus(latitude: Double, longitude: Double, apiKey: String): Result<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("https://isitwater-com.p.rapidapi.com/?latitude=$latitude&longitude=$longitude")
                    .get()
                    .addHeader("x-rapidapi-key", apiKey)
                    .addHeader("x-rapidapi-host", "isitwater-com.p.rapidapi.com")
                    .build()

                client.newCall(request).execute().use { response ->
                    val body = response.body?.string()
                    if (response.isSuccessful && body != null) {
                        val json = JSONObject(body)
                        Result.success(json.getBoolean("water"))
                    } else {
                        Result.failure(Exception("Water status request failed: ${response.code}"))
                    }
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun fetchElevation(latitude: Double, longitude: Double): Result<Double?> =
        withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("https://api.opentopodata.org/v1/etopo1?locations=$latitude,$longitude")
                    .get()
                    .build()

                client.newCall(request).execute().use { response ->
                    val body = response.body?.string()
                    if (response.isSuccessful && body != null) {
                        val json = JSONObject(body)
                        val results = json.optJSONArray("results")
                        val elevation = if (results != null && results.length() > 0) {
                            results.getJSONObject(0).optDouble("elevation")
                        } else null
                        Result.success(elevation)
                    } else {
                        Result.failure(Exception("Elevation request failed: ${response.code}"))
                    }
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}

