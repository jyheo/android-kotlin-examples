package com.example.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * Created by jyheo on 17. 5. 11.
 * Changed to Kotlin on Sept. 7, 2019
 */

class MyView : View {
    var rect = Rect(10, 10, 110, 110)
    var color = Color.BLUE
    var paint = Paint()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        color = attrs.getAttributeIntValue(null, "mycolor", Color.BLUE)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = color
        canvas.drawRect(rect, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            rect.left = event.x.toInt()
            rect.top = event.y.toInt()
            rect.right = rect.left + 100
            rect.bottom = rect.top + 100
            invalidate()
        }
        return super.onTouchEvent(event)
    }
}
