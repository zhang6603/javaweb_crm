package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.Contacts;

import java.util.List;

public interface TransactionService {
    List<Contacts> getContactsListByName(String name);
}
