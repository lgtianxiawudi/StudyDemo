package com.example.ligang.studydemo.dashboard.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.View;

import com.example.ligang.studydemo.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Stack;

/**
 * Created by ligang967 on 16/9/14.
 */

public class SvgView extends Drawable {
    private static final String SHAPE_CLIP_PATH = "clip-path";
    private static final String SHAPE_GROUP = "group";
    private static final String SHAPE_PATH = "path";
    private static final String SHAPE_VECTOR = "vector";
    private VectorDrawableCompatState state;


    public SvgView(Context context,int resId) throws Exception {



        Resources resources = context.getResources();
        XmlPullParser parser = resources.getXml(resId);
        AttributeSet attrs = Xml.asAttributeSet(parser);

        state = new VectorDrawableCompatState();
        VPathRenderer pathRenderer = new VPathRenderer();
        state.mVPathRenderer = pathRenderer;

        state.mChangingConfigurations = getChangingConfigurations();
        state.mCacheDirty = true;
        inflateInternal(resources, parser, attrs, context.getTheme(),state);
        invalidateSelf();
    }
    private TypedArray obtainAttributes(
            Resources res, Resources.Theme theme, AttributeSet set, int[] attrs) {
        if (theme == null) {
            return res.obtainAttributes(set, attrs);
        }
        return theme.obtainStyledAttributes(set, attrs, 0, 0);
    }
    private void updateStateFromTypedArray(Resources res, Resources.Theme theme, AttributeSet set,XmlPullParser parser, VectorDrawableCompatState state)
            throws XmlPullParserException {
        TypedArray a = obtainAttributes(res, theme,set, AndroidResources.styleable_VectorDrawableTypeArray);
        VPathRenderer pathRenderer = state.mVPathRenderer;

        // Account for any configuration changes.
        // state.mChangingConfigurations |= Utils.getChangingConfigurations(a);

        final int mode = TypedArrayUtils.getNamedInt(a, parser, "tintMode",
                AndroidResources.styleable_VectorDrawable_tintMode, -1);
        state.mTintMode = parseTintModeCompat(mode, PorterDuff.Mode.SRC_IN);

        final ColorStateList tint =
                a.getColorStateList(AndroidResources.styleable_VectorDrawable_tint);
        if (tint != null) {
            state.mTint = tint;
        }

        state.mAutoMirrored = TypedArrayUtils.getNamedBoolean(a, parser, "autoMirrored",
                AndroidResources.styleable_VectorDrawable_autoMirrored, state.mAutoMirrored);

        pathRenderer.mViewportWidth = TypedArrayUtils.getNamedFloat(a, parser, "viewportWidth",
                AndroidResources.styleable_VectorDrawable_viewportWidth,
                pathRenderer.mViewportWidth);

        pathRenderer.mViewportHeight = TypedArrayUtils.getNamedFloat(a, parser, "viewportHeight",
                AndroidResources.styleable_VectorDrawable_viewportHeight,
                pathRenderer.mViewportHeight);

        if (pathRenderer.mViewportWidth <= 0) {
            throw new XmlPullParserException(a.getPositionDescription() +
                    "<vector> tag requires viewportWidth > 0");
        } else if (pathRenderer.mViewportHeight <= 0) {
            throw new XmlPullParserException(a.getPositionDescription() +
                    "<vector> tag requires viewportHeight > 0");
        }

        pathRenderer.mBaseWidth = a.getDimension(
                AndroidResources.styleable_VectorDrawable_width, pathRenderer.mBaseWidth);
        pathRenderer.mBaseHeight = a.getDimension(
                AndroidResources.styleable_VectorDrawable_height, pathRenderer.mBaseHeight);
        if (pathRenderer.mBaseWidth <= 0) {
            throw new XmlPullParserException(a.getPositionDescription() +
                    "<vector> tag requires width > 0");
        } else if (pathRenderer.mBaseHeight <= 0) {
            throw new XmlPullParserException(a.getPositionDescription() +
                    "<vector> tag requires height > 0");
        }

        // shown up from API 11.
        final float alphaInFloat = TypedArrayUtils.getNamedFloat(a, parser, "alpha",
                AndroidResources.styleable_VectorDrawable_alpha, pathRenderer.getAlpha());
        pathRenderer.setAlpha(alphaInFloat);

        final String name = a.getString(AndroidResources.styleable_VectorDrawable_name);
        if (name != null) {
            pathRenderer.mRootName = name;
            pathRenderer.mVGTargetsMap.put(name, pathRenderer);
        }
        a.recycle();
    }
    /**
     * Parses a {@link android.graphics.PorterDuff.Mode} from a tintMode
     * attribute's enum value.
     */
    private static PorterDuff.Mode parseTintModeCompat(int value, PorterDuff.Mode defaultMode) {
        switch (value) {
            case 3:
                return PorterDuff.Mode.SRC_OVER;
            case 5:
                return PorterDuff.Mode.SRC_IN;
            case 9:
                return PorterDuff.Mode.SRC_ATOP;
            case 14:
                return PorterDuff.Mode.MULTIPLY;
            case 15:
                return PorterDuff.Mode.SCREEN;
            case 16:
                return PorterDuff.Mode.ADD;
            default:
                return defaultMode;
        }
    }
    private void inflateInternal(Resources res, XmlPullParser parser, AttributeSet attrs,
                                 Resources.Theme theme,VectorDrawableCompatState state) throws XmlPullParserException, IOException {
        final VPathRenderer pathRenderer = state.mVPathRenderer;
        boolean noPathTag = true;

        // Use a stack to help to build the group tree.
        // The top of the stack is always the current group.
        final Stack<VGroup> groupStack = new Stack<VGroup>();
        groupStack.push(pathRenderer.mRootGroup);

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                final String tagName = parser.getName();
                final VGroup currentGroup = groupStack.peek();
                if (SHAPE_PATH.equals(tagName)) {
                    final VFullPath path = new VFullPath();
                    path.inflate(res, attrs, theme, parser);
                    currentGroup.mChildren.add(path);
                    if (path.getPathName() != null) {
                        pathRenderer.mVGTargetsMap.put(path.getPathName(), path);
                    }
                    noPathTag = false;
                    state.mChangingConfigurations |= path.mChangingConfigurations;
                } else if (SHAPE_CLIP_PATH.equals(tagName)) {
                    final VClipPath path = new VClipPath();
                    path.inflate(res, attrs, theme, parser);
                    currentGroup.mChildren.add(path);
                    if (path.getPathName() != null) {
                        pathRenderer.mVGTargetsMap.put(path.getPathName(), path);
                    }
                    state.mChangingConfigurations |= path.mChangingConfigurations;
                } else if (SHAPE_GROUP.equals(tagName)) {
                    VGroup newChildGroup = new VGroup();
                    newChildGroup.inflate(res, attrs, theme, parser);
                    currentGroup.mChildren.add(newChildGroup);
                    groupStack.push(newChildGroup);
                    if (newChildGroup.getGroupName() != null) {
                        pathRenderer.mVGTargetsMap.put(newChildGroup.getGroupName(),
                                newChildGroup);
                    }
                    state.mChangingConfigurations |= newChildGroup.mChangingConfigurations;
                }else if(SHAPE_VECTOR.equals(tagName)){
                    updateStateFromTypedArray(res, theme, attrs, parser,state);
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                final String tagName = parser.getName();
                if (SHAPE_GROUP.equals(tagName)) {
                    groupStack.pop();
                }
            }
            eventType = parser.next();
        }

        if (noPathTag) {
            final StringBuffer tag = new StringBuffer();

            if (tag.length() > 0) {
                tag.append(" or ");
            }
            tag.append(SHAPE_PATH);

            throw new XmlPullParserException("no " + tag + " defined");
        }
    }

    @Override
    public void draw(Canvas canvas) {
        state.mVPathRenderer.draw(canvas,200,200,null);
    }

    @Override
    public void setAlpha(int i) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSPARENT;
    }
}
