package kz.sozdik.screen

import com.agoda.kakao.common.views.KView
import com.agoda.kakao.pager.KViewPager
import com.agoda.kakao.progress.KProgressBar
import com.agoda.kakao.text.KButton
import com.agoda.kakao.text.KTextView
import kz.sozdik.R
import kz.sozdik.dictionary.presentation.DictionaryFragment

object DictionaryScreen : KScreen<DictionaryScreen>() {

    override val layoutId: Int? = R.layout.fragment_dictionary
    override val viewClass: Class<*>? = DictionaryFragment::class.java

    val kazCharsView = KView { withId(R.id.kazCharsView) }
    val searchButton = KButton { withId(R.id.searchButton) }
    val hintTextView = KTextView { withId(R.id.hintTextView) }
    val progressBar = KProgressBar { withId(R.id.progressBar) }
    val viewPager = KViewPager { withId(R.id.viewPager) }
    val pageIndicator = KView { withId(R.id.pageIndicator) }
}