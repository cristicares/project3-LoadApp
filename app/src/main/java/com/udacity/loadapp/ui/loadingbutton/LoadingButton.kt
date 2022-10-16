package com.udacity.loadapp.ui.loadingbutton

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.core.animation.addListener
import androidx.core.content.withStyledAttributes
import com.udacity.R
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var buttonBackgroundColor = 0
    private var buttonLoadingColor = 0
    private var buttonTextColor = 0
    private var circleLoadingColor = 0
    private var progress = 0F
    private var circleRadius = 60F

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Completed -> {
                isClickable = true
                isFocusable = true
            }
            ButtonState.Clicked -> {
                isClickable = false
                isFocusable = false

                valueAnimator.cancel()
                valueAnimator.start()
            }
            ButtonState.Loading -> {}
        }
        invalidate()
    }

    init {
        isClickable = true
        isFocusable = true
        buttonState = ButtonState.Completed

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            buttonBackgroundColor = getColor(R.styleable.LoadingButton_buttonBackgroundColor, 0)
            buttonLoadingColor = getColor(R.styleable.LoadingButton_buttonLoadingColor, 0)
            buttonTextColor = getColor(R.styleable.LoadingButton_buttonTextColor, 0)
            circleLoadingColor = getColor(R.styleable.LoadingButton_circleLoadingColor, 0)
        }
    }

    private val valueAnimator = ValueAnimator.ofFloat(0F, 1F).apply {
        duration = 2000
        interpolator = AccelerateInterpolator()
        addListener(
            onStart = {
                progress = 0F
                buttonState = ButtonState.Loading
            },
            onEnd = {
                buttonState = ButtonState.Completed
            }
        )
        addUpdateListener { animator ->
            progress = animator.animatedFraction
            invalidate()
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        buttonState = ButtonState.Clicked
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        when (buttonState) {
            ButtonState.Completed -> onDrawInitButton(canvas)
            ButtonState.Loading -> onDrawLoadingButton(canvas)
        }

    }

    private fun onDrawLoadingButton(canvas: Canvas) {

        val progressWidth = widthSize * progress
        paint.color = buttonLoadingColor
        canvas.drawRect(0F, 0F, progressWidth, heightSize.toFloat(), paint)
        paint.color = buttonBackgroundColor
        canvas.drawRect(progressWidth, 0F, widthSize.toFloat(), heightSize.toFloat(), paint)

        paint.color = buttonTextColor
        val pointX = widthSize / 2F
        val pointY = (heightSize - paint.descent() - paint.ascent()) / 2F
        canvas.drawText(resources.getString(R.string.button_loading), pointX, pointY, paint)

        val rectF = RectF(
            0f,
            0f,
            circleRadius,
            circleRadius
        )

        canvas.translate(pointX + 200, pointY - 50)
        paint.color = circleLoadingColor
        canvas.drawArc(rectF, 0.0F, progress * 360, true, paint)
    }

    private fun onDrawInitButton(canvas: Canvas) {
        paint.color = buttonBackgroundColor
        canvas.drawRect(0F, 0F, widthSize.toFloat(), heightSize.toFloat(), paint)

        paint.color = buttonTextColor
        val pointX = widthSize.toFloat() / 2.0F
        val pointY = (heightSize.toFloat() - paint.descent() - paint.ascent()) / 2F
        canvas.drawText(resources.getString(R.string.button_download), pointX, pointY, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}