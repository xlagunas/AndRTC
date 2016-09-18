package cat.xlagunas.andrtc.presenter;

import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import cat.xlagunas.andrtc.view.SearchListView;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import xlagunas.cat.andrtc.domain.Friend;
import xlagunas.cat.andrtc.domain.interactor.RequestNewFriendshipUseCase;
import xlagunas.cat.andrtc.domain.interactor.SearchUserUseCase;
import xlagunas.cat.andrtc.domain.repository.UserRepository;

/**
 * Created by xlagunas on 15/03/16.
 */
public class AddContactsPresenter implements Presenter {

    private static final String TAG = AddContactsPresenter.class.getSimpleName();
    private final SearchUserUseCase searchUserUseCase;
    private final RequestNewFriendshipUseCase newFriendshipUseCase;

    private Subscription invalidateCacheSubscription;

    private SearchListView view;

    private final UserRepository userRepository;

    @Inject
    public AddContactsPresenter(SearchUserUseCase userUseCase,
                                RequestNewFriendshipUseCase newFriendshipUseCase,
                                UserRepository userRepository) {
        this.searchUserUseCase = userUseCase;
        this.newFriendshipUseCase = newFriendshipUseCase;

        this.userRepository = userRepository;
    }

    public SearchListView getView() {
        return view;
    }

    public void setView(SearchListView view) {
        this.view = view;
    }

    @Override
    public void resume() {
        search("");
        invalidateCacheSubscription = userRepository.observeDataInvalidation()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(text -> {
                    Log.d(TAG, "Received msg:" +text);
                    updateContacts();
                });
    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {
        view = null;
        searchUserUseCase.unsubscribe();
        newFriendshipUseCase.unsubscribe();
        invalidateCacheSubscription.unsubscribe();

    }

    public void search(String keyword) {
        searchUserUseCase.setFilter(keyword);
        executeSearch();
    }

    private void executeSearch() {
        searchUserUseCase.execute(new Subscriber<List<Friend>>() {

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
            public void onNext(List<Friend> friends) {
                view.addFriends(friends);
            }

        });
    }

    public void updateContacts() {
        executeSearch();
    }

}
