package cat.xlagunas.andrtc.presenter;

import javax.inject.Inject;

import cat.xlagunas.andrtc.presenter.Presenter;
import cat.xlagunas.andrtc.view.ConferenceView;
import xlagunas.cat.andrtc.domain.User;
import xlagunas.cat.andrtc.domain.interactor.WebRTCUseCase;

/**
 * Created by xlagunas on 3/8/16.
 */
public class ConferencePresenter implements Presenter {

    private final User user;
    private final WebRTCUseCase useCase;

    private ConferenceView view;
    private String conferenceId;

    @Inject
    public ConferencePresenter(User user, WebRTCUseCase webRTCUseCase){
        this.user = user;
        this.useCase = webRTCUseCase;
    }

    public void setView(ConferenceView view) {
        this.view = view;
    }

    public void setConferenceId(String conferenceId) {
        this.conferenceId = conferenceId;
    }

    @Override
    public void resume() {
    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {

    }
}
