package xlagunas.cat.andrtc.domain.interactor;

import javax.inject.Inject;

import rx.Observable;
import xlagunas.cat.andrtc.domain.User;
import xlagunas.cat.andrtc.domain.executor.PostExecutionThread;
import xlagunas.cat.andrtc.domain.repository.UserRepository;

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
