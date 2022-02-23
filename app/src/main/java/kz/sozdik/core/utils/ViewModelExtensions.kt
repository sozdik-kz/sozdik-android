package kz.sozdik.core.utils

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

fun <T : ViewModel> Fragment.getViewModel(
    modelClass: Class<T>,
    viewModelFactory: ViewModelProvider.Factory? = null
): T {
    val viewModelProvider = viewModelFactory?.let {
        ViewModelProvider(this, it)
    } ?: ViewModelProvider(this)
    return viewModelProvider.get(modelClass)
}