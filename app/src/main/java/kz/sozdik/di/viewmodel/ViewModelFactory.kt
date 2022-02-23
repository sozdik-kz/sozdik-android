package kz.sozdik.di.viewmodel

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

class ViewModelFactory<T : ViewModel> @Inject constructor(
    private val viewModelProvider: Provider<T>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModelProvider.get() as T
    }

    inline fun <reified T : ViewModel> createViewModel(fragment: Fragment): T {
        return ViewModelProvider(fragment, this).get(T::class.java)
    }
}