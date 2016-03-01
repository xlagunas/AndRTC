package xlagunas.cat.data.mapper;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Credentials;
import xlagunas.cat.data.FriendEntity;
import xlagunas.cat.data.UserEntity;
import xlagunas.cat.domain.Friend;
import xlagunas.cat.domain.User;

/**
 * Created by xlagunas on 26/02/16.
 */
public class UserEntityMapper {

    public User transformUser(UserEntity entity){
        User user = (User) mapAbstractUser(entity);
        user.setFriends(transformFriends(entity));
        return user;
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
        user.setHashedPassword(Credentials.basic(entity.getUsername(), entity.getPassword()));

        return user;
    }
    private Friend mapFriendEntity(FriendEntity entity) {
        Friend friend = new Friend();
        friend.setUsername(entity.getUsername());
        friend.setName(entity.getName());
        friend.setLastSurname(entity.getLastSurname());
        friend.setSurname(entity.getSurname());

        return friend;
    }
}
