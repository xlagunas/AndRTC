package cat.xlagunas.user.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cat.xlagunas.core.OpenForTesting
import cat.xlagunas.core.common.displayMessage
import cat.xlagunas.core.common.viewModelProviderFactory
import cat.xlagunas.user.R
import cat.xlagunas.user.User
import cat.xlagunas.user.databinding.ActivityRegisterBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import timber.log.Timber

@OpenForTesting
class RegisterFragment : Fragment() {

    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var actionButton: FloatingActionButton
    private var _binding: ActivityRegisterBinding? = null
    private val binding: ActivityRegisterBinding
        get() = _binding!!

    private val userBinder = RegisterUserBinder()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        registerViewModel = ViewModelProvider(this, viewModelProviderFactory())
            .get(RegisterViewModel::class.java)
        registerViewModel.onRegistration.observe(
            viewLifecycleOwner,
            Observer(this::handleRegistration)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ActivityRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        actionButton = binding.fab

        setupBinding()

        actionButton.setOnClickListener {
            registerViewModel.register(RegisterUserConverter().toUser(userBinder))
        }
    }

    private fun setupBinding() {
        binding.content.firstNameEt.doAfterTextChanged { userBinder.firstName = it.toString() }
        binding.content.lastName.doAfterTextChanged { userBinder.lastName = it.toString() }
        binding.content.email.doAfterTextChanged { userBinder.email = it.toString() }
        binding.content.username.doAfterTextChanged { userBinder.username = it.toString() }
        binding.content.password.doAfterTextChanged { userBinder.password = it.toString() }
    }

    private fun handleUserAlreadyLoggedIn(user: User?) {
        Timber.d("User logged in ${user?.username}")
    }

    private fun handleRegistration(registrationState: RegistrationState) {
        when (registrationState) {

            is RegistrationError -> handleError(registrationState.message)
            is Success -> displayMessage(
                requireContext().getString(
                    R.string.registration_successful,
                    userBinder.username
                )
            )
        }
        actionButton.isEnabled = true
    }

    private fun handleError(message: String?) {
        val errorMsg = "Error registering user $message"
        Timber.e(errorMsg)
        displayMessage(errorMsg)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
