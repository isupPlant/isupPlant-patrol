package com.supcon.mes.module_xj.model.api;

import com.app.annotation.apt.ContractFactory;
import com.supcon.mes.middleware.model.bean.CommonBAP5ListEntity;
import com.supcon.mes.middleware.model.bean.CommonListEntity;
import com.supcon.mes.module_xj.model.bean.CommonDeviceDCSListEntity;

import java.util.List;

/**
 * @author yangkai
 * @email yangkai2@supcon.com
 * Date: 2020/8/17 9:22
 */
@ContractFactory(entites = CommonDeviceDCSListEntity.class)
public interface DeviceDCSParamQueryAPI {
    void getDeviceDCSParams(List<String> list);
}
