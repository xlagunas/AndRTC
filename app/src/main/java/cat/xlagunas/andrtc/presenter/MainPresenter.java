package cat.xlagunas.andrtc.presenter;


import javax.inject.Inject;

import cat.xlagunas.andrtc.ServiceFacade;
import cat.xlagunas.andrtc.data.cache.UserCache;
import cat.xlagunas.andrtc.view.LoadDataView;
import cat.xlagunas.andrtc.view.LogOutDataView;
import rx.Observer;
import xlagunas.cat.andrtc.domain.DefaultSubscriber;
import xlagunas.cat.andrtc.domain.interactor.LogOutUseCase;

/**
 * Created by xlagunas on 12/03/16.
 */
public class MainPresenter implements Presenter {

    private final ServiceFacade serviceFacade;
    private final LogOutUseCase logOutUseCase;
    private LogOutDataView view;

    @Inject
    public MainPresenter(ServiceFacade service, LogOutUseCase useCase){
        this.serviceFacade = service;
        this.logOutUseCase = useCase;
    }

    public void initPresenter(){
        serviceFacade.startService();
    }

    public void setView(LogOutDataView view){
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
        logOutUseCase.execute(new DefaultSubscriber(){
            @Override
            public void onCompleted() {
                view.onLogOut();
            }
        });
    }
}
