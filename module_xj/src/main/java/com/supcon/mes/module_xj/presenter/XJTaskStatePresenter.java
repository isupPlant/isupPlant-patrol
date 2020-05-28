package com.supcon.mes.module_xj.presenter;

import com.supcon.mes.middleware.model.bean.BAP5CommonEntity;
import com.supcon.mes.module_xj.model.contract.XJTaskStateContract;
import com.supcon.mes.module_xj.model.network.XJHttpClient;

import java.util.Map;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by wangshizhan on 2020/4/10
 * Email:wangshizhan@supcom.com
 */
public class XJTaskStatePresenter extends XJTaskStateContract.Presenter {
    @Override
    public void updateTaskState(Map<String, Object> queryMap) {
        mCompositeSubscription.add(XJHttpClient.taskStateUpdate(queryMap)
                .onErrorReturn(new Function<Throwable, BAP5CommonEntity<String>>() {
                    @Override
                    public BAP5CommonEntity<String> apply(Throwable throwable) throws Exception {
                        BAP5CommonEntity bap5CommonEntity = new BAP5CommonEntity();
                        bap5CommonEntity.success = false;
                        bap5CommonEntity.msg = throwable.toString();
                        return bap5CommonEntity;
                    }
                })
                .subscribe(new Consumer<BAP5CommonEntity<String>>() {
                    @Override
                    public void accept(BAP5CommonEntity<String> commonEntity) throws Exception {
                        if(commonEntity.success){
                            getView().updateTaskStateSuccess();
                        }
                        else{
                            getView().updateTaskStateFailed(commonEntity.msg);
                        }
                    }
                }));
    }
}
