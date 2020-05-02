package cat.xlagunas.contact.data

import cat.xlagunas.core.persistence.db.FriendEntity
import cat.xlagunas.core.persistence.db.UserEntity
import cat.xlagunas.contact.domain.Friend
import java.util.function.BiFunction

class FriendConverter {

    fun toFriend(friendDto: FriendDto): Friend {
        return Friend(
            friendId = friendDto.id,
            username = friendDto.username,
            email = friendDto.email,
            image = friendDto.profilePic,
            name = friendDto.name,
            relationshipStatus = friendDto.relationship.name
        )
    }

    fun toFriendList(friendDtoList: List<FriendEntity>): List<Friend> {
        return friendDtoList.map { toFriend(it) }
    }

    fun toFriend(friendEntity: FriendEntity): Friend {
        return Friend(
            friendId = friendEntity.friendId,
            username = friendEntity.username,
            email = friendEntity.email,
            image = friendEntity.imageUrl,
            name = friendEntity.name,
            relationshipStatus = friendEntity.relationShipStatus
        )
    }

    fun toFriendEntity(friend: Friend, currentUserId: Long): FriendEntity =
        FriendEntity(
            friendId = friend.friendId,
            username = friend.username,
            name = friend.name,
            imageUrl = friend.image ?: "",
            email = friend.email,
            relationShipStatus = friend.relationshipStatus,
            userId = currentUserId
        )

    fun toFriendEntity() =
        BiFunction { user: UserEntity, friend: Friend -> this.toFriendEntity(friend, user.id!!) }

    fun updateRelationship(friend: Friend, newRelationship: Relationship): Friend {
        return friend.copy(relationshipStatus = newRelationship.name)
    }

    fun toFriendDtoList(friends: List<Friend>): List<FriendDto> {
        return friends.map(this::toFriendDto).toList()
    }

    private fun toFriendDto(friend: Friend): FriendDto {
        return FriendDto(
            id = friend.friendId,
            email = friend.email,
            name = friend.name,
            username = friend.username,
            profilePic = friend.image,
            relationship = Relationship.valueOf(friend.relationshipStatus)
        )
    }
}