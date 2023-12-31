package com.rate.quiz.ui2.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.duxl.baselib.utils.DisplayUtil;
import com.rate.quiz.R;
import com.rate.quiz.base.BaseMVVMFragment;
import com.rate.quiz.databinding.ActivityQaBinding;
import com.rate.quiz.vm.ProfileViewModel;
import com.rate.quiz.vm.QAViewModel;

public class QAAllFragment extends BaseMVVMFragment<QAViewModel, ActivityQaBinding> {

    public static final int PROGRESS_RATE = 100;
    private ValueAnimator progressBarAnim;
    private boolean submitted = false;

    public QAAllFragment() {
        super(R.layout.activity_qa);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel.fetchQuestions();
    }

    @NonNull
    @Override
    protected ActivityQaBinding bindView(View view) {
        return ActivityQaBinding.bind(view);
    }

    @Override
    protected void initView() {
        binding.actionBar.setOnBackButtonClickListener(v -> {
            if (submitted) {
                getNavController().popBackStack(R.id.fragment_quiz, false);
            } else {
                navigateBack();
            }
        });
        binding.progressBar.setMax(PROGRESS_RATE);
        binding.progressBar.setVisibility(View.INVISIBLE);
    }

    private View genMenuView() {
        ImageView imageView = new ImageView(requireContext());
        imageView.setImageResource(R.drawable.ic_task_list_wheel);
        int size = DisplayUtil.dip2px(requireContext(), 24);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(size, size));
        return imageView;
    }

    @Override
    protected void observer() {
        super.observer();
        observer(viewModel.getProgress(), progress -> {
            updateProgressText(progress);
            if (progress.progress > progress.max) {
                profileViewModel.fetchUserInfo();
                viewModel.complete();
                navigateTo(QAAllFragmentDirections.actionFragmentQaAllToDialogQaSubmitted());
            } else {
                startProgressBarAnimation(progress);
            }
        });

        observer(viewModel.getQuestions(), questionList -> {
            submitted = questionList.submitted;
            if (questionList.submitted) {
                if (binding.flQa.findViewById(R.id.ll_qa_submitted) == null) {
                    binding.flQa.addView(getLayoutInflater().inflate(R.layout.layout_qa_submitted, binding.flQa, false));
                }
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
