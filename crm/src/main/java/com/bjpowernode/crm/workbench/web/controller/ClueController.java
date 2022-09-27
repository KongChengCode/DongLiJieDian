package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.commons.constants.Constants;
import com.bjpowernode.crm.commons.domain.ReturnObject;
import com.bjpowernode.crm.commons.utils.DateUtils;
import com.bjpowernode.crm.commons.utils.UUIDUtils;
import com.bjpowernode.crm.settings.domain.DicValue;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.DicValueService;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.domain.ClueActivityRelation;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.bjpowernode.crm.workbench.service.ClueActivityRelationService;
import com.bjpowernode.crm.workbench.service.ClueService;
import org.apache.commons.collections4.queue.PredicatedQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
public class ClueController {

    @Autowired
    private ClueService clueService;

    @Autowired
    private UserService userService;

    @Autowired
    private DicValueService dicValueService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ClueActivityRelationService clueActivityRelationService;

    /**
     * 跳转到线索主页面
     * @return
     */
    @RequestMapping("/workbench/clue/index.do")
    public String index(HttpServletRequest request){
        List<User> userList = userService.queryAllUsers();
        request.setAttribute(Constants.USERlIST,userList);
        List<DicValue> appellationList = dicValueService.queryDicValueByTypeCode("appellation");
        List<DicValue> sourceList = dicValueService.queryDicValueByTypeCode("source");
        List<DicValue> clueStateList = dicValueService.queryDicValueByTypeCode("clueState");
        request.setAttribute("appellationList",appellationList);
        request.setAttribute("sourceList",sourceList);
        request.setAttribute("clueStateList",clueStateList);
        return "workbench/clue/index";
    }

