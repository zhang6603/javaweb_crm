package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.dao.ContactsDao;
import com.bjpowernode.crm.workbench.dao.CustomerDao;
import com.bjpowernode.crm.workbench.dao.TranDao;
import com.bjpowernode.crm.workbench.dao.TranHistoryDao;
import com.bjpowernode.crm.workbench.domain.Contacts;
import com.bjpowernode.crm.workbench.domain.Customer;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.domain.TranHistory;
import com.bjpowernode.crm.workbench.service.TransactionService;
import org.apache.ibatis.transaction.Transaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionServiceImpl implements TransactionService {
    private ContactsDao contactsDao = SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);
    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);
    public List<Contacts> getContactsListByName(String name) {
        List<Contacts> list = contactsDao.getContactsListByName(name);
        return list;
    }

    public boolean save(String customerName, Tran t) {
        //查找是否存在该客户，如果存在则直接添加，若不存在，则创建客户再创建交易
        boolean flag = true;
        Customer customer = customerDao.getByName(customerName);
        if (customer == null){
            customer = new Customer();
            customer.setId(UUIDUtil.getUUID());
            customer.setName(customerName);
            customer.setOwner(t.getOwner());
            customer.setCreateBy(t.getCreateBy());
            customer.setCreateTime(t.getCreateTime());
            int count = customerDao.save(customer);
            if (count != 1){
                flag = false;
            }
        }
        t.setCustomerId(customer.getId());
        int count1 = tranDao.save(t);
        if (count1 != 1){
            flag = false;
        }
        //创建一条交易历史
        TranHistory tranHistory = new TranHistory();
        tranHistory.setId(UUIDUtil.getUUID());
        tranHistory.setCreateBy(t.getCreateBy());
        tranHistory.setCreateTime(t.getCreateTime());
        tranHistory.setExpectedDate(t.getExpectedDate());
        tranHistory.setMoney(t.getMoney());
        tranHistory.setTranId(t.getId());
        tranHistory.setStage(t.getStage());
        int count2 = tranHistoryDao.save(tranHistory);
        if (count2 != 1){
            flag = false;
        }
        return flag;
    }

    public PaginationVO<Transaction> pageList(Map<String, Object> map) {
        //查出符合要求总条数total
        int total = tranDao.getTotalByCondition(map);
        //结合条件查询符合要求的交易信息
        List<Transaction> tList = tranDao.getTransactionByCondition(map);
        //封装成vo返回
        PaginationVO<Transaction> vo = new PaginationVO<Transaction>();
        vo.setTotal(total);
        vo.setDataList(tList);
        return vo;
    }

    public Tran detail(String id) {
        Tran t = tranDao.getById(id);
        return t;
    }

    public List<TranHistory> getTranHistoryList(String id) {
        List<TranHistory> list = tranHistoryDao.getById(id);
        return list;
    }

    public boolean changeStage(Tran t) {
        boolean flag = true;
        //修改交易表中内容
        int count = tranDao.changeStage(t);
        if (count != 1){
            flag = false;
        }
        //交易历史新增一条记录
        TranHistory th = new TranHistory();
        th.setId(UUIDUtil.getUUID());
        th.setMoney(t.getMoney());
        th.setStage(t.getStage());
        th.setTranId(t.getId());
        th.setExpectedDate(t.getExpectedDate());
        th.setCreateTime(t.getEditTime());
        th.setCreateBy(t.getEditBy());
        int count2 = tranHistoryDao.save(th);
        if (count2 != 1){
            flag = false;
        }
        return flag;
    }

    public Map<String, Object> getCharts() {
        //获取total
        int total = tranDao.getTotal();
        //获取dataList
        List<Map<String,String>> dataList = tranDao.getDataList();
        //封装到map中
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("total",total);
        map.put("dataList",dataList);
        return map;
    }
}
