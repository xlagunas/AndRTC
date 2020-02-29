package cat.xlagunas.viv.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import cat.xlagunas.core.OpenForTesting
import cat.xlagunas.core.data.di.viewModelProviderFactory
import di.Injectable
import cat.xlagunas.core.domain.entity.User
import cat.xlagunas.viv.R
import cat.xlagunas.viv.databinding.ActivityRegisterBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_LONG
import timber.log.Timber

@OpenForTesting
class RegisterFragment : androidx.fragment.app.Fragment(), Injectable {

    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var actionButton: com.google.android.material.floatingactionbutton.FloatingActionButton
    private lateinit var binding: ActivityRegisterBinding

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        registerViewModel = ViewModelProviders.of(this, viewModelProviderFactory()).get(RegisterViewModel::class.java)
        registerViewModel.findUser().observe(this, Observer(this::handleUserAlreadyLoggedIn))
        registerViewModel.registrationState.observe(this, Observer(this::handleRegistration))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_register, container, false)
        binding.user = RegisterUserBinder()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        actionButton = view.findViewById(R.id.fab)
        actionButton.setOnClickListener {
            registerViewModel.register(RegisterUserConverter().toUser(binding.user!!))
        }
    }

    private fun handleNavigationSuccess() {
        Snackbar.make(binding.root, "User registered ", LENGTH_LONG).show()
        navController().navigate(R.id.action_register_successful)
    }

    private fun handleUserAlreadyLoggedIn(user: User?) {
        Timber.d("User logged in ${user?.username}")
    }

    private fun handleRegistration(registrationState: RegistrationState?) {
        when (registrationState) {
            is RegistrationError -> handleError(registrationState.message)
            is Success -> handleNavigationSuccess()
        }
        actionButton.isEnabled = true
    }

    private fun handleError(message: String?) {
        val errorMsg = "Error registering user $message"
        Timber.e(errorMsg)
        Snackbar.make(binding.root, errorMsg, LENGTH_LONG).show()
    }

    fun navController() = findNavController()
}