package com.inkwell.feature.maps.ui

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.inkwell.core.data.repository.model.SavedLocation
import com.inkwell.core.data.repository.WorldNoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MapsUiState(
    val currentLocation: Location? = null,
    val savedLocations: List<SavedLocation> = emptyList(),
    val permissionGranted: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class MapsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val worldNoteRepository: WorldNoteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapsUiState())
    val uiState: StateFlow<MapsUiState> = _uiState.asStateFlow()

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private var currentNovelId: String = ""

    fun loadLocations(novelId: String) {
        currentNovelId = novelId
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val locations = worldNoteRepository.getLocationsByNovelId(novelId)
                _uiState.update { state ->
                    state.copy(
                        savedLocations = locations,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(isLoading = false, error = e.message)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val location = fusedLocationClient.lastLocation.await()
                _uiState.update { state ->
                    state.copy(
                        currentLocation = location,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(isLoading = false, error = e.message)
                }
            }
        }
    }

    fun addLocation(
        name: String,
        latitude: Double,
        longitude: Double,
        description: String
    ) {
        viewModelScope.launch {
            try {
                val location = SavedLocation(
                    novelId = currentNovelId,
                    name = name,
                    latitude = latitude,
                    longitude = longitude,
                    description = description
                )
                worldNoteRepository.insertLocation(location)
                loadLocations(currentNovelId)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun deleteLocation(locationId: String) {
        viewModelScope.launch {
            try {
                worldNoteRepository.deleteLocation(locationId)
                loadLocations(currentNovelId)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun setPermissionGranted(granted: Boolean) {
        _uiState.update { it.copy(permissionGranted = granted) }
        if (granted) {
            getCurrentLocation()
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

@SuppressLint("MissingPermission")
private suspend fun FusedLocationProviderClient.lastLocation(): Location? {
    return kotlinx.coroutines.tasks.await(lastLocation)
}
