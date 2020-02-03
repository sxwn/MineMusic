package com.xiaowei.minemusic.activitys;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xiaowei.minemusic.R;
import com.xiaowei.minemusic.adapters.MusicGridAdapter;
import com.xiaowei.minemusic.adapters.MusicListAdapter;
import com.xiaowei.minemusic.helpers.RealmHelp;
import com.xiaowei.minemusic.models.MusicSourceModel;
import com.xiaowei.minemusic.views.GridSpaceItemDecoration;

public class MainActivity extends BaseActivity {

    private RecyclerView mRvGrid,mRvList;
    private MusicGridAdapter mGridAdapter;
    private MusicListAdapter mListAdapter;
    private RealmHelp mRealmHelp;
    private MusicSourceModel mMusicSourceModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        initView();
    }

    private void initData () {
        mRealmHelp = new RealmHelp();
        mMusicSourceModel = mRealmHelp.getMusicSource();
    }

    /**
     * 初始化view
     */
    private void initView () {
        initNavBar(false,"我的音乐",true);

        mRvGrid = fd(R.id.rv_grid);
        mRvGrid.setLayoutManager(new GridLayoutManager(this,3));
//        默认是1dp
//        mRvGrid.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        mRvGrid.addItemDecoration(new GridSpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.ablumMarginSize),mRvGrid));
        mRvGrid.setNestedScrollingEnabled(false);
        mGridAdapter = new MusicGridAdapter(this, mMusicSourceModel.getAlbum());
        mRvGrid.setAdapter(mGridAdapter);

        /**
         * 1、假如已知列表高度的情况下,可以在布局中直接把RecycleView的高度定义上
         * 2、在不知道列表高度的情况下,需要手动计算RecycleView的高度
         */
        mRvList = fd(R.id.rv_list);
        mRvList.setLayoutManager(new LinearLayoutManager(this));
        mRvGrid.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
//        取消RecycleView滑动
        mRvList.setNestedScrollingEnabled(false);
        mListAdapter = new MusicListAdapter(this,mRvList, mMusicSourceModel.getHot());
        mRvList.setAdapter(mListAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealmHelp.close();
    }
}
