package cat.xlagunas.contact.core

import cat.xlagunas.contact.domain.Friend
import cat.xlagunas.core.persistence.db.FriendEntity

/** The friends of `contact/contact_list.json` as Room emits them (a null picture is stored as ""). */
val jordi = Friend(10, "jordi", "Jordi", "https://example.com/jordi.png", "jordi@example.com", "ACCEPTED")
val marta = Friend(11, "marta", "Marta", "", "marta@example.com", "PENDING")

fun Friend.toEntity(userId: Long) = FriendEntity(friendId, userId, username, name, email, image.orEmpty(), relationshipStatus)
