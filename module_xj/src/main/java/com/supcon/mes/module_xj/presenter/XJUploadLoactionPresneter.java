package com.supcon.mes.module_xj.presenter;

import com.supcon.mes.middleware.model.bean.BAP5CommonEntity;
import com.supcon.mes.middleware.model.bean.CommonBAP5ListEntity;
import com.supcon.mes.middleware.model.bean.CommonEntity;
import com.supcon.mes.module_xj.model.contract.XJRealTimeUploadLoactionContract;
import com.supcon.mes.module_xj.model.network.XJHttpClient;

import java.util.Map;

import io.reactivex.functions.Function;

/**
 * @author yangkai
 * @email yangkai2@supcon.com
 * Date: 2020/8/4 11:13
 */
public class XJUploadLoactionPresneter extends XJRealTimeUploadLoactionContract.Presenter {
    @Override
    public void uploadLoaction(Map<String, Object> queryParams) {
        mCompositeSubscription.add(XJHttpClient.uploadLoaction(queryParams).onErrorReturn( new Function<Throwable, BAP5CommonEntity>() {
                    @Override
                    public BAP5CommonEntity apply(Throwable throwable) throws Exception {
                        BAP5CommonEntity entity = new BAP5CommonEntity();
                        entity.success = false;
                        entity.msg = throwable.getMessage();
                        return entity;
                    }
                }).subscribe(entity -> {
                    if (entity.success) {
                        getView().uploadLoactionSuccess(entity);
                    } else {
                        getView().uploadLoactionFailed(entity.msg);
                    }
                })
        );



    }
}
