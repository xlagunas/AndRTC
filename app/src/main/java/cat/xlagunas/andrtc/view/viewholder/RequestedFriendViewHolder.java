package cat.xlagunas.andrtc.view.viewholder;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;

import butterknife.BindView;
import cat.xlagunas.andrtc.R;
import xlagunas.cat.andrtc.domain.DefaultSubscriber;
import xlagunas.cat.andrtc.domain.Friend;
import xlagunas.cat.andrtc.domain.User;
import xlagunas.cat.andrtc.domain.interactor.UpdateFriendshipUseCase;

/**
 * Created by xlagunas on 19/03/16.
 */

@AutoFactory(implementing = FriendViewHolderFactory.class)
public class RequestedFriendViewHolder extends FriendViewHolder {
    private static final String TAG = RequestedFriendViewHolder.class.getSimpleName();

    @BindView(R.id.contact_accept_friendship)
    ImageButton acceptContact;

    @BindView(R.id.contact_reject_friendship)
    ImageButton rejectContact;

    final UpdateFriendshipUseCase updateFriendshipUseCase;

    public RequestedFriendViewHolder(ViewGroup parent, @Provided UpdateFriendshipUseCase updateFriendshipUseCase) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_requested, parent, false));
        this.updateFriendshipUseCase = updateFriendshipUseCase;

        acceptContact.setOnClickListener(v -> acceptFriendship());

        rejectContact.setOnClickListener(v -> rejectFriendship());
    }

    private void acceptFriendship() {
        Friend friend = getFriendAdapter().getItem(getAdapterPosition());
        updateFriendship(friend, Friend.PENDING_RELATIONSHIP, Friend.ACCEPTED_RELATIONSHIP);
    }

    private void rejectFriendship() {
        Friend friend = getFriendAdapter().getItem(getAdapterPosition());
        updateFriendship(friend, Friend.PENDING_RELATIONSHIP, Friend.REJECTED_RELATIONSHIP);
    }

    private void updateFriendship(final Friend friend, final String initialStatus, final String finalStatus) {
        updateFriendshipUseCase.setContactId(friend.getId());
        updateFriendshipUseCase.setPreviousState(initialStatus);
        updateFriendshipUseCase.setNextState(finalStatus);

        updateFriendshipUseCase.execute(new DefaultSubscriber<User>());
    }
}
