package xlagunas.cat.andrtc.domain.interactor;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;
import xlagunas.cat.andrtc.domain.Friend;
import xlagunas.cat.andrtc.domain.User;
import xlagunas.cat.andrtc.domain.executor.PostExecutionThread;
import xlagunas.cat.andrtc.domain.repository.UserRepository;

/**
 * Created by xlagunas on 1/8/16.
 */
public class SearchFriendUseCase extends UseCase {

    private final UserRepository repository;
    private final User user;

    private String id;

    public void setId(String id) {
        this.id = id;
    }

    @Inject
    SearchFriendUseCase(User user, UserRepository repository, PostExecutionThread postExecutionThread) {
        super(postExecutionThread);
        this.repository = repository;
        this.user = user;
    }

    @Override
    protected Observable<Friend> buildUseCaseObservable() {
        return repository.listContacts(user).filter(new Func1<Friend, Boolean>() {
            @Override
            public Boolean call(Friend friend) {
                return friend.getId().equals(id);
            }
        });
    }
}
