package kz.sozdik.screen

import io.github.kakaocup.kakao.common.views.KView
import io.github.kakaocup.kakao.pager.KViewPager
import io.github.kakaocup.kakao.progress.KProgressBar
import io.github.kakaocup.kakao.text.KButton
import io.github.kakaocup.kakao.text.KTextView
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
//    val pageIndicator = KView { withId(R.id.pageIndicator) }
}