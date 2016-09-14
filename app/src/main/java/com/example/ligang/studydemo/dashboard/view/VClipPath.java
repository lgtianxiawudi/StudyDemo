package com.example.ligang.studydemo.dashboard.view;

/**
 * Created by ligang967 on 16/9/14.
 */

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.graphics.drawable.*;
import android.util.AttributeSet;

import org.xmlpull.v1.XmlPullParser;

public class VClipPath extends VPath {
    public VClipPath() {
        // Empty constructor.
    }

    public VClipPath(VClipPath copy) {
        super(copy);
    }

    public void inflate(Resources r, AttributeSet attrs, Resources.Theme theme, XmlPullParser parser) {
        // TODO TINT THEME Not supported yet
        final boolean hasPathData = TypedArrayUtils.hasAttribute(parser, "pathData");
        if (!hasPathData) {
            return;
        }
        final TypedArray a = obtainAttributes(r, theme, attrs,
                AndroidResources.styleable_VectorDrawableClipPath);
        updateStateFromTypedArray(a);
        a.recycle();
    }

    private void updateStateFromTypedArray(TypedArray a) {
        // Account for any configuration changes.
        // mChangingConfigurations |= Utils.getChangingConfigurations(a);;

        final String pathName =
                a.getString(AndroidResources.styleable_VectorDrawableClipPath_name);
        if (pathName != null) {
            mPathName = pathName;
        }

        final String pathData =
                a.getString(AndroidResources.styleable_VectorDrawableClipPath_pathData);
        if (pathData != null) {
            mNodes = PathParser.createNodesFromPathData(pathData);
        }
    }

    @Override
    public boolean isClipPath() {
        return true;
    }
}
