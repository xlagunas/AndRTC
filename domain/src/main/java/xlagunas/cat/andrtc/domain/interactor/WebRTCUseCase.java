package xlagunas.cat.andrtc.domain.interactor;

import javax.inject.Inject;

import rx.Observable;
import xlagunas.cat.andrtc.domain.executor.PostExecutionThread;

/**
 * Created by xlagunas on 22/07/16.
 */
public class WebRTCUseCase extends UseCase {
    //TODO COMPLETE

    @Inject
    WebRTCUseCase(PostExecutionThread postExecutionThread) {
        super(postExecutionThread);
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return null;
    }
}
