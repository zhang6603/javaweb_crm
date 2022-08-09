package com.bjpowernode.crm.workbench.dao;

import com.bjpowernode.crm.workbench.domain.Customer;

public interface CustomerDao {

    Customer getByName(String company);

    int save(Customer customer);
}
