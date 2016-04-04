package cat.xlagunas.andrtc.presenter;


import javax.inject.Inject;

import cat.xlagunas.andrtc.ServiceFacade;
import cat.xlagunas.andrtc.data.cache.UserCache;
import cat.xlagunas.andrtc.view.LoadDataView;

/**
 * Created by xlagunas on 12/03/16.
 */
public class MainPresenter implements Presenter {

    private final ServiceFacade serviceFacade;
    private final UserCache userCache;
    private LoadDataView view;

    @Inject
    public MainPresenter(ServiceFacade service, UserCache cache){
        this.serviceFacade = service;
        this.userCache = cache;
    }

    public void initPresenter(){
        if (!userCache.isGCMRegistered()){
            serviceFacade.startService();
        }
    }

    public void setView(LoadDataView view){
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
        userCache.removeCache();
    }
}
