package com.xiaowei.minemusic.activitys;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xiaowei.libpermission.PermissionGrant;
import com.xiaowei.minemusic.R;
import com.xiaowei.minemusic.adapters.MusicGridAdapter;
import com.xiaowei.minemusic.adapters.MusicListAdapter;
import com.xiaowei.minemusic.entity.DownloadBean;
import com.xiaowei.minemusic.helpers.RealmHelp;
import com.xiaowei.minemusic.models.MusicSourceModel;
import com.xiaowei.minemusic.updater.AppUpdater;
import com.xiaowei.minemusic.updater.net.INetCallBack;
import com.xiaowei.minemusic.updater.net.INetDownLoadCallBack;
import com.xiaowei.minemusic.updater.ui.UpdateVersionShowDialog;
import com.xiaowei.minemusic.utils.AppUtils;
import com.xiaowei.minemusic.views.GridSpaceItemDecoration;

import java.io.File;

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
//        checkVersion();
        checkPermission();
    }

    private void checkPermission() {

    }

    @PermissionGrant(1)
    private void onRequestWriteStorageGranted() {
        Toast.makeText(MainActivity.this, "写权限已申请", Toast.LENGTH_SHORT).show();
    }

    private void checkVersion() {
        AppUpdater.getInstance().getNetManager().get("http://59.110.162.30/app_updater_version.json", new INetCallBack() {
            @Override
            public void success(String response) {
                Log.d("weip",response);
//                1、解析json
//                http://59.110.162.30/app_updater_version.json
//                {
//                    "title":"4.5.0更新啦！",
//                        "content":"1. 优化了阅读体验；\n2. 上线了 hyman 的课程；\n3. 修复了一些已知问题。",
//                        "url":"http://59.110.162.30/v450_imooc_updater.apk",
//                        "md5":"14480fc08932105d55b9217c6d2fb90b",
//                        "versionCode":"450"
//                }
//                2、做版本匹配
//                如果需要更新
//                3、弹框
//                4、点击下载

                DownloadBean bean = DownloadBean.parse(response);

                if (bean == null) {
                    Toast.makeText(MainActivity.this, "版本检测接口返回数据异常", Toast.LENGTH_SHORT).show();
                    return;
                }

//                检测：是否需要弹框
                try {
                    long versionCode = Long.parseLong(bean.versionCode);
                    if (versionCode <= AppUtils.getVersionCode(MainActivity.this)) {
                        Toast.makeText(MainActivity.this, "已经是最新版本，无需更新", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "版本检测接口返回版本号异常", Toast.LENGTH_SHORT).show();
                    return;
                }

//                弹框
//                UpdateVersionShowDialog.show(MainActivity.this, bean);
            }

            @Override
            public void failed(Throwable throwable) {
                throwable.printStackTrace();
                Toast.makeText(MainActivity.this, "版本更新接口请求失败", Toast.LENGTH_SHORT).show();
            }
        },MainActivity.this);
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

        AppUpdater.getInstance().getNetManager().cacel(this);
    }
}
