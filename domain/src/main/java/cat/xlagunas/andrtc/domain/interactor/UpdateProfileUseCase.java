package cat.xlagunas.andrtc.domain.interactor;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.schedulers.Schedulers;
import cat.xlagunas.andrtc.domain.User;
import cat.xlagunas.andrtc.domain.executor.PostExecutionThread;
import cat.xlagunas.andrtc.domain.repository.UserRepository;

/**
 * Created by xlagunas on 30/03/16.
 */
public class UpdateProfileUseCase extends UseCase{

    private final User user;
    private final UserRepository userRepository;

    @Inject
    public UpdateProfileUseCase(PostExecutionThread thread, User user, UserRepository userRepository){
        super(thread);
        this.user = user;
        this.userRepository = userRepository;
    }


    @Override
    protected Observable buildUseCaseObservable() {
        return userRepository.updateProfile(user);
    }

    @Override
    public void execute(Observer useCaseSubscriber) {
        this.buildUseCaseObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.immediate())
                .subscribe(useCaseSubscriber);
    }
}
