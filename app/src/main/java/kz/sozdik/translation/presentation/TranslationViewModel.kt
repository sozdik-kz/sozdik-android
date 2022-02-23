package kz.sozdik.translation.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kz.sozdik.R
import kz.sozdik.core.system.ResourceManager
import kz.sozdik.core.utils.Lang
import kz.sozdik.main.KeyboardState
import kz.sozdik.presentation.utils.ErrorMessageFactory
import kz.sozdik.translation.domain.TranslateInteractor
import javax.inject.Inject

class TranslationViewModel @Inject constructor(
    private val translateInteractor: TranslateInteractor,
    private val resourceManager: ResourceManager
) : ViewModel() {

    private var currentLangFrom = Lang.RUSSIAN
    private var currentState = KeyboardState.CLOSED

    private val _showKzLettersLiveData = MutableLiveData<Boolean>()
    val showKzLettersLiveData: LiveData<Boolean> = _showKzLettersLiveData

    private val _showTranslateViewLiveData = MutableLiveData<Boolean>()
    val showTranslateViewLiveData: LiveData<Boolean> = _showTranslateViewLiveData

    private val _translationCourseTitleLiveData = MutableLiveData<String>()
    val translationCourseTitleLiveData: LiveData<String> = _translationCourseTitleLiveData

    private val _translationResultLiveData = MutableLiveData<String>()
    val translationResultLiveData: LiveData<String> = _translationResultLiveData

    private val _errorMessageLiveData = MutableLiveData<String>()
    val errorMessageLiveData: LiveData<String> = _errorMessageLiveData

    fun translate(text: String) {
        if (text.isBlank()) {
            _errorMessageLiveData.value = resourceManager.getString(R.string.translate_error_empty_text)
            return
        }
        viewModelScope.launch {
            try {
                val langTo = if (currentLangFrom == Lang.KAZAKH_CYRILLIC) Lang.RUSSIAN else Lang.KAZAKH_CYRILLIC
                val translation = translateInteractor.translate(currentLangFrom, langTo, text)
                _translationResultLiveData.value = translation
            } catch (e: Throwable) {
                _errorMessageLiveData.value = ErrorMessageFactory.create(resourceManager, e)
            }
        }
    }

    fun toggleTranslationCourse() {
        when (currentLangFrom) {
            Lang.KAZAKH_CYRILLIC -> {
                currentLangFrom = Lang.RUSSIAN
                _translationCourseTitleLiveData.value = resourceManager.getString(R.string.translate_course_ru_kk)
            }
            Lang.RUSSIAN -> {
                currentLangFrom = Lang.KAZAKH_CYRILLIC
                _translationCourseTitleLiveData.value = resourceManager.getString(R.string.translate_course_kk_ru)
            }
        }
        showKazakhLettersIfAvailable()
    }

    fun onKeyboardStateChanged(newState: KeyboardState) {
        currentState = newState
        _showTranslateViewLiveData.value = currentState == KeyboardState.CLOSED
        showKazakhLettersIfAvailable()
    }

    fun onClearPressed() {
        _translationResultLiveData.value = ""
    }

    private fun showKazakhLettersIfAvailable() {
        _showKzLettersLiveData.value = currentState == KeyboardState.OPEN && currentLangFrom == Lang.KAZAKH_CYRILLIC
    }
}