package com.hash.coinconvert.ui2.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.hash.coinconvert.R;
import com.hash.coinconvert.base.BaseMVVMFragment;
import com.hash.coinconvert.databinding.FragmentProfileBinding;
import com.hash.coinconvert.vm.ProfileViewModel;

import java.text.NumberFormat;
import java.util.Calendar;

public class ProfileFragment extends BaseMVVMFragment<ProfileViewModel, FragmentProfileBinding> {

    public ProfileFragment() {
        super(R.layout.fragment_profile);
    }

    @NonNull
    @Override
    protected FragmentProfileBinding bindView(View view) {
        return FragmentProfileBinding.bind(view);
    }

    @Override
    protected void onFirstResume() {
        super.onFirstResume();
        viewModel.fetchUserInfo();
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
        binding.menuSettings.setOnClickListener(v -> getNavController().navigate(ProfileFragmentDirections.actionFragmentProfileToFragmentSetting()));
        binding.menuAbout.setOnClickListener(v -> {
        });
        binding.menuPrivacy.setOnClickListener(v -> {
        });
    }
}
