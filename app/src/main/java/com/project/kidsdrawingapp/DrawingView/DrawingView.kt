package com.project.kidsdrawingapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context, attr: AttributeSet) : View(context, attr) {
    private var mDrawPath = CustomPath(Color.BLACK, 20f)
    private lateinit var mCanvasBitmap: Bitmap
    private var mDrawPaint = Paint()
    private var mCanvasPaint = Paint(Paint.DITHER_FLAG)
    private var mBrushSize:Float = 0.toFloat()
    private val mpaths = ArrayList<CustomPath>()
    private val mundopaths =ArrayList<CustomPath>()
    private var color = Color.BLACK

    init {
        setUpDrawing()
    }

    fun onClickUndo(){
        if(mpaths.size>0)
        {
            mundopaths.add(mpaths.removeAt(mpaths.size-1))
            invalidate()
        }
    }

    private fun setUpDrawing() {
        mDrawPaint.color = Color.BLACK
        mDrawPaint.style = Paint.Style.STROKE
        mDrawPaint.strokeJoin = Paint.Join.ROUND
        mDrawPaint.strokeCap = Paint.Cap.ROUND
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        super.onSizeChanged(w, h, oldW, oldH)
        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        mCanvasBitmap.eraseColor(Color.WHITE) // Fill the bitmap with white background
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(mCanvasBitmap, 0f, 0f, mCanvasPaint)

        for (path in mpaths) {
            mDrawPaint.strokeWidth = path.brushThickness
            mDrawPaint.color = path.color
            canvas.drawPath(path, mDrawPaint)
        }

        if (!mDrawPath.isEmpty) {
            mDrawPaint.strokeWidth = mDrawPath.brushThickness
            mDrawPaint.color = mDrawPath.color
            canvas.drawPath(mDrawPath, mDrawPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        val touchY = event?.y

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                mDrawPath.color = mDrawPaint.color
                mDrawPath.brushThickness = mBrushSize

                mDrawPath.reset()
                mDrawPath.moveTo(touchX!!, touchY!!)
            }
            MotionEvent.ACTION_MOVE -> {
                mDrawPath.lineTo(touchX!!, touchY!!)
            }
            MotionEvent.ACTION_UP ->{
                mpaths.add(mDrawPath!!)
                mDrawPath= CustomPath(mDrawPaint.color,mBrushSize)
            }
            else -> return false
        }
        invalidate()
        return true
    }

    fun setSizeForBrush(newsize : Float)
    {
       mBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
       newsize,resources.displayMetrics)
        mDrawPaint!!.strokeWidth = mBrushSize
    }
    fun setcolor(newcolor:String){
        color = Color.parseColor(newcolor)
        mDrawPaint!!.color = color
    }

    internal inner class CustomPath(var color: Int, var brushThickness: Float) : Path()
}
