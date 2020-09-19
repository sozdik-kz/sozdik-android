package kz.sozdik.main

import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.SparseArray
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import androidx.core.os.bundleOf
import androidx.core.util.containsKey
import androidx.core.util.set
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import kotlinx.android.synthetic.main.fragment_main.bottomNavigationView
import kotlinx.android.synthetic.main.fragment_main.rootView
import kz.sozdik.R
import kz.sozdik.dictionary.domain.model.Word
import kz.sozdik.dictionary.presentation.DictionaryFragment
import kz.sozdik.favorites.presentation.FavoritesPagerFragment
import kz.sozdik.history.presentation.HistoryPagerFragment
import kz.sozdik.main.KeyboardState.CLOSED
import kz.sozdik.main.KeyboardState.OPEN
import kz.sozdik.main.KeyboardState.UNKNOWN
import kz.sozdik.profile.presentation.ProfileFragment
import kz.sozdik.translation.presentation.TranslateFragment
import timber.log.Timber

private const val ARG_SELECTED_FRAGMENT_ID = "ARG_SELECTED_FRAGMENT_ID"

class MainFragment :
    Fragment(R.layout.fragment_main),
    WordHandler {

    private var wordToDisplay: Word? = null

    private val fragments = SparseArray<Fragment>()
    private var selectedFragmentId = R.id.nav_dictionary

    private var heightDiff = 0
    private var currentKeyboardState = UNKNOWN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedFragmentId = savedInstanceState?.getInt(ARG_SELECTED_FRAGMENT_ID)
            ?: R.id.nav_dictionary
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomNavigationView.setOnNavigationItemSelectedListener {
            navigate(it.itemId)
            return@setOnNavigationItemSelectedListener true
        }
        bottomNavigationView.selectedItemId = selectedFragmentId
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(ARG_SELECTED_FRAGMENT_ID, selectedFragmentId)
        super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        rootView.viewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener)
    }

    override fun onPause() {
        super.onPause()
        rootView.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalLayoutListener)
    }

    override fun showWordTranslation(word: Word) {
        wordToDisplay = word
        bottomNavigationView.selectedItemId = R.id.nav_dictionary
        wordToDisplay = null
    }

    private fun navigate(itemId: Int) {
        if (!fragments.containsKey(itemId)) {
            fragments[itemId] = when (itemId) {
                R.id.nav_dictionary -> DictionaryFragment.create(wordToDisplay)
                R.id.nav_history -> HistoryPagerFragment()
                R.id.nav_favorites -> FavoritesPagerFragment()
                R.id.nav_translate -> TranslateFragment()
                R.id.nav_profile -> ProfileFragment()
                else -> throw IllegalArgumentException("unknown item id")
            }
        }

        val prevFragmentId = selectedFragmentId
        selectedFragmentId = itemId
        childFragmentManager.commit(allowStateLoss = true) {
            if (prevFragmentId != selectedFragmentId) {
                hide(fragments[prevFragmentId])
                setMaxLifecycle(fragments[prevFragmentId], Lifecycle.State.CREATED)
            }
            val nextFragmentTag = fragments[itemId].javaClass.name + "_" + itemId
            if (childFragmentManager.findFragmentByTag(nextFragmentTag) == null) {
                add(R.id.fragmentContainer, fragments[itemId], nextFragmentTag)
            } else {
                val fragment = fragments[itemId]
                if (fragment is DictionaryFragment) {
                    fragment.arguments = bundleOf(DictionaryFragment.ARGUMENT_WORD to wordToDisplay)
                }
                show(fragment)
                setMaxLifecycle(fragment, Lifecycle.State.RESUMED)
            }
        }
    }

    private fun notifyKeyboardListeners(state: KeyboardState) {
        val currentFragment = fragments[selectedFragmentId] as? KeyboardStateListener
        currentFragment?.onKeyboardStateChanged(state)
    }

    @Suppress("MagicNumber")
    // TODO: Check if it can be replaced by keyboard insets
    private val onGlobalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        val rect = Rect()
        rootView.getWindowVisibleDisplayFrame(rect)
        heightDiff = rootView.rootView.height - (rect.bottom - rect.top)
        var maxDiff = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            50f,
            resources.displayMetrics
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val buttonBarHeight = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                48f,
                resources.displayMetrics
            )
            maxDiff += buttonBarHeight
        }
        val keyboardState = if (heightDiff > maxDiff) OPEN else CLOSED
        if (currentKeyboardState != keyboardState) {
            Timber.d("New KeyboardState = $keyboardState")

            currentKeyboardState = keyboardState
            notifyKeyboardListeners(keyboardState)
            bottomNavigationView.isVisible = keyboardState == CLOSED
        }
    }
}

interface WordHandler {
    fun showWordTranslation(word: Word)
}

interface KeyboardStateListener {
    fun onKeyboardStateChanged(state: KeyboardState)
}

enum class KeyboardState {
    UNKNOWN, OPEN, CLOSED
}