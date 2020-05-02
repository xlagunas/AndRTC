package cat.xlagunas.viv

import android.content.Intent
import android.net.Uri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import cat.xlagunas.call.CallConfirmationDialog
import cat.xlagunas.contact.ui.ContactFragment
import cat.xlagunas.core.navigation.Navigator
import cat.xlagunas.user.login.LoginFragment
import cat.xlagunas.user.profile.ProfileFragment
import cat.xlagunas.user.register.RegisterFragment
import timber.log.Timber
import javax.inject.Inject
import kotlin.reflect.KClass

class AndroidNavigator @Inject constructor(private val activityProvider: TopActivityProvider) :
    Navigator {
    override fun startCall(roomId: String) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://viv.cat/conference?roomId=$roomId")
        ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        activityProvider.topActivity?.startActivity(intent)
    }

    override fun navigateToContacts() {
        navigateToFragment(ContactFragment::class)
    }

    override fun navigateToProfile() {
        navigateToFragment(ProfileFragment::class)
    }

    override fun navigateToLogin() {
        navigateToFragment(LoginFragment::class)
    }

    override fun navigateToRegistration() {
        navigateToFragment(RegisterFragment::class, true)
    }

    override fun requestCall(userId: Long, contactName: String) {
        val dialog = CallConfirmationDialog.newInstance(userId, contactName)
        dialog.show(activityProvider.topActivity?.supportFragmentManager!!, "CallConfirmationDialog")
    }

    private fun navigateToFragment(
        fragmentClass: KClass<out Fragment>,
        addToBackStack: Boolean = false
    ) {
        val activity = activityProvider.topActivity
        val fragmentTag = fragmentClass.simpleName

        Timber.d("replacing ${activity?.supportFragmentManager?.findFragmentById(R.id.my_nav_host_fragment)}fragment to ${fragmentClass.simpleName}")
        activity?.supportFragmentManager?.commit {
            replace(R.id.my_nav_host_fragment, fragmentClass.java, bundleOf(), fragmentTag)
            if (addToBackStack) addToBackStack(fragmentTag)
        }
    }
}