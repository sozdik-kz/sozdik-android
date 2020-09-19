package kz.sozdik.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class SectionsPagerAdapter<T : Fragment>(
    private val fragmentItems: List<FragmentItem<T>>,
    fm: FragmentManager
) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): T = fragmentItems[position].fragment

    override fun getCount(): Int = fragmentItems.size

    override fun getPageTitle(position: Int): CharSequence? = fragmentItems[position].tabTitle
}

data class FragmentItem<T : Fragment>(
    val fragment: T,
    val tabTitle: String
)