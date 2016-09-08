package com.example.ligang.studydemo.scratch.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.example.ligang.commonlibrary.util.MeasureUtil;
import com.example.ligang.studydemo.R;

/**
 * TODO: document your custom view class.
 */
public class ScratchView extends View {

    private int coverColor;//蒙层颜色

    private Bitmap coverBitmap;//蒙层bitmap

    private Canvas coverCanvas;//蒙层canvas

    private Paint coverPaint;//蒙层画笔

    private Paint eraserPaint;//擦除画笔

    private float eraserSize;//擦除画笔大小

    private Path eraserPath;//擦除路径

    private Paint drawPaint;//绘画画笔

    private float startX;//path开始擦除X坐标

    private float startY;//path开始擦除Y坐标

    private float mTouchSlop;//最小滑动距离

    /**
     * 水印
     */
    private BitmapDrawable mWatermark;

    private int waterResId;

    /**
     * 存放蒙层像素信息的数组
     */
    private int mPixels[];

    private Handler handler = new Handler(){
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            Log.e("Percent",msg.what+"");
        }
    };


    public ScratchView(Context context) {
        super(context);
        init(null, 0);
    }

    public ScratchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ScratchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.ScratchView, defStyle, 0);

        coverColor = a.getColor(R.styleable.ScratchView_coverColor,Color.RED);

        eraserSize = a.getFloat(R.styleable.ScratchView_eraserSize,12f);

        waterResId = a.getResourceId(R.styleable.ScratchView_waterImg,-1);

        a.recycle();


        // Update TextPaint and text measurements from attributes
        setWatermark(waterResId);
        invalidateDrawPaint();
        invalidateCoverPaint();
        invalidateEarsePaint();
    }
    private void invalidateDrawPaint() {
        drawPaint = new Paint();
        drawPaint.setAntiAlias(true);
        drawPaint.setDither(true);
    }
    private void invalidateCoverPaint() {
        coverPaint = new Paint();
        coverPaint.setAntiAlias(true);
        coverPaint.setDither(true);
        coverPaint.setColor(coverColor);
    }
    private void invalidateCoverBitmap(int width, int height) {
        coverBitmap = Bitmap.createBitmap(width,width, Bitmap.Config.ARGB_8888);
        coverCanvas = new Canvas(coverBitmap);
        Rect rect = new Rect(0,0,width,width);
        coverCanvas.drawRect(rect,coverPaint);
        invalidateCoverWater(rect,coverCanvas);
        mPixels = new int[width * height];
    }

    private void invalidateCoverWater(Rect rect,Canvas canvas) {
        if (mWatermark != null) {
            Rect bounds = new Rect(rect);
            mWatermark.setBounds(bounds);
            mWatermark.draw(canvas);
        }
    }

    /**
     * 设置水印图标
     *
     * @param resId 图标资源id，-1表示去除水印
     */
    public void setWatermark(int resId) {
        if (resId == -1) {
            mWatermark = null;
        } else {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);
            mWatermark = new BitmapDrawable(bitmap);
            mWatermark.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        }
    }
    private void invalidateEarsePaint(){
        eraserPaint = new Paint();
        eraserPaint.setAntiAlias(true);
        eraserPaint.setDither(true);
        eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));//设置擦除效果
        eraserPaint.setStyle(Paint.Style.STROKE);
        eraserPaint.setStrokeCap(Paint.Cap.ROUND);//设置笔尖形状，让绘制的边缘圆滑
        eraserPaint.setStrokeWidth(eraserSize);

        eraserPath = new Path();

        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        mTouchSlop = viewConfiguration.getScaledTouchSlop();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int saveCount = canvas.getSaveCount();

        canvas.drawBitmap(coverBitmap,getPaddingLeft(),getPaddingTop(),drawPaint);

        canvas.restoreToCount(saveCount);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        invalidateCoverBitmap(w, h);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        setMeasuredDimension(MeasureUtil.getMeasureByModel(widthMeasureSpec, 300), MeasureUtil.getMeasureByModel(heightMeasureSpec,300));
    }


    private void startErase(float x, float y){
        startX = x;
        startY = y;
        eraserPath.reset();
        eraserPath.moveTo(startX,startY);

    }
    private void earse(float x, float y){
        float xWidht = Math.abs(startX-x);
        float yHeight = Math.abs(startY-y);
        if(xWidht>=mTouchSlop||yHeight>=mTouchSlop){
            eraserPath.lineTo(x,y);
            coverCanvas.drawPath(eraserPath,eraserPaint);
            startErase(x,y);
            earsePercent();
        }
    }
    private void earsePercent(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                int width = getWidth();
                int height = getHeight();
                coverBitmap.getPixels(mPixels,0,width,0,0,width,height);
                float erasePixelCount = 0;//擦除的像素个数
                float totalPixelCount = width * height;//总像素个数

                for (int pos = 0; pos < totalPixelCount; pos++) {
                    if (mPixels[pos] == 0) {//透明的像素值为0
                        erasePixelCount++;
                    }
                }

                int percent = 0;
                if (erasePixelCount >= 0 && totalPixelCount > 0) {
                    percent = Math.round(erasePixelCount * 100 / totalPixelCount);
                }
                handler.sendEmptyMessage(percent);
            }
        });
    }
    /**
     * 停止擦除
     */
    private void stopErase() {
        this.startX = 0;
        this.startX = 0;
        eraserPath.reset();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startErase(event.getX(), event.getY());
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                earse(event.getX(), event.getY());
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                stopErase();
                invalidate();
                return true;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }


}
