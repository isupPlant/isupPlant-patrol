package com.supcon.mes.module_xj.presenter;

import com.supcon.mes.middleware.model.bean.BAP5CommonListEntity;
import com.supcon.mes.middleware.model.bean.CommonBAPListEntity;
import com.supcon.mes.middleware.model.bean.CommonListEntity;
import com.supcon.mes.module_xj.model.bean.DeviceDCSEntity;
import com.supcon.mes.module_xj.model.contract.DeviceDCSParamQueryContract;
import com.supcon.mes.module_xj.model.network.XJHttpClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.Function;

/**
 * @author yangkai
 * @email yangkai2@supcon.com
 * Date: 2020/8/17 9:28
 */
public class DeviceDCSParamQueryPresenter extends DeviceDCSParamQueryContract.Presenter {
    @Override
    public void getDeviceDCSParams(List<String> itemNumber) {
        Map<String,Object> map=new HashMap<>();
        map.put("tagNames",itemNumber);
        mCompositeSubscription.add(
                XJHttpClient.getDeviceDCSParam(map)
                        .onErrorReturn(new Function<Throwable, BAP5CommonListEntity<DeviceDCSEntity>>() {
                            @Override
                            public BAP5CommonListEntity<DeviceDCSEntity> apply(Throwable throwable) throws Exception {
                                BAP5CommonListEntity<DeviceDCSEntity> commonListEntity = new BAP5CommonListEntity<>();
                                commonListEntity.success = false;
                                commonListEntity.msg = throwable.toString();
                                return commonListEntity;
                            }
                        })
                        .subscribe(entity -> {
                            if (entity.success) {
                                getView().getDeviceDCSParamsSuccess(entity);
                            } else {
                                getView().getDeviceDCSParamsFailed(entity.msg);
                            }
                        }));
    }
}
