package com.rate.quiz.ui2.fragment;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.duxl.baselib.utils.DisplayUtil;
import com.rate.quiz.R;
import com.rate.quiz.base.BaseMVVMFragment;
import com.rate.quiz.databinding.FragmentQaBinding;
import com.rate.quiz.entity.AnswerBody;
import com.rate.quiz.entity.QuestionItem;
import com.rate.quiz.entity.QuestionsItem;
import com.rate.quiz.vm.QAViewModel;
import com.rate.quiz.widget.QAItemView;

import java.util.ArrayList;
import java.util.List;

public class QAFragment extends BaseMVVMFragment<QAViewModel, FragmentQaBinding> implements QAItemView.OnAnswerChangeListener {

    private int questionIndex;
    private boolean lastQuestion;

    public QAFragment() {
        super(R.layout.fragment_qa);
    }

    @NonNull
    @Override
    protected FragmentQaBinding bindView(View view) {
        return FragmentQaBinding.bind(view);
    }

    @Override
    protected void initView() {
        questionIndex = QAFragmentArgs.fromBundle(getArguments()).getIndex();
        binding.btnBack.setOnClickListener(v -> navigateBack());
        binding.btnNext.setOnClickListener(v -> {
            viewModel.answer(getAnswersBody(), lastQuestion);
            if (!lastQuestion) {
                navigateTo(QAFragmentDirections.actionFragmentQaToFragmentQa().setIndex(questionIndex + 1));
            }
        });
        binding.btnBack.setVisibility(questionIndex == 0 ? View.GONE : View.VISIBLE);
        binding.btnNext.setEnabled(false);
        //for first page loading
        if (questionIndex == 0) {
            binding.getRoot().setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void observer() {
        super.observer();
        observer(viewModel.getQuestions(), questionList -> {
            List<QuestionsItem> list = questionList.lists;
            binding.getRoot().setVisibility(View.VISIBLE);
            binding.llQuestions.removeAllViews();
            //index is start from 0,show text must start from 1
            viewModel.updateProgress(questionIndex + 1);

            lastQuestion = questionIndex == list.size() - 1;
            binding.btnNext.setText(lastQuestion ? R.string.qa_btn_done : R.string.qa_btn_next);

            QuestionsItem item = list.get(questionIndex);
            binding.llQuestions.addView(genTitle(item.title));
            boolean single = item.questions.size() <= 1;
            List<String> questionIds = new ArrayList<>();
            for (QuestionItem i : item.questions) {
                questionIds.add(i.id);
                QAItemView view = new QAItemView(requireContext());
                view.setOnAnswerChangeListener(this);
                view.setQuestion(i, single);
                binding.llQuestions.addView(view);
            }
            viewModel.fetchAnswers(questionIds);
        });
        observer(viewModel.getAnswer(), map -> {
            for (int i = 0; i < binding.llQuestions.getChildCount(); i++) {
                View child = binding.llQuestions.getChildAt(i);
                if (child instanceof QAItemView) {
                    QAItemView view = ((QAItemView) child);
                    view.setAnswer(map.get(view.getQuestionId()));
                }
            }
        });
    }

    @Override
    public void onAnswerChange(QAItemView view, boolean answered) {
        binding.btnNext.setEnabled(answered && isAnswered());
    }

    private boolean isAnswered() {
        for (int i = 0; i < binding.llQuestions.getChildCount(); i++) {
            View v = binding.llQuestions.getChildAt(i);
            if (v instanceof QAItemView) {
                QAItemView qaItemView = (QAItemView) v;
                if (!qaItemView.isAnswered()) {
                    return false;
                }
            }
        }
        return true;
    }

    private List<AnswerBody> getAnswersBody() {
        List<AnswerBody> list = new ArrayList<>();
        for (int i = 0; i < binding.llQuestions.getChildCount(); i++) {
            View v = binding.llQuestions.getChildAt(i);
            if (v instanceof QAItemView) {
                QAItemView qaItemView = (QAItemView) v;
                if (qaItemView.isAnswered()) {
                    list.add(qaItemView.getAnswer());
                } else {
                    return null;
                }
            }
        }
        return list;
    }

    private TextView genTitle(String title) {
        TextView textView = new TextView(requireContext(), null, 0, R.style.TextView_Bold);
        textView.setText(title);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32f);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = DisplayUtil.dip2px(requireContext(), 16f);
        textView.setLayoutParams(params);

        return textView;
    }

    @Override
    protected void initViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(getVMClass());
    }

    @Override
    public Class<? extends QAViewModel> getVMClass() {
        return QAViewModel.class;
    }
}
