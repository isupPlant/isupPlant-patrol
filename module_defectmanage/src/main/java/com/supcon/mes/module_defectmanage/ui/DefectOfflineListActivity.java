package com.supcon.mes.module_defectmanage.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.supcon.common.view.util.ToastUtils;
import com.supcon.mes.mbap.utils.StatusBarUtils;
import com.supcon.mes.mbap.view.CustomDialog;
import com.supcon.mes.middleware.IntentRouter;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.model.bean.BAP5CommonEntity;
import com.supcon.mes.middleware.util.EmptyAdapterHelper;
import com.supcon.mes.module_defectmanage.R;
import com.supcon.mes.module_defectmanage.model.api.AddDefectAPI;
import com.supcon.mes.module_defectmanage.model.bean.DefectModelEntity;
import com.supcon.mes.module_defectmanage.model.bean.DefectModelEntityDao;
import com.supcon.mes.module_defectmanage.model.contract.AddDefectContract;
import com.supcon.mes.module_defectmanage.presenter.AddDefectPresenter;
import com.supcon.mes.module_defectmanage.ui.adapter.DefectAddInfoAdapter;
import com.supcon.mes.module_defectmanage.util.DatabaseManager;
import com.supcon.mes.module_defectmanage.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;


@Presenter(value = AddDefectPresenter.class)
@Router(value = Utils.AppCode.DEFECT_MANAGEMENT_OFF_LINE_LIST)
public class DefectOfflineListActivity extends BaseRefreshRecyclerActivity<DefectModelEntity> implements AddDefectContract.View {
    @BindByTag("leftBtn")
    ImageButton leftBtn;
    @BindByTag("llt_buttom")
    LinearLayout llt_buttom;
    @BindByTag("contentView")
    RecyclerView contentView;
    @BindByTag("rightBtn_text")
    Button rightBtn_text;
    @BindByTag("all")
    Button all;
    @BindByTag("delete")
    Button delete;
    @BindByTag("upload")
    Button upload;
    @BindByTag("titleText")
    TextView titleText;

    DefectAddInfoAdapter adapter;
    Map<Long, DefectModelEntity> checkedMap = new HashMap<>();
    Long tableNo;

    @Override
    protected int getLayoutID() {
        return R.layout.ac_defect_off_line_list;
    }

