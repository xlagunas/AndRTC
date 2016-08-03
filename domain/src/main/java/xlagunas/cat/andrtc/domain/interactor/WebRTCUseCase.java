package xlagunas.cat.andrtc.domain.interactor;

import javax.inject.Inject;

import rx.Observable;
import xlagunas.cat.andrtc.domain.executor.PostExecutionThread;
import xlagunas.cat.andrtc.domain.repository.UserRepository;

/**
 * Created by xlagunas on 22/07/16.
 */
public class WebRTCUseCase extends UseCase {

    private String conferenceId;

    public void setConferenceId(String conferenceId) {
        this.conferenceId = conferenceId;
    }

    @Inject
    WebRTCUseCase(PostExecutionThread postExecutionThread) {
        super(postExecutionThread);
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return null;
    }
}
