package cat.xlagunas.andrtc.view.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pedrogomez.renderers.Renderer;

import butterknife.Bind;
import butterknife.ButterKnife;
import cat.xlagunas.andrtc.R;
import xlagunas.cat.andrtc.domain.Friend;

/**
 * Created by xlagunas on 14/03/16.
 */
public class CurrentFriend extends ButterKnifeRenderer<Friend> {

    @Bind(R.id.contact_username)
    TextView username;
    @Bind(R.id.contact_fullname)
    TextView fullname;
    @Bind(R.id.contact_thumbnail)
    ImageView thumbnail;

    @Override
    protected View inflate(LayoutInflater inflater, ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_friend, parent, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void render() {
        Friend friend = getContent();
        username.setText(friend.getUsername());
        fullname.setText(friend.getName()+" "+friend.getSurname()+" "+friend.getLastSurname());
    }
}
