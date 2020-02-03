package com.xiaowei.minemusic.views;

import android.graphics.Rect;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GridSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int mSpace;

    public GridSpaceItemDecoration(int space, RecyclerView parent) {
        mSpace = space;
        getRecycleViewOffsets(parent);
    }

    /**
     * 设置Item偏移量
     * @param outRect Item的矩形边界
     * @param view  ItemView
     * @param parent RecycleView
     * @param state RecycleView的状态
     */
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.left = mSpace;
//        判断Item是不是每一行第一个Item
//        if (parent.getChildLayoutPosition(view) % 3 == 0){
//            outRect.left = 0;
//        }
    }

    private void getRecycleViewOffsets(RecyclerView parent){
//        View margin
//        margin为正,则View 距离边界产生一个距离
//        margin为负,则View 会超出边界产生一个距离
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) parent.getLayoutParams();
        layoutParams.leftMargin = -mSpace;
        parent.setLayoutParams(layoutParams);
    }
}
