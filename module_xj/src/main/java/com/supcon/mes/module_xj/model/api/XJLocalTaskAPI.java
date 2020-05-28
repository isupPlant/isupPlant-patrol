package com.supcon.mes.module_xj.model.api;

import com.app.annotation.apt.ContractFactory;
import com.supcon.common.com_http.NullEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by wangshizhan on 2020/4/8
 * Email:wangshizhan@supcom.com
 */
@ContractFactory(entites = {List.class, NullEntity.class})
public interface XJLocalTaskAPI {

    void getLocalTask(Map<String,Object> queryMap);

    void saveLocalTask();

}
