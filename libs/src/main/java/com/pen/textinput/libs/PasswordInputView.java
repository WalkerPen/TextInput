package com.pen.textinput.libs;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputFilter;
import android.text.InputType;
import android.util.AttributeSet;

/**
 * Created by Pen on 2017/5/11.
 */

public class PasswordInputView extends AppCompatEditText {
    private int dotColor = Color.BLACK; //圆点颜色
    private int backgroundColor = Color.TRANSPARENT; //背景颜色
    private int strokeColor = Color.BLACK; //描边颜色
    private int divideColor = Color.BLACK; //分割线颜色
    private int dotWidth = 20; //圆点宽度
    private int divideWidth = 2; //分割线宽
    private int strokeWidth = 2; //边框描边宽
    private int inputLength = 6; //密码个数
    private int defaultHeightDp = 30;//控件默认高度
    private float cornerRadius = 9;//圆角半径
    private Paint paint;
    private float gridWidth;//格子宽
    private float gridHeight;//格子高
    private int textLength;//以输入密码的个数

    public PasswordInputView(Context context) {
        this(context, null);
    }

    public PasswordInputView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PasswordInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PasswordInputView);
            backgroundColor = typedArray.getInt(R.styleable.PasswordInputView_piv_background_color, Color.TRANSPARENT);
            dotColor = typedArray.getInt(R.styleable.PasswordInputView_piv_dot_color, Color.BLACK);
            strokeColor = typedArray.getInt(R.styleable.PasswordInputView_piv_stroke_color, Color.BLACK);
            divideColor = typedArray.getInt(R.styleable.PasswordInputView_piv_divide_color, Color.BLACK);
            dotWidth = typedArray.getDimensionPixelSize(R.styleable.PasswordInputView_piv_dot_width, 20);
            divideWidth = typedArray.getDimensionPixelSize(R.styleable.PasswordInputView_piv_divide_width, 2);
            strokeWidth = typedArray.getDimensionPixelSize(R.styleable.PasswordInputView_piv_stroke_width, 2);
            cornerRadius = typedArray.getDimensionPixelSize(R.styleable.PasswordInputView_piv_corner_radius, 9);
            inputLength = typedArray.getInt(R.styleable.PasswordInputView_piv_input_length, 6);
            typedArray.recycle();
        }
        init();
    }

    private void init() {
        setBackground();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        setCursorVisible(false);
        setFocusableInTouchMode(true);
        setSingleLine();
        setTextColor(Color.TRANSPARENT);
        setInputType(InputType.TYPE_CLASS_NUMBER);
        //代码设置最大输入长度
        setFilters(new InputFilter[]{new InputFilter.LengthFilter(inputLength)});
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

        //内容部分的宽，除去padding，除去描边，除去分割线
        int contentWidth = 0;
        //内容部分的高，除去padding，除去描边
        int contentHeight = 0;
        if(widthMeasureMode == MeasureSpec.AT_MOST && heightMeasureMode == MeasureSpec.AT_MOST) {
            heightMeasureSize = dip2px(defaultHeightDp);
            contentHeight = heightMeasureSize - getPaddingTop() - getPaddingBottom() - strokeWidth * 2;
            contentWidth = contentHeight * inputLength;
            widthMeasureSize = contentWidth + getPaddingLeft() + getPaddingRight() + strokeWidth * 2 + divideWidth * (inputLength - 1);
        }else if(widthMeasureMode == MeasureSpec.AT_MOST && heightMeasureMode == MeasureSpec.EXACTLY) {
            contentHeight = heightMeasureSize - getPaddingTop() - getPaddingBottom() - strokeWidth * 2;
            contentWidth = contentHeight * inputLength;
            widthMeasureSize = contentWidth + getPaddingLeft() + getPaddingRight() + strokeWidth * 2 + divideWidth * (inputLength - 1);
        }else if(widthMeasureMode == MeasureSpec.EXACTLY && heightMeasureMode == MeasureSpec.AT_MOST) {
            contentWidth = widthMeasureSize - getPaddingLeft() - getPaddingRight() - strokeWidth * 2 - divideWidth * (inputLength - 1);
            contentHeight = contentWidth / inputLength;
            heightMeasureSize = contentHeight + getPaddingTop() + getPaddingBottom() + strokeWidth * 2;
        }else if(widthMeasureMode == MeasureSpec.EXACTLY && heightMeasureMode == MeasureSpec.EXACTLY) {
            contentWidth = widthMeasureSize - getPaddingLeft() - getPaddingRight() - strokeWidth * 2 - divideWidth * (inputLength - 1);
            contentHeight = heightMeasureSize - getPaddingTop() - getPaddingBottom() - strokeWidth * 2;
        }

        //算出格子的宽高,要减去边框的宽度
        gridWidth = contentWidth * 1f / inputLength;
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
        for(int i = 0; i < inputLength - 1; i ++) {
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

    public int getDotColor() {
        return dotColor;
    }

    public void setDotColor(int dotColor) {
        this.dotColor = dotColor;
    }

    public int getDotWidth() {
        return dotWidth;
    }

    public void setDotWidth(int dotWidth) {
        this.dotWidth = dotWidth;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
    }

    public int getDivideColor() {
        return divideColor;
    }

    public void setDivideColor(int divideColor) {
        this.divideColor = divideColor;
    }

    public int getDivideWidth() {
        return divideWidth;
    }

    public void setDivideWidth(int divideWidth) {
        this.divideWidth = divideWidth;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public int getInputLength() {
        return inputLength;
    }

    public void setInputLength(int inputLength) {
        this.inputLength = inputLength;
    }

    public float getCornerRadius() {
        return cornerRadius;
    }

    public void setCornerRadius(float cornerRadius) {
        this.cornerRadius = cornerRadius;
    }
}
