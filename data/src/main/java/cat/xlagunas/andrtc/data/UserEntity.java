package cat.xlagunas.andrtc.data;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by xlagunas on 26/02/16.
 */

public class UserEntity {
    @SerializedName("_id")
    private String id;
    @SerializedName("username")
    private String username;
    @SerializedName("email")
    private String email;
    @SerializedName("firstSurname")
    private String firstSurname;
    @SerializedName("lastSurname")
    private String lastSurname;
    @SerializedName("name")
    private String name;
    @SerializedName("password")
    private String password;
    @SerializedName("thumbnail")
    private String thumbnail;
    @SerializedName("joinDate")
    private DateTime joinDate;
    @SerializedName("requested")
    private List<FriendEntity> requested;
    @SerializedName("pending")
    private List<FriendEntity> pending;
    @SerializedName("blocked")
    private List<FriendEntity> blocked;
    @SerializedName("accepted")
    private List<FriendEntity> accepted;
    @SerializedName("status")
    private String status;
    @SerializedName("facebookId")
    private String facebookId;

    public UserEntity() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstSurname() {
        return firstSurname;
    }

    public void setFirstSurname(String firstSurname) {
        this.firstSurname = firstSurname;
    }

    public String getLastSurname() {
        return lastSurname;
    }

    public void setLastSurname(String lastSurname) {
        this.lastSurname = lastSurname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public DateTime getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(DateTime joinDate) {
        this.joinDate = joinDate;
    }

    public List<FriendEntity> getRequested() {
        return requested;
    }

    public void setRequested(List<FriendEntity> requested) {
        this.requested = requested;
    }

    public List<FriendEntity> getPending() {
        return pending;
    }

    public void setPending(List<FriendEntity> pending) {
        this.pending = pending;
    }

    public List<FriendEntity> getBlocked() {
        return blocked;
    }

    public void setBlocked(List<FriendEntity> blocked) {
        this.blocked = blocked;
    }

    public List<FriendEntity> getAccepted() {
        return accepted;
    }

    public void setAccepted(List<FriendEntity> accepted) {
        this.accepted = accepted;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstSurname='" + firstSurname + '\'' +
                ", lastSurname='" + lastSurname + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", joinDate=" + joinDate +
                ", requested=" + requested +
                ", pending=" + pending +
                ", blocked=" + blocked +
                ", accepted=" + accepted +
                ", status='" + status + '\'' +
                '}';
    }
}