    @Override
    protected void initData() {
        super.initData();

        //情况分类：1、从巡检传过来的数据2、从列表中过来的某一条数据
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            tableNo = bundle.getLong(Constant.INTENT_EXTRA_OBJECT_CHAT);
            if (tableNo != null && tableNo.longValue() > 0) {
//                loadDataFromDb(tableNo);
            }
        }
    }

    @Override
    protected void initView() {
        super.initView();

        StatusBarUtils.setWindowStatusBarColor(this, R.color.themeColor);

        contentView.setLayoutManager(new LinearLayoutManager(context));

        titleText.setText(R.string.defect_off_line_list_title);
        rightBtn_text.setVisibility(View.VISIBLE);
        rightBtn_text.setText(getString(R.string.defect_choose));
    }

    @Override
    protected void initListener() {
        super.initListener();

        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        Disposable disposableRight = RxView.clicks(rightBtn_text)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    if (llt_buttom.getVisibility() == View.VISIBLE) {
                        llt_buttom.setVisibility(View.GONE);
                        checkedMap.clear();

                        setAllBtn(false);
                        adapter.setCheckedMap(checkedMap);
                        adapter.setChoosing(false);
                    } else {
                        llt_buttom.setVisibility(View.VISIBLE);
                        adapter.setChoosing(true);
                    }
                });

        Disposable disposable = RxView.clicks(all)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    //检查有没有选过的，如果有，就清空；如果没有就全选
                    if (checkedMap != null && checkedMap.size() > 0) {
                        setAllBtn(false);
                        checkedMap.clear();
                    } else {
                        List<DefectModelEntity> allList = adapter.getList();
                        for (DefectModelEntity defectModelEntity : allList) {
                            checkedMap.put(defectModelEntity.dbId, defectModelEntity);
                        }
                        setAllBtn(true);
                    }

                    adapter.setCheckedMap(checkedMap);
                });

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
                                    llt_buttom.setVisibility(View.GONE);
                                    checkedMap.clear();
                                    setAllBtn(false);
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
        adapter.setOnItemChildViewClickListener(new OnItemChildViewClickListener() {
            @Override
            public void onItemChildViewClick(View childView, int position, int action, Object obj) {
                DefectModelEntity entity = (DefectModelEntity) obj;

                if (llt_buttom.getVisibility() == View.VISIBLE) {
                    //选中
                    if (checkedMap.containsKey(entity.dbId)) {
                        checkedMap.remove(entity.dbId);
                    } else {
                        checkedMap.put(entity.dbId, entity);
                    }

                    updateCheckStatus();
                } else {
                    //调转到详情中
                    Bundle bundle = new Bundle();
                    bundle.putLong(Constant.INTENT_EXTRA_ID, entity.dbId);
                    IntentRouter.go(context, Constant.AppCode.DEFECT_MANAGEMENT_ADD, bundle);
                }
            }
        });
    }

    private void updateCheckStatus() {
        if (checkedMap.size() < 1) {
            setAllBtn(false);
        } else if (checkedMap.size() == adapter.getList().size()) {
            setAllBtn(true);
        } else {
            delete.setVisibility(View.VISIBLE);
            upload.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 提交数据，提交失败后，提示是否保存在本地，保存在本地的数据都可以编辑
     */
    private void submit() {
        if (!checkIsValid()){
            ToastUtils.show(context, R.string.defect_data_is_not_complete);
            return;
        }
        //如果有附件的话 还要添加附件的逻辑
        List<DefectModelEntity> list = new ArrayList<>();
        for (DefectModelEntity value : checkedMap.values()) {
            list.add(value);
        }
        onLoading();
        presenterRouter.create(AddDefectAPI.class).defectEntryBatch(list);
    }


    private boolean checkIsValid() {
        for (DefectModelEntity value : checkedMap.values()) {
            if (!value.isValid) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void defectEntrySuccess(BAP5CommonEntity entity) {

    }

    @Override
    public void defectEntryFailed(String errorMsg) { ;
    }

    @Override
    public void defectEntryBatchSuccess(BAP5CommonEntity entity) {
        //删除上传的数据
        List<DefectModelEntity> list = new ArrayList<>();
        for (DefectModelEntity value : checkedMap.values()) {
            list.add(value);
        }
        DatabaseManager.getDao().getDefectModelEntityDao().deleteInTx(list);
        checkedMap.clear();
        loadDataFromDb(tableNo);

        onLoadSuccess();
        ToastUtils.show(context, context.getString(R.string.submit_success));
    }

    @Override
    public void defectEntryBatchFailed(String errorMsg) {
        onLoadSuccess();
        //提示用户保存在本地，但是不能重复保存啊,数据库的id是怎么回事
        ToastUtils.show(context, context.getString(R.string.defect_submit_failed_save_to_local));
    }

    @Override
    protected IListAdapter<DefectModelEntity> createAdapter() {
        adapter = new DefectAddInfoAdapter(context);
        return adapter;
    }

    /**
     * 从数据库中获取数据
     */
    private void loadDataFromDb(Long tableNo) {
        if (tableNo != null) {
            List<DefectModelEntity> list = DatabaseManager.getDao().getDefectModelEntityDao().queryBuilder()
                .where(DefectModelEntityDao.Properties.TableNo.eq(tableNo.longValue())).list();

            if (list != null && list.size() > 0) {
                //通知页面更新
                refreshListController.refreshComplete(list);
            } else {
                refreshListController.refreshComplete(new ArrayList<>());

            }
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDataFromDb(tableNo);
    }

    @Override
    protected void onInit() {
        super.onInit();

        refreshListController.setEmpterAdapter(EmptyAdapterHelper.getRecyclerEmptyAdapter(context, getString(R.string.safety_no_search_data)));
        refreshListController.setLoadMoreEnable(false);
        refreshListController.setPullDownRefreshEnabled(false);
        refreshListController.setAutoPullDownRefresh(false);
        refreshListController.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
//                loadDataFromDb(tableNo);
            }
        });
    }

    public void refreshList() {
        if(refreshListController!=null)
            refreshListController.refreshBegin();
    }

    private void setAllBtn(boolean isAllSelected) {
        if (isAllSelected) {
            Drawable drawable = getResources().getDrawable(R.drawable.ic_check_yes);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(),drawable.getMinimumHeight());
            all.setCompoundDrawables(null, drawable, null, null);
            all.setText(getString(R.string.defect_check_plan_cancel_all));

            delete.setVisibility(View.VISIBLE);
            upload.setVisibility(View.VISIBLE);
        } else {
            delete.setVisibility(View.INVISIBLE);
            upload.setVisibility(View.INVISIBLE);

            Drawable drawable = getResources().getDrawable(R.drawable.ic_check_no);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(),drawable.getMinimumHeight());
            all.setCompoundDrawables(null, drawable, null, null);
            all.setText(getString(R.string.middleware_choose_all));
        }
    }
}
