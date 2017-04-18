package cat.xlagunas.andrtc.data.mapper;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import cat.xlagunas.andrtc.constants.ServerConstants;
import cat.xlagunas.andrtc.data.FriendEntity;
import cat.xlagunas.andrtc.data.UserEntity;
import okhttp3.Credentials;
import okio.ByteString;
import rx.Observable;
import xlagunas.cat.andrtc.domain.Friend;
import xlagunas.cat.andrtc.domain.User;

/**
 * Created by xlagunas on 26/02/16.
 */
@Singleton
public class UserEntityMapper {

    @Inject
    public UserEntityMapper() {

    }

    public User transformUser(UserEntity entity) {
        User user = (User) mapAbstractUser(entity);
        user.setFriends(transformFriends(entity));
        return user;
    }

    public UserEntity tranformUserEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setUsername(user.getUsername());
        entity.setEmail(user.getEmail());
        entity.setLastSurname(user.getLastSurname());
        entity.setFirstSurname(user.getSurname());
        entity.setName(user.getName());
        entity.setThumbnail(user.getThumbnail());
        entity.setFacebookId(user.getFacebookId());
        //this is only used to register a new user and password is clear in this case so don't need to decode
        entity.setPassword(user.getPassword());

        return entity;
    }

    public String decodeBasicAuthPassword(String username, String hashedPassword) {
        //rip the "Basic " string in front of the password
        String secret = hashedPassword.substring(6);
        String str = ByteString.decodeBase64(secret).utf8();

        return str.substring(username.length() + 1);
    }

    public String encodeBasicAuthPassword(String username, String password) {
        return Credentials.basic(username, password);
    }

    public List<Friend> transformFriends(UserEntity entity) {
        List<Friend> friends = new ArrayList<>();
        if (entity.getAccepted() != null) {
            for (FriendEntity userEntity : entity.getAccepted()) {
                Friend friend = mapFriendEntity(userEntity);
                friend.setFriendState(Friend.ACCEPTED);
                friends.add(friend);
            }
        }

        if (entity.getBlocked() != null) {
            for (FriendEntity userEntity : entity.getBlocked()) {
                Friend friend = mapFriendEntity(userEntity);
                friend.setFriendState(Friend.BLOCKED);
                friends.add(friend);
            }
        }

        if (entity.getPending() != null) {
            for (FriendEntity userEntity : entity.getPending()) {
                Friend friend = mapFriendEntity(userEntity);
                friend.setFriendState(Friend.PENDING);
                friends.add(friend);
            }
        }

        if (entity.getRequested() != null) {
            for (FriendEntity userEntity : entity.getRequested()) {
                Friend friend = mapFriendEntity(userEntity);
                friend.setFriendState(Friend.REQUESTED);
                friends.add(friend);
            }
        }

        return friends;
    }

    private User mapAbstractUser(UserEntity entity) {
        User user = new User();
        user.setId(entity.getId());
        user.setUsername(entity.getUsername());
        user.setName(entity.getName());
        user.setLastSurname(entity.getLastSurname());
        user.setSurname(entity.getFirstSurname());
        user.setEmail(entity.getEmail());
        user.setPassword(entity.getPassword());
        user.setFacebookId(entity.getFacebookId());
        user.setThumbnail(entity.getThumbnail().startsWith("http") ? entity.getThumbnail() : ServerConstants.IMAGE_SERVER + entity.getThumbnail());

        return user;
    }

    public Friend mapFriendEntity(FriendEntity entity) {
        Friend friend = new Friend();
        friend.setId(entity.getId());
        friend.setUsername(entity.getUsername());
        friend.setName(entity.getName());
        friend.setLastSurname(entity.getLastSurname());
        friend.setSurname(entity.getSurname());
        friend.setEmail(entity.getEmail());
        friend.setThumbnail(entity.getThumbnail().startsWith("http") ? entity.getThumbnail() : ServerConstants.IMAGE_SERVER + entity.getThumbnail());

        return friend;
    }

    public Observable<UserEntity> parseFacebookJsonData(JSONObject jsonUserData) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(jsonUserData.optString("id"));
        userEntity.setEmail(jsonUserData.optString("email"));
        userEntity.setName(jsonUserData.optString("first_name"));
        userEntity.setFirstSurname(jsonUserData.optString("middle_name"));
        userEntity.setLastSurname(jsonUserData.optString("last_name"));
        userEntity.setJoinDate(new DateTime());
        userEntity.setFacebookId(jsonUserData.optString("id"));
        try {
            userEntity.setThumbnail(jsonUserData.getJSONObject("picture").getJSONObject("data").getString("url"));
        } catch (JSONException e) {
            return Observable.error(e);
        }
        return Observable.just(userEntity);
    }

    public Observable<List<FriendEntity>> transformFacebookFriends(JSONArray jsonFriendList) {
        List<FriendEntity> friendEntities = new ArrayList<>(jsonFriendList.length());
        for (int i = 0; i < jsonFriendList.length(); i++) {
            try {
                JSONObject friend = jsonFriendList.getJSONObject(i);
                FriendEntity friendEntity = new FriendEntity();
                //TODO transform friend and add to array
                friendEntities.add(friendEntity);
            } catch (JSONException e) {
                return Observable.error(e);
            }
        }
        return Observable.just(friendEntities);
    }

    public Observable<UserEntity> parseGoogleData(GoogleSignInAccount account) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(account.getEmail());
        userEntity.setEmail(account.getEmail());
        userEntity.setName(account.getDisplayName());
        userEntity.setFirstSurname("");
        userEntity.setLastSurname("");
        userEntity.setJoinDate(new DateTime());
        userEntity.setThumbnail(account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : "");
        userEntity.setGoogleId(account.getId());

        return Observable.just(userEntity);
    }
}
