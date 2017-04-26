package xlagunas.cat.andrtc.domain.interactor;

import javax.inject.Inject;

import rx.Observable;
import xlagunas.cat.andrtc.domain.executor.PostExecutionThread;
import xlagunas.cat.andrtc.domain.repository.UserRepository;

/**
 * Created by xlagunas on 26/02/16.
 */

public class LogOutUseCase extends UseCase {

    private final UserRepository repository;

    @Inject
    public LogOutUseCase(PostExecutionThread postExecutionThread, UserRepository repository) {
        super(postExecutionThread);
        this.repository = repository;
    }


    @Override
    protected Observable buildUseCaseObservable() {
        return repository.logoutUser();
    }
}
