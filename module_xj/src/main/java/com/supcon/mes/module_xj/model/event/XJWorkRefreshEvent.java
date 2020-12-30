package com.supcon.mes.module_xj.model.event;

import com.supcon.mes.middleware.model.bean.xj.XJTaskWorkEntity;
import com.supcon.mes.middleware.model.bean.xj.XJWorkEntity;
import com.supcon.mes.middleware.model.event.BaseEvent;

/**
 * Created by wangshizhan on 2020/4/15
 * Email:wangshizhan@supcom.com
 */
public class XJWorkRefreshEvent extends BaseEvent {

    private int position;
    private boolean isFinish = false;
    private XJTaskWorkEntity mXJWorkEntity;
    public XJWorkRefreshEvent(){

    }

    public XJWorkRefreshEvent(XJTaskWorkEntity xjWorkEntity){
        mXJWorkEntity = xjWorkEntity;
    }

    public XJTaskWorkEntity getXJWorkEntity() {
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
