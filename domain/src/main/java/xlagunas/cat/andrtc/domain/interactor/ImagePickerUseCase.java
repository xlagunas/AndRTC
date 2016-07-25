package xlagunas.cat.andrtc.domain.interactor;

import javax.inject.Inject;

import rx.Observable;
import xlagunas.cat.andrtc.domain.executor.PostExecutionThread;
import xlagunas.cat.andrtc.domain.repository.FileRepository;

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
