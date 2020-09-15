package com.supcon.mes.module_xj.presenter;

import com.supcon.mes.middleware.SupPlantApplication;
import com.supcon.mes.middleware.model.bean.BAP5CommonEntity;
import com.supcon.mes.module_xj.model.contract.XJUpdateStatusContract;
import com.supcon.mes.module_xj.model.network.XJHttpClient;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.functions.Function;

/**
 * @author yangkai
 * @email yangkai2@supcon.com
 * Date: 2020/8/5 9:12
 */
public class XJUpdateTaskStatusPresenter  extends XJUpdateStatusContract.Presenter {
    @Override
    public void updateXJTaskStatus(long taskId,String status) {
        Map<String,Object> params=new HashMap<>();
        params.put("userId", SupPlantApplication.getAccountInfo().userId);
        params.put("taskId",taskId);
        params.put("status",status);
        mCompositeSubscription.add(XJHttpClient.updateXJTaskStatus(params).onErrorReturn(new Function<Throwable, BAP5CommonEntity>() {
                    @Override
                    public BAP5CommonEntity apply(Throwable throwable) throws Exception {
                        BAP5CommonEntity entity = new BAP5CommonEntity();
                        entity.success = false;
                        entity.msg = throwable.getMessage();
                        return entity;
                    }
                }).subscribe(entity -> {
                    if (entity.success) {
                        getView().updateXJTaskStatusSuccess(entity);
                    } else {
                        getView().updateXJTaskStatusFailed(entity.msg);
                    }
                })
        );

    }
}
