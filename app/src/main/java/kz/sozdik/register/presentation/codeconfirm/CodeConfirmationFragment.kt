package kz.sozdik.register.presentation.codeconfirm

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.fragment_code_confirmation.*
import kz.sozdik.R
import kz.sozdik.di.getAppDepsProvider
import kz.sozdik.main.MainFragment
import kz.sozdik.presentation.utils.popScreen
import kz.sozdik.presentation.utils.popScreenTo
import kz.sozdik.presentation.utils.showToast
import kz.sozdik.register.di.DaggerCodeConfirmationPresenterComponent
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

private const val ARG_EMAIL = "ARG_EMAIL"
private const val ARG_CONFIRMATION_TOKEN = "ARG_CONFIRMATION_TOKEN"

class CodeConfirmationFragment :
    MvpAppCompatFragment(R.layout.fragment_code_confirmation),
    CodeConfirmView {

    companion object {
        fun create(email: String, confirmationToken: String): CodeConfirmationFragment =
            CodeConfirmationFragment().apply {
                arguments = bundleOf(
                    ARG_EMAIL to email,
                    ARG_CONFIRMATION_TOKEN to confirmationToken
                )
            }
    }

    @InjectPresenter
    lateinit var presenter: CodeConfirmPresenter

    @ProvidePresenter
    internal fun providePresenter(): CodeConfirmPresenter {
        val confirmationToken = arguments?.getString(ARG_CONFIRMATION_TOKEN) ?: ""
        return DaggerCodeConfirmationPresenterComponent.factory()
            .create(
                appDependency = requireContext().getAppDepsProvider(),
                confirmationToken = confirmationToken
            )
            .getConfirmCodePresenter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setNavigationOnClickListener { popScreen() }
        emailTextView.text = arguments?.getString(ARG_EMAIL)
        confirmButton.setOnClickListener {
            presenter.confirmRegistration(codeEditText.text.toString())
        }
    }

    override fun onSuccess() {
        activity?.popScreenTo(MainFragment::class)
    }

    override fun onError(message: String) {
        showToast(message)
    }

    override fun showViewLoading(isVisible: Boolean) {
        progressBar.isVisible = isVisible
    }

    override fun showButtonConfirm(isVisible: Boolean) {
        confirmButton.isVisible = isVisible
    }
}