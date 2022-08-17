package com.bjpowernode.crm.workbench.web.controller;


import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServiceFactory;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.Contacts;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.domain.TranHistory;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.bjpowernode.crm.workbench.service.CustomerService;
import com.bjpowernode.crm.workbench.service.TransactionService;
import com.bjpowernode.crm.workbench.service.impl.ActivityServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.CustomerServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.TransactionServiceImpl;
import org.apache.ibatis.transaction.Transaction;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
        }else if("/workbench/transaction/getaccountName.do".equals(path)){
            getaccountName(request,response);
        }else if("/workbench/transaction/save.do".equals(path)){
            save(request,response);
        }else if("/workbench/transaction/pageList.do".equals(path)){
            pageList(request,response);
        }else if("/workbench/transaction/detail.do".equals(path)){
            detail(request,response);
        }else if("/workbench/transaction/getTranHistoryList.do".equals(path)){
            getTranHistoryList(request,response);
        }else if("/workbench/transaction/changeStage.do".equals(path)){
            changeStage(request,response);
        }else if("/workbench/transaction/getCharts.do".equals(path)){
            getCharts(request,response);
        }

    }

    private void getCharts(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("展示图表");
        TransactionService ts = (TransactionService) ServiceFactory.getService(new TransactionServiceImpl());
        Map<String,Object> map = ts.getCharts();
        PrintJson.printJsonObj(response,map);
    }

    private void changeStage(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("执行交易阶段变更");
        String id = request.getParameter("id");
        String stage = request.getParameter("stage");
        String money = request.getParameter("money");
        String expectedDate = request.getParameter("expectedDate");
        String editTime = DateTimeUtil.getSysTime();
        String editeBy = ((User)request.getSession().getAttribute("user")).getName();
        Map<String,String> map = (Map<String, String>) request.getServletContext().getAttribute("possibilityMap");
        String possibility = map.get(stage);

        Tran t = new Tran();
        t.setId(id);
        t.setStage(stage);
        t.setMoney(money);
        t.setExpectedDate(expectedDate);
        t.setEditBy(editeBy);
        t.setEditTime(editTime);
        t.setPossibility(possibility);

        TransactionService ts = (TransactionService) ServiceFactory.getService(new TransactionServiceImpl());
        boolean flag = ts.changeStage(t);
        Map<String,Object> map1 = new HashMap<String, Object>();
        map1.put("success",flag);
        map1.put("t",t);
        PrintJson.printJsonObj(response,map1);
    }

    private void getTranHistoryList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("交易历史查询");
        String id = request.getParameter("id");
        TransactionService ts = (TransactionService) ServiceFactory.getService(new TransactionServiceImpl());
        Map<String,String> pMap = (Map<String, String>) request.getServletContext().getAttribute("possibilityMap");
        List<TranHistory> list = ts.getTranHistoryList(id);
        for (TranHistory tranHistory : list){
            String stage = tranHistory.getStage();
            String possibility = pMap.get(stage);
            tranHistory.setPossibility(possibility);
        }
        PrintJson.printJsonObj(response,list);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("交易信息详情");
        String id = request.getParameter("id");
        TransactionService ts = (TransactionService) ServiceFactory.getService(new TransactionServiceImpl());
        Tran t = ts.detail(id);
        Map<String,String> pMap = (Map<String, String>) request.getServletContext().getAttribute("possibilityMap");
        String possibility = pMap.get(t.getStage());
        t.setPossibility(possibility);
        request.setAttribute("t",t);
        request.getRequestDispatcher("/workbench/transaction/detail.jsp").forward(request,response);
    }

    private void pageList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("交易列表显示");
        String name = request.getParameter("name");
        String owner = request.getParameter("owner");
        String customerName = request.getParameter("customerName");
        String stage = request.getParameter("stage");
        String transactionType = request.getParameter("transactionType");
        String source = request.getParameter("source");
        String contactsName = request.getParameter("contactsName");
        String pageNoStr = request.getParameter("pageNo");
        int pageNo = Integer.valueOf(pageNoStr);
        String pageSizeStr = request.getParameter("pageSize");
        int pageSize = Integer.valueOf(pageSizeStr);
        //计算出略过的条数
        int skipCount = (pageNo-1)*pageSize;


        Map<String,Object> map = new HashMap<String, Object>();
        map.put("name",name);
        map.put("owner",owner);
        map.put("customerName",customerName);
        map.put("stage",stage);
        map.put("transactionType",transactionType);
        map.put("source",source);
        map.put("contactsName",contactsName);
        map.put("skipCount",skipCount);
        map.put("pageSize",pageSize);

        TransactionService ts = (TransactionService) ServiceFactory.getService(new TransactionServiceImpl());
        PaginationVO<Transaction> vo = ts.pageList(map);
        PrintJson.printJsonObj(response,vo);
    }

    private void save(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("添加交易");
        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("create-owner");
        String money = request.getParameter("create-money");
        String name = request.getParameter("create-name");
        String expectedDate = request.getParameter("create-expectedDate");
        String customerName = request.getParameter("create-customerName");
        String stage = request.getParameter("create-stage");
        String type = request.getParameter("create-type");
        String source = request.getParameter("create-source");
        String activityId = request.getParameter("create-activityId");
        String contactsId = request.getParameter("create-contactsId");
        String createTime = DateTimeUtil.getSysTime();
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        String description = request.getParameter("create-description");
        String contactSummary = request.getParameter("create-contactSummary");
        String nextContactTime = request.getParameter("create-nextContactTime");

        Tran t = new Tran();
        t.setId(id);
        t.setOwner(owner);
        t.setMoney(money);
        t.setName(name);
        t.setExpectedDate(expectedDate);
        t.setStage(stage);
        t.setType(type);
        t.setSource(source);
        t.setActivityId(activityId);
        t.setContactsId(contactsId);
        t.setCreateTime(createTime);
        t.setCreateBy(createBy);
        t.setDescription(description);
        t.setContactSummary(contactSummary);
        t.setNextContactTime(nextContactTime);

        TransactionService ts = (TransactionService) ServiceFactory.getService(new TransactionServiceImpl());
        boolean flag = ts.save(customerName,t);
        if (flag){
            response.sendRedirect(request.getContextPath()+"/workbench/transaction/index.jsp");
        }


    }

    private void getaccountName(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("自动补全名字查询");
        String name = request.getParameter("name");
        CustomerService cs = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());
        List<String> list = cs.getaccountName(name);
        PrintJson.printJsonObj(response,list);
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
