package com.erank.yogappl.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.erank.yogappl.R
import com.erank.yogappl.adapters.DataPagerAdapter
import com.erank.yogappl.utils.enums.SearchState
import com.erank.yogappl.utils.enums.SourceType
import com.erank.yogappl.utils.interfaces.SearchUpdateable
import kotlinx.android.synthetic.main.fragment_tabs.*


class TabsFragment : Fragment(), SearchUpdateable {

    companion object {

        fun newInstance(sourceType: SourceType) =
            TabsFragment().apply {
                arguments = bundleOf("type" to sourceType)
            }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_tabs, container, false)

    private var dataPagerAdapter: DataPagerAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sourceType = arguments!!.getSerializable("type") as SourceType
        val tabTitles = resources.getStringArray(R.array.tab_titles)

        dataPagerAdapter = DataPagerAdapter(childFragmentManager, tabTitles, sourceType)

        with(data_view_pager) {
            adapter = dataPagerAdapter
            tab_layout.setupWithViewPager(this)
        }

        setColors(sourceType)
    }

    private fun setColors(type: SourceType) = tab_layout.apply {

        val bgColors = resources.getIntArray(R.array.tabs_colors)

        val i = type.ordinal

        val bgColor = bgColors[i]

        setBackgroundColor(bgColor)
    }

    override fun updateSearch(state: SearchState, query: String) {
        val item = dataPagerAdapter?.getItem(data_view_pager.currentItem)
        (item as? SearchUpdateable)?.updateSearch(state, query)
    }

}
