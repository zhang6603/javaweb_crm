package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.*;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.bjpowernode.crm.workbench.service.impl.ActivityServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ActivityController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("进入到市场活动控制器");
        String path = request.getServletPath();

        if ("/workbench/activity/getUserList.do".equals(path)){
            getUserList(request,response);
        }else if ("/workbench/activity/save.do".equals(path)){
            save(request,response);
        }else if ("/workbench/activity/pageList.do".equals(path)){
            pageList(request,response);
        }else if ("/workbench/activity/delete.do".equals(path)){
            delete(request,response);
        }else if ("/workbench/activity/edit.do".equals(path)){
            edit(request,response);
        }else if ("/workbench/activity/update.do".equals(path)){
            update(request,response);
        }else if ("/workbench/activity/detail.do".equals(path)){
            detail(request,response);
        }else if ("/workbench/activity/getRemarkListByAid.do".equals(path)){
            getRemarkListByAid(request,response);
        }else if ("/workbench/activity/deleteRemark.do".equals(path)){
            deleteRemark(request,response);
        }else if ("/workbench/activity/updateRemark.do".equals(path)){
            updateRemark(request,response);
        }else if ("/workbench/activity/saveRemark.do".equals(path)){
            saveRemark(request,response);
        }

    }

    private void saveRemark(HttpServletRequest request, HttpServletResponse response) {
        String noteContent = request.getParameter("noteContent");
        String activityId = request.getParameter("activityId");
        String uuid = UUIDUtil.getUUID();
        String createTime = DateTimeUtil.getSysTime();
        String createBy = ((User) request.getSession().getAttribute("user")).getName();
        String editFlag = "0";

        ActivityRemark ar = new ActivityRemark();
        ar.setId(uuid);
        ar.setNoteContent(noteContent);
        ar.setCreateTime(createTime);
        ar.setCreateBy(createBy);
        ar.setEditFlag(editFlag);
        ar.setActivityId(activityId);

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = as.saveRemark(ar);
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("success",flag);
        map.put("ar",ar);
        PrintJson.printJsonObj(response,map);

    }

    private void updateRemark(HttpServletRequest request, HttpServletResponse response) {
        //首先获取请求中携带的参数
        String remarkId = request.getParameter("remarkId");
        String noteContent = request.getParameter("noteContent");

        ActivityRemark ar = new ActivityRemark();

        ar.setId(remarkId);
        ar.setNoteContent(noteContent);
        ar.setEditBy(((User)request.getSession().getAttribute("user")).getName());
        ar.setEditTime(DateTimeUtil.getSysTime());
        ar.setEditFlag("1");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = as.updateRemark(ar);

        Map<String,Object> map = new HashMap<String, Object>();
        map.put("success",flag);
        map.put("ar",ar);
        PrintJson.printJsonObj(response,map);
    }

    private void deleteRemark(HttpServletRequest request, HttpServletResponse response) {

        //首先获取请求中携带的参数
        String remarkId = request.getParameter("remarkId");
        //业务层调用方法处理业务
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = as.deleteRemark(remarkId);
        PrintJson.printJsonFlag(response,flag);
    }

    private void getRemarkListByAid(HttpServletRequest request, HttpServletResponse response) {
        //获取请求中携带的参数Aid
        String Aid = request.getParameter("Aid");

        //业务层调用方法处理业务
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        List<ActivityRemark> list = as.getRemarkListByAid(Aid);

        PrintJson.printJsonObj(response,list);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //获取请求中携带的参数id
        String id = request.getParameter("id");
        //业务层调用方法,返回activity对象
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        Activity a = as.detail(id);

        //将对象a存储到请求域中
        request.setAttribute("a",a);
        //采用转发的方式跳转页面
        request.getRequestDispatcher("/workbench/activity/detail.jsp").forward(request,response);
    }

    private void update(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("执行市场活动修改操作");

        String id = request.getParameter("id");
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String cost = request.getParameter("cost");
        String description = request.getParameter("description");
        String editTime = DateTimeUtil.getSysTime();
        String editBy = ((User)request.getSession().getAttribute("user")).getName();

        //创建Activity对象封装数据
        Activity activity = new Activity();
        activity.setCost(cost);
        activity.setCreateBy(editBy);
        activity.setCreateTime(editTime);
        activity.setDescription(description);
        activity.setEndDate(endDate);
        activity.setName(name);
        activity.setOwner(owner);
        activity.setId(id);
        activity.setStartDate(startDate);
        //创建业务层动态代理对象
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        //调用业务层方法
        boolean flag = as.update(activity);
        PrintJson.printJsonFlag(response,flag);
    }

    private void edit(HttpServletRequest request, HttpServletResponse response) {
        //首先接受请求中的参数id
        String id = request.getParameter("id");

        //调用业务层取得map集合
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        Map<String,Object> map = as.edit(id);

        //利用工具类将map集合打包成json格式的字符串
        PrintJson.printJsonObj(response,map);


    }

    private void delete(HttpServletRequest request, HttpServletResponse response) {
        //首先获取请求中的参数
        String[] ids = request.getParameterValues("id");

        //创建业务层对象,调用方法完成业务逻辑
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = as.delete(ids);

        //给前端返回flag变量
        PrintJson.printJsonFlag(response,flag);

    }

    private void pageList(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("进入到市场活动列表查询");

        String name = request.getParameter("name");
        String owner = request.getParameter("owner");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String pageNoStr = request.getParameter("pageNo");
        int pageNo = Integer.valueOf(pageNoStr);
        //每一页展示的记录数
        String pageSizeStr = request.getParameter("pageSize");
        int pageSize = Integer.valueOf(pageSizeStr);
        //计算出略过的记录数
        int skipCount = (pageNo-1)*pageSize;

        Map<String,Object> map = new HashMap<String, Object>();
        map.put("name",name);
        map.put("owner",owner);
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("pageSize",pageSize);
        map.put("skipCount",skipCount);

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());


        /*
            前端要:市场活动信息列表
                  查询的总条数

                  业务层拿到了以上两项信息之后,如果做返回
                  map
                  map.put("dataList",dataList)
                  map.put("total",total)
                  PrintJson  map--->json
                  {"total":total,"dataList":[{市场活动1},{市场活动2},....]}

                  vo
                  paginationVO<T>
                    private int total;
                    private List<T> dataList

                  PaginationVO<Activity> vo = new PaginationVO<>();
                  vo.setTotal(total);
                  vo.setDataList(dataList);
                  PrintJSON  vo --> json

                  将来分页查询,每个模块都有,所以我们选择使用一个通用vo,操作起来比较方便

         */

        PaginationVO<Activity> vo = as.pageList(map);
        PrintJson.printJsonObj(response,vo);


    }

    private void save(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("执行市场活动添加的操作(结合条件查询)");

        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String name = request.getParameter("name");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String cost = request.getParameter("cost");
        String description = request.getParameter("description");
        String createTime = DateTimeUtil.getSysTime();
        String createBy = ((User)request.getSession().getAttribute("user")).getName();

        //创建Activity对象封装数据
        Activity activity = new Activity();
        activity.setCost(cost);
        activity.setCreateBy(createBy);
        activity.setCreateTime(createTime);
        activity.setDescription(description);
        activity.setEndDate(endDate);
        activity.setName(name);
        activity.setOwner(owner);
        activity.setId(id);
        activity.setStartDate(startDate);
        //创建业务层动态代理对象
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        //调用业务层方法
        boolean flag = as.save(activity);
        PrintJson.printJsonFlag(response,flag);
    }

    private void getUserList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("取得用户信息列表");
        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> uList = us.getUserList();
        PrintJson.printJsonObj(response,uList);
    }


}
