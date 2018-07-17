package cat.xlagunas.viv.profile

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
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
import cat.xlagunas.data.OpenForTesting
import cat.xlagunas.domain.commons.User
import cat.xlagunas.viv.R
import cat.xlagunas.viv.commons.di.Injectable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import javax.inject.Inject

@OpenForTesting
class ProfileFragment : Fragment(), Injectable {

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

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var profileViewModel: ProfileViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        profileViewModel = ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel::class.java)
        profileViewModel.loggedInStatus.observe(this, Observer(this::userLoggedInStatus))
        profileViewModel.user.observe(this, Observer(this::onUserEvent))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.content_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)
    }

    private fun userLoggedInStatus(loggedInStatus: Boolean?) {
        if (loggedInStatus == false) {
            navController().navigate(R.id.action_login)
        }
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

    fun navController() = findNavController()
}