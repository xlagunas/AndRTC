package cat.xlagunas.andrtc.data.net.params;

/**
 * Created by xlagunas on 29/03/16.
 */
public class UpdateParams {

    private String userId;
    private String oldStatus;
    private String newStatus;

    public UpdateParams(String userId, String oldStatus, String newStatus) {
        this.userId = userId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }
}
