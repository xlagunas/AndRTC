package xlagunas.cat.data;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by xlagunas on 26/02/16.
 */

public class UserEntity {

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
    @SerializedName("thumbnail")
    private DateTime joinDate;
    @SerializedName("requested")
    private List<UserEntity> requested;
    @SerializedName("pending")
    private List<UserEntity> pending;
    @SerializedName("blocked")
    private List<UserEntity> blocked;
    @SerializedName("accepted")
    private List<UserEntity> accepted;
    @SerializedName("status")
    private String status;

    public UserEntity() {
        super();
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

    public List<UserEntity> getRequested() {
        return requested;
    }

    public void setRequested(List<UserEntity> requested) {
        this.requested = requested;
    }

    public List<UserEntity> getPending() {
        return pending;
    }

    public void setPending(List<UserEntity> pending) {
        this.pending = pending;
    }

    public List<UserEntity> getBlocked() {
        return blocked;
    }

    public void setBlocked(List<UserEntity> blocked) {
        this.blocked = blocked;
    }

    public List<UserEntity> getAccepted() {
        return accepted;
    }

    public void setAccepted(List<UserEntity> accepted) {
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
}
