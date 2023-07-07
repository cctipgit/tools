package com.hash.coinconvert.ui.fragment.dialog;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;

import androidx.annotation.Nullable;

import com.duxl.baselib.download.DownLoadManager;
import com.duxl.baselib.ui.dialog.BaseDialogFragment;
import com.duxl.baselib.utils.APKManager;
import com.duxl.baselib.utils.DisplayUtil;
import com.duxl.baselib.utils.EmptyUtils;
import com.duxl.baselib.utils.Utils;
import com.hash.coinconvert.BuildConfig;
import com.hash.coinconvert.R;
import com.hash.coinconvert.databinding.DialogVersionBinding;
import com.hash.coinconvert.entity.VersionInfo;
import com.hash.coinconvert.utils.FontUtil;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;

import java.io.File;

public class VersionDialog extends BaseDialogFragment {

    private DialogVersionBinding mBinding;
    private VersionInfo mVersionInfo;
    private int mDownloadCode = Integer.MIN_VALUE;

    public static VersionDialog newInstance(VersionInfo versionInfo) {
        Bundle args = new Bundle();
        args.putSerializable("versionInfo", versionInfo);
        VersionDialog fragment = new VersionDialog();
        fragment.setCancelable2(false);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public boolean getCancelOutside() {
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVersionInfo = (VersionInfo) getArguments().getSerializable("versionInfo");
    }

    @Override
    public int getResId() {
        return R.layout.dialog_version;
    }

    @Override
    public int getWidth() {
        int width = DisplayUtil.getScreenWidth(getContext()) - DisplayUtil.dip2px(getContext(), 32);
        return width;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = DialogVersionBinding.bind(view);
        mBinding.tvMsg.setText(Html.fromHtml(mVersionInfo.description));
        if (mVersionInfo.forceUpdate == 1) {
            mBinding.tvOptionLeft.setVisibility(View.GONE);
        }

        FontUtil.setType(mBinding.tvOptionLeft, FontUtil.FOUNT_TYPE.POPPINS_MEDIUM);
        mBinding.tvOptionLeft.setOnClickListener(v->{
            dismissDialog();
            if (mCancelClickRunnable != null) {
                mCancelClickRunnable.run();
            }
        });

        FontUtil.setType(mBinding.tvOptionRight, FontUtil.FOUNT_TYPE.POPPINS_MEDIUM);
        mBinding.tvOptionRight.setOnClickListener(v->{
            if (BuildConfig.isGooglePlay) {
                openGooglePlay(getContext(), getContext().getPackageName());
            } else {
                //startDownloadApk();
                openBrowerDownloadApk(getContext());
            }

        });
    }

    @Override
    public void onDestroyView() {
        if (mDownloadCode != Integer.MIN_VALUE) {
            DownLoadManager.getInstance().pause(mDownloadCode);
        }
        super.onDestroyView();
    }

    private Runnable mCancelClickRunnable;

    /**
     * 设置取消更新点击回调
     *
     * @param runnable
     */
    public VersionDialog setCancelClickRunnable(Runnable runnable) {
        mCancelClickRunnable = runnable;
        return this;
    }

    /**
     * 跳转GooglePlay应用详情页
     *
     * @param context
     * @param pkgName
     */
    public static void openGooglePlay(Context context, String pkgName) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + context.getPackageName()));
            intent.setPackage("com.google.vending");//这里对应的是谷歌商店，跳转别的商店改成对应的即可
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            } else {//没有应用市场，通过浏览器跳转到Google Play
                Intent intent2 = new Intent(Intent.ACTION_VIEW);
                intent2.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + pkgName));
                if (intent2.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent2);
                } else {
                    //没有Google Play 也没有浏览器
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openBrowerDownloadApk(Context context) {
        //mVersionInfo.downloadUrl = "https://upload.exchange2currency.com/cc1.apk";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(mVersionInfo.downloadUrl));
        context.startActivity(intent);
    }

    /**
     * 下载apk文件
     */
    private void startDownloadApk() {
        //mVersionInfo.downloadUrl = "https://upload.exchange2currency.com/cc1.apk";
        String saveUrl = DownLoadManager.EXTERNAL_FILE_DIR
                + File.separator + "apks" + File.separator + "convert-" + mVersionInfo.versionName + System.currentTimeMillis() + ".apk";

        mBinding.tvOptionRight.setVisibility(View.GONE);
        mBinding.barProgress.setVisibility(View.VISIBLE);
        mDownloadCode = DownLoadManager.getInstance().downloadApk(mVersionInfo.downloadUrl, saveUrl, new FileDownloadLargeFileListener() {
            @Override
            protected void pending(BaseDownloadTask task, long soFarBytes, long totalBytes) {
                if (BuildConfig.DEBUG) {
                    System.out.println("duxl:startDownloadApk.pending# task = " + task + ", soFarBytes = " + soFarBytes + ", totalBytes = " + totalBytes);
                }

            }

            @Override
            protected void progress(BaseDownloadTask task, long soFarBytes, long totalBytes) {
                if (BuildConfig.DEBUG) {
                    System.out.println("duxl:startDownloadApk.progress# task = " + task + ", soFarBytes = " + soFarBytes + ", totalBytes = " + totalBytes);
                }
                if (EmptyUtils.isNotNull(mBinding.barProgress)) {
                    mBinding.barProgress.setProgress((int) (soFarBytes / (double) totalBytes * 100D));
                }
//                if (EmptyUtils.isNotNull(mTvProgress)) {
//                    mTvProgress.setText((int) (soFarBytes / (double) totalBytes * 100D) + "%");
//                }
            }

            @Override
            protected void paused(BaseDownloadTask task, long soFarBytes, long totalBytes) {
                if (BuildConfig.DEBUG) {
                    System.out.println("duxl:startDownloadApk.paused# task = " + task + ", soFarBytes = " + soFarBytes + ", totalBytes = " + totalBytes);
                }
            }

            @Override
            protected void completed(BaseDownloadTask task) {
                if (BuildConfig.DEBUG) {
                    System.out.println("duxl:startDownloadApk.completed# task = " + task);
                }
                //ToastUtils.show(R.string.download_completed);
                if (EmptyUtils.isNotNull(mBinding.barProgress)) {
                    mBinding.barProgress.setVisibility(View.GONE);
                }
                if (EmptyUtils.isNotNull(mBinding.tvOptionRight)) {
                    mBinding.tvOptionRight.setVisibility(View.VISIBLE);
                }

                if (EmptyUtils.isNotNull(mBinding.tvMsg)) {
                    APKManager.installApk(Utils.getApp(), task.getTargetFilePath());
                }
            }

            @Override
            protected void error(BaseDownloadTask task, Throwable e) {
                if (BuildConfig.DEBUG) {
                    System.out.println("duxl:startDownloadApk.error# task = " + task + ", e = " + e);
                }
                //ToastUtils.show(R.string.download_file_failed);
                if (EmptyUtils.isNotNull(mBinding.barProgress)) {
                    mBinding.barProgress.setVisibility(View.GONE);
                }
                if (EmptyUtils.isNotNull(mBinding.tvOptionRight)) {
                    mBinding.tvOptionRight.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected void warn(BaseDownloadTask task) {
                if (BuildConfig.DEBUG) {
                    System.out.println("duxl:startDownloadApk.warn# task = " + task);
                }
            }
        });
    }
}
