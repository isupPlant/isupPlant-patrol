package com.supcon.mes.module_xj.model.api;

import com.app.annotation.apt.ContractFactory;
import com.supcon.common.com_http.NullEntity;

import java.util.Map;

/**
 * Created by wangshizhan on 2020/4/10
 * Email:wangshizhan@supcom.com
 */
@ContractFactory(entites = NullEntity.class)
public interface XJTaskStateAPI {

    void updateTaskState(Map<String, Object> queryMap);

}
