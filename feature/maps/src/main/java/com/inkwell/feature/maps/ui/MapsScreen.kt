package com.inkwell.feature.maps.ui

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.inkwell.core.data.repository.model.SavedLocation
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapsScreen(
    novelId: String,
    onNavigateBack: () -> Unit,
    viewModel: MapsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showAddLocationDialog by remember { mutableStateOf(false) }
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        viewModel.setPermissionGranted(fineLocationGranted || coarseLocationGranted)
    }

    LaunchedEffect(novelId) {
        viewModel.loadLocations(novelId)
    }

    LaunchedEffect(Unit) {
        val hasFinePermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarsePermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasFinePermission || hasCoarsePermission) {
            viewModel.setPermissionGranted(true)
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    val mapsDescription = stringResource(
        com.inkwell.feature.characters.ui.R.string.maps_screen_description,
        uiState.savedLocations.size
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(com.inkwell.feature.characters.ui.R.string.writing_locations))
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.semantics {
                            contentDescription = stringResource(com.inkwell.feature.characters.ui.R.string.navigate_back)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddLocationDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.semantics {
                    contentDescription = stringResource(com.inkwell.feature.characters.ui.R.string.add_location)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .semantics {
                    contentDescription = mapsDescription
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                MapsComposable(
                    savedLocations = uiState.savedLocations,
                    currentLocation = uiState.currentLocation,
                    onMapClick = { latLng ->
                        selectedLocation = latLng
                        showAddLocationDialog = true
                    },
                    modifier = Modifier.fillMaxSize()
                )

                if (!uiState.permissionGranted) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = stringResource(com.inkwell.feature.characters.ui.R.string.location_permission_needed),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            TextButton(
                                onClick = {
                                    permissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                }
                            ) {
                                Text(text = stringResource(com.inkwell.feature.characters.ui.R.string.grant_permission))
                            }
                        }
                    }
                }
            }

            Text(
                text = stringResource(com.inkwell.feature.characters.ui.R.string.saved_locations),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            if (uiState.savedLocations.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(com.inkwell.feature.characters.ui.R.string.no_saved_locations),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = uiState.savedLocations,
                        key = { it.id }
                    ) { location ->
                        LocationCard(
                            location = location,
                            onDelete = { showDeleteDialog = location.id },
                            modifier = Modifier.semantics {
                                contentDescription = stringResource(
                                    com.inkwell.feature.characters.ui.R.string.location_card_description,
                                    location.name,
                                    location.description
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddLocationDialog) {
        AddLocationDialog(
            initialLocation = selectedLocation,
            onDismiss = {
                showAddLocationDialog = false
                selectedLocation = null
            },
            onConfirm = { name, description, latLng ->
                viewModel.addLocation(name, latLng.latitude, latLng.longitude, description)
                showAddLocationDialog = false
                selectedLocation = null
            }
        )
    }

    showDeleteDialog?.let { locationId ->
        val location = uiState.savedLocations.find { it.id == locationId }
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = {
                Text(text = stringResource(com.inkwell.feature.characters.ui.R.string.delete_location))
            },
            text = {
                Text(
                    text = stringResource(
                        com.inkwell.feature.characters.ui.R.string.delete_location_confirmation,
                        location?.name ?: ""
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteLocation(locationId)
                        showDeleteDialog = null
                    }
                ) {
                    Text(text = stringResource(com.inkwell.feature.characters.ui.R.string.delete))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = null }
                ) {
                    Text(text = stringResource(com.inkwell.feature.characters.ui.R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun MapsComposable(
    savedLocations: List<SavedLocation>,
    currentLocation: Location?,
    onMapClick: (LatLng) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    AndroidView(
        factory = { ctx ->
            MapView(ctx).apply {
                getMapAsync { googleMap ->
                    googleMap.uiSettings.isZoomControlsEnabled = true
                    googleMap.uiSettings.isMyLocationButtonEnabled = true

                    currentLocation?.let { location ->
                        val currentLatLng = LatLng(location.latitude, location.longitude)
                        googleMap.addMarker(
                            MarkerOptions()
                                .position(currentLatLng)
                                .title("Current Location")
                        )
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
                    }

                    savedLocations.forEach { location ->
                        val latLng = LatLng(location.latitude, location.longitude)
                        googleMap.addMarker(
                            MarkerOptions()
                                .position(latLng)
                                .title(location.name)
                                .snippet(location.description)
                        )
                    }

                    googleMap.setOnMapClickListener { latLng ->
                        onMapClick(latLng)
                    }
                }
            }
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddLocationDialog(
    initialLocation: LatLng?,
    onDismiss: () -> Unit,
    onConfirm: (name: String, description: String, location: LatLng) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf(initialLocation ?: LatLng(0.0, 0.0)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(com.inkwell.feature.characters.ui.R.string.add_location))
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = {
                        Text(text = stringResource(com.inkwell.feature.characters.ui.R.string.location_name))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = {
                        Text(text = stringResource(com.inkwell.feature.characters.ui.R.string.location_description))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(
                        com.inkwell.feature.characters.ui.R.string.coordinates,
                        location.latitude,
                        location.longitude
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name, description, location)
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text(text = stringResource(com.inkwell.feature.characters.ui.R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(com.inkwell.feature.characters.ui.R.string.cancel))
            }
        }
    )
}

@Composable
private fun LocationCard(
    location: SavedLocation,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = location.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                if (location.description.isNotBlank()) {
                    Text(
                        text = location.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = stringResource(
                        com.inkwell.feature.characters.ui.R.string.coordinates_format,
                        location.latitude,
                        location.longitude
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier.semantics {
                    contentDescription = stringResource(
                        com.inkwell.feature.characters.ui.R.string.delete_location,
                        location.name
                    )
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
