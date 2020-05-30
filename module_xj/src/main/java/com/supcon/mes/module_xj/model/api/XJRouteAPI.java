package com.supcon.mes.module_xj.model.api;

import com.app.annotation.apt.ContractFactory;

import java.util.List;

/**
 * Created by wangshizhan on 2020/5/30
 * Email:wangshizhan@supcom.com
 */
@ContractFactory(entites = List.class)
public interface XJRouteAPI {
    void getRouteList();
}
