package com.rate.quiz.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 计算器
 */
public class Calculator {

    private static final String TAG = "Calculator";

    /**
     * 两个数的计算
     *
     * @param a1           数字1
     * @param a2           数字2
     * @param operator     运算符
     * @param scale        保留小数位数
     * @param roundingMode 舍入模式
     * @return
     * @throws Exception
     */
    private static BigDecimal doubleCal(BigDecimal a1, BigDecimal a2, char operator, int scale, RoundingMode roundingMode) throws Exception {
        switch (operator) {
            case '+':
                return a1.add(a2);
            case '-':
                return a1.subtract(a2);
            case '*':
                return a1.multiply(a2);
            case '/':
                return a1.divide(a2, scale, roundingMode);
            default:
                break;
        }
        throw new Exception("illegal operator!");
    }

    /**
     * 定义优先级
     *
     * @param s
     * @return
     * @throws Exception
     */
    private static int getPriority(String s) throws Exception {
        if (s == null) return 0;
        switch (s) {
            case "(":
                return 1;
            case "+":
                ;
            case "-":
                return 2;
            case "*":
                ;
            case "/":
                return 3;
            default:
                break;
        }
        throw new Exception("illegal operator!");
    }

    /**
     * 将中缀表达式解析为后缀表达式
     *
     * @param expr
     * @return
     * @throws Exception
     */
    private static String toSufExpr(String expr) throws Exception {
        //System.out.println("将" + expr + "解析为后缀表达式...");
        /*返回结果字符串*/
        StringBuffer sufExpr = new StringBuffer();
        /*盛放运算符的栈*/
        Stack<String> operator = new Stack<String>();
        operator.push(null);//在栈顶压人一个null，配合它的优先级，目的是减少下面程序的判断
        /* 将expr打散分散成运算数和运算符 */
        Pattern p = Pattern.compile("(?<!\\d)-?\\d+(\\.\\d+)?|[+\\-*/()]");//这个正则为匹配表达式中的数字或运算符
        Matcher m = p.matcher(expr);
        while (m.find()) {
            String temp = m.group();
            if (temp.matches("[+\\-*/()]")) { //是运算符
                if (temp.equals("(")) { //遇到左括号，直接压栈
                    operator.push(temp);
                    //System.out.println("'('压栈");
                } else if (temp.equals(")")) { //遇到右括号，弹栈输出直到弹出左括号（左括号不输出）
                    String topItem = null;
                    while (!(topItem = operator.pop()).equals("(")) {
                        System.out.println(topItem + "弹栈");
                        sufExpr.append(topItem + " ");
                        //System.out.println("输出:" + sufExpr);
                    }
                } else {//遇到运算符，比较栈顶符号，若该运算符优先级大于栈顶，直接压栈；若小于栈顶，弹栈输出直到大于栈顶，然后将该运算符压栈。
                    while (getPriority(temp) <= getPriority(operator.peek())) {
                        sufExpr.append(operator.pop() + " ");
                        //System.out.println("输出sufExpr:" + sufExpr);
                    }
                    operator.push(temp);
                    //System.out.println("\"" + temp + "\"" + "压栈");
                }
            } else {//遇到数字直接输出
                sufExpr.append(temp + " ");
                //System.out.println("输出sufExpr:" + sufExpr);
            }
        }

        String topItem = null;//最后将符合栈弹栈并输出
        while (null != (topItem = operator.pop())) {
            sufExpr.append(topItem + " ");
        }
        return sufExpr.toString();
    }

    /**
     * 计算表达式
     *
     * @param expr         表达式，例如：3+2-5*0
     * @param scale        保留小数位位数
     * @param roundingMode 舍入方式
     * @return
     */
    public static BigDecimal evaluate(String expr, int scale, RoundingMode roundingMode) {
        try {
            String sufExpr = toSufExpr(expr);// 转为后缀表达式
            //System.out.println("开始计算后缀表达式...");
            /* 盛放数字栈 */
            Stack<BigDecimal> number = new Stack();
            /* 这个正则匹配每个数字和符号 */
            Pattern p = Pattern.compile("-?\\d+(\\.\\d+)?|[+\\-*/]");
            Matcher m = p.matcher(sufExpr);
            while (m.find()) {
                String temp = m.group();
                if (temp.matches("[+\\-*/]")) {// 遇到运算符，将最后两个数字取出，进行该运算，将结果再放入容器
                    //System.out.println("符号" + temp);
                    BigDecimal a1 = number.pop();
                    BigDecimal a2 = number.pop();
                    BigDecimal res = doubleCal(a2, a1, temp.charAt(0), scale, roundingMode);
                    number.push(res);
                    //System.out.println(a2 + "和" + a1 + "弹栈，并计算" + a2 + temp + a1);
                    //System.out.println("数字栈：" + number);
                } else {// 遇到数字直接放入容器
                    number.push(new BigDecimal(temp));
                    //System.out.println("数字栈：" + number);
                }
            }
            return number.pop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

}
