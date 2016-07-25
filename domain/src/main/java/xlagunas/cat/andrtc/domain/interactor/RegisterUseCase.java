package xlagunas.cat.andrtc.domain.interactor;


import javax.inject.Inject;

import rx.Observable;
import xlagunas.cat.andrtc.domain.executor.PostExecutionThread;
import xlagunas.cat.andrtc.domain.repository.UserRepository;

/**
 * Created by xlagunas on 9/03/16.
 */
public class RegisterUseCase extends UseCase {
    private final UserRepository userRepository;
    private final PostExecutionThread postExecutionThread;

    private xlagunas.cat.andrtc.domain.User user;

    @Inject
    public RegisterUseCase(PostExecutionThread postExecutionThread, UserRepository userRepository) {
        super(postExecutionThread);
        this.userRepository = userRepository;
        this.postExecutionThread = postExecutionThread;
    }

    public void setUser(xlagunas.cat.andrtc.domain.User user){
        this.user = user;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return userRepository.registerUser(user);
    }
}
