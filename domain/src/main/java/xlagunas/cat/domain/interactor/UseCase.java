package xlagunas.cat.domain.interactor;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;
import xlagunas.cat.domain.executor.PostExecutionThread;

/**
 * Created by xlagunas on 26/02/16.
 */
public abstract class UseCase {

    private final PostExecutionThread postExecutionThread;

    public UseCase(PostExecutionThread postExecutionThread){
        this.postExecutionThread = postExecutionThread;
    }

    private Subscription subscription = Subscriptions.empty();

    protected abstract Observable buildUseCaseObservable();

    @SuppressWarnings("unchecked")
    public void execute(Subscriber useCaseSubscriber) {
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
