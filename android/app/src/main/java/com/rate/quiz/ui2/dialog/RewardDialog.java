package com.rate.quiz.ui2.dialog;

import android.view.View;

import androidx.lifecycle.SavedStateHandle;

import com.rate.quiz.R;
import com.rate.quiz.databinding.DialogRewardBinding;
import com.rate.quiz.entity.PinItem;

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

    private OnPositiveClickListener listener;

    @Override
    protected void initView() {
        PinItem item = RewardDialogArgs.fromBundle(getArguments()).getData();

        binding.imgReward.setImageResource(item.picResId);
        binding.tvMessage.setText(getString(R.string.dialog_reward_message, item.desc));

        binding.btnClose.setOnClickListener(v -> dismiss());
        binding.btnShare.setOnClickListener(v -> onPositiveClick(new AutoCloseAction() {
            @Override
            public void invoke(SavedStateHandle stateHandle) {
                String url = "https://play.google.com/store/apps/details?id=" + getContext().getPackageName();
                stateHandle.set(KEY, url);
                if (listener != null) {
                    listener.onPositionClick(url);
                }
            }
        }));
    }

    public void setOnPositiveClickListener(OnPositiveClickListener listener){
        this.listener = listener;
    }

    public interface OnPositiveClickListener {
        void onPositionClick(String shareUrl);
    }
}
