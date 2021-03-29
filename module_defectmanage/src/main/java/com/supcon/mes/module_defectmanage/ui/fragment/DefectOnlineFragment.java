package com.supcon.mes.module_defectmanage.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.app.annotation.BindByTag;
import com.app.annotation.Presenter;
import com.supcon.common.view.base.adapter.IListAdapter;
import com.supcon.common.view.base.fragment.BaseRefreshRecyclerFragment;
import com.supcon.mes.middleware.IntentRouter;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.model.bean.BAP5CommonEntity;
import com.supcon.mes.middleware.util.EmptyAdapterHelper;
import com.supcon.mes.module_defectmanage.R;
import com.supcon.mes.module_defectmanage.model.api.GetDefectListAPI;
import com.supcon.mes.module_defectmanage.model.bean.DefectOnlineEntity;
import com.supcon.mes.module_defectmanage.model.contract.GetDefectListContract;
import com.supcon.mes.module_defectmanage.presenter.GetDefectListPresenter;
import com.supcon.mes.module_defectmanage.ui.adapter.DefectOnlineAdapter;
import com.supcon.mes.module_defectmanage.util.Utils;

import java.util.List;


/**
 * Time:    2020/7/21  19: 35
 * Author： nina
 * Des:
 */
@Presenter(GetDefectListPresenter.class)
public class DefectOnlineFragment extends BaseRefreshRecyclerFragment<DefectOnlineEntity> implements GetDefectListContract.View {
    @BindByTag("contentView")
    RecyclerView contentView;
    DefectOnlineAdapter adapter;

    String tableNo, areaCode;

    @Override
    protected IListAdapter<DefectOnlineEntity> createAdapter() {
        adapter =  new DefectOnlineAdapter(context);
        return adapter;
    }

    @Override
    protected int getLayoutID() {
        return R.layout.frag_defect_list;
    }

    @Override
    protected void onInit() {
        super.onInit();
        refreshListController.setEmpterAdapter(EmptyAdapterHelper.getRecyclerEmptyAdapter(context, getString(R.string.safety_no_search_data)));
        refreshListController.setLoadMoreEnable(true);
        refreshListController.setAutoPullDownRefresh(true);
        refreshListController.setPullDownRefreshEnabled(true);
        refreshListController.setOnRefreshPageListener(pageIndex -> doFilter(pageIndex));
    }

    private void doFilter(int pageIndex) {
        presenterRouter.create(GetDefectListAPI.class).getDefectList(tableNo, areaCode, pageIndex);
    }

    public void refreshList() {
        if(refreshListController!=null)
            refreshListController.refreshBegin();
    }


    @Override
    protected void initView() {
        super.initView();
        contentView.setLayoutManager(new LinearLayoutManager(context));
    }

    @Override
    protected void initListener() {
        super.initListener();
        adapter.setOnItemChildViewClickListener((childView, position, action, obj) -> {
            DefectOnlineEntity item = ((DefectOnlineEntity) obj);
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constant.INTENT_EXTRA_OBJECT, item);
            IntentRouter.go(context, Utils.AppCode.DEFECT_MANAGEMENT_ON_LINE_DETAIL,bundle);
        });
    }

    @Override
    protected void initData() {
        super.initData();

    }

    public void setInfo(String tableNo, String areaCode) {
        this.areaCode = areaCode;
        this.tableNo = tableNo;
    }


    @Override
    public void onResume() {
        super.onResume();

        refreshList();
    }

    @Override
    public void getDefectListSuccess(BAP5CommonEntity entity) {

        if (entity != null) {
            refreshListController.refreshComplete((List<DefectOnlineEntity>) entity.data);
        } else {
            refreshListController.refreshComplete();
        }
    }

    @Override
    public void getDefectListFailed(String errorMsg) {
        refreshListController.refreshComplete();
    }
}
