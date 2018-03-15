package com.bmqb.bmpatternlockview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.bmqb.patternlockview.PatternLockView;
import com.bmqb.patternlockview.PatternPreviewView;
import com.bmqb.patternlockview.listener.PatternLockViewListener;
import com.bmqb.patternlockview.utils.PatternLockUtils;
import com.bmqb.patternlockview.utils.ResourceUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String FIX_PATTERN = "fix_pattern";
    public static final String CREATE_PATTERN = "create_pattern";
    public static final String CHECK_PATTERN = "check_pattern";

    private String mAction = "";
    private String mLocalKey = "";

    private PatternLockView mPatternLockView;
    private PatternPreviewView mPatternPreview;

    private TextView mTVTips;

    private String mCreatePattern;

    private int mCheckTimes = 3;

    private PatternLockViewListener mPatternLockViewListener = new PatternLockViewListener() {
        @Override
        public void onStarted() {
            Log.d(getClass().getName(), "Pattern drawing started");
            mTVTips.setText("完成后请抬起手指");
        }

        @Override
        public void onProgress(List<PatternLockView.Dot> progressPattern) {
            Log.d(getClass().getName(), "Pattern progress: " +
                PatternLockUtils.patternToString(mPatternLockView, progressPattern));
        }

        @Override
        public void onComplete(List<PatternLockView.Dot> pattern) {
            Log.d(getClass().getName(), "Pattern complete: " +
                PatternLockUtils.patternToString(mPatternLockView, pattern));

            switch (mAction) {
                case FIX_PATTERN:
                    break;
                case CREATE_PATTERN:
                    createPattern(pattern);
                    break;
                case CHECK_PATTERN:
                    checkPattern(pattern);
                    break;
            }
        }

        @Override
        public void onCleared() {
            Log.d(getClass().getName(), "Pattern has been cleared");
        }
    };

    private void createPattern(List<PatternLockView.Dot> pattern) {

        if (pattern.size() < 4) {
            mTVTips.setText("至少需要4个点，请重试");
            mPatternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG);
            mPatternLockView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPatternLockView.clearPattern();
                    mTVTips.setText("请绘制新的解锁手势");
                }
            }, 1500);
            return;
        }

        String patternStr = PatternLockUtils.patternToString(mPatternLockView, pattern);

        if (TextUtils.isEmpty(mCreatePattern)) {
            mCreatePattern = patternStr;
            boolean[][] selected = new boolean[3][3];
            for (PatternLockView.Dot dot : pattern) {
                selected[dot.getRow()][dot.getColumn()] = true;
            }
            mPatternPreview.setSelectedDots(selected);
            mPatternLockView.clearPattern();
            mTVTips.setText("重绘手势以确认");
        } else {
            if (patternStr.equals(mCreatePattern)) {
                SPUtils.savePattern(getApplicationContext(), patternStr);
                mTVTips.setText("设置手势成功");
                mPatternLockView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 800);
            } else {
                mCreatePattern = "";
                mTVTips.setText("两次图案不一致，请重新绘制");
                mPatternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG);

                mPatternLockView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPatternLockView.clearPattern();
                        mPatternPreview.clearPattern();
                        mTVTips.setText("请绘制新的解锁手势");
                    }
                }, 1500);


            }
        }
    }

    private void checkPattern(List<PatternLockView.Dot> pattern) {
        String patternStr = PatternLockUtils.patternToString(mPatternLockView, pattern);

        mCheckTimes--;

        if (mCheckTimes == 0) {
            Toast.makeText(this, "请重新登录", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (patternStr.equals(mLocalKey)) {
            mTVTips.setText("输入正确");
        } else {
            mTVTips.setText("你还有"+mCheckTimes+"次机会");
            mPatternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG);
            mPatternLockView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPatternLockView.clearPattern();
                }
            }, 1500);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        if (getIntent() != null) {
            mAction = getIntent().getStringExtra("ACTION");
            mLocalKey = getIntent().getStringExtra("PATTERN_KEY");
        }

        mPatternPreview = (PatternPreviewView) findViewById(R.id.preview_view);
        mTVTips = (TextView) findViewById(R.id.profile_name);

        if (CHECK_PATTERN.equals(mAction)) {
            mPatternPreview.setVisibility(View.INVISIBLE);
        }

        mPatternLockView = (PatternLockView) findViewById(R.id.patter_lock_view);
        mPatternLockView.setDotCount(3);
        mPatternLockView.setDotNormalSize((int) ResourceUtils.getDimensionInPx(this, R.dimen.pattern_lock_dot_size));
        mPatternLockView.setDotSelectedSize((int) ResourceUtils.getDimensionInPx(this, R.dimen.pattern_lock_dot_selected_size));
        mPatternLockView.setPathWidth((int) ResourceUtils.getDimensionInPx(this, R.dimen.pattern_lock_path_width));
        mPatternLockView.setAspectRatioEnabled(true);
        mPatternLockView.setAspectRatio(PatternLockView.AspectRatio.ASPECT_RATIO_HEIGHT_BIAS);
        mPatternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT);
        mPatternLockView.setDotAnimationDuration(150);
        mPatternLockView.setPathEndAnimationDuration(100);
        mPatternLockView.setCorrectStateColor(ResourceUtils.getColor(this, R.color.white));
        mPatternLockView.setRegularStateColor(ResourceUtils.getColor(this, R.color.colorAccent));
        mPatternLockView.setInStealthMode(false);
        mPatternLockView.setTactileFeedbackEnabled(true);
        mPatternLockView.setInputEnabled(true);
        mPatternLockView.addPatternLockListener(mPatternLockViewListener);
        mPatternLockView.setEnableHapticFeedback(false);

        mPatternPreview.setDotCount(3);

    }

}
