package com.supcon.mes.module_xj.model.api;

import com.app.annotation.apt.ContractFactory;
import com.supcon.mes.middleware.model.bean.BAP5CommonListEntity;

/**
 * @author fengjun
 * 创建日期：2018/6/14
 * 描述：MainActivity
 */
@ContractFactory(entites = BAP5CommonListEntity.class)
public interface LSXJRouterAPI {

    void queryRouteList(long eamId);
}
