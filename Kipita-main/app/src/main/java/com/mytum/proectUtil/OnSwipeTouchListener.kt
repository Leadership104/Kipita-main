package com.mytum.proectUtil

import android.content.Context
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.mytum.SwipeGestureListener

class OnSwipeTouchListener internal constructor(ctx: Context?, mainView: View, onSwipe1: SwipeGestureListener) :
    View.OnTouchListener {
    private val SWIPE_THRESHOLD = 100
    private val SWIPE_VELOCITY_THRESHOLD = 100

    private val gestureDetector: GestureDetector
    var context: Context?
    var onSwipe: SwipeGestureListener
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return false
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            var result = false
            try {
                val diffY = e2.y - e1!!.y
                val diffX = e2.x - e1!!.x
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight()
                        } else {
                            onSwipeLeft()
                        }
                        result = true
                    }else{
                        Log.d("aaaaaaaaaaaaa", "else part 2")
                    }
                }else{
                    Log.d("aaaaaaaaaaaaa", "else part 1")
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
            return result
        }

    }

    fun onSwipeRight() {
        onSwipe!!.swipeRight()
    }

    fun onSwipeLeft() {
        onSwipe!!.swipeLeft()
    }

    init {
        gestureDetector = GestureDetector(ctx, GestureListener())
        mainView.setOnTouchListener(this)
        context = ctx
        onSwipe = onSwipe1
    }
}