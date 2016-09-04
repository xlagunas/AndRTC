package xlagunas.cat.andrtc.domain.interactor;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action1;
import xlagunas.cat.andrtc.domain.User;
import xlagunas.cat.andrtc.domain.executor.PostExecutionThread;
import xlagunas.cat.andrtc.domain.repository.SocialRepository;
import xlagunas.cat.andrtc.domain.repository.UserRepository;

/**
 * Created by xlagunas on 26/02/16.
 */

public class LogOutUseCase extends UseCase {

    private final UserRepository repository;
    private final SocialRepository socialRepository;

    @Inject
    public LogOutUseCase(PostExecutionThread postExecutionThread, UserRepository repository, SocialRepository socialRepository) {
        super(postExecutionThread);
        this.repository = repository;
        this.socialRepository = socialRepository;
    }


    @Override
    protected Observable buildUseCaseObservable() {
        return repository.logoutUser()
                .doOnCompleted(socialRepository.logoutFacebook());
    }
}
