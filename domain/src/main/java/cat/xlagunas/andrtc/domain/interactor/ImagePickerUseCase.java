package cat.xlagunas.andrtc.domain.interactor;

import javax.inject.Inject;

import rx.Observable;
import cat.xlagunas.andrtc.domain.executor.PostExecutionThread;
import cat.xlagunas.andrtc.domain.repository.FileRepository;

/**
 * Created by xlagunas on 13/7/16.
 */

public class ImagePickerUseCase extends UseCase {

    private final FileRepository fileRepository;

    @Inject
    public ImagePickerUseCase(PostExecutionThread postExecutionThread, FileRepository fileRepository) {
        super(postExecutionThread);
        this.fileRepository = fileRepository;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return fileRepository.generateImageFile();
    }
}
