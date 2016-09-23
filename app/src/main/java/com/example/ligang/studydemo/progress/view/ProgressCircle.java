package com.example.ligang.studydemo.progress.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import com.example.ligang.commonlibrary.util.MeasureUtil;
import com.example.ligang.studydemo.R;

/**
 * TODO: document your custom view class.
 */
public class ProgressCircle extends View implements Animatable, ValueAnimator.AnimatorUpdateListener {

    private int innerColor = Color.parseColor("#FF3030");

    private int outerColor = Color.parseColor("#FFDAB9");

    private Paint circlePaint = null;

    private float minRaius = 0;

    private RectF arcRect1 = null;

    private RectF arcRect2 = null;

    private RectF arcRect3 = null;

    private RectF arcRect4 = null;

    private float centerX = 0;

    private float centerY = 0;

    private Paint processPaint = null;

    private float currentProcess = 100;

    private float maxProcess = 100;

    private float drasProcess = 0;

    private Paint linePaint = null;

    private ValueAnimator animator;

    float dividerLength ;

    int duration;

    int repearCount;

    int repeatModle;

    private int processColor = 0;

    private int lineColor = 0;

    private Paint textPaint = null;

    public ProgressCircle(Context context) {
        super(context);
        init(null, 0);
    }

    public ProgressCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ProgressCircle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.ProgressCircle, defStyle, 0);

        minRaius = a.getFloat(R.styleable.ProgressCircle_minRaius,50f);
        currentProcess = a.getFloat(R.styleable.ProgressCircle_currentProcess,100);
        maxProcess = a.getFloat(R.styleable.ProgressCircle_maxProcess,100);
        innerColor = a.getColor(R.styleable.ProgressCircle_innerColor,Color.parseColor("#FF3030"));
        outerColor = a.getColor(R.styleable.ProgressCircle_outerColor,Color.parseColor("#FFDAB9"));
        dividerLength = a.getFloat(R.styleable.ProgressCircle_dividerLength,10);
        duration = a.getInteger(R.styleable.ProgressCircle_duration,4000);
        repearCount = a.getInteger(R.styleable.ProgressCircle_repearCount,Animation.INFINITE);
        repeatModle = a.getInteger(R.styleable.ProgressCircle_repeatModle,ValueAnimator.RESTART);
        processColor = a.getColor(R.styleable.ProgressCircle_processColor,Color.GREEN);
        lineColor = a.getColor(R.styleable.ProgressCircle_lineColor,Color.WHITE);
        a.recycle();


        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
        circlePaint = new Paint();
        circlePaint.setDither(true);
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(10);
        processPaint = new Paint();
        processPaint.setDither(true);
        processPaint.setAntiAlias(true);
        processPaint.setStyle(Paint.Style.STROKE);
        processPaint.setStrokeWidth(14);
        processPaint.setColor(processColor);
        linePaint = new Paint();
        linePaint.setDither(true);
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.FILL);
        linePaint.setStrokeWidth(4);
        linePaint.setColor(lineColor);
        textPaint = new Paint();
        textPaint.setDither(true);
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(30);
        textPaint.setColor(Color.BLACK);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int saveCount = canvas.getSaveCount();
        canvas.drawCircle(centerX, centerY, minRaius+dividerLength*0, circlePaint);
        canvas.drawCircle(centerX, centerY, minRaius+dividerLength*1, circlePaint);
        canvas.drawCircle(centerX, centerY, minRaius+dividerLength*2, circlePaint);
        canvas.drawCircle(centerX, centerY, minRaius+dividerLength*3, circlePaint);
        canvas.drawArc(arcRect1, 270, getCurrentAngle(), false, processPaint);
        canvas.drawArc(arcRect2, 270, getCurrentAngle(), false, processPaint);
        canvas.drawArc(arcRect3, 270, getCurrentAngle(), false, processPaint);
        canvas.drawArc(arcRect4, 270, getCurrentAngle(), false, processPaint);
        canvas.restoreToCount(saveCount);
        drawTextOnCenter(canvas);
        drawSmallCircleAndLine(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MeasureUtil.getMeasureByModel(widthMeasureSpec, 200), MeasureUtil.getMeasureByModel(heightMeasureSpec, 200));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int width = getWidth();
        int height = getHeight();
        centerX = width / 2;
        centerY = height / 2;
        float maxRadius = width>height?centerY:centerX;
        RadialGradient radialGradient = new RadialGradient(centerX, centerY, maxRadius, new int[]{innerColor, outerColor}, null, Shader.TileMode.CLAMP);
        circlePaint.setShader(radialGradient);
        arcRect1 = getRectF(minRaius);
        arcRect2 = getRectF(minRaius+dividerLength);
        arcRect3 = getRectF(minRaius+dividerLength*2);
        arcRect4 = getRectF(minRaius+dividerLength*3);
    }

    private RectF getRectF(float radius) {
        RectF rectF = new RectF();
        rectF.left = centerX - radius;
        rectF.top = centerY - radius;
        rectF.right = centerX + radius;
        rectF.bottom = centerY + radius;
        return rectF;
    }

    private float getCurrentAngle(){
        return 360*drasProcess/maxProcess;
    }

    private void drawSmallCircleAndLine(Canvas canvas){
        int saveCount = canvas.getSaveCount();

        canvas.rotate(getCurrentAngle(),centerX,centerY);
        canvas.drawCircle(arcRect4.centerX(),arcRect4.top,processPaint.getStrokeWidth(),linePaint);
        canvas.drawLine(arcRect4.centerX(),arcRect4.top,arcRect1.centerX(),arcRect1.top+minRaius*0.2f,linePaint);
        canvas.restoreToCount(saveCount);
    }
    private void drawTextOnCenter(Canvas canvas){
        int saveCount = canvas.getSaveCount();

        String content = (int)drasProcess+"";

        float widht = linePaint.measureText(content);

        canvas.drawText(content,centerX-widht/2,centerY,textPaint);

        canvas.restoreToCount(saveCount);

    }

    @Override
    public void start() {
        if (animator!=null&& animator.isRunning()){
            return;
        }
        animator = ValueAnimator.ofFloat(0,currentProcess);
        animator.setDuration(duration);
        animator.setRepeatCount(repearCount);
        animator.setRepeatMode(repeatModle);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(this);
        animator.start();
    }

    @Override
    public void stop() {
        if (animator!=null){
            animator.cancel();
        }
    }

    @Override
    public boolean isRunning() {
        if (animator!=null){
            return animator.isRunning();
        }
        return false;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        drasProcess = (float)valueAnimator.getAnimatedValue();
        postInvalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator!=null){
            animator.cancel();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        start();
    }
}
