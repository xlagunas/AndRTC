package cat.xlagunas.andrtc.presenter;

import java.util.Timer;
import java.util.TimerTask;

import cat.xlagunas.andrtc.view.CallRequestDataView;

/**
 * Created by xlagunas on 25/7/16.
 */
public abstract class CallRequestPresenter implements Presenter {

    private boolean isCaller;

    protected String friendId;
    protected Timer timer;
    protected CallRequestDataView view;

    private final static int MAX_DIALING_TIME = 10000;

    public void init(boolean isCaller){
        this.isCaller = isCaller;
        if (isCaller){
            view.hideAcceptCallButton();
        }
    }

    public void setFriendId(String id){
        this.friendId = id;
    }


    public void setView(CallRequestDataView view){
        this.view = view;
    }

    protected void startTimer(){
        timer = new Timer("Cancel timer");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                view.cancelConference();
            }

        }, MAX_DIALING_TIME);
    }

    public void goToConference(String conferenceId){
        timer.cancel();
        view.startConference(conferenceId);
    }

    public void cancel(){
        timer.cancel();
        view.cancelConference();
    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {

    }
}
