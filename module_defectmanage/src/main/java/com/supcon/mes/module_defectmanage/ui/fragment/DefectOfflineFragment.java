package com.supcon.mes.module_defectmanage.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.app.annotation.BindByTag;
import com.app.annotation.Controller;
import com.jakewharton.rxbinding2.view.RxView;
import com.supcon.common.view.base.adapter.IListAdapter;
import com.supcon.common.view.base.fragment.BaseRefreshRecyclerFragment;
import com.supcon.common.view.listener.OnRefreshListener;
import com.supcon.common.view.util.ToastUtils;
import com.supcon.mes.mbap.view.CustomDialog;
import com.supcon.mes.middleware.IntentRouter;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.model.event.BaseEvent;
import com.supcon.mes.middleware.util.EmptyAdapterHelper;
import com.supcon.mes.module_defectmanage.R;
import com.supcon.mes.module_defectmanage.controller.BathUploadDefectController;
import com.supcon.mes.module_defectmanage.model.bean.DefectModelEntity;
import com.supcon.mes.module_defectmanage.model.bean.DefectModelEntityDao;
import com.supcon.mes.module_defectmanage.ui.DefectOfflineListActivity;
import com.supcon.mes.module_defectmanage.ui.adapter.DefectAddInfoAdapter;
import com.supcon.mes.module_defectmanage.util.DatabaseManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;


/**
 * Time:    2020/7/21  19: 35
 * Author： nina
 * Des:
 */
@Controller(value = BathUploadDefectController.class)
public class DefectOfflineFragment extends BaseRefreshRecyclerFragment<DefectModelEntity> {
    @BindByTag("llt_buttom")
    LinearLayout llt_buttom;
    @BindByTag("contentView")
    RecyclerView contentView;
    @BindByTag("all")
    Button all;
    @BindByTag("delete")
    Button delete;
    @BindByTag("upload")
    Button upload;


    DefectAddInfoAdapter adapter;
    Map<Long, DefectModelEntity> checkedMap = new HashMap<>();
    String tableNo, areaCode;

    @Override
    protected int getLayoutID() {
        return R.layout.frag_defect_offline_list;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDataFromDb(tableNo);
    }

