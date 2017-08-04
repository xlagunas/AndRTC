package cat.xlagunas.andrtc.presenter;

import javax.inject.Inject;

import cat.xlagunas.andrtc.di.ActivityScope;
import cat.xlagunas.andrtc.view.LoginDataView;
import cat.xlagunas.andrtc.domain.DefaultSubscriber;
import cat.xlagunas.andrtc.domain.User;
import cat.xlagunas.andrtc.domain.interactor.GoogleLoginUseCase;
import cat.xlagunas.andrtc.domain.interactor.LoginUseCase;

/**
 * Created by xlagunas on 1/03/16.
 */
@ActivityScope
public class LoginPresenter implements Presenter {

    private final LoginUseCase loginUseCase;
    private final GoogleLoginUseCase googleLoginUseCase;
    private LoginDataView view;

    @Inject
    LoginPresenter(LoginUseCase loginUseCase, GoogleLoginUseCase googleLoginUseCase) {
        this.loginUseCase = loginUseCase;
        this.googleLoginUseCase = googleLoginUseCase;
    }

    public void setView(LoginDataView view) {
        this.view = view;
    }

    public void initialize() {
        view.hideLoading();
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
        googleLoginUseCase.unsubscribe();
        this.view = null;
    }

    public void doLogin(String username, String password) {
        loginUseCase.setCredentials(username, password);
        loginUseCase.execute(getLoginSubscriber());

        view.showLoading();
    }

    public void doGoogleLogin() {
        googleLoginUseCase.execute(getLoginSubscriber());
    }

    private DefaultSubscriber<User> getLoginSubscriber() {
        return new DefaultSubscriber<User>() {

            @Override
            public void onError(Throwable e) {
                view.hideLoading();
                view.showError("Error loading");
                e.printStackTrace();
            }

            @Override
            public void onNext(User user) {
                view.hideLoading();
                view.onUserRecovered(user);
            }
        };
    }


}
