package com.rate.quiz.ui2.fragment;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duxl.baselib.utils.DisplayUtil;
import com.rate.quiz.R;
import com.rate.quiz.base.BaseMVVMFragment;
import com.rate.quiz.database.DBHolder;
import com.rate.quiz.databinding.FragmentCurrencyBinding;
import com.rate.quiz.entity.TokenWrapper;
import com.rate.quiz.ui2.adapter.SpaceDecoration;
import com.rate.quiz.ui2.adapter.TokenAdapter;
import com.rate.quiz.utils.StatusBarUtils;
import com.rate.quiz.vm.CurrencyViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CurrencyFragment extends BaseMVVMFragment<CurrencyViewModel, FragmentCurrencyBinding> {
    public static final String TAG = "CurrencyFragment";

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
            try {
                viewModel.fetchPrices();
                viewModel.loadAllCurrencyInfo();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        StatusBarUtils.setViewHeightEqualsStatusBarHeightLinear(binding.paddingView);

        binding.btnAddCurrency.setOnClickListener(v -> navigateTo(CurrencyFragmentDirections.actionFragmentCurrencyToActivitySelectTokens()));
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
                            tokenAdapter.notifyItemChanged(tokenAdapter.getItemPosition(item), TokenAdapter.PAYLOAD_UPDATE_PRICE);
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
        binding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
        startInterval();
    }

    private final ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            if (binding == null) return;
            Rect rect = new Rect();
            binding.getRoot().getWindowVisibleDisplayFrame(rect);
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
        int verticalSpace = DisplayUtil.dip2px(requireContext(), 8);
        recyclerView.addItemDecoration(new SpaceDecoration(0, verticalSpace, 0, verticalSpace));
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
        binding.getRoot().getViewTreeObserver().removeOnGlobalLayoutListener(globalLayoutListener);
        super.onDestroyView();
        tokenAdapter.destroyView();
        stopInterval();
    }

    @Override
    public Class<? extends CurrencyViewModel> getVMClass() {
        return CurrencyViewModel.class;
    }
}
