package com.supcon.mes.module_xj.model.network;

import com.app.annotation.apt.ApiFactory;
import com.supcon.mes.middleware.model.bean.BAP5CommonEntity;
import com.supcon.mes.middleware.model.bean.CommonBAPListEntity;
import com.supcon.mes.module_xj.model.bean.XJTaskEntity;

import java.util.Map;

import io.reactivex.Flowable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by wangshizhan on 2020/3/24
 * Email:wangshizhan@supcom.com
 */
@ApiFactory(name = "XJHttpClient")
public interface NetworkAPI {

    /**
     * 巡检任务列表
     * @param pageMap
     * @return
     */
    @POST("/msService/PATROL/patrolTask/potrolTask/potrolTaskList-query")
    Flowable<BAP5CommonEntity<CommonBAPListEntity<XJTaskEntity>>> getTaskList(@Body Map<String, Object> pageMap);

    /**
     * 批量变更巡检任务状态
     * @param queryMap
     * @return
     */
    @GET("/msService/PATROL/patrolTask/potrolTask/taskStateUpdate")
    Flowable<BAP5CommonEntity<String>> taskStateUpdate(@QueryMap Map<String, Object> queryMap);




    /**
     * 上传巡检任务
     * @param pageMap
     * @return
     */
    @POST("/msService/PATROL/uploadTaskResult")
    Flowable<BAP5CommonEntity<String>> uploadTaskResult(@Query ("isOnLine") boolean isOnLine, @Body Map<String, Object> pageMap);
}
