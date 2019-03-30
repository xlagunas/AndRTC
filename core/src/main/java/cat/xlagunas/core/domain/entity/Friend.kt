package cat.xlagunas.core.domain.entity

data class Friend(
    val friendId: Long,
    val username: String,
    val name: String,
    val image: String?,
    val email: String,
    val relationshipStatus: String
)
