package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.commons.constants.Constants;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.commons.utils.DateUtils;
import com.bjpowernode.crm.commons.utils.HSSFUtils;
import com.bjpowernode.crm.commons.utils.UUIDUtils;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.service.ActivityRemarkService;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.*;

@Controller
public class ActivityController {

    @Autowired
    private UserService userService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ActivityRemarkService activityRemarkService;

    @RequestMapping("/workbench/activity/index.do")
    public String index(HttpServletRequest request){
        List<User> userList = userService.queryAllUsers();
        request.setAttribute("userList",userList);
        //session.setAttribute(Constants.USERlIST,userList);

        return "workbench/activity/index";
    }



    @RequestMapping("/workbench/activity/saveCreateActivity.do")
    @ResponseBody/*会将返回的对象自动转化为JSON对象*/
    public Object saveCreateActivity(Activity activity, HttpSession session){
        /*前台jsp页面有6个参数，可以利用实体类传递，后台有9个参数，前台只传递了6个参数，还缺3个参数id , create_time, create_by*/
        //System.out.println("----------------------------------------------------");
        User user = (User)session.getAttribute(Constants.SESSION_USER);
        activity.setId(UUIDUtils.getUUID());
        activity.setCreateTime(DateUtils.formatDateTime(new Date()));
        activity.setCreateBy(user.getId());
        /**
         * 写数据我们一般考虑写成功写失败，也就是说看看是否发生异常；查数据不考虑异常
         */
        ReturnObject returnObject = new ReturnObject();
        try {
            int res = activityService.saveCreateActivity(activity);
            if (res > 0) {
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_SUCCESS);
            } else{
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("系统忙，请稍后重试....");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnObject;
    }

    @RequestMapping("/workbench/activity/queryActivityByConditionForPage.do")
    @ResponseBody
    public Object queryActivityByConditionForPage(String name,String owner,String startDate,
                                                  String endDate,int pageNo,int pageSize){
        Map<String,Object> map = new HashMap<>();
        map.put("name",name);
        map.put("owner",owner);
        map.put("start_date",startDate);
        map.put("end_date",endDate);
        map.put("beginNo",(pageNo - 1) * pageSize);
        map.put("pageSize",pageSize);
        //调用service层方法
        List<Activity> activityList = activityService.queryActivityByConditionForPage(map);
        int totalRows = activityService.queryCountOfActivityByCondition(map);
        //根据查询结果生成相应信息
        Map<String,Object> resMap = new HashMap<>();
        resMap.put("activityList",activityList);
        resMap.put("totalRows",totalRows);
        return resMap;
    }

    @RequestMapping("/workbench/activity/deleteActivityByIds.do")
    @ResponseBody
    public Object deleteActivityByIds(String[] id){
        ReturnObject returnObject = new ReturnObject();
        try {
            //调用service层方法
            int result = activityService.deleteActivityByIds(id);
            if (result > 0) {
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_SUCCESS);
            }else {
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("系统忙，请稍后重试....");
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试....");
        }
        return returnObject;
    }

    @RequestMapping("/workbench/activity/queryActivityById.do")
    @ResponseBody
    public Object queryActivityById(String id){
        Activity activity = activityService.queryActivityById(id);
        return activity;
    }

    @RequestMapping("/workbench/activity/editActivityByIdOfActivity.do")
    @ResponseBody
    public Object editActivityByIdOfActivity(Activity activity,HttpSession session){
        //System.out.println("================================================================");
        User user = (User)session.getAttribute(Constants.SESSION_USER);
        activity.setEditTime(DateUtils.formatDateTime(new Date()));

        //注意输入的是id
        activity.setEditBy(user.getId());
        ReturnObject returnObject = new ReturnObject();
        try {
            int result = activityService.saveEditActivity(activity);
            if (result > 0) {
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_SUCCESS);
            }else {
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("系统忙，请稍后....");
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后....");
        }
        return returnObject;
    }

    @RequestMapping("/workbench/activity/fileDownload.do")
    public void fileDownload(HttpServletResponse response) throws IOException {
        //1.设置响应类型
        response.setContentType("application/octet-stream;charset=UTF-8");
        //2.获取输出流,字节流
        OutputStream out = response.getOutputStream();

        //浏览器接收到响应信息之后，默认情况下，直接显示在窗口打开响应信息；及时打不开，也会调用应用程序打开；只有实在是打不开才会激活文件下载窗口
        //可以设置响应头信息，使浏览器接受响应信息只够，直接激活文件下载窗口，即使能打开也不打开
        response.addHeader("Content-Disposition","attachment;filename=mystudentList.xls");

        //3.读取excel文件（InputStream），把文件输出到浏览器（OutputStream）
        FileInputStream fis = new FileInputStream("E:\\IdeaWorkspace\\crm-project\\studentList.xls");
        byte[] buff = new byte[256];
        int len = 0;
        while((len = fis.read(buff))!=-1){
            out.write(buff,0,len);
        }

        //关闭资源,谁new谁关闭，out是response--Tomcat new的，所以我们不需要关闭
        fis.close();
        out.flush();

    }

    @RequestMapping("/workbench/activity/exportAllActivities.do")
    public void exportAllActivities(HttpServletResponse response) throws IOException {
        //调用service层方法，查询所有市场活动
        List<Activity> activityList = activityService.queryAllActivities();
        //创建excel文件，并且把activityList写入excel文件中
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("市场活动");
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("ID");
        cell = row.createCell(1);
        cell.setCellValue("所有者");
        cell = row.createCell(2);
        cell.setCellValue("名称");
        cell = row.createCell(3);
        cell.setCellValue("开始时间");
        cell = row.createCell(4);
        cell.setCellValue("结束时间");
        cell = row.createCell(5);
        cell.setCellValue("成本");
        cell = row.createCell(6);
        cell.setCellValue("描述");
        cell = row.createCell(7);
        cell.setCellValue("创建时间");
        cell = row.createCell(8);
        cell.setCellValue("创建者");
        cell = row.createCell(9);
        cell.setCellValue("修改时间");
        cell = row.createCell(10);
        cell.setCellValue("修改者");

        //遍历市场活动activityList，创建HSSFRow对象,生成所有的数据行
        Activity activity = null;
        //activityList可能里面没有元素或者根本不存在
        if (activityList != null && activityList.size() > 0) {
            for (int i = 0; i < activityList.size(); i++) {
                activity = activityList.get(i);

                //每遍历一个activity，生成一行
                row = sheet.createRow(i+1);
                //每一行都要创建11列，每一列的数据都从activity获取
                cell = row.createCell(0);
                cell.setCellValue(activity.getId());
                cell = row.createCell(1);
                cell.setCellValue(activity.getOwner());
                cell = row.createCell(2);
                cell.setCellValue(activity.getName());
                cell = row.createCell(3);
                cell.setCellValue(activity.getStartDate());
                cell = row.createCell(4);
                cell.setCellValue(activity.getEndDate());
                cell = row.createCell(5);
                cell.setCellValue(activity.getCost());
                cell = row.createCell(6);
                cell.setCellValue(activity.getDescription());
                cell = row.createCell(7);
                cell.setCellValue(activity.getCreateTime());
                cell = row.createCell(8);
                cell.setCellValue(activity.getCreateBy());
                cell = row.createCell(9);
                cell.setCellValue(activity.getEditTime());
                cell = row.createCell(10);
                cell.setCellValue(activity.getEditBy());
            }
        }

        //根据workbook对象生成excel文件
       /* OutputStream os = new FileOutputStream("E:\\IdeaWorkspace\\crm-project\\doc\\activityList.xls");
        wb.write(os);*/

        //关闭资源
       /* os.close();
        wb.close();*/

        //把生成的excel文件下载到客户端
        //1.设置响应类型
        response.setContentType("application/octet-stream;charset=UTF-8");
        //2.获取输出|入流
        OutputStream out = response.getOutputStream();//输出流
        /*InputStream is = new FileInputStream("E:\\IdeaWorkspace\\crm-project\\doc\\activityList.xls");//输入流*/

        //3.可以设置响应头信息，使浏览器接受响应信息只够，直接激活文件下载窗口，即使能打开也不打开
        response.addHeader("Content-Disposition","attachment;filename=activityList.xls");

      /*  byte[] buff = new byte[256];//缓存区
        int length = 0;
        while((length = is.read(buff)) != -1){
            out.write(buff,0,length);
        }*/

        //关闭流
       /* is.close();*/
        wb.write(out);
        wb.close();
        out.flush();//Tomcat自己关
    }


    @RequestMapping("/workbench/activity/downloadActivityByIds.do")
    public void downloadActivityByIds(HttpServletResponse response,HttpServletRequest request) throws IOException {
        String ids = request.getParameter("ids");
        String[] split = ids.split(",");

        List<Activity> activityList = activityService.queryActivityByIds(split);

        //创建excel文件，并且把activityList写入excel文件中
        HSSFWorkbook hwb = new HSSFWorkbook();
        HSSFSheet sheet = hwb.createSheet("市场活动");
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("ID");
        cell = row.createCell(1);
        cell.setCellValue("所有者");
        cell = row.createCell(2);
        cell.setCellValue("名称");
        cell = row.createCell(3);
        cell.setCellValue("开始时间");
        cell = row.createCell(4);
        cell.setCellValue("结束时间");
        cell = row.createCell(5);
        cell.setCellValue("成本");
        cell = row.createCell(6);
        cell.setCellValue("描述");
        cell = row.createCell(7);
        cell.setCellValue("创建时间");
        cell = row.createCell(8);
        cell.setCellValue("创建者");
        cell = row.createCell(9);
        cell.setCellValue("修改时间");
        cell = row.createCell(10);
        cell.setCellValue("修改者");

        //遍历市场活动activityList，创建HSSFRow对象,生成所有的数据行
        Activity activity = null;
        //activityList可能里面没有元素或者根本不存在
        if (activityList != null && activityList.size() > 0) {
            for (int i = 0; i < activityList.size(); i++) {
                activity = activityList.get(i);

                //每遍历一个activity，生成一行
                row = sheet.createRow(i+1);
                //每一行都要创建11列，每一列的数据都从activity获取
                cell = row.createCell(0);
                cell.setCellValue(activity.getId());
                cell = row.createCell(1);
                cell.setCellValue(activity.getOwner());
                cell = row.createCell(2);
                cell.setCellValue(activity.getName());
                cell = row.createCell(3);
                cell.setCellValue(activity.getStartDate());
                cell = row.createCell(4);
                cell.setCellValue(activity.getEndDate());
                cell = row.createCell(5);
                cell.setCellValue(activity.getCost());
                cell = row.createCell(6);
                cell.setCellValue(activity.getDescription());
                cell = row.createCell(7);
                cell.setCellValue(activity.getCreateTime());
                cell = row.createCell(8);
                cell.setCellValue(activity.getCreateBy());
                cell = row.createCell(9);
                cell.setCellValue(activity.getEditTime());
                cell = row.createCell(10);
                cell.setCellValue(activity.getEditBy());
            }
        }


        //1.设置响应类型
        response.setContentType("application/octet-stream;charset=UTF-8");
        //2.设置输出流
        OutputStream outputStream = response.getOutputStream();
        //3.设置响应头
        response.addHeader("Content-Disposition","attachment;filename=SomeOfActivity.xls");
        //4.输出到浏览器
        hwb.write(outputStream);
        //5.关闭流
        hwb.close();
        outputStream.flush();
    }


    /**
     * 本个项目重点查看，有很多小知识点，不注意到就惨了===上传文件
     * @param activityFile
     * @return
     */
    @RequestMapping("/workbench/activity/ImportActivity.do")
    @ResponseBody//返回json字符串
    public Object ImportActivity(MultipartFile activityFile,HttpSession session){
        User user = (User)session.getAttribute(Constants.SESSION_USER);
        ReturnObject returnObject = new ReturnObject();
        try {
            /**MultipartFile接受excel上传的文件，封装这个对象需要SpringMVC的文件上传解析器*/
            //把接收到的excel文件写到磁盘目录上
            /**这里的文件地址目录拼接有两种方式*/
            /*String originalFilename = activityFile.getOriginalFilename();//生成上传的文件完整名
            File file = new File("E:\\IdeaWorkspace\\crm-project\\doc\\" + originalFilename);//第一种拼接地址的方式
            File file1 = new File("E:\\IdeaWorkspace\\crm-project\\doc\\" , originalFilename);//第二种拼接地址的方式
            activityFile.transferTo(file);//用try  catch处理可能出现的异常*/

            //解析excel文件，获取文件中的数据，并且封装成activityList对象
            //FileInputStream fis = new FileInputStream("E:\\IdeaWorkspace\\crm-project\\doc\\" + originalFilename);

            InputStream is = activityFile.getInputStream();
            HSSFWorkbook wb = new HSSFWorkbook(is);
            HSSFSheet sheet = wb.getSheetAt(0);

            HSSFRow row = null;
            HSSFCell cell = null;
            Activity activity = null;
            List<Activity> activityList = new ArrayList<>();
            for (int i = 1;i <= sheet.getLastRowNum();i++) {
                row = sheet.getRow(i);
                activity = new Activity();
                /**文件的id不能由用户随机设定,即在于用户约定好的模板中根本就没有id这一列*/
                activity.setId(UUID.randomUUID().toString().replaceAll("-",""));
                /**所有者设置为当前用户，模板文档将没有这个选项*/
                activity.setOwner(user.getId());
                activity.setCreateTime(DateUtils.formatDateTime(new Date()));
                activity.setCreateBy(user.getId());

                for (int j = 0;j < row.getLastCellNum(); j++) {
                    cell = row.getCell(j);
                    String cellValue = HSSFUtils.getCellValueForStr(cell);
                    //文件的格式是提前约定好的
                    if (j == 0) {
                        /**①文件的id不能由用户随机设定,即在于用户约定好的模板中根本就没有id这一列*/
                        /**②用户在模板文件内填入的owner选项是名称（如张三），但是在数据库中是id格式（如123456）*/
                        /**总结遇到的问题：用户只能添加id，后台需要id
                          *解决办法①：根据用户名到数据库里面去查id      【结论】：办法不行，因为多个用户的用户名可能相同，同一个用户名可能查出多个id
                          *解决办法②：如果用户数据量较小，可以给用户做一个附录，将每个用户名对应的id名提供给用户，由用户输入id
                          *解决办法③：创建一个公共的账号，所有的导入都由这个账号负责，后续由领导分配
                          *解决方法④：那个上传文档，由那个负责
                         */
                        activity.setName(cellValue);
                    }else if( j == 1){
                        activity.setStartDate(cellValue);
                    }else if(j == 2){
                        activity.setEndDate(cellValue);
                    }else if(j == 3){
                        activity.setCost(cellValue);
                    }else if(j == 4){
                        activity.setDescription(cellValue);
                    }
                }
                //每一行的所有列都封装完成之后，把activity保存到list之中
                activityList.add(activity);
            }
            //调用service方法，保存市场活动
            int result = activityService.saveCreateActivityByList(activityList);
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_SUCCESS);
            returnObject.setRetData(result);
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后重试...");
        }
        return returnObject;
    }

    @RequestMapping("/workbench/activity/detailActivity.do")
    public String detailActivity(String id,HttpServletRequest request) {
        //调用service层方法查询数据
        Activity activity = activityService.queryActivityForDetailById(id);
        List<ActivityRemark> remarkList = activityRemarkService.queryActivityRemarkForDetailByActivityId(id);

        //把数据保存到作用域中
        request.setAttribute("activity",activity);
        request.setAttribute("remarkList",remarkList);

        //请求转发
        return "workbench/activity/detail";
    }



}
