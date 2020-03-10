package com.erank.yogappl.utils.interfaces

interface OnItemActionCallback<T> {
    fun onItemSelected(item: T, pos: Int)
    fun onEditAction(item: T, pos: Int)
    fun onDeleteAction(item: T, pos: Int)
    fun onSignAction(item: T, i: Int)
}