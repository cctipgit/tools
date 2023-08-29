package com.rate.quiz.ui2.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.rate.quiz.R;
import com.rate.quiz.entity.TaskItem;
import com.rate.quiz.utils.StringUtils;
import com.rate.quiz.widget.RoundButton;

import java.util.List;

public class HomeTaskListAdapter extends BaseQuickAdapter<TaskItem, BaseViewHolder> {

    public static final int PAYLOAD_STATE = 1718;

    public HomeTaskListAdapter() {
        super(R.layout.adapter_task_list);
        addChildClickViewIds(R.id.btn_check);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, TaskItem item) {
        holder.setText(R.id.tv_chance,
                StringUtils.format(getContext(), R.string.home_task_item_chance, item.spinTimes));
        holder.setText(R.id.tv_task_name, item.name);
        bindButtonCheck(holder, item);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, TaskItem item, @NonNull List<?> payloads) {
        if (payloads.size() > 0) {
            switch (((int) payloads.get(0))) {
                case PAYLOAD_STATE:
                    bindButtonCheck(holder, item);
                    break;
            }
        } else {
            super.convert(holder, item, payloads);
        }
    }

    private void bindButtonCheck(@NonNull BaseViewHolder holder, TaskItem item) {
        RoundButton btnCheck = holder.getView(R.id.btn_check);
        btnCheck.setEnabled(!item.done);
        btnCheck.setText(item.done ? R.string.home_task_item_completed : R.string.home_task_item_go);
    }
}
