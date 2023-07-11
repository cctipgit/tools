package com.hash.coinconvert.ui2.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.duxl.baselib.utils.ToastUtils;
import com.hash.coinconvert.R;
import com.hash.coinconvert.base.BaseMVVMFragment;
import com.hash.coinconvert.databinding.ActivityQaBinding;
import com.hash.coinconvert.vm.ProfileViewModel;
import com.hash.coinconvert.vm.QAViewModel;

public class QAAllFragment extends BaseMVVMFragment<QAViewModel, ActivityQaBinding> {

    public static final int PROGRESS_RATE = 100;
    private ValueAnimator progressBarAnim;

    public QAAllFragment() {
        super(R.layout.activity_qa);
    }

    @NonNull
    @Override
    protected ActivityQaBinding bindView(View view) {
        return ActivityQaBinding.bind(view);
    }

    @Override
    protected void initView() {
        binding.progressBar.setMax(PROGRESS_RATE);
        binding.progressBar.setVisibility(View.INVISIBLE);

        viewModel.fetchQuestions();
        viewModel.getProgress().observe(this, progress -> {
            updateProgressText(progress);
            if (progress.progress > progress.max) {
                profileViewModel.fetchUserInfo();
                viewModel.complete();
                ToastUtils.show(R.string.qa_submit_succ);
            } else {
                startProgressBarAnimation(progress);
            }
        });
    }

    private void updateProgressText(QAViewModel.Progress progress) {
        String s = getString(R.string.qa_progress, progress.progress, progress.max);
        SpannableString ss = new SpannableString(s);
        String ps = String.valueOf(progress.progress);
        int boldEnd = s.indexOf(ps) + ps.length();
        ss.setSpan(new StyleSpan(Typeface.BOLD), 0, boldEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.theme_text_color)), 0, boldEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        binding.tvProgress.setText(ss);
    }

    private void startProgressBarAnimation(QAViewModel.Progress progress) {
        binding.progressBar.setVisibility(View.VISIBLE);
        int now = binding.progressBar.getProgress();
        int dst = (int) (progress.progress * 1f / progress.max * PROGRESS_RATE);
        if (progressBarAnim == null) {
            progressBarAnim = ObjectAnimator.ofInt(now, dst);
            progressBarAnim.setDuration(400);
            progressBarAnim.addUpdateListener(animation -> binding.progressBar.setProgress((Integer) animation.getAnimatedValue()));
        } else {
            progressBarAnim.setIntValues(now, dst);
        }
        progressBarAnim.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (progressBarAnim != null && progressBarAnim.isRunning()) {
            progressBarAnim.cancel();
            progressBarAnim = null;
        }
    }

    @Override
    protected void initViewModel() {
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        viewModel = new ViewModelProvider(requireActivity()).get(getVMClass());
    }

    @Override
    public Class<? extends QAViewModel> getVMClass() {
        return QAViewModel.class;
    }
}
