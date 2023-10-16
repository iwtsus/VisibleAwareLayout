package com.zxy.view

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.FrameLayout


/**
 * description:可自动监听可见性FrameLayout
 */
@Suppress("unused")
open class VisibleAwareLayout : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        val displayMetrics = DisplayMetrics()
        (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getRealMetrics(
            displayMetrics
        )
        screenWidth = displayMetrics.widthPixels
        screenHeight = displayMetrics.heightPixels
    }

    private var screenWidth = 0
    private var screenHeight = 0
    private var lastVisibleStatus: Boolean = false
    private var visiblePercent: Double = 1.0
    private val onGlobalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        handleListener()
    }
    private val onScrollChangedListener = ViewTreeObserver.OnScrollChangedListener {
        handleListener()
    }
    var visibilityListener: VisibilityListener? = null

    private fun isInScreen(): Boolean {
        if (!isAttachedToWindow) {
            return false
        }
        if (visibility != VISIBLE) {
            return false
        }
        val globalRect = Rect()
        getGlobalVisibleRect(globalRect)
        val rightOut = globalRect.right <= 0
        val leftOut = globalRect.left >= screenWidth
        val bottomOut =  globalRect.bottom <= 0
        val topOut =  globalRect.top >= screenHeight
        if (rightOut || leftOut || bottomOut || topOut){
            return false
        }
        val drawingRect = Rect()
        getDrawingRect(drawingRect)
        val drawingArea =
            (drawingRect.bottom - drawingRect.top) * (drawingRect.right - drawingRect.left)
        val localArea = (globalRect.bottom - globalRect.top) * (globalRect.right - globalRect.left)
        return localArea.toDouble() / drawingArea >= visiblePercent
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        handleListener()
        viewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener)
        viewTreeObserver.addOnScrollChangedListener(onScrollChangedListener)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        handleListener()
        viewTreeObserver.removeOnGlobalLayoutListener(onGlobalLayoutListener)
        viewTreeObserver.removeOnScrollChangedListener(onScrollChangedListener)
    }

    override fun onVisibilityAggregated(isVisible: Boolean) {
        super.onVisibilityAggregated(isVisible)
        handleListener()
    }

    private fun handleListener() {
        post {
            val currentStatus = isInScreen()
            if (lastVisibleStatus == currentStatus) {
                return@post
            }
            lastVisibleStatus = currentStatus
            if (currentStatus) {
                visibilityListener?.onShow()
            } else {
                visibilityListener?.onDismiss()
            }
        }
    }

    fun setVisibleArea(percent: Double) {
        visiblePercent = percent
    }

    fun isVisible(): Boolean {
        return lastVisibleStatus
    }

    interface VisibilityListener {
        /** 可见 */
        fun onShow()

        /** 不可见 */
        fun onDismiss()
    }
}