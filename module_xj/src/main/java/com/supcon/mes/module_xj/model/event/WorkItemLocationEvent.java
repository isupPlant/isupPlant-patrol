package com.supcon.mes.module_xj.model.event;

import com.supcon.common.com_http.BaseEntity;

/**
 * Created by wangshizhan on 2017/12/29.
 * Email:wangshizhan@supcon.com
 */

public class WorkItemLocationEvent extends BaseEntity {

    private int location;
    private String msg;

    public WorkItemLocationEvent(int location){
        this.location = location;
    }

    public int getLocation() {
        return location;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
