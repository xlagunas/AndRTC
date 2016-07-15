package cat.xlagunas.andrtc.view;

import android.net.Uri;

/**
 * Created by xlagunas on 13/7/16.
 */
public interface UserDetailsRegisterView extends BaseRegisterDataView{

    void onImageFileGenerated(Uri file);
    void onError(String message);
    void addPictureToGallery(Uri file);
    void updateImage(Uri imageFile);

    void updateFullName(String fullName);
}
