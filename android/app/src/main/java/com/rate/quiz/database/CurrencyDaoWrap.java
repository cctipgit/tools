package com.rate.quiz.database;

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.rate.quiz.entity.CurrencyInfo;

import java.util.List;

public class CurrencyDaoWrap implements DefaultLifecycleObserver {

    private CurrencyDao currencyDao;

    public CurrencyDaoWrap(Lifecycle lifecycle) {
        currencyDao = new CurrencyDao();
        lifecycle.addObserver(this);
    }

    /**
     * 添加货币
     *
     * @param list
     */
    public void insert(List<CurrencyInfo> list) {
        currencyDao.insert(list);
    }

    /**
     * 添加货币
     *
     * @param currencyInfo
     */
    public void insert(CurrencyInfo currencyInfo) {
        currencyDao.insert(currencyInfo);
    }

    /**
     * 修改价格
     * @param list
     */
    public void updatePrice(List<CurrencyInfo> list) {
        currencyDao.updatePrice(list);
    }

    /**
     * 删除数据
     *
     * @param list
     */
    @SuppressLint("NewApi")
    public void delete(List<CurrencyInfo> list) {
        currencyDao.delete(list);
    }

    /**
     * 根据指定的token删除数据
     *
     * @param tokens
     */
    public void deleteByTokens(List<String> tokens) {
        currencyDao.deleteByTokens(tokens);
    }

    /**
     * 获取所有的货币
     *
     * @return
     */
    public List<CurrencyInfo> getCurrencyAll() {
        return currencyDao.getCurrencyAll();
    }

    /**
     * 根据指定tokens获取货币
     *
     * @param tokens
     * @return
     */
    public List<CurrencyInfo> getCurrencyByTokens(List<String> tokens) {
        return currencyDao.getCurrencyByTokens(tokens);
    }

    /**
     * 根据指定tokens获取货币
     *
     * @param tokens
     * @return
     */
    public List<CurrencyInfo> getCurrencyByTokens(SQLiteDatabase db, List<String> tokens) {
        return currencyDao.getCurrencyByTokens(db, tokens);
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        currencyDao.close();
    }
}
