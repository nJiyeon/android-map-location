package campus.tech.kakao.map.viewTest

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import campus.tech.kakao.map.R
import campus.tech.kakao.map.view.SearchActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchActivityTest {

    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(SearchActivity::class.java)

    @Test
    fun testSearchTextInput_typing_displaysResults() {
        onView(withId(R.id.search_text_input)).perform(typeText("카페"))
        onView(withId(R.id.search_result_view)).check(matches(isDisplayed()))
    }

    @Test
    fun testDeleteTextInput_click_clearsSearchText() {
        onView(withId(R.id.search_text_input)).perform(typeText("카페"))
        onView(withId(R.id.delete_text_input)).perform(click())
        onView(withId(R.id.search_text_input)).check(matches(withText("")))
    }

    @Test
    fun testKeywordHistoryView_click_displaysKeywordInSearchTextInput() {
        // 미리 검색어를 추가하여 테스트
        activityScenarioRule.scenario.onActivity { activity ->
            activity.keywordViewModel.saveKeyword("카페")
        }
        onView(withId(R.id.keyword_history_view)).perform(click())
        onView(withId(R.id.search_text_input)).check(matches(withText("카페")))
    }
}