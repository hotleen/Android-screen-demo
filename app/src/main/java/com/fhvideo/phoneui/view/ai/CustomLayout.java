package com.fhvideo.phoneui.view.ai;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fhvideo.FHResource;


public class CustomLayout extends FrameLayout {

    private Context mContext;
    private CustomDrawable background;

    public CustomLayout(@NonNull Context context) {
        super(context);
        initView(context, null, 0);
    }

    public CustomLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView(context, attrs, 0);
    }

    public CustomLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    @SuppressLint("NewApi")
    private void initView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        background = new CustomDrawable(getBackground());
        setBackground(background);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        resetBackgroundHoleArea();
    }

    @SuppressLint("NewApi")
    private void resetBackgroundHoleArea() {
        Path path = null;
        // 以子View为范围构造需要透明显示的区域
        // View view = findViewById(R.id.iv_scan);
        View views = getView("iv_scan");
        if (views != null) {
            path = new Path();
            // 矩形透明区域
            int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
            path.addCircle(screenWidth / 2, dp2Px(mContext, 300), dp2Px(mContext, 162), Path.Direction.CW);
        }
        if (path != null) {
            background.setSrcPath(path);
        }
    }

    /**
     * 设置单个view
     *
     * @param name 控件id名
     */
    protected <T extends View> T getView(String name) {
        T view = findViewById(FHResource.getInstance().getId(mContext, "id", name));
        if (view == null) {
            Log.e("FH_ERROR", "findViewById 出错：" + name);
        }
        return view;
    }

    public int dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
