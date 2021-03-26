package com.supcon.mes.module_defectmanage.model.api;

import com.app.annotation.apt.ContractFactory;
import com.supcon.mes.middleware.model.bean.BAP5CommonEntity;
import com.supcon.mes.module_defectmanage.model.bean.DefectModelEntity;

import java.util.List;

@ContractFactory(entites = {BAP5CommonEntity.class} )
public interface GetDefectListAPI {
    void getDefectList(String tableNo, String areaCode, int pageNo);
}
