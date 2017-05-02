package cat.xlagunas.andrtc.di.modules;

import cat.xlagunas.andrtc.di.UserScope;
import cat.xlagunas.andrtc.view.viewholder.AcceptedFriendViewHolderFactory;
import cat.xlagunas.andrtc.view.viewholder.AddFriendViewHolderFactory;
import cat.xlagunas.andrtc.view.viewholder.FriendViewHolderFactory;
import cat.xlagunas.andrtc.view.viewholder.PendingFriendViewHolderFactory;
import cat.xlagunas.andrtc.view.viewholder.RequestedFriendViewHolderFactory;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntKey;
import dagger.multibindings.IntoMap;
import cat.xlagunas.andrtc.domain.Friend;
import cat.xlagunas.andrtc.domain.User;

/**
 * Created by xlagunas on 2/03/16.
 */

@Module
public class UserModule {

    private User user;

    public UserModule() {
    }

    public UserModule(User user) {
        this.user = user;
    }

    @Provides
    @UserScope
    public User getUser() {
        return user;
    }

    @Provides
    @IntoMap
    @IntKey(Friend.ACCEPTED)
    FriendViewHolderFactory provideAcceptedFriendViewHolderFactory(AcceptedFriendViewHolderFactory factory) {
        return factory;
    }

    @Provides
    @IntoMap
    @IntKey(Friend.PENDING)
    FriendViewHolderFactory providePendingFriendViewHolderFactory(PendingFriendViewHolderFactory factory) {
        return factory;
    }

    @Provides
    @IntoMap
    @IntKey(Friend.REQUESTED)
    FriendViewHolderFactory provideRequestedFriendViewHolderFactory(RequestedFriendViewHolderFactory factory) {
        return factory;
    }

    @Provides
    @IntoMap
    @IntKey(Friend.CREATED)
    FriendViewHolderFactory provideCreatedFriendViewHolderFactory(AddFriendViewHolderFactory factory) {
        return factory;
    }

}
