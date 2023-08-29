package com.rate.quiz.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.duxl.baselib.utils.DisplayUtil;
import com.duxl.baselib.utils.EmptyUtils;
import com.duxl.baselib.utils.SPUtils;
import com.duxl.baselib.widget.SmartRecyclerView;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.rate.quiz.Constants;
import com.rate.quiz.R;
import com.rate.quiz.database.CurrencyDaoWrap;
import com.rate.quiz.database.entity.Token;
import com.rate.quiz.database.repository.TokenRepository;
import com.rate.quiz.entity.CurrencyInfo;
import com.rate.quiz.ui.adapter.SwitchCurrencyAdapter;
import com.rate.quiz.ui2.dialog.SelectTypeDialog;
import com.rate.quiz.utils.FontUtil;
import com.rate.quiz.widget.ActionBar;
import com.rate.quiz.widget.AppStatusView;
import com.rate.quiz.widget.LetterPopupWindow;
import com.rate.quiz.widget.SearchView;
import com.rate.quiz.widget.SideBar;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 货币搜索
 * create by duxl 2023/1/8
 */
public abstract class SearchCurrencyActivity extends BaseRecyclerViewActivity implements SearchView.OnActionChangedListener, SideBar.OnTouchingLetterChangedListener, SelectTypeDialog.OnSelectTypeChangeListener {

    public static final String TAG = "SearchCurrencyActivity";
    private SwitchCurrencyAdapter mAdapter;
    private List<SwitchCurrencyAdapter.ItemEntity> mAllData = new ArrayList<>();
    private List<SwitchCurrencyAdapter.ItemEntity> mShowData = new ArrayList<>();
    private CurrencyDaoWrap mCurrencyDao;
    private boolean mIsInputKeywords;
    private LetterPopupWindow mLetterPopupWindow;
    private int mSideBarMarginTop;

    public static final int SELECT_TYPE_CURRENCY = R.id.rb_select_type_currency;
    public static final int SELECT_TYPE_TOKENS = R.id.rb_select_type_tokens;

    @IntDef({SELECT_TYPE_CURRENCY, SELECT_TYPE_TOKENS})
    public @interface SelectType {

    }

    @SelectType
    protected int selectType = SELECT_TYPE_CURRENCY;

    private ActionBar actionBar;

    @Override
    protected void initParams(Intent args) {
        super.initParams(args);
        mCurrencyDao = new CurrencyDaoWrap(getLifecycle());
    }

    @Override
    protected void initView(View v) {
        super.initView(v);
        getSearchView().setOnActionChangedListener(this);
        getSideBar().setOnTouchingLetterChangedListener(this);
        mSmartRecyclerView.setEnableLoadMore(false);
        mSmartRecyclerView.setEnableRefresh(false);
        StickyHeader stickyHeader = new StickyHeader(mSmartRecyclerView.getContentView());
        mSmartRecyclerView.getContentView().addItemDecoration(stickyHeader);
        mSideBarMarginTop = ((ViewGroup.MarginLayoutParams) getSideBar().getLayoutParams()).topMargin;
        actionBarAddArrow();
    }

