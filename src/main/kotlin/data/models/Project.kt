package data.models

import kotlinx.serialization.Serializable

@Serializable
data class Project(
    val id: Int,
    val title: String,
    val description: String = "",
    val team_lead: TeamLead? = null,
    val members: List<Member> = emptyList()
)

@Serializable
data class TeamLead(
    val id: Int,
    val username: String
)

@Serializable
data class Member(
    val id: Int,
    val username: String
)

@Serializable
data class ProjectCreateRequest(
    val title: String
)

@Serializable
data class JoinProjectRequest(
    val project_id: Int
)

@Serializable
data class JoinProjectResponse(
    val success: Boolean,
    val message: String,
    val project: Project?
)