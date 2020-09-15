package com.supcon.mes.module_xj.model.bean;

import com.google.gson.annotations.Expose;
import com.supcon.common.com_http.BaseEntity;

/**
 * Created by wangshizhan on 2019/5/28
 * Email:wangshizhan@supcom.com
 */
public class DeviceDCSEntity extends BaseEntity {

    public String name;
    public Long value;
    public Long quality;
    public String timeStamp;
    public boolean result;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public Long getQuality() {
        return quality;
    }

    public void setQuality(Long quality) {
        this.quality = quality;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }


}
