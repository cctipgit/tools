package com.hash.coinconvert.ui2.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.duxl.baselib.utils.DisplayUtil;
import com.hash.coinconvert.R;
import com.hash.coinconvert.base.BaseBindingFragment;
import com.hash.coinconvert.base.BaseFragment;
import com.hash.coinconvert.base.BaseMVVMFragment;
import com.hash.coinconvert.databinding.FragmentRedeemBinding;
import com.hash.coinconvert.entity.RedeemItem;
import com.hash.coinconvert.ui2.activity.HomeActivity;
import com.hash.coinconvert.ui2.adapter.RedeemAdapter;
import com.hash.coinconvert.vm.RedeemViewModel;

public class RedeemFragment extends BaseMVVMFragment<RedeemViewModel, FragmentRedeemBinding> {

    public static final String SHOW_NAVIGATION = "showNavigation";

    private RedeemAdapter redeemAdapter;
    private boolean showNavigation;
    public RedeemFragment() {
        super(R.layout.fragment_redeem);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        showNavigation = !(context instanceof HomeActivity);
    }
    @Override
    public void initView() {
        redeemAdapter = new RedeemAdapter();
        redeemAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.btn_redeem) {
                RedeemItem item = redeemAdapter.getItem(position);
                viewModel.redeem(item.id);
            }
        });
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerview.setAdapter(redeemAdapter);
        binding.clButtons.setOnTouchListener((var v, @SuppressLint("ClickableViewAccessibility") var event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                int width = v.getWidth();
                float x = event.getX();
                if (x < width / 2) {
                    navigateTo(RedeemFragmentDirections.actionFragmentRedeemToFragmentPointsDetails());
                } else {
                    navigateTo(RedeemFragmentDirections.actionFragmentRedeemToFragmentRedeemHistory());
                }
            }
            return true;
        });
        updateNavigationButton();
    }

    private void updateNavigationButton() {
        binding.actionBar.setBackViewVisible(showNavigation);
        binding.actionBar.setFitsSystemWindows(false);
        binding.actionBar.setBackViewTintColor(ContextCompat.getColor(requireContext(), R.color.theme_bg));
    }

    @Override
    protected void observer() {
        super.observer();
        observer(viewModel.getRedeemList(), list -> {
            redeemAdapter.setNewInstance(list);
            profileViewModel.fetchUserInfo();
        });
        observer(profileViewModel.getUserInfo(), user -> {
            //adjust text size in case of overflow
            String text = user.getFormattedPoint();
            TextPaint paint = binding.tvTotalPoints.getPaint();
            float maxWidth = DisplayUtil.getScreenWidth(requireContext()) * 308f / 388f * 0.75f;
            while (binding.tvTotalPoints.getPaint().measureText(text) > maxWidth) {
                paint.setTextSize(paint.getTextSize() * 0.95f);
            }
            binding.tvTotalPoints.setText(text);
        });
    }

    @Override
    public Class<? extends RedeemViewModel> getVMClass() {
        return RedeemViewModel.class;
    }

    @Override
    public FragmentRedeemBinding bindView(@NonNull View view) {
        return FragmentRedeemBinding.bind(view);
    }
}
