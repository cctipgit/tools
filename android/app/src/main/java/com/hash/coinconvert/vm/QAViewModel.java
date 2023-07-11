package com.hash.coinconvert.vm;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hash.coinconvert.entity.AnswerBody;
import com.hash.coinconvert.entity.AnswersBody;
import com.hash.coinconvert.entity.QuestionsItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QAViewModel extends ToolApiViewModel {

    private MutableLiveData<List<QuestionsItem>> questions = new MutableLiveData<>();
    private MutableLiveData<Progress> progress = new MutableLiveData<>();
    private MutableLiveData<Map<String, List<Integer>>> answer = new MutableLiveData<>();

    public LiveData<List<QuestionsItem>> getQuestions() {
        return questions;
    }

    public LiveData<Map<String, List<Integer>>> getAnswer() {
        return answer;
    }

    public LiveData<Progress> getProgress() {
        return progress;
    }

    private Map<String, List<Integer>> answers = new HashMap<>();

    public void fetchQuestions() {
        startLoading();
        execute(api.questionList(), questionList -> {
            questions.postValue(questionList.lists);
        });
    }

    public void submitAnswers() {
        startLoading();
        List<AnswerBody> list = new ArrayList<>();
        answers.forEach((key, value) -> list.add(new AnswerBody(key, value)));
        execute(api.submitAnswer(new AnswersBody(list)), ignore -> {
            if (questions.getValue() != null) {
                updateProgress(questions.getValue().size() + 1);
            }
        });
    }

    public void updateProgress(int progressValue) {
        if (questions.getValue() != null) {
            progress.postValue(new Progress(progressValue, questions.getValue().size()));
        }
    }

    public void complete() {
        int size = questions.getValue().size();
        progress.postValue(new Progress(size, size));
    }

    public void answer(List<AnswerBody> list, boolean last) {
        if (list == null) return;
        list.forEach(item -> answers.put(item.id, item.options));
        if (last) {
            submitAnswers();
        }
    }

    public void fetchAnswers(List<String> ids) {
        Map<String, List<Integer>> map = new HashMap<>(ids.size());
        ids.forEach(item -> {
            List<Integer> list = answers.get(item);
            if (list != null) {
                map.put(item, list);
            }
        });
        answer.postValue(map);
    }

    public static class Progress {
        public final int progress;
        public final int max;

        public Progress(int progress, int max) {
            this.progress = progress;
            this.max = max;
        }
    }
}
