package kz.sozdik.base

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.akexorcist.localizationactivity.core.LocalizationActivityDelegate
import com.akexorcist.localizationactivity.core.OnLocaleChangedListener
import moxy.MvpAppCompatActivity

abstract class BaseActivity : MvpAppCompatActivity(), OnLocaleChangedListener {

    protected open val displayHomeAsUpEnabled: Boolean = false

    protected abstract val layoutId: Int

    private var toast: Toast? = null

    private val localizationDelegate = LocalizationActivityDelegate(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        localizationDelegate.addOnLocaleChangedListener(this)
        localizationDelegate.onCreate(savedInstanceState)
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
    }

    fun showToast(message: String) {
        toast?.cancel()
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast?.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        localizationDelegate.onResume(this)
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(localizationDelegate.attachBaseContext(newBase))
    }

    override fun getApplicationContext(): Context =
        localizationDelegate.getApplicationContext(super.getApplicationContext())

    override fun getResources(): Resources =
        localizationDelegate.getResources(super.getResources())

    fun setLanguage(language: String) {
        localizationDelegate.setLanguage(this, language)
    }

    override fun onBeforeLocaleChanged() {}

    override fun onAfterLocaleChanged() {}
}