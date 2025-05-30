package data.models

import kotlinx.serialization.Serializable

@Serializable
data class ProjectBoard(
    val id: Int,
    val project: Int,
    val tasks: List<Task> = emptyList()
)

@Serializable
data class Task(
    val id: Int,
    val title: String,
    val description: String,
    val priority: String,
    val created_at: String,
    val board: Int,
    val assigned_to: AssignedTo
)

@Serializable
data class AssignedTo(
    val id: Int,
    val username: String
)

@Serializable
data class TaskCreateRequest(
    val title: String,
    val description: String,
    val board: String,
    val assigned_to_id: String
)

@Serializable
data class TaskCreateResponse(
    val id: Int,
    val title: String,
    val description: String,
    val priority: String? = null,
    val created_at: String? = null,
    val board: Int,
    val assigned_to: AssignedTo
)