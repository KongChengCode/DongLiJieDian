package com.bjpowernode.crm.commons.utils;

import org.apache.poi.hssf.usermodel.HSSFCell;

/**
 * 关于excel文件操作的工具类
 */
public class HSSFUtils {

    /**
     * 从指定的HSSFCell对象中获取列的值
     */
    public static String getCellValueForStr(HSSFCell cell){

        String result = "";
        if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
            result = cell.getStringCellValue();
        }else if(cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC){
            //result = Double.toString(cell.getNumericCellValue())；
            result = cell.getNumericCellValue() + "";
        }else if(cell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN){
            result = cell.getBooleanCellValue() + "";
        }else if(cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA){
            //公式类型
            result = cell.getCellFormula() + "";
        }else {
            //空类型或者错误类型
            result = "";
        }
        return result;
    }
}
