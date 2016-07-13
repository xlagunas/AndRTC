package cat.xlagunas.andrtc.presenter;

import android.net.Uri;

import java.io.File;

import javax.inject.Inject;

import cat.xlagunas.andrtc.view.ImagePickerDataView;
import rx.Observer;
import xlagunas.cat.andrtc.domain.User;
import xlagunas.cat.andrtc.domain.interactor.ImagePickerUseCase;

/**
 * Created by xlagunas on 13/7/16.
 */
public class ImagePickerPresenter implements Presenter {
    private Uri imageUri;

    private final ImagePickerUseCase useCase;
    private final User user;
    private ImagePickerDataView view;

    @Inject
    public ImagePickerPresenter(User user, ImagePickerUseCase useCase){
        this.useCase = useCase;
        this.user = user;
    }

    public void setView(ImagePickerDataView view){
        this.view = view;
    }

    @Override
    public void resume() {
        if (user.getThumbnail() != null && !"".equals(user.getThumbnail())){
            view.updateImage(Uri.parse(user.getThumbnail()));
        }
    }

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
                imageUri = Uri.fromFile(file);
                view.onImageFileGenerated(imageUri);
            }
        });
    }

    public void onPictureTaken() {
        view.addPictureToGallery(imageUri);
        view.updateImage(imageUri);
        user.setThumbnail(imageUri.toString());
    }

    public void onPictureSelectedFromGallery(Uri urlImage) {
        this.imageUri = urlImage;
        view.updateImage(urlImage);
        user.setThumbnail(imageUri.toString());
    }

    public void onDestroy(){
        useCase.unsubscribe();
        view = null;
    }
}
