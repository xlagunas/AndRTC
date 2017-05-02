package cat.xlagunas.andrtc.presenter;


import javax.inject.Inject;

import cat.xlagunas.andrtc.view.LogOutDataView;
import cat.xlagunas.andrtc.domain.DefaultSubscriber;
import cat.xlagunas.andrtc.domain.interactor.LogOutUseCase;

/**
 * Created by xlagunas on 12/03/16.
 */
public class MainPresenter implements Presenter {

    private final LogOutUseCase logOutUseCase;

    private LogOutDataView view;

    @Inject
    public MainPresenter(LogOutUseCase useCase) {
        this.logOutUseCase = useCase;
    }

    public void initPresenter() {

    }

    public void setView(LogOutDataView view) {
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
    }

    public void logout() {
        logOutUseCase.execute(new DefaultSubscriber() {
            @Override
            public void onCompleted() {
                view.onLogOut();
            }
        });
    }
}
