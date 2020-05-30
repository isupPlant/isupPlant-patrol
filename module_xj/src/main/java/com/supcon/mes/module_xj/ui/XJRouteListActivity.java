package com.supcon.mes.module_xj.ui;

import android.annotation.SuppressLint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.app.annotation.BindByTag;
import com.app.annotation.Presenter;
import com.app.annotation.apt.Router;
import com.jakewharton.rxbinding2.view.RxView;
import com.supcon.common.view.base.activity.BaseRefreshRecyclerActivity;
import com.supcon.common.view.base.adapter.IListAdapter;
import com.supcon.common.view.listener.OnItemChildViewClickListener;
import com.supcon.common.view.listener.OnRefreshListener;
import com.supcon.common.view.util.LogUtil;
import com.supcon.common.view.util.StatusBarUtils;
import com.supcon.mes.mbap.utils.SpaceItemDecoration;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.model.bean.xj.XJRouteEntity;
import com.supcon.mes.middleware.model.event.SelectDataEvent;
import com.supcon.mes.middleware.util.ErrorMsgHelper;
import com.supcon.mes.module_xj.R;
import com.supcon.mes.module_xj.model.api.XJRouteAPI;
import com.supcon.mes.module_xj.model.contract.XJRouteContract;
import com.supcon.mes.module_xj.presenter.XJRoutePresenter;
import com.supcon.mes.module_xj.ui.adapter.XJRouteAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;

/**
 * Created by wangshizhan on 2020/5/30
 * Email:wangshizhan@supcom.com
 */
@Router(Constant.Router.XJ_ROUTE_LIST)
@Presenter(XJRoutePresenter.class)
public class XJRouteListActivity extends BaseRefreshRecyclerActivity implements XJRouteContract.View {

    @BindByTag("contentView")
    RecyclerView contentView;

    XJRouteAdapter mXJRouteAdapter;

    @Override
    protected IListAdapter createAdapter() {
        mXJRouteAdapter = new XJRouteAdapter(context);
        return mXJRouteAdapter;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.ac_xj_route_list;
    }

    @Override
    protected void onInit() {
        super.onInit();
        refreshListController.setAutoPullDownRefresh(true);
        refreshListController.setPullDownRefreshEnabled(false);
        refreshListController.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenterRouter.create(XJRouteAPI.class).getRouteList();
            }
        });
    }

    @Override
    protected void initView() {
        super.initView();
        StatusBarUtils.setWindowStatusBarColor(this, R.color.themeColor);
        contentView.setLayoutManager(new LinearLayoutManager(context));
        contentView.addItemDecoration(new SpaceItemDecoration(1));
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initListener() {
        super.initListener();

        RxView.clicks(findViewById(R.id.leftBtn))
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        back();
                    }
                });

        mXJRouteAdapter.setOnItemChildViewClickListener(new OnItemChildViewClickListener() {
            @Override
            public void onItemChildViewClick(View childView, int position, int action, Object obj) {

                XJRouteEntity xjRouteEntity = (XJRouteEntity) obj;
                EventBus.getDefault().post(new SelectDataEvent<>(xjRouteEntity, "selectRoute"));
                back();
            }
        });
    }

    @Override
    public void getRouteListSuccess(List entity) {
        refreshListController.refreshComplete(entity);
    }

    @Override
    public void getRouteListFailed(String errorMsg) {
        refreshListController.refreshComplete(null);
        LogUtil.e(ErrorMsgHelper.msgParse(errorMsg));
    }
}
