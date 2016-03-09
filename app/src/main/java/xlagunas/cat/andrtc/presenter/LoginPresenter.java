package xlagunas.cat.andrtc.presenter;

import android.widget.Toast;

import javax.inject.Inject;

import rx.Observer;
import xlagunas.cat.andrtc.di.PerActivity;
import xlagunas.cat.andrtc.view.LoadDataView;
import xlagunas.cat.domain.DefaultSubscriber;
import xlagunas.cat.domain.User;
import xlagunas.cat.domain.interactor.LoginUseCase;

/**
 * Created by xlagunas on 1/03/16.
 */
@PerActivity
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