    private void actionBarAddArrow() {
        actionBar = findViewById(R.id.action_bar);
        if (actionBar != null) {
            actionBar.setFitsSystemWindows(false);
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_arrow_down);
            drawable.setTint(ContextCompat.getColor(this, R.color.theme_text_color));
            int size = DisplayUtil.dip2px(this, 24f);
            drawable.setBounds(0, 0, size, size);
            actionBar.getTitleView().setCompoundDrawables(null, null, drawable, null);
            actionBar.getTitleView().setCompoundDrawablePadding(DisplayUtil.dip2px(this, 8f));
            actionBar.getTitleView().setPadding(0, 0, 0, 0);
            actionBar.getTitleView().setOnClickListener(v -> {
                //show dialog
                SelectTypeDialog.newInstance(selectType).show(getSupportFragmentManager(), "select_type");
            });
            actionBar.setPadding(0, 0, 0, 0);
        }
    }

    @Override
    public void onSelectTypeChange(int type) {
        if (type == this.selectType) return;
        if (type == SELECT_TYPE_CURRENCY) {
            actionBar.getTitleView().setText(R.string.select_type_currency);
        } else {
            actionBar.getTitleView().setText(R.string.select_type_tokens);
        }
        this.selectType = type;
        loadData(0);
    }

    @Override
    public void onBackPressed() {
        if (getSearchView().isEditing()) {
            getSearchView().reset();
            return;
        }
        super.onBackPressed();
    }

    protected abstract SearchView getSearchView();

    protected abstract SideBar getSideBar();

    protected abstract List<String> getCheckedTokens();

    // 保存频繁使用到的token
    protected void saveFrequentlyToken(CurrencyInfo currency) {
        String tokenJson = SPUtils.getInstance().getString(Constants.SP.KEY.FREQUENTLY_TOKEN_LIST);
        List<String> frequentlyToken = null;
        if (EmptyUtils.isNotEmpty(tokenJson)) {
            frequentlyToken = new Gson().fromJson(tokenJson, new TypeToken<List<String>>() {
            }.getType());
        }
        if (frequentlyToken == null) {
            frequentlyToken = new ArrayList<>();
        }
        if (!frequentlyToken.contains(currency.token)) {
            frequentlyToken.add(currency.token);
        }

        // 保存平凡使用的token个数，最多12个
        if (frequentlyToken.size() > Constants.MAX_FREQUENTLY_TOKEN) {
            frequentlyToken.remove(0);
        }

        SPUtils.getInstance().put(Constants.SP.KEY.FREQUENTLY_TOKEN_LIST, new Gson().toJson(frequentlyToken));
    }

    @Override
    protected BaseQuickAdapter getAdapter() {
        if (mAdapter == null) {
            mAdapter = new SwitchCurrencyAdapter();
        }
        return mAdapter;
    }

    @Override
    protected AppStatusView resetRecyclerStatusView(SmartRecyclerView recyclerView) {
        AppStatusView statusView = super.resetRecyclerStatusView(recyclerView);
        statusView.setEmptyImgRes(R.mipmap.empty_search);
        statusView.setEmptyText(getString(R.string.search_currency_empty_msg));
        FontUtil.setType(statusView.getStateTextView(), FontUtil.FOUNT_TYPE.POPPINS_MEDIUM);
        return statusView;
    }

    private Observable<List<SwitchCurrencyAdapter.ItemEntity>> loadDataAsync() {
        Log.d(TAG, "loadDataAsync");
        return Observable.create(sub -> {
            List<SwitchCurrencyAdapter.ItemEntity> list = new ArrayList<>();
            String currencyType = selectType == SELECT_TYPE_CURRENCY ? Token.TOKEN_TYPE_CURRENCY : Token.TOKEN_TYPE_DIGITAL;
            List<Token> tokens = TokenRepository.queryByCurrencyType(currencyType);
            Log.d(TAG, "loadDataAsync:" + tokens.size());
            String location = SPUtils.getInstance().getString(Constants.SP.KEY.LOCATION_COUNTRY_CODE);
            if (EmptyUtils.isNotEmpty(location)) {
                for (Token item : tokens) {
                    if (TextUtils.equals(item.countryCode, location)) {
                        String groupName = getString(R.string.local_currency);
                        // 添加定位分组
                        SwitchCurrencyAdapter.ItemEntity titleItem = new SwitchCurrencyAdapter.ItemEntity(groupName);
                        titleItem.manualAdd = true;
                        list.add(titleItem);
                        SwitchCurrencyAdapter.ItemEntity contentItem = new SwitchCurrencyAdapter.ItemEntity(item);
                        contentItem.manualAdd = true;
                        list.add(contentItem);
                        break;
                    }
                }
            }

            String fChat = null;
            for (int i = 0; i < tokens.size(); i++) {
                Token token = tokens.get(i);
                if (!TextUtils.equals(fChat, token.fchat)) {
                    fChat = token.fchat;
                    SwitchCurrencyAdapter.ItemEntity titleItem = new SwitchCurrencyAdapter.ItemEntity(fChat);
                    list.add(titleItem);
                }
                SwitchCurrencyAdapter.ItemEntity contentItem = new SwitchCurrencyAdapter.ItemEntity(token);
                list.add(contentItem);
            }
            sub.onNext(list);
            sub.onComplete();
        });
    }

    @Override
    @SuppressLint({"NewApi", "CheckResult"})
    protected void loadData(int pageNum) {
        loadDataAsync().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    mAllData.clear();
                    mAllData.addAll(list);
                    mShowData.clear();
                    mShowData.addAll(list);
                    SwitchCurrencyAdapter adapter = (SwitchCurrencyAdapter) getAdapter();
                    adapter.setNewInstance(mShowData);
                    adapter.getRecyclerView().scrollToPosition(0);
                    Log.d(TAG, "onLoadData:" + mAllData.size());
                });

