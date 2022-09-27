package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.commons.constants.Constants;
import com.bjpowernode.crm.commons.utils.DateUtils;
import com.bjpowernode.crm.commons.utils.UUIDUtils;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.workbench.domain.*;
import com.bjpowernode.crm.workbench.mapper.*;
import com.bjpowernode.crm.workbench.service.ClueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ClueServiceImpl implements ClueService {

    @Autowired
    private ClueMapper clueMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private ContactsMapper contactsMapper;

    @Autowired
    private ClueRemarkMapper clueRemarkMapper;

    @Autowired
    private CustomerRemarkMapper customerRemarkMapper;

    @Autowired
    private ContactsRemarkMapper contactsRemarkMapper;

    @Autowired
    private ClueActivityRelationMapper clueActivityRelationMapper;

    @Autowired
    private ContactsActivityRelationMapper contactsActivityRelationMapper;

    @Autowired
    private TranMapper tranMapper;

    @Autowired
    private TranRemarkMapper tranRemarkMapper;

    @Override
    public int createClue(Clue clue) {
        return clueMapper.insertClue(clue);
    }

    @Override
    public List<Clue> queryClueByCondition(Map<String,Object> map) {
        return clueMapper.selectClueByCondition(map);
    }

    @Override
    public int queryCountsOfClueByConditions(Map<String, Object> map) {
        return clueMapper.selectCountOfClueByConditions(map);
    }

    @Override
    public int editClueByCondition(Clue clue) {
        return clueMapper.updateClueByCondition(clue);
    }

    @Override
    public Clue queryDetailOfOneClueByCondition(String id) {
        return clueMapper.selectDetailOfOneClueByCondition(id);
    }

    @Override
    public int removeClueById(String[] id) {
        return clueMapper.deleteClueById(id);
    }

    @Override
    public Clue queryClueByPrimaryKey(String id) {
        return clueMapper.selectByPrimaryKey(id);
    }

    @Override
    public void saveConvertClue(Map<String, Object> map) {
        String clueId = (String)map.get("clueId");
        User user = (User) map.get(Constants.SESSION_USER);
        //根据id查询线索的信息
        Clue clue = clueMapper.selectClueById(clueId);
        //1.把该线索中有关公司的额信息转换到客户表中
        Customer customer = new Customer();
        customer.setAddress(clue.getAddress());
        customer.setContactSummary(clue.getContactSummary());
        customer.setCreateBy(user.getId());
        customer.setCreateTime(DateUtils.formatDateTime(new Date()));
        customer.setDescription(clue.getDescription());
        customer.setId(clue.getId());
        customer.setName(clue.getCompany());
        customer.setNextContactTime(clue.getNextContactTime());
        customer.setOwner(clue.getId());//不一定是初级用户,谁创建谁是owner
        customer.setPhone(clue.getPhone());//不一定是初级用户,谁创建谁是owner
        customer.setWebsite(clue.getWebsite());//不一定是初级用户,谁创建谁是owner
        customerMapper.insertCustomer(customer);

        //2.创建联系人实体类
        Contacts contacts = new Contacts();
        contacts.setAddress(clue.getAddress());
        contacts.setAppellation(clue.getAppellation());
        contacts.setContactSummary(clue.getContactSummary());
        contacts.setCreateBy(clue.getCreateBy());
        contacts.setCreateTime(DateUtils.formatDateTime(new Date()));
        contacts.setAddress(clue.getAddress());
        contacts.setCustomerId(customer.getId());
        contacts.setDescription(clue.getDescription());
        contacts.setEmail(clue.getEmail());
        contacts.setFullname(clue.getFullname());
        contacts.setId(UUIDUtils.getUUID());
        contacts.setJob(clue.getJob());
        contacts.setMphone(clue.getMphone());
        contacts.setNextContactTime(clue.getNextContactTime());
        contacts.setSource(clue.getSource());
        contacts.setOwner(user.getId());
        contactsMapper.insertContacts(contacts);

        //3.4.根据clueId查询该线索下所有备注
        List<ClueRemark> clueRemarkList = clueRemarkMapper.selectClueRemarkById(clueId);
        //将线索下的备注转为客户备注表里面,将线索下的备注转为联系人备注表里面
        //判断list是否为空
        if (clueRemarkList != null && clueRemarkList.size() > 0) {
            CustomerRemark customerRemark = null;
            List<CustomerRemark> curList = new ArrayList<>();
            List<ContactsRemark> corList = new ArrayList<>();
            ContactsRemark contactsRemark = null;
            for(ClueRemark cr : clueRemarkList){
                customerRemark = new CustomerRemark();
                customerRemark.setCreateBy(cr.getCreateBy());
                customerRemark.setCreateTime(cr.getCreateTime());
                customerRemark.setCustomerId(cr.getId());
                customerRemark.setEditBy(cr.getEditBy());
                customerRemark.setEditFlag(cr.getEditFlag());
                customerRemark.setEditTime(cr.getEditTime());
                customerRemark.setId(UUIDUtils.getUUID());
                customerRemark.setNoteContent(cr.getNoteContent());
                curList.add(customerRemark);

                contactsRemark = new ContactsRemark();
                contactsRemark.setContactsId(contacts.getId());
                contactsRemark.setCreateBy(cr.getCreateBy());
                contactsRemark.setCreateTime(cr.getCreateTime());
                contactsRemark.setEditBy(cr.getEditBy());
                contactsRemark.setEditFlag(cr.getEditFlag());
                contactsRemark.setEditTime(cr.getEditTime());
                contactsRemark.setId(UUIDUtils.getUUID());
                contactsRemark.setNoteContent(cr.getNoteContent());
                corList.add(contactsRemark);

            }
            customerRemarkMapper.insertCustomerRemarkByList(curList);
            contactsRemarkMapper.insertContactsRemarkByList(corList);
        }


        //根据clueId查询线索和市场活动的关联关系
        List<ClueActivityRelation> clueActivityRelationList = clueActivityRelationMapper.selectClueActivityRelationByClueId(clueId);

        //把该线索和市场活动的关联关系转换为联系人和市场活动的关联关系
        if (clueActivityRelationList != null && clueActivityRelationList.size() > 0) {
            ContactsActivityRelation contactsActivityRelation = null;
            List<ContactsActivityRelation> carList = new ArrayList<>();
            for(ClueActivityRelation car : clueActivityRelationList){
                contactsActivityRelation = new ContactsActivityRelation();
                contactsActivityRelation.setActivityId(car.getActivityId());
                contactsActivityRelation.setContactsId(contacts.getId());
                contactsActivityRelation.setId(UUIDUtils.getUUID());
                carList.add(contactsActivityRelation);
            }
            contactsActivityRelationMapper.insertContactActivityRelationByList(carList);
        }


        //如果需要创建交易，往交易表添加数据
        String isCreateTran = (String) map.get("isCreateTran");
        Tran tran = null;
        if (isCreateTran.equals("true")) {
            tran = new Tran();
            tran.setActivityId((String)map.get("activityId"));
            tran.setExpectedDate((String)map.get("expectedDate"));
            tran.setMoney((String)map.get("money"));
            tran.setName((String)map.get("name"));
            tran.setStage((String)map.get("stage"));
            tran.setContactsId(contacts.getId());
            tran.setCreateBy(user.getId());
            tran.setOwner(user.getId());
            tran.setCustomerId(customer.getId());
            tran.setCreateTime(DateUtils.formatDateTime(new Date()));
            tran.setId(UUIDUtils.getUUID());
            tranMapper.insertTran(tran);

            //把线索下的备注转到交易表的备注
            if (clueRemarkList != null && clueRemarkList.size() > 0){
                TranRemark tranRemark = null;
                List<TranRemark> trList = new ArrayList<>();
                for(ClueRemark cr:clueRemarkList){
                    tranRemark = new TranRemark();
                    tranRemark.setCreateBy(cr.getCreateBy());
                    tranRemark.setCreateTime(cr.getCreateTime());
                    tranRemark.setEditBy(cr.getEditBy());
                    tranRemark.setEditFlag(cr.getEditFlag());
                    tranRemark.setEditTime(cr.getEditTime());
                    tranRemark.setId(UUIDUtils.getUUID());
                    tranRemark.setNoteContent(cr.getNoteContent());
                    tranRemark.setTranId(tran.getId());
                    trList.add(tranRemark);
                }
                tranRemarkMapper.insertTranRemarkByList(trList);
            }
        }

        //删除线索备注表
        clueRemarkMapper.deleteClueRemarkClueId(clueId);
        //删除线索和市场活动的关联关系
        clueActivityRelationMapper.deleteClueActivityRelationByClueId(clueId);
        //删除该线索
        clueMapper.deleteOneClueById(clueId);

    }
}
