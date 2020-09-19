package kz.sozdik.test

import androidx.annotation.IntegerRes
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import kz.sozdik.R
import kz.sozdik.login.presentation.LoginActivity
import kz.sozdik.screen.LoginScreen
import kz.sozdik.views.toastWithTextIsDisplayed
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginTest : TestCase() {

    // TODO: Fix test

    @get:Rule
    val activityTestRule = ActivityTestRule(LoginActivity::class.java, true, false)

    @Test
    fun test() {
        before {
            activityTestRule.launchActivity(null)
        }.after {
        }.run {
            step("Check initial state") {
                LoginScreen {
                    emailEditText.hasEmptyText()
                    passwordEditText.hasEmptyText()

                    emailEditText.isDisplayed()
                    passwordEditText.isDisplayed()
                    loginButton.isDisplayed()
                    googleSignInButton.isDisplayed()
                    facebookSignInButton.isDisplayed()
                    progressBar.isNotDisplayed()
                }
            }

            step("Click on Login button with empty email") {
                LoginScreen {
                    loginButton.click()
                    emailInputLayout.hasError(getString(R.string.login_error_wrong_email))
                }
            }

            step("Click on Login button with wrong email") {
                LoginScreen {
                    emailEditText.typeText("wrong email")
                    loginButton.click()
                    emailInputLayout.hasError(getString(R.string.login_error_wrong_email))
                    progressBar.isNotDisplayed()
                }
            }

            step("Click on Login button with empty password") {
                LoginScreen {
                    emailEditText.replaceText("test@email.com")
                    loginButton.click()
                    passwordInputLayout.hasError(getString(R.string.login_error_empty_password))
                }
            }

            step("Click on Login button with wrong credentials") {
                LoginScreen {
                    passwordEditText.typeText("wrong_password")
                    loginButton.click()
                    progressBar.isDisplayed()
                    loginButton.isNotDisplayed()
                    toastWithTextIsDisplayed(getString(R.string.login_error_wrong_email_or_password))
                    flakySafely(timeoutMs = 3000) { loginButton.isDisplayed() }
                }
            }
        }
    }

    private fun getString(@IntegerRes resId: Int): String = activityTestRule.activity.getString(resId)
}