package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.LauncherRepository
import com.example.model.AppId
import com.example.model.AppInstallState
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("HoYo Launcher", appName)
    }

    @Test
    fun `verify launcher repository initialization and states`() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = LauncherRepository(context)

        // Verify available apps
        assertEquals(2, repository.availableApps.size)
        val calc = repository.availableApps.first { it.id == AppId.CALCULATOR }
        assertNotNull(calc)
        assertEquals("Calculadora Neo", calc.name)

        // Reset to initial
        repository.resetAllApps()
        val states = repository.appStates.value
        assertEquals(AppInstallState.NOT_INSTALLED, states[AppId.CALCULATOR])
        assertEquals(AppInstallState.NOT_INSTALLED, states[AppId.NOTEPAD])
    }
}
