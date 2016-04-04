package xlagunas.cat.andrtc.domain.interactor;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.schedulers.Schedulers;
import xlagunas.cat.andrtc.domain.User;
import xlagunas.cat.andrtc.domain.executor.PostExecutionThread;
import xlagunas.cat.andrtc.domain.repository.UserRepository;

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
