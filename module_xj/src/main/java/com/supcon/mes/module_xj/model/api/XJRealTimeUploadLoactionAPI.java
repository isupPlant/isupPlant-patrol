package com.supcon.mes.module_xj.model.api;

import com.app.annotation.apt.ContractFactory;
import com.supcon.mes.middleware.model.bean.BAP5CommonEntity;

import java.util.Map;

/**
 * @author yangkai
 * @email yangkai2@supcon.com
 * 上传实时定位
 */

@ContractFactory(entites = {BAP5CommonEntity.class})
public interface XJRealTimeUploadLoactionAPI {
        void uploadLoaction(Map<String,Object> queryMap);
}
