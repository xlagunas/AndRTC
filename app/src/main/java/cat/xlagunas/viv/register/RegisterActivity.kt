package cat.xlagunas.viv.register

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import cat.xlagunas.domain.commons.User
import cat.xlagunas.viv.R
import cat.xlagunas.viv.commons.di.VivApplication
import cat.xlagunas.viv.databinding.ActivityRegisterBinding
import timber.log.Timber

class RegisterActivity : AppCompatActivity() {

    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var actionButton: FloatingActionButton
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerViewModel = ViewModelProviders.of(this, (application as VivApplication).viewModelFactory).get(RegisterViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        binding.user = RegisterUserBinder()

        registerViewModel.findUser().observe(this, Observer<User> {
            Timber.d("User logged in %s", it?.username)
        })
        actionButton = findViewById(R.id.fab)
        actionButton.setOnClickListener {
            registerViewModel
                    .register(RegisterUserConverter().toUser(binding.user!!))
                    .doOnSubscribe { actionButton.isEnabled = false }
                    .subscribe({
                        showToast()
                        actionButton.isEnabled = true
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
}