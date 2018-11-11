package cat.xlagunas.viv.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import cat.xlagunas.core.di.Injectable
import cat.xlagunas.core.di.VivApplication
import cat.xlagunas.core.domain.entity.User
import cat.xlagunas.data.OpenForTesting
import cat.xlagunas.viv.R
import cat.xlagunas.viv.databinding.ActivityRegisterBinding
import dagger.DaggerMonolythComponent
import timber.log.Timber
import javax.inject.Inject

@OpenForTesting
class RegisterFragment : androidx.fragment.app.Fragment(), Injectable {

    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var actionButton: com.google.android.material.floatingactionbutton.FloatingActionButton
    private lateinit var binding: ActivityRegisterBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerMonolythComponent.builder().withParentComponent(VivApplication.appComponent(context!!)).build()
            .inject(this)
    }

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
        return null
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
        //com.google.android.material.snackbar.Snackbar.make(binding.root,"User registered ",com.google.android.material.snackbar.Snackbar.LENGTH_LONG).show()
    }

    private fun showErrorToast() {
        //com.google.android.material.snackbar.Snackbar.make(binding.root,"Error registering user", com.google.android.material.snackbar.Snackbar.LENGTH_LONG).show()
    }

    fun navController() = findNavController()
}