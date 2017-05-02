package cat.xlagunas.andrtc.domain.interactor;


import javax.inject.Inject;

import rx.Observable;
import cat.xlagunas.andrtc.domain.User;
import cat.xlagunas.andrtc.domain.executor.PostExecutionThread;
import cat.xlagunas.andrtc.domain.repository.UserRepository;

/**
 * Created by xlagunas on 9/03/16.
 */
public class RegisterUseCase extends UseCase {
    private final UserRepository userRepository;
    private final PostExecutionThread postExecutionThread;
    private final User user;

    @Inject
    public RegisterUseCase(User user, PostExecutionThread postExecutionThread, UserRepository userRepository) {
        super(postExecutionThread);
        this.userRepository = userRepository;
        this.postExecutionThread = postExecutionThread;
        this.user = user;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return userRepository.registerUser(user);
    }
}
