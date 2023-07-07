package com.hash.coinconvert.entity;

/**
 * {
 * "fromSymbol": "AED", // 兑换货币标识
 * "fromType": 1,		 // 1-法币 2-虚拟币
 * "toSymbol": "AUD",   // 目标货币标识
 * "toType": 1,		 // 1-法币 2-虚拟币
 * "interval": 4		 // 时间 1-当天 2-7天 3-1月 4-一年
 * }
 */
public class ChartRequestBody {
    public String fromSymbol;
    public int fromType;
    public String toSymbol;
    public int toType;
    public int interval;
}
