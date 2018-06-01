package cat.xlagunas.data.common.converter

import cat.xlagunas.data.common.db.FriendEntity
import cat.xlagunas.data.common.net.FriendDto
import cat.xlagunas.domain.commons.Friend

class FriendConverter {

    fun toFriend(friendDto: FriendDto): Friend {
        return Friend(
                friendId = friendDto.id,
                username = friendDto.username,
                email = friendDto.email,
                image = friendDto.profilePic ?: "defaultImage",
                name = friendDto.name,
                relationshipStatus = friendDto.relationship.name)
    }

    fun toFriend(friendEntity: FriendEntity): Friend {
        return Friend(
                friendId = friendEntity.friendId,
                username = friendEntity.username,
                email = friendEntity.email,
                image = friendEntity.imageUrl,
                name = friendEntity.name,
                relationshipStatus = friendEntity.relationShipStatus)
    }
}