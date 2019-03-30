package cat.xlagunas.viv.commons.di

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import cat.xlagunas.viv.commons.TestApplication

class MockTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
        return super.newApplication(cl, TestApplication::class.java.name, context)
    }
}