package com.rate.quiz.ui2.fragment;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.rate.quiz.R;
import com.rate.quiz.base.BaseMVVMFragment;
import com.rate.quiz.databinding.FragmentRedeemHistoryBinding;
import com.rate.quiz.entity.RedeemHistoryItem;
import com.rate.quiz.ui2.adapter.LineItemDecoration;
import com.rate.quiz.ui2.adapter.RedeemHistoryAdapter;
import com.rate.quiz.vm.RedeemHistoryViewModel;

import java.util.List;

public class RedeemHistoryFragment extends BaseMVVMFragment<RedeemHistoryViewModel, FragmentRedeemHistoryBinding> {

    private int page = 0;
    private RedeemHistoryAdapter adapter;

    public RedeemHistoryFragment() {
        super(R.layout.fragment_redeem_history);
    }

    @Override
    protected void onFirstResume() {
        super.onFirstResume();
    }

    @NonNull
    @Override
    protected FragmentRedeemHistoryBinding bindView(View view) {
        return FragmentRedeemHistoryBinding.bind(view);
    }

    @Override
    protected void initView() {
        initRecyclerView();
    }

    @Override
    protected void observer() {
        super.observer();
        observer(viewModel.getHistories(),pointsList->{
            List<RedeemHistoryItem> list = pointsList.list;
            if(list != null){
                adapter.addData(list);
            }
            boolean loadEnd = adapter.getItemCount() >= pointsList.totalNum;
            if(loadEnd){
                adapter.getLoadMoreModule().loadMoreEnd();
            }else{
                adapter.getLoadMoreModule().loadMoreComplete();
            }
            if(adapter.getItemCount() == 0 && adapter.getEmptyLayout() == null){
                adapter.setEmptyView(R.layout.view_empty1);
            }
        });
    }

    private void initRecyclerView(){
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerview.addItemDecoration(new LineItemDecoration(requireContext(),false));
        adapter = new RedeemHistoryAdapter();
        adapter.addLoadMoreModule(adapter);
        adapter.getLoadMoreModule().setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                viewModel.fetch(page++);
            }
        });
        binding.recyclerview.setAdapter(adapter);
    }

    @Override
    public Class<? extends RedeemHistoryViewModel> getVMClass() {
        return RedeemHistoryViewModel.class;
    }
}
