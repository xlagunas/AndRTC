package cat.xlagunas.andrtc.domain.interactor;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;
import cat.xlagunas.andrtc.domain.Friend;
import cat.xlagunas.andrtc.domain.User;
import cat.xlagunas.andrtc.domain.executor.PostExecutionThread;
import cat.xlagunas.andrtc.domain.repository.UserRepository;

/**
 * Created by xlagunas on 15/03/16.
 */
public class SearchUserUseCase extends UseCase {
    private final UserRepository repository;
    private final User user;

    private String filter;

    @Inject
    public SearchUserUseCase(PostExecutionThread postExecutionThread, UserRepository repository, User user) {
        super(postExecutionThread);
        this.repository = repository;
        this.user = user;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    @Override
    protected Observable<List<Friend>> buildUseCaseObservable() {

        Observable<Friend> observable = filter.isEmpty()
                ? repository.listRequestedContacts()
                : repository.listAllContacts().filter(applyFilter())
                .mergeWith(repository.searchUsers(user, filter));

        return  observable
                .distinct().toSortedList();
    }

    private Func1<Friend, Boolean> applyFilter() {
        return new Func1<Friend, Boolean>() {
            @Override
            public Boolean call(Friend friend) {
                return friend.contactMatchesSearchKeywords(filter);
            }
        };
    }

}
