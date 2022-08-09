package com.bjpowernode.crm.workbench.web.controller;


import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServiceFactory;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.Contacts;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.bjpowernode.crm.workbench.service.TransactionService;
import com.bjpowernode.crm.workbench.service.impl.ActivityServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.TransactionServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


public class TransactionController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("进入到交易控制器");
        String path = request.getServletPath();

        if ("/workbench/transaction/add.do".equals(path)){
            add(request,response);
        }else if("/workbench/transaction/getActivityList.do".equals(path)){
            getActivityList(request,response);
        }else if("/workbench/transaction/getContactsListByName.do".equals(path)){
            getContactsListByName(request,response);
        }

    }

    private void getContactsListByName(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("获取联系人列表");
        String name = request.getParameter("name");
        TransactionService ts = (TransactionService) ServiceFactory.getService(new TransactionServiceImpl());
        List<Contacts> list = ts.getContactsListByName(name);
        PrintJson.printJsonObj(response,list);
    }

    private void getActivityList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("获取市场活动源");
        String name = request.getParameter("name");
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> list = as.getActivityListByConvert(name);
        PrintJson.printJsonObj(response,list);
    }

    private void add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("打开添加交易功能");
        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> uList = us.getUserList();
        request.setAttribute("uList",uList);
        request.getRequestDispatcher("/workbench/transaction/save.jsp").forward(request,response);
    }


}
