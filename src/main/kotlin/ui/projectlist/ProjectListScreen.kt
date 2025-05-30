package ui.projectlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import data.models.Project
import data.network.NetworkService
import data.network.PreferencesManager
import kotlinx.coroutines.launch

@Composable
fun ProjectListScreen(
    projects: List<Project>,
    isLoading: Boolean,
    onProjectSelected: (Project) -> Unit,
    onCreateProject: (String) -> Unit,
    onLogout: () -> Unit,
    username: String,
    message: String?,
    isError: Boolean
) {
    var newProjectTitle by remember { mutableStateOf("") }
    var isCreatingProject by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(16.dp).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Добро пожаловать, $username!",
                style = MaterialTheme.typography.h5
            )
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error)
            ) {
                Text("Выйти")
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Создать новый проект:", style = MaterialTheme.typography.h6)
            OutlinedTextField(
                value = newProjectTitle,
                onValueChange = { newProjectTitle = it },
                label = { Text("Название проекта") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    if (newProjectTitle.isBlank()) return@Button
                    isCreatingProject = true
                    onCreateProject(newProjectTitle)
                    newProjectTitle = ""
                    isCreatingProject = false
                },
                enabled = !isCreatingProject,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isCreatingProject) {
                    CircularProgressIndicator(Modifier.size(20.dp))
                } else {
                    Text("Создать проект")
                }
            }
        }

        Text(
            "Ваши проекты:",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.align(Alignment.Start)
        )

        if (isLoading) {
            CircularProgressIndicator()
        } else if (projects.isEmpty()) {
            Text(
                "Нет проектов",
                style = MaterialTheme.typography.body1,
                color = Color.Gray,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(projects) { project ->
                    ProjectCard(project, onProjectSelected)
                }
            }
        }

        if (message != null) {
            Text(
                text = message,
                color = if (isError) MaterialTheme.colors.error else MaterialTheme.colors.primary,
                style = MaterialTheme.typography.body2
            )
        }
    }
}

@Composable
private fun ProjectCard(
    project: Project,
    onProjectSelected: (Project) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onProjectSelected(project) },
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                project.title,
                style = MaterialTheme.typography.h6
            )
            Text(
                "ID: ${project.id}",
                style = MaterialTheme.typography.caption
            )
            Text(
                "Автор: ${project.team_lead?.username ?: "N/A"}",
                style = MaterialTheme.typography.body2
            )
            if (project.members.isNotEmpty()) {
                Text(
                    "Участники: ${project.members.joinToString { it.username }}",
                    style = MaterialTheme.typography.body2
                )
            }
        }
    }
}