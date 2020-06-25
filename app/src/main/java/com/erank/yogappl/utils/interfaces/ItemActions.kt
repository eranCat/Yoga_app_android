package com.erank.yogappl.utils.interfaces

interface ItemActions<T> {
    fun bind(item: T)
    fun setOnClickListeners(item: T){}
}