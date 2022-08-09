package com.bjpowernode.crm.workbench.dao;

import com.bjpowernode.crm.workbench.domain.Clue;

import java.util.List;
import java.util.Map;

public interface ClueDao {


    int save(Clue clue);

    List<Clue> getClueByCondition(Map<String, Object> map1);

    int getTotalByCondition(Map<String, Object> map1);

    Clue detail(String id);

    Clue getById(String clueId);

    int delete(String clueId);
}
