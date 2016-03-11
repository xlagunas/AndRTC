package xlagunas.cat.andrtc.domain.interactor;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by xlagunas on 26/02/16.
 */

public class LoginUseCase extends UseCase {

    private final xlagunas.cat.andrtc.domain.repository.UserRepository repository;
    private String username;
    private String password;

    @Inject
    public LoginUseCase(xlagunas.cat.andrtc.domain.executor.PostExecutionThread postExecutionThread, xlagunas.cat.andrtc.domain.repository.UserRepository repository) {
        super(postExecutionThread);
        this.repository = repository;
    }

    public void setCredentials(String username, String password){
        this.username = username;
        this.password = password;
    }


    @Override
    protected Observable buildUseCaseObservable() {
        return repository.login(username, password);
    }
}
