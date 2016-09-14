package com.example.ligang.studydemo.dashboard.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.example.ligang.commonlibrary.loadingdrawable.LoadingRenderer;
import com.example.ligang.commonlibrary.util.SvgPathParserUtil;
import com.example.ligang.studydemo.R;

import java.text.ParseException;
import java.util.List;

/**
 * Created by ligang967 on 16/9/13.
 */

public class SvgAnimView extends LoadingRenderer {


    private static final Interpolator LINEAR_INTERPOLATOR = new LinearInterpolator();

    private static int paintColor = Color.BLACK;

    private Path path = null;

    private Path paintPath = new Path();

    private PathMeasure pathMeasure = null;

    private final Paint mPaint = new Paint();

    private final Animator.AnimatorListener mAnimatorListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationRepeat(Animator animator) {
            super.onAnimationRepeat(animator);
            paintPath = new Path();
        }

        @Override
        public void onAnimationStart(Animator animation) {
            super.onAnimationStart(animation);
            paintPath = new Path();
        }
    };


    public SvgAnimView(Context context) throws ParseException {
        super(context);
        setupPaint();
        path = new SvgPathParserUtil().parsePath("M 50.0,90.0 L 82.9193546357,27.2774101308 L 12.5993502926,35.8158045183 L 59.5726265715,88.837672697 L 76.5249063296,20.0595700732 L 10.2916450361,45.1785327898 L 68.5889268818,85.4182410261 L 68.5889268818,14.5817589739 L 10.2916450361,54.8214672102 L 76.5249063296,79.9404299268 L 59.5726265715,11.162327303 L 12.5993502926,64.1841954817 L 82.9193546357,72.7225898692 L 50.0,10.0 L 17.0806453643,72.7225898692 L 87.4006497074,64.1841954817 L 40.4273734285,11.162327303 L 23.4750936704,79.9404299268 L 89.7083549639,54.8214672102 L 31.4110731182,14.5817589739 L 31.4110731182,85.4182410261 L 89.7083549639,45.1785327898 L 23.4750936704,20.0595700732 L 40.4273734285,88.837672697 L 87.4006497074,35.8158045183 L 17.0806453643,27.2774101308 L 50.0,90.0Z");
        pathMeasure = new PathMeasure(path,false);
        addRenderListener(mAnimatorListener);
    }

    private void setupPaint() {

        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(getStrokeWidth());
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(paintColor);
        mPaint.setDither(true);
    }

    @Override
    public void draw(Canvas canvas, Rect bounds) {
        int saveCount = canvas.save();

        canvas.drawPath(paintPath,mPaint);

        canvas.restoreToCount(saveCount);
    }

    @Override
    public void computeRender(float renderProgress) {
        // Moving the start trim only occurs in the first 50% of a
        // single ring animation
         pathMeasure.getSegment(0,pathMeasure.getLength()*renderProgress,paintPath,true);
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
        invalidateSelf();
    }

    @Override
    public void reset() {

    }


    public void setColor(int color) {
        this.paintColor = color;
    }

    @Override
    public void setStrokeWidth(float strokeWidth) {
        super.setStrokeWidth(strokeWidth);
        mPaint.setStrokeWidth(strokeWidth);
        invalidateSelf();
    }



}
