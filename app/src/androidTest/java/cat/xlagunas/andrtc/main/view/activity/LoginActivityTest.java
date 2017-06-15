package cat.xlagunas.andrtc.main.view.activity;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.view.activity.LoginActivity;
import cat.xlagunas.andrtc.view.activity.RegisterActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;

/**
 * Created by xlagunas on 2/5/17.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginActivityTest {

    @Rule
    public IntentsTestRule<LoginActivity> activityRule = new IntentsTestRule<>(
            LoginActivity.class);

    @Test
    public void whenRegisterClicked_thenRegisterActivity() {
        onView(ViewMatchers.withId(R.id.sign_in)).perform(click());

        intended(hasComponent(RegisterActivity.class.getName()));
    }


}