    @RequestMapping("/workbench/clue/queryClueByCondition.do")
    @ResponseBody
    public Object queryClueByCondition(String appellation, String company, String phone, String source, String owner,
                                       String mphone, String state, Integer pageNo, Integer pageSize){
        //封装参数
        Integer beginNo = (pageNo - 1) * pageSize;
        Map<String,Object> map = new HashMap<>();
        map.put("appellation",appellation);
        map.put("company",company);
        map.put("phone",phone);
        map.put("source",source);
        map.put("owner",owner);
        map.put("mphone",mphone);
        map.put("state",state);
        map.put("beginNo",beginNo);
        map.put("pageSize",pageSize);

        ReturnObject returnObject = new ReturnObject();
        int totalRows = 0;
        Map<String,Object> mp = new HashMap<>();

        try {
            //使用service层对象执行sql语句
            List<Clue> clueList = clueService.queryClueByCondition(map);
            totalRows = clueService.queryCountsOfClueByConditions(map);
            mp.put("clueList",clueList);
            mp.put("totalRows",totalRows);
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_SUCCESS);
            returnObject.setRetData(mp);
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后...");
        }
        return returnObject;

    }

    @RequestMapping("/workbench/clue/createClue.do")
    @ResponseBody
    public Object createClue(Clue clue){

        ReturnObject returnObject = new ReturnObject();
        //收集参数
        clue.setId(UUIDUtils.getUUID());
        clue.setCreateTime(DateUtils.formatDateTime(new Date()));

        try {
            //使用service层对象执行sql语句
            int result = clueService.createClue(clue);
            if (result > 0) {
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_SUCCESS);
            } else {
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("系统忙，请稍后...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后...");
        }
        return  returnObject;
    }

    @RequestMapping("/workbench/clue/findDetailOfClueByCondition.do")
    @ResponseBody
    public Object findDetailOfClueByCondition(String id){
        Clue clue = clueService.queryDetailOfOneClueByCondition(id);
        return clue;
    }

    @RequestMapping("/workbench/clue/saveClueByCondition.do")
    @ResponseBody
    public Object saveClueByCondition(Clue clue,HttpSession session){
        //收集参数
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        clue.setEditBy(user.getId());
        clue.setEditTime(DateUtils.formatDateTime(new Date()));

        ReturnObject returnObject = new ReturnObject();

        try {
            //使用service对象
            int result = clueService.editClueByCondition(clue);
            if (result > 0) {
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_SUCCESS);
            } else {
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("系统忙，请稍后...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后...");
        }

        return returnObject;
    }

    @RequestMapping("/workbench/clue/removeClueById.do")
    @ResponseBody
    public Object removeClueById(String[] id){
        ReturnObject returnObject = new ReturnObject();
        try {
            int result = clueService.removeClueById(id);
            if (result > 0) {
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_SUCCESS);
            } else {
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("系统忙，请稍后...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后...");
        }
        return returnObject;
    }

    @RequestMapping("/workbench/clue/toClueDetail.do")
    public String toClueDetail(String id,HttpServletRequest request){
        Clue clue = clueService.queryClueByPrimaryKey(id);
        List<Activity> activities = activityService.queryActivityForClueId(id);
        request.setAttribute("activities",activities);
        request.setAttribute("clue",clue);
        return "workbench/clue/detail";
    }

    @RequestMapping("/workbench/clue/queryActivityForDetailByNameClueId.do")
    @ResponseBody
    public Object queryActivityForDetailByNameClueId(String activityName,String clueId){
        //封装参数
        Map<String,Object> map = new HashMap<>();
        map.put("activityName",activityName);
        map.put("clueId",clueId);

        //调用service方法查询市场活动
        List<Activity> activityList = activityService.queryActivityForDetailByNameClueId(map);
        return activityList;
    }

    @RequestMapping("/workbench/clue/saveBund.do")
    @ResponseBody
    public Object saveBund(String[] activityId,String clueId){
        ClueActivityRelation car = null;
        List<ClueActivityRelation> relationList = new ArrayList<>();
        for (String a : activityId){
           car = new ClueActivityRelation();
           car.setActivityId(a);
           car.setClueId(clueId);
           car.setId(UUIDUtils.getUUID());
           relationList.add(car);
        }

        ReturnObject returnObject = new ReturnObject();
        try {
            //调用service方法，保存线索和市场活动关联关系
            int result = clueActivityRelationService.saveCreateClueActivityRelationByList(relationList);
            if (result > 0) {
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_SUCCESS);

                List<Activity> activityList = activityService.queryActivityForDetailByIds(activityId);
                returnObject.setRetData(activityList);
            } else {
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMessage("系统忙，请稍后...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后...");
        }
        return  returnObject;
    }

    @RequestMapping("/workbench/clue/saveUnbund.do")
    @ResponseBody
    public Object saveUnbund(ClueActivityRelation relation){
        ReturnObject returnObject = new ReturnObject();
        try {
            //调用service层方法，删除线索和市场活动的关联关系
            int result = clueActivityRelationService.deleteClueActivityRelationByClueIdActivityId(relation);
            if (result > 0) {
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_SUCCESS);
            } else {
                returnObject.setMessage("系统忙，请稍后...");
                returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setMessage("系统忙，请稍后...");
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
        }
        return returnObject;
    }

    @RequestMapping("/workbench/clue/toConvert.do")
    public String toConvert(String id,HttpServletRequest request){
        //调用service方法查询线索的明细信息
        Clue clue = clueService.queryDetailOfOneClueByCondition(id);
        List<DicValue> stageList = dicValueService.queryDicValueByTypeCode("stage");
        //把数据保存到request中
        request.setAttribute("clue",clue);
        request.setAttribute("stageList",stageList);
        //请求转发
        return "workbench/clue/convert";
    }

    @RequestMapping("/workbench/clue/queryActivityForConvertByNameClueId.do")
    @ResponseBody
    public Object queryActivityForConvertByNameClueId(String activityName,String clueId){
        //封装参数
        Map<String,Object> map = new HashMap<>();
        map.put("activityName",activityName);
        map.put("clueId",clueId);

        //调用service层方法查询市场活动
        List<Activity> activityList = activityService.queryActivityByForConvertByNameClueId(map);

        //根据查询结果，返回响应信息
        return activityList;
    }

    @RequestMapping("/workbench/clue/convertClue.do")
    @ResponseBody
    public Object convertClue(String clueId,String money,String name,String expectedDate,String stage,String activityId,
                              String isCreateTran,HttpSession session){
        //封装参数
        Map<String,Object> map = new HashMap<>();
        map.put("clueId",clueId);
        map.put("name",name);
        map.put("money",money);
        map.put("expectedDate",expectedDate);
        map.put("stage",stage);
        map.put("activityId",activityId);
        map.put("isCreateTran",isCreateTran);
        map.put(Constants.SESSION_USER,session.getAttribute(Constants.SESSION_USER));

        ReturnObject returnObject = new ReturnObject();
        try {
            //调用service层方法
            clueService.saveConvertClue(map);
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Constants.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMessage("系统忙，请稍后...");
        }
        return returnObject;
    }

}
