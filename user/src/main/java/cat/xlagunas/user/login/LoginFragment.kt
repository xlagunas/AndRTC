package cat.xlagunas.user.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import cat.xlagunas.core.OpenForTesting
import cat.xlagunas.core.common.displayMessage
import cat.xlagunas.core.common.viewModel
import cat.xlagunas.user.R
import cat.xlagunas.user.databinding.FragmentLoginBinding

@OpenForTesting
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

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
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.loginButton.setOnClickListener {
            loginViewModel.login(
                binding.usernameInputLayout.text(),
                binding.passwordTextInput.text()
            )
        }
        binding.register.setOnClickListener { loginViewModel.navigateToRegistration() }
        return binding.root
    }

    private fun handleLoginResult(loginState: LoginState?) {
        when (loginState) {
            is SuccessLoginState -> displayMessage(
                requireContext().getString(
                    R.string.login_successful,
                    binding.usernameInputLayout.text()
                )
            )
            is InvalidLoginState -> displayMessage(loginState.errorMessage)
            is ValidationError -> {
                binding.usernameInputLayout.error = "Username can't be empty"
                binding.passwordTextInput.error = "Password can't be empty"
            }
        }
    }
}
