package com.example.storyapp.ui.upload

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.example.storyapp.MainActivity
import com.example.storyapp.R
import com.example.storyapp.data.remote.api.APIConfig
import com.example.storyapp.utils.EspressoIdlingResource
import com.example.storyapp.utils.JsonConverter
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class UploadActivityTest {
    private val mockWebServer = MockWebServer()

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val device = UiDevice.getInstance(instrumentation)
    @Before
    fun setUp() {
        mockWebServer.start(8080)
        APIConfig.BASE_URL = "http://127.0.0.1:8080/"
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        ActivityScenario.launch(UploadActivity::class.java)
    }
    
    @Test
    fun testUpload_Success() {
        Espresso.onView(ViewMatchers.withId(R.id.ed_add_description)).perform(ViewActions.typeText("Test Description"))
        Espresso.onView(ViewMatchers.withId(R.id.btn_camera)).perform(ViewActions.click())
        executeUiAutomatorActions(device, CAMERA_BUTTON_SHUTTER_ID, CAMERA_BUTTON_DONE_ID)
        
        Espresso.onView(ViewMatchers.withId(R.id.button_add)).perform(ViewActions.click())

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(JsonConverter.readStringFromFile("story_success.json"))
        mockWebServer.enqueue(mockResponse)
    }
    
    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    private fun executeUiAutomatorActions(device: UiDevice, vararg ids: String, actionTimeout: Long = 20000L) {
        for (id in ids) {
            val obj = device.findObject(UiSelector().resourceId(id))
            if (obj.waitForExists(actionTimeout)) {
                obj.click()
            }
        }
    }
    
    companion object {
        const val CAMERA_BUTTON_SHUTTER_ID = "com.android.camera2:id/shutter_button"
        const val CAMERA_BUTTON_DONE_ID = "com.android.camera2:id/done_button"
    }
}