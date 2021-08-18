package kz.sozdik.core.system

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

class ViewModelFactory<T : ViewModel> @Inject constructor(
    private val viewModel: Provider<T>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModel.get() as T
    }

    inline fun <reified T : ViewModel> createViewModel(fragmentActivity: FragmentActivity): T {
        return ViewModelProvider(fragmentActivity, this).get(T::class.java)
    }

    inline fun <reified T : ViewModel> createViewModel(fragment: Fragment): T {
        return ViewModelProvider(fragment, this).get(T::class.java)
    }
}
