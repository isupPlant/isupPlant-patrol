package com.supcon.mes.module_xj.ui;

import android.annotation.SuppressLint;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.annotation.BindByTag;
import com.app.annotation.Controller;
import com.app.annotation.Presenter;
import com.app.annotation.apt.Router;
import com.jakewharton.rxbinding2.view.RxView;
import com.supcon.common.view.base.activity.BaseRefreshRecyclerActivity;
import com.supcon.common.view.base.adapter.IListAdapter;
import com.supcon.common.view.listener.OnItemChildViewClickListener;
import com.supcon.common.view.util.DisplayUtil;
import com.supcon.common.view.util.LogUtil;
import com.supcon.common.view.util.StatusBarUtils;
import com.supcon.common.view.util.ToastUtils;
import com.supcon.mes.mbap.utils.GsonUtil;
import com.supcon.mes.mbap.utils.SpaceItemDecoration;
import com.supcon.mes.mbap.view.CustomDialog;
import com.supcon.mes.mbap.view.CustomImageButton;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.model.bean.ObjectEntity;
import com.supcon.mes.middleware.model.bean.xj.XJAreaEntity;
import com.supcon.mes.middleware.model.bean.xj.XJWorkEntity;
import com.supcon.mes.middleware.util.ErrorMsgHelper;
import com.supcon.mes.middleware.util.SystemCodeManager;
import com.supcon.mes.module_xj.R;
import com.supcon.mes.module_xj.controller.XJCameraController;
import com.supcon.mes.module_xj.model.api.XJTaskSubmitAPI;
import com.supcon.mes.module_xj.model.bean.XJTaskEntity;
import com.supcon.mes.module_xj.model.contract.XJTaskSubmitContract;
import com.supcon.mes.module_xj.model.event.XJAreaRefreshEvent;
import com.supcon.mes.module_xj.model.event.XJWorkRefreshEvent;
import com.supcon.mes.module_xj.presenter.XJTaskSubmitPresenter;
import com.supcon.mes.module_xj.ui.adapter.XJWorkViewAdapter;
import com.supcon.mes.testo_805i.controller.TestoController;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wangshizhan on 2020/1/10
 * Email:wangshizhan@supcom.com
 */
@Router(Constant.Router.XJ_WORK_ITEM_VIEW)
@Controller(value = {TestoController.class, XJCameraController.class})
@Presenter({XJTaskSubmitPresenter.class})
public class XJWorkViewActivity extends BaseRefreshRecyclerActivity<XJWorkEntity> implements XJTaskSubmitContract.View {

    @BindByTag("titleText")
    TextView titleText;

    @BindByTag("leftBtn")
    CustomImageButton leftBtn;

    @BindByTag("rightBtn")
    CustomImageButton rightBtn;

    @BindByTag("contentView")
    RecyclerView contentView;

    @BindByTag("eamSpinner")
    Spinner eamSpinner;

    XJWorkViewAdapter mXJWorkItemAdapter;

    XJAreaEntity mXJAreaEntity;

    ObjectEntity mDevice = null;
    List<String> deviceNames = new ArrayList<>();
    private List<XJWorkEntity> mWorkEntities = new ArrayList<>();
    private boolean isFromTask, isXJFinished;
    private XJTaskEntity mXJTaskEntity;
    @Override
    protected int getLayoutID() {
        return R.layout.ac_xj_work_view;
    }

