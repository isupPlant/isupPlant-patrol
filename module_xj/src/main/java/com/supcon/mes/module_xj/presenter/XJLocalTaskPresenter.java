package com.supcon.mes.module_xj.presenter;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.supcon.mes.mbap.utils.GsonUtil;
import com.supcon.mes.middleware.SupPlantApplication;
import com.supcon.mes.middleware.util.XJCacheUtil;
import com.supcon.mes.module_xj.model.bean.XJTaskEntity;
import com.supcon.mes.module_xj.model.contract.XJLocalTaskContract;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wangshizhan on 2020/4/8
 * Email:wangshizhan@supcom.com
 */
public class XJLocalTaskPresenter extends XJLocalTaskContract.Presenter {

    @SuppressLint("CheckResult")
    @Override
    public void getLocalTask(Map<String,Object> queryMap) {
        List<String> taskNames = XJCacheUtil.getTasks(SupPlantApplication.getAppContext());

        List<XJTaskEntity> taskEntities = new ArrayList<>();
        Flowable.fromIterable(taskNames)
                .subscribeOn(Schedulers.newThread())
                .filter(s -> {
                    String cache = XJCacheUtil.getString(s.replace(".0", ""));
                    if (!TextUtils.isEmpty(cache)) {
                        return true;
                    }

                    return false;
                })
                .map(s -> {
                    String cache = XJCacheUtil.getString(s.replace(".0", ""));
                    XJTaskEntity xjLocalTaskEntity = GsonUtil.gsonToBean(cache, XJTaskEntity.class);
                    return xjLocalTaskEntity;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(taskEntities::add, throwable -> {

                }, () -> getView().getLocalTaskSuccess(taskEntities));


    }

    @Override
    public void saveLocalTask() {

    }
}
