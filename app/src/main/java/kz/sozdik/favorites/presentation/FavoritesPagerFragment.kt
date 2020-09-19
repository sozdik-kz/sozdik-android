package kz.sozdik.favorites.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_pager.tabLayout
import kotlinx.android.synthetic.main.fragment_pager.toolbar
import kotlinx.android.synthetic.main.fragment_pager.viewPager
import kz.sozdik.R
import kz.sozdik.core.utils.Lang
import kz.sozdik.base.FragmentItem
import kz.sozdik.base.SectionsPagerAdapter

class FavoritesPagerFragment : Fragment(R.layout.fragment_pager) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentItems = listOf(
            FragmentItem(
                FavoriteListFragment.create(Lang.KAZAKH_CYRILLIC),
                getString(R.string.tab_from_kaz)
            ),
            FragmentItem(
                FavoriteListFragment.create(Lang.RUSSIAN),
                getString(R.string.tab_from_rus)
            )
        )
        val adapter = SectionsPagerAdapter(fragmentItems, childFragmentManager)

        with(toolbar) {
            setTitle(R.string.tab_favorites)
            inflateMenu(R.menu.menu_favorites)
            setOnMenuItemClickListener {
                val currentFragment = adapter.getItem(viewPager.currentItem)
                currentFragment.onMenuClearClick()
                return@setOnMenuItemClickListener true
            }
        }
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
    }
}