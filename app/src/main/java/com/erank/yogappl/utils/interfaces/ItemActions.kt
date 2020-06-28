package com.erank.yogappl.utils.interfaces

interface ItemActions<T> {
    fun bind(item: Any)
    fun setOnClickListeners(item: T){}
}