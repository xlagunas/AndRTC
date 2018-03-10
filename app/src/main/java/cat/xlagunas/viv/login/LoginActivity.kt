package cat.xlagunas.viv.login

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import butterknife.BindView
import butterknife.ButterKnife
import cat.xlagunas.data.common.extensions.text
import cat.xlagunas.data.user.login.GoogleSignInDataSource.Companion.RC_SIGN_IN
import cat.xlagunas.viv.R
import cat.xlagunas.viv.commons.di.VivApplication
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.SignInButton
import kotlinx.android.synthetic.main.activity_login.*
import timber.log.Timber


class LoginActivity : AppCompatActivity() {

    @BindView(R.id.sign_in_button)
    lateinit var signInButton: SignInButton

    @BindView(R.id.username_input_layout)
    lateinit var usernameInputLayout: TextInputLayout

    @BindView(R.id.password_text_input)
    lateinit var passwordInputLayout: TextInputLayout

    @BindView(R.id.login_button)
    lateinit var loginButton: Button

    private lateinit var loginViewModel: LoginViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginViewModel = ViewModelProviders.of(this, (application as VivApplication).viewModelFactory)
                .get(LoginViewModel::class.java)
        setContentView(R.layout.activity_login)
        setSupportActionBar(toolbar)
        ButterKnife.bind(this)

        lifecycle.addObserver(loginViewModel.registerGoogle())

        signInButton.setOnClickListener { loginViewModel.initGoogleSignIn() }
        loginViewModel.onLoginStateChange()
                .observe(this, Observer<LoginState?> { t: LoginState? -> Timber.d("Login status: %s", t?.isSuccess) })

        loginButton.setOnClickListener {
            loginViewModel.login(usernameInputLayout.text(), passwordInputLayout.text())
        }
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

}
