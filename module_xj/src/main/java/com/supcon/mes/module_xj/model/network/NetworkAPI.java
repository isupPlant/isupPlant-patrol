package com.supcon.mes.module_xj.model.network;

import com.app.annotation.apt.ApiFactory;
import com.supcon.mes.middleware.model.bean.BAP5CommonEntity;
import com.supcon.mes.middleware.model.bean.BAP5CommonListEntity;
import com.supcon.mes.middleware.model.bean.CommonBAPListEntity;
import com.supcon.mes.middleware.model.bean.xj.XJTaskEntity;
import com.supcon.mes.module_xj.model.bean.DeviceDCSEntity;
import com.supcon.mes.module_xj.model.bean.LSXJRouterEntity;

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
     * 巡检未下发的巡检任务列表
     * @param pageMap
     * @return
     */
    @POST("/msService/PATROL/patrolTask/potrolTask/potrolTaskList-query")
    Flowable<BAP5CommonEntity<CommonBAPListEntity<XJTaskEntity>>> getTaskList(@Body Map<String, Object> pageMap);


    /**
     * 获取已下发和正在进行中的巡检任务列表
     *
     * 由于平台接口原因，现改用两个接口区分，详细情况问杨凯，丁亚浩
     *
     * @param pageMap
     * @return
     */
    @POST("/msService/PATROL/patrolTask/potrolTask/mobilePotrolTaskList-query")
    Flowable<BAP5CommonEntity<CommonBAPListEntity<XJTaskEntity>>> getRunningTaskList(@Body Map<String, Object> pageMap);

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
    Flowable<BAP5CommonEntity<Long>> uploadTaskResult(@Query ("isOnLine") boolean isOnLine, @Body Map<String, Object> pageMap);

    /**
     * 上传实时定位
     * @param pageMap
     * @return
     */
    @POST("/msService/PATROL/routeMap/mapInfo/uploadLoaction")
    Flowable<BAP5CommonEntity> uploadLoaction(@Body Map<String, Object> pageMap);



    /**
     * 更改巡检开始状态
     * @param pageMap
     * @return
     */
    @POST("/msService/PATROL/mobile/patrolTask/updatePatrolTask")
    Flowable<BAP5CommonEntity> updateXJTaskStatus(@Body Map<String, Object> pageMap);






    /**
     * 设备id获取巡检路线
     * @param
     * @return
     */
    @POST("/msService/EAM/workGroup/getRoutListByEamId")
    Flowable<BAP5CommonListEntity<LSXJRouterEntity>> getRouteList(@Body Map<String, Object> params);


    /**
     * 获取DCS设备数据
     */
    @POST("/msService/TagManagement/readTagsSync")
    Flowable<BAP5CommonListEntity<DeviceDCSEntity>> getDeviceDCSParam(@Body Map<String, Object> pageMap);
}
