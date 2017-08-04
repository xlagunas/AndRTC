package cat.xlagunas.andrtc.data.repository;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Singleton;

import cat.xlagunas.andrtc.data.cache.UserCache;
import rx.Observable;
import cat.xlagunas.andrtc.domain.repository.FileRepository;

/**
 * Created by xlagunas on 13/7/16.
 */
@Singleton
public class FileRepositoryImpl implements FileRepository {


    private final UserCache userCache;

    @Inject
    public FileRepositoryImpl(UserCache userCache) {
        this.userCache = userCache;
    }

    @Override
    public Observable<File> generateImageFile() {
        return userCache.generateProfilePictureFile();
    }

    @Override
    public Observable<String> getStoredToken() {
        if (!userCache.isGCMRegistered() && userCache.getGCMToken() != null) {
            return Observable.just(userCache.getGCMToken());
        } else {
            return Observable.empty();
        }
    }
}
