package com.bjpowernode.crm.web.listener;

import com.bjpowernode.crm.settings.service.DicService;
import com.bjpowernode.crm.settings.service.impl.DicServiceImpl;
import com.bjpowernode.crm.utils.ServiceFactory;
import com.bjpowernode.crm.workbench.service.ClueService;
import com.bjpowernode.crm.workbench.service.impl.ClueServiceImpl;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Map;
import java.util.Set;

public class SysInitListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent event) {
        System.out.println("服务器缓存处理数据字典开始");
        //通过event事件取得application对象
        ServletContext application = event.getServletContext();

        //将数据字典存储至application域中
        DicService ds = (DicService) ServiceFactory.getService(new DicServiceImpl());

        Map<String,Object> map = ds.getAll();

        //遍历map集合，将其中的List集合分别存储至application域中
        Set<String> set = map.keySet();
        for(String s : set){
            application.setAttribute(s,map.get(s));
        }
        System.out.println("服务器缓存处理数据字典结束");
    }
}
