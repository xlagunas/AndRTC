package xlagunas.cat.andrtc.domain.interactor;

import javax.inject.Inject;

import rx.Observable;
import xlagunas.cat.andrtc.domain.Friend;
import xlagunas.cat.andrtc.domain.executor.PostExecutionThread;
import xlagunas.cat.andrtc.domain.repository.UserRepository;

/**
 * Created by xlagunas on 19/03/16.
 */
public class ContactsUseCase extends UseCase {

    private final UserRepository userRepository;

    @Inject
    public ContactsUseCase(PostExecutionThread postExecutionThread, UserRepository userRepository) {
        super(postExecutionThread);
        this.userRepository = userRepository;
    }


        @Override
    protected Observable<Friend> buildUseCaseObservable() {
        return userRepository.listContacts();
    }
}
