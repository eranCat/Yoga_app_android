package com.erank.yogappl.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.erank.yogappl.R
import com.erank.yogappl.adapters.DataPagerAdapter
import com.erank.yogappl.utils.enums.SourceType
import com.erank.yogappl.utils.interfaces.SourceTypeDynamic
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_tabs.*


class TabsFragment : Fragment(), SourceTypeDynamic {

    companion object {

        fun newInstance(sourceType: SourceType): TabsFragment {
            val fragment = TabsFragment()
            fragment.arguments = Bundle().apply {
                putSerializable("type", sourceType)
            }
            return fragment
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_tabs, container, false)

    private var dataPagerAdapter: DataPagerAdapter? = null
    private var tabLayout: TabLayout? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sourceType = arguments!!.getSerializable("type") as SourceType
        val tabTitles = resources.getStringArray(R.array.tab_titles)

        dataPagerAdapter = DataPagerAdapter(childFragmentManager, tabTitles, SourceType.ALL)

        val viewPager = data_view_pager
        viewPager.adapter = dataPagerAdapter

        tabLayout = tab_layout
        tabLayout!!.setupWithViewPager(viewPager)

        setColors(sourceType)
    }

    private fun setColors(type: SourceType) {

        tabLayout?.apply {
            val i = type.ordinal
            val color = resources.getIntArray(R.array.tabs_colors)[i]
            setBackgroundColor(color)
            val indicator = resources.getIntArray(R.array.tabs_indicators)[i]
            setSelectedTabIndicatorColor(indicator)
        }

    }

    override fun setSourceType(type: SourceType) {
        dataPagerAdapter?.setSourceType(type)
        setColors(type)
    }
}
