package com.supcon.mes.module_xj.model.event;

import com.supcon.mes.middleware.model.bean.xj.XJAreaEntity;
import com.supcon.mes.module_xj.model.bean.XJTaskEntity;
import com.supcon.mes.sb2.model.event.BaseEvent;

/**
 * @author yangkai
 * @email yangkai2@supcon.com
 * Date: 2020/10/16 17:08
 */
public class XJTempTaskUploadRefreshEvent extends BaseEvent {
    private Long id;
    public XJTempTaskUploadRefreshEvent(Long id){
        this.id=id;
    }

    public Long getId() {
        return id;
    }
}
