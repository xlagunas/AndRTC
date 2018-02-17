package cat.xlagunas.viv.login

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
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

    private lateinit var googleViewModel: LoginViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        googleViewModel = ViewModelProviders.of(this, (application as VivApplication).viewModelFactory).get(LoginViewModel::class.java)
        setContentView(R.layout.activity_login)
        setSupportActionBar(toolbar)
        ButterKnife.bind(this)

        lifecycle.addObserver(googleViewModel.registerGoogle())

        signInButton.setOnClickListener { googleViewModel.initGoogleSignIn() }
        googleViewModel .onLoginStateChange()
                .observe(this, Observer<LoginState?> { t: LoginState? -> Timber.d("Login status: %s", t?.isSuccess) })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            googleViewModel.handleLoginResult(GoogleSignIn.getSignedInAccountFromIntent(data))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(googleViewModel.registerGoogle())
    }

}
