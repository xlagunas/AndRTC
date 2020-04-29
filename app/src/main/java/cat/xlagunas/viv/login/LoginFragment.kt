package cat.xlagunas.viv.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import butterknife.BindView
import butterknife.ButterKnife
import cat.xlagunas.core.OpenForTesting
import cat.xlagunas.core.common.viewModel
import cat.xlagunas.viv.R
import cat.xlagunas.viv.commons.extension.text
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

@OpenForTesting
class LoginFragment : Fragment() {

    @BindView(R.id.username_input_layout)
    lateinit var usernameInputLayout: com.google.android.material.textfield.TextInputLayout

    @BindView(R.id.password_text_input)
    lateinit var passwordInputLayout: com.google.android.material.textfield.TextInputLayout

    @BindView(R.id.login_button)
    lateinit var loginButton: Button

    @BindView(R.id.register)
    lateinit var registerButton: Button

    lateinit var loginViewModel: LoginViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loginViewModel = viewModel(LoginViewModel::class.java)
        loginViewModel.onLoginStateChange()
            .observe(viewLifecycleOwner, Observer(this::handleLoginResult))
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
        loginButton.setOnClickListener {
            loginViewModel.login(
                usernameInputLayout.text(),
                passwordInputLayout.text()
            )
        }
        registerButton.setOnClickListener { loginViewModel.navigateToRegistration() }
    }

    private fun handleLoginResult(loginState: LoginState?) {
        when (loginState) {
            is InvalidLoginState -> Snackbar.make(
                requireView(),
                loginState.errorMessage,
                BaseTransientBottomBar.LENGTH_SHORT
            ).show()
            is ValidationError -> {
                usernameInputLayout.error = "Username can't be empty"
                passwordInputLayout.error = "Password can't be empty"
            }
        }
    }
}
