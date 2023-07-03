package com.hash.coinconvert.ui2.fragment;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hash.coinconvert.R;
import com.hash.coinconvert.base.BaseMVVMFragment;
import com.hash.coinconvert.database.DBHolder;
import com.hash.coinconvert.databinding.FragmentCurrencyBinding;
import com.hash.coinconvert.entity.TokenWrapper;
import com.hash.coinconvert.ui2.adapter.TokenAdapter;
import com.hash.coinconvert.vm.CurrencyViewModel;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CurrencyFragment extends BaseMVVMFragment<CurrencyViewModel, FragmentCurrencyBinding> {
    public CurrencyFragment() {
        super(R.layout.fragment_currency);
    }

    private TokenAdapter tokenAdapter;

    private boolean keyboardVisible = false;

    private static final long INTERVAL_TIME = 1000L;

    private Disposable interval;

    private void startInterval() {
        interval = Observable.interval(INTERVAL_TIME, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io()).subscribe(aLong -> {
            viewModel.fetchPrices();
            viewModel.loadAllCurrencyInfo();
        });
    }

    private void stopInterval() {
        if (!interval.isDisposed()) {
            interval.dispose();
        }
    }

    @NonNull
    @Override
    protected FragmentCurrencyBinding bindView(View view) {
        return FragmentCurrencyBinding.bind(view);
    }

    @Override
    protected void initView() {
        binding.actionBar.addMenu(R.drawable.ic_home_currency_add, (v) -> {
            getNavController().navigate(CurrencyFragmentDirections.actionFragmentCurrencyToActivitySelectTokens());
        });
        initRecyclerView(binding.recyclerview);
        DBHolder.getInstance().prepareTokenAsync(requireContext()).subscribeOn(Schedulers.io()).subscribe(ignore -> viewModel.loadAllCurrencyInfo());

        viewModel.getCurrencies().observe(getViewLifecycleOwner(), tokenWrappers -> {
            if (tokenAdapter.getItemCount() > 0) {
                //update tokens
                tokenWrappers.forEach(item -> {
                    Log.d("HomeActivity", item.getSymbol() + ":" + item.getPrice().toString());
                    boolean exist = false;
                    for (int i = 0; i < tokenAdapter.getData().size(); i++) {
                        TokenWrapper token = tokenAdapter.getItem(i);
                        if (token.getSymbol().equals(item.getSymbol())) {
                            token.setToken(item.getToken());
                            tokenAdapter.notifyItemChanged(i, TokenAdapter.PAYLOAD_UPDATE_PRICE);
                            exist = true;
                        }
                    }
                    if (!exist) {
                        tokenAdapter.addData(item);
                        tokenAdapter.notifyItemInserted(tokenAdapter.getItemCount());
                    }
                });
            } else {
                tokenAdapter.setList(tokenWrappers);
            }
        });

        View rootView = binding.getRoot().getRootView();
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                rootView.getWindowVisibleDisplayFrame(rect);
                int height = rect.bottom - rect.top;
                int screenHeight = requireContext().getResources().getDisplayMetrics().heightPixels;
                boolean visible = height < screenHeight * 0.75f;
                if (visible != keyboardVisible) {
                    tokenAdapter.onKeyBoardVisibilityChange(visible);
                    keyboardVisible = visible;
                }
            }
        });

        startInterval();
    }

    private void initRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        tokenAdapter = new TokenAdapter();
        tokenAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.cl_detail) {
                //go detail
                NavDirections directions = CurrencyFragmentDirections.actionFragmentCurrencyToFragmentTokenDetail(tokenAdapter.getItem(position).getSymbol());
                getNavController().navigate(directions);
            }
        });
        recyclerView.setAdapter(tokenAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        tokenAdapter.destroyView();
        binding = null;
        stopInterval();
    }

    @Override
    public Class<? extends CurrencyViewModel> getVMClass() {
        return CurrencyViewModel.class;
    }
}
