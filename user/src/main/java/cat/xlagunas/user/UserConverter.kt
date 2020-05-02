package cat.xlagunas.user

import cat.xlagunas.core.data.db.UserEntity
import cat.xlagunas.core.data.net.UserDto
import cat.xlagunas.user.User

class UserConverter {
    fun toUser(userEntity: UserEntity): cat.xlagunas.user.User =
        cat.xlagunas.user.User(
            id = userEntity.id,
            username = userEntity.username,
            firstName = userEntity.firstName,
            lastName = userEntity.lastName,
            email = userEntity.email,
            imageUrl = userEntity.imageUrl
                ?: "STRING_REFERENCE_FOR_DEFAULT_IMAGE_IN_USER_CONVERTER",
            password = null
        )

    fun toUserEntity(user: cat.xlagunas.user.User): UserEntity =
        UserEntity(
            id = user.id,
            username = user.username,
            firstName = user.firstName,
            lastName = user.lastName,
            email = user.email,
            imageUrl = user.imageUrl
        )

    fun toUserEntity(userDto: UserDto): UserEntity =
        UserEntity(
            id = userDto.id,
            username = userDto.username,
            firstName = userDto.firstName,
            lastName = userDto.lastName,
            email = userDto.email,
            imageUrl = userDto.profilePic
        )

    fun toUserDto(user: cat.xlagunas.user.User): UserDto =
        UserDto(
            id = user.id,
            username = user.username,
            firstName = user.firstName,
            lastName = user.lastName,
            email = user.email,
            profilePic = user.imageUrl,
            password = user.password
        )
}
