package com.example.ligang.studydemo.dashboard.view;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.graphics.drawable.VectorDrawableCompat;

/**
 * Created by ligang967 on 16/9/14.
 */

public class VectorDrawableCompatState extends Drawable.ConstantState {
    static final PorterDuff.Mode DEFAULT_TINT_MODE = PorterDuff.Mode.SRC_IN;
    public int mChangingConfigurations;
    public VPathRenderer mVPathRenderer;
    public ColorStateList mTint = null;
    public PorterDuff.Mode mTintMode = DEFAULT_TINT_MODE;
    public boolean mAutoMirrored;

    Bitmap mCachedBitmap;
    int[] mCachedThemeAttrs;
    ColorStateList mCachedTint;
    PorterDuff.Mode mCachedTintMode;
    int mCachedRootAlpha;
    boolean mCachedAutoMirrored;
    public boolean mCacheDirty;

    /**
     * Temporary paint object used to draw cached bitmaps.
     */
    Paint mTempPaint;

    // Deep copy for mutate() or implicitly mutate.
    public VectorDrawableCompatState(VectorDrawableCompatState copy) {
        if (copy != null) {
            mChangingConfigurations = copy.mChangingConfigurations;
            mVPathRenderer = new VPathRenderer(copy.mVPathRenderer);
            if (copy.mVPathRenderer.mFillPaint != null) {
                mVPathRenderer.mFillPaint = new Paint(copy.mVPathRenderer.mFillPaint);
            }
            if (copy.mVPathRenderer.mStrokePaint != null) {
                mVPathRenderer.mStrokePaint = new Paint(copy.mVPathRenderer.mStrokePaint);
            }
            mTint = copy.mTint;
            mTintMode = copy.mTintMode;
            mAutoMirrored = copy.mAutoMirrored;
        }
    }

    public void drawCachedBitmapWithRootAlpha(Canvas canvas, ColorFilter filter,
                                              Rect originalBounds) {
        // The bitmap's size is the same as the bounds.
        final Paint p = getPaint(filter);
        canvas.drawBitmap(mCachedBitmap, null, originalBounds, p);
    }

    public boolean hasTranslucentRoot() {
        return mVPathRenderer.getRootAlpha() < 255;
    }

    /**
     * @return null when there is no need for alpha paint.
     */
    public Paint getPaint(ColorFilter filter) {
        if (!hasTranslucentRoot() && filter == null) {
            return null;
        }

        if (mTempPaint == null) {
            mTempPaint = new Paint();
            mTempPaint.setFilterBitmap(true);
        }
        mTempPaint.setAlpha(mVPathRenderer.getRootAlpha());
        mTempPaint.setColorFilter(filter);
        return mTempPaint;
    }

    public void updateCachedBitmap(int width, int height) {
        mCachedBitmap.eraseColor(Color.TRANSPARENT);
        Canvas tmpCanvas = new Canvas(mCachedBitmap);
        mVPathRenderer.draw(tmpCanvas, width, height, null);
    }

    public void createCachedBitmapIfNeeded(int width, int height) {
        if (mCachedBitmap == null || !canReuseBitmap(width, height)) {
            mCachedBitmap = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            mCacheDirty = true;
        }

    }

    public boolean canReuseBitmap(int width, int height) {
        if (width == mCachedBitmap.getWidth()
                && height == mCachedBitmap.getHeight()) {
            return true;
        }
        return false;
    }

    public boolean canReuseCache() {
        if (!mCacheDirty
                && mCachedTint == mTint
                && mCachedTintMode == mTintMode
                && mCachedAutoMirrored == mAutoMirrored
                && mCachedRootAlpha == mVPathRenderer.getRootAlpha()) {
            return true;
        }
        return false;
    }

    public void updateCacheStates() {
        // Use shallow copy here and shallow comparison in canReuseCache(),
        // likely hit cache miss more, but practically not much difference.
        mCachedTint = mTint;
        mCachedTintMode = mTintMode;
        mCachedRootAlpha = mVPathRenderer.getRootAlpha();
        mCachedAutoMirrored = mAutoMirrored;
        mCacheDirty = false;
    }

    public VectorDrawableCompatState() {
        mVPathRenderer = new VPathRenderer();
    }

    @Override
    public Drawable newDrawable() {
//        return new VectorDrawableCompat(this);
        return null;
    }

    @Override
    public Drawable newDrawable(Resources res) {
//        return new VectorDrawableCompat(this);
        return null;
    }

    @Override
    public int getChangingConfigurations() {
        return mChangingConfigurations;
    }
}
