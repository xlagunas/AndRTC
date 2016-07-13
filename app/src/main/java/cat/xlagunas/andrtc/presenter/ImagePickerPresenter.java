package cat.xlagunas.andrtc.presenter;

import java.io.File;

import javax.inject.Inject;

import cat.xlagunas.andrtc.view.ImagePickerDataView;
import rx.Observer;
import xlagunas.cat.andrtc.domain.interactor.ImagePickerUseCase;

/**
 * Created by xlagunas on 13/7/16.
 */
public class ImagePickerPresenter implements Presenter {
    private File imageFile;

    private final ImagePickerUseCase useCase;
    private ImagePickerDataView view;

    @Inject
    public ImagePickerPresenter(ImagePickerUseCase useCase){
        this.useCase = useCase;
    }

    public void setView(ImagePickerDataView view){
        this.view = view;
    }

    @Override
    public void resume() {}

    @Override
    public void pause() {}

    @Override
    public void destroy() {}

    public void requestCameraPicture(){
        useCase.execute(new Observer<File>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                view.onError(e.getMessage());
            }

            @Override
            public void onNext(File file) {
                imageFile = file;
                view.onImageFileGenerated(imageFile);
            }
        });
    }

    public void onPictureTaken() {
        view.addPictureToGallery(imageFile);
        view.updateImage(imageFile);
    }

    public void onPictureSelectedFromGallery(File urlImage) {
        view.updateImage(urlImage);
    }
}
