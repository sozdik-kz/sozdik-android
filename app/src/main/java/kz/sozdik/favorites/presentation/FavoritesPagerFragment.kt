package kz.sozdik.favorites.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_pager.*
import kz.sozdik.R
import kz.sozdik.base.FragmentItem
import kz.sozdik.base.SectionsPagerAdapter
import kz.sozdik.core.utils.Lang

class FavoritesPagerFragment : Fragment(R.layout.fragment_pager) {

    private val adapter by lazy {
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
        SectionsPagerAdapter(fragmentItems, childFragmentManager)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

    override fun onDestroyView() {
        super.onDestroyView()
        viewPager.adapter = null
    }
}