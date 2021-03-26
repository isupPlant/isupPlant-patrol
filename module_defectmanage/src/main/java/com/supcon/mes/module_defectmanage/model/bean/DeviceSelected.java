package com.supcon.mes.module_defectmanage.model.bean;

import com.supcon.mes.middleware.model.bean.DeviceEntity;
import com.supcon.mes.middleware.model.bean.SelectEntity;

/**
 * Time:    2021/3/25  16: 53
 * Author： mac
 * Des:
 */
public class DeviceSelected extends SelectEntity<Long> {
    public Long id;


    public String name;

    @Override
    public Long get_id() {
        return id;
    }

    @Override
    public String get_name() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
