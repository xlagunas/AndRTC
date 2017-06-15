package cat.xlagunas.andrtc.domain.interactor;

import javax.inject.Inject;

import rx.Observable;
import cat.xlagunas.andrtc.domain.User;
import cat.xlagunas.andrtc.domain.executor.PostExecutionThread;
import cat.xlagunas.andrtc.domain.repository.UserRepository;

/**
 * Created by xlagunas on 17/3/16.
 */
public class RequestNewFriendshipUseCase extends UseCase {

    private final UserRepository userRepository;
    private final User user;

    private String newContactId;

    @Inject
    RequestNewFriendshipUseCase(PostExecutionThread postExecutionThread, User user, UserRepository repository) {
        super(postExecutionThread);
        this.userRepository = repository;
        this.user = user;
    }

    public void setNewContactId(String newContactId) {
        this.newContactId = newContactId;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return userRepository.requestNewFriendship(user, newContactId);
    }
}
