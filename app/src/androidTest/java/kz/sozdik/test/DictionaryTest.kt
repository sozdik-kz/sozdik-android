package kz.sozdik.test

import android.preference.PreferenceManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import kz.sozdik.core.utils.Lang
import kz.sozdik.main.MainActivity
import kz.sozdik.screen.DictionaryScreen
import kz.sozdik.core.system.PrefsManager
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class DictionaryTest : TestCase() {

    @get:Rule
    val activityTestRule = ActivityTestRule(MainActivity::class.java, true, false)

    private val prefsManager by lazy {
        PrefsManager(PreferenceManager.getDefaultSharedPreferences(activityTestRule.activity))
    }

    @Inject
    lateinit var userPref: PrefsManager

    @Test
    fun testInitialState() {
        before {
            activityTestRule.launchActivity(null)
        }.after {
        }.run {
            step("Check initial state") {
                DictionaryScreen {
                    val enabled = userPref.isKazakhLettersEnabled()
                    val lang = userPref.getLanguageFrom()
                    val languageFrom = prefsManager.getLanguageFrom()
                    if (prefsManager.isKazakhLettersEnabled() && Lang.isKazakh(languageFrom)) {
                        kazCharsView.isVisible()
                    } else {
                        kazCharsView.isGone()
                    }
                    searchButton.isVisible()
                }
            }
        }
    }
}