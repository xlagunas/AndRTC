package cat.xlagunas.viv.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import butterknife.BindView
import butterknife.ButterKnife
import cat.xlagunas.core.di.Injectable
import cat.xlagunas.core.di.VivApplication
import cat.xlagunas.data.OpenForTesting
import cat.xlagunas.data.user.login.GoogleSignInDataSource.Companion.RC_SIGN_IN
import cat.xlagunas.viv.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.SignInButton
import com.google.android.material.snackbar.Snackbar
import dagger.DaggerMonolythComponent
import javax.inject.Inject

@OpenForTesting
class LoginFragment : Fragment(), Injectable {

    @BindView(R.id.sign_in_button)
    lateinit var signInButton: SignInButton

    @BindView(R.id.username_input_layout)
    lateinit var usernameInputLayout: com.google.android.material.textfield.TextInputLayout

    @BindView(R.id.password_text_input)
    lateinit var passwordInputLayout: com.google.android.material.textfield.TextInputLayout

    @BindView(R.id.login_button)
    lateinit var loginButton: Button

    @BindView(R.id.register)
    lateinit var registerButton: Button

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerMonolythComponent.builder()
            .withParentComponent(VivApplication.appComponent(context!!)).build()
            .inject(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loginViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel::class.java)

        lifecycle.addObserver(loginViewModel.registerGoogle())

        loginViewModel.onLoginStateChange()
            .observe(this, Observer(this::handleLoginResult))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ButterKnife.bind(this, view)
        signInButton.setOnClickListener { loginViewModel.initGoogleSignIn() }
        // loginButton.setOnClickListener { loginViewModel.login(usernameInputLayout.text(), passwordInputLayout.text()) }
        loginButton.setOnClickListener {
            startActivity(
                Intent(Intent.ACTION_VIEW).setClassName(
                    context!!,
                    "cat.xlagunas.conference.ui.ConferenceActivity"
                )
            )
        }
        registerButton.setOnClickListener { navController().navigate(R.id.action_login_to_register) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            loginViewModel.handleLoginResult(GoogleSignIn.getSignedInAccountFromIntent(data))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(loginViewModel.registerGoogle())
    }

    private fun handleLoginResult(loginState: LoginState?) {
        when (loginState) {
            is SuccessLoginState -> {
                navController().popBackStack()
            }
            is InvalidLoginState -> com.google.android.material.snackbar.Snackbar.make(
                view!!, loginState.errorMessage, Snackbar.LENGTH_SHORT
            ).show()
            is ValidationError -> {
                usernameInputLayout.error = "Username can't be empty"
                passwordInputLayout.error = "Password can't be empty"
            }
        }
    }

    fun navController() = findNavController()
}
