package com.pestipona.kidsdrawingapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.graphics.Path
import android.util.Size
import android.util.TypedValue
import android.view.MotionEvent

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var mDrawPath: CustomPath? = null
    private var mCanvasBitmap: Bitmap? = null
    private var mDrawPaint: Paint? = null
    private var mCanvasPaint: Paint? = null
    private var mBrushSize: Float = 0.toFloat()
    private var color = Color.BLACK
    private var canvas: Canvas? = null
    private val mPaths = ArrayList<CustomPath>()

    /* This function calls setUpDrawing to initialize the private variables */
    init {
        setUpDrawing()
    }

    /* This function sets the values for all private variables */
    private fun setUpDrawing() {
        mDrawPaint = Paint()
        mDrawPath = CustomPath(color, mBrushSize)
        mDrawPaint!!.color = color
        mDrawPaint!!.style = Paint.Style.STROKE
        mDrawPaint!!.strokeJoin = Paint.Join.ROUND
        mDrawPaint!!.strokeCap = Paint.Cap.ROUND
        mCanvasPaint = Paint(Paint.DITHER_FLAG)
        //mBrushSize = 20.toFloat()
    }

    /* setup canvas dimensions */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(mCanvasBitmap!!)
    }

    // Change Canvas to Canvas? if fails
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(mCanvasBitmap!!, 0f, 0f, mCanvasPaint)

        for (path in mPaths) {
            mDrawPaint!!.strokeWidth = path.brushThickness
            mDrawPaint!!.color = path.color
            canvas.drawPath(path, mDrawPaint!!)
        }

        if (!mDrawPath!!.isEmpty) {
            // color and thickness of the paint
            mDrawPaint!!.strokeWidth = mDrawPath!!.brushThickness
            mDrawPaint!!.color = mDrawPath!!.color
            canvas.drawPath(mDrawPath!!, mDrawPaint!!)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        val touchY = event?.y

        // when finger is pressed on screen
        // 3 important events happen
        when(event?.action){
            // when finger is pressed down on the screen
            MotionEvent.ACTION_DOWN -> {
                // color and thickness of the path
                mDrawPath!!.color = color
                mDrawPath!!.brushThickness = mBrushSize

                // reset value for next event
                mDrawPath!!.reset()
                if (touchX != null) {
                    if (touchY != null) {
                        mDrawPath!!.moveTo(touchX, touchY)
                    }
                }
            }
            // when finger is dragged on the screen
            MotionEvent.ACTION_MOVE -> {
                if (touchX != null) {
                    if (touchY != null) {
                        mDrawPath!!.lineTo(touchX, touchY)
                    }
                }
            }
            // when finger is lifted off the screen
            MotionEvent.ACTION_UP -> {
                mPaths.add(mDrawPath!!)
                mDrawPath = CustomPath(color, mBrushSize)
            }
            // catch all since 'when' event, since there are other possible events
            else -> return false
        }
        invalidate()

        return true
    }

    fun setSizeForBrush(newSize: Float){
        // using the constraints of the screen dimension
        mBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            newSize, resources.displayMetrics
        )
        // update the stroke width
        mDrawPaint!!.strokeWidth = mBrushSize
    }

    // sets the color of the brush
    fun setColor(newColor: String) {
        color = Color.parseColor(newColor)
        mDrawPaint!!.color = color
    }

    // An inner class for custom path with two params as color and stroke
    internal inner class CustomPath(var color: Int, var brushThickness: Float): Path(){

    }
}