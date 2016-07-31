package cat.xlagunas.andrtc.view;

/**
 * Created by xlagunas on 25/7/16.
 */
public interface CallRequestDataView {
    void hideAcceptCallButton();
    void startConference(String confId);
    void cancelConference();

    void setOnError(Throwable e);
}
