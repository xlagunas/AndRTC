package cat.xlagunas.andrtc.data.mapper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import cat.xlagunas.andrtc.data.FriendEntity;
import cat.xlagunas.andrtc.data.UserEntity;
import okhttp3.Credentials;
import okio.ByteString;
import xlagunas.cat.andrtc.domain.Friend;
import xlagunas.cat.andrtc.domain.User;

/**
 * Created by xlagunas on 26/02/16.
 */
@Singleton
public class UserEntityMapper {

    @Inject
    public UserEntityMapper(){

    }

    public User transformUser(UserEntity entity){
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
        //this is only used to register a new user and password is clear in this case so don't need to decode
        entity.setPassword(user.getPassword());

        return entity;
    }

    public String decodeBasicAuthPassword(String username, String hashedPassword) {
        //rip the "Basic " string in front of the password
        String secret = hashedPassword.substring(6);
        String str = ByteString.decodeBase64(secret).utf8();

        return str.substring(username.length()+1);
    }

    public String encodeBasicAuthPassword(String username, String password){
        return Credentials.basic(username, password);
    }

    public List<Friend> transformFriends(UserEntity entity){
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

    private User mapAbstractUser(UserEntity entity){
        User user = new User();
        user.setId(entity.getId());
        user.setUsername(entity.getUsername());
        user.setName(entity.getName());
        user.setLastSurname(entity.getLastSurname());
        user.setSurname(entity.getFirstSurname());
        user.setEmail(entity.getEmail());
        user.setPassword(entity.getPassword());
        user.setThumbnail("http://192.168.1.133:3000/images/"+entity.getThumbnail());

        return user;
    }

    public  Friend mapFriendEntity(FriendEntity entity) {
        Friend friend = new Friend();
        friend.setId(entity.getId());
        friend.setUsername(entity.getUsername());
        friend.setName(entity.getName());
        friend.setLastSurname(entity.getLastSurname());
        friend.setSurname(entity.getSurname());
        friend.setEmail(entity.getEmail());
        friend.setThumbnail("http://192.168.1.133:3000/images/"+entity.getThumbnail());

        return friend;
    }
}
