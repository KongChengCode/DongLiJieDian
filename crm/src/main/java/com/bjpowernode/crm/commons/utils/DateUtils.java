package com.bjpowernode.crm.commons.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 对Date类型数据进行处理的工具类
 */
public class DateUtils {
    /**
     * 对指定的Date进行格式化：yyyy-MM-dd HH:mm:ss
     * @param date
     * @return
     */
    public static String formatDateTime(Date date){
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = sf.format(date);
        return dateStr;
    }
}
