package com.supcon.mes.module_defectmanage.model.network;

import com.app.annotation.apt.ApiFactory;
import com.supcon.mes.middleware.model.bean.BAP5CommonEntity;
import com.supcon.mes.module_defectmanage.model.bean.DefectModelEntity;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Administrator on 2016/3/23.
 */
@ApiFactory(name = "DefectManagerHttpClient")
public interface ApiService {

    @POST("/msService/DefectManage/problemManage/problemManage/defectEntry")
    Flowable<BAP5CommonEntity<Long>> defectEntry(@Body DefectModelEntity body);

    @POST("/msService/DefectManage/problemManage/problemManage/defectEntryBatch")
    Flowable<BAP5CommonEntity<Object>> defectEntryBatch(@Body List<DefectModelEntity> infoList);


}
