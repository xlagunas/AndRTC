package cat.xlagunas.andrtc.di.modules;

import cat.xlagunas.andrtc.data.repository.SocialRepositoryImpl;
import cat.xlagunas.andrtc.data.social.FacebookManager;
import cat.xlagunas.andrtc.data.social.FacebookManagerImpl;
import cat.xlagunas.andrtc.data.social.GoogleManager;
import cat.xlagunas.andrtc.data.social.GoogleManagerImpl;
import cat.xlagunas.andrtc.di.ActivityScope;
import dagger.Module;
import dagger.Provides;
import xlagunas.cat.andrtc.domain.repository.SocialRepository;

/**
 * Created by xlagunas on 20/8/16.
 */

@Module
public class SocialModule {

    @Provides
    @ActivityScope
    FacebookManager provideFacebookManager(FacebookManagerImpl facebookManager) {
        return facebookManager;
    }

    @Provides
    @ActivityScope
    SocialRepository provideSocialRepository(SocialRepositoryImpl socialRepositoryImpl) {
        return socialRepositoryImpl;
    }

    @Provides
    @ActivityScope
    GoogleManager provideGoogleManager(GoogleManagerImpl googleManager) {
        return googleManager;
    }

}
