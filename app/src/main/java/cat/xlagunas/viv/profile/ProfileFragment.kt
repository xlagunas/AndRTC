package cat.xlagunas.viv.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import cat.xlagunas.core.di.Injectable
import cat.xlagunas.core.domain.entity.User
import cat.xlagunas.data.OpenForTesting
import cat.xlagunas.viv.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import javax.inject.Inject

@OpenForTesting
class
ProfileFragment : androidx.fragment.app.Fragment(), Injectable {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // DaggerMonolythComponent.builder().withParentComponent(VivApplication.appComponent(context!!)).build().inject(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        profileViewModel = ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel::class.java)
        profileViewModel.loginDataEvent.observe(this, Observer(this::userLoggedInStatus))
        profileViewModel.user.observe(this, Observer(this::onUserEvent))
        profileViewModel.getLoginStatus()
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

    @OnClick(R.id.logout_button)
    fun logout() {
        profileViewModel.logout()
    }

    fun navController() = findNavController()
}