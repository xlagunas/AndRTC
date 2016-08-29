package cat.xlagunas.andrtc.presenter;

import javax.inject.Inject;

import xlagunas.cat.andrtc.domain.DefaultSubscriber;
import xlagunas.cat.andrtc.domain.Friend;
import xlagunas.cat.andrtc.domain.interactor.CallRequestUseCase;
import xlagunas.cat.andrtc.domain.interactor.CancelCallRequestUseCase;
import xlagunas.cat.andrtc.domain.interactor.SearchFriendUseCase;

/**
 * Created by xlagunas on 25/7/16.
 */
public class CallerRequestPresenter extends CallRequestPresenter {

    private final CallRequestUseCase callRequestUseCase;
    private final CancelCallRequestUseCase cancelCallRequestUseCase;
    private final SearchFriendUseCase searchFriendUseCase;

    @Inject
    public CallerRequestPresenter(SearchFriendUseCase searchFriendUseCase, CallRequestUseCase callRequestUseCase, CancelCallRequestUseCase cancelCallRequestUseCase) {
        this.callRequestUseCase = callRequestUseCase;
        this.cancelCallRequestUseCase = cancelCallRequestUseCase;
        this.searchFriendUseCase = searchFriendUseCase;
    }

    @Override
    public void init(boolean isCaller) {
        super.init(isCaller);
        searchFriendUseCase.setId(friendId);
        searchFriendUseCase.execute(new DefaultSubscriber<Friend>() {
            @Override
            public void onNext(Friend friend) {
                super.onNext(friend);
                view.updateUserData(friend);
            }
        });
    }

    @Override
    public void resume() {

        callRequestUseCase.setFriendId(friendId);
        callRequestUseCase.execute(new DefaultSubscriber<Void>() {
            @Override
            public void onCompleted() {
                startTimer();
            }

            @Override
            public void onError(Throwable e) {
                view.setOnError(e);
            }
        });
    }

    @Override
    public void cancel() {
        timer.cancel();
        cancelCallRequestUseCase.setRoomId(friendId);
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

    @Override
    public void destroy() {
        searchFriendUseCase.unsubscribe();
        callRequestUseCase.unsubscribe();
        cancelCallRequestUseCase.unsubscribe();
        this.view = null;
    }
}
