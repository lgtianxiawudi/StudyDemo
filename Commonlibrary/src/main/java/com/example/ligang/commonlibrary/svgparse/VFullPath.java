package com.example.ligang.commonlibrary.svgparse;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import org.xmlpull.v1.XmlPullParser;

/**
 * Created by ligang967 on 16/9/14.
 */

public class VFullPath extends VPath {
    private static final int LINECAP_BUTT = 0;
    private static final int LINECAP_ROUND = 1;
    private static final int LINECAP_SQUARE = 2;

    private static final int LINEJOIN_MITER = 0;
    private static final int LINEJOIN_ROUND = 1;
    private static final int LINEJOIN_BEVEL = 2;
    /////////////////////////////////////////////////////
    // Variables below need to be copied (deep copy if applicable) for mutation.
    private int[] mThemeAttrs;

    int mStrokeColor = Color.TRANSPARENT;
    float mStrokeWidth = 0;

    int mFillColor = Color.TRANSPARENT;
    float mStrokeAlpha = 1.0f;
    int mFillRule;
    float mFillAlpha = 1.0f;
    float mTrimPathStart = 0;
    float mTrimPathEnd = 1;
    float mTrimPathOffset = 0;

    Paint.Cap mStrokeLineCap = Paint.Cap.BUTT;
    Paint.Join mStrokeLineJoin = Paint.Join.MITER;
    float mStrokeMiterlimit = 4;

    public VFullPath() {
        // Empty constructor.
    }

    public VFullPath(VFullPath copy) {
        super(copy);
        mThemeAttrs = copy.mThemeAttrs;

        mStrokeColor = copy.mStrokeColor;
        mStrokeWidth = copy.mStrokeWidth;
        mStrokeAlpha = copy.mStrokeAlpha;
        mFillColor = copy.mFillColor;
        mFillRule = copy.mFillRule;
        mFillAlpha = copy.mFillAlpha;
        mTrimPathStart = copy.mTrimPathStart;
        mTrimPathEnd = copy.mTrimPathEnd;
        mTrimPathOffset = copy.mTrimPathOffset;

        mStrokeLineCap = copy.mStrokeLineCap;
        mStrokeLineJoin = copy.mStrokeLineJoin;
        mStrokeMiterlimit = copy.mStrokeMiterlimit;
    }

    private Paint.Cap getStrokeLineCap(int id, Paint.Cap defValue) {
        switch (id) {
            case LINECAP_BUTT:
                return Paint.Cap.BUTT;
            case LINECAP_ROUND:
                return Paint.Cap.ROUND;
            case LINECAP_SQUARE:
                return Paint.Cap.SQUARE;
            default:
                return defValue;
        }
    }

    private Paint.Join getStrokeLineJoin(int id, Paint.Join defValue) {
        switch (id) {
            case LINEJOIN_MITER:
                return Paint.Join.MITER;
            case LINEJOIN_ROUND:
                return Paint.Join.ROUND;
            case LINEJOIN_BEVEL:
                return Paint.Join.BEVEL;
            default:
                return defValue;
        }
    }

    @Override
    public boolean canApplyTheme() {
        return mThemeAttrs != null;
    }

    public void inflate(Resources r, AttributeSet attrs, Resources.Theme theme, XmlPullParser parser) {
        final TypedArray a = obtainAttributes(r, theme, attrs,
                AndroidResources.styleable_VectorDrawablePath);
        updateStateFromTypedArray(a, parser);
        a.recycle();
    }

    private void updateStateFromTypedArray(TypedArray a, XmlPullParser parser) {
        // Account for any configuration changes.
        // mChangingConfigurations |= Utils.getChangingConfigurations(a);

        // Extract the theme attributes, if any.
        mThemeAttrs = null; // TODO TINT THEME Not supported yet a.extractThemeAttrs();

        // In order to work around the conflicting id issue, we need to double check the
        // existence of the attribute.
        // B/c if the attribute existed in the compiled XML, then calling TypedArray will be
        // safe since the framework will look up in the XML first.
        // Note that each getAttributeValue take roughly 0.03ms, it is a price we have to pay.
        final boolean hasPathData = TypedArrayUtils.hasAttribute(parser, "pathData");
        if (!hasPathData) {
            // If there is no pathData in the <path> tag, then this is an empty path,
            // nothing need to be drawn.
            return;
        }

        final String pathName = a.getString(AndroidResources.styleable_VectorDrawablePath_name);
        if (pathName != null) {
            mPathName = pathName;
        }
        final String pathData =
                a.getString(AndroidResources.styleable_VectorDrawablePath_pathData);
        if (pathData != null) {
            mNodes = PathParser.createNodesFromPathData(pathData);
        }

        mFillColor = TypedArrayUtils.getNamedColor(a, parser, "fillColor",
                AndroidResources.styleable_VectorDrawablePath_fillColor, mFillColor);
        mFillAlpha = TypedArrayUtils.getNamedFloat(a, parser, "fillAlpha",
                AndroidResources.styleable_VectorDrawablePath_fillAlpha, mFillAlpha);
        final int lineCap = TypedArrayUtils.getNamedInt(a, parser, "strokeLineCap",
                AndroidResources.styleable_VectorDrawablePath_strokeLineCap, -1);
        mStrokeLineCap = getStrokeLineCap(lineCap, mStrokeLineCap);
        final int lineJoin = TypedArrayUtils.getNamedInt(a, parser, "strokeLineJoin",
                AndroidResources.styleable_VectorDrawablePath_strokeLineJoin, -1);
        mStrokeLineJoin = getStrokeLineJoin(lineJoin, mStrokeLineJoin);
        mStrokeMiterlimit = TypedArrayUtils.getNamedFloat(a, parser, "strokeMiterLimit",
                AndroidResources.styleable_VectorDrawablePath_strokeMiterLimit,
                mStrokeMiterlimit);
        mStrokeColor = TypedArrayUtils.getNamedColor(a, parser, "strokeColor",
                AndroidResources.styleable_VectorDrawablePath_strokeColor, mStrokeColor);
        mStrokeAlpha = TypedArrayUtils.getNamedFloat(a, parser, "strokeAlpha",
                AndroidResources.styleable_VectorDrawablePath_strokeAlpha, mStrokeAlpha);
        mStrokeWidth = TypedArrayUtils.getNamedFloat(a, parser, "strokeWidth",
                AndroidResources.styleable_VectorDrawablePath_strokeWidth, mStrokeWidth);
        mTrimPathEnd = TypedArrayUtils.getNamedFloat(a, parser, "trimPathEnd",
                AndroidResources.styleable_VectorDrawablePath_trimPathEnd, mTrimPathEnd);
        mTrimPathOffset = TypedArrayUtils.getNamedFloat(a, parser, "trimPathOffset",
                AndroidResources.styleable_VectorDrawablePath_trimPathOffset, mTrimPathOffset);
        mTrimPathStart = TypedArrayUtils.getNamedFloat(a, parser, "trimPathStart",
                AndroidResources.styleable_VectorDrawablePath_trimPathStart, mTrimPathStart);
    }

    @Override
    public void applyTheme(Resources.Theme t) {
        if (mThemeAttrs == null) {
            return;
        }

            /*
             * TODO TINT THEME Not supported yet final TypedArray a =
             * t.resolveAttributes(mThemeAttrs, styleable_VectorDrawablePath);
             * updateStateFromTypedArray(a); a.recycle();
             */
    }

    /* Setters and Getters, used by animator from AnimatedVectorDrawable. */
    @SuppressWarnings("unused")
    int getStrokeColor() {
        return mStrokeColor;
    }

    @SuppressWarnings("unused")
    void setStrokeColor(int strokeColor) {
        mStrokeColor = strokeColor;
    }

    @SuppressWarnings("unused")
    float getStrokeWidth() {
        return mStrokeWidth;
    }

    @SuppressWarnings("unused")
    void setStrokeWidth(float strokeWidth) {
        mStrokeWidth = strokeWidth;
    }

    @SuppressWarnings("unused")
    float getStrokeAlpha() {
        return mStrokeAlpha;
    }

    @SuppressWarnings("unused")
    void setStrokeAlpha(float strokeAlpha) {
        mStrokeAlpha = strokeAlpha;
    }

    @SuppressWarnings("unused")
    int getFillColor() {
        return mFillColor;
    }

    @SuppressWarnings("unused")
    void setFillColor(int fillColor) {
        mFillColor = fillColor;
    }

    @SuppressWarnings("unused")
    float getFillAlpha() {
        return mFillAlpha;
    }

    @SuppressWarnings("unused")
    void setFillAlpha(float fillAlpha) {
        mFillAlpha = fillAlpha;
    }

    @SuppressWarnings("unused")
    float getTrimPathStart() {
        return mTrimPathStart;
    }

    @SuppressWarnings("unused")
    void setTrimPathStart(float trimPathStart) {
        mTrimPathStart = trimPathStart;
    }

    @SuppressWarnings("unused")
    float getTrimPathEnd() {
        return mTrimPathEnd;
    }

    @SuppressWarnings("unused")
    void setTrimPathEnd(float trimPathEnd) {
        mTrimPathEnd = trimPathEnd;
    }

    @SuppressWarnings("unused")
    float getTrimPathOffset() {
        return mTrimPathOffset;
    }

    @SuppressWarnings("unused")
    void setTrimPathOffset(float trimPathOffset) {
        mTrimPathOffset = trimPathOffset;
    }
}
