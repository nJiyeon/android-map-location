package campus.tech.kakao.map.viewTest

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import campus.tech.kakao.map.MainActivity
import campus.tech.kakao.map.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testSearchEditText_click_opensSearchActivity() {
        onView(withId(R.id.search_edit_text)).perform(click())
        onView(withId(R.id.search_text_input)).check(matches(isDisplayed()))
    }

    @Test
    fun testMapError_displaysErrorLayout() {
        // 에러 고의로 발생시켜서 확인
        activityScenarioRule.scenario.onActivity { activity ->
            activity.showErrorScreen(Exception("Test error"))
        }
        onView(withId(R.id.error_layout)).check(matches(isDisplayed()))
        onView(withId(R.id.error_message)).check(matches(withText(R.string.map_error_message)))
    }
}
