package cat.xlagunas.data.common.net

data class FriendDto(val id: Long, val username: String, val firstName: String, val lastName: String, val email: String, val profilePic: String?, val relationship: Relationship)

enum class Relationship {}