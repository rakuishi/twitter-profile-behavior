package com.rakuishi.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.coordinatorlayout.widget.CoordinatorLayout


class PullToRefreshCoordinatorLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : CoordinatorLayout(context, attrs, defStyle) {

    interface OnPullDownListener {
        fun onPullDown(y: Float)
        fun onPullDownCompleted(y: Float)
    }

    var actionDownY: Float = 0f
    var offset = 0f
    var onPullDownListener: OnPullDownListener? = null
    var isAppBarLayoutOffsetZero = true
    private var isMoving = false

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                actionDownY = ev.y
                isMoving = false
            }
            MotionEvent.ACTION_MOVE -> {
                offset = ev.y - actionDownY
                // 下方向の指の動き && AppBarLayout が最大伸長の時に isMoving とする
                isMoving = isMoving || (isAppBarLayoutOffsetZero && offset >= 0)
                if (isMoving && offset >= 0) {
                    onPullDownListener?.onPullDown(offset)
                }
            }
            MotionEvent.ACTION_UP -> {
                onPullDownListener?.onPullDownCompleted(offset)
                isMoving = false
            }
        }

        return super.dispatchTouchEvent(ev)
    }
}