    @Override
    protected void initListener() {
        super.initListener();


//        Disposable disposable = RxView.clicks(all)
//                .throttleFirst(200, TimeUnit.MILLISECONDS)
//                .subscribe(o -> {
//                    //检查有没有选过的，如果有，就清空；如果没有就全选
//                    if (checkedMap != null && checkedMap.size() > 0) {
//                        setAllBtn(false);
//                        checkedMap.clear();
//                    } else {
//
//                        setAllBtn(true);
//                    }
//
//                    adapter.setCheckedMap(checkedMap);
//                });

        Disposable disposableSave = RxView.clicks(delete)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    if (checkedMap.size() < 1) {
                        ToastUtils.show(context, R.string.defect_data_choose_is_null);
                        return;
                    }
                    //弹出提示框，提示是否真的删除，如果真的删除？
                    new CustomDialog(context)
                            .twoButtonAlertDialog(getString(R.string.defect_delete_choosed_defect_list))
                            .bindView(R.id.grayBtn, getString(R.string.no))
                            .bindView(R.id.redBtn, getString(R.string.yes))
                            .bindClickListener(R.id.grayBtn, null, true)
                            .bindClickListener(R.id.redBtn, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    List<DefectModelEntity> list = new ArrayList<>();
                                    for (DefectModelEntity value : checkedMap.values()) {
                                        list.add(value);
                                    }

                                    DatabaseManager.getDao().getDefectModelEntityDao().deleteInTx(list);

                                    //重制UI
                                    checkedMap.clear();
                                    updateCheckStatus();
                                    loadDataFromDb(tableNo);
                                }
                            }, true)
                            .show();
                });

        Disposable disposableList = RxView.clicks(upload)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    if (checkedMap.size() < 1) {
                        ToastUtils.show(context, R.string.defect_data_choose_is_null);
                        return;
                    }
                    submit();
                });

        adapter.setCheckedMap(checkedMap);
        adapter.setListener(new DefectAddInfoAdapter.OnclickListener(){
            @Override
            public void clickCheckButton(DefectModelEntity entity) {
                //选中
                if (checkedMap.containsKey(entity.dbId)) {
                    checkedMap.remove(entity.dbId);
                } else {
                    checkedMap.put(entity.dbId, entity);
                }

                updateCheckStatus();
            }

            @Override
            public void clickItem(DefectModelEntity entity) {
                //调转到详情中
                Bundle bundle = new Bundle();
                bundle.putLong(Constant.INTENT_EXTRA_ID, entity.dbId);
                IntentRouter.go(context, Constant.AppCode.DEFECT_MANAGEMENT_ADD, bundle);
            }
        });

    }

    /**
     * 提交数据，提交失败后，提示是否保存在本地，保存在本地的数据都可以编辑
     */
    private void submit() {
        List<DefectModelEntity> list = new ArrayList<>();
        for (DefectModelEntity value : checkedMap.values()) {
            list.add(value);
        }

        getController(BathUploadDefectController.class).bathUploadDefectList(list);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataSelectNode(BaseEvent event) {
        if (event != null && event.isSuccess()) {
            checkedMap.clear();
            updateCheckStatus();
            loadDataFromDb(tableNo);
        }
    }


    @Override
    protected IListAdapter<DefectModelEntity> createAdapter() {
        adapter = new DefectAddInfoAdapter(context);
        return adapter;
    }

    /**
     * 从数据库中获取数据
     */
    public void loadDataFromDb(String tableNo) {
        if (tableNo != null) {
            List<DefectModelEntity> list = DatabaseManager.getDao().getDefectModelEntityDao().queryBuilder()
                    .where(DefectModelEntityDao.Properties.TableNo.eq(tableNo)).list();
//                    .where(DefectModelEntityDao.Properties.AreaCode.eq(areaCode)).list();
            if (list == null) {
                list = new ArrayList<>();
            }
            refreshListController.refreshComplete(list);
        } else {
            refreshListController.refreshComplete(new ArrayList<>());

        }
    }


    @Override
    protected void onInit() {
        super.onInit();

        contentView.setLayoutManager(new LinearLayoutManager(context));

        refreshListController.setEmpterAdapter(EmptyAdapterHelper.getRecyclerEmptyAdapter(context, getString(R.string.safety_no_search_data)));
        refreshListController.setLoadMoreEnable(false);
        refreshListController.setPullDownRefreshEnabled(false);
        refreshListController.setAutoPullDownRefresh(false);
        refreshListController.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });

        EventBus.getDefault().register(this);
    }

    public void setInfo(String tableNo, String areaCode) {
        this.tableNo = tableNo;
        this.areaCode = areaCode;
    }

    public void setAllChoose(boolean chooseAll) {
        if (!chooseAll) {
            checkedMap.clear();
            llt_buttom.setVisibility(View.GONE);
        } else {
            List<DefectModelEntity> allList = adapter.getList();
            for (DefectModelEntity defectModelEntity : allList) {
                checkedMap.put(defectModelEntity.dbId, defectModelEntity);
            }
            llt_buttom.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    private void updateCheckStatus() {
        if (checkedMap.size() < 1) {
            setAllChoose(false);
            //通知标题
            if (context instanceof DefectOfflineListActivity) {
                ((DefectOfflineListActivity) context).setRightButton(false);
            }
            llt_buttom.setVisibility(View.GONE);

        } else if (checkedMap.size() == adapter.getList().size()) {
            setAllChoose(true);

            if (context instanceof DefectOfflineListActivity) {
                ((DefectOfflineListActivity) context).setRightButton(true);
            }
            llt_buttom.setVisibility(View.VISIBLE);
        } else {
            if (context instanceof DefectOfflineListActivity) {
                ((DefectOfflineListActivity) context).setRightButton(false);
            }
            llt_buttom.setVisibility(View.VISIBLE);

        }
        adapter.notifyDataSetChanged();
    }

}
