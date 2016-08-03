package xlagunas.cat.andrtc.domain.interactor;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import xlagunas.cat.andrtc.domain.Friend;
import xlagunas.cat.andrtc.domain.User;
import xlagunas.cat.andrtc.domain.executor.PostExecutionThread;
import xlagunas.cat.andrtc.domain.repository.UserRepository;

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
                ? Observable.<Friend>empty()
                : repository.searchUsers(user, filter);

        return  observable
                .mergeWith(repository.listRequestedContacts()).toSortedList();
    }
}
