package com.erank.yogappl.utils.interfaces

interface OnItemActionCallback<T> {
    //    TODO maybe delete pos params and use just id
    fun onItemSelected(item: T)
    fun onEditAction(item: T)
    fun onSignAction(item: T)
}