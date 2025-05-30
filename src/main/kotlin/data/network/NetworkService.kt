package data.network

import data.models.AuthRequest
import data.models.AuthResponse
import data.models.Project
import data.models.ProjectBoard
import data.models.ProjectCreateRequest
import data.models.TaskCreateRequest
import data.models.TaskCreateResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class NetworkService(private val client: HttpClient) {
    suspend fun loadProjects(token: String): List<Project> {
        return try {
            client.get("http://127.0.0.1:8000/") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }.body()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun loadProjectBoard(token: String, projectId: Int): ProjectBoard {
        return client.get("http://127.0.0.1:8000/api/projects/board/$projectId") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }.body()
    }

    suspend fun createTask(token: String, boardId: Int, title: String): TaskCreateResponse {
        return client.post("http://127.0.0.1:8000/api/projects/${boardId}/tasks/create") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
            contentType(ContentType.Application.Json)
            setBody(
                TaskCreateRequest(
                    title = title,
                    description = "bla bla bla",
                    board = boardId.toString(),
                    assigned_to_id = "2"
                )
            )
        }.body()
    }

    suspend fun createProject(token: String, title: String): Project {
        return client.post("http://127.0.0.1:8000/api/projects/create") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
            contentType(ContentType.Application.Json)
            setBody(ProjectCreateRequest(title))
        }.body()
    }

    suspend fun authenticate(username: String, password: String, isLogin: Boolean): AuthResponse {
        val endpoint = if (isLogin) "login" else "register"
        return client.post("http://127.0.0.1:8000/users/$endpoint/") {
            contentType(ContentType.Application.Json)
            setBody(AuthRequest(username, password))
        }.body()
    }
}