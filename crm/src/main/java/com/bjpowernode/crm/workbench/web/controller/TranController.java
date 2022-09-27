package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.commons.constants.Constants;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.settings.domain.DicValue;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.DicValueService;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.domain.TranHistory;
import com.bjpowernode.crm.workbench.domain.TranRemark;
import com.bjpowernode.crm.workbench.service.CustomerService;
import com.bjpowernode.crm.workbench.service.TranHistoryService;
import com.bjpowernode.crm.workbench.service.TranRemarkService;
import com.bjpowernode.crm.workbench.service.TranService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@Controller
public class TranController {

    @Autowired
    private DicValueService dicValueService;

    @Autowired
    private UserService userService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private TranService tranService;

    @Autowired
    private TranRemarkService tranRemarkService;

    @Autowired
    private TranHistoryService tranHistoryService;

    @RequestMapping("/workbench/transaction/index.do")
    public String index(HttpServletRequest request){
        //调用service层
        List<DicValue> transactionTypeList = dicValueService.queryDicValueByTypeCode("transactionType");
        List<DicValue> sourceList = dicValueService.queryDicValueByTypeCode("source");
        List<DicValue> stageList = dicValueService.queryDicValueByTypeCode("stage");
        //把数据放入request
        request.setAttribute("transactionTypeList",transactionTypeList);
        request.setAttribute("sourceList",sourceList);
        request.setAttribute("stageList",stageList);
        //请求转发
        return "workbench/transaction/index";
    }

    @RequestMapping("/workbench/transaction/toSave.do")
    public String toSave(HttpServletRequest request){
        //调用service层方法查询动态数据
        List<User> userList = userService.queryAllUsers();
        List<DicValue> transactionTypeList = dicValueService.queryDicValueByTypeCode("transactionType");
        List<DicValue> sourceList = dicValueService.queryDicValueByTypeCode("source");
        List<DicValue> stageList = dicValueService.queryDicValueByTypeCode("stage");
        //保存数据的作用域
        request.setAttribute("transactionTypeList",transactionTypeList);
        request.setAttribute("sourceList",sourceList);
        request.setAttribute("stageList",stageList);
        request.setAttribute("userList",userList);
        System.out.println("=================================================");
        return "workbench/transaction/save1";
    }

    @RequestMapping("/workbench/transaction/getPossibleByStage.do")
    @ResponseBody
    public Object getPossibleByStage(String stageValue){
        //解析properties文件，获取阶段可能性
        ResourceBundle bundle = ResourceBundle.getBundle("possible");
        String possibility = bundle.getString(stageValue);
        return possibility;
    }

    @RequestMapping("/workbench/transaction/queryCustomerName.do")
    @ResponseBody
    //例子，只是为了实验自动补全插件的用法
    public Object queryAllCustomerName(String name){
        List<String> nameList = customerService.queryAllCustomerName(name);
        return nameList;
    }

    @RequestMapping("/workbench/transaction/saveCreateTran.do")
    @ResponseBody
    public Object saveCreateTran(@RequestParam Map<String,Object> map, HttpSession session){
        //封装参数
        map.put(Constants.SESSION_USER,session.getAttribute(Constants.SESSION_USER));
        System.out.println("===================================================================================");
        System.out.println("owner" + map.get("owner"));
        ReturnObject returnObject = new ReturnObject();
        try {
            //调用service方法
            tranService.saveCreateTran(map);
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后...");

        }
        return returnObject;
    }

    @RequestMapping("/workbench/transaction/detailTran.do")
    public String detailTran(String id,HttpServletRequest request){
        System.out.println("=============================================");
        //调用service层方法，查询数据
        Tran tran=tranService.queryTranForDetailById(id);
        List<TranRemark> remarkList=tranRemarkService.queryTranRemarkForDetailByTranId(id);
        List<TranHistory> historyList=tranHistoryService.queryTranHistoryForDetailByTranId(id);

        System.out.println("===================================================tran="+tran+"\nremarkList="+remarkList+"\nhistoryList="+historyList);
        //根据tran所处阶段名称查询可能性
        ResourceBundle bundle=ResourceBundle.getBundle("possible");
        String possibility=bundle.getString(tran.getStage());
        tran.setPossibility(possibility);

        //把数据保存到request中
        request.setAttribute("tran",tran);
        request.setAttribute("remarkList",remarkList);
        request.setAttribute("historyList",historyList);
        //request.setAttribute("possibility",possibility);

        //调用service方法，查询交易所有的阶段
        List<DicValue> stageList=dicValueService.queryDicValueByTypeCode("stage");
        request.setAttribute("stageList",stageList);

        //请求转发
        return "workbench/transaction/detail";
    }
}
