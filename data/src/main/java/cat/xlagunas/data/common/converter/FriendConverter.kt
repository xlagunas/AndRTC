package cat.xlagunas.data.common.converter

import cat.xlagunas.data.common.db.FriendEntity
import cat.xlagunas.data.common.db.UserEntity
import cat.xlagunas.data.common.net.FriendDto
import cat.xlagunas.data.common.net.Relationship
import cat.xlagunas.domain.commons.Friend
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
}