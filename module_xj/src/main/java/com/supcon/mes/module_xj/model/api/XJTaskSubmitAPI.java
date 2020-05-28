package com.supcon.mes.module_xj.model.api;

import com.app.annotation.apt.ContractFactory;
import com.supcon.common.com_http.NullEntity;
import com.supcon.mes.module_xj.model.bean.XJTaskEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by wangshizhan on 2020/4/15
 * Email:wangshizhan@supcom.com
 */
@ContractFactory(entites = {String.class, NullEntity.class})
public interface XJTaskSubmitAPI {

    void uploadFile(List<XJTaskEntity> xjTaskEntities, boolean isArea);

    void uploadXJData(boolean isOnLine, Map<String, Object> pageMap);
}
