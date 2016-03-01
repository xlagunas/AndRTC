package xlagunas.cat.andrtc.presenter;

import javax.inject.Inject;
import javax.inject.Singleton;

import xlagunas.cat.domain.interactor.UseCase;

/**
 * Created by xlagunas on 1/03/16.
 */
@Singleton
public class LoginPresenter implements Presenter {

    private UseCase loginUseCase;

    @Inject
    LoginPresenter(UseCase loginUseCase){
        this.loginUseCase = loginUseCase;
    }

    @Override
    public void resume() {}

    @Override
    public void pause() {}

    @Override
    public void destroy() {
        loginUseCase.unsubscribe();

    }
}
