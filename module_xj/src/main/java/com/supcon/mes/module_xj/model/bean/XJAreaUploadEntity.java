package com.supcon.mes.module_xj.model.bean;

import com.supcon.common.com_http.BaseEntity;
import com.supcon.mes.middleware.model.bean.SystemCodeEntity;

/**
 * 巡检本地缓存实体类，只存必要数据，为上传做准备
 * Created by wangshizhan on 2020/3/24
 * Email:wangshizhan@supcom.com
 */
public class XJAreaUploadEntity extends BaseEntity {

    /**
     * {
     * 				"id" : 1003,
     * 				"payCardType": {
     * 					"id": "PATROL_payCardType/signIn"
     *                                },
     * 				"cardTime": 1585640716459,
     * 				"signInReason": {
     * 					"id":"PATROL_signInType/notPosted"
     *                }* 			}
     */

    public long id;
    public StringIdEntity payCardType;
    public long cardTime;
    public long completeTime;
    public StringIdEntity signInReason;
}
