package com.hash.coinconvert.livedatabus.event;

import com.hash.coinconvert.entity.CurrencyInfo;

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
