package cat.xlagunas.andrtc.presenter;

import javax.inject.Inject;

import rx.Observer;
import xlagunas.cat.andrtc.domain.DefaultSubscriber;
import xlagunas.cat.andrtc.domain.Friend;
import xlagunas.cat.andrtc.domain.User;
import xlagunas.cat.andrtc.domain.interactor.CallRequestUseCase;
import xlagunas.cat.andrtc.domain.interactor.CancelCallRequestUseCase;
import xlagunas.cat.andrtc.domain.interactor.SearchFriendUseCase;
import xlagunas.cat.andrtc.domain.interactor.SearchUserUseCase;

/**
 * Created by xlagunas on 25/7/16.
 */
public class CallerRequestPresenter extends CallRequestPresenter {

    private final User user;
    private final CallRequestUseCase callRequestUseCase;
    private final CancelCallRequestUseCase cancelCallRequestUseCase;
    private final SearchFriendUseCase searchFriendUseCase;

    @Inject
    public CallerRequestPresenter(User user, SearchFriendUseCase searchFriendUseCase, CallRequestUseCase callRequestUseCase, CancelCallRequestUseCase cancelCallRequestUseCase){
        this.user = user;
        this.callRequestUseCase = callRequestUseCase;
        this.cancelCallRequestUseCase = cancelCallRequestUseCase;
        this.searchFriendUseCase = searchFriendUseCase;
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

    @Override
    public void resume() {

//        callRequestUseCase.setFriendId(friendId);
//        callRequestUseCase.execute(new Observer() {
//            @Override
//            public void onCompleted() {
//                startTimer();
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                view.setOnError(e);
//            }
//
//            @Override
//            public void onNext(Object o) {
//                //LEFT BLANK ON PURPOSE
//            }
//        });
    }

    @Override
    public void cancel() {
        cancelCallRequestUseCase.setFriendId(friendId);
        cancelCallRequestUseCase.execute(new Observer() {
            @Override
            public void onCompleted() {
                CallerRequestPresenter.super.cancel();
            }

            @Override
            public void onError(Throwable e) {
                view.setOnError(e);
            }

            @Override
            public void onNext(Object o) {
//                LEFT BLANK ON PURPOSE
            }
        });
        super.cancel();
    }
}
