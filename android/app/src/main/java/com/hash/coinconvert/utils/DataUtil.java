package com.hash.coinconvert.utils;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.List;

public class DataUtil {

    public static List<Object> getList(int count) {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(new Object());
        }
        return list;
    }
    /**
     * String 转换 ByteBuffer
     * @param str
     * @return
     */
    public static ByteBuffer getByteBuffer(String str)
    {
        return ByteBuffer.wrap(str.getBytes());
    }

    /**
     * ByteBuffer 转换 String
     * @param buffer
     * @return
     */
    public static String getString(ByteBuffer buffer)
    {
        Charset charset = null;
        CharsetDecoder decoder = null;
        CharBuffer charBuffer = null;
        try
        {
            charset = Charset.forName("UTF-8");
            decoder = charset.newDecoder();
             charBuffer = decoder.decode(buffer);//用这个的话，只能输出来一次结果，第二次显示为空
//            charBuffer = decoder.decode(buffer.asReadOnlyBuffer());
            return charBuffer.toString();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return "";
        }
    }
    public static float getAvg(List<Float> list) {

        float sum=0;

        for(int i = 0; i < list.size(); i++) {

            sum += list.get(i);

        }

        float avg = new BigDecimal(sum).divide(new BigDecimal(list.size()),12,BigDecimal.ROUND_DOWN).floatValue();

        return avg;

    }


}
