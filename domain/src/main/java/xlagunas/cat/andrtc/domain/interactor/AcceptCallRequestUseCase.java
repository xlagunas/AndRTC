package xlagunas.cat.andrtc.domain.interactor;

import javax.inject.Inject;

import rx.Observable;
import xlagunas.cat.andrtc.domain.User;
import xlagunas.cat.andrtc.domain.executor.PostExecutionThread;
import xlagunas.cat.andrtc.domain.repository.UserRepository;

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
