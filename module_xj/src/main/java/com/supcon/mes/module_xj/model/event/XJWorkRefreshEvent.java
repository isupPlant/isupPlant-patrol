package com.supcon.mes.module_xj.model.event;

import com.supcon.mes.middleware.model.bean.xj.XJWorkEntity;
import com.supcon.mes.middleware.model.event.BaseEvent;

/**
 * Created by wangshizhan on 2020/4/15
 * Email:wangshizhan@supcom.com
 */
public class XJWorkRefreshEvent extends BaseEvent {

    private int position;
    private boolean isFinish = false;
    private XJWorkEntity mXJWorkEntity;
    public XJWorkRefreshEvent(){

    }

    public XJWorkRefreshEvent(XJWorkEntity xjWorkEntity){
        mXJWorkEntity = xjWorkEntity;
    }

    public XJWorkEntity getXJWorkEntity() {
        return mXJWorkEntity;
    }

    public XJWorkRefreshEvent(int position, boolean isFinish){
        this.position = position;
        this.isFinish = isFinish;
    }

    public int getPosition() {
        return position;
    }

    public boolean isFinish() {
        return isFinish;
    }
}
