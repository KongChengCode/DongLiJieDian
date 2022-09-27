package com.bjpowernode.test.POI;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 使用Apache-poi生成Excel文件
 */
public class CreateExcelTest {
    @Test
    public void createPoi() throws IOException {
        //创建HSSFWorkbook对象，对应一个Excel文件
        HSSFWorkbook wb = new HSSFWorkbook();
        //使用wb创建HSSFSheet对象，对应wb文件中的一页
        HSSFSheet sheet = wb.createSheet("学生列表");
        //使用sheet创建HSSFRows对象，对应sheet中的一行
        HSSFRow row = sheet.createRow(0);//行号:从0开始,依次增加
        //使用row创建HSSFCell对象，对应row中间的列
        HSSFCell cell = row.createCell(0);//列号:从0开始,依次增加
        cell.setCellValue("学号");
        cell = row.createCell(1);
        cell.setCellValue("姓名");
        cell = row.createCell(2);
        cell.setCellValue("年龄");

        //生成样式对象HSSFCellStyle
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        //使用sheet创建10个HSSFRow对象，对应sheet中的10行
        for (int i = 1; i < 11; i++) {
            row = sheet.createRow(i);

            cell = row.createCell(0);
            cell.setCellValue(100+i);
            cell = row.createCell(1);
            cell.setCellValue("NAME"+i);
            cell = row.createCell(2);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(20+i);
        }

        //调用工具函数生成Excel文件
        OutputStream os = new FileOutputStream("E:\\IdeaWorkspace\\crm-project\\studentList.xls");
        //文件可以不创建，但是目录必须创建好
        wb.write(os);

        //关闭资源
        os.close();
        wb.close();

        System.out.println("====================create successfully==================");
    }
}
