package com.supcon.mes.module_xj.ui;

import android.annotation.SuppressLint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

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
import com.supcon.mes.middleware.model.bean.BAP5CommonListEntity;
import com.supcon.mes.middleware.model.bean.xj.XJRouteEntity;
import com.supcon.mes.middleware.model.event.SelectDataEvent;
import com.supcon.mes.middleware.util.ErrorMsgHelper;
import com.supcon.mes.module_xj.R;
import com.supcon.mes.module_xj.model.api.LSXJRouterAPI;
import com.supcon.mes.module_xj.model.api.XJRouteAPI;
import com.supcon.mes.module_xj.model.bean.LSXJRouterEntity;
import com.supcon.mes.module_xj.model.contract.LSXJRouterContract;
import com.supcon.mes.module_xj.model.contract.XJRouteContract;
import com.supcon.mes.module_xj.presenter.LSXJRouterPresenter;
import com.supcon.mes.module_xj.presenter.XJRoutePresenter;
import com.supcon.mes.module_xj.ui.adapter.XJRouteAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wangshizhan on 2020/5/30
 * Email:wangshizhan@supcom.com
 */
@Router(Constant.Router.XJ_ROUTE_LIST)
@Presenter(value = {XJRoutePresenter.class, LSXJRouterPresenter.class})
public class XJRouteListActivity extends BaseRefreshRecyclerActivity implements XJRouteContract.View, LSXJRouterContract.View {

    @BindByTag("titleText")
    TextView titleText;

    @BindByTag("contentView")
    RecyclerView contentView;
    private List<XJRouteEntity> xjRouteEntityList;
    XJRouteAdapter mXJRouteAdapter;
    private long eamId;

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
        eamId = getIntent().getLongExtra(Constant.IntentKey.SBDA_ONLINE_EAMID, -1);
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
        titleText.setText("巡检路线选择");
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
        xjRouteEntityList = entity;
        if (eamId != -1) {
            presenterRouter.create(LSXJRouterAPI.class).queryRouteList(eamId);
        } else {
            refreshListController.refreshComplete(entity);
        }

    }

    @Override
    public void getRouteListFailed(String errorMsg) {
        refreshListController.refreshComplete(null);
        LogUtil.e(ErrorMsgHelper.msgParse(errorMsg));
    }


    List<XJRouteEntity> xjData = new ArrayList<>();

    @SuppressLint("CheckResult")
    @Override
    public void queryRouteListSuccess(BAP5CommonListEntity entity) {
        List<LSXJRouterEntity> lsxjRouterEntityList = entity.data;
        //获取当前设备相关联的巡检路线
        Flowable.fromIterable(xjRouteEntityList)
                .subscribeOn(Schedulers.newThread())
                .filter(new Predicate<XJRouteEntity>() {
                    @Override
                    public boolean test(XJRouteEntity xjRouteEntity) throws Exception {
                        if (lsxjRouterEntityList.size() == 0) {
                            return false;
                        } else {
                            for (int i = 0; i < lsxjRouterEntityList.size(); i++) {
                                if (xjRouteEntity.id != null && lsxjRouterEntityList.get(i).id != null &&
                                        (xjRouteEntity.id.longValue() == lsxjRouterEntityList.get(i).id.longValue())) {
                                    return true;
                                }
                            }
                            return false;
                        }

                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<XJRouteEntity>() {
                    @Override
                    public void accept(XJRouteEntity xjRouteEntity) throws Exception {
                        xjData.add(xjRouteEntity);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        refreshListController.refreshComplete(xjData);
                    }
                });

    }

    @Override
    public void queryRouteListFailed(String errorMsg) {
        refreshListController.refreshComplete(null);
        LogUtil.e(ErrorMsgHelper.msgParse(errorMsg));
    }
}
