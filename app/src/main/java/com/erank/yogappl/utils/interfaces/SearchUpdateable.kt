package com.erank.yogappl.utils.interfaces

import com.erank.yogappl.utils.enums.SearchState

interface SearchUpdateable {
    fun updateSearch(state: SearchState, query: String = "")
}