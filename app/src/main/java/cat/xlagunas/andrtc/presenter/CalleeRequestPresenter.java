package cat.xlagunas.andrtc.presenter;

import javax.inject.Inject;

import xlagunas.cat.andrtc.domain.DefaultSubscriber;
import xlagunas.cat.andrtc.domain.Friend;
import xlagunas.cat.andrtc.domain.User;
import xlagunas.cat.andrtc.domain.interactor.AcceptCallRequestUseCase;
import xlagunas.cat.andrtc.domain.interactor.CancelCallRequestUseCase;
import xlagunas.cat.andrtc.domain.interactor.SearchFriendUseCase;

/**
 * Created by xlagunas on 25/7/16.
 */
public class CalleeRequestPresenter extends CallRequestPresenter {

    private final User user;
    private final CancelCallRequestUseCase cancelCallRequestUseCase;
    private final AcceptCallRequestUseCase acceptCallRequestUseCase;
    private final SearchFriendUseCase searchFriendUseCase;

    @Inject
    public CalleeRequestPresenter(User user, SearchFriendUseCase searchFriendUseCase, AcceptCallRequestUseCase acceptCallRequestUseCase, CancelCallRequestUseCase cancelCallRequestUseCase){
        this.user = user;
        this.acceptCallRequestUseCase = acceptCallRequestUseCase;
        this.cancelCallRequestUseCase = cancelCallRequestUseCase;
        this.searchFriendUseCase = searchFriendUseCase;
    }

    @Override
    public void resume() {
        startTimer();
    }

    @Override
    public void destroy() {

    }

    @Override
    public void init(boolean isCaller) {
        super.init(isCaller);
        searchFriendUseCase.setId(friendId);
        searchFriendUseCase.execute(new DefaultSubscriber<Friend>(){
            @Override
            public void onNext(Friend friend) {
                super.onNext(friend);
                view.updateUserData(friend);
            }
        });
    }



    public void accept(){
        acceptCallRequestUseCase.setRoomId(callId);
        acceptCallRequestUseCase.execute(new DefaultSubscriber<Void>(){
            @Override
            public void onCompleted() {
                super.onCompleted();
                CalleeRequestPresenter.super.goToConference();
            }
        });
    }
    @Override
    public void cancel() {
        timer.cancel();
        cancelCallRequestUseCase.setRoomId(callId);
        cancelCallRequestUseCase.execute(new DefaultSubscriber<Void>() {
            @Override
            public void onCompleted() {
                view.cancelConference();
            }

            @Override
            public void onError(Throwable e) {
                view.setOnError(e);
            }

        });
    }
}
