package cat.xlagunas.data.mapper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import cat.xlagunas.data.FriendEntity;
import cat.xlagunas.data.UserEntity;
import okhttp3.Credentials;
import okio.ByteString;
import xlagunas.cat.domain.Friend;
import xlagunas.cat.domain.User;

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
        entity.setUsername(user.getUsername());
        entity.setEmail(user.getEmail());
        entity.setLastSurname(user.getLastSurname());
        entity.setFirstSurname(user.getSurname());
        entity.setName(user.getName());
        entity.setPassword(decodeBasicAuthPassword(user.getHashedPassword(), user.getUsername()));

        return entity;
    }

    private String decodeBasicAuthPassword(String hashedPassword, String username) {
        //rip the "Basic " string in front of the password
        String secret = hashedPassword.substring(6);
        String str = ByteString.decodeBase64(secret).utf8();

        return str.substring(username.length()+1);
    }

    public List<Friend> transformFriends(UserEntity entity){
        List<Friend> friends = new ArrayList<>();

        for (FriendEntity userEntity : entity.getAccepted()){
            Friend friend = mapFriendEntity(userEntity);
            friend.setFriendState(Friend.ACCEPTED);
            friends.add(friend);
        }

        for (FriendEntity userEntity : entity.getBlocked()){
            Friend friend = mapFriendEntity(userEntity);
            friend.setFriendState(Friend.BLOCKED);
            friends.add(friend);
        }

        for (FriendEntity userEntity : entity.getPending()){
            Friend friend = mapFriendEntity(userEntity);
            friend.setFriendState(Friend.PENDING);
            friends.add(friend);
        }

        for (FriendEntity userEntity : entity.getRequested()){
            Friend friend = mapFriendEntity(userEntity);
            friend.setFriendState(Friend.REQUESTED);
            friends.add(friend);
        }

        return friends;
    }

    private User mapAbstractUser(UserEntity entity){
        User user = new User();
        user.setUsername(entity.getUsername());
        user.setName(entity.getName());
        user.setLastSurname(entity.getLastSurname());
        user.setSurname(entity.getFirstSurname());
        user.setEmail(entity.getEmail());
        user.setHashedPassword(Credentials.basic(entity.getUsername(), entity.getPassword()));

        return user;
    }
    private Friend mapFriendEntity(FriendEntity entity) {
        Friend friend = new Friend();
        friend.setUsername(entity.getUsername());
        friend.setName(entity.getName());
        friend.setLastSurname(entity.getLastSurname());
        friend.setSurname(entity.getSurname());
        friend.setEmail(entity.getEmail());

        return friend;
    }
}
