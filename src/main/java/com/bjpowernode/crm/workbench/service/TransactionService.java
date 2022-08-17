package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.domain.Contacts;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.domain.TranHistory;
import org.apache.ibatis.transaction.Transaction;

import java.util.List;
import java.util.Map;

public interface TransactionService {
    List<Contacts> getContactsListByName(String name);

    boolean save(String customerName, Tran t);

    PaginationVO<Transaction> pageList(Map<String, Object> map);

    Tran detail(String id);

    List<TranHistory> getTranHistoryList(String id);

    boolean changeStage(Tran t);

    Map<String, Object> getCharts();

}
