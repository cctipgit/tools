package com.hash.coinconvert.ui2.fragment;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.hash.coinconvert.R;
import com.hash.coinconvert.base.BaseMVVMFragment;
import com.hash.coinconvert.databinding.FragmentLotteryBinding;
import com.hash.coinconvert.ui2.dialog.RewardDialog;
import com.hash.coinconvert.vm.LotteryViewModel;
import com.hash.coinconvert.widget.lottery.LotteryView;

public class LotteryFragment extends BaseMVVMFragment<LotteryViewModel, FragmentLotteryBinding> implements LotteryView.OnRotateFinishListener {
    public LotteryFragment() {
        super(R.layout.fragment_lottery);
    }

    @NonNull
    @Override
    protected FragmentLotteryBinding bindView(View view) {
        return FragmentLotteryBinding.bind(view);
    }

    @Override
    protected void initView() {
        binding.lotteryView.setOnRewardListener(this);
        binding.btnStart.setOnClickListener(v->{

            getNavController().navigate(LotteryFragmentDirections.actionFragmentLotteryToDialogReward());
//            if(!binding.lotteryView.isRunning()){
//                binding.lotteryView.test();
//            }
        });
    }

    @Override
    protected void observer() {
        super.observer();
        LiveData<String> liveData = getLiveDataInCurrentBackstack(RewardDialog.KEY);
        observer(liveData,s->{
            Log.d("RewardDialog","s:"+s);

        });
    }

    @Override
    public Class<? extends LotteryViewModel> getVMClass() {
        return LotteryViewModel.class;
    }

    @Override
    public void onRotateFinished() {
        Log.d("LotteryFragment","onRotateFinished");
        getNavController().navigate(LotteryFragmentDirections.actionFragmentLotteryToDialogReward());
    }
}
