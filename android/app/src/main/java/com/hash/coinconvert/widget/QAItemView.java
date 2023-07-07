package com.hash.coinconvert.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.duxl.baselib.utils.DisplayUtil;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.JustifyContent;
import com.hash.coinconvert.R;
import com.hash.coinconvert.entity.AnswerBody;
import com.hash.coinconvert.entity.QuestionItem;

import java.util.ArrayList;
import java.util.List;

public class QAItemView extends LinearLayout implements View.OnClickListener {

    public static final int MULTI = 2;

    private String questionId;
    private OnAnswerChangeListener onAnswerChangeListener;
    private boolean multiChoice;
    private List<Integer> answers;

    public QAItemView(Context context) {
        super(context);
        init(context);
    }

    public QAItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public QAItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
    }

    /**
     * @param single current page only has one question
     */
    public void setQuestion(QuestionItem item, boolean single) {
        questionId = item.id;
        multiChoice = item.qType == MULTI;
        answers = new ArrayList<>();
        addView(genTitle(item.title));
        addView(genAnswers(item.options, single));
    }

    public void setAnswer(@Nullable List<Integer> checkIndexList) {
        if (checkIndexList != null) {
            checkIndexList.forEach(answerIndex -> {
                ((CheckedTextView) ((FlexboxLayout) getChildAt(getChildCount() - 1)).getChildAt(answerIndex)).setChecked(true);
            });
            this.answers.clear();
            this.answers.addAll(checkIndexList);
            if (this.onAnswerChangeListener != null) {
                this.onAnswerChangeListener.onAnswerChange(this, isAnswered());
            }
        }
    }

    public void setOnAnswerChangeListener(OnAnswerChangeListener onAnswerChangeListener) {
        this.onAnswerChangeListener = onAnswerChangeListener;
    }

    private TextView genTitle(String title) {
        Context context = getContext();
        TextView textView = new TextView(context, null, 0, R.style.TextView_Medium);
        textView.setText(title);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = DisplayUtil.dip2px(context, 24f);
        textView.setLayoutParams(params);
        return textView;
    }

    private FlexboxLayout genAnswers(List<String> answers, boolean single) {
        Context context = getContext();
        FlexboxLayout layout = new FlexboxLayout(context);
        layout.setFlexDirection(FlexDirection.ROW);
        layout.setFlexWrap(FlexWrap.WRAP);
        layout.setJustifyContent(JustifyContent.FLEX_START);
        layout.setDividerDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider_flex_box));
        layout.setShowDivider(FlexboxLayout.SHOW_DIVIDER_MIDDLE);

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = DisplayUtil.dip2px(context, 16f);
        layout.setLayoutParams(params);

        for (int i = 0; i < answers.size(); i++) {
            layout.addView(genAnswer(answers.get(i), single, i));
        }
        return layout;
    }

    private CheckedTextView genAnswer(String answer, boolean matchParent, int index) {
        CheckedTextView textView = new CheckedTextView(getContext(), null, 0, R.style.QAAnswer);
        textView.setText(answer);
        textView.setChecked(false);
        textView.setGravity(Gravity.CENTER);
        textView.setOnClickListener(this);

        FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                matchParent ? ViewGroup.LayoutParams.MATCH_PARENT : ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        if (matchParent && index > 0) {
            params.topMargin = DisplayUtil.dip2px(getContext(), 8f);
        }
        textView.setLayoutParams(params);

        return textView;
    }

    @Override
    public void onClick(View v) {
        if (v instanceof CheckedTextView) {
            CheckedTextView cv = (CheckedTextView) v;
            cv.setChecked(!cv.isChecked());
            FlexboxLayout layout = (FlexboxLayout) getChildAt(getChildCount() - 1);
            answers.clear();
            for (int i = 0; i < layout.getChildCount(); i++) {
                CheckedTextView item = (CheckedTextView) layout.getChildAt(i);
                if (item.isChecked()) {
                    answers.add(i);
                }
                if (!multiChoice && item != v) {
                    item.setChecked(false);
                }
            }
            if (onAnswerChangeListener != null) {
                onAnswerChangeListener.onAnswerChange(this, isAnswered());
            }
        }
    }

    public boolean isAnswered() {
        return answers.size() > 0;
    }

    public AnswerBody getAnswer() {
        List<Integer> list = new ArrayList<>(answers);
        return new AnswerBody(questionId, list);
    }

    public String getQuestionId() {
        return questionId;
    }

    public interface OnAnswerChangeListener {
        void onAnswerChange(QAItemView view, boolean isAnswered);
    }
}
