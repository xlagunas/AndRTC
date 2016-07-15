package cat.xlagunas.andrtc.presenter;

import android.util.Log;

import javax.inject.Inject;

import rx.Observer;
import cat.xlagunas.andrtc.view.RegisterDataView;
import xlagunas.cat.andrtc.domain.User;
import xlagunas.cat.andrtc.domain.interactor.RegisterUseCase;

/**
 * Created by xlagunas on 9/03/16.
 */
public class RegisterPresenter implements Presenter {

    private final static String TAG = RegisterPresenter.class.getSimpleName();

    private final RegisterUseCase registerUseCase;
    private final User user;
    private RegisterDataView view;


    @Inject
    public RegisterPresenter(User user, RegisterUseCase useCase){
        this.registerUseCase = useCase;
        this.user = user;
    }

    public void setView(RegisterDataView view){
        this.view = view;
    }


    @Override
    public void resume() {}

    @Override
    public void pause() {}

    public void registerUser(){
        registerUseCase.setUser(user);
        registerUseCase.execute(new Observer<User>() {
            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "Error registering user:",e);
            }

            @Override
            public void onNext(User user) {
                view.onUserRegistered(user);
            }
        });
    }

    @Override
    public void destroy() {
        registerUseCase.unsubscribe();
        this.view = null;

    }
}
