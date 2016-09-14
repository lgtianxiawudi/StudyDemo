package com.example.ligang.commonlibrary.svgparse;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by ligang967 on 16/9/14.
 */

public class VPath {

    static final String LOGTAG = "VPath";

    protected PathDataNode[] mNodes = null;
    String mPathName;
    public int mChangingConfigurations;

    public VPath() {
        // Empty constructor.
    }

    public void printVPath(int level) {
        String indent = "";
        for (int i = 0; i < level; i++) {
            indent += "    ";
        }
        Log.v(LOGTAG, indent + "current path is :" + mPathName +
                " pathData is " + NodesToString(mNodes));

    }

    public String NodesToString(PathDataNode[] nodes) {
        String result = " ";
        for (int i = 0; i < nodes.length; i++) {
            result += nodes[i].type + ":";
            float[] params = nodes[i].params;
            for (int j = 0; j < params.length; j++) {
                result += params[j] + ",";
            }
        }
        return result;
    }

    public VPath(VPath copy) {
        mPathName = copy.mPathName;
        mChangingConfigurations = copy.mChangingConfigurations;
        mNodes = PathParser.deepCopyNodes(copy.mNodes);
    }

    public void toPath(Path path) {
        path.reset();
        if (mNodes != null) {
            PathDataNode.nodesToPath(mNodes, path);
        }
    }

    public String getPathName() {
        return mPathName;
    }

    public boolean canApplyTheme() {
        return false;
    }

    public void applyTheme(Resources.Theme t) {
    }

    public boolean isClipPath() {
        return false;
    }

    /* Setters and Getters, used by animator from AnimatedVectorDrawable. */
    @SuppressWarnings("unused")
    public PathDataNode[] getPathData() {
        return mNodes;
    }

    @SuppressWarnings("unused")
    public void setPathData(PathDataNode[] nodes) {
        if (!PathParser.canMorph(mNodes, nodes)) {
            // This should not happen in the middle of animation.
            mNodes = PathParser.deepCopyNodes(nodes);
        } else {
            PathParser.updateNodes(mNodes, nodes);
        }
    }
    /**
     * Obtains styled attributes from the theme, if available, or unstyled
     * resources if the theme is null.
     */
    public TypedArray obtainAttributes(
            Resources res, Resources.Theme theme, AttributeSet set, int[] attrs) {
        if (theme == null) {
            return res.obtainAttributes(set, attrs);
        }
        return theme.obtainStyledAttributes(set, attrs, 0, 0);
    }
}
