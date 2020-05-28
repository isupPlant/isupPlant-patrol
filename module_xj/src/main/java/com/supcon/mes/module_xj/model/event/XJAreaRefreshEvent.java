package com.supcon.mes.module_xj.model.event;

import com.supcon.mes.middleware.model.bean.xj.XJAreaEntity;
import com.supcon.mes.middleware.model.event.BaseEvent;

/**
 * Created by wangshizhan on 2020/4/15
 * Email:wangshizhan@supcom.com
 */
public class XJAreaRefreshEvent extends BaseEvent {

    private XJAreaEntity mXJAreaEntity;

    public XJAreaRefreshEvent(XJAreaEntity xjAreaEntity){
        mXJAreaEntity = xjAreaEntity;
    }

    public XJAreaEntity getXJAreaEntity() {
        return mXJAreaEntity;
    }
}
