package xlagunas.cat.andrtc.domain.interactor;

import javax.inject.Inject;

import rx.Observable;
import xlagunas.cat.andrtc.domain.executor.PostExecutionThread;
import xlagunas.cat.andrtc.domain.repository.SocialRepository;

/**
 * Created by xlagunas on 20/8/16.
 */
public class FacebookLoginUseCase extends UseCase {
    private final SocialRepository socialRepository;

    @Inject
    public FacebookLoginUseCase(PostExecutionThread postExecutionThread, SocialRepository socialRepository) {
        super(postExecutionThread);
        this.socialRepository = socialRepository;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return socialRepository.registerFacebookUser();
    }
}
