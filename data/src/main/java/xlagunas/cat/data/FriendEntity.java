package xlagunas.cat.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xlagunas on 29/02/16.
 */
public class FriendEntity {
    @SerializedName("username")
    private String username;
    @SerializedName("name")
    private String name;
    @SerializedName("firstSurname")
    private String surname;
    @SerializedName("lastSurname")
    private String lastSurname;
    @SerializedName("thumbnail")
    private String thumbnail;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getLastSurname() {
        return lastSurname;
    }

    public void setLastSurname(String lastSurname) {
        this.lastSurname = lastSurname;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }


}
