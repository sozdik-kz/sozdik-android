package kz.sozdik.test

import androidx.annotation.IntegerRes
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import kz.sozdik.R
import kz.sozdik.register.presentation.registration.RegistrationFragment
import kz.sozdik.screen.RegistrationScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegistrationTest : TestCase() {

    @get:Rule
    val activityTestRule = ActivityTestRule(RegistrationFragment::class.java, true, false)

    /*
    TODO Add tests for:
     1) click on terms of use TextView
     2) click on privacy policy TextView
     3) click on login button
     4) Successful registration
    */
    @Test
    fun test() {
        before {
            activityTestRule.launchActivity(null)
        }.after {
        }.run {
            step("Check initial state") {
                RegistrationScreen {
                    firstNameEditText.hasEmptyText()
                    lastNameEditText.hasEmptyText()
                    emailEditText.hasEmptyText()
                    passwordEditText.hasEmptyText()

                    termsOfUseCheckBox.isChecked()
                    privacyPolicyCheckBox.isChecked()

                    firstNameEditText.isDisplayed()
                    lastNameEditText.isDisplayed()
                    emailEditText.isDisplayed()
                    passwordEditText.isDisplayed()
                    loginButton.isDisplayed()
                    registerButton.isDisplayed()
                    termsOfUseCheckBox.isDisplayed()
                    termsOfUseTextView.isDisplayed()
                    privacyPolicyCheckBox.isDisplayed()
                    privacyPolicyTextView.isDisplayed()

                    progressBar.isNotDisplayed()
                }
            }

            step("Check Register button disabled if checkboxes aren't checked") {
                RegistrationScreen {
                    termsOfUseCheckBox.setChecked(false)
                    registerButton.isDisabled()
                    termsOfUseCheckBox.setChecked(true)

                    privacyPolicyCheckBox.setChecked(false)
                    registerButton.isDisabled()
                    privacyPolicyCheckBox.setChecked(true)

                    termsOfUseCheckBox.setChecked(false)
                    privacyPolicyCheckBox.setChecked(false)
                    registerButton.isDisabled()

                    termsOfUseCheckBox.setChecked(true)
                    privacyPolicyCheckBox.setChecked(true)
                    registerButton.isEnabled()
                }
            }

            step("Click on Register button with empty first name") {
                RegistrationScreen {
                    registerButton.click()
                    firstNameInputLayout.hasError(getString(R.string.register_error_wrong_first_name))
                }
            }

            step("Click on Register button with empty last name") {
                RegistrationScreen {
                    firstNameEditText.typeText("FirstName")
                    registerButton.click()
                    lastNameInputLayout.hasError(getString(R.string.register_error_wrong_last_name))
                }
            }

            step("Click on Register button with empty email") {
                RegistrationScreen {
                    lastNameEditText.typeText("LastName")
                    registerButton.click()
                    emailInputLayout.hasError(getString(R.string.register_error_wrong_email))
                    progressBar.isNotDisplayed()
                }
            }

            step("Click on Register button with wrong email") {
                RegistrationScreen {
                    emailEditText.typeText("wrong email")
                    registerButton.click()
                    emailInputLayout.hasError(getString(R.string.register_error_wrong_email))
                    progressBar.isNotDisplayed()
                }
            }

            step("Click on Register button with empty password") {
                RegistrationScreen {
                    emailEditText.replaceText("test@email.com")
                    registerButton.click()
                    passwordInputLayout.hasError(getString(R.string.register_error_wrong_password))
                    progressBar.isNotDisplayed()
                }
            }

            step("Click on Register button with wrong password") {
                RegistrationScreen {
                    passwordEditText.typeText("short")
                    registerButton.click()
                    passwordInputLayout.hasError(getString(R.string.register_error_wrong_password))
                    progressBar.isNotDisplayed()
                }
            }
        }
    }

    private fun getString(@IntegerRes resId: Int): String = activityTestRule.activity.getString(resId)
}