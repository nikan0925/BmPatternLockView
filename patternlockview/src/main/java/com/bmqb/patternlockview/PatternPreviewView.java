package com.bmqb.patternlockview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.annotation.Dimension;
import android.util.AttributeSet;
import android.view.View;

import com.bmqb.patternlockview.utils.ResourceUtils;

public class PatternPreviewView extends View {

    private Paint mDotPaint;

    private float mViewWidth;
    private float mViewHeight;

    private static int sDotCount = 3;
    private int mDotNormalSize;
    private int mNormalStateColor;
    private int mSelectedStateColor;

    private PatternLockView.DotState[][] mDotStates;

    private boolean[][] mSelectedDots;

    public PatternPreviewView(Context context) {
        this(context, null);
    }

    public PatternPreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.PatternPreviewView);
        try {
            sDotCount = typedArray.getInt(R.styleable.PatternPreviewView_pDotCount, 3);
            mNormalStateColor =
                typedArray.getColor(R.styleable.PatternPreviewView_pNormalStateColor,
                    ResourceUtils.getColor(getContext(), R.color.white));
            mSelectedStateColor =
                typedArray.getColor(R.styleable.PatternPreviewView_pSelectedStateColor,
                    ResourceUtils.getColor(getContext(), R.color.pomegranate));
            mDotNormalSize =
                (int) typedArray.getDimension(R.styleable.PatternPreviewView_pDotNormalSize,
                    ResourceUtils.getDimensionInPx(getContext(), R.dimen.pattern_lock_dot_size));
        } finally {
            typedArray.recycle();
        }

        mDotStates = new PatternLockView.DotState[sDotCount][sDotCount];
        mSelectedDots = new boolean[sDotCount][sDotCount];
        for (int i = 0; i < sDotCount; i++) {
            for (int j = 0; j < sDotCount; j++) {
                mDotStates[i][j] = new PatternLockView.DotState();
                mDotStates[i][j].mSize = mDotNormalSize;
                mSelectedDots[i][j] = false;
            }
        }

        initView();
    }

    private void initView() {
        mDotPaint = new Paint();
        mDotPaint.setAntiAlias(true);
        mDotPaint.setDither(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        for (int i = 0; i < sDotCount; i++) {
            float centerY = getCenterYForRow(i);
            for (int j = 0; j < sDotCount; j++) {
                PatternLockView.DotState dotState = mDotStates[i][j];
                float centerX = getCenterXForColumn(j);
                float size = dotState.mSize * dotState.mScale;
                float translationY = dotState.mTranslateY;
                drawCircle(canvas, (int) centerX, (int) centerY + translationY, size,
                    dotState.mAlpha, mSelectedDots[i][j]);
            }
        }
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        int adjustedWidth = width - getPaddingLeft() - getPaddingRight();
        mViewWidth = adjustedWidth / (float) sDotCount;

        int adjustedHeight = height - getPaddingTop() - getPaddingBottom();
        mViewHeight = adjustedHeight / (float) sDotCount;
    }

    private int resolveMeasured(int measureSpec, int desired) {
        int result;
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (MeasureSpec.getMode(measureSpec)) {
            case MeasureSpec.UNSPECIFIED:
                result = desired;
                break;
            case MeasureSpec.AT_MOST:
                result = Math.max(specSize, desired);
                break;
            case MeasureSpec.EXACTLY:
            default:
                result = specSize;
        }
        return result;
    }

    private float getCenterXForColumn(int column) {
        return getPaddingLeft() + column * mViewWidth + mViewWidth / 2f;
    }

    private float getCenterYForRow(int row) {
        return getPaddingTop() + row * mViewHeight + mViewHeight / 2f;
    }

    private void drawCircle(Canvas canvas, float centerX, float centerY, float size, float alpha,
                            boolean isSelected) {
        if (isSelected) {
            mDotPaint.setColor(mSelectedStateColor);
        } else {
            mDotPaint.setColor(mNormalStateColor);
        }
        mDotPaint.setAlpha((int) (alpha * 255));
        canvas.drawCircle(centerX, centerY, size / 2, mDotPaint);
    }

    public void setSelectedDots(boolean[][] selects) {
        mSelectedDots = selects;
        invalidate();
    }

    public void setNormalStateColor(@ColorInt int normalStateColor) {
        mNormalStateColor = normalStateColor;
    }

    public void setSelectedStateColor(@ColorInt int selectedStateColor) {
        mSelectedStateColor = selectedStateColor;
    }

    public void setDotCount(int dotCount) {
        sDotCount = dotCount;

        mDotStates = new PatternLockView.DotState[sDotCount][sDotCount];
        mSelectedDots = new boolean[sDotCount][sDotCount];
        for (int i = 0; i < sDotCount; i++) {
            for (int j = 0; j < sDotCount; j++) {
                mDotStates[i][j] = new PatternLockView.DotState();
                mDotStates[i][j].mSize = mDotNormalSize;
            }
        }

        requestLayout();
        invalidate();
    }

    public void setDotNormalSize(@Dimension int dotNormalSize) {
        mDotNormalSize = dotNormalSize;

        for (int i = 0; i < sDotCount; i++) {
            for (int j = 0; j < sDotCount; j++) {
                mDotStates[i][j] = new PatternLockView.DotState();
                mDotStates[i][j].mSize = mDotNormalSize;
            }
        }

        invalidate();
    }

    public void clearPattern() {
        for (int i = 0; i < sDotCount; i++) {
            for (int j = 0; j < sDotCount; j++) {
                mSelectedDots[i][j] = false;
            }
        }

        invalidate();
    }
}
