package kz.sozdik.views

import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import kz.sozdik.views.viewMatchers.ToastMatcher

fun toastWithTextIsDisplayed(textToastMessage: String) {
    Espresso.onView(ViewMatchers.withText(textToastMessage)).inRoot(ToastMatcher())
        .check(matches(ViewMatchers.isDisplayed()))
}