package com.supcon.mes.module_xj.model.bean;

import com.supcon.common.com_http.BaseEntity;
import com.supcon.mes.middleware.model.bean.xj.XJTaskEntity;

import java.util.List;

/**
 * Created by wangshizhan on 2020/1/9
 * Email:wangshizhan@supcom.com
 */
public class XJTaskGroupEntity extends BaseEntity {

    public String staffName;
    public long date;
    public String name;
    public String typeValue;
    public int spanCount;
    public List<XJTaskEntity> taskEntities;
    public boolean isTemp;
}
