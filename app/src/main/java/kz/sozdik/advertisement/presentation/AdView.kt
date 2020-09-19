package kz.sozdik.advertisement.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import kz.sozdik.R
import kz.sozdik.advertisement.DaggerAdvertPresenterComponent
import kz.sozdik.advertisement.domain.model.Banner
import kz.sozdik.di.getAppDepsProvider
import kz.sozdik.main.AdvertView
import kz.sozdik.main.AdvertView.AdProvider
import moxy.MvpDelegate
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class AdView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr),
    AdvertView {

    private var parentDelegate: MvpDelegate<Any>? = null
    private var mvpDelegate: MvpDelegate<Any>? = null

    @InjectPresenter
    lateinit var presenter: AdvertPresenter

    private var customAdView: SimpleDraweeView

    init {
        val view = View.inflate(context, R.layout.ad_wrapper, null)
        customAdView = view.findViewById(R.id.customAdView)
        addView(view)
    }

    @ProvidePresenter
    fun provideAdvertPresenter(): AdvertPresenter =
        DaggerAdvertPresenterComponent.builder()
            .appDependency(context.getAppDepsProvider())
            .build()
            .getAdvertPresenter()

    fun initWidget(parentDelegate: MvpDelegate<Any>) {
        this.parentDelegate = parentDelegate

        getMvpDelegate().onCreate()
        getMvpDelegate().onAttach()
    }

    fun setIsAdDisabled(isAdDisabled: Boolean) {
        presenter.setIsAdDisabled(isAdDisabled)
    }

    private fun getMvpDelegate(): MvpDelegate<Any> {
        if (mvpDelegate == null) {
            mvpDelegate = MvpDelegate(this)
            mvpDelegate?.setParentDelegate(parentDelegate, "AdView")
        }
        return mvpDelegate!!
    }

    override fun showCustomAdView(banner: Banner) {
        presenter.onAdLoaded(AdProvider.CUSTOM)

        val controller = Fresco.newDraweeControllerBuilder()
            .setUri(banner.imageUrl)
            .setAutoPlayAnimations(true)
            .build()
        customAdView.controller = controller
        customAdView.setOnClickListener {
            Answers.getInstance().logCustom(CustomEvent("Banner click. Custom"))
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(banner.redirectUrl)))
        }
    }

    override fun hideAllAdViews() {
        customAdView.isVisible = false
    }

    override fun changeAdViewVisibility(provider: AdProvider, isVisible: Boolean) {
        when (provider) {
            AdProvider.CUSTOM -> customAdView.isVisible = isVisible
        }
    }
}