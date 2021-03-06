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
import com.supcon.mes.middleware.SupPlantApplication;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.model.bean.ObjectEntity;
import com.supcon.mes.middleware.model.bean.xj.XJAreaEntity;
import com.supcon.mes.middleware.model.bean.xj.XJTaskAreaEntity;
import com.supcon.mes.middleware.model.bean.xj.XJTaskEntity;
import com.supcon.mes.middleware.model.bean.xj.XJTaskWorkEntity;
import com.supcon.mes.middleware.model.bean.xj.XJTaskWorkEntity;
import com.supcon.mes.middleware.util.ErrorMsgHelper;
import com.supcon.mes.middleware.util.SystemCodeManager;
import com.supcon.mes.middleware.util.XJTaskCacheUtil;
import com.supcon.mes.patrol.R;
import com.supcon.mes.module_xj.controller.XJCameraController;
import com.supcon.mes.module_xj.model.api.XJTaskSubmitAPI;
import com.supcon.mes.module_xj.model.contract.XJTaskSubmitContract;
import com.supcon.mes.module_xj.model.event.XJAreaRefreshEvent;
import com.supcon.mes.module_xj.model.event.XJTempTaskUploadRefreshEvent;
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
public class XJWorkViewActivity extends BaseRefreshRecyclerActivity<XJTaskWorkEntity> implements XJTaskSubmitContract.View {

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

    XJTaskAreaEntity mXJAreaEntity;

    ObjectEntity mDevice = null;
    List<String> deviceNames = new ArrayList<>();
    private List<XJTaskWorkEntity> mWorkEntities = new ArrayList<>();
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
//        if(xjAreaEntityStr!=null){
//            mXJAreaEntity = GsonUtil.gsonToBean(xjAreaEntityStr, XJAreaEntity.class);
//        }
        String taskNo = getIntent().getStringExtra(Constant.IntentKey.XJ_TASK_NO_STR);


        if (!TextUtils.isEmpty(taskNo)) {

            String taskStr = XJTaskCacheUtil.getString(taskNo);
            mXJTaskEntity = GsonUtil.gsonToBean(taskStr, XJTaskEntity.class);
        }

        if (xjAreaEntityStr != null && mXJTaskEntity != null) {

            for(XJTaskAreaEntity areaEntity: mXJTaskEntity.areas){
                if(xjAreaEntityStr.equals(String.valueOf(areaEntity.id))){
                    mXJAreaEntity = areaEntity;
                    break;
                }
            }
            mXJAreaEntity.works = XJTaskCacheUtil.getTaskWork(taskNo, Long.valueOf(xjAreaEntityStr));
        }

        refreshListController.setPullDownRefreshEnabled(false);
        refreshListController.setAutoPullDownRefresh(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//
        if(isFromTask){
            EventBus.getDefault().post(new XJAreaRefreshEvent());
        }
//
//            int finishNum = 0;
//            for(XJTaskWorkEntity xjWorkEntity : mXJAreaEntity.works){
//                if (xjWorkEntity.isFinished){
//                    finishNum++;
//                }
//            }
//
//            mXJAreaEntity.finishNum = finishNum;
//            if(mXJAreaEntity.finishNum != mXJAreaEntity.works.size()){
//                mXJAreaEntity.isFinished = false;
//            }
//        SupPlantApplication.dao().getXJTaskAreaEntityDao().update(mXJAreaEntity);
//        XJTaskCacheUtil.insertTasksWork(mXJTaskEntity.tableNo, mXJAreaEntity.id, mXJAreaEntity.works);
//       }
//        else{
//            EventBus.getDefault().post(new XJWorkRefreshEvent());
//        }
    }

