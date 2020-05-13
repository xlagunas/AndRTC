package cat.xlagunas.contact.domain

data class Friend(
    val friendId: Long,
    val username: String,
    val name: String,
    val image: String?,
    val email: String,
    val relationshipStatus: String
)
