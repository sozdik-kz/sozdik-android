package kz.sozdik.test

import androidx.annotation.IntegerRes
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import kz.sozdik.R
import kz.sozdik.extenssions.disableWifiAndMobileData
import kz.sozdik.extenssions.enableWifiAndMobileData
import kz.sozdik.extenssions.scrollToAndClick
import kz.sozdik.main.MainActivity
import kz.sozdik.screen.FeedbackScreen
import kz.sozdik.views.toastWithTextIsDisplayed
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val NETWORK_ESTABLISHMENT_DELAY = 5_000L

@RunWith(AndroidJUnit4::class)
class FeedbackTest : TestCase() {

    @get:Rule
    val activityTestRule = ActivityTestRule(MainActivity::class.java, true, false)

    /*@Test
    fun test() {
        before {
            activityTestRule.launchActivity(null)
        }.after {
        }.run {
            step("Check initial state") {
                FeedbackScreen {
                    nameEditText.hasEmptyText()
                    emailEditText.hasEmptyText()
                    messageEditText.hasEmptyText()

                    nameEditText.isDisplayed()
                    emailEditText.isDisplayed()
                    messageEditText.isDisplayed()
                    sendButton.isDisplayed()
                    progressBar.isNotDisplayed()
                }
            }

            step("Click on Send button with empty name") {
                FeedbackScreen {
                    sendButton.scrollToAndClick()
                    nameInputLayout.hasError(getString(R.string.feedback_error_empty_name))
                    progressBar.isNotDisplayed()
                }
            }

            step("Click on Send button with empty email") {
                FeedbackScreen {
                    nameEditText.typeText("Android Test")
                    sendButton.scrollToAndClick()
                    emailInputLayout.hasError(getString(R.string.feedback_error_wrong_email))
                    progressBar.isNotDisplayed()
                }
            }

            step("Click on Send button with wrong email") {
                FeedbackScreen {
                    emailEditText.typeText("wrong email")
                    sendButton.scrollToAndClick()
                    emailInputLayout.hasError(getString(R.string.feedback_error_wrong_email))
                    progressBar.isNotDisplayed()
                }
            }

            step("Click on Send button with empty message") {
                FeedbackScreen {
                    emailEditText.replaceText("test.sozdik.android@gmail.com")
                    sendButton.scrollToAndClick()
                    messageInputLayout.hasError(getString(R.string.feedback_error_empty_text))
                    progressBar.isNotDisplayed()
                }
            }

            step("Click on Send button without internet connection") {
                FeedbackScreen {
                    device.network.disableWifiAndMobileData()
                    Screen.idle(NETWORK_ESTABLISHMENT_DELAY)

                    messageEditText.replaceText("Test without Internet connection")
                    sendButton.scrollToAndClick()
                    toastWithTextIsDisplayed(getString(R.string.error_no_internet_connection))
                    progressBar.isNotDisplayed()
                    sendButton.isDisplayed()
                    flakySafely(timeoutMs = 1000) { sendButton.isDisplayed() }
                }
            }

            step("Click on Send button with valid fields") {
                FeedbackScreen {
                    device.network.enableWifiAndMobileData()
                    Screen.idle(NETWORK_ESTABLISHMENT_DELAY)

                    messageEditText.replaceText("This is UI test. Ignore it")
                    closeSoftKeyboard()
                    sendButton.scrollToAndClick()
                    progressBar.isDisplayed()
                    sendButton.isNotDisplayed()
                    toastWithTextIsDisplayed(getString(R.string.feedback_message_sent))
                }
            }
        }
    }*/

    private fun getString(@IntegerRes resId: Int): String = activityTestRule.activity.getString(resId)
}