package cat.xlagunas.andrtc.presenter;

import javax.inject.Inject;

import cat.xlagunas.andrtc.view.SearchListView;
import rx.Observer;
import rx.Subscriber;
import xlagunas.cat.andrtc.domain.Friend;
import xlagunas.cat.andrtc.domain.interactor.SearchUserUseCase;

/**
 * Created by xlagunas on 15/03/16.
 */
public class AddContactsPresenter implements Presenter {

    private final SearchUserUseCase searchUserUseCase;
    private SearchListView view;

    @Inject
    public AddContactsPresenter(SearchUserUseCase userUseCase) {
        this.searchUserUseCase = userUseCase;
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

            }

            @Override
            public void onNext(Friend friend) {
                view.addFriendToList(friend);
            }


        });
    }


}
