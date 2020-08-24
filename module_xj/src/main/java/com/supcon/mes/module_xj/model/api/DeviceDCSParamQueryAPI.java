package com.supcon.mes.module_xj.model.api;

import com.app.annotation.apt.ContractFactory;
import com.supcon.mes.middleware.model.bean.BAP5CommonListEntity;
import com.supcon.mes.middleware.model.bean.CommonListEntity;

import java.util.List;

/**
 * @author yangkai
 * @email yangkai2@supcon.com
 * Date: 2020/8/17 9:22
 */
@ContractFactory(entites = BAP5CommonListEntity.class)
public interface DeviceDCSParamQueryAPI {
    void getDeviceDCSParams(List<String> list);
}
