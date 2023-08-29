package com.rate.quiz.ui2.fragment;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.rate.quiz.web.WebActivity;
import com.rate.quiz.R;
import com.rate.quiz.base.BaseMVVMFragment;
import com.rate.quiz.databinding.FragmentProfileBinding;
import com.rate.quiz.vm.ProfileViewModel;

import java.util.Calendar;

public class ProfileFragment extends BaseMVVMFragment<ProfileViewModel, FragmentProfileBinding> {

    public static final int REQUEST_CODE_QA = 1815;

    public ProfileFragment() {
        super(R.layout.fragment_profile);
    }

    @NonNull
    @Override
    protected FragmentProfileBinding bindView(View view) {
        return FragmentProfileBinding.bind(view);
    }

    @Override
    protected void observer() {
        super.observer();
        viewModel.getUserInfo().observe(getViewLifecycleOwner(), userInfo -> {
            Glide.with(binding.imgAvatar).load(ProfileViewModel.getAvatarResId(userInfo.uid)).circleCrop().into(binding.imgAvatar);
            binding.tvName.setText(userInfo.userName);
            binding.tvCreateTime.setText(getCreateTimeString(userInfo.created));
            binding.tvPoints.setText(getString(R.string.profile_points_amount, userInfo.getFormattedPoint()));
        });
    }

    private String getCreateTimeString(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return getString(R.string.profile_format_create_time, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR));
    }

    @Override
    public Class<? extends ProfileViewModel> getVMClass() {
        return ProfileViewModel.class;
    }

    @Override
    protected void initView() {
        binding.menuSettings.setOnClickListener(v -> navigateTo(ProfileFragmentDirections.actionFragmentProfileToFragmentSetting()));
        binding.menuAbout.setOnClickListener(v -> WebActivity.load(requireContext(), "https://www.freeprivacypolicy.com/live/c5ebf19a-fad0-435e-9037-40e48ddf285c"));
        binding.menuPrivacy.setOnClickListener(v -> WebActivity.load(requireContext(), "https://www.freeprivacypolicy.com/live/7864f2dc-fee8-4572-8063-8ca6be4d1ad0"));
    }

    @Override
    protected void onCommonActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onCommonActivityResult", "onCommonActivityResult");
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_QA) {
            profileViewModel.fetchUserInfo();
        }
    }
}
