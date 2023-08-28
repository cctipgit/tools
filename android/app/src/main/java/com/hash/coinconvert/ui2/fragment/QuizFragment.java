package com.hash.coinconvert.ui2.fragment;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.hash.coinconvert.R;
import com.hash.coinconvert.base.BaseMVVMFragment;
import com.hash.coinconvert.databinding.FragmentQuizHomeBinding;
import com.hash.coinconvert.vm.ProfileViewModel;
import com.hash.coinconvert.vm.QAViewModel;

public class QuizFragment extends BaseMVVMFragment<QAViewModel, FragmentQuizHomeBinding> {

    private ProfileViewModel userViewModel;

    public QuizFragment() {
        super(R.layout.fragment_quiz_home);
    }

    @NonNull
    @Override
    protected FragmentQuizHomeBinding bindView(View view) {
        return FragmentQuizHomeBinding.bind(view);
    }

    @Override
    protected void initView() {
        userViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        binding.btnPlay.setOnClickListener(v -> {
            navigateTo(QuizFragmentDirections.actionFragmentQuizToFragmentQaAll());
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.fetchQuestions();
    }

    @Override
    public Class<? extends QAViewModel> getVMClass() {
        return QAViewModel.class;
    }

    @Override
    protected void observer() {
        super.observer();
        userViewModel.getUserInfo().observe(getViewLifecycleOwner(), userInfo -> {
            Glide.with(binding.imgAvatar).load(ProfileViewModel.getAvatarResId(userInfo.uid)).circleCrop().into(binding.imgAvatar);
            binding.tvName.setText(userInfo.userName);
        });
        viewModel.getQuestions().observe(getViewLifecycleOwner(), questionList -> {
            String s = questionList.submitted ? "1" : "0";
            SpannableString ss = new SpannableString(s + "/1");
            ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.colorDisable)), 1, ss.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            binding.tvTimes.setText(ss);
            binding.btnPlay.setEnabled(!questionList.submitted);
        });
    }
}
