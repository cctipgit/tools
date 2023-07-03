package com.hash.coinconvert.ui2.fragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duxl.baselib.utils.DisplayUtil;
import com.hash.coinconvert.R;
import com.hash.coinconvert.base.BaseMVVMFragment;
import com.hash.coinconvert.databinding.FragmentTaskListBinding;
import com.hash.coinconvert.entity.TaskItem;
import com.hash.coinconvert.ui2.adapter.HomeTaskListAdapter;
import com.hash.coinconvert.utils.StatusBarUtils;
import com.hash.coinconvert.vm.TaskViewModel;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

public class TaskListFragment extends BaseMVVMFragment<TaskViewModel, FragmentTaskListBinding> {
    private HomeTaskListAdapter taskListAdapter;

    private Disposable interval;
    private long expireInMills;

    private Drawable clock;

    public TaskListFragment() {
        super(R.layout.fragment_task_list);
    }

    @NonNull
    @Override
    protected FragmentTaskListBinding bindView(View view) {
        return FragmentTaskListBinding.bind(view);
    }

    @Override
    protected void initView() {
        StatusBarUtils.setViewHeightEqualsStatusBarHeight(binding.paddingView);

        setupRecyclerView();

        binding.tvGame.setOnClickListener(v -> {
            getNavController().navigate(TaskListFragmentDirections.actionFragmentTaskListToFragmentLottery());
        });
        binding.imgGame.setOnClickListener(v -> {
            getNavController().navigate(TaskListFragmentDirections.actionFragmentTaskListToFragmentLottery());
        });
    }

    @Override
    protected void onFirstResume() {
        super.onFirstResume();
        viewModel.fetchTasks();
    }

    @Override
    protected void observer() {
        super.observer();
        observer(viewModel.getTaskList(), taskList -> {
            if (taskList.list != null) {
                int total = taskList.list.size();
                int done = (int) taskList.list.stream().filter(i -> i.done).count();
                binding.tvProgress.setText(getString(R.string.home_task_list_preview, done, total));
                taskListAdapter.setNewInstance(taskList.list);
                expireInMills = taskList.expireTime;
                startInterval();
            }
            if (taskList.pinNum > 0) {
                binding.tvGame.setVisibility(View.VISIBLE);
                binding.tvGame.setText(String.valueOf(taskList.pinNum));
            } else {
                binding.tvGame.setVisibility(View.INVISIBLE);
            }
        });
        observer(viewModel.getCheckResult(), id -> {
            for (int i = 0; i < taskListAdapter.getData().size(); i++) {
                TaskItem item = taskListAdapter.getItem(i);
                if (item.id.equals(id)) {
                    item.done = true;
                    taskListAdapter.notifyItemChanged(i, HomeTaskListAdapter.PAYLOAD_STATE);
                    break;
                }
            }
        });
    }

    private void startInterval() {
        stopInterval();
        interval = Observable.interval(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    long d = (expireInMills - System.currentTimeMillis()) / 1000;
                    if (d > 0) {
                        long h = d / 3600;
                        long m = (d % 3600) / 60;
                        long s = (d % 3600) % 60;
                        binding.tvExpire.setText(getString(R.string.home_task_list_time, h, m, s));
                        if (clock == null) {
                            clock = ContextCompat.getDrawable(requireContext(), R.drawable.ic_task_list_clock);
                            int size = DisplayUtil.dip2px(getContext(), 24f);
                            clock.setBounds(0, 0, size, size);
                            binding.tvExpire.setCompoundDrawables(clock, null, null, null);
                        }
                    } else {
                        binding.tvExpire.setText(R.string.home_task_list_expired);
                        binding.tvExpire.setCompoundDrawables(null, null, null, null);
                    }
                });
    }

    private void stopInterval() {
        if (interval != null && !interval.isDisposed()) {
            interval.dispose();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopInterval();
    }

    @Override
    public Class<? extends TaskViewModel> getVMClass() {
        return TaskViewModel.class;
    }

    private void setupRecyclerView() {
        taskListAdapter = new HomeTaskListAdapter();
        taskListAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.btn_check) {
                TaskItem item = taskListAdapter.getItem(position);
                viewModel.checkTask(item.id, item.params);
            }
        });
        RecyclerView recyclerView = binding.rvTaskList;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(taskListAdapter);
        recyclerView.addItemDecoration(new MyItemDecoration(requireContext()));
    }

    public static class MyItemDecoration extends RecyclerView.ItemDecoration {

        private final int gap;
        private final Paint paint;

        MyItemDecoration(Context context) {
            this.gap = DisplayUtil.dip2px(context, 8f);
            paint = new Paint();
            paint.setStrokeWidth(gap / 8f);
            paint.setColor(ContextCompat.getColor(context, R.color.list_divider_line));
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            outRect.bottom = gap;
        }

        @Override
        public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.onDraw(c, parent, state);
            int childCount = parent.getChildCount();
            int paddingStart = parent.getPaddingStart();
            int paddingEnd = parent.getPaddingEnd();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + gap;
                int y = (top + bottom) / 2;
                c.drawLine(paddingStart, y, parent.getWidth() - paddingEnd, y, paint);
            }
        }
    }
}