    @Override
    protected void initView() {
        super.initView();
        StatusBarUtils.setWindowStatusBarColor(this, R.color.themeColor);
//        if(isFromTask){
//            titleText.setText(getString(R.string.xj_work_view));
//        }
//        else {
//            titleText.setText(getString(R.string.xj_work_finish));
//        }
        titleText.setText(mXJAreaEntity.name);
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
                        if (mXJWorkItemAdapter.getList().size()>0){
                            showFinishDialog();
                        }else{
                            ToastUtils.show(context, "??????????????????????????????!");
                        }

                    }
                });

        mXJWorkItemAdapter.setOnItemChildViewClickListener(new OnItemChildViewClickListener() {
            @Override
            public void onItemChildViewClick(View childView, int position, int action, Object obj) {
                XJTaskWorkEntity xjWorkEntity;
                int imgIndex;
                if (obj instanceof Integer){
                    imgIndex = (int)obj;
                    xjWorkEntity = null;
                }else {
                    xjWorkEntity = (XJTaskWorkEntity)obj;
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
//                            bundle.putInt("position", imgIndex);  //?????????22?????????
//
//                            int[] location = new int[2];
//                            childView.getLocationOnScreen(location);  //?????????????????????
//                            bundle.putInt("locationX",location[0]);
//                            bundle.putInt("locationY",location[1]);
//
//                            bundle.putInt("width", DisplayUtil.dip2px(100, context));//??????
//                            bundle.putInt("height", DisplayUtil.dip2px(100, context));//??????
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
                    //?????????????????????????????????
                    for(XJTaskWorkEntity xjWorkEntity : mXJAreaEntity.works){
                        xjWorkEntity.isFinished=true;
                    }
                    //??????????????????????????????
                    mXJTaskEntity.areas.clear();
                    mXJTaskEntity.areas.add(mXJAreaEntity);
                    List<XJTaskEntity> xjTaskEntityList=new ArrayList<>();
                    xjTaskEntityList.add(mXJTaskEntity);
                    presenterRouter.create(XJTaskSubmitAPI.class).uploadFile(xjTaskEntityList, true);
                    onLoading(getString(R.string.xj_task_uploading));
                }, true)
                .show();
    }

    private void redoWork(XJTaskWorkEntity xjWorkEntity, int position) {

        if (!xjWorkEntity.isConclusionModify){  //?????????(???????????????????????????)

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
                        mXJAreaEntity.finishNum--;
                        xjWorkEntity.isFinished = false;
                        xjWorkEntity.xjImgName=null;
                        xjWorkEntity.completeDate = 0;
                        xjWorkEntity.conclusionID = null;
                        xjWorkEntity.realValue = null;
                        xjWorkEntity.conclusionName = null;
                        xjWorkEntity.concluse = null;
                        xjWorkEntity.taskDetailState = SystemCodeManager.getInstance().getSystemCodeEntity("PATROL_taskDetailState/uncheck");
                        xjWorkEntity.isRealPass = false;
                        xjWorkEntity.isRealPhoto=false;
                        if (mXJAreaEntity.isFinished){
                            mXJAreaEntity.isFinished=false;
//                            SupPlantApplication.dao().getXJTaskAreaEntityDao().update(mXJAreaEntity);
                            XJTaskCacheUtil.insertTasksArea(mXJAreaEntity);
                        }

                        SupPlantApplication.dao().getXJTaskWorkEntityDao().update(xjWorkEntity);
                        refreshWorkList();
                        EventBus.getDefault().post(new XJWorkRefreshEvent());
                    },true)
                    .show();
        }

    }


    @SuppressLint("CheckResult")
    private void showWorks(String deviceName) {

        boolean isAll;
        isAll = getString(R.string.xj_work_eam_all).equals(deviceName);

        List<XJTaskWorkEntity> workEntities = new ArrayList<>();
        Flowable.fromIterable(mWorkEntities)
                .subscribeOn(Schedulers.newThread())
                .filter(new Predicate<XJTaskWorkEntity>() {
                    @Override
                    public boolean test(XJTaskWorkEntity xjWorkEntity) throws Exception {

                        if(isAll || xjWorkEntity.eamId!=null && xjWorkEntity.eamId.name!=null && xjWorkEntity.eamId.name.equals(deviceName)){
                            return true;
                        }
                        return false;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<XJTaskWorkEntity>() {
                    @Override
                    public void accept(XJTaskWorkEntity xjWorkEntity) throws Exception {
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
    protected IListAdapter<XJTaskWorkEntity> createAdapter() {
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
        List<XJTaskWorkEntity> noEamWorks = new ArrayList<>();
        Flowable.fromIterable(mXJAreaEntity.works)
                .subscribeOn(Schedulers.newThread())
                .filter(new Predicate<XJTaskWorkEntity>() {
                    @Override
                    public boolean test(XJTaskWorkEntity xjWorkEntity) throws Exception {

                        if(!xjWorkEntity.isFinished && !isXJFinished){
                            return false;
                        }


                        return true;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<XJTaskWorkEntity>() {
                    @Override
                    public void accept(XJTaskWorkEntity xjWorkEntity) throws Exception {


                        if(xjWorkEntity.eamId == null || xjWorkEntity.eamId.id == null){
                            noEamWorks.add(xjWorkEntity);
                        }
                        else {
                            if (mDevice == null || mDevice.id != xjWorkEntity.eamLongId) {
                                mDevice = xjWorkEntity.eamId;
                                XJTaskWorkEntity workEntity = new XJTaskWorkEntity();
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
                            XJTaskWorkEntity workEntity = new XJTaskWorkEntity();
                            workEntity.isEamView =true;
                            workEntity.eamNum = deviceNames.size() + 1;

                            if(deviceNames.size()==0){
                                workEntity.eamName = context.getString(R.string.xj_patrol_area_inspection);
                            }
                            else{
                                workEntity.eamName = context.getString(R.string.xj_patrol_no_device);

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
                                ArrayAdapter<String> eamSpinnerAdapter = new ArrayAdapter<>(context, R.layout.ly_spinner_item_dark, deviceNames);  //???????????????????????????
                                eamSpinnerAdapter.setDropDownViewResource(R.layout.ly_spinner_dropdown_item);     //??????????????????????????????????????????
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
    public void uploadXJWorkFileSuccess(Long entity) {

    }

    @Override
    public void uploadXJWorkFileFailed(String errorMsg) {

    }

    @Override
    public void uploadXJDataSuccess(Long id) {
        if (id==null){
            id=0l;
        }
        onLoadSuccess(context.getResources().getString(R.string.xj_patrol_upload_succeed));
        mXJAreaEntity.isUpload=true;
        XJTaskCacheUtil.insertTasksArea(mXJAreaEntity);
        mXJTaskEntity.id = id;
        EventBus.getDefault().post(new XJTempTaskUploadRefreshEvent(id));
    }

    @Override
    public void uploadXJDataFailed(String errorMsg) {
        onLoadFailed(ErrorMsgHelper.msgParse(errorMsg));
    }

}
