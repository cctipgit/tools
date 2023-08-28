package com.hash.coinconvert.vm;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hash.coinconvert.entity.AnswerBody;
import com.hash.coinconvert.entity.AnswersBody;
import com.hash.coinconvert.entity.PinList;
import com.hash.coinconvert.entity.QuestionList;
import com.hash.coinconvert.entity.QuestionsItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QAViewModel extends ToolApiViewModel {

    private MutableLiveData<QuestionList> questions = new MutableLiveData<>();
    private MutableLiveData<Progress> progress = new MutableLiveData<>();
    private MutableLiveData<Map<String, List<Integer>>> answer = new MutableLiveData<>();

    public LiveData<QuestionList> getQuestions() {
        return questions;
    }

    public LiveData<Map<String, List<Integer>>> getAnswer() {
        return answer;
    }

    private MutableLiveData<PinList> pinList = new MutableLiveData<>();

    public LiveData<PinList> getPinList(){
        return pinList;
    }

    public LiveData<Progress> getProgress() {
        return progress;
    }

    private Map<String, List<Integer>> answers = new HashMap<>();

    public void fetchQuestions() {
        if (questions.getValue() != null) return;
        startLoading();
        execute(api.questionList(), questionList -> questions.postValue(questionList));
    }

    public void refetch() {
        execute(api.questionList(), questionList -> questions.postValue(questionList));
    }

    public void submitAnswers() {
        startLoading();
        List<AnswerBody> list = new ArrayList<>();
        answers.forEach((key, value) -> list.add(new AnswerBody(key, value)));
        execute(api.submitAnswer(new AnswersBody(list)), ignore -> {
            if (questions.getValue() != null) {
                updateProgress(getQuestionSize() + 1);
            }
        });
    }

    public void fetchPinList(){
        startLoadingIfNeeded(pinList);
        execute(api.pinList(),res -> {
            pinList.postValue(res);
        });
    }

    public void updateProgress(int progressValue) {
        if (questions.getValue() != null) {
            progress.postValue(new Progress(progressValue, getQuestionSize()));
        }
    }

    public void complete() {
        int size = getQuestionSize();
        progress.postValue(new Progress(size, size));
        refetch();
    }

    private int getQuestionSize() {
        if (questions.getValue() != null && questions.getValue().lists != null) {
            return questions.getValue().lists.size();
        }
        return 0;
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
