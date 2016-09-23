package com.example.ligang.studydemo.nestedsroll.view;

import android.content.Context;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.OverScroller;

import com.example.ligang.commonlibrary.util.LogUtil;
import com.example.ligang.commonlibrary.util.MeasureUtil;
import com.example.ligang.studydemo.R;

/**
 * Created by ligang967 on 16/9/23.
 */

public class NestenSrollingCustom extends LinearLayout implements NestedScrollingParent{

    private View top;

    private View content;

    private int topHeight;

    private OverScroller scroller;

    public NestenSrollingCustom(Context context) {
        super(context);
        init(context);
    }

    public NestenSrollingCustom(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NestenSrollingCustom(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        scroller = new OverScroller(context);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        top = findViewById(R.id.nested_id_top);
        content = findViewById(R.id.nested_id_content);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        top.measure(widthMeasureSpec,MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED));
        setMeasuredDimension(MeasureUtil.getMeasureByModel(widthMeasureSpec,400),MeasureUtil.getMeasureByModel(heightMeasureSpec,(top.getMeasuredHeight()+content.getMeasuredHeight())));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        topHeight = top.getMeasuredHeight();
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        LogUtil.e("onStartNestedScroll:"+nestedScrollAxes);
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        super.onNestedScrollAccepted(child, target, axes);
        LogUtil.e("onNestedScrollAccepted");
    }

    @Override
    public void onStopNestedScroll(View child) {
        super.onStopNestedScroll(child);
        LogUtil.e("onStopNestedScroll");
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        LogUtil.e("onNestedScroll:"+dxConsumed+","+dyConsumed+","+dxUnconsumed+","+dyUnconsumed);
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        LogUtil.e("onNestedPreScroll:"+dx+","+dy+","+consumed);
        boolean isNeedScroolDown = dy>0&&getScrollY()<topHeight;//向上滑动
        boolean isNeedScroolUp = dy<0&&!ViewCompat.canScrollVertically(target,-1)&&getScrollY()>0;//向下滑动
        if (isNeedScroolDown||isNeedScroolUp){
            scrollBy(0,dy);
            consumed[1]=dy;//传入消耗掉得值
        }
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        LogUtil.e("onNestedFling:"+velocityX+","+velocityY+","+consumed);
        if (getScrollY()>topHeight){
            return false;
        }else {
            scroller.fling(0,getScrollY(),0,(int)velocityY,0,0,0,topHeight);
            invalidate();
            return true;
        }
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        LogUtil.e("onNestedPreFling:"+velocityX+","+velocityY);
        return super.onNestedPreFling(target, velocityX, velocityY);
    }

    @Override
    public int getNestedScrollAxes() {
        LogUtil.e("getNestedScrollAxes");
        return 0;
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()){
            scrollTo(0,scroller.getCurrY());
            invalidate();
        }
    }
}
