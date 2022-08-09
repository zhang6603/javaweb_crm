package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.settings.dao.UserDao;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.dao.ActivityDao;
import com.bjpowernode.crm.workbench.dao.ActivityRemarkDao;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.service.ActivityService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityServiceImpl implements ActivityService {
    private ActivityDao activityDao = SqlSessionUtil.getSqlSession().getMapper(ActivityDao.class);
    private ActivityRemarkDao activityRemarkDao =SqlSessionUtil.getSqlSession().getMapper(ActivityRemarkDao.class);
    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);
    public boolean save(Activity activity) {
        boolean flag = true;
        int count = activityDao.save(activity);
        if (count != 1){
            flag = false;
        }
        return flag;
    }

    public PaginationVO<Activity> pageList(Map<String, Object> map) {
        //取得total
        int total = activityDao.getTotalByCondition(map);

        //取得dataList
        List<Activity> dataList = activityDao.getActivityByCondition(map);

        //将total和dataList封装到vo中
        PaginationVO<Activity> vo = new PaginationVO<Activity>();
        vo.setTotal(total);
        vo.setDataList(dataList);

        //将vo返回
        return vo;
    }

    public boolean delete(String[] ids) {
        boolean flag = false;
        //查询需要删除备注的数量
        int count1 = activityRemarkDao.getCountByAids(ids);
        //删除备注
        int count2 = activityRemarkDao.deleteByAids(ids);
        if (count1 == count2){
            flag = true;
        }
        //删除市场活动
        int count3 = activityDao.delete(ids);
        if (count3 == ids.length){
            flag = true;
        }
    return flag;
    }

    public Map<String, Object> edit(String id) {
        //调用DAO层
        //取得uList
        List<User> uList = userDao.getUserList();
        //取得a
        Activity a = activityDao.getById(id);
        //打包成map集合返回
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("uList",uList);
        map.put("a",a);
        return map;
    }

    public boolean update(Activity activity) {
        boolean flag = true;
        int count = activityDao.update(activity);
        if (count != 1){
            flag = false;
        }
        return flag;
    }

    public Activity detail(String id) {
        //业务层调用DAO层连接数据库(根据id查单条)
        Activity a = activityDao.detail(id);
        return a;
    }

    public List<ActivityRemark> getRemarkListByAid(String aid) {
        //调用DAO层,连接数据库查询市场活动相对于的备注信息
        List<ActivityRemark> list = activityRemarkDao.getRemarkListByAid(aid);
        return list;
    }

    public boolean deleteRemark(String remarkId) {
        boolean flag = true;
        //调用DAO层,连接数据库根据id删单条
        int count = activityRemarkDao.deleteById(remarkId);
        if (count != 1){
            flag = false;
        }
        return flag;

    }

    public boolean updateRemark(ActivityRemark ar) {
        boolean flag = true;
        //调用DAO层,连接数据库根据对象更新单条
        int count = activityRemarkDao.update(ar);

        if (count != 1){
            flag = false;
        }
        return flag;
    }

    public boolean saveRemark(ActivityRemark ar) {

        boolean flag = true;

        int count = activityRemarkDao.saveRemark(ar);
        if (count != 1){
            flag = false;
        }
        return flag;
    }

    public List<Activity> showActivityList(String cid) {
        //调用DAO层连接数据库

        List<Activity> list = activityDao.getActivityListByClue(cid);

        return list;
    }

    public List<Activity> getActivityListByName(Map<String, Object> map) {
        //调用DAO层连接数据库
        List<Activity> list = activityDao.getActivityListByName(map);

        return list;
    }

    public List<Activity> getActivityListByConvert(String name) {
        List<Activity> list = activityDao.getActivityListByConvert(name);
        return list;
    }
}
