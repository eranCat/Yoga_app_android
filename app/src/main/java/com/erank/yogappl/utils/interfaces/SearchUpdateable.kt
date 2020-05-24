package com.erank.yogappl.utils.interfaces

import com.erank.yogappl.data.enums.SearchState

interface SearchUpdateable {
    fun updateSearch(state: SearchState, query: String = "")
}