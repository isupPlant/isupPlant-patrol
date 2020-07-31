package com.supcon.mes.module_xj.model.bean;

import com.supcon.common.com_http.BaseEntity;
import com.supcon.mes.middleware.model.bean.SystemCodeEntity;
import com.supcon.mes.middleware.model.bean.xj.XJAreaEntity;
import com.supcon.mes.middleware.model.bean.xj.XJRouteEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by wangshizhan on 2020/3/24
 * Email:wangshizhan@supcom.com
 */
public class XJTaskEntity extends BaseEntity {

    /**
     *          attrMap: {PATROL_1_0_0_patrolTask_potrolTaskList_LISTPT_ASSO_3a556662_35fb_4884_a6ab_1aff5d055ac7: "王世展"}
     * cid: 1000
     *          createStaff: null
     * createTime: 1585031314080
     * endTime: 1585209600000
     * id: 1592
     * patrolType: {id: "PATROL_routeType/eam", value: "设备巡检"}
     *  id: "PATROL_routeType/eam"
     *  value: "设备巡检"
     *          pending: {activityName: null, activityType: null, bulkDealFlag: null, deploymentId: null, id: null,…}
     * startTime: 1585202400000
     *          status: 99
     * tableInfoId: 2837
     * tableNo: "patrolTask_20200324_025"
     * taskState: {id: "PATROL_taskState/notIssued", value: "未下发"}
     *  id: "PATROL_taskState/notIssued"
     *  value: "未下发"
     *          valid: true
     *          version: 1
     * workRoute: {code: "lx001", id: 1025, name: "巡检路线001"}
     *  code: "lx001"
     *  id: 1025
     *  name: "巡检路线001"
     */

    public Map<String, Object> attrMap;

    public Long id;
    public Long cid;
    public Long tableInfoId;
    public String tableNo;
    public String staffName;
    public long createTime;

    public long startTime;
    public long endTime;

    public SystemCodeEntity patrolType;
    public SystemCodeEntity taskState;

    public XJRouteEntity workRoute;

    public boolean isFinished;
    public long realStartTime;
    public long realEndTime;

    public int viewType;//0 首页； 1 上传、获取页面
    public boolean isChecked;
    public boolean isTemp;
    public List<XJAreaEntity> areas;

    public String exceptinWorkIds;
//    public List<XJLocalWorkItemEntity> works;
}
