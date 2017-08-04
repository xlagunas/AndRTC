package cat.xlagunas.andrtc.presenter;

import java.util.List;

import javax.inject.Inject;

import cat.xlagunas.andrtc.view.ListDataView;
import cat.xlagunas.andrtc.domain.DefaultSubscriber;
import cat.xlagunas.andrtc.domain.Friend;
import cat.xlagunas.andrtc.domain.interactor.ContactsUseCase;

/**
 * Created by xlagunas on 19/03/16.
 */
public class ShowContactsPresenter implements Presenter {

    ContactsUseCase useCase;
    ListDataView view;

    @Inject
    public ShowContactsPresenter(ContactsUseCase useCase) {
        this.useCase = useCase;
    }

    public void setView(ListDataView view) {
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
        this.view = null;
        useCase.unsubscribe();
    }

    public void init() {
        useCase.execute(new DefaultSubscriber<List<Friend>>() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                e.printStackTrace();
            }

            @Override
            public void onNext(List<Friend> friends) {
                super.onNext(friends);
                view.addFriends(friends);
            }
        });
    }

    public void updateContacts() {
        init();
    }
}
