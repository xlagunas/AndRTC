package cat.xlagunas.andrtc.view.fragment;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.di.components.UserComponent;
import cat.xlagunas.andrtc.presenter.UserDetailsRegisterPresenter;
import cat.xlagunas.andrtc.view.UserDetailsRegisterView;
import timber.log.Timber;

/**
 * Created by xlagunas on 4/04/16.
 */
public class UserDetailsRegisterFragment extends GenericRegisterFragment implements UserDetailsRegisterView, View.OnLayoutChangeListener {

    private final static int REQUEST_IMAGE_CAPTURE = 10001;
    private final static int REQUEST_LOAD_GALLERY_IMAGE = 10010;
    private static final String TAG = UserDetailsRegisterFragment.class.getSimpleName();

    @BindView(R.id.camera_button)
    ImageView userImage;

    @BindView(R.id.fullname)
    TextInputLayout fullName;

    @BindView(R.id.finish_button)
    Button finishButton;

    @Inject
    UserDetailsRegisterPresenter presenter;

    @Override
    public int getLayout() {
        return R.layout.fragment_user_details_register;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent(UserComponent.class).inject(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.addOnLayoutChangeListener(this);
        presenter.setView(this);
        fullName.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateFullUser(s.toString());
            }
        });

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
    public View getNextButton() {
        return finishButton;
    }

    @Override
    public void updateImage(Uri file) {
        Glide.with(this).loadFromMediaStore(file).into(userImage);
    }

    @Override
    public void updateFullName(String fullName) {
        this.fullName.getEditText().setText(fullName);
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
            Timber.d("Image successfully obtained");
            presenter.onPictureTaken();
        } else if (requestCode == REQUEST_LOAD_GALLERY_IMAGE && resultCode == Activity.RESULT_OK) {
            Timber.d("Image successfully obtained");
            presenter.onPictureSelectedFromGallery(data.getData());
        }
    }

    private void validateFullUser(String fullUser) {
        if (presenter.validateUsername(fullUser)) {
            this.fullName.setError(null);
            this.fullName.setErrorEnabled(false);
        } else {
            setError(fullName, getString(R.string.registration_full_name_error));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.presenter.destroy();
    }

    @Override
    public void enableNextStep(boolean enable) {
        getNextButton().setEnabled(enable);
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        v.removeOnLayoutChangeListener(this);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            int cx = (int) nextButton.getX();
            int cy = (int) nextButton.getY();
            int width = getView().getWidth();
            int height = getView().getHeight();

            getView().setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));

            float finalRadius = Math.max(width, height) / 2 + Math.max(width - cx, height - cy);
            Animator anim = ViewAnimationUtils.createCircularReveal(v, cx, cy, 0, finalRadius);

            anim.setDuration(300);
            anim.start();
        }
    }
}

