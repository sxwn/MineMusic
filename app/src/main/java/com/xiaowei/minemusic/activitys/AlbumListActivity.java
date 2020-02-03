package com.xiaowei.minemusic.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.xiaowei.minemusic.R;
import com.xiaowei.minemusic.adapters.MusicListAdapter;
import com.xiaowei.minemusic.helpers.RealmHelp;
import com.xiaowei.minemusic.models.AlbumModel;

public class AlbumListActivity extends BaseActivity {

    public static final String ALBUMID =  "albumId";

    private RecyclerView mRvList;
    private MusicListAdapter mListAdapter;
    private String mAlbumId;
    private RealmHelp mRealmHelp;
    private AlbumModel mAlbumModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_list);

        initData();
        initView();
    }

    private void initData() {
        mAlbumId = getIntent().getStringExtra(ALBUMID);
        mRealmHelp = new RealmHelp();
        mAlbumModel = mRealmHelp.getAlbum(mAlbumId);
    }

    private void initView() {
        initNavBar(true,"专辑歌单",false);

        mRvList = fd(R.id.rv_list);
        mRvList.setLayoutManager(new LinearLayoutManager(this));
        mRvList.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        mListAdapter = new MusicListAdapter(this,null, mAlbumModel.getList());
        mRvList.setAdapter(mListAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealmHelp.close();
    }
}
