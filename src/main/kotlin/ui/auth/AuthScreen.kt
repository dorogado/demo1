package ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.network.NetworkService
import data.network.PreferencesManager
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit,
    networkService: NetworkService,
    preferencesManager: PreferencesManager
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }
    var isLoginMode by remember { mutableStateOf(true) }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = if (isLoginMode) "Вход в аккаунт" else "Регистрация",
            style = MaterialTheme.typography.h5
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Имя пользователя") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Person, null) }
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Lock, null) }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    if (username.isBlank() || password.isBlank()) {
                        message = "Заполните все поля"
                        isError = true
                        return@Button
                    }

                    isLoading = true
                    message = null

                    coroutineScope.launch {
                        try {
                            val result = networkService.authenticate(username, password, isLoginMode)
                            preferencesManager.saveAuthData(username, result.access)
                            message = if (isLoginMode) "Успешный вход!" else "Регистрация и вход выполнены успешно!"
                            isError = false
                            onLoginSuccess()
                        } catch (e: Exception) {
                            message = "Ошибка: ${e.message}"
                            isError = true
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (isLoginMode) MaterialTheme.colors.primary
                    else MaterialTheme.colors.secondary
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(Modifier.size(20.dp))
                } else {
                    Text(if (isLoginMode) "Войти" else "Зарегистрироваться")
                }
            }

            Button(
                onClick = { isLoginMode = !isLoginMode },
                enabled = !isLoading,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary)
            ) {
                Text(if (isLoginMode) "Регистрация" else "Вход")
            }
        }

        if (message != null) {
            Text(
                text = message!!,
                color = if (isError) MaterialTheme.colors.error else MaterialTheme.colors.primary,
                style = MaterialTheme.typography.body2
            )
        }
    }
}