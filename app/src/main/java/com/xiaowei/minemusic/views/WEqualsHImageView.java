package com.xiaowei.minemusic.views;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

/**
 * 宽高相等的ImageView
 */
public class WEqualsHImageView extends AppCompatImageView {

    public WEqualsHImageView(Context context) {
        super(context);
    }

    public WEqualsHImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WEqualsHImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
//        获取View宽度
//        int width = MeasureSpec.getSize(widthMeasureSpec);
//        获取View模式,三种match_parent,wrap_content,具体dp
//        int mode = MeasureSpec.getMode(widthMeasureSpec);

    }
}
