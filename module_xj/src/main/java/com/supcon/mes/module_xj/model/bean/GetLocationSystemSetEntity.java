package com.supcon.mes.module_xj.model.bean;

import com.google.gson.annotations.SerializedName;
import com.supcon.common.com_http.BaseEntity;

/**
 * @author yangkai
 * @email yangkai2@supcon.com
 * Date: 2020/8/4 16:50
 */
public class GetLocationSystemSetEntity extends BaseEntity {

    @SerializedName("patrol.isLocation")
    private boolean isLocation;//开启人员GPS定位   true 是 false否


    @SerializedName("patrol.locationInterval")
    private long locationInterval;//GPS定位时间间隔
}
