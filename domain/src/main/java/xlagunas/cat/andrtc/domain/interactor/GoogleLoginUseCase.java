package xlagunas.cat.andrtc.domain.interactor;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import xlagunas.cat.andrtc.domain.User;
import xlagunas.cat.andrtc.domain.executor.PostExecutionThread;
import xlagunas.cat.andrtc.domain.repository.SocialRepository;
import xlagunas.cat.andrtc.domain.repository.UserRepository;

/**
 * Created by xlagunas on 20/8/16.
 */
public class GoogleLoginUseCase extends UseCase {
    private final SocialRepository socialRepository;
    private final UserRepository userRepository;

    @Inject
    public GoogleLoginUseCase(PostExecutionThread postExecutionThread, SocialRepository socialRepository, UserRepository userRepository) {
        super(postExecutionThread);
        this.socialRepository = socialRepository;
        this.userRepository = userRepository;
    }

    @Override
    protected Observable<User> buildUseCaseObservable() {
        return socialRepository.registerGoogleUser()
                .observeOn(Schedulers.io())
                .flatMap(new Func1<User, Observable<User>>() {
            @Override
            public Observable<User> call(User user) {
                return userRepository.registerGoogleUser(user);
            }
        });
    }
}
