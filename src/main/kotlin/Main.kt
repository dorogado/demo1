import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import data.models.Project
import data.models.ProjectBoard
import data.network.NetworkService
import data.network.PreferencesManager
import data.network.createHttpClient
import kotlinx.coroutines.launch
import ui.auth.AuthScreen
import ui.projectlist.ProjectListScreen
import ui.tasksboard.TaskBoardScreen

@Composable
@Preview
fun App() {
    val networkService = remember { NetworkService(createHttpClient()) }
    val preferencesManager = remember { PreferencesManager() }
    val coroutineScope = rememberCoroutineScope()

    var isLoggedIn by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }
    var projects by remember { mutableStateOf<List<Project>>(emptyList()) }
    var isLoadingProjects by remember { mutableStateOf(false) }
    var selectedProject by remember { mutableStateOf<Project?>(null) }
    var projectBoard by remember { mutableStateOf<ProjectBoard?>(null) }
    var isLoadingBoard by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val savedUsername = preferencesManager.getUsername()
        val savedToken = preferencesManager.getToken()

        if (savedUsername.isNotEmpty() && savedToken.isNotEmpty()) {
            username = savedUsername
            isLoggedIn = true
            isLoadingProjects = true
            try {
                projects = networkService.loadProjects(savedToken)
            } catch (e: Exception) {
                message = "Ошибка загрузки проектов: ${e.message}"
                isError = true
            } finally {
                isLoadingProjects = false
            }
        }
    }

    if (!isLoggedIn) {
        AuthScreen(
            onLoginSuccess = {
                isLoggedIn = true
                username = preferencesManager.getUsername()
                coroutineScope.launch {
                    isLoadingProjects = true
                    try {
                        projects = networkService.loadProjects(preferencesManager.getToken())
                    } catch (e: Exception) {
                        message = "Ошибка загрузки проектов: ${e.message}"
                        isError = true
                    } finally {
                        isLoadingProjects = false
                    }
                }
            },
            networkService = networkService,
            preferencesManager = preferencesManager
        )
    } else if (selectedProject != null && projectBoard != null) {
        TaskBoardScreen(
            project = selectedProject!!,
            board = projectBoard!!,
            isLoading = isLoadingBoard,
            onBack = {
                selectedProject = null
                projectBoard = null
            },
            onAddTask = { title ->
                coroutineScope.launch {
                    try {
                        val response = networkService.createTask(
                            preferencesManager.getToken(),
                            projectBoard!!.id,
                            title
                        )
                        projectBoard = networkService.loadProjectBoard(
                            preferencesManager.getToken(),
                            selectedProject!!.id
                        )
                        message = "Задача добавлена успешно!"
                        isError = false
                    } catch (e: Exception) {
                        message = "Ошибка при создании задачи: ${e.message}"
                        isError = true
                    }
                }
            },
            message = message,
            isError = isError
        )
    } else {
        ProjectListScreen(
            projects = projects,
            isLoading = isLoadingProjects,
            onProjectSelected = { project ->
                selectedProject = project
                isLoadingBoard = true
                coroutineScope.launch {
                    try {
                        projectBoard = networkService.loadProjectBoard(
                            preferencesManager.getToken(),
                            project.id
                        )
                        isLoadingBoard = false
                    } catch (e: Exception) {
                        message = e.message ?: "Error loading project board"
                        isError = true
                        isLoadingBoard = false
                    }
                }
            },
            onCreateProject = { title ->
                coroutineScope.launch {
                    try {
                        val newProject = networkService.createProject(
                            preferencesManager.getToken(),
                            title
                        )
                        projects = projects + newProject
                        message = "Проект создан успешно!"
                        isError = false
                    } catch (e: Exception) {
                        message = "Ошибка создания проекта: ${e.message}"
                        isError = true
                    }
                }
            },
            onLogout = {
                preferencesManager.clearAuthData()
                isLoggedIn = false
                username = ""
                projects = emptyList()
                selectedProject = null
                projectBoard = null
                message = null
            },
            username = username,
            message = message,
            isError = isError
        )
    }
}

fun main() = application {
    Window(
        title = "Project Management",
        onCloseRequest = ::exitApplication
    ) {
        App()
    }
}
