package com.rakuishi.behavior

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import androidx.annotation.DimenRes
import androidx.appcompat.widget.Toolbar
import androidx.core.view.updateLayoutParams
import androidx.core.widget.NestedScrollView
import com.google.android.material.appbar.AppBarLayout
import kotlin.math.abs

class ProfileBehavior(rootView: View) : AppBarLayout.OnOffsetChangedListener,
    NestedScrollView.OnScrollChangeListener, PullToRefreshCoordinatorLayout.OnPullDownListener {

    private val context: Context = rootView.context
    private val coordinatorLayout: PullToRefreshCoordinatorLayout = rootView.findViewById(R.id.coordinatorLayout)
    private val toolbar: Toolbar = rootView.findViewById(R.id.toolbar)
    private val appBarLayout: AppBarLayout = rootView.findViewById(R.id.appBarLayout)
    private val pullToRefreshView: PullToRefreshView = rootView.findViewById(R.id.pullToRefreshView)
    private val nestedScrollView: NestedScrollView = rootView.findViewById(R.id.nestedScrollView)
    private val avatarImageView: ImageView = rootView.findViewById(R.id.avatarImageView)

    // 展開時 0, 収縮時 1
    private var appBarExpandedPer: Float = 0f
    private var appBarOffset: Int = 0
    private var appBarTotalScrollRange: Int? = null
    private var scrollY: Int = 0
    private val handler = Handler(Looper.getMainLooper())

    var onRefresh: (() -> Unit)? = null

    init {
        toolbar.let {
            it.inflateMenu(R.menu.menu_profile)
            it.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        }
        coordinatorLayout.onPullDownListener = this
        appBarLayout.addOnOffsetChangedListener(this)
        nestedScrollView.setOnScrollChangeListener(this)
        avatarImageView.clipToOval()
        renderAvatar()
    }

    fun reset() {
        pullToRefreshView.reset()
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        if (appBarTotalScrollRange == null) {
            appBarTotalScrollRange = appBarLayout.totalScrollRange
        }

        coordinatorLayout.isAppBarLayoutOffsetZero = verticalOffset == 0

        // 前回と値が異なる場合のみアニメーション処理を行う
        if (appBarOffset != abs(verticalOffset)) {
            appBarOffset = abs(verticalOffset)
            appBarExpandedPer = appBarOffset / appBarTotalScrollRange!!.toFloat()

            handler.post {
                renderAvatar()
            }
        }
    }

    override fun onScrollChange(v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
        this.scrollY = scrollY
        renderAvatar()
    }

    override fun onPullDown(y: Float) {
        pullToRefreshView.onPullDown(y)
    }

    override fun onPullDownCompleted(y: Float) {
        if (pullToRefreshView.onPullDownCompleted(y)) {
            onRefresh?.invoke()
        }
    }

    private fun renderAvatar() {
        val maxSize = px(res = R.dimen.avatar_max_size)
        val minSize = px(res = R.dimen.avatar_min_size)
        val imageSize = (maxSize - (maxSize - minSize) * appBarExpandedPer).toInt()

        val x = px(dp = 12)
        val appBarStartY = px(res = R.dimen.appbar_height) - maxSize / 2
        val appBarFinalY = px(res = R.dimen.toolbar_height)
        val y = appBarStartY - (appBarStartY - appBarFinalY) * appBarExpandedPer - scrollY

        avatarImageView.let {
            it.updateLayoutParams {
                width = imageSize
                height = imageSize
            }
            it.x = x.toFloat()
            it.y = y
        }
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