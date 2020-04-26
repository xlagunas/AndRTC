package cat.xlagunas.viv.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import cat.xlagunas.core.OpenForTesting
import cat.xlagunas.core.common.viewModel
import cat.xlagunas.core.domain.entity.User
import cat.xlagunas.viv.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import di.Injectable

@OpenForTesting
class
ProfileFragment : Fragment(), Injectable {

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
        profileViewModel = viewModel(ProfileViewModel::class.java)
        profileViewModel.user.observe(viewLifecycleOwner, Observer(this::onUserEvent))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.content_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)
    }

    private fun onUserEvent(user: User?) {
        user?.let(this::renderUser)
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

    @OnClick(R.id.logout_button)
    fun logout() {
        profileViewModel.logout()
    }
}