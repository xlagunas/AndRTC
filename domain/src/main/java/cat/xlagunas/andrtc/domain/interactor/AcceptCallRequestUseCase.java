package cat.xlagunas.andrtc.domain.interactor;

import javax.inject.Inject;

import rx.Observable;
import cat.xlagunas.andrtc.domain.User;
import cat.xlagunas.andrtc.domain.executor.PostExecutionThread;
import cat.xlagunas.andrtc.domain.repository.UserRepository;

/**
 * Created by xlagunas on 25/7/16.
 */
public class AcceptCallRequestUseCase extends UseCase {

    private final User user;
    private final UserRepository userRepository;


    private String friendId;

    public void setRoomId(String id){
        this.friendId = id;
    }

    @Inject
    public AcceptCallRequestUseCase(User user, UserRepository userRepository, PostExecutionThread postExecutionThread) {
        super(postExecutionThread);
        this.user = user;
        this.userRepository = userRepository;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return userRepository.acceptCallUser(user, friendId);
    }
}
