package cat.xlagunas.data.common.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "friend")
@ForeignKey(entity = cat.xlagunas.data.common.db.UserEntity::class, parentColumns = arrayOf("id"), childColumns = arrayOf("user_id"))
data class FriendEntity(
        @PrimaryKey
        @ColumnInfo(name = "id")
        val friendId: Long,
        @ColumnInfo(name = "user_id")
        val userId: Long,
        @ColumnInfo(name = "username")
        val username: String,
        @ColumnInfo(name = "name")
        val name: String,
        @ColumnInfo(name = "email")
        val email: String,
        @ColumnInfo(name = "profile_url")
        val imageUrl: String,
        @ColumnInfo(name = "relationship_status")
        val relationShipStatus: String)