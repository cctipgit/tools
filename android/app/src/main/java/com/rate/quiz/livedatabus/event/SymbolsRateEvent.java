package com.rate.quiz.livedatabus.event;

import com.rate.quiz.entity.CurrencyInfo;

import java.util.List;

/**
 * 汇率更新事件
 */
public class SymbolsRateEvent {

    private List<CurrencyInfo> list;

    public SymbolsRateEvent(List<CurrencyInfo> list) {
        this.list = list;
    }

    public List<CurrencyInfo> getList() {
        return list;
    }
}
