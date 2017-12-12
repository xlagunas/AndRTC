package cat.xlagunas.domain.commons

data class User(val id: Long?,
                val username: String,
                val firstName: String,
                val lastName: String,
                val email: String,
                val imageUrl: String) {

    constructor(username: String,
                firstName: String,
                lastName: String,
                email: String,
                imageUrl: String) : this(null, username, firstName, lastName, email, imageUrl)
}
