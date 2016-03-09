package xlagunas.cat.domain.interactor;

import javax.inject.Inject;

import rx.Observable;
import xlagunas.cat.domain.User;
import xlagunas.cat.domain.executor.PostExecutionThread;
import xlagunas.cat.domain.repository.UserRepository;

/**
 * Created by xlagunas on 9/03/16.
 */
public class RegisterUseCase extends UseCase {
    private final UserRepository userRepository;
    private final PostExecutionThread postExecutionThread;

    private User user;

    @Inject
    public RegisterUseCase(PostExecutionThread postExecutionThread, UserRepository userRepository) {
        super(postExecutionThread);
        this.userRepository = userRepository;
        this.postExecutionThread = postExecutionThread;
    }

    public void setUser(User user){
        this.user = user;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return userRepository.registerUser(user);
    }
}
