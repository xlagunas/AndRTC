package cat.xlagunas.andrtc.presenter;

import android.net.Uri;

import java.io.File;

import javax.inject.Inject;

import cat.xlagunas.andrtc.view.UserDetailsRegisterView;
import cat.xlagunas.andrtc.view.util.FieldValidator;
import rx.Observer;
import xlagunas.cat.andrtc.domain.User;
import xlagunas.cat.andrtc.domain.interactor.ImagePickerUseCase;

/**
 * Created by xlagunas on 13/7/16.
 */
public class UserDetailsRegisterPresenter implements Presenter {
    private Uri imageUri;

    private final ImagePickerUseCase useCase;
    private final FieldValidator validator;
    private final User user;

    private UserDetailsRegisterView view;

    @Inject
    public UserDetailsRegisterPresenter(User user, FieldValidator validator, ImagePickerUseCase useCase) {
        this.useCase = useCase;
        this.user = user;
        this.validator = validator;
    }

    public void setView(UserDetailsRegisterView view) {
        this.view = view;
    }

    @Override
    public void resume() {
        if (user.getThumbnail() != null && !"".equals(user.getThumbnail())) {
            view.updateImage(Uri.parse(user.getThumbnail()));
        }

        if (user.getName() != null && !"".equals(user.getName())) {
            view.updateFullName(String.format("%s %s %s", user.getName(), user.getSurname(), user.getLastSurname()));
        }
    }

    @Override
    public void pause() {
    }

    @Override
    public void destroy() {
    }

    public void requestCameraPicture() {
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

    public void onDestroy() {
        useCase.unsubscribe();
        view = null;
    }

    public boolean validateUsername(String fullName) {
        boolean isValid = validator.isValidFullName(fullName);
        view.enableNextStep(isValid);
        if (isValid) {
            parseFullName(fullName);
        }
        return isValid;
    }

    private void parseFullName(String username) {
        String[] nameArray = username.split(" ");
        if (nameArray.length >= 3) {
            user.setLastSurname(nameArray[nameArray.length - 1]);
            user.setSurname(nameArray[nameArray.length - 2]);
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < nameArray.length - 2; i++) {
                stringBuilder.append(nameArray[i] + " ");
            }
            user.setName(stringBuilder.toString());
        } else if (nameArray.length == 2) {
            user.setName(nameArray[0]);
            user.setSurname(nameArray[1]);
            user.setLastSurname("");
        }
    }


}
