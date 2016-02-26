package xlagunas.cat.data.mapper;

import java.util.ArrayList;
import java.util.List;

import xlagunas.cat.data.UserEntity;
import xlagunas.cat.domain.AbstractUser;
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

        for (UserEntity userEntity : entity.getAccepted()){
            Friend friend = (Friend) mapAbstractUser(userEntity);
            friend.setFriendState(Friend.ACCEPTED);
            friends.add(friend);
        }

        for (UserEntity userEntity : entity.getBlocked()){
            Friend friend = (Friend) mapAbstractUser(userEntity);
            friend.setFriendState(Friend.BLOCKED);
            friends.add(friend);
        }

        for (UserEntity userEntity : entity.getPending()){
            Friend friend = (Friend) mapAbstractUser(userEntity);
            friend.setFriendState(Friend.PENDING);
            friends.add(friend);
        }

        for (UserEntity userEntity : entity.getRequested()){
            Friend friend = (Friend) mapAbstractUser(userEntity);
            friend.setFriendState(Friend.REQUESTED);
            friends.add(friend);
        }

        return friends;
    }

    private AbstractUser mapAbstractUser(UserEntity entity){
        Friend friend = new Friend();
        friend.setUsername(entity.getUsername());
        friend.setName(entity.getName());
        friend.setLastSurname(entity.getLastSurname());
        friend.setSurname(entity.getFirstSurname());

        return friend;
    }
}
