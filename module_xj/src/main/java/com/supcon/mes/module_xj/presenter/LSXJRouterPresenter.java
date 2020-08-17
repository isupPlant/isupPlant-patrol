package com.supcon.mes.module_xj.presenter;

import com.supcon.mes.middleware.model.bean.BAP5CommonListEntity;
import com.supcon.mes.module_xj.model.bean.LSXJRouterEntity;
import com.supcon.mes.module_xj.model.contract.LSXJRouterContract;
import com.supcon.mes.module_xj.model.network.XJHttpClient;


import java.util.HashMap;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * @author fengjun
 * 创建日期：2018/6/14
 * 描述：MainActivity
 */
public class LSXJRouterPresenter extends LSXJRouterContract.Presenter {
    @Override
    public void queryRouteList(long eamId) {
        HashMap<String, Object> idParams = new HashMap<>();
        idParams.put("id", eamId);
        mCompositeSubscription.add(XJHttpClient.getRouteList(idParams)
                .onErrorReturn(new Function<Throwable, BAP5CommonListEntity<LSXJRouterEntity>>() {
                    @Override
                    public BAP5CommonListEntity apply(Throwable throwable) throws Exception {

                        BAP5CommonListEntity bap5CommonEntity = new BAP5CommonListEntity();
                        bap5CommonEntity.success = false;
                        bap5CommonEntity.msg = throwable.toString();
                        return bap5CommonEntity;
                    }
                })
                .subscribe(new Consumer<BAP5CommonListEntity<LSXJRouterEntity>>() {
                    @Override
                    public void accept(BAP5CommonListEntity<LSXJRouterEntity> commonBAPListEntityBAP5CommonEntity) throws Exception {
                        if (commonBAPListEntityBAP5CommonEntity.success) {
                            getView().queryRouteListSuccess(commonBAPListEntityBAP5CommonEntity);
                        } else {
                            getView().queryRouteListFailed(commonBAPListEntityBAP5CommonEntity.msg);
                        }
                    }
                }));
    }
}
