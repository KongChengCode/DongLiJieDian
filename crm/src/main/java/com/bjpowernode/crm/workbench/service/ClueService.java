package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.workbench.domain.Clue;

import java.util.List;
import java.util.Map;

public interface ClueService {
    int createClue(Clue clue);

    List<Clue> queryClueByCondition(Map<String,Object> map);

    int queryCountsOfClueByConditions(Map<String,Object> map);

    int editClueByCondition(Clue clue);

    Clue queryDetailOfOneClueByCondition(String id);

    int removeClueById(String[] id) ;

    Clue queryClueByPrimaryKey(String id);

    void saveConvertClue(Map<String,Object> map);
}
