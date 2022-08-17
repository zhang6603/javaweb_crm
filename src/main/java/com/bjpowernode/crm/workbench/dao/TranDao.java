package com.bjpowernode.crm.workbench.dao;

import com.bjpowernode.crm.workbench.domain.Tran;
import org.apache.ibatis.transaction.Transaction;

import java.util.List;
import java.util.Map;

public interface TranDao {

    int save(Tran t);

    int getTotalByCondition(Map<String, Object> map);

    List<Transaction> getTransactionByCondition(Map<String, Object> map);

    Tran getById(String id);

    int changeStage(Tran t);

    int getTotal();

    List<Map<String, String>> getDataList();
}
