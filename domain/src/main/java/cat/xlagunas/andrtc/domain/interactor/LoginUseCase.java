package cat.xlagunas.andrtc.domain.interactor;

import javax.inject.Inject;

import rx.Observable;
import cat.xlagunas.andrtc.domain.repository.UserRepository;

/**
 * Created by xlagunas on 26/02/16.
 */

public class LoginUseCase extends UseCase {

    private final UserRepository repository;
    private String username;
    private String password;

    @Inject
    public LoginUseCase(cat.xlagunas.andrtc.domain.executor.PostExecutionThread postExecutionThread, UserRepository repository) {
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
