package cat.xlagunas.viv.commons

import androidx.test.rule.ActivityTestRule
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import cat.xlagunas.viv.R
import org.junit.Assert

class FragmentTestRule<A : AppCompatActivity, F : androidx.fragment.app.Fragment>(
    activityClass: Class<A>,
    private val fragmentClass: Class<F>
) : ActivityTestRule<A>(activityClass, true, false) {
    var fragment: F? = null
        private set

    override fun beforeActivityLaunched() {
        super.beforeActivityLaunched()

        try {
            fragment = fragmentClass.newInstance()
        } catch (e: InstantiationException) {
            Assert.fail(
                String.format(
                    "%s: Could not insert %s into TestActivity: %s", javaClass.simpleName,
                    fragmentClass.simpleName, e.message
                )
            )
        } catch (e: IllegalAccessException) {
            Assert.fail(
                String.format(
                    "%s: Could not insert %s into TestActivity: %s",
                    javaClass.simpleName,
                    fragmentClass.simpleName,
                    e.message
                )
            )
        }
    }

    override fun afterActivityLaunched() {
        super.afterActivityLaunched()

        activity.runOnUiThread {
            val manager = activity.supportFragmentManager
            val transaction = manager.beginTransaction()
            transaction.replace(R.id.my_nav_host_fragment, fragment!!)
            transaction.commit()
        }
    }
}