package com.example.ligang.studydemo.dashboard.view;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.graphics.drawable.*;
import android.support.v4.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;

/**
 * Created by ligang967 on 16/9/14.
 */

public class VGroup {
    // mStackedMatrix is only used temporarily when drawing, it combines all
    // the parents' local matrices with the current one.
    public final Matrix mStackedMatrix = new Matrix();

    /////////////////////////////////////////////////////
    // Variables below need to be copied (deep copy if applicable) for mutation.
    public final ArrayList<Object> mChildren = new ArrayList<Object>();

    private float mRotate = 0;
    private float mPivotX = 0;
    private float mPivotY = 0;
    private float mScaleX = 1;
    private float mScaleY = 1;
    private float mTranslateX = 0;
    private float mTranslateY = 0;

    // mLocalMatrix is updated based on the update of transformation information,
    // either parsed from the XML or by animation.
    public final Matrix mLocalMatrix = new Matrix();
    public int mChangingConfigurations;
    private int[] mThemeAttrs;
    private String mGroupName = null;

    public VGroup(VGroup copy, ArrayMap<String, Object> targetsMap) {
        mRotate = copy.mRotate;
        mPivotX = copy.mPivotX;
        mPivotY = copy.mPivotY;
        mScaleX = copy.mScaleX;
        mScaleY = copy.mScaleY;
        mTranslateX = copy.mTranslateX;
        mTranslateY = copy.mTranslateY;
        mThemeAttrs = copy.mThemeAttrs;
        mGroupName = copy.mGroupName;
        mChangingConfigurations = copy.mChangingConfigurations;
        if (mGroupName != null) {
            targetsMap.put(mGroupName, this);
        }

        mLocalMatrix.set(copy.mLocalMatrix);

        final ArrayList<Object> children = copy.mChildren;
        for (int i = 0; i < children.size(); i++) {
            Object copyChild = children.get(i);
            if (copyChild instanceof VGroup) {
                VGroup copyGroup = (VGroup) copyChild;
                mChildren.add(new VGroup(copyGroup, targetsMap));
            } else {
                VPath newPath = null;
                if (copyChild instanceof VFullPath) {
                    newPath = new VFullPath((VFullPath) copyChild);
                } else if (copyChild instanceof VClipPath) {
                    newPath = new VClipPath((VClipPath) copyChild);
                } else {
                    throw new IllegalStateException("Unknown object in the tree!");
                }
                mChildren.add(newPath);
                if (newPath.mPathName != null) {
                    targetsMap.put(newPath.mPathName, newPath);
                }
            }
        }
    }

    public VGroup() {
    }

    public String getGroupName() {
        return mGroupName;
    }

    public Matrix getLocalMatrix() {
        return mLocalMatrix;
    }

    public void inflate(Resources res, AttributeSet attrs, Resources.Theme theme, XmlPullParser parser) {
        final TypedArray a = obtainAttributes(res, theme, attrs,
                AndroidResources.styleable_VectorDrawableGroup);
        updateStateFromTypedArray(a, parser);
        a.recycle();
    }

    private void updateStateFromTypedArray(TypedArray a, XmlPullParser parser) {
        // Account for any configuration changes.
        // mChangingConfigurations |= Utils.getChangingConfigurations(a);

        // Extract the theme attributes, if any.
        mThemeAttrs = null; // TODO TINT THEME Not supported yet a.extractThemeAttrs();

        // This is added in API 11
        mRotate = TypedArrayUtils.getNamedFloat(a, parser, "rotation",
                AndroidResources.styleable_VectorDrawableGroup_rotation, mRotate);

        mPivotX = a.getFloat(AndroidResources.styleable_VectorDrawableGroup_pivotX, mPivotX);
        mPivotY = a.getFloat(AndroidResources.styleable_VectorDrawableGroup_pivotY, mPivotY);

        // This is added in API 11
        mScaleX = TypedArrayUtils.getNamedFloat(a, parser, "scaleX",
               AndroidResources.styleable_VectorDrawableGroup_scaleX, mScaleX);

        // This is added in API 11
        mScaleY = TypedArrayUtils.getNamedFloat(a, parser, "scaleY",
                AndroidResources.styleable_VectorDrawableGroup_scaleY, mScaleY);

        mTranslateX = TypedArrayUtils.getNamedFloat(a, parser, "translateX",
                AndroidResources.styleable_VectorDrawableGroup_translateX, mTranslateX);
        mTranslateY = TypedArrayUtils.getNamedFloat(a, parser, "translateY",
                AndroidResources.styleable_VectorDrawableGroup_translateY, mTranslateY);

        final String groupName =
                a.getString(AndroidResources.styleable_VectorDrawableGroup_name);
        if (groupName != null) {
            mGroupName = groupName;
        }

        updateLocalMatrix();
    }

    private void updateLocalMatrix() {
        // The order we apply is the same as the
        // RenderNode.cpp::applyViewPropertyTransforms().
        mLocalMatrix.reset();
        mLocalMatrix.postTranslate(-mPivotX, -mPivotY);
        mLocalMatrix.postScale(mScaleX, mScaleY);
        mLocalMatrix.postRotate(mRotate, 0, 0);
        mLocalMatrix.postTranslate(mTranslateX + mPivotX, mTranslateY + mPivotY);
    }

    /* Setters and Getters, used by animator from AnimatedVectorDrawable. */
    @SuppressWarnings("unused")
    public float getRotation() {
        return mRotate;
    }

    @SuppressWarnings("unused")
    public void setRotation(float rotation) {
        if (rotation != mRotate) {
            mRotate = rotation;
            updateLocalMatrix();
        }
    }

    @SuppressWarnings("unused")
    public float getPivotX() {
        return mPivotX;
    }

    @SuppressWarnings("unused")
    public void setPivotX(float pivotX) {
        if (pivotX != mPivotX) {
            mPivotX = pivotX;
            updateLocalMatrix();
        }
    }

    @SuppressWarnings("unused")
    public float getPivotY() {
        return mPivotY;
    }

    @SuppressWarnings("unused")
    public void setPivotY(float pivotY) {
        if (pivotY != mPivotY) {
            mPivotY = pivotY;
            updateLocalMatrix();
        }
    }

    @SuppressWarnings("unused")
    public float getScaleX() {
        return mScaleX;
    }

    @SuppressWarnings("unused")
    public void setScaleX(float scaleX) {
        if (scaleX != mScaleX) {
            mScaleX = scaleX;
            updateLocalMatrix();
        }
    }

    @SuppressWarnings("unused")
    public float getScaleY() {
        return mScaleY;
    }

    @SuppressWarnings("unused")
    public void setScaleY(float scaleY) {
        if (scaleY != mScaleY) {
            mScaleY = scaleY;
            updateLocalMatrix();
        }
    }

    @SuppressWarnings("unused")
    public float getTranslateX() {
        return mTranslateX;
    }

    @SuppressWarnings("unused")
    public void setTranslateX(float translateX) {
        if (translateX != mTranslateX) {
            mTranslateX = translateX;
            updateLocalMatrix();
        }
    }

    @SuppressWarnings("unused")
    public float getTranslateY() {
        return mTranslateY;
    }

    @SuppressWarnings("unused")
    public void setTranslateY(float translateY) {
        if (translateY != mTranslateY) {
            mTranslateY = translateY;
            updateLocalMatrix();
        }
    }
    /**
     * Obtains styled attributes from the theme, if available, or unstyled
     * resources if the theme is null.
     */
    static TypedArray obtainAttributes(
            Resources res, Resources.Theme theme, AttributeSet set, int[] attrs) {
        if (theme == null) {
            return res.obtainAttributes(set, attrs);
        }
        return theme.obtainStyledAttributes(set, attrs, 0, 0);
    }
}
