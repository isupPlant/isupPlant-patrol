package com.supcon.mes.module_xj.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.ArrayMap;

import com.app.annotation.Presenter;
import com.supcon.common.view.base.controller.BaseDataController;
import com.supcon.mes.middleware.util.XJCacheUtil;
import com.supcon.mes.module_xj.model.api.XJLocalTaskAPI;
import com.supcon.mes.module_xj.model.bean.XJTaskEntity;
import com.supcon.mes.module_xj.model.contract.XJLocalTaskContract;
import com.supcon.mes.module_xj.model.event.XJAreaRefreshEvent;
import com.supcon.mes.module_xj.model.event.XJTaskRefreshEvent;
import com.supcon.mes.module_xj.presenter.XJLocalTaskPresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wangshizhan on 2020/4/8
 * Email:wangshizhan@supcom.com
 */
@Presenter(XJLocalTaskPresenter.class)
public class XJLocalTaskController extends BaseDataController implements XJLocalTaskContract.View {

    private Map<String, XJTaskEntity> mXJLocalTaskMap = new ArrayMap<>();

    public XJLocalTaskController(Context context) {
        super(context);
    }

    @Override
    public void onInit() {
        super.onInit();

        EventBus.getDefault().register(this);
    }



    @SuppressLint("CheckResult")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTaskUpdate(XJTaskRefreshEvent xjTaskRefreshEvent) {
        presenterRouter.create(XJLocalTaskAPI.class).getLocalTask(new HashMap<>());

    }

    @Override
    public void initListener() {
        super.initListener();
    }

    @Override
    public void initData() {
        super.initData();

    }

    @Override
    public void onResume() {
        super.onResume();
        presenterRouter.create(XJLocalTaskAPI.class).getLocalTask(new HashMap<>());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @SuppressLint("CheckResult")
    @Override
    public void getLocalTaskSuccess(List entity) {
        List<XJTaskEntity> tasks = entity;

        Flowable.fromIterable(tasks)
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<XJTaskEntity>() {
                    @Override
                    public void accept(XJTaskEntity taskEntity) throws Exception {
                        mXJLocalTaskMap.put(String.valueOf(taskEntity.id), taskEntity);

                    }
                });

    }

    @Override
    public void getLocalTaskFailed(String errorMsg) {

    }

    @Override
    public void saveLocalTaskSuccess() {

    }

    @Override
    public void saveLocalTaskFailed(String errorMsg) {

    }

    public XJTaskEntity getLocalTask(String tableNo){

        if(mXJLocalTaskMap.containsKey(tableNo)){
            return mXJLocalTaskMap.get(tableNo);
        }


        return null;
    }


    public void save(XJTaskEntity xjLocalTaskEntity){
        String key  = xjLocalTaskEntity.tableNo;
        mXJLocalTaskMap.put(key, xjLocalTaskEntity);
        XJCacheUtil.putString(key, xjLocalTaskEntity.toString());
    }

    public void add(XJTaskEntity xjLocalTaskEntity){
        String key  = xjLocalTaskEntity.tableNo;
        mXJLocalTaskMap.put(key, xjLocalTaskEntity);
        XJCacheUtil.putString(key, xjLocalTaskEntity.toString());
    }
}
