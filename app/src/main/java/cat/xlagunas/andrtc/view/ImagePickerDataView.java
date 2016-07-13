package cat.xlagunas.andrtc.view;

import java.io.File;

/**
 * Created by xlagunas on 13/7/16.
 */
public interface ImagePickerDataView {

    void onImageFileGenerated(File file);
    void updateImage(File file);
    void onError(String message);
    void addPictureToGallery(File file);
}
