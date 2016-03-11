package xlagunas.cat.andrtc.domain.interactor;

import javax.inject.Inject;

import rx.Observable;
import xlagunas.cat.andrtc.domain.User;
import xlagunas.cat.andrtc.domain.executor.PostExecutionThread;
import xlagunas.cat.andrtc.domain.repository.UserRepository;

/**
 * Created by xlagunas on 11/03/16.
 */

public class RegisterGCMTokenUseCase extends UseCase{

    private final PostExecutionThread postExecutionThread;
    private final User user;
    private final UserRepository userRepository;

    private String gcmToken;

    public void setToken(String token){
        this.gcmToken = token;
    }

    @Inject
    RegisterGCMTokenUseCase(PostExecutionThread postExecutionThread, User user, UserRepository userRepository) {
        super(postExecutionThread);
        this.postExecutionThread = postExecutionThread;
        this.user = user;
        this.userRepository = userRepository;
    }
    @Override
    protected Observable buildUseCaseObservable() {
        return userRepository.registerGCMToken(user, gcmToken);
    }
}
