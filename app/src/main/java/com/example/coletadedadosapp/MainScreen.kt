package com.example.coletadedadosapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    // Estados para armazenar os valores dos campos
    var nome by remember { mutableStateOf("") }
    var cpf by remember { mutableStateOf("") }
    var data by remember { mutableStateOf("") }
    var localizacao by remember { mutableStateOf("Aguardando localização...") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Coleta de Dados") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()), // Permite rolar a tela se o conteúdo for grande
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp) // Espaço entre os componentes
        ) {
            Text(
                text = "Informações Pessoais",
                style = MaterialTheme.typography.headlineSmall
            )

            // Campo Nome
            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome Completo") },
                modifier = Modifier.fillMaxWidth()
            )

            // Campo CPF (a máscara será adicionada depois)
            OutlinedTextField(
                value = cpf,
                onValueChange = { cpf = it },
                label = { Text("CPF (xxx.xxx.xxx-xx)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // Campo Data (a máscara será adicionada depois)
            OutlinedTextField(
                value = data,
                onValueChange = { data = it },
                label = { Text("Data (dd/mm/aaaa)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Seção de Capturas
            Text(
                text = "Capturas",
                style = MaterialTheme.typography.headlineSmall
            )

            // Localização
            Text(text = "Localização GPS:", style = MaterialTheme.typography.titleMedium)
            Text(text = localizacao, color = Color.Gray)
            Button(onClick = { /* TODO: Lógica para buscar GPS */ }) {
                Text("Atualizar Localização")
            }

            // Foto
            Button(onClick = { /* TODO: Lógica para abrir a câmera */ }) {
                Text("Tirar Foto")
            }
            // Aqui mostraremos a foto depois de tirada
            // Box(modifier = Modifier.size(200.dp).background(Color.LightGray))

            // Assinatura
            Text("Assine abaixo:", style = MaterialTheme.typography.titleMedium)
            // Aqui ficará o campo de assinatura
            Box(modifier = Modifier.fillMaxWidth().height(150.dp).background(Color.LightGray))

            Spacer(modifier = Modifier.height(16.dp))

            // Botão de Envio
            Button(
                onClick = { /* TODO: Lógica para salvar/enviar os dados */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar Dados")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}