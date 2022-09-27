package com.bjpowernode.test.POI;

import com.bjpowernode.crm.commons.utils.HSSFUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 使用apche-poi解析excel类
 */
public class ParseExcel {

    @Test
    public void parseExcel() throws IOException {
        //根据excel文件生成HSSFWorkbook对象，封装了excel文件的所有信息
        InputStream is = new FileInputStream("E:\\IdeaWorkspace\\crm-project\\doc\\testDocument.xls");
        HSSFWorkbook wb = new HSSFWorkbook(is);
        //根据wb获取HSSFSheet对象，封装一页的所有信息
        HSSFSheet sheet = wb.getSheetAt(0);
        //根据sheet获取HSSFRow对象，封装了一行的所有信息
        HSSFRow row = null;
        HSSFCell cell = null;
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            //sheet.getLastRowNum()最后一行的下标
            row = sheet.getRow(i);//行的下标从0开始，依次增加

            for (int j = 0;j < row.getLastCellNum();j++) {
                //row.getLastCellNum()是最后一列的下标+1，注意与上面获取最后一行下标的方法不一样
                //根据row获取HSSFCell对象，获取一列的所有信息
                cell = row.getCell(j);//列的下标从0开始，依次增加

                //获取列中的元素
                System.out.print(HSSFUtils.getCellValueForStr(cell) + " ");
            }
            System.out.println();
        }

    }

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
