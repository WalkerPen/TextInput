package com.pen.textinput.libs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import java.lang.reflect.Field;

/**
 * Created by Pen on 2017/5/11.
 */

public class PaswordInputView extends AppCompatEditText {
    private int dotColor = Color.BLACK; //圆点颜色
    private int dotWidth = 40; //圆点颜色
    private int backgroundColor = Color.TRANSPARENT; //背景颜色
    private int strokeColor = Color.BLACK; //描边颜色
    private int divideColor = Color.BLACK; //分割线颜色
    private int divideWidth = 20; //分割线宽
    private int strokeWidth = 20; //边框描边宽
    private int inputCount = 6; //密码个数
    private int defaultHeightDp = 30;
    private float cornerRadius = 9;
    private Paint paint;
    private float gridWidth;//格子宽
    private float gridHeight;//格子高
    private int textLength;

    public PaswordInputView(Context context) {
        this(context, null);
    }

    public PaswordInputView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaswordInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackground();
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        setCursorVisible(false);
        setFocusableInTouchMode(true);
        setTextColor(Color.TRANSPARENT);
        setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        textLength = text.length();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMeasureMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthMeasureSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMeasureMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightMeasureSize = MeasureSpec.getSize(heightMeasureSpec);

        int contentWidth = 0;
        int contentHeight = 0;
        if(widthMeasureMode == MeasureSpec.AT_MOST && heightMeasureMode == MeasureSpec.AT_MOST) {
            heightMeasureSize = dip2px(defaultHeightDp);
            contentHeight = heightMeasureSize - getPaddingTop() - getPaddingBottom() - strokeWidth * 2;
            contentWidth = contentHeight * inputCount;
            widthMeasureSize = contentWidth + getPaddingLeft() + getPaddingRight() + strokeWidth * 2 + divideWidth * (inputCount - 1);
        }else if(widthMeasureMode == MeasureSpec.AT_MOST && heightMeasureMode == MeasureSpec.EXACTLY) {
            contentHeight = heightMeasureSize - getPaddingTop() - getPaddingBottom() - strokeWidth * 2;
            contentWidth = contentHeight * inputCount;
            widthMeasureSize = contentWidth + getPaddingLeft() + getPaddingRight() + strokeWidth * 2 + divideWidth * (inputCount - 1);
        }else if(widthMeasureMode == MeasureSpec.EXACTLY && heightMeasureMode == MeasureSpec.AT_MOST) {
            contentWidth = widthMeasureSize - getPaddingLeft() - getPaddingRight() - strokeWidth * 2 - divideWidth * (inputCount - 1);
            contentHeight = contentWidth / inputCount;
            heightMeasureSize = contentHeight + getPaddingTop() + getPaddingBottom() + strokeWidth * 2;
        }else if(widthMeasureMode == MeasureSpec.EXACTLY && heightMeasureMode == MeasureSpec.EXACTLY) {
            contentWidth = widthMeasureSize - getPaddingLeft() - getPaddingRight() - strokeWidth * 2 - divideWidth * (inputCount - 1);
            contentHeight = heightMeasureSize - getPaddingTop() - getPaddingBottom() - strokeWidth * 2;
        }

        //算出格子的宽高,要减去边框的宽度
        gridWidth = contentWidth * 1f / inputCount;
        gridHeight = contentHeight ;

        widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthMeasureSize, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightMeasureSize, MeasureSpec.EXACTLY);
        long end = System.currentTimeMillis();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画间隔
        paint.setColor(divideColor);
        for(int i = 0; i < inputCount - 1; i ++) {
            float left = getPaddingLeft() + strokeWidth + gridWidth * (i + 1) + divideWidth * i;
//            int top = getPaddingTop();
            float top = strokeWidth;
            float bottom = top + getPaddingTop() + getPaddingBottom() + gridHeight;
            canvas.drawRect(left, top, left + divideWidth, bottom, paint);
        }

        //画圆点
        paint.setColor(dotColor);
        for(int i = 0; i < textLength; i ++) {
            float left = getPaddingLeft() + strokeWidth + gridWidth * i + divideWidth * i + (gridWidth - dotWidth) / 2;
            float top = getPaddingTop() + strokeWidth + (gridHeight - dotWidth) / 2;
            float right = left + dotWidth;
            float bottom = top + dotWidth;
            RectF oval = new RectF(left, top, right, bottom);
            canvas.drawOval(oval, paint);
        }
    }


    public int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void setBackground() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(cornerRadius);
        drawable.setColor(backgroundColor);
        drawable.setStroke(strokeWidth, strokeColor);

        setBackgroundDrawable(drawable);
    }
}
