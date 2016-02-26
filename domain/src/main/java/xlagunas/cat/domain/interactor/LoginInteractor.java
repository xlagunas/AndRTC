package xlagunas.cat.domain.interactor;

import javax.inject.Inject;

import rx.Observable;
import xlagunas.cat.domain.executor.PostExecutionThread;
import xlagunas.cat.domain.repository.UserRepository;

/**
 * Created by xlagunas on 26/02/16.
 */
public class LoginInteractor extends UseCase {

    private UserRepository repository;
    private String username;
    private String password;

    @Inject
    public LoginInteractor(PostExecutionThread postExecutionThread, UserRepository repository) {
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
