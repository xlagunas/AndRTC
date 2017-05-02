package cat.xlagunas.andrtc.domain.interactor;


import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.schedulers.Schedulers;
import cat.xlagunas.andrtc.domain.User;
import cat.xlagunas.andrtc.domain.executor.PostExecutionThread;
import cat.xlagunas.andrtc.domain.repository.FileRepository;
import cat.xlagunas.andrtc.domain.repository.UserRepository;

/**
 * Created by xlagunas on 11/03/16.
 */

public class RegisterGCMTokenUseCase extends UseCase {

    private final PostExecutionThread postExecutionThread;
    private final User user;
    private final UserRepository userRepository;
    private final FileRepository fileRepository;

    @Inject
    RegisterGCMTokenUseCase(PostExecutionThread postExecutionThread, User user, UserRepository userRepository, FileRepository fileRepository) {
        super(postExecutionThread);
        this.postExecutionThread = postExecutionThread;
        this.user = user;
        this.userRepository = userRepository;
        this.fileRepository = fileRepository;
    }

    @Override
    protected Observable<Boolean> buildUseCaseObservable() {
        return fileRepository.getStoredToken()
                .flatMap(token -> userRepository.registerGCMToken(user, token));
    }

    @Override
    public void execute(Observer useCaseSubscriber) {
        this.buildUseCaseObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.immediate())
                .subscribe(useCaseSubscriber);
    }
}
