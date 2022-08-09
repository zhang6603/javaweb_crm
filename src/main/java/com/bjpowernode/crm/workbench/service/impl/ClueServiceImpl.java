package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.settings.dao.UserDao;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.utils.DateTimeUtil;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.utils.UUIDUtil;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.dao.*;
import com.bjpowernode.crm.workbench.domain.*;
import com.bjpowernode.crm.workbench.service.ClueService;

import java.util.List;
import java.util.Map;

public class ClueServiceImpl implements ClueService {

    //关于线索的DAO
    private ClueDao clueDao = SqlSessionUtil.getSqlSession().getMapper(ClueDao.class);
    private ClueRemarkDao clueRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ClueRemarkDao.class);
    private ClueActivityRelationDao clueActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ClueActivityRelationDao.class);

    //关于客户的DAO
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);
    private CustomerRemarkDao customerRemarkDao = SqlSessionUtil.getSqlSession().getMapper(CustomerRemarkDao.class);

    //关于联系人的DAO
    private ContactsDao contactsDao = SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);
    private ContactsRemarkDao contactsRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ContactsRemarkDao.class);
    private ContactsActivityRelationDao contactsActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ContactsActivityRelationDao.class);

    //关于交易的DAO
    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);
    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    public List<User> getUserList() {
        List<User> list = userDao.getUserList();
        return list;

    }

    public boolean sava(Clue clue) {
        boolean flag = true;
        int count = clueDao.save(clue);
        if (count != 1){
            flag = false;
        }
        return flag;
    }

    public PaginationVO<Clue> pageList(Map<String, Object> map1) {
        //取得记录总条数total
        int total = clueDao.getTotalByCondition(map1);
        //取得线索列表clueList
        List<Clue> clueList = clueDao.getClueByCondition(map1);
        //封装到vo中返回
        PaginationVO<Clue> paginationVO = new PaginationVO<Clue>();
        paginationVO.setTotal(total);
        paginationVO.setDataList(clueList);
        return paginationVO;
    }

    public Clue detail(String id) {
        //调用DAO层连接数据库根据id查单条
        Clue c = clueDao.detail(id);

        return c;
    }

    public boolean unbund(String id) {
        boolean flag = true ;
        int count = clueActivityRelationDao.unbund(id);
        if (count != 1){
            flag = false;
        }
        return flag;
    }

    public boolean bund(String cid, String[] aid) {
        //调用DAO层连接输入库
        boolean flag = true;
        int count = 0;
        for(String id : aid){
            ClueActivityRelation c = new ClueActivityRelation();
            c.setId(UUIDUtil.getUUID());
            c.setClueId(cid);
            c.setActivityId(id);
            count = clueActivityRelationDao.bund(c);
            if (count != 1){
                flag = false;
            }
        }

        return flag;
    }

    public boolean convert(String clueId, Tran t, String createBy) {
        boolean flag = true;
        String createTime = DateTimeUtil.getSysTime();
        //(1) 获取到线索id，通过线索id获取线索对象（线索对象当中封装了线索的信息）
        Clue c = clueDao.getById(clueId);

        //(2) 通过线索对象提取客户信息，当该客户不存在的时候，新建客户（根据公司的名称精确匹配，判断该客户是否存在！）
        String company = c.getCompany();
        Customer customer = customerDao.getByName(company);
        if (customer==null){
            //客户不存在,需要新建客户信息
            customer = new Customer();
            customer.setAddress(c.getAddress());
            customer.setWebsite(c.getWebsite());
            customer.setPhone(c.getPhone());
            customer.setOwner(c.getOwner());
            customer.setNextContactTime(c.getNextContactTime());
            customer.setName(company);
            customer.setId(UUIDUtil.getUUID());
            customer.setDescription(c.getDescription());
            customer.setCreateTime(createTime);
            customer.setCreateBy(createBy);
            customer.setContactSummary(c.getContactSummary());
            int count = customerDao.save(customer);
            if (count!=1){
                flag = false;
            }

        }

        //(3) 通过线索对象提取联系人信息，保存联系人
        Contacts contacts = new Contacts();
        contacts.setSource(c.getSource());
        contacts.setOwner(c.getOwner());
        contacts.setNextContactTime(c.getNextContactTime());
        contacts.setMphone(c.getMphone());
        contacts.setJob(c.getJob());
        contacts.setId(UUIDUtil.getUUID());
        contacts.setFullname(c.getFullname());
        contacts.setEmail(c.getEmail());
        contacts.setDescription(c.getDescription());
        contacts.setCustomerId(customer.getId());
        contacts.setCreateTime(createTime);
        contacts.setCreateBy(createBy);
        contacts.setContactSummary(c.getContactSummary());
        contacts.setAppellation(c.getAppellation());
        contacts.setAddress(c.getAddress());
        int count1 = contactsDao.save(contacts);
        if (count1!=1){
            flag = false;
        }

        //(4) 线索备注转换到客户备注以及联系人备注
        List<ClueRemark> clueRemarkList = clueRemarkDao.getByClueId(clueId);
        for (ClueRemark clueRemark : clueRemarkList){
            //遍历每一条线索备注信息,并新建客户备注与联系人备注
            String noteContent = clueRemark.getNoteContent();
            CustomerRemark customerRemark = new CustomerRemark();
            customerRemark.setNoteContent(noteContent);
            customerRemark.setCreateBy(createBy);
            customerRemark.setCreateTime(createTime);
            customerRemark.setCustomerId(customer.getId());
            customerRemark.setEditFlag("0");
            customerRemark.setId(UUIDUtil.getUUID());
            int count2 = customerRemarkDao.save(customerRemark);
            if (count2!=1){
                flag = false;
            }

            ContactsRemark contactsRemark = new ContactsRemark();
            contactsRemark.setNoteContent(noteContent);
            contactsRemark.setCreateBy(createBy);
            contactsRemark.setCreateTime(createTime);
            contactsRemark.setContactsId(contacts.getId());
            contactsRemark.setEditFlag("0");
            contactsRemark.setId(UUIDUtil.getUUID());
            int count3 = contactsRemarkDao.save(contactsRemark);
            if (count3!=1){
                flag = false;
            }
        }

        //(5) “线索和市场活动”的关系转换到“联系人和市场活动”的关系
        List<ClueActivityRelation> clueActivityRelationList = clueActivityRelationDao.getByClueId(clueId);
        for (ClueActivityRelation clueActivityRelation : clueActivityRelationList){
            String activityId = clueActivityRelation.getActivityId();
            ContactsActivityRelation contactsActivityRelation = new ContactsActivityRelation();
            contactsActivityRelation.setActivityId(activityId);
            contactsActivityRelation.setContactsId(contacts.getId());
            contactsActivityRelation.setId(UUIDUtil.getUUID());
            int count4 = contactsActivityRelationDao.save(contactsActivityRelation);
            if (count4!=1){
                flag = false;
            }
        }

        //(6) 如果有创建交易需求，创建一条交易
        if (t!=null){
            t.setOwner(c.getOwner());
            t.setCustomerId(customer.getId());
            t.setSource(c.getSource());
            t.setContactsId(contacts.getId());
            t.setCreateBy(createBy);
            t.setCreateTime(createTime);
            t.setDescription(c.getDescription());
            t.setContactSummary(c.getContactSummary());
            t.setNextContactTime(c.getNextContactTime());
            int count5 = tranDao.save(t);
            if (count5!=1){
                flag = false;
            }

            //(7) 如果创建了交易，则创建一条该交易下的交易历史
            TranHistory tranHistory = new TranHistory();
            tranHistory.setStage(t.getStage());
            tranHistory.setTranId(t.getId());
            tranHistory.setMoney(t.getMoney());
            tranHistory.setId(UUIDUtil.getUUID());
            tranHistory.setExpectedDate(t.getExpectedDate());
            tranHistory.setCreateTime(createTime);
            tranHistory.setCreateBy(createBy);
            int count6 = tranHistoryDao.save(tranHistory);
            if (count6!=1){
                flag = false;
            }
        }



        //(8) 删除线索备注
        for (ClueRemark clueRemark : clueRemarkList){

            int count7 = clueRemarkDao.delete(clueRemark);
            if (count7!=1){
                flag = false;
            }
        }


        //(9) 删除线索和市场活动的关系
        for (ClueActivityRelation clueActivityRelation : clueActivityRelationList){
            int count8 = clueActivityRelationDao.delete(clueActivityRelation);
            if (count8!=1){
                flag = false;
            }
        }

        //(10) 删除线索
        int count9 = clueDao.delete(clueId);
        if (count9!=1){
            flag = false;
        }



        return flag;
    }
}