//
//        List<CurrencyInfo> allCurrency = mCurrencyDao.getCurrencyAll();
//        if (EmptyUtils.isEmpty(allCurrency)) {
//            return;
//        }
//        // 当前定位货币
//        String location = SPUtils.getInstance().getString(Constants.SP.KEY.LOCATION_COUNTRY_CODE);
//        if (EmptyUtils.isNotEmpty(location)) {
//            for (CurrencyInfo item : allCurrency) {
//                if (TextUtils.equals(item.countryCode, location)) {
//                    String groupName = getString(R.string.local_currency);
//                    // 添加定位分组
//                    SwitchCurrencyAdapter.ItemEntity titleItem = new SwitchCurrencyAdapter.ItemEntity(groupName);
//                    titleItem.manualAdd = true;
//                    mAllData.add(titleItem);
//                    // 添加定位item
//                    boolean isChecked = EmptyUtils.isNotEmpty(getCheckedTokens()) && getCheckedTokens().contains(item.token);
//                    CurrencyInfo localCurrency = item.clone();
//                    localCurrency.fChat = groupName;
//                    SwitchCurrencyAdapter.ItemEntity contentItem = new SwitchCurrencyAdapter.ItemEntity(localCurrency, isChecked);
//                    contentItem.manualAdd = true;
//                    mAllData.add(contentItem);
//                    break;
//                }
//            }
//        }
//
//        // 平凡使用的货币
//        String tokenJson = SPUtils.getInstance().getString(Constants.SP.KEY.FREQUENTLY_TOKEN_LIST);
//        if (EmptyUtils.isNotEmpty(tokenJson)) {
//            List<String> frequentlyToken = new Gson().fromJson(tokenJson, new TypeToken<List<String>>() {
//            }.getType());
//            if (EmptyUtils.isNotEmpty(frequentlyToken)) {
//                final String groupName = getString(R.string.frequently_used);
//                // 添加定位分组
//                SwitchCurrencyAdapter.ItemEntity titleItem = new SwitchCurrencyAdapter.ItemEntity(groupName);
//                titleItem.manualAdd = true;
//                mAllData.add(titleItem);
//
//                // 添加平凡使用的item
//                List<SwitchCurrencyAdapter.ItemEntity> collect = allCurrency.stream().filter(it -> frequentlyToken.contains(it.token)).sorted().map(it -> {
//                    CurrencyInfo item = it.clone();
//                    boolean isChecked = EmptyUtils.isNotEmpty(getCheckedTokens()) && getCheckedTokens().contains(item.token);
//                    item.fChat = groupName;
//                    SwitchCurrencyAdapter.ItemEntity contentItem = new SwitchCurrencyAdapter.ItemEntity(item, isChecked);
//                    contentItem.manualAdd = true;
//                    return contentItem;
//                }).collect(Collectors.toList());
//                mAllData.addAll(collect);
//            }
//        }
//
//        // 其它货币
//        String fChat = null;
//        for (int i = 0; i < allCurrency.size(); i++) {
//            CurrencyInfo currencyInfo = allCurrency.get(i);
//            if (!TextUtils.equals(fChat, currencyInfo.fChat)) {
//                fChat = currencyInfo.fChat;
//                SwitchCurrencyAdapter.ItemEntity titleItem = new SwitchCurrencyAdapter.ItemEntity(fChat);
//                mAllData.add(titleItem);
//            }
//            boolean isChecked = EmptyUtils.isNotEmpty(getCheckedTokens()) && getCheckedTokens().contains(currencyInfo.token);
//            SwitchCurrencyAdapter.ItemEntity contentItem = new SwitchCurrencyAdapter.ItemEntity(currencyInfo, isChecked);
//            mAllData.add(contentItem);
//        }
//        mShowData.addAll(mAllData);
//        getAdapter().setNewInstance(mShowData);
    }

    @Override
    public void onKeywordsChanged(String keywords) {
        mIsInputKeywords = true;
        mShowData.clear();
        if (EmptyUtils.isEmpty(keywords) || EmptyUtils.isEmpty(keywords.trim())) {
            getAdapter().notifyDataSetChanged();
            mSmartRecyclerView.getStatusView().showContent();
            return;
        } else {
            keywords = keywords.trim().toLowerCase();
            for (SwitchCurrencyAdapter.ItemEntity item : mAllData) {
                if (!item.manualAdd && item.getItemType() == SwitchCurrencyAdapter.ItemEntity.ItemType.CONTENT.ordinal()) {
                    if (item.token.name.toLowerCase().contains(keywords) || item.token.token.toLowerCase().contains(keywords) || item.token.countryCode.toLowerCase().contains(keywords)) {
                        mShowData.add(item);
                    }
                }
            }
            getAdapter().notifyDataSetChanged();
            if (EmptyUtils.isEmpty(mShowData)) {
                mSmartRecyclerView.getStatusView().showEmpty();
            } else {
                mSmartRecyclerView.getStatusView().showContent();
            }
        }
    }

    @Override
    public void onCloseSearch() {
        mIsInputKeywords = false;
        mShowData.clear();
        mShowData.addAll(mAllData);
        getAdapter().notifyDataSetChanged();
        mSmartRecyclerView.getStatusView().showContent();
    }

    @Override
    public void onTouchLetterEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                Log.i("duxl.log", "Action move: x=" + e.getX() + ", y=" + e.getY());
                if (mLetterPopupWindow != null) {
                    if (e.getY() < 0 || e.getY() > getSideBar().getHeight()) {
                        return;
                    }
                    mLetterPopupWindow.update((int) e.getY() + mSideBarMarginTop - getSideBar().getHeight() / 2);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                Log.i("duxl.log", "Action cancel");
                if (mLetterPopupWindow != null) {
                    mLetterPopupWindow.dismiss();
                    mLetterPopupWindow = null;
                }
                break;
        }
    }

    @Override
    public void onTouchingLetterChanged(String s) {
        if (mLetterPopupWindow == null) {
            mLetterPopupWindow = new LetterPopupWindow(this);
            mLetterPopupWindow.show(getSideBar());
        }
        mLetterPopupWindow.setLetter(s);

        if ("#".equals(s)) {
            ((LinearLayoutManager) mSmartRecyclerView.getContentView().getLayoutManager()).scrollToPositionWithOffset(0, 0);
            return;
        }

        int itemCount = mAdapter.getItemCount();
        for (int i = 0; i < itemCount; i++) {
            SwitchCurrencyAdapter.ItemEntity itemEntity = mAdapter.getItemOrNull(i);
            if (EmptyUtils.isNotNull(itemEntity)) {
                if (itemEntity.getItemType() == SwitchCurrencyAdapter.ItemEntity.ItemType.TITLE.ordinal()) {
                    if (s.equalsIgnoreCase(itemEntity.title)) {
                        ((LinearLayoutManager) mSmartRecyclerView.getContentView().getLayoutManager()).scrollToPositionWithOffset(i, 0);
                        break;
                    }
                }
            }
        }
    }

    private class StickyHeader extends RecyclerView.ItemDecoration {
        int titleHeight;

        public StickyHeader(RecyclerView recyclerView) {
            recyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
                @Override
                public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                    // 禁止title区域下面的Item被点击
                    return !mIsInputKeywords && e.getY() < titleHeight;
                }
            });
        }

        @Override
        public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.onDrawOver(c, parent, state);
            if (mIsInputKeywords) {
                return;
            }

            View firstView = parent.getChildAt(0);
            int position = parent.getChildAdapterPosition(firstView);
            SwitchCurrencyAdapter.ItemEntity itemEntity = mAdapter.getItemOrNull(position);
            if (EmptyUtils.isNull(itemEntity)) {
                return;
            }

            SwitchCurrencyAdapter.ItemEntity nextItemEntity = mAdapter.getItemOrNull(position + 1);
            if (itemEntity.getItemType() == SwitchCurrencyAdapter.ItemEntity.ItemType.TITLE.ordinal()) {
                Bitmap bitmap = getTitleBitmap(itemEntity.title);
                titleHeight = bitmap.getHeight();
                int top = firstView.getTop();
                if (nextItemEntity != null && nextItemEntity.getItemType() == SwitchCurrencyAdapter.ItemEntity.ItemType.CONTENT.ordinal()) {
                    top = 0;
                }
                c.drawBitmap(bitmap, 0, top, null);
                bitmap.recycle();
            } else {
                Bitmap bitmap = getTitleBitmap(itemEntity.token.fchat);
                titleHeight = bitmap.getHeight();
                int top = 0;
                if (nextItemEntity != null && nextItemEntity.getItemType() == SwitchCurrencyAdapter.ItemEntity.ItemType.TITLE.ordinal()) {
                    if (firstView.getTop() < 0 && firstView.getHeight() + firstView.getTop() < titleHeight) {
                        top = firstView.getHeight() + firstView.getTop() - titleHeight;
                    }
                }
                c.drawBitmap(bitmap, 0, top, null);
                bitmap.recycle();
            }
        }

        private Bitmap getTitleBitmap(String title) {
            TextView tvTitle = (TextView) LayoutInflater.from(SearchCurrencyActivity.this).inflate(R.layout.adapter_switch_currency_title_listitem, null);
            tvTitle.setBackgroundColor(getResources().getColor(R.color.theme_bg));
            tvTitle.setText(title);
            int size = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            tvTitle.measure(size, size);
            tvTitle.layout(0, 0, DisplayUtil.getScreenWidth(SearchCurrencyActivity.this) - DisplayUtil.dip2px(SearchCurrencyActivity.this, 84f), tvTitle.getMeasuredHeight());
            tvTitle.setDrawingCacheEnabled(true);
            Bitmap bitmap = tvTitle.getDrawingCache();
            return bitmap;
        }
    }
}