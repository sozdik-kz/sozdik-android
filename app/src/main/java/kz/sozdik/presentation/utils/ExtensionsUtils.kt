package kz.sozdik.presentation.utils

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import kz.sozdik.R
import kotlin.reflect.KClass

fun View.showKeyboard() {
    requestFocus()
    val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(this, 0)
}

fun Fragment.hideKeyboard() {
    context?.let {
        val inputMethodManager = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
    }
}

fun Activity.hideKeyboard() {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
}

fun Context.openInChromeTab(url: String) {
    val builder = CustomTabsIntent.Builder()
    builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
    val customTabsIntent = builder.build()
    customTabsIntent.launchUrl(this, Uri.parse(url))
}

fun FragmentActivity.addFragment(
    fragment: Fragment,
    addToBackStack: Boolean = true,
    tag: String = fragment::class.java.name
) {
    supportFragmentManager.commit {
        add(R.id.container, fragment, tag)
        if (addToBackStack) addToBackStack(tag)
    }
}

fun FragmentActivity.replaceFragment(
    fragment: Fragment,
    addToBackStack: Boolean = true,
    tag: String = fragment::class.java.name
) {
    supportFragmentManager.commit {
        replace(R.id.container, fragment, tag)
        if (addToBackStack) addToBackStack(tag)
    }
}

private const val PREVIOUS_FRAGMENT_TAG_ARG = "PREVIOUS_FRAGMENT_TAG_ARG"

private fun Fragment.getPreviousTag(): String? = arguments?.getString(PREVIOUS_FRAGMENT_TAG_ARG)
fun Fragment.getCurrentScreen(): Fragment? = childFragmentManager.findFragmentById(R.id.fragmentContainer)

fun Fragment.popScreen() {
    requireActivity().hideKeyboard()

    val previousTag = getCurrentScreen()?.getPreviousTag()
    val fragmentManager = parentFragment?.childFragmentManager ?: childFragmentManager

    when {
        previousTag != null -> popScreenTo(previousTag, true)
        fragmentManager.backStackEntryCount < 2 -> requireActivity().popFragment()
        else -> fragmentManager.popBackStackImmediate()
    }
}

fun FragmentActivity.popFragment(): Boolean {
    hideKeyboard()
    return when {
        supportFragmentManager.backStackEntryCount < 2 -> {
            false
        }
        else -> supportFragmentManager.popBackStackImmediate()
    }
}

private fun Fragment.popScreenTo(tag: String, inclusive: Boolean = false) {
    val flag = if (inclusive) FragmentManager.POP_BACK_STACK_INCLUSIVE else 0

    requireActivity().hideKeyboard()

    if (childFragmentManager.backStackEntryCount == 0 ||
        childFragmentManager.getBackStackEntryAt(0).name == tag && inclusive
    ) {
        requireActivity().popFragment()
    } else {
        if (!childFragmentManager.popBackStackImmediate(tag, flag)) requireActivity().popFragment()
    }
}

fun <T : Fragment> FragmentActivity.popScreenTo(target: KClass<T>, inclusive: Boolean = false) {
    popScreenTo(target.java.name, inclusive)
}

private fun FragmentActivity.popScreenTo(tag: String, inclusive: Boolean = false) {
    val flag = if (inclusive) FragmentManager.POP_BACK_STACK_INCLUSIVE else 0

    hideKeyboard()

    if (!supportFragmentManager.popBackStackImmediate(tag, flag)) popFragment()
}

fun Fragment.showToast(@StringRes resId: Int) {
    showToast(getString(resId))
}

fun Fragment.showToast(message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}