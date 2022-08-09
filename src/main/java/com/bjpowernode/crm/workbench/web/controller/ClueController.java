package com.bjpowernode.crm.workbench.web.controller;


import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.PrintJson;
import com.bjpowernode.crm.utils.ServiceFactory;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.Clue;
import com.bjpowernode.crm.workbench.domain.Tran;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.bjpowernode.crm.workbench.service.ClueService;
import com.bjpowernode.crm.workbench.service.impl.ActivityServiceImpl;
import com.bjpowernode.crm.workbench.service.impl.ClueServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;


public class ClueController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("进入到线索控制器");
        String path = request.getServletPath();

        if ("/workbench/clue/getUserList.do".equals(path)){
            getUserList(request,response);
        }else if("/workbench/clue/save.do".equals(path)){
            save(request,response);
        }else if("/workbench/clue/pageList.do".equals(path)){
            pageList(request,response);
        }else if("/workbench/clue/detail.do".equals(path)){
            detail(request,response);
        }else if("/workbench/clue/showActivityList.do".equals(path)){
            showActivityList(request,response);
        }else if("/workbench/clue/unbund.do".equals(path)){
            unbund(request,response);
        }else if("/workbench/clue/getActivityListByNameAndNotByClue.do".equals(path)){
            getActivityListByName(request,response);
        }else if("/workbench/clue/bund.do".equals(path)){
            bund(request,response);
        }else if("/workbench/clue/getActivityListByName.do".equals(path)){
            getActivityListByConvert(request,response);
        }
        else if("/workbench/clue/convert.do".equals(path)){
            convert(request,response);
        }

    }

    private void convert(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("转换线索操作");
        //接受请求中携带的参数
        String clueId = request.getParameter("clueId");
        String flag = request.getParameter("flag");
        //创建交易对象
        Tran t = null;
        if ("a".equals(flag)){
            //携带了交易表单
            t = new Tran();
            t.setId(UUIDUtil.getUUID());
            t.setMoney(request.getParameter("money"));
            t.setName(request.getParameter("name"));
            t.setExpectedDate(request.getParameter("expectedDate"));
            t.setStage(request.getParameter("stage"));
            t.setActivityId(request.getParameter("activityId"));
        }
        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        boolean flag1 = cs.convert(clueId,t,createBy);
        if (flag1){
            response.sendRedirect(request.getContextPath()+"/workbench/clue/index.jsp");
        }

    }

    private void getActivityListByConvert(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到获取市场活动列表的操作");
        String name = request.getParameter("name");
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> list = as.getActivityListByConvert(name);
        PrintJson.printJsonObj(response,list);
    }

    private void bund(HttpServletRequest request, HttpServletResponse response) {
        String cid = request.getParameter("cid");
        String[] aid = request.getParameterValues("aid");
        //调用业务层处理业务
        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = cs.bund(cid,aid);
        PrintJson.printJsonFlag(response,flag);
    }

    private void getActivityListByName(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("获取未关联过的市场活动列表");
        String clueId = request.getParameter("clueId");
        String name = request.getParameter("name");
        //调用业务层处理业务
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("clueId",clueId);
        map.put("name",name);
        List<Activity> list = as.getActivityListByName(map);
        PrintJson.printJsonObj(response,list);
    }

    private void unbund(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("解除关联");
        String id = request.getParameter("id");
        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = cs.unbund(id);
        PrintJson.printJsonFlag(response,flag);
    }

    private void showActivityList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("展现该线索所关联的市场活动信息");
        //获取请求中携带的参数cid
        String cid = request.getParameter("cid");
        //调用业务层处理业务
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> list = as.showActivityList(cid);
        PrintJson.printJsonObj(response,list);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("跳转到详细信息JSP页面");
        //获取请求中携带的参数
        String id = request.getParameter("id");

        //调用业务层获取该id所对应的线索对象
        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        Clue c = cs.detail(id);
        //将该对象存入request域中
        request.setAttribute("c",c);
        //转发页面至JSP
        request.getRequestDispatcher("/workbench/clue/detail.jsp").forward(request,response);
    }

    private void pageList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到线索信息列表展示的操作");
        //从请求中获取携带的参数信息
        String fullname = request.getParameter("fullname");
        String company = request.getParameter("company");
        String phone = request.getParameter("phone");
        String source = request.getParameter("source");
        String owner = request.getParameter("owner");
        String mphone = request.getParameter("mphone");
        String clueState = request.getParameter("clueState");
        String pageNoStr = request.getParameter("pageNo");
        String pageSizeStr = request.getParameter("pageSize");
        int pageNo = Integer.parseInt(pageNoStr);
        int pageSize = Integer.parseInt(pageSizeStr);
        //计算越过的记录数
        int skipCount = (pageNo-1)*pageSize;

        //创建Map集合封装参数传递给业务层
        Map<String,Object> map1 = new HashMap<String, Object>();
        map1.put("fullname",fullname);
        map1.put("company",company);
        map1.put("phone",phone);
        map1.put("source",source);
        map1.put("owner",owner);
        map1.put("mphone",mphone);
        map1.put("clueState",clueState);
        map1.put("skipCount",skipCount);
        map1.put("pageSize",pageSize);

        //调用业务层处理业务
        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        PaginationVO<Clue> paginationVO = cs.pageList(map1);
        PrintJson.printJsonObj(response,paginationVO);


    }

    private void save(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到添加线索方法");
        //首先获取参数中携带的数据
        String id = UUIDUtil.getUUID();
        String fullname = request.getParameter("fullname");
        String appellation = request.getParameter("appellation");
        String owner = request.getParameter("owner");
        String company = request.getParameter("company");
        String job = request.getParameter("job");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String website = request.getParameter("website");
        String mphone = request.getParameter("mphone");
        String state = request.getParameter("state");
        String source = request.getParameter("source");
        String createBy = ((User)request.getSession().getAttribute("user")).getName();
        String createTime = DateTimeUtil.getSysTime();
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");
        String address = request.getParameter("address");

        Clue clue = new Clue();
        clue.setId(id);
        clue.setFullname(fullname);
        clue.setAppellation(appellation);
        clue.setOwner(owner);
        clue.setCompany(company);
        clue.setJob(job);
        clue.setEmail(email);
        clue.setPhone(phone);
        clue.setWebsite(website);
        clue.setMphone(mphone);
        clue.setState(state);
        clue.setSource(source);
        clue.setCreateBy(createBy);
        clue.setCreateTime(createTime);
        clue.setDescription(description);
        clue.setContactSummary(contactSummary);
        clue.setNextContactTime(nextContactTime);
        clue.setAddress(address);

        //调用业务层处理业务
        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = cs.sava(clue);
        PrintJson.printJsonFlag(response,flag);

    }

    private void getUserList(HttpServletRequest request, HttpServletResponse response) {
        //调用业务层处理业务
        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        List<User> list = cs.getUserList();
        PrintJson.printJsonObj(response,list);

    }


}
