package com.rate.quiz.quotes;

import static com.rate.quiz.Constants.SP.DEFAULT.DEFAULT_CURRENCY_VALUE;

import android.annotation.SuppressLint;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.duxl.baselib.utils.EmptyUtils;
import com.duxl.baselib.utils.SPUtils;
import com.exchange2currency.ef.currencyconvert.grpc.Price;
import com.rate.quiz.Constants;
import com.rate.quiz.entity.QuotesItem;
import com.rate.quiz.utils.AmountUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

public class RateQuotesUtils {
    public static BigDecimal currencyQuotesRateCalculate(String currency,String formCurrency,String formPrice,String toPrice){
      int amount = currency == formCurrency ? SPUtils.getInstance().getInt(Constants.SP.KEY.DEFAULT_CURRENCY_VALUE) : 1;
        BigDecimal form = new BigDecimal(formPrice);
        BigDecimal to = new BigDecimal(toPrice);
        BigDecimal toForm = form
                .divide(to,20, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(amount));
        if (to == BigDecimal.ZERO){
          return new BigDecimal("0");
      }
      return toForm;
    }
    public static String currentcyNumber(String currentcy){
        if (currentcy.equals("digital")){
            SPUtils.getInstance().put(Constants.CURRENCY_TYPE.DIGITAL,"1");
            return SPUtils.getInstance().getString(Constants.CURRENCY_TYPE.DIGITAL);
        }else if (currentcy.equals("currency")){
            SPUtils.getInstance().put(Constants.CURRENCY_TYPE.CURRENCY,SPUtils.getInstance().getInt(Constants.SP.KEY.DEFAULT_CURRENCY_VALUE,DEFAULT_CURRENCY_VALUE)+"");
            return SPUtils.getInstance().getString(Constants.CURRENCY_TYPE.CURRENCY);
        }else {
            SPUtils.getInstance().put(Constants.CURRENCY_TYPE.FUTURES,"1");
            return SPUtils.getInstance().getString(Constants.CURRENCY_TYPE.FUTURES);
        }
    }
    public static String currentcyNumber2(String currentcy){
        if (currentcy.equals("digital")){
            SPUtils.getInstance().put(Constants.CURRENCY_TYPE.DIGITAL,"digital");
            return SPUtils.getInstance().getString(Constants.CURRENCY_TYPE.DIGITAL);
        }else if (currentcy.equals("currency")){
            SPUtils.getInstance().put(Constants.CURRENCY_TYPE.CURRENCY,"currency");
            return SPUtils.getInstance().getString(Constants.CURRENCY_TYPE.CURRENCY);
        }else { // 期货
            SPUtils.getInstance().put(Constants.CURRENCY_TYPE.FUTURES,"futures");
            return SPUtils.getInstance().getString(Constants.CURRENCY_TYPE.FUTURES);
        }
    }
    @SuppressLint("LongLogTag")
    public static String calculationCurrentcyPrecision(BigDecimal value, String currentcy){
        BigDecimal val = value;
        BigDecimal bigDecimal = val;

        if (currentcy.equals("digital")){
            switch (SPUtils.getInstance().getInt(Constants.SP.KEY.DECIMICAL_CRYPTOCURRENCY,Constants.SP.DEFAULT.DECIMICAL_CRYPTOCURRENCY)){
                case 0:
                    val = bigDecimal.setScale(0,RoundingMode.HALF_UP);
                    break;
                case 2:
                    val = bigDecimal.setScale(2,RoundingMode.HALF_UP);
                    break;
                case 4:
                    val = bigDecimal.setScale(4,RoundingMode.HALF_UP);
                    break;
                case 6:
                    val = bigDecimal.setScale(6,RoundingMode.HALF_UP);
                    break;
                case 8:
                    val = bigDecimal.setScale(8,RoundingMode.HALF_UP);
                    break;
                case 10:
                    val = bigDecimal.setScale(10,RoundingMode.HALF_UP);
                    break;
                case 12:
                    val = bigDecimal.setScale(12,RoundingMode.HALF_UP);
                    break;
            }

        }else if (currentcy.equals("currency")){
            switch (SPUtils.getInstance().getInt(Constants.SP.KEY.DECIMICAL_LEGAL_TENDER,Constants.SP.DEFAULT.DECIMICAL_LEGAL_TENDER)){
                case 0:
                    val = bigDecimal.setScale(0,RoundingMode.HALF_UP);
                    break;
                case 2:
                    val = bigDecimal.setScale(2,RoundingMode.HALF_UP);
                    break;
                case 4:
                    val = bigDecimal.setScale(4,RoundingMode.HALF_UP);
                    break;
                case 6:
                    val = bigDecimal.setScale(6,RoundingMode.HALF_UP);
                    break;
                case 8:
                    val =bigDecimal.setScale(8,RoundingMode.HALF_UP);
                    break;
                case 10:
                    val =bigDecimal.setScale(10,RoundingMode.HALF_UP);
                    break;
                case 12:
                    val = bigDecimal.setScale(12,RoundingMode.HALF_UP);
                    break;
            }
        }else { // 期货

        }

        return val.toString();
    }

