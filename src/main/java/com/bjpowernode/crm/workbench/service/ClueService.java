package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.domain.Tran;

import java.util.List;
import java.util.Map;

public interface ClueService {

    List<User> getUserList();

    boolean sava(Clue clue);

    PaginationVO<Clue> pageList(Map<String, Object> map1);

    Clue detail(String id);

    boolean unbund(String id);

    boolean bund(String cid, String[] aid);

    boolean convert(String clueId, Tran t, String createBy);
}
