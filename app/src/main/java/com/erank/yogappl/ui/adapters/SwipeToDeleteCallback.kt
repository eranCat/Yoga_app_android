package com.erank.yogappl.ui.adapters

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.erank.yogappl.R

abstract class SwipeToDeleteCallback : ItemTouchHelper.SimpleCallback {

    private val deleteIcon: Drawable
    private val intrinsicWidth: Int
    private val intrinsicHeight: Int
    private val background: ColorDrawable
    private val clearPaint: Paint

    constructor(context: Context) : super(0, ItemTouchHelper.LEFT) {
        deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete)!!
        deleteIcon.setTint(Color.WHITE)
        intrinsicWidth = deleteIcon.intrinsicWidth
        intrinsicHeight = deleteIcon.intrinsicHeight
        background = ColorDrawable()
        background.color = Color.parseColor("#f44336")
        clearPaint = Paint()
        clearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ) = false

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {

        val itemView = viewHolder.itemView
        val top = itemView.top
        val bottom = itemView.bottom
        val itemHeight = bottom - itemView.top
        val isCanceled = dX == 0f && !isCurrentlyActive

        val right = itemView.right
        if (isCanceled) {
            val left = right + dX
            clearCanvas(c, top.toFloat(), left, bottom.toFloat(), right.toFloat())
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }

        // Draw the red delete background
        background.setBounds(right + dX.toInt(), top, right, bottom)
        background.draw(c)

        // Calculate position of delete icon
        val deleteIconTop = top + (itemHeight - intrinsicHeight) / 2
        val deleteIconMargin = (itemHeight - intrinsicHeight) / 2
        val deleteIconLeft = right - deleteIconMargin - intrinsicWidth
        val deleteIconRight = right - deleteIconMargin
        val deleteIconBottom = deleteIconTop + intrinsicHeight

        // Draw the delete icon
        deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
        deleteIcon.draw(c)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun clearCanvas(
        c: Canvas,
        top: Float, left: Float,
        bottom: Float, right: Float
    ) = c.drawRect(left, top, right, bottom, clearPaint)
}