    @Override
    protected void onInit() {
        super.onInit();

        String xjAreaEntityStr = getIntent().getStringExtra(Constant.IntentKey.XJ_AREA_ENTITY_STR);
        isFromTask = getIntent().getBooleanExtra(Constant.IntentKey.XJ_IS_FROM_TASK, false);
        isXJFinished = getIntent().getBooleanExtra(Constant.IntentKey.XJ_IS_FINISHED, false);
        if(xjAreaEntityStr!=null){
            mXJAreaEntity = GsonUtil.gsonToBean(xjAreaEntityStr, XJAreaEntity.class);
        }
        String taskStr = getIntent().getStringExtra(Constant.IntentKey.XJ_TASK_ENTITY_STR);
        if(!TextUtils.isEmpty(taskStr)){
            mXJTaskEntity = GsonUtil.gsonToBean(taskStr, XJTaskEntity.class);
        }

        refreshListController.setPullDownRefreshEnabled(false);
        refreshListController.setAutoPullDownRefresh(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(isFromTask){

            int finishNum = 0;
            for(XJWorkEntity xjWorkEntity : mXJAreaEntity.works){
                if (xjWorkEntity.isFinished){
                    finishNum++;
                }
            }

            mXJAreaEntity.finishNum = finishNum;
            if(mXJAreaEntity.finishNum != mXJAreaEntity.works.size()){
                mXJAreaEntity.isFinished = false;
            }


            EventBus.getDefault().post(new XJAreaRefreshEvent(mXJAreaEntity));
        }
        else{
            EventBus.getDefault().post(new XJWorkRefreshEvent());
        }
    }

    @Override
    protected void initView() {
        super.initView();
        StatusBarUtils.setWindowStatusBarColor(this, R.color.themeColor);
//        if(isFromTask){
//            titleText.setText(getString(R.string.xj_work_view));
//        }
//        else {
            titleText.setText(getString(R.string.xj_work_finish));
//        }
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setImageResource(R.drawable.ic_xj_work_upload);
        contentView.setLayoutManager(new LinearLayoutManager(context));
        contentView.addItemDecoration(new SpaceItemDecoration(DisplayUtil.dip2px(5, context)));
        contentView.setAdapter(mXJWorkItemAdapter);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initListener() {
        super.initListener();
        RxView.clicks(leftBtn)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        onBackPressed();
                    }
                });
        RxView.clicks(rightBtn)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        showFinishDialog();
                    }
                });

        mXJWorkItemAdapter.setOnItemChildViewClickListener(new OnItemChildViewClickListener() {
            @Override
            public void onItemChildViewClick(View childView, int position, int action, Object obj) {
                XJWorkEntity xjWorkEntity;
                int imgIndex;
                if (obj instanceof Integer){
                    imgIndex = (int)obj;
                    xjWorkEntity = null;
                }else {
                    xjWorkEntity = (XJWorkEntity)obj;
                    imgIndex = 0;

                }
                String tag = (String) childView.getTag();
                switch (tag){

                    case "itemXJWorkViewRedo":
                        redoWork(xjWorkEntity, position);
                        break;

                    case "itemXJWorkViewPics":
//                        if (action == CustomGalleryView.ACTION_VIEW){
//                            CustomGalleryView customGalleryView = (CustomGalleryView) childView;
//                            List<GalleryBean> galleryBeanList = customGalleryView.getGalleryAdapter().getList();
//
//                            Bundle bundle = new Bundle();
//                            bundle.putSerializable("images", (Serializable) FaultPicHelper.getImagePathList(galleryBeanList));
//                            bundle.putInt("position", imgIndex);  //点击位置索引
//
//                            int[] location = new int[2];
//                            childView.getLocationOnScreen(location);  //点击图片的位置
//                            bundle.putInt("locationX",location[0]);
//                            bundle.putInt("locationY",location[1]);
//
//                            bundle.putInt("width", DisplayUtil.dip2px(100, context));//必须
//                            bundle.putInt("height", DisplayUtil.dip2px(100, context));//必须
//                            bundle.putBoolean("isEditable", false);
//                            getWindow().setWindowAnimations(R.style.fadeStyle);
//                            IntentRouter.go(context, Constant.Router.IMAGE_VIEW, bundle);
//                        }
                        break;
                }


            }
        });

        eamSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String deviceName = deviceNames.get(position);
                showWorks(deviceName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    private void showFinishDialog() {
        new CustomDialog(context)
                .twoButtonAlertDialog(context.getResources().getString(R.string.xj_patrol_upload_task))
                .bindClickListener(R.id.grayBtn, v -> {
                }, true)
                .bindClickListener(R.id.redBtn, v -> {
                    //将本区域数据变成已完成
                    for(XJWorkEntity xjWorkEntity : mXJAreaEntity.works){
                        xjWorkEntity.isFinished=true;
                    }
                    //拼接单个区域上传数据
                    mXJTaskEntity.areas.clear();
                    mXJTaskEntity.areas.add(mXJAreaEntity);
                    List<XJTaskEntity> xjTaskEntityList=new ArrayList<>();
                    xjTaskEntityList.add(mXJTaskEntity);
                    presenterRouter.create(XJTaskSubmitAPI.class).uploadFile(xjTaskEntityList, true);
                    onLoading(getString(R.string.xj_task_uploading));
                }, true)
                .show();
    }

    private void redoWork(XJWorkEntity xjWorkEntity, int position) {

        if (!xjWorkEntity.isConclusionModify){  //禁修改(结论不可修改或免检)

            ToastUtils.show(context,getString(R.string.xj_work_view_redo_unable));

        }else {
            new CustomDialog(context)
                    .twoButtonAlertDialog(getString(R.string.xj_work_view_redo_warning))
                    .bindView(R.id.redBtn,getString(R.string.yes))
                    .bindView(R.id.grayBtn,getString(R.string.no))
                    .bindClickListener(R.id.grayBtn,v -> {
                        //TODO
                    },true)
                    .bindClickListener(R.id.redBtn,v -> {

                        xjWorkEntity.isFinished = false;
                        xjWorkEntity.completeDate = 0;
                        xjWorkEntity.taskDetailState = SystemCodeManager.getInstance().getSystemCodeEntity("PATROL_taskDetailState/uncheck");
                        xjWorkEntity.isRealPass = false;

                        EventBus.getDefault().post(new XJWorkRefreshEvent(xjWorkEntity));
                        refreshWorkList();

                    },true)
                    .show();
        }

    }


    @SuppressLint("CheckResult")
    private void showWorks(String deviceName) {

        boolean isAll;
        isAll = getString(R.string.xj_work_eam_all).equals(deviceName);

        List<XJWorkEntity> workEntities = new ArrayList<>();
        Flowable.fromIterable(mWorkEntities)
                .subscribeOn(Schedulers.newThread())
                .filter(new Predicate<XJWorkEntity>() {
                    @Override
                    public boolean test(XJWorkEntity xjWorkEntity) throws Exception {

                        if(isAll || xjWorkEntity.eamId!=null && xjWorkEntity.eamId.name!=null && xjWorkEntity.eamId.name.equals(deviceName)){
                            return true;
                        }
                        return false;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<XJWorkEntity>() {
                    @Override
                    public void accept(XJWorkEntity xjWorkEntity) throws Exception {
                        workEntities.add(xjWorkEntity);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        refreshListController.refreshComplete(workEntities);
                    }
                });
    }


    @Override
    protected IListAdapter<XJWorkEntity> createAdapter() {
        mXJWorkItemAdapter = new XJWorkViewAdapter(context, !getIntent().getBooleanExtra(Constant.IntentKey.XJ_IS_FINISHED, false));
        return mXJWorkItemAdapter;
    }

    @Override
    protected void initData() {
        super.initData();

        if(mXJAreaEntity!=null && mXJAreaEntity.works!=null){
            refreshWorkList();
        }

    }

    private void refreshWorkList() {

        mDevice = null;
        deviceNames.clear();
        mWorkEntities.clear();
        initWorks();
    }


    @SuppressLint("CheckResult")
    private void initWorks(){
        List<XJWorkEntity> noEamWorks = new ArrayList<>();
        Flowable.fromIterable(mXJAreaEntity.works)
                .subscribeOn(Schedulers.newThread())
                .filter(new Predicate<XJWorkEntity>() {
                    @Override
                    public boolean test(XJWorkEntity xjWorkEntity) throws Exception {

                        if(!xjWorkEntity.isFinished && !isXJFinished){
                            return false;
                        }


                        return true;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<XJWorkEntity>() {
                    @Override
                    public void accept(XJWorkEntity xjWorkEntity) throws Exception {


                        if(xjWorkEntity.eamId == null || xjWorkEntity.eamId.id == null){
                            noEamWorks.add(xjWorkEntity);
                        }
                        else {
                            if (mDevice == null || mDevice.id != xjWorkEntity.eamLongId) {
                                mDevice = xjWorkEntity.eamId;
                                XJWorkEntity workEntity = new XJWorkEntity();
                                workEntity.eamId = mDevice;
                                workEntity.eamLongId = mDevice.id;
                                workEntity.isEamView = true;
                                workEntity.eamName = mDevice.name;
                                workEntity.eamNum = deviceNames.size() + 1;
                                mWorkEntities.add(workEntity);

                                deviceNames.add(workEntity.eamId.name);
                            }
                            mWorkEntities.add(xjWorkEntity);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        if(noEamWorks.size() != 0){
                            XJWorkEntity workEntity = new XJWorkEntity();
                            workEntity.isEamView =true;
                            workEntity.eamNum = deviceNames.size() + 1;

                            if(deviceNames.size()==0){
                                workEntity.eamName = "区域巡检";
                            }
                            else{
                                workEntity.eamName = "无设备巡检";

                            }
                            deviceNames.add(workEntity.eamName);
                            mWorkEntities.add(workEntity);
                            mWorkEntities.addAll(noEamWorks);
                        }

                        if(deviceNames.size() == 0){
                            ((ViewGroup)eamSpinner.getParent()).setVisibility(View.GONE);
                            refreshListController.refreshComplete(mWorkEntities);
                        }
                        else {

                            if(deviceNames.size()==1){
                                ((ViewGroup)eamSpinner.getParent()).setVisibility(View.GONE);
                                refreshListController.refreshComplete(mWorkEntities);
                            }
                            else{
                                deviceNames.add(0, getString(R.string.xj_work_eam_all));
                                ((ViewGroup)eamSpinner.getParent()).setVisibility(View.VISIBLE);
                                ArrayAdapter<String> eamSpinnerAdapter = new ArrayAdapter<>(context, R.layout.ly_spinner_item_dark, deviceNames);  //创建一个数组适配器
                                eamSpinnerAdapter.setDropDownViewResource(R.layout.ly_spinner_dropdown_item);     //设置下拉列表框的下拉选项样式
                                eamSpinner.setAdapter(eamSpinnerAdapter);
                            }


                        }

                    }
                });

    }


    @Override
    public void uploadFileSuccess(String path) {

        LogUtil.d(""+path);

        if(TextUtils.isEmpty(path)){
            onLoadFailed(context.getResources().getString(R.string.xj_patrol_upload));
            return;
        }

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("filePath", path.replace("\\", "/"));
        queryMap.put("uploadTaskResultDTOs",new ArrayList<>());
        presenterRouter.create(XJTaskSubmitAPI.class).uploadXJData(false, queryMap);
    }


    @Override
    public void uploadFileFailed(String errorMsg) {
        onLoadFailed(ErrorMsgHelper.msgParse(errorMsg));
    }

    @Override
    public void uploadXJDataSuccess() {
        onLoadSuccess(context.getResources().getString(R.string.xj_patrol_upload_succeed));
    }

    @Override
    public void uploadXJDataFailed(String errorMsg) {
        onLoadFailed(ErrorMsgHelper.msgParse(errorMsg));
    }

}
