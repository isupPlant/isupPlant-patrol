package com.supcon.mes.module_xj.controller;

import android.annotation.SuppressLint;
import android.content.Context;

import com.app.annotation.Presenter;
import com.supcon.common.view.base.controller.BaseDataController;
import com.supcon.common.view.util.LogUtil;
import com.supcon.mes.middleware.model.event.RefreshEvent;
import com.supcon.mes.middleware.model.listener.OnAPIResultListener;
import com.supcon.mes.module_xj.model.api.XJLocalTaskAPI;
import com.supcon.mes.module_xj.model.bean.XJTaskEntity;
import com.supcon.mes.module_xj.model.contract.XJLocalTaskContract;
import com.supcon.mes.module_xj.presenter.XJLocalTaskPresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * Created by wangshizhan on 2020/4/9
 * Email:wangshizhan@supcom.com
 */
@Presenter(XJLocalTaskPresenter.class)
public class XJTaskUploadController extends BaseDataController implements XJLocalTaskContract.View {


    private List<XJTaskEntity> mUploadTasks = new ArrayList<>();

    private OnAPIResultListener<Integer> mOnResultListener;

    private boolean needRefresh = false;

    public XJTaskUploadController(Context context) {
        super(context);
    }


    @Override
    public void onInit() {
        super.onInit();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefresh(RefreshEvent refreshEvent) {
        needRefresh = true;
    }

    @Override
    public void initData() {
        super.initData();
        presenterRouter.create(XJLocalTaskAPI.class).getLocalTask(new HashMap<>());
    }


    @Override
    public void onResume() {
        super.onResume();
        if(needRefresh){
            presenterRouter.create(XJLocalTaskAPI.class).getLocalTask(new HashMap<>());
        }
    }

    public void setOnResultListener(OnAPIResultListener<Integer> onResultListener) {
        mOnResultListener = onResultListener;
    }

    public List<XJTaskEntity> getUploadTasks() {
        return mUploadTasks;
    }

    @SuppressLint("CheckResult")
    @Override
    public void getLocalTaskSuccess(List entity) {
        mUploadTasks.clear();
        List<XJTaskEntity> localTaskEntities = entity;
        Flowable.fromIterable(localTaskEntities)
                .subscribe(new Consumer<XJTaskEntity>() {
                    @Override
                    public void accept(XJTaskEntity xjLocalTaskEntity) throws Exception {
                        if (xjLocalTaskEntity.isFinished) {
                            mUploadTasks.add(xjLocalTaskEntity);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        if(mOnResultListener!=null){
                            mOnResultListener.onSuccess(mUploadTasks.size());
                        }
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
}
