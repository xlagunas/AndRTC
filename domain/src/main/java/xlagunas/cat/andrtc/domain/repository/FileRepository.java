package xlagunas.cat.andrtc.domain.repository;

import java.io.File;

import rx.Observable;

/**
 * Created by xlagunas on 13/7/16.
 */
public interface FileRepository {

    Observable<File> generateImageFile();
}
