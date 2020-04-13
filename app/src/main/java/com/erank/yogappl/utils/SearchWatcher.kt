package com.erank.yogappl.utils

import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import com.erank.yogappl.utils.enums.SearchState
import com.erank.yogappl.utils.interfaces.SearchUpdateable

abstract class SearchWatcher : MenuItem.OnActionExpandListener,
    SearchView.OnQueryTextListener, SearchUpdateable {

    private var isSearching: Boolean = false

    override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
        isSearching = false
        updateSearch(SearchState.CLOSED)
        return true
    }

    override fun onMenuItemActionExpand(item: MenuItem): Boolean {
        isSearching = true
        return true
    }

    override fun onQueryTextChange(q: String): Boolean {
        if (isSearching)
            updateSearch(SearchState.CHANGED, q)
        return true
    }

    override fun onQueryTextSubmit(query: String?) = true
}