package com.erank.yogappl.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.erank.yogappl.fragments.EventsListFragment
import com.erank.yogappl.fragments.LessonsListFragment
import com.erank.yogappl.utils.enums.SourceType


//for the 2 tabs of lessons,events viewpager
class DataPagerAdapter(
    fm: FragmentManager,
    private val tabTitles: Array<String>,
    sourceType: SourceType
) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragments by lazy {
        arrayOf<Fragment>(
            LessonsListFragment.newInstance(sourceType),
            EventsListFragment.newInstance(sourceType)
        )
    }

    override fun getItem(index: Int) = fragments[index]

    override fun getPageTitle(position: Int) = tabTitles[position]

    override fun getCount() = fragments.size
}