package com.rate.quiz.ui2.fragment;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.rate.quiz.R;
import com.rate.quiz.base.BaseMVVMFragment;
import com.rate.quiz.databinding.FragmentPointsDetailsBinding;
import com.rate.quiz.entity.RedeemPointItem;
import com.rate.quiz.ui2.adapter.LineItemDecoration;
import com.rate.quiz.ui2.adapter.PointsDetailAdapter;
import com.rate.quiz.vm.PointsDetailsViewModel;

import java.text.NumberFormat;
import java.util.List;

public class PointsDetailsFragment extends BaseMVVMFragment<PointsDetailsViewModel, FragmentPointsDetailsBinding> {

    private int page = 0;
    private PointsDetailAdapter adapter;

    public PointsDetailsFragment() {
        super(R.layout.fragment_points_details);
    }

    @NonNull
    @Override
    protected FragmentPointsDetailsBinding bindView(View view) {
        return FragmentPointsDetailsBinding.bind(view);
    }

    @Override
    protected void initView() {
        initRecyclerView();
    }

    @Override
    protected void observer() {
        super.observer();
        observer(viewModel.getPointsList(),pointsList->{
            binding.tvTotal.setText(getString(R.string.points_total_points, NumberFormat.getNumberInstance().format(pointsList.totalPoints)));
            List<RedeemPointItem> list = pointsList.list;
            if(list != null){
                adapter.addData(list);
            }
            boolean loadEnd = adapter.getItemCount() >= pointsList.totalNum;
            if(loadEnd){
                adapter.getLoadMoreModule().loadMoreEnd();
            }else{
                adapter.getLoadMoreModule().loadMoreComplete();
            }
        });
    }

    private void initRecyclerView(){
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerview.addItemDecoration(new LineItemDecoration(requireContext(),false));
        adapter = new PointsDetailAdapter();
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
    public Class<? extends PointsDetailsViewModel> getVMClass() {
        return PointsDetailsViewModel.class;
    }
}
