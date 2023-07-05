package com.hash.coinconvert.ui2.fragment;

import android.graphics.Rect;
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
import com.hash.coinconvert.ui2.adapter.LineItemDecoration;
import com.hash.coinconvert.ui2.adapter.TokenAdapter;
import com.hash.coinconvert.vm.CurrencyViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    private final Map<String, TokenWrapper> dbFavTokenMap = new HashMap<>();
    private final Map<String, TokenWrapper> adapterTokenMap = new HashMap<>();

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
        binding.actionBar.addMenu(R.drawable.ic_home_currency_add, (v) -> navigateTo(CurrencyFragmentDirections.actionFragmentCurrencyToActivitySelectTokens()));
        initRecyclerView(binding.recyclerview);
        DBHolder.getInstance().prepareTokenAsync(requireContext()).subscribeOn(Schedulers.io()).subscribe(ignore -> viewModel.loadAllCurrencyInfo());

        viewModel.getCurrencies().observe(getViewLifecycleOwner(), tokenWrappers -> {
            if (tokenAdapter.getItemCount() > 0) {
                tokenWrappers.forEach(item -> dbFavTokenMap.put(item.getSymbol(), item));
                tokenAdapter.getData().forEach(item -> adapterTokenMap.put(item.getSymbol(), item));
                dbFavTokenMap.forEach((key, value) -> {
                    if (adapterTokenMap.containsKey(key)) {
                        TokenWrapper item = adapterTokenMap.get(key);
                        if (item != null) {
                            item.setToken(value.getToken());
                        }
                    } else {
                        tokenAdapter.addData(value);
                    }
                });
                adapterTokenMap.forEach((key, value) -> {
                    if (!dbFavTokenMap.containsKey(key)) {
                        //remove
                        List<TokenWrapper> list = tokenAdapter.getData();
                        for (int i = 0; i < list.size(); i++) {
                            TokenWrapper item = list.get(i);
                            if (Objects.equals(item.getSymbol(), key)) {
                                tokenAdapter.remove(item);
                                break;
                            }
                        }
                    }
                });
                dbFavTokenMap.clear();
                adapterTokenMap.clear();
            } else {
                tokenAdapter.setList(tokenWrappers);
            }
        });

        //listen the keyboard visibility event
        View rootView = binding.getRoot().getRootView();
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
        startInterval();
    }

    private final ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            if (binding == null) return;
            Rect rect = new Rect();
            binding.getRoot().getRootView().getWindowVisibleDisplayFrame(rect);
            int height = rect.bottom - rect.top;
            int screenHeight = requireContext().getResources().getDisplayMetrics().heightPixels;
            boolean visible = height < screenHeight * 0.75f;
            if (visible != keyboardVisible) {
                tokenAdapter.onKeyBoardVisibilityChange(visible);
                keyboardVisible = visible;
            }
        }
    };

    private void initRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.addItemDecoration(new LineItemDecoration(requireContext(), true));
        tokenAdapter = new TokenAdapter();
        tokenAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.cl_detail) {
                //go detail
                NavDirections directions = CurrencyFragmentDirections.actionFragmentCurrencyToFragmentTokenDetail(tokenAdapter.getItem(position).getSymbol());
                navigateTo(directions);
            }
        });
        recyclerView.setAdapter(tokenAdapter);
    }

    @Override
    public void onDestroyView() {
        binding.getRoot().getRootView().getViewTreeObserver().removeOnGlobalLayoutListener(globalLayoutListener);
        super.onDestroyView();
        tokenAdapter.destroyView();
        stopInterval();
    }

    @Override
    public Class<? extends CurrencyViewModel> getVMClass() {
        return CurrencyViewModel.class;
    }
}
