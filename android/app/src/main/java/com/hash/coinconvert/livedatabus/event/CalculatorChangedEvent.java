package com.hash.coinconvert.livedatabus.event;

import java.math.BigDecimal;

/**
 * 计算器输入已改变事件
 */
public class CalculatorChangedEvent {

    /**
     * 触发事件的对象
     */
    public Object object;

    public String token;

    /**
     * 金额(输入的或表达式计算后的结果)
     */
    public String amount;

    /**
     * 是否是hint金额
     */
    public boolean isHintAmount;

    /**
     * 换算成美元后的金额
     */
    public BigDecimal dollarAmount;

    public CalculatorChangedEvent(Object object, String token, String amount, boolean isHintAmount, BigDecimal dollarAmount) {
        this.object = object;
        this.token = token;
        this.amount = amount;
        this.isHintAmount = isHintAmount;
        this.dollarAmount = dollarAmount;
    }
}
