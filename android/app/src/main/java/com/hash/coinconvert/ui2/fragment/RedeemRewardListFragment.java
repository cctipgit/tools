package com.hash.coinconvert.ui2.fragment;

import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.duxl.baselib.utils.DisplayUtil;
import com.hash.coinconvert.R;
import com.hash.coinconvert.base.BaseMVVMFragment;
import com.hash.coinconvert.databinding.RecyclerViewBinding;
import com.hash.coinconvert.entity.RedeemItem;
import com.hash.coinconvert.ui2.adapter.RedeemAdapter;
import com.hash.coinconvert.vm.RedeemViewModel;

public class RedeemRewardListFragment extends BaseMVVMFragment<RedeemViewModel, RecyclerViewBinding> {

    private RedeemAdapter redeemAdapter;

    public RedeemRewardListFragment() {
        super(R.layout.recycler_view);
    }

    @NonNull
    @Override
    protected RecyclerViewBinding bindView(View view) {
        return RecyclerViewBinding.bind(view);
    }

    @Override
    protected void onFirstResume() {
        super.onFirstResume();
        viewModel.fetchRedeemCards();
    }

    @Override
    protected void initView() {
        redeemAdapter = new RedeemAdapter();
        redeemAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.btn_redeem) {
                RedeemItem item = redeemAdapter.getItem(position);
                viewModel.redeem(item.id);
            }
        });
        binding.getRoot().setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.getRoot().setAdapter(redeemAdapter);
    }

    @Override
    public Class<? extends RedeemViewModel> getVMClass() {
        return RedeemViewModel.class;
    }

    @Override
    protected void observer() {
        super.observer();
        observer(viewModel.getRedeemList(), list -> {
            redeemAdapter.setNewInstance(list);
            profileViewModel.fetchUserInfo();
        });
    }

    @Override
    protected void setupProgressGravity(ProgressBar progressBar) {
        FrameLayout.LayoutParams params = ((FrameLayout.LayoutParams) progressBar.getLayoutParams());
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        params.topMargin = DisplayUtil.dip2px(requireContext(), 40);
        progressBar.setLayoutParams(params);
    }
}
