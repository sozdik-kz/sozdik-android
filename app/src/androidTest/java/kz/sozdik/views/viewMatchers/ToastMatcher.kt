package kz.sozdik.views.viewMatchers

import android.view.WindowManager
import androidx.test.espresso.Root
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

class ToastMatcher : TypeSafeMatcher<Root>() {
    override fun describeTo(description: Description) {
        description.appendText("check of toast type")
    }

    override fun matchesSafely(root: Root): Boolean {
        val type = root.windowLayoutParams.get().type
        if (type == WindowManager.LayoutParams.TYPE_TOAST) {
            val windowToken = root.decorView.windowToken
            val appToken = root.decorView.applicationWindowToken
            if (windowToken === appToken) {
                return true
                // means this window isn't contained by any other windows.
            }
        }
        return false
    }
}