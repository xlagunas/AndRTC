package cat.xlagunas.andrtc.domain.interactor;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import cat.xlagunas.andrtc.domain.Friend;
import cat.xlagunas.andrtc.domain.User;
import cat.xlagunas.andrtc.domain.executor.PostExecutionThread;
import cat.xlagunas.andrtc.domain.repository.UserRepository;

/**
 * Created by xlagunas on 19/03/16.
 */
public class ContactsUseCase extends UseCase {
    private final User user;
    private final UserRepository userRepository;

    @Inject
    public ContactsUseCase(PostExecutionThread postExecutionThread, User user, UserRepository userRepository) {
        super(postExecutionThread);
        this.user = user;
        this.userRepository = userRepository;
    }


        @Override
    protected Observable<List<Friend>> buildUseCaseObservable() {
        return userRepository.listContacts(user).toSortedList();
    }
}
