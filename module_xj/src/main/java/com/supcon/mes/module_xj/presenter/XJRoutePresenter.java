package com.supcon.mes.module_xj.presenter;

import com.supcon.mes.middleware.SupPlantApplication;
import com.supcon.mes.middleware.model.bean.xj.XJRouteEntity;
import com.supcon.mes.middleware.model.bean.xj.XJRouteEntityDao;
import com.supcon.mes.module_xj.R;
import com.supcon.mes.module_xj.model.contract.XJRouteContract;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by wangshizhan on 2020/5/30
 * Email:wangshizhan@supcom.com
 */
public class XJRoutePresenter extends XJRouteContract.Presenter {
    @Override
    public void getRouteList() {
        mCompositeSubscription.add(
                Flowable.timer(200, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Exception {
                                List<XJRouteEntity> routeEntities = SupPlantApplication.dao().getXJRouteEntityDao().queryBuilder()
                                        .where(XJRouteEntityDao.Properties.Valid.eq(true), XJRouteEntityDao.Properties.IsRun.eq(true))
                                        .list();

                                if(routeEntities!=null && routeEntities.size()!=0){
                                    XJRoutePresenter.this.getView().getRouteListSuccess(routeEntities);
                                }
                                else{
                                    getView().getRouteListFailed(SupPlantApplication.getAppContext().getString(R.string.xj_patrol_no_route));
                                }
                            }
                        })
        );
    }
}
