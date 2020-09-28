package kz.sozdik.profile.presentation

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.fragment_profile.*
import kz.sozdik.BuildConfig
import kz.sozdik.R
import kz.sozdik.di.getAppDepsProvider
import kz.sozdik.feedback.presentation.FeedbackFragment
import kz.sozdik.login.presentation.LoginFragment
import kz.sozdik.presentation.utils.openInChromeTab
import kz.sozdik.presentation.utils.replaceFragment
import kz.sozdik.profile.di.DaggerProfileComponent
import kz.sozdik.profile.domain.model.Profile
import kz.sozdik.register.presentation.registration.RegistrationFragment
import moxy.MvpAppCompatFragment
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import java.util.Calendar

class ProfileFragment :
    MvpAppCompatFragment(R.layout.fragment_profile),
    ProfileView {

    @InjectPresenter
    lateinit var profilePresenter: ProfilePresenter

    @ProvidePresenter
    internal fun providePresenter(): ProfilePresenter =
        DaggerProfileComponent.builder()
            .appDependency(requireContext().getAppDepsProvider())
            .build()
            .getProfilePresenter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.inflateMenu(R.menu.menu_profile)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_preferences -> activity?.replaceFragment(SettingsFragment())
                R.id.action_logout -> profilePresenter.logout()
            }
            return@setOnMenuItemClickListener true
        }

        loginButton.setOnClickListener {
            activity?.replaceFragment(LoginFragment())
        }

        registerButton.setOnClickListener {
            activity?.replaceFragment(RegistrationFragment())
        }

        rateAppButton.setOnClickListener {
            try {
                val playMarketIntent = Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse("market://details?id=${context?.packageName}"))
                startActivity(playMarketIntent)
            } catch (e: ActivityNotFoundException) {
                context?.openInChromeTab(
                    "https://play.google.com/store/apps/details?id=${context?.packageName}"
                )
            }
        }

        appVersionButton.description = getString(
            R.string.copyright,
            BuildConfig.VERSION_NAME,
            Calendar.getInstance().get(Calendar.YEAR)
        )
        appVersionButton.setOnClickListener {
            DeviceIdDialogFragment().show(childFragmentManager, null)
        }

        shareButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
                .setType("text/plain")
                .putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                .putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text))
            startActivity(intent)
        }

        feedbackButton.setOnClickListener {
            activity?.replaceFragment(FeedbackFragment.create())
        }
    }

    override fun showLoginAndRegisterButtons(isVisible: Boolean) {
        registerButton.isVisible = isVisible
        loginButton.isVisible = isVisible
    }

    override fun showProfileInfo(isVisible: Boolean) {
        profileLayout.isVisible = isVisible
    }

    override fun setProfileInfo(profile: Profile) {
        fullNameTextView.text = profile.fullName
        avatarImageView.setImageURI(profile.avatarUrl)
    }

    override fun showLogoutButton(isVisible: Boolean) {
        toolbar.menu.findItem(R.id.action_logout).isVisible = isVisible
    }
}