package cat.xlagunas.viv.register

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import cat.xlagunas.data.OpenForTesting
import cat.xlagunas.domain.commons.User
import cat.xlagunas.viv.R
import cat.xlagunas.viv.commons.di.Injectable
import cat.xlagunas.viv.databinding.ActivityRegisterBinding
import timber.log.Timber
import javax.inject.Inject

@OpenForTesting
class RegisterFragment : Fragment(), Injectable {

    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var actionButton: FloatingActionButton
    private lateinit var binding: ActivityRegisterBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        registerViewModel = ViewModelProviders.of(this, viewModelFactory).get(RegisterViewModel::class.java)
        registerViewModel.findUser().observe(this, Observer<User> {
            Timber.d("User logged in %s", it?.username)
        })
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
            registerViewModel
                .register(RegisterUserConverter().toUser(binding.user!!))
                .doOnSubscribe { actionButton.isEnabled = false }
                .subscribe({
                    showToast()
                    actionButton.isEnabled = true
                    navController().navigate(R.id.action_register_successful)
                }, { error ->
                    Timber.e(error, "Network error")
                    actionButton.isEnabled = true
                    showErrorToast()
                })
        }
    }

    private fun showToast() {
        Snackbar.make(binding.root, "User registered ", Snackbar.LENGTH_LONG).show()
    }

    private fun showErrorToast() {
        Snackbar.make(binding.root, "Error registering user", Snackbar.LENGTH_LONG).show()
    }

    fun navController() = findNavController()
}