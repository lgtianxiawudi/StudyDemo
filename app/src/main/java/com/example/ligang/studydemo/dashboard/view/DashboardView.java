package com.example.ligang.studydemo.dashboard.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.icu.util.MeasureUnit;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.example.ligang.commonlibrary.util.MeasureUtil;

/**
 * Created by ligang967 on 16/9/9.
 */

public class DashboardView extends View {

    private Paint outCirclBgPain;//外围圆圈画笔

    private int outCircleColors[] = {Color.BLUE, Color.RED};//外围圆圈渐变色

    private RectF rectF = null;//整个VIew的矩形

    private int outCircleBgPainSize = 35;

    private Paint outLinePaint = null;//刻度画笔

    private Paint outLineTextPain = null;//刻度文字画笔

    private Paint innerCirclePain;//内圈背景画笔

    private Paint innerCircleSmall;//内圈小圆图标

    private RectF innerRectF = null;//内圈矩形

    private int minValue = 0;//最小值

    private int maxValue = 36;//最大值

    private float[] mCurrentPosition = new float[2];//获取当前进度的坐标

    private float currentAnimValue = 0;//动画进度值

    private Paint centerLogoPain = null;

    private String logoContent = "Beta";

    public DashboardView(Context context) {
        super(context);
        init(null, 0);
    }

    public DashboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public DashboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        initOutCircleBgPaint();
        initOutLinePaint();
        initOutLineTextPaint();
        initInnerCirclePain();
        initInnerCircleSmall();
        initCenterLogoPain();
    }

    private void initOutCircleBgPaint() {
        outCirclBgPain = new Paint();
        outCirclBgPain.setAntiAlias(true);
        outCirclBgPain.setStrokeCap(Paint.Cap.ROUND);//设置笔尖形状，让绘制的边缘圆滑
        outCirclBgPain.setStrokeWidth(outCircleBgPainSize);
        outCirclBgPain.setStyle(Paint.Style.STROKE);
        outCirclBgPain.setDither(true);
    }

    private void initOutLinePaint() {
        outLinePaint = new Paint();
        outLinePaint.setAntiAlias(true);
        outLinePaint.setDither(true);
        outLinePaint.setStrokeWidth(2);
        outLinePaint.setStyle(Paint.Style.STROKE);
        outLinePaint.setColor(Color.WHITE);
    }

    private void initOutLineTextPaint() {
        outLineTextPain = new Paint();
        outLineTextPain.setAntiAlias(true);
        outLineTextPain.setDither(true);
        outLineTextPain.setStrokeWidth(2);
        outLineTextPain.setStyle(Paint.Style.STROKE);
        outLineTextPain.setColor(Color.BLACK);
        outLineTextPain.setTextSize(20);
    }

    private void initInnerCirclePain() {
        innerCirclePain = new Paint();
        innerCirclePain.setAntiAlias(true);
        innerCirclePain.setDither(true);
        innerCirclePain.setStrokeWidth(outCircleBgPainSize / 4);
        innerCirclePain.setStyle(Paint.Style.STROKE);
        innerCirclePain.setColor(Color.GRAY);
    }

    private void initInnerCircleSmall(){
        innerCircleSmall =new Paint();
        innerCircleSmall.setAntiAlias(true);
        innerCircleSmall.setDither(true);
        innerCircleSmall.setStrokeWidth(outCircleBgPainSize / 4);
        innerCircleSmall.setStyle(Paint.Style.FILL);
        innerCircleSmall.setColor(Color.BLUE);
    }

    private void initCenterLogoPain(){
        centerLogoPain = new Paint();
        centerLogoPain.setAntiAlias(true);
        centerLogoPain.setDither(true);
        centerLogoPain.setColor(Color.GREEN);
        centerLogoPain.setTextSize(24);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawOutCircle(canvas);
        drawOutCircleLine(canvas);
        drawInnerCircle(canvas);
        drawLogoAndProgress(canvas);
    }

    private void drawOutCircle(Canvas canvas) {
        float[] p = {.2f, 0.8f};
        SweepGradient sweepGradient = new SweepGradient(rectF.centerX(), rectF.centerY(), outCircleColors, p);
        Matrix matrix = new Matrix();
        sweepGradient.getLocalMatrix(matrix);
        matrix.postRotate(40, rectF.centerX(), rectF.centerY());
        sweepGradient.setLocalMatrix(matrix);
        outCirclBgPain.setShader(sweepGradient);
        int saveCount = canvas.getSaveCount();
        canvas.drawArc(rectF, 165, 210, false
                , outCirclBgPain);
        canvas.restoreToCount(saveCount);
    }

    private void drawOutCircleLine(Canvas canvas) {
        int saveCount = canvas.getSaveCount();
        canvas.rotate(-105, rectF.centerX(), rectF.centerY());
        int count = maxValue-minValue+1;
        for (int i = 0; i < count; i++) {
            if (i%5==0){
                outLinePaint.setStrokeWidth(6);
                canvas.drawText((minValue+i) + "", rectF.centerX(), rectF.top + outCircleBgPainSize * 2, outLineTextPain);
            }else {
                outLinePaint.setStrokeWidth(2);
            }
            canvas.drawLine(rectF.centerX(), 0, rectF.centerX(), rectF.top + outCircleBgPainSize, outLinePaint);
            canvas.rotate(210f/count, rectF.centerX(), rectF.centerY());
        }
        canvas.rotate(-105, rectF.centerX(), rectF.centerY());
        canvas.restoreToCount(saveCount);
    }


    private void drawInnerCircle(Canvas canvas) {
        int saveCount = canvas.getSaveCount();

        innerCirclePain.setColor(Color.GRAY);

        canvas.drawArc(innerRectF,165, 210,false,innerCirclePain);

        innerCirclePain.setColor(Color.BLUE);

        Path path = new Path();

        float currentAngle = (currentAnimValue-minValue)*210f/(maxValue-minValue+1);

        path.addArc(innerRectF, 165, currentAngle);

        PathMeasure pathMeasure = new PathMeasure(path,false);

        pathMeasure.getPosTan(pathMeasure.getLength(),mCurrentPosition,null);

        canvas.drawPath(path, innerCirclePain);

        canvas.drawCircle(mCurrentPosition[0],mCurrentPosition[1],outCircleBgPainSize/2,innerCircleSmall);

        canvas.restoreToCount(saveCount);
    }

    private void drawLogoAndProgress(Canvas canvas){
        int saveCount = canvas.getSaveCount();

        float contentLengthLogo = centerLogoPain.measureText(logoContent);

        Rect rectLogo = new Rect();

        centerLogoPain.getTextBounds(logoContent,0,logoContent.length(),rectLogo);

        float contentHeight = centerLogoPain.descent()+centerLogoPain.ascent();

        canvas.drawText(logoContent,rectF.centerX()-contentLengthLogo/2,rectF.centerY(),centerLogoPain);

        String proContent = (int)currentAnimValue+"";

        float contentLengthPro = centerLogoPain.measureText(proContent);

        canvas.drawText(currentAnimValue+"",rectF.centerX()-contentLengthPro/2,rectF.centerY()+rectLogo.height()+10,centerLogoPain);

        canvas.restoreToCount(saveCount);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int width = getWidth();
        int height = getHeight();
        int lenght = width > height ? height : width;
        rectF = new RectF(left + outCircleBgPainSize, top + outCircleBgPainSize, left - outCircleBgPainSize + lenght, top - outCircleBgPainSize + lenght);
        innerRectF = new RectF(rectF.left + 3 * outCircleBgPainSize, rectF.top + 3 * outCircleBgPainSize, rectF.right - 3 * outCircleBgPainSize, rectF.bottom - 3 * outCircleBgPainSize);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MeasureUtil.getMeasureByModel(widthMeasureSpec, 400), MeasureUtil.getMeasureByModel(heightMeasureSpec, 400));
    }
    public void setMinAndMaxValue(int minValue,int maxValue){
        if (maxValue<minValue){
            throw new RuntimeException("the maxValue must bigger than the minValue");
        }
        this.minValue = minValue;
        this.maxValue = maxValue;
        postInvalidate();
    }
    public void setCurrentValue(float currentValue){
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(minValue,currentValue);
        valueAnimator.setDuration(2000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                currentAnimValue = (float)valueAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        valueAnimator.start();
    }
}
