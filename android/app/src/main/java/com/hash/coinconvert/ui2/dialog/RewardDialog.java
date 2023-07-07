package com.hash.coinconvert.ui2.dialog;

import android.view.View;

import androidx.lifecycle.SavedStateHandle;

import com.hash.coinconvert.R;
import com.hash.coinconvert.databinding.DialogRewardBinding;
import com.hash.coinconvert.entity.PinItem;

public class RewardDialog extends BaseFragmentDialog<DialogRewardBinding> {

    public static final String KEY = "reward_share";

    @Override
    public int getLayoutId() {
        return R.layout.dialog_reward;
    }

    @Override
    public DialogRewardBinding bind(View view) {
        return DialogRewardBinding.bind(view);
    }

    @Override
    protected void initView() {
        PinItem item = RewardDialogArgs.fromBundle(getArguments()).getData();

        binding.imgReward.setImageResource(item.picResId);
        binding.tvMessage.setText(getString(R.string.dialog_reward_message,item.desc));

        binding.btnClose.setOnClickListener(v->dismiss());
        binding.btnShare.setOnClickListener(v->onPositiveClick(new AutoCloseAction() {
            @Override
            public void invoke(SavedStateHandle stateHandle) {
                stateHandle.set(KEY,"https://www.ratetool.com");
            }
        }));
    }
}
