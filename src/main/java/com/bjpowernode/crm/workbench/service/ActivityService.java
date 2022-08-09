package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;

import java.util.List;
import java.util.Map;

public interface ActivityService {

    boolean save(Activity activity);

    PaginationVO<Activity> pageList(Map<String, Object> map);

    boolean delete(String[] ids);

    Map<String, Object> edit(String id);

    boolean update(Activity activity);

    Activity detail(String id);

    List<ActivityRemark> getRemarkListByAid(String aid);

    boolean deleteRemark(String remarkId);

    boolean updateRemark(ActivityRemark ar);

    boolean saveRemark(ActivityRemark ar);

    List<Activity> showActivityList(String cid);

    List<Activity> getActivityListByName(Map<String, Object> map);

    List<Activity> getActivityListByConvert(String name);

}
