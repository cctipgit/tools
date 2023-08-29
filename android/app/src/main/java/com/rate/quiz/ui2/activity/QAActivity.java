package com.rate.quiz.ui2.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.rate.quiz.R;
import com.rate.quiz.databinding.ActivityQaBinding;
import com.rate.quiz.vm.QAViewModel;

public class QAActivity extends BaseActivity {

    public static final int PROGRESS_RATE = 100;

    private QAViewModel viewModel;
    private ActivityQaBinding binding;

    private ValueAnimator progressBarAnim;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(QAViewModel.class);
        binding = ActivityQaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.progressBar.setMax(PROGRESS_RATE);
        binding.progressBar.setVisibility(View.INVISIBLE);

        viewModel.onCreate();
        viewModel.getProgress().observe(this, progress -> {
            if(progress.progress > progress.max){
                setResult2(RESULT_OK);
                finish();
            }else {
                updateProgressText(progress);
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
        ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this,R.color.theme_text_color)),0,boldEnd,Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        binding.tvProgress.setText(ss);
    }

    private void startProgressBarAnimation(QAViewModel.Progress progress){
        binding.progressBar.setVisibility(View.VISIBLE);
        int now = binding.progressBar.getProgress();
        int dst = (int) (progress.progress*1f/progress.max * PROGRESS_RATE);
        if(progressBarAnim == null){
            progressBarAnim = ObjectAnimator.ofInt(now,dst);
            progressBarAnim.setDuration(400);
            progressBarAnim.addUpdateListener(animation -> binding.progressBar.setProgress((Integer) animation.getAnimatedValue()));
        }else{
            progressBarAnim.setIntValues(now,dst);
        }
        progressBarAnim.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(progressBarAnim != null && progressBarAnim.isRunning()){
            progressBarAnim.cancel();
            progressBarAnim = null;
        }
    }
}
