package com.supcon.mes.module_xj.model.api;

import com.app.annotation.apt.ContractFactory;
import com.supcon.mes.middleware.model.bean.xj.XJTaskEntity;
import com.supcon.mes.middleware.model.bean.xj.XJWorkEntity;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * Created by wangshizhan on 2020/4/15
 * Email:wangshizhan@supcom.com
 */
@ContractFactory(entites = {String.class, Long.class,Long.class})
public interface XJTaskSubmitAPI {

    void uploadFile(List<XJTaskEntity> xjTaskEntities, boolean isArea);
    void uploadXJWorkFile(List<XJWorkEntity> xjTaskEntities, boolean isArea, String workName, long actualStartTime,long actualEndTime);
    void uploadXJData(boolean isOnLine, Map<String, Object> pageMap);
}
