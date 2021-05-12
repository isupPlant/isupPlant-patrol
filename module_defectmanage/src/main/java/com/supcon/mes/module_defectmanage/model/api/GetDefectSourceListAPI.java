package com.supcon.mes.module_defectmanage.model.api;

import com.app.annotation.apt.ContractFactory;
import com.supcon.mes.middleware.model.bean.BAP5CommonEntity;

@ContractFactory(entites = {BAP5CommonEntity.class} )
public interface GetDefectSourceListAPI {
    void getDefectSourceList(int pageNo);
}
