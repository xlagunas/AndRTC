package cat.xlagunas.andrtc.domain.interactor;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;
import cat.xlagunas.andrtc.domain.executor.PostExecutionThread;

/**
 * Created by xlagunas on 26/02/16.
 */
public abstract class UseCase {

    private final PostExecutionThread postExecutionThread;

    UseCase(PostExecutionThread postExecutionThread){
        this.postExecutionThread = postExecutionThread;
    }

    private Subscription subscription = Subscriptions.empty();

    protected abstract Observable buildUseCaseObservable();

    @SuppressWarnings("unchecked")
    public void execute(Observer useCaseSubscriber) {
        this.subscription = this.buildUseCaseObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(postExecutionThread.getScheduler())
                .subscribe(useCaseSubscriber);
    }

    public void unsubscribe() {
        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }
}
