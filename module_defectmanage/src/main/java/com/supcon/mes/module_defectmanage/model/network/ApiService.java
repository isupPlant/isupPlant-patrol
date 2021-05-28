package com.supcon.mes.module_defectmanage.model.network;

import com.app.annotation.apt.ApiFactory;
import com.supcon.mes.middleware.model.bean.BAP5CommonEntity;
import com.supcon.mes.middleware.model.bean.BapPageResultEntity;
import com.supcon.mes.module_defectmanage.model.bean.DefectListNumEntity;
import com.supcon.mes.module_defectmanage.model.bean.DefectModelEntity;
import com.supcon.mes.module_defectmanage.model.bean.DefectOnlineEntity;
import com.supcon.mes.module_defectmanage.model.bean.DefectRequestListEntity;
import com.supcon.mes.module_defectmanage.model.bean.DefectSourceEntity;

import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Administrator on 2016/3/23.
 */
@ApiFactory(name = "DefectManagerHttpClient")
public interface ApiService {

    @POST("/msService/DefectManage/problemManage/problemManage/defectEntry")
    Flowable<BAP5CommonEntity<Object>> defectEntry(@Body DefectModelEntity body);

    @POST("/msService/DefectManage/problemManage/problemManage/defectEntryBatch")
    Flowable<BAP5CommonEntity<Object>> defectEntryBatch(@Body List<DefectModelEntity> infoList);

    /**
     *
     * {
     * "pageSize":10,
     * "pageNum":1,
     * "cid":1000,
     * "defectSource":"OSI",
     * "eamDeptId":null,
     * "areaCode":null,
     * "tableNo":null,
     * "eamId":null
     * }
     * @return
     */
    @POST("/msService/DefectManage/problemManage/problemManage/getDefectPage")
    Flowable<BAP5CommonEntity<BapPageResultEntity<DefectOnlineEntity>>> getDefectList(@Body DefectRequestListEntity map);


    @POST("/msService/DefectManage/problemSource/problemSource/sourcePartRef-query")
    Flowable<BAP5CommonEntity<BapPageResultEntity<DefectSourceEntity>>> sourcePartRefQuery(@Body Map<String, Object> queryParam);

    @POST("/msService/DefectManage/problemListed/listedNumber/listedRef-query")
    Flowable<BAP5CommonEntity<BapPageResultEntity<DefectListNumEntity>>> listedRefQuery(@Body Map<String, Object> queryParam);

}