    @SuppressLint("LongLogTag")
    public static BigDecimal calculationAvg(BigDecimal value, String currentcy){
        BigDecimal val = value;
        if (currentcy.equals("digital")){
            switch (SPUtils.getInstance().getInt(Constants.SP.KEY.DECIMICAL_CRYPTOCURRENCY)){
                case 0:
                    val = value.setScale(0,RoundingMode.HALF_UP);
                    break;
                case 2:
                    val = value.setScale(2,RoundingMode.HALF_UP);
                    break;
                case 4:
                    val = value.setScale(4,RoundingMode.HALF_UP);
                    break;
                case 6:
                    val = value.setScale(6,RoundingMode.HALF_UP);
                    break;
                case 8:
                    val = value.setScale(8,RoundingMode.HALF_UP);
                    break;
                case 10:
                    val = value.setScale(10,RoundingMode.HALF_UP);
                    break;
                case 12:
                    val = value.setScale(12,RoundingMode.HALF_UP);
                    break;
            }

        }else if (currentcy.equals("currency")){
            switch (SPUtils.getInstance().getInt(Constants.SP.KEY.DECIMICAL_LEGAL_TENDER)){
                case 0:
                    val = value.setScale(0,RoundingMode.HALF_UP);
                    break;
                case 2:
                    val = value.setScale(2,RoundingMode.HALF_UP);
                    break;
                case 4:
                    val = value.setScale(4,RoundingMode.HALF_UP);
                    break;
                case 6:
                    val = value.setScale(6,RoundingMode.HALF_UP);
                    break;
                case 8:
                    val = value.setScale(8,RoundingMode.HALF_UP);
                    break;
                case 10:
                    val = value.setScale(10,RoundingMode.HALF_UP);
                    break;
                case 12:
                    val = value.setScale(12,RoundingMode.HALF_UP);
                    break;
            }
        }else {

        }

        return val;

    }
    public static void beforePageInfo(String currencyInfo)  {
        Map map =  RateQuotesUtils.transStringToMap(currencyInfo);
        String s = SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_NAME);
        if (TextUtils.isEmpty(s)){
            SPUtils.getInstance().put(Constants.SP.KEY.CURRENT_NAME,map.get("slideToken").toString()+"/"+map.get("firstToken").toString());
            SPUtils.getInstance().put(Constants.SP.KEY.CURRENT_ICON, map.get("slideIcon").toString());
            SPUtils.getInstance().put(Constants.SP.KEY.CURRENT_FIRST,map.get("firstIcon").toString());
            SPUtils.getInstance().put(Constants.SP.KEY.CURRENT_TYPE,map.get("firstType").toString());
            SPUtils.getInstance().put(Constants.SP.KEY.CURRENT_SLIDE_TYPE,map.get("currencyType").toString());
        }else {
            String[] temp = s.split("/");
            if (temp[1].equals(map.get("firstToken"))){
                SPUtils.getInstance().put(Constants.SP.KEY.CURRENT_TYPE,map.get("firstType").toString());
                SPUtils.getInstance().put(Constants.SP.KEY.CURRENT_SLIDE_TYPE,map.get("currencyType").toString());
                SPUtils.getInstance().put(Constants.SP.KEY.CURRENT_NAME,map.get("slideToken").toString()+"/"+temp[1]);
                SPUtils.getInstance().put(Constants.SP.KEY.CURRENT_ICON, map.get("slideIcon").toString());
                SPUtils.getInstance().put(Constants.SP.KEY.CURRENT_FIRST,SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_FIRST));
            }else {
                SPUtils.getInstance().put(Constants.SP.KEY.CURRENT_TYPE,map.get("firstType").toString());
                SPUtils.getInstance().put(Constants.SP.KEY.CURRENT_SLIDE_TYPE,map.get("currencyType").toString());
                SPUtils.getInstance().put(Constants.SP.KEY.CURRENT_NAME,map.get("slideToken").toString()+"/"+temp[1]);
                SPUtils.getInstance().put(Constants.SP.KEY.CURRENT_ICON, map.get("slideIcon").toString());
                SPUtils.getInstance().put(Constants.SP.KEY.CURRENT_FIRST,SPUtils.getInstance().getString(Constants.SP.KEY.CURRENT_FIRST));
            }
        }
    }
    public static String transMapToString(Map map){
        java.util.Map.Entry entry;
        StringBuffer sb = new StringBuffer();
        for(Iterator iterator = map.entrySet().iterator(); iterator.hasNext();)
        {
            entry = (java.util.Map.Entry)iterator.next();
            sb.append(entry.getKey().toString()).append( "'" ).append(null==entry.getValue()?"":
                    entry.getValue().toString()).append (iterator.hasNext() ? "^" : "");
        }
        return sb.toString();
    }
    public static Map transStringToMap(String mapString){
        Map map = new HashMap();
        java.util.StringTokenizer items;
        for(StringTokenizer entrys = new StringTokenizer(mapString, "^"); entrys.hasMoreTokens();
            map.put(items.nextToken(), items.hasMoreTokens() ? ((Object) (items.nextToken())) : null))
            items = new StringTokenizer(entrys.nextToken(), "'");
        return map;
    }

    public static IntentFilter Filter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.hash.filterAction");
        return intentFilter;
    }


    public static String formatCount(String count, boolean isCalculator,String currencyType,int mDecimicalLegalTender,int mDecimicalCryptocurrency) {

        if (EmptyUtils.isEmpty(count)) {
            return count;
        }
        int decimal = mDecimicalLegalTender;
        if ("digital".equalsIgnoreCase(RateQuotesUtils.currentcyNumber2(currencyType))) {
            decimal = mDecimicalCryptocurrency;
        }
        if (isCalculator) {
            return AmountUtils.formatAmount(count, decimal, 3, false);
        }
        return AmountUtils.formatAmount(count, decimal, 3, true);
    }

    public static ArrayList<QuotesItem> addQuotesAllList(Price.GetQuotationResponse getQuotationResponse){
        ArrayList<QuotesItem> quotesItemList = new ArrayList<>();
        for (int i = 0;i<getQuotationResponse.getDataList().size(); i++){
            QuotesItem quotesItem = new QuotesItem();
            quotesItem.setAmount(getQuotationResponse.getAmount());
            quotesItem.setDateUnit(getQuotationResponse.getDateUnit());
            quotesItem.setPriceFrom(getQuotationResponse.getPriceFrom());
            quotesItem.setPriceTo(getQuotationResponse.getPriceTo());
            quotesItem.setTokenFrom(getQuotationResponse.getTokenFrom());
            quotesItem.setTokenTo(getQuotationResponse.getTokenTo());
            quotesItem.setCmd(String.valueOf(getQuotationResponse.getCmd()));
            quotesItem.setMid(i+"");
            quotesItem.setPrice_time(getQuotationResponse.getDataList().get(i).getPriceTime()+"");
            quotesItem.setChildPriceFrom(getQuotationResponse.getDataList().get(i).getPriceFrom());
            quotesItem.setChildPriceTo(getQuotationResponse.getDataList().get(i).getPriceTo());
            quotesItem.setChildPriceDate(getQuotationResponse.getDataList().get(i).getPriceDate());
            quotesItemList.add(quotesItem);
        }
        return quotesItemList;
    }

    public static ArrayList<QuotesItem> updateQuotesCurrentList(Price.GetQuotationResponse getQuotationResponse, int mid){
        ArrayList<QuotesItem> queryQuotesAllList = new ArrayList<>();
        Price.QutationBaseData qutationBaseData = getQuotationResponse.getDataList().get(0);
        QuotesItem item2 = new QuotesItem();
        item2.setAmount(getQuotationResponse.getAmount());
        item2.setDateUnit(getQuotationResponse.getDateUnit());
        item2.setPriceFrom(getQuotationResponse.getPriceFrom());
        item2.setPriceTo(getQuotationResponse.getPriceTo());
        item2.setTokenFrom(getQuotationResponse.getTokenFrom());
        item2.setTokenTo(getQuotationResponse.getTokenTo());
        item2.setCmd(String.valueOf(getQuotationResponse.getCmd()));
        item2.setMid(new BigDecimal(mid).add(new BigDecimal(1)).toPlainString()+"");
        item2.setChildPriceFrom(qutationBaseData.getPriceFrom());
        item2.setChildPriceTo(qutationBaseData.getPriceTo());
        item2.setChildPriceDate(qutationBaseData.getPriceDate());
        queryQuotesAllList.add(item2);
        return queryQuotesAllList;
    }
}
