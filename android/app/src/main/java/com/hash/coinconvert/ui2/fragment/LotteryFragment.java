package com.hash.coinconvert.ui2.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;

import com.duxl.baselib.utils.DisplayUtil;
import com.duxl.baselib.utils.ToastUtils;
import com.flyco.roundview.RoundTextView;
import com.flyco.roundview.RoundViewDelegate;
import com.hash.coinconvert.R;
import com.hash.coinconvert.base.BaseMVVMFragment;
import com.hash.coinconvert.databinding.FragmentLotteryBinding;
import com.hash.coinconvert.entity.PinItem;
import com.hash.coinconvert.ui2.dialog.RewardDialog;
import com.hash.coinconvert.utils.ShareHelper;
import com.hash.coinconvert.utils.task.TaskHelper;
import com.hash.coinconvert.vm.LotteryViewModel;
import com.hash.coinconvert.widget.lottery.LotteryView;

public class LotteryFragment extends BaseMVVMFragment<LotteryViewModel, FragmentLotteryBinding> implements LotteryView.OnRotateFinishListener {
    public static final String TAG = "LotteryFragment";

    public LotteryFragment() {
        super(R.layout.fragment_lottery);
    }

    private TextView pointsView;

    @NonNull
    @Override
    protected FragmentLotteryBinding bindView(View view) {
        return FragmentLotteryBinding.bind(view);
    }

    @Override
    protected void onFirstResume() {
        super.onFirstResume();
        profileViewModel.fetchUserInfo();
    }

    @Override
    protected void initView() {
        initMenuPointsView();
        binding.lotteryView.setOnRewardListener(this);
        binding.btnStart.setOnClickListener(v -> {
            if (!binding.lotteryView.isRunning()) {
                profileViewModel.decreasePinNum();
                viewModel.pinCheck();
            }
        });
        binding.lotteryView.setVisibility(View.INVISIBLE);
        binding.btnStart.setVisibility(View.INVISIBLE);
        viewModel.fetchPinList();
    }

    public void setPointsViewText(String points) {
        if (pointsView != null) {
            pointsView.setVisibility(View.VISIBLE);
            pointsView.setText(points);
        }
    }

    private void initMenuPointsView() {
        this.pointsView = genMenuPointsView();
        this.pointsView.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void setupProgressGravity(ProgressBar progressBar) {
        FrameLayout.LayoutParams params = ((FrameLayout.LayoutParams) progressBar.getLayoutParams());
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        params.topMargin = DisplayUtil.dip2px(requireContext(), 40);
        progressBar.setLayoutParams(params);
    }

    private RoundTextView genMenuPointsView() {
        Context context = requireContext();
        RoundTextView roundTextView = new RoundTextView(requireContext());
        roundTextView.setTextColor(ContextCompat.getColor(context, R.color.theme_bg));
        RoundViewDelegate delegate = roundTextView.getDelegate();
        delegate.setCornerRadius(8);
        delegate.setStrokeWidth(2);
        delegate.setStrokeColor(ContextCompat.getColor(context, R.color.orange));

        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_points);
        int size = DisplayUtil.dip2px(context, 20f);
        drawable.setBounds(0, 0, size, size);
        roundTextView.setCompoundDrawables(drawable, null, null, null);
        int dp8 = DisplayUtil.dip2px(context, 8f);
        roundTextView.setCompoundDrawablePadding(dp8);
        roundTextView.setPadding(dp8, dp8 / 2, dp8, dp8 / 2);
        return roundTextView;
    }

    @Override
    protected void observer() {
        super.observer();
        LiveData<String> liveData = getLiveDataInCurrentBackstack(RewardDialog.KEY);
        observer(liveData, s -> ShareHelper.shareText(requireContext(), s));
        observer(viewModel.getPinCheckId(), id -> {
            TaskHelper.pin();
            binding.lotteryView.startRotate(id);
        });

        observer(profileViewModel.getUserInfo(), user -> {
            this.setPointsViewText(user.getFormattedPoint());
            binding.btnStart.setEnabled(user.pinChance > 0);
        });

        observer(viewModel.getPinList(), pinList -> {
            binding.lotteryView.setData(pinList.lists.toArray(new PinItem[0]));
            binding.lotteryView.setVisibility(View.VISIBLE);
            binding.btnStart.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public Class<? extends LotteryViewModel> getVMClass() {
        return LotteryViewModel.class;
    }

    @Override
    public void onRotateFinished(PinItem item) {
        if (item == null) {
            ToastUtils.show(R.string.dialog_reward_error_none_pin_item);
            return;
        }
        profileViewModel.fetchUserInfo();
        RewardDialog dialog = new RewardDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable("data", item);
        dialog.setArguments(bundle);
        dialog.setOnPositiveClickListener(url -> ShareHelper.shareText(requireContext(), url));
        dialog.show(getChildFragmentManager(), "reward");
//        navigateTo(LotteryFragmentDirections.actionFragmentLotteryToDialogReward(item));
    }

    @Override
    protected int getProgressBarTint() {
        return Color.WHITE;
    }
}
