package cat.xlagunas.core.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "friend")
@ForeignKey(
    entity = UserEntity::class,
    parentColumns = ["id"],
    childColumns = ["user_id"]
)
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
    val relationShipStatus: String
)