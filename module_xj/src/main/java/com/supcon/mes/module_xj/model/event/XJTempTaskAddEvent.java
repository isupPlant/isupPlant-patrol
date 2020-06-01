package com.supcon.mes.module_xj.model.event;

import com.supcon.mes.middleware.model.event.BaseEvent;
import com.supcon.mes.module_xj.model.bean.XJTaskEntity;

/**
 * Created by wangshizhan on 2020/5/30
 * Email:wangshizhan@supcom.com
 */
public class XJTempTaskAddEvent extends BaseEvent {

    private XJTaskEntity mTempTaskEntity;

    public XJTempTaskAddEvent(XJTaskEntity tempTaskEntity){
        mTempTaskEntity = tempTaskEntity;
    }

    public XJTaskEntity getTempTaskEntity() {
        return mTempTaskEntity;
    }
}
