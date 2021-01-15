package kz.sozdik.register.presentation.registration

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.view.View
import android.widget.CompoundButton
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.fragment_dictionary.toolbar
import kotlinx.android.synthetic.main.fragment_registration.continueButton
import kotlinx.android.synthetic.main.fragment_registration.emailEditText
import kotlinx.android.synthetic.main.fragment_registration.emailInputLayout
import kotlinx.android.synthetic.main.fragment_registration.firstNameEditText
import kotlinx.android.synthetic.main.fragment_registration.firstNameInputLayout
import kotlinx.android.synthetic.main.fragment_registration.lastNameEditText
import kotlinx.android.synthetic.main.fragment_registration.lastNameInputLayout
import kotlinx.android.synthetic.main.fragment_registration.loginButton
import kotlinx.android.synthetic.main.fragment_registration.passwordEditText
import kotlinx.android.synthetic.main.fragment_registration.passwordInputLayout
import kotlinx.android.synthetic.main.fragment_registration.privacyPolicyCheckBox
import kotlinx.android.synthetic.main.fragment_registration.progressBar
import kotlinx.android.synthetic.main.fragment_registration.termsOfUseCheckBox
import kz.sozdik.R
import kz.sozdik.di.getAppDepsProvider
import kz.sozdik.login.presentation.LoginFragment
import kz.sozdik.presentation.utils.openInChromeTab
import kz.sozdik.presentation.utils.replaceFragment
import kz.sozdik.presentation.utils.showToast
import kz.sozdik.register.di.DaggerRegistrationPresenterComponent
import kz.sozdik.register.presentation.codeconfirm.CodeConfirmationFragment
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

private const val REQUEST_CODE_CONFIRM = 10
private const val REQUEST_CODE_LOGIN = 20

class RegistrationFragment :
    MvpAppCompatFragment(R.layout.fragment_registration),
    RegistrationView,
    CompoundButton.OnCheckedChangeListener {

    @InjectPresenter
    lateinit var presenter: RegistrationPresenter

    @ProvidePresenter
    internal fun providePresenter(): RegistrationPresenter =
        DaggerRegistrationPresenterComponent.builder()
            .appDependency(requireContext().getAppDepsProvider())
            .build()
            .getRegistrationPresenter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        continueButton.setOnClickListener {
            presenter.register(
                emailEditText.text.toString(),
                passwordEditText.text.toString(),
                firstNameEditText.text.toString(),
                lastNameEditText.text.toString()
            )
        }

        loginButton.setOnClickListener {
            activity?.replaceFragment(LoginFragment.create())
        }

        with(toolbar) {
            setNavigationOnClickListener { activity?.onBackPressed() }
            inflateMenu(R.menu.menu_registration)
            setOnMenuItemClickListener {
                InfoDialogFragment().show(childFragmentManager, "InfoDialogFragment")
                return@setOnMenuItemClickListener true
            }
        }

        var linkText = getString(R.string.register_privacy_policy_link_text)
        var text = getString(R.string.register_privacy_policy)
        var stringBuilder = SpannableStringBuilder(text)
        stringBuilder.setSpan(
            object : URLSpan(getString(R.string.register_privacy_policy_url)) {
                override fun onClick(widget: View) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        widget.cancelPendingInputEvents()
                    }
                    requireContext().openInChromeTab(getString(R.string.register_privacy_policy_url))
                }

                override fun updateDrawState(textPaint: TextPaint) {
                    super.updateDrawState(textPaint)
                    textPaint.isUnderlineText = true
                }
            },
            text.indexOf(linkText),
            text.indexOf(linkText) + linkText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        privacyPolicyCheckBox.text = stringBuilder
        privacyPolicyCheckBox.movementMethod = LinkMovementMethod.getInstance()

        linkText = getString(R.string.register_terms_of_use_link_text)
        text = getString(R.string.register_terms_of_use)
        stringBuilder = SpannableStringBuilder(text)
        stringBuilder.setSpan(
            object : URLSpan(getString(R.string.register_terms_of_use_url)) {
                override fun onClick(widget: View) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        widget.cancelPendingInputEvents()
                    }
                    requireContext().openInChromeTab(getString(R.string.register_terms_of_use_url))
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = true
                }
            },
            text.indexOf(linkText),
            text.indexOf(linkText) + linkText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        termsOfUseCheckBox.text = stringBuilder
        termsOfUseCheckBox.movementMethod = LinkMovementMethod.getInstance()

        privacyPolicyCheckBox.setOnCheckedChangeListener(this)
        termsOfUseCheckBox.setOnCheckedChangeListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_CONFIRM -> activity?.onBackPressed()
                REQUEST_CODE_LOGIN -> activity?.onBackPressed()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun showCodeConfirmationScreen(email: String, confirmationToken: String) {
        activity?.replaceFragment(CodeConfirmationFragment.create(email, confirmationToken))
    }

    override fun onError(message: String) {
        showToast(message)
    }

    override fun showViewLoading(isVisible: Boolean) {
        progressBar.isVisible = isVisible
    }

    override fun showButtonContinue(isVisible: Boolean) {
        continueButton.isVisible = isVisible
    }

    override fun showFirstNameError(message: String?) {
        firstNameInputLayout.error = message
    }

    override fun showLastNameError(message: String?) {
        lastNameInputLayout.error = message
    }

    override fun showEmailError(message: String?) {
        emailInputLayout.error = message
    }

    override fun showPasswordError(message: String?) {
        passwordInputLayout.error = message
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        continueButton.isEnabled = privacyPolicyCheckBox.isChecked && termsOfUseCheckBox.isChecked
    }
}