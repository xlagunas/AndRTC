package cat.xlagunas.andrtc.presenter;

import javax.inject.Inject;

import cat.xlagunas.andrtc.di.ActivityScope;
import cat.xlagunas.andrtc.view.LoginDataView;
import rx.Observer;
import xlagunas.cat.andrtc.domain.DefaultSubscriber;
import xlagunas.cat.andrtc.domain.User;
import xlagunas.cat.andrtc.domain.interactor.FacebookLoginUseCase;
import xlagunas.cat.andrtc.domain.interactor.LoginUseCase;

/**
 * Created by xlagunas on 1/03/16.
 */
@ActivityScope
public class LoginPresenter implements Presenter {

    private final LoginUseCase loginUseCase;
    private final FacebookLoginUseCase facebookLoginUseCase;
    private LoginDataView view;

    @Inject
    LoginPresenter(LoginUseCase loginUseCase, FacebookLoginUseCase facebookLoginUseCase) {
        this.loginUseCase = loginUseCase;
        this.facebookLoginUseCase = facebookLoginUseCase;
    }

    public void setView(LoginDataView view) {
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
        loginUseCase.unsubscribe();
        this.view = null;
    }

    public void doLogin(String username, String password) {
        loginUseCase.setCredentials(username, password);
        loginUseCase.execute(new Observer<User>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                view.hideLoading();
                view.showError("Error loading");
            }

            @Override
            public void onNext(User user) {
                view.hideLoading();
                view.onUserRecovered(user);
            }
        });

        view.showLoading();
    }

    public void initialize() {
        view.hideLoading();
    }

    public void doFacebookLogin() {
        facebookLoginUseCase.execute(new DefaultSubscriber<User>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                view.hideLoading();
                view.showError("Error loading");
            }

            @Override
            public void onNext(User user) {
                view.hideLoading();
                view.onUserRecovered(user);
//                view.onUserRecovered(user);
            }
        });
    }
}
