package com.hash.coinconvert.ui2.fragment;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.hash.coinconvert.R;
import com.hash.coinconvert.base.BaseBindingFragment;
import com.hash.coinconvert.base.BaseFragment;
import com.hash.coinconvert.base.BaseMVVMFragment;
import com.hash.coinconvert.databinding.FragmentRedeemBinding;
import com.hash.coinconvert.vm.RedeemViewModel;

public class RedeemFragment extends BaseMVVMFragment<RedeemViewModel, FragmentRedeemBinding> {
    public RedeemFragment() {
        super(R.layout.fragment_redeem);
    }

    @Override
    public void initView() {

    }

    @Override
    protected void onFirstResume() {
        super.onFirstResume();
        viewModel.fetchRedeemCards();
    }

    @Override
    protected void observer() {
        super.observer();
        observer(viewModel.getRedeemList(), list -> {
            list.forEach(redeemItem -> Log.d("Redeem", redeemItem.reward + "," + redeemItem.pic));
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
