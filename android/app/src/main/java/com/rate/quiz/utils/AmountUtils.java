package com.rate.quiz.utils;

import android.text.TextUtils;

import com.duxl.baselib.utils.ArithmeticUtils;
import com.duxl.baselib.utils.EmptyUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 金额相关工具类
 * create by duxl 2021/9/30
 */
public class AmountUtils {

    /**
     * 数量是否为零
     *
     * @param amount 数量，可能传入的字符串是0、0.0、0.00等格式
     * @return
     */
    public static boolean amountZero(String amount) {
        try {
            if (EmptyUtils.isNotEmpty(amount)) {
                amount = amount.replaceAll(",", "");
                String formatValid = ArithmeticUtils.formatValid(new BigDecimal(amount));
                return TextUtils.equals("0", formatValid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 格式化金额
     *
     * @param amount   金额
     * @param newScale 需要的小数位
     * @return
     */
    public static String formatAmount(String amount, int newScale) {
        return formatAmount(ArithmeticUtils.newBigDecimal(amount), newScale);
    }

    /**
     * 格式化金额
     *
     * @param amount   金额
     * @param newScale 需要的小数位
     * @return
     */
    public static String formatAmount(BigDecimal amount, int newScale) {
        return formatAmount(amount, newScale, 3, true);
    }


    /**
     * 格式化金额
     *
     * @param amount    金额
     * @param newScale  需要的小数位
     * @param groupSize 整数位分组大小
     * @param minDigits 小数位末尾0是否保留
     * @return
     */
    public static String formatAmount(String amount, int newScale, int groupSize, boolean minDigits) {
        return formatAmount(ArithmeticUtils.newBigDecimal(amount), newScale, groupSize, minDigits);
    }

    /**
     * 格式化金额
     *
     * @param amount    金额
     * @param newScale  需要的小数位
     * @param groupSize 整数位分组大小
     * @param minDigits 小数位末尾0是否保留
     * @return
     */
    public static String formatAmount(BigDecimal amount, int newScale, int groupSize, boolean minDigits) {
        return ArithmeticUtils.convertNum(amount, newScale, groupSize, minDigits, RoundingMode.HALF_UP);
    }

    /**
     * 两数相除
     *
     * @param one   被除数
     * @param two   除数
     * @param scale 保留的小数位
     * @return
     */
    public static BigDecimal divide(BigDecimal one, BigDecimal two, int scale) {
        return one.divide(two, scale, RoundingMode.HALF_UP);
    }
}
