package com.bjpowernode.crm.test;

import com.bjpowernode.crm.commons.constants.Constants;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.aspectj.apache.bcel.classfile.Constant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
public class TestController {

    @RequestMapping("/test/fileDownload.do")
    public void fileDownload(HttpServletResponse response) throws Exception {
        //首先在服务器端生成excel文件
        //生成excel文件
        HSSFWorkbook wb = new HSSFWorkbook();
        //生成页
        HSSFSheet sheet = wb.createSheet("NO1");
        //生成行


        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);


        //生成列
        HSSFRow row = sheet.createRow(0);
        row.setRowStyle(cellStyle);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("姓名");
        cell = row.createCell(1);
        cell.setCellValue("年龄");



        //第二行
        row = sheet.createRow(1);
        row.setRowStyle(cellStyle);
        cell = row.createCell(0);
        cell.setCellValue("张三");
        cell = row.createCell(1);
        cell.setCellValue("12");

        //第三行
        row = sheet.createRow(2);
        row.setRowStyle(cellStyle);
        cell = row.createCell(0);
        cell.setCellValue("李四");
        cell = row.createCell(1);
        cell.setCellValue("45");


        //写入
        OutputStream out = new FileOutputStream("E:\\IdeaWorkspace\\crm-project\\doc\\testDocument.xls");
        wb.write(out);

        //关闭
        wb.close();
        out.close();



        //从服务器下载文件
        //1.设置响应类型
        response.setContentType("application/octet-stream;charset=UTF-8");
        //2.设置输入输出流
        OutputStream outputStream = response.getOutputStream();
        FileInputStream fis = new FileInputStream("E:\\IdeaWorkspace\\crm-project\\doc\\testDocument.xls");

        //3.设置响应头,使它不直接显示到浏览器上，而是以文件下载形式存放
        response.addHeader("Content-Disposition","attachment;filename=testDocument.xls");

        //4.输出到浏览器
        int length = 0;
        byte[] buff = new byte[256];
        while((length = fis.read(buff)) != -1){
            outputStream.write(buff,0,length);
        }

        //关闭并刷新
        fis.close();
        outputStream.flush();

    }

    /**
     * 反例，同步请求返回JSON字符串
     * 配置SpringMVC的文件上传解析器
     * @return
     */
    @RequestMapping("/workbench/activity/fileUpload.do?t=12345")
    @ResponseBody//虽然是同步请求，但是这个注解不是ajax的专利，负责将返回的值转化为json字符串
    public Object fileUpload(String userName, MultipartFile myFile) throws IOException {
        //MultipartFile 这是SpringMVC提供的，将请求体里面的信息封装到这个类里面；SpringMVC调用了一个类到请求体内拿数据封装成这个类
        //把文本数据打印到控制台
        System.out.println("userName = " + userName);
        //把文件在服务器指定的目录中生成一个同样的文件
        //文件后缀名不能写死
        String originalFilename = myFile.getOriginalFilename();
        File file = new File("E:\\IdeaWorkspace\\crm-project\\doc\\" + originalFilename);//路径必须手动创建好，文件如果不存在，会自动创建
        myFile.transferTo(file);

        //返回响应信息
        ReturnObject returnObject = new ReturnObject();
        returnObject.setMessage(Constants.RETURN_OBJECT_CODE_SUCCESS);
        returnObject.setMessage("上传成功");
    return returnObject;
    }
}
