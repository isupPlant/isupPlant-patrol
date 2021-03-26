package com.supcon.mes.module_xj.model.bean;

import com.supcon.common.com_http.BaseEntity;
import com.supcon.mes.middleware.model.bean.ObjectEntity;
import com.supcon.mes.middleware.model.bean.SystemCodeEntity;
import com.supcon.mes.middleware.model.converter.SystemCodeConverter;

import org.greenrobot.greendao.annotation.Convert;

/**
 * 巡检本地缓存实体类，只存必要数据，为上传做准备
 * Created by wangshizhan on 2020/3/24
 * Email:wangshizhan@supcom.com
 */
public class XJWorkUploadEntity extends BaseEntity {

    public long id;
    public String concluse;
    public StringIdEntity realValue;
    public StringIdEntity passReason;
    public StringIdEntity taskDetailState;
    public boolean isRealPass;
    public boolean isRealPhoto;
    public String xjImgName;
    public long completeDate;
    public String remark;
    @Convert(converter = SystemCodeConverter.class, columnType = String.class)
    public SystemCodeEntity abnormalReason;// 异常原因（系统编码）


    public String reason  ;//原因（字符串）
}
