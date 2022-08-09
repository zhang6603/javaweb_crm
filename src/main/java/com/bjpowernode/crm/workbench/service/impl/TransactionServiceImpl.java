package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.workbench.dao.ContactsDao;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.Contacts;
import com.bjpowernode.crm.workbench.service.TransactionService;

import java.util.List;

public class TransactionServiceImpl implements TransactionService {
    private ContactsDao contactsDao = SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);

    public List<Contacts> getContactsListByName(String name) {
        List<Contacts> list = contactsDao.getContactsListByName(name);
        return list;
    }
}
