package cat.xlagunas.user.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import cat.xlagunas.core.OpenForTesting
import cat.xlagunas.core.common.viewModelProviderFactory
import cat.xlagunas.user.R
import cat.xlagunas.user.User
import cat.xlagunas.user.databinding.ActivityRegisterBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

@OpenForTesting
class RegisterFragment : Fragment() {

    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var actionButton: FloatingActionButton
    private lateinit var binding: ActivityRegisterBinding

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        registerViewModel = ViewModelProviders.of(this, viewModelProviderFactory()).get(RegisterViewModel::class.java)
        registerViewModel.findUser().observe(viewLifecycleOwner, Observer(this::handleUserAlreadyLoggedIn))
        registerViewModel.onRegistrationError.observe(viewLifecycleOwner,
            Observer(this::handleRegistrationError)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

    private fun handleUserAlreadyLoggedIn(user: User?) {
        Timber.d("User logged in ${user?.username}")
    }

    private fun handleRegistrationError(registrationState: RegistrationError) {
        handleError(registrationState.message)
        actionButton.isEnabled = true
    }

    private fun handleError(message: String?) {
        val errorMsg = "Error registering user $message"
        Timber.e(errorMsg)
        Snackbar.make(binding.root, errorMsg, BaseTransientBottomBar.LENGTH_LONG).show()
    }
}
