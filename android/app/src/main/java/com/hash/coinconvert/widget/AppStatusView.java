package com.hash.coinconvert.widget;

import android.view.View;
import android.widget.TextView;

import com.duxl.baselib.ui.status.IRefreshContainer;
import com.duxl.baselib.ui.status.SimpleStatusView;
import com.trello.rxlifecycle4.LifecycleProvider;

import java.lang.ref.SoftReference;

/**
 * 状态View(统一处理一些逻辑)
 * create by duxl 2021/2/5
 */
public class AppStatusView extends SimpleStatusView {

    private SoftReference<LifecycleProvider> mReference;

    public AppStatusView(LifecycleProvider page, IRefreshContainer refreshContainer) {
        super(refreshContainer);
        mReference = new SoftReference<>(page);
    }

    public AppStatusView(LifecycleProvider page, IRefreshContainer refreshContainer, int layoutResId) {
        super(refreshContainer, layoutResId);
        mReference = new SoftReference<>(page);
    }

    public TextView getStateTextView() {
        return mTvStatus;
    }

//    @Override
//    public void showEmpty() {
//        super.showEmpty();
//        setClickListenerIfNotNull(mTvStatus);
//        setClickListenerIfNotNull(mIvStatus);
//    }

    /**
     * 设置按钮点击事件
     *
     * @param view
     */
    @Override
    protected void setClickListenerIfNotNull(View view) {
        if (view == null) {
            return;
        }

        view.setOnClickListener(v -> {
            if (getStatus() == Status.Loading) {
                if (getRefreshContainer().getRefreshLayout().getOnLoadListener() != null) {
                    getRefreshContainer().getRefreshLayout().getOnLoadListener().onLoadingClick();
                }
            } else if (getStatus() == Status.Empty) {
                if (getRefreshContainer().getRefreshLayout().getOnLoadListener() != null) {
                    getRefreshContainer().getRefreshLayout().getOnLoadListener().onEmptyClick();
                }
            } else if (getStatus() == Status.Error) {
                if (!doError(mErrCode)) {
                    if (getRefreshContainer().getRefreshLayout().getOnLoadListener() != null) {
                        getRefreshContainer().getRefreshLayout().getOnLoadListener().onErrorClick(mErrCode);
                    }
                }
            }
        });
    }

    /**
     * 统一处理错误逻辑
     *
     * @param errCode
     * @return 错误是否已处理
     */
    private boolean doError(int errCode) {
        // TODO duxl 统一处理错误代码
        return false;
    }
}
