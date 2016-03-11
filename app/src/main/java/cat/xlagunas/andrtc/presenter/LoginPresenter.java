package cat.xlagunas.andrtc.presenter;

import android.widget.Toast;

import javax.inject.Inject;

import cat.xlagunas.andrtc.di.ActivityScope;
import cat.xlagunas.andrtc.view.LoadDataView;
import rx.Observer;
import xlagunas.cat.andrtc.domain.User;
import xlagunas.cat.andrtc.domain.interactor.LoginUseCase;

/**
 * Created by xlagunas on 1/03/16.
 */
@ActivityScope
public class LoginPresenter implements Presenter {

    private final LoginUseCase loginUseCase;
    private LoadDataView view;

    @Inject
    LoginPresenter(LoginUseCase loginUseCase){
        this.loginUseCase = loginUseCase;
    }

    public void setView(LoadDataView view){
        this.view = view;
    }

    @Override
    public void resume() {}

    @Override
    public void pause() {}

    @Override
    public void destroy() {
        loginUseCase.unsubscribe();
        this.view = null;
    }

    public void doLogin(String username, String password){
        loginUseCase.setCredentials(username, password);
        loginUseCase.execute(new Observer<User>() {
            @Override
            public void onCompleted() {
                Toast.makeText(view.context(), "COMPLETED", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(Throwable e) {
                view.hideLoading();
                view.showError("Error loading");
            }

            @Override
            public void onNext(User user) {
                view.hideLoading();
            }
        });

        view.showLoading();
    }

    public void initialize() {
        view.hideLoading();
    }

}
