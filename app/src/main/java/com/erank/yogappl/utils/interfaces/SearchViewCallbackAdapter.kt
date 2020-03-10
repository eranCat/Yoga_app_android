package com.erank.yogappl.utils.interfaces

import android.view.MenuItem
import androidx.appcompat.widget.SearchView

interface SearchViewCallbackAdapter :
    MenuItem.OnActionExpandListener,
    SearchView.OnQueryTextListener {
    override fun onQueryTextSubmit(query: String?) = false
}