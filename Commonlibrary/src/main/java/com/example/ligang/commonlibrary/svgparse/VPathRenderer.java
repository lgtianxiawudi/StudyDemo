package com.example.ligang.commonlibrary.svgparse;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.v4.util.ArrayMap;

/**
 * Created by ligang967 on 16/9/14.
 */

public class VPathRenderer {
    static final String LOGTAG = "VPathRenderer";
    /* Right now the internal data structure is organized as a tree.
     * Each node can be a group node, or a path.
     * A group node can have groups or paths as children, but a path node has
     * no children.
     * One example can be:
     *                 Root Group
     *                /    |     \
     *           Group    Path    Group
     *          /     \             |
     *         Path   Path         Path
     *
     */
    // Variables that only used temporarily inside the draw() call, so there
    // is no need for deep copying.
    private final Path mPath;
    private final Path mRenderPath;
    private static final Matrix IDENTITY_MATRIX = new Matrix();
    private final Matrix mFinalPathMatrix = new Matrix();

    public Paint mStrokePaint;
    public Paint mFillPaint;
    private PathMeasure mPathMeasure;

    /////////////////////////////////////////////////////
    // Variables below need to be copied (deep copy if applicable) for mutation.
    private int mChangingConfigurations;
    public final VGroup mRootGroup;
    public float mBaseWidth = 0;
    public float mBaseHeight = 0;
    public float mViewportWidth = 0;
    public float mViewportHeight = 0;
    int mRootAlpha = 0xFF;
    public String mRootName = null;

    public final ArrayMap<String, Object> mVGTargetsMap = new ArrayMap<String, Object>();

    public VPathRenderer() {
        mRootGroup = new VGroup();
        mPath = new Path();
        mRenderPath = new Path();
    }

    public void setRootAlpha(int alpha) {
        mRootAlpha = alpha;
    }

    public int getRootAlpha() {
        return mRootAlpha;
    }

    // setAlpha() and getAlpha() are used mostly for animation purpose, since
    // Animator like to use alpha from 0 to 1.
    public void setAlpha(float alpha) {
        setRootAlpha((int) (alpha * 255));
    }

    @SuppressWarnings("unused")
    public float getAlpha() {
        return getRootAlpha() / 255.0f;
    }

    public VPathRenderer(VPathRenderer copy) {
        mRootGroup = new VGroup(copy.mRootGroup, mVGTargetsMap);
        mPath = new Path(copy.mPath);
        mRenderPath = new Path(copy.mRenderPath);
        mBaseWidth = copy.mBaseWidth;
        mBaseHeight = copy.mBaseHeight;
        mViewportWidth = copy.mViewportWidth;
        mViewportHeight = copy.mViewportHeight;
        mChangingConfigurations = copy.mChangingConfigurations;
        mRootAlpha = copy.mRootAlpha;
        mRootName = copy.mRootName;
        if (copy.mRootName != null) {
            mVGTargetsMap.put(copy.mRootName, this);
        }
    }

    private void drawGroupTree(VGroup currentGroup, Matrix currentMatrix,
                               Canvas canvas, int w, int h, ColorFilter filter,float process) {
        // Calculate current group's matrix by preConcat the parent's and
        // and the current one on the top of the stack.
        // Basically the Mfinal = Mviewport * M0 * M1 * M2;
        // Mi the local matrix at level i of the group tree.
        currentGroup.mStackedMatrix.set(currentMatrix);

        currentGroup.mStackedMatrix.preConcat(currentGroup.mLocalMatrix);

        // Save the current clip information, which is local to this group.
        canvas.save();

        // Draw the group tree in the same order as the XML file.
        for (int i = 0; i < currentGroup.mChildren.size(); i++) {
            Object child = currentGroup.mChildren.get(i);
            if (child instanceof VGroup) {
                VGroup childGroup = (VGroup) child;
                drawGroupTree(childGroup, currentGroup.mStackedMatrix,
                        canvas, w, h, filter,process);
            } else if (child instanceof VPath) {
                VPath childPath = (VPath) child;
                drawPath(currentGroup, childPath, canvas, w, h, filter,process);
            }
        }

        canvas.restore();
    }

    public void draw(Canvas canvas, int w, int h, ColorFilter filter,float process) {
        // Traverse the tree in pre-order to draw.
        drawGroupTree(mRootGroup, IDENTITY_MATRIX, canvas, w, h, filter,process);
    }

    private void drawPath(VGroup vGroup, VPath vPath, Canvas canvas, int w, int h,
                          ColorFilter filter,float process) {
        final float scaleX = w / mViewportWidth;
        final float scaleY = h / mViewportHeight;
        final float minScale = Math.min(scaleX, scaleY);
        final Matrix groupStackedMatrix = vGroup.mStackedMatrix;

        mFinalPathMatrix.set(groupStackedMatrix);
        mFinalPathMatrix.postScale(scaleX, scaleY);


        final float matrixScale = getMatrixScale(groupStackedMatrix);
        if (matrixScale == 0) {
            // When either x or y is scaled to 0, we don't need to draw anything.
            return;
        }
        vPath.toPath(mPath);
        final Path path = getPathByProcess(mPath,process);

        mRenderPath.reset();

        if (vPath.isClipPath()) {
            mRenderPath.addPath(path, mFinalPathMatrix);
            canvas.clipPath(mRenderPath);
        } else {
            VFullPath fullPath = (VFullPath) vPath;
            if (fullPath.mTrimPathStart != 0.0f || fullPath.mTrimPathEnd != 1.0f) {
                float start = (fullPath.mTrimPathStart + fullPath.mTrimPathOffset) % 1.0f;
                float end = (fullPath.mTrimPathEnd + fullPath.mTrimPathOffset) % 1.0f;

                if (mPathMeasure == null) {
                    mPathMeasure = new PathMeasure();
                }
                mPathMeasure.setPath(mPath, false);

                float len = mPathMeasure.getLength();
                start = start * len;
                end = end * len;
                path.reset();
                if (start > end) {
                    mPathMeasure.getSegment(start, len, path, true);
                    mPathMeasure.getSegment(0f, end, path, true);
                } else {
                    mPathMeasure.getSegment(start, end, path, true);
                }
                path.rLineTo(0, 0); // fix bug in measure
            }
            mRenderPath.addPath(path, mFinalPathMatrix);

            if (fullPath.mFillColor != Color.TRANSPARENT) {
                if (mFillPaint == null) {
                    mFillPaint = new Paint();
                    mFillPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                    mFillPaint.setAntiAlias(true);
                }

                final Paint fillPaint = mFillPaint;
                fillPaint.setColor(applyAlpha(fullPath.mFillColor, fullPath.mFillAlpha));
                fillPaint.setColorFilter(filter);
                canvas.drawPath(mRenderPath, fillPaint);
            }

            if (fullPath.mStrokeColor != Color.TRANSPARENT) {
                if (mStrokePaint == null) {
                    mStrokePaint = new Paint();
                    mStrokePaint.setStyle(Paint.Style.STROKE);
                    mStrokePaint.setAntiAlias(true);
                }

                final Paint strokePaint = mStrokePaint;
                if (fullPath.mStrokeLineJoin != null) {
                    strokePaint.setStrokeJoin(fullPath.mStrokeLineJoin);
                }

                if (fullPath.mStrokeLineCap != null) {
                    strokePaint.setStrokeCap(fullPath.mStrokeLineCap);
                }

                strokePaint.setStrokeMiter(fullPath.mStrokeMiterlimit);
                strokePaint.setColor(applyAlpha(fullPath.mStrokeColor, fullPath.mStrokeAlpha));
                strokePaint.setColorFilter(filter);
                final float finalStrokeScale = minScale * matrixScale;
                strokePaint.setStrokeWidth(fullPath.mStrokeWidth * finalStrokeScale);
                canvas.drawPath(mRenderPath, strokePaint);
            }
        }
    }

    private Path getPathByProcess(Path temPath,float process){
        PathMeasure pathMeasure = new PathMeasure(temPath,false);
        Path path = new Path();
        pathMeasure.getSegment(0,pathMeasure.getLength()*process,path,true);
        return path;
    }

    private static float cross(float v1x, float v1y, float v2x, float v2y) {
        return v1x * v2y - v1y * v2x;
    }

    private float getMatrixScale(Matrix groupStackedMatrix) {
        // Given unit vectors A = (0, 1) and B = (1, 0).
        // After matrix mapping, we got A' and B'. Let theta = the angel b/t A' and B'.
        // Therefore, the final scale we want is min(|A'| * sin(theta), |B'| * sin(theta)),
        // which is (|A'| * |B'| * sin(theta)) / max (|A'|, |B'|);
        // If  max (|A'|, |B'|) = 0, that means either x or y has a scale of 0.
        //
        // For non-skew case, which is most of the cases, matrix scale is computing exactly the
        // scale on x and y axis, and take the minimal of these two.
        // For skew case, an unit square will mapped to a parallelogram. And this function will
        // return the minimal height of the 2 bases.
        float[] unitVectors = new float[]{0, 1, 1, 0};
        groupStackedMatrix.mapVectors(unitVectors);
        float scaleX = (float) Math.hypot(unitVectors[0], unitVectors[1]);
        float scaleY = (float) Math.hypot(unitVectors[2], unitVectors[3]);
        float crossProduct = cross(unitVectors[0], unitVectors[1], unitVectors[2],
                unitVectors[3]);
        float maxScale = Math.max(scaleX, scaleY);

        float matrixScale = 0;
        if (maxScale > 0) {
            matrixScale = Math.abs(crossProduct) / maxScale;
        }
        return matrixScale;
    }
    private static int applyAlpha(int color, float alpha) {
        int alphaBytes = Color.alpha(color);
        color &= 0x00FFFFFF;
        color |= ((int) (alphaBytes * alpha)) << 24;
        return color;
    }
}
