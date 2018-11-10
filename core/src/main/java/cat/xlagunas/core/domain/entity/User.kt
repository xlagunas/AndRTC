package cat.xlagunas.core.domain.entity

data class User(
    val id: Long?,
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val imageUrl: String,
    val password: String?
) {

    constructor(
        username: String,
        firstName: String,
        lastName: String,
        email: String,
        imageUrl: String,
        password: String?
    ) : this(null, username, firstName, lastName, email, imageUrl, password)
}
