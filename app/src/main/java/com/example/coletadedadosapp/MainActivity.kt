package com.example.coletadedadosapp // corrigido para coincidir com a pasta

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.exemplo.coletadedadosapp.ui.theme.ColetaDeDadosAppTheme
import com.google.android.gms.location.*

class MainActivity : ComponentActivity() {

    // Cliente da API de localização do Google
    private lateinit var fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o cliente de localização
        fusedLocationClient = com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(this)

        setContent {
            ColetaDeDadosAppTheme {
                androidx.compose.material3.Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    // Passamos o cliente de localização para a nossa tela
                    DataCollectionScreen(fusedLocationClient)
                }
            }
        }
    }
}

// Tornar o cliente opcional para permitir Preview sem serviços
@androidx.compose.runtime.Composable
fun DataCollectionScreen(fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient?) {
    // Contexto atual, necessário para verificar permissões e mostrar Toasts
    val context = LocalContext.current

    // Estados para os campos de texto
    var nome by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
    var cpf by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
    var data by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
    var localizacao by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("Nenhuma localização obtida") }

    // --- LÓGICA DE PERMISSÃO ---
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isFineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val isCoarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (isFineLocationGranted || isCoarseLocationGranted) {
            if (fusedLocationClient != null) {
                getCurrentLocation(context, fusedLocationClient) { lat, lon ->
                    localizacao = "Lat: $lat, Lon: $lon"
                }
            } else {
                Toast.makeText(context, "Cliente de localização indisponível.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Permissão de localização negada.", Toast.LENGTH_SHORT).show()
        }
    }

    fun requestLocation() {
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fusedLocationClient == null) {
            Toast.makeText(context, "Cliente de localização indisponível.", Toast.LENGTH_SHORT).show()
            return
        }

        if (hasFineLocationPermission || hasCoarseLocationPermission) {
            getCurrentLocation(context, fusedLocationClient) { lat, lon ->
                localizacao = "Lat: $lat, Lon: $lon"
            }
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // --- INTERFACE GRÁFICA (UI) ---
    androidx.compose.foundation.layout.Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
    ) {
        androidx.compose.material3.Text(text = "Formulário de Coleta", style = androidx.compose.material3.MaterialTheme.typography.headlineSmall)

        androidx.compose.material3.OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { androidx.compose.material3.Text("Nome Completo") },
            modifier = Modifier.fillMaxWidth()
        )

        androidx.compose.material3.OutlinedTextField(
            value = cpf,
            onValueChange = { cpf = it },
            label = { androidx.compose.material3.Text("CPF") },
            placeholder = { androidx.compose.material3.Text("000.000.000-00") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        androidx.compose.material3.OutlinedTextField(
            value = data,
            onValueChange = { data = it },
            label = { androidx.compose.material3.Text("Data") },
            placeholder = { androidx.compose.material3.Text("dd/mm/aaaa") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
            modifier = Modifier.fillMaxWidth()
        )

        androidx.compose.material3.Text(text = "Localização GPS:", style = androidx.compose.material3.MaterialTheme.typography.bodyLarge)
        androidx.compose.material3.Text(text = localizacao, style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)
        // O botão agora chama nossa função para requisitar a localização
        androidx.compose.material3.Button(onClick = { requestLocation() }) {
            androidx.compose.material3.Text("Atualizar Localização")
        }

        androidx.compose.material3.Button(onClick = { /* TODO: Lógica para abrir a câmera */ }) {
            androidx.compose.material3.Text("Tirar Foto")
        }

        androidx.compose.material3.Text(text = "Assinatura:", style = androidx.compose.material3.MaterialTheme.typography.bodyLarge)
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .border(width = 1.dp, color = Color.Gray)
        ) {
            // TODO: Lógica para capturar assinatura
        }

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(24.dp))

        androidx.compose.material3.Button(
            onClick = { /* TODO: Lógica para salvar e enviar os dados */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            androidx.compose.material3.Text("Salvar Coleta")
        }
    }
}

// Esta função faz a mágica de pegar a localização.
// A anotação SuppressLint é necessária porque já checamos a permissão antes de chamá-la.
@SuppressLint("MissingPermission")
private fun getCurrentLocation(
    context: Context,
    fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient,
    onLocationReceived: (Double, Double) -> Unit
) {
    // Configura o pedido de localização para alta precisão e apenas uma atualização.
    val locationRequest = com.google.android.gms.location.LocationRequest.Builder(com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, 10000L)
        .setWaitForAccurateLocation(true)
        .setMinUpdateIntervalMillis(5000L)
        .setMaxUpdateDelayMillis(15000L)
        .build()

    // Cria um callback para receber a localização
    val locationCallback = object : com.google.android.gms.location.LocationCallback() {
        override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
            // Pega a última localização do resultado
            locationResult.lastLocation?.let { location ->
                onLocationReceived(location.latitude, location.longitude)
                // Remove as atualizações para não consumir bateria
                fusedLocationClient.removeLocationUpdates(this)
            }
        }
    }

    // Pede as atualizações de localização
    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        .addOnFailureListener { e ->
            Toast.makeText(context, "Erro ao obter localização: ${e.message}", Toast.LENGTH_SHORT).show()
        }
}


@Preview(showBackground = true)
@androidx.compose.runtime.Composable
fun DefaultPreview() {
    ColetaDeDadosAppTheme {
        // No preview não temos serviços Google — passamos null
        DataCollectionScreen(fusedLocationClient = null)
    }
}
