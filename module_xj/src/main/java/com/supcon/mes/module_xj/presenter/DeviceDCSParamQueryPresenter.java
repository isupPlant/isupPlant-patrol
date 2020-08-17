package com.supcon.mes.module_xj.presenter;

import com.supcon.mes.middleware.model.bean.CommonListEntity;
import com.supcon.mes.middleware.model.network.MiddlewareHttpClient;
import com.supcon.mes.module_xj.model.bean.DeviceDCSEntity;
import com.supcon.mes.module_xj.model.contract.DeviceDCSParamQueryContract;
import com.supcon.mes.module_xj.model.network.XJHttpClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.Consumer;
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
                        .onErrorReturn(new Function<Throwable, CommonListEntity<DeviceDCSEntity>>() {
                            @Override
                            public CommonListEntity<DeviceDCSEntity> apply(Throwable throwable) throws Exception {
                                CommonListEntity<DeviceDCSEntity> commonListEntity = new CommonListEntity<>();
                                commonListEntity.success = false;
                                commonListEntity.errMsg = throwable.toString();
                                return commonListEntity;
                            }
                        })
                        .subscribe(new Consumer<CommonListEntity<DeviceDCSEntity>>() {
                            @Override
                            public void accept(CommonListEntity<DeviceDCSEntity> deviceDCSEntityCommonListEntity) throws Exception {
                                if (getView() != null) {
                                    if (deviceDCSEntityCommonListEntity.success) {
                                        getView().getDeviceDCSParamsSuccess(deviceDCSEntityCommonListEntity);
                                    } else {
                                        getView().getDeviceDCSParamsFailed(deviceDCSEntityCommonListEntity.errMsg);
                                    }
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                getView().getDeviceDCSParamsFailed(throwable.toString());
                            }
                        }));
    }
}
