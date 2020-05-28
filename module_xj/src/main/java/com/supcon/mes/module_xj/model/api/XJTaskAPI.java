package com.supcon.mes.module_xj.model.api;

import com.app.annotation.apt.ContractFactory;
import com.supcon.mes.middleware.model.bean.CommonBAPListEntity;

import java.util.Map;

/**
 * Created by wangshizhan on 2020/3/24
 * Email:wangshizhan@supcom.com
 */
@ContractFactory(entites = CommonBAPListEntity.class)
public interface XJTaskAPI {

    void getTaskList(int pageNo, int pageSize, Map<String,Object> queryMap);

}
