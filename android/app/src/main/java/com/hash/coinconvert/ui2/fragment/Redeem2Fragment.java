package com.hash.coinconvert.ui2.fragment;

import android.graphics.Color;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.bumptech.glide.Glide;
import com.duxl.baselib.utils.DisplayUtil;
import com.google.android.material.appbar.AppBarLayout;
import com.hash.coinconvert.R;
import com.hash.coinconvert.base.BaseMVVMFragment;
import com.hash.coinconvert.databinding.FragmentRedeem2Binding;
import com.hash.coinconvert.vm.ProfileViewModel;
import com.hash.coinconvert.vm.Redeem2ViewModel;

import java.util.ArrayList;
import java.util.List;

public class Redeem2Fragment extends BaseMVVMFragment<Redeem2ViewModel, FragmentRedeem2Binding> {
    public Redeem2Fragment() {
        super(R.layout.fragment_redeem2);
    }

    @NonNull
    @Override
    protected FragmentRedeem2Binding bindView(View view) {
        return FragmentRedeem2Binding.bind(view);
    }

    @Override
    protected void initView() {
        initViewPager();
        binding.appbarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int a = Math.abs(verticalOffset) / 2;
                if (a > 255) {
                    a = 255;
                }
                binding.actionBar.setBackgroundColor(Color.argb(a, 119, 71, 242));
            }
        });
        binding.btnHistory.setOnClickListener(v -> navigateTo(Redeem2FragmentDirections.actionFragmentRedeemToFragmentPointsDetails()));
        binding.btnSetting.setOnClickListener(v -> navigateTo(Redeem2FragmentDirections.actionFragmentRedeemToFragmentProfile()));
    }

    private void initViewPager() {
        binding.viewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager(), getLifecycle()));
        binding.viewPager.setUserInputEnabled(false);
        binding.btnDraw.setOnClickListener(v -> changeFragment(((TextView) v)));
        binding.btnRedeem.setOnClickListener(v -> changeFragment(((TextView) v)));
        changeFragment(binding.btnRedeem);
    }

    private void changeFragment(TextView textView) {
        if (textView == binding.btnDraw) {
            binding.viewPager.setCurrentItem(1);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            textView.getPaint().setFakeBoldText(true);

            binding.btnRedeem.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            binding.btnRedeem.getPaint().setFakeBoldText(true);
        } else {
            binding.viewPager.setCurrentItem(0);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            textView.getPaint().setFakeBoldText(true);

            binding.btnDraw.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            binding.btnDraw.getPaint().setFakeBoldText(true);
        }
    }

    @Override
    protected void observer() {
        super.observer();
        observer(profileViewModel.getUserInfo(), user -> {
            Glide.with(binding.imgAvatar).load(ProfileViewModel.getAvatarResId(user.uid)).circleCrop().into(binding.imgAvatar);
            binding.tvName.setText(user.userName);
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
    public Class<? extends Redeem2ViewModel> getVMClass() {
        return Redeem2ViewModel.class;
    }

    private static class ViewPagerAdapter extends FragmentStateAdapter {

        private List<Fragment> fragmentList;

        public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
            fragmentList = new ArrayList<>();
            fragmentList.add(new RedeemRewardListFragment());
            fragmentList.add(new LotteryFragment());
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getItemCount() {
            return fragmentList.size();
        }
    }
}
