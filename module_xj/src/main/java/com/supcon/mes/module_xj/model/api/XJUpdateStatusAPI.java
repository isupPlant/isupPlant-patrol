package com.supcon.mes.module_xj.model.api;

import com.app.annotation.apt.ContractFactory;
import com.supcon.mes.middleware.model.bean.BAP5CommonEntity;

import java.util.Map;

/**
 * @author yangkai
 * @email yangkai2@supcon.com
 * Date: 2020/8/5 9:05
 */

@ContractFactory(entites = {BAP5CommonEntity.class})
public interface XJUpdateStatusAPI  {
        void updateXJTaskStatus(long taskId,String status);

}
