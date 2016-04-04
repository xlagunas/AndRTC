package xlagunas.cat.andrtc.domain.interactor;

import javax.inject.Inject;

import rx.Observable;
import xlagunas.cat.andrtc.domain.User;
import xlagunas.cat.andrtc.domain.executor.PostExecutionThread;
import xlagunas.cat.andrtc.domain.repository.UserRepository;

/**
 * Created by xlagunas on 29/03/16.
 */
public class UpdateFriendshipUseCase extends UseCase {

    private final UserRepository repository;
    private final User user;

    private String contactId;
    private String previousState;
    private String nextState;

    @Inject
    UpdateFriendshipUseCase(PostExecutionThread postExecutionThread, User user, UserRepository repository) {
        super(postExecutionThread);
        this.repository = repository;
        this.user = user;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public void setPreviousState(String previousState) {
        this.previousState = previousState;
    }

    public void setNextState(String nextState) {
        this.nextState = nextState;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return repository.updateFriendship(user, contactId, previousState, nextState);
    }

}
