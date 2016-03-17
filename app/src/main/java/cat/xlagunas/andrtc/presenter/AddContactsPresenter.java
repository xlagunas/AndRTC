package cat.xlagunas.andrtc.presenter;

import javax.inject.Inject;

import cat.xlagunas.andrtc.view.SearchListView;
import rx.Observer;
import rx.Subscriber;
import xlagunas.cat.andrtc.domain.Friend;
import xlagunas.cat.andrtc.domain.interactor.RequestNewFriendshipUseCase;
import xlagunas.cat.andrtc.domain.interactor.SearchUserUseCase;

/**
 * Created by xlagunas on 15/03/16.
 */
public class AddContactsPresenter implements Presenter {

    private final SearchUserUseCase searchUserUseCase;
    private final RequestNewFriendshipUseCase newFriendshipUseCase;
    private SearchListView view;

    @Inject
    public AddContactsPresenter(SearchUserUseCase userUseCase, RequestNewFriendshipUseCase newFriendshipUseCase) {
        this.searchUserUseCase = userUseCase;
        this.newFriendshipUseCase = newFriendshipUseCase;
    }

    public SearchListView getView() {
        return view;
    }

    public void setView(SearchListView view) {
        this.view = view;
    }

    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {
        view = null;
        searchUserUseCase.unsubscribe();
        newFriendshipUseCase.unsubscribe();

    }

    public void search(String keyword){
        searchUserUseCase.setFilter(keyword);
        searchUserUseCase.execute(new Subscriber<Friend>() {

            @Override
            public void onCompleted() {
                view.showList();
            }

            @Override
            public void onStart() {
                view.clearAdapter();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Friend friend) {
                view.addFriendToList(friend);
            }


        });
    }


    public void requestFriendship(Friend friend) {
        newFriendshipUseCase.setNewContactId(friend.getId());
        newFriendshipUseCase.execute(new Observer() {
            @Override
            public void onCompleted() {
                view.showConfirmation();
            }

            @Override
            public void onError(Throwable e) {
                view.showConfirmationError(e);
            }

            @Override
            public void onNext(Object o) {

            }
        });
    }
}
