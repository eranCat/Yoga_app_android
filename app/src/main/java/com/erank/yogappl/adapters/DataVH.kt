package com.erank.yogappl.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.erank.yogappl.utils.interfaces.ItemActions

abstract class DataVH<T>(parent: ViewGroup, @LayoutRes layout: Int) :
    RecyclerView.ViewHolder(inflate(parent, layout)),
    ItemActions<T>

private fun inflate(parent: ViewGroup, layout: Int): View {
    val inflater = LayoutInflater.from(parent.context)
    return inflater.inflate(layout, parent, false)
}