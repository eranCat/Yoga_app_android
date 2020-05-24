package com.erank.yogappl.ui.custom_views

import android.content.Context
import android.graphics.*
import android.graphics.Bitmap.Config.ALPHA_8
import android.graphics.Bitmap.Config.ARGB_8888
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Paint.FILTER_BITMAP_FLAG
import android.graphics.PorterDuff.Mode.CLEAR
import android.util.AttributeSet
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.widget.ImageView


class RoundedImageView : ImageView {

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    init {
        cornerRadius = TypedValue.applyDimension(
            COMPLEX_UNIT_DIP,
            CORNER_RADIUS,
            resources.displayMetrics
        )
        paint = Paint(ANTI_ALIAS_FLAG)
        maskPaint = Paint(ANTI_ALIAS_FLAG or FILTER_BITMAP_FLAG)
        maskPaint!!.xfermode = PorterDuffXfermode(CLEAR)
        setWillNotDraw(false)
    }

    companion object {
        private const val CORNER_RADIUS = 8f
    }

    private var maskBitmap: Bitmap? = null
    private var paint: Paint? = null
    private var maskPaint: Paint? = null
    private var cornerRadius = 0f

    override fun draw(canvas: Canvas) {
        val offscreenBitmap = Bitmap.createBitmap(width, height, ARGB_8888)
        val offscreenCanvas = Canvas(offscreenBitmap)
        super.draw(offscreenCanvas)

        maskBitmap ?: run { maskBitmap = mask }

        offscreenCanvas.drawBitmap(maskBitmap!!, 0f, 0f, maskPaint)
        canvas.drawBitmap(offscreenBitmap, 0f, 0f, paint)
    }

    private val mask: Bitmap?
        get() {

            val mask = Bitmap.createBitmap(width, height, ALPHA_8)
            val paint = Paint(ANTI_ALIAS_FLAG)
//        paint.color = Color.WHITE

            with(Canvas(mask)) {
                val rectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
                drawRect(rectF, paint)
                paint.xfermode = PorterDuffXfermode(CLEAR)
                drawRoundRect(rectF, cornerRadius, cornerRadius, paint)
            }

            return mask
        }
}