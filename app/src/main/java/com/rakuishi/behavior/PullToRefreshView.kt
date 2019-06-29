package com.rakuishi.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DimenRes
import kotlin.math.max
import kotlin.math.min

class PullToRefreshView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    private val defaultY: Int
    private var isLoading: Boolean = false

    private val root: View
    private val scrollableContainer: View
    private val imageView: ImageView
    private val textView: TextView

    init {
        View.inflate(context, R.layout.view_pull_to_refresh, this)
        root = findViewById(R.id.root)
        scrollableContainer = findViewById(R.id.scrollableContainer)
        imageView = findViewById(R.id.imageView)
        textView = findViewById(R.id.textView)

        defaultY = px(res = R.dimen.toolbar_height) * -1
        reset()
    }

    fun onPullDown(y: Float) {
        if (isLoading) return

        // 変化量を 1/2 にする
        val positionY = min(0f, defaultY + y / 2)
        scrollableContainer.y = positionY

        root.setBackgroundResource(R.color.transparent_black)

        if (positionY == 0f) {
            textView.setText(R.string.release_to_refresh)
        } else {
            textView.setText(R.string.pull_down_to_refresh)
        }

        // 最後の 4dp で 0 ~ 180 度の回転を行うようにする
        val dp4 = px(dp = 4)
        val ratio = 180f / dp4
        imageView.rotation = max(dp4 + positionY, 0f) * ratio
    }

    /**
     * @return true 時は、読み込み開始
     */
    fun onPullDownCompleted(y: Float): Boolean {
        // 変化量を 1/2 にする
        val positionY = min(0f, defaultY + y / 2)

        return if (positionY == 0f) {
            imageView.setImageDrawable(null)
            textView.setText(R.string.loading)
            isLoading = true
            true
        } else {
            reset()
            false
        }
    }

    fun reset() {
        isLoading = false
        root.setBackgroundResource(android.R.color.transparent)
        imageView.setImageResource(R.drawable.ic_arrow_downward_white_24dp)
        textView.setText(R.string.pull_down_to_refresh)
        scrollableContainer.y = defaultY.toFloat()
    }

    private fun px(@DimenRes res: Int? = null, dp: Int? = null): Int {
        if (res != null) {
            return context.resources.getDimensionPixelOffset(res)
        }

        if (dp != null) {
            return (context.resources.displayMetrics.density * dp).toInt()
        }

        return 0
    }
}
