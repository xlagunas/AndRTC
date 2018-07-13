package cat.xlagunas.viv.profile

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import butterknife.BindView
import butterknife.ButterKnife
import cat.xlagunas.domain.commons.User
import cat.xlagunas.viv.R
import cat.xlagunas.viv.commons.di.VivApplication
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class ProfileFragment : Fragment() {

    @BindView(R.id.profile_image)
    lateinit var thumbnail: ImageView

    @BindView(R.id.profile_email)
    lateinit var email: TextView

    @BindView(R.id.profile_username)
    lateinit var username: TextView

    @BindView(R.id.profile_firstname)
    lateinit var firstName: TextView

    @BindView(R.id.profile_lastname)
    lateinit var lastName: TextView

    private lateinit var profileViewModel: ProfileViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        profileViewModel =
            ViewModelProviders.of(this, (activity!!.applicationContext as VivApplication).viewModelFactory)
                .get(ProfileViewModel::class.java)
        profileViewModel.user.observe(this, Observer(this::onUserEvent))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.content_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)
    }

    private fun onUserEvent(user: User?) {
        if (user == null) {
            findNavController().navigate(R.id.action_login)
        } else {
            renderUser(user)
        }
    }

    private fun renderUser(user: User) {
        Glide.with(this)
            .load(user.imageUrl)
            .apply(RequestOptions.placeholderOf(R.drawable.profile_placeholder))
            .into(thumbnail)

        email.text = user.email
        username.text = user.username
        firstName.text = user.firstName
        lastName.text = user.lastName
    }
}