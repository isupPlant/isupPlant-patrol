package com.supcon.mes.module_xj.model.bean;

import com.supcon.common.com_http.BaseEntity;

import java.util.List;

/**
 * 巡检本地缓存实体类，只存必要数据，为上传做准备
 * Created by wangshizhan on 2020/3/24
 * Email:wangshizhan@supcom.com
 */
public class XJUploadEntity extends BaseEntity {

    public List<XJTaskUploadEntity> uploadTaskResultDTOs;
}
