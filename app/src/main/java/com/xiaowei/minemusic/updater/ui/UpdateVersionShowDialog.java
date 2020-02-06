package com.xiaowei.minemusic.updater.ui;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.xiaowei.minemusic.R;
import com.xiaowei.minemusic.activitys.MainActivity;
import com.xiaowei.minemusic.entity.DownloadBean;
import com.xiaowei.minemusic.updater.AppUpdater;
import com.xiaowei.minemusic.updater.net.INetDownLoadCallBack;
import com.xiaowei.minemusic.utils.AppUtils;

import java.io.File;

public class UpdateVersionShowDialog extends DialogFragment {

    private static final String KEY_DOWNLOAD_BEAN = "download_bean";

    private DownloadBean mDownloadBean;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mDownloadBean = (DownloadBean) arguments.getSerializable(KEY_DOWNLOAD_BEAN);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_updater, container, false);
        bindEvents(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void bindEvents(View view) {
        TextView tvTitle = view.findViewById(R.id.tv_title);
        TextView tvContent = view.findViewById(R.id.tv_content);
        TextView tvUpdate = view.findViewById(R.id.tv_update);

        tvTitle.setText(mDownloadBean.title);
        tvContent.setText(mDownloadBean.content);
        tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                //                debug可以查看私有目录,release没有
                File targetFile = new File(getActivity().getCacheDir(), "target.apk");
                AppUpdater.getInstance().getNetManager().download(mDownloadBean.url, targetFile, new INetDownLoadCallBack() {
                    @Override
                    public void success(File apkFile) {
//                        安装的代码
                        v.setEnabled(true);
                        Log.d("weip", "success = " + apkFile.getAbsolutePath());

                        dismiss();
//                      checkMd5
                        AppUtils.installApk(getActivity(), apkFile);
                    }

                    @Override
                    public void progress(int progress) {
//                       更新页面
                        Log.d("weip", "progress = " + progress);
                        tvUpdate.setText(progress + "%");
                    }

                    @Override
                    public void failed(Throwable throwable) {
                        v.setEnabled(true);
                        Toast.makeText(getActivity(), "文件下载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public static void show(FragmentActivity activity, DownloadBean bean) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_DOWNLOAD_BEAN, bean);
        UpdateVersionShowDialog dialog = new UpdateVersionShowDialog();
        dialog.setArguments(bundle);
        dialog.show(activity.getSupportFragmentManager(), "updateVersionShowDialog");
    }
}
