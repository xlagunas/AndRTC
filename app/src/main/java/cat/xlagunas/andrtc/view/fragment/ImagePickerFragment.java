package cat.xlagunas.andrtc.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;
import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.di.components.UserComponent;
import cat.xlagunas.andrtc.presenter.ImagePickerPresenter;
import cat.xlagunas.andrtc.view.ImagePickerDataView;

/**
 * Created by xlagunas on 4/04/16.
 */
public class ImagePickerFragment extends GenericRegisterFragment implements ImagePickerDataView {

    private final static int REQUEST_IMAGE_CAPTURE = 10001;
    private final static int REQUEST_LOAD_GALLERY_IMAGE = 10010;
    private static final String TAG = ImagePickerFragment.class.getSimpleName();

    @Bind(R.id.camera_button)
    ImageView userImage;

    @Inject
    ImagePickerPresenter presenter;

    @Override
    public int getLayout() {
        return R.layout.fragment_register_image_picker;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent(UserComponent.class).inject(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.setView(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.resume();
    }

    @OnClick(R.id.camera_button)
    public void onCameraButtonClicked() {
        presenter.requestCameraPicture();
    }

    @OnClick(R.id.media_button)
    public void onMediaButtonClicked() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, REQUEST_LOAD_GALLERY_IMAGE);
    }

    @Override
    public void onImageFileGenerated(Uri file) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, file);

            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

    }

    @Override
    public void updateImage(Uri file) {
        Glide.with(this).loadFromMediaStore(file).into(userImage);
    }

    @Override
    public void onError(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void addPictureToGallery(Uri imageUri) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(imageUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "Image successfully obtained");
            presenter.onPictureTaken();
        } else if (requestCode == REQUEST_LOAD_GALLERY_IMAGE && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "Image successfully obtained");
            presenter.onPictureSelectedFromGallery(data.getData());
        }
    }

}
