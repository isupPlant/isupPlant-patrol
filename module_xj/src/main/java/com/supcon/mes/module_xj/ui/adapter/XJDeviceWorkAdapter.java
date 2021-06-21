package com.supcon.mes.module_xj.ui.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.annotation.BindByTag;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.supcon.common.view.base.adapter.BaseListDataRecyclerViewAdapter;
import com.supcon.common.view.base.adapter.viewholder.BaseRecyclerViewHolder;
import com.supcon.common.view.listener.OnChildViewClickListener;
import com.supcon.common.view.util.LogUtil;
import com.supcon.common.view.util.SharedPreferencesUtils;
import com.supcon.common.view.util.ToastUtils;
import com.supcon.mes.aic_vib.util.DecimalFormatUtil;
import com.supcon.mes.mbap.view.CustomEditText;
import com.supcon.mes.mbap.view.CustomGalleryView;
import com.supcon.mes.mbap.view.CustomSpinner;
import com.supcon.mes.middleware.SupPlantApplication;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.constant.TemperatureMode;
import com.supcon.mes.middleware.constant.VibMode;
import com.supcon.mes.middleware.model.bean.xj.XJInputTypeEntity;
import com.supcon.mes.middleware.model.bean.xj.XJInputTypeEntityDao;
import com.supcon.mes.middleware.model.bean.xj.XJWorkEntity;
import com.supcon.mes.middleware.ui.view.SimpleSwitchView;
import com.supcon.mes.middleware.ui.view.search.CustomPopTextView;
import com.supcon.mes.middleware.util.SBTUtil;
import com.supcon.mes.middleware.util.SystemCodeManager;
import com.supcon.mes.module_xj.IntentRouter;
 
import com.supcon.mes.module_xj.controller.XJCameraController;
import com.supcon.mes.module_xj.controller.XJWorkCameraController;
import com.supcon.mes.module_xj.ui.XJDeviceWorkListActivity;
import com.supcon.mes.module_xj.util.FaultPicHelper;
import com.supcon.mes.module_xj.util.Str2NumUtil;
import com.supcon.mes.patrol.R;
import com.supcon.mes.sb2.util.SB2ThermometerHelper;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by wangshizhan on 2020/4/16
 * Email:wangshizhan@supcom.com
 */
public class XJDeviceWorkAdapter extends BaseListDataRecyclerViewAdapter<XJWorkEntity> {

    private SB2ThermometerHelper sb2ThermometerHelper;

    private Map<String, String> realValueMap;
    private List<String> realValues = new ArrayList<>();

    public XJDeviceWorkAdapter(Context context) {
        super(context);
    }


    public void setConclusions(Map<String, String> realValueMap) {

        if (realValueMap == null) {
            return;
        }

        this.realValueMap = realValueMap;
        realValues.addAll(realValueMap.values());
    }

    @Override
    protected BaseRecyclerViewHolder<XJWorkEntity> getViewHolder(int viewType) {

        if (viewType == 0) {
            return new XJWorkItemEamViewHolder(context, parent);
        }
        return new XJWorkItemContentViewholder(context, parent);
    }


    @Override
    public int getItemViewType(int position, XJWorkEntity xjWorkEntity) {
        return TextUtils.isEmpty(xjWorkEntity.content) ? 0 : 1;
    }

    /**
     * @description 测温
     * @author zhangwenshuai1
     * @date 2018/4/28
     */
    public void setSb2ThermometerHelper() {
        sb2ThermometerHelper = SB2ThermometerHelper.getInstance();
    }

    class XJWorkItemEamViewHolder extends BaseRecyclerViewHolder<XJWorkEntity> {

        @BindByTag("itemXJWorkEamNum")
        TextView itemXJWorkEamNum;

        @BindByTag("itemXJWorkEamName")
        TextView itemXJWorkEamName;

        @BindByTag("itemXJWorkSkip")
        ImageView itemXJWorkSkip;

        @BindByTag("itemXJWorkFinish")
        ImageView itemXJWorkFinish;

        public XJWorkItemEamViewHolder(Context context, ViewGroup parent) {
            super(context, parent);
        }

        @Override
        protected int layoutId() {
            return R.layout.item_xj_work_eam;
        }

        @Override
        protected void initView() {
            super.initView();
//            itemXJWorkSkip.setVisibility(View.GONE);
            itemXJWorkFinish.setVisibility(View.GONE);
        }

        @SuppressLint("CheckResult")
        @Override
        protected void initListener() {
            super.initListener();
            RxView.clicks(itemXJWorkSkip)
                    .throttleFirst(200, TimeUnit.MILLISECONDS)
                    .subscribe(o -> onItemChildViewClick(itemXJWorkSkip, 0, getItem(getAdapterPosition())));

            RxView.clicks(itemXJWorkFinish)
                    .throttleFirst(200, TimeUnit.MILLISECONDS)
                    .subscribe(o -> onItemChildViewClick(itemXJWorkFinish, 0, getItem(getAdapterPosition())));

            itemXJWorkEamName.setOnClickListener(v -> {
                XJWorkEntity xjWorkItemEntity = getItem(getAdapterPosition());
                if (xjWorkItemEntity.eamId == null || xjWorkItemEntity.eamId.id == null) {
                    ToastUtils.show(context, context.getResources().getString(R.string.xj_patrol_no_device_look));
                }
//                    Bundle bundle = new Bundle();
//                    bundle.putLong(Constant.IntentKey.SBDA_ENTITY_ID,  xjWorkItemEntity.eamId.id);
//                    IntentRouter.go(context, Constant.Router.SBDA_VIEW, bundle);
            });


        }

        @SuppressLint("DefaultLocale")
        @Override
        protected void update(XJWorkEntity data) {
            itemXJWorkEamNum.setText(String.format("%d", data.eamNum));
            if (!TextUtils.isEmpty(data.areaNum)){
                itemXJWorkEamName.setText(data.eamName+"("+data.areaNum+")");
            }else{
                itemXJWorkEamName.setText(data.eamName);
            }
        }
    }

    class XJWorkItemContentViewholder extends BaseRecyclerViewHolder<XJWorkEntity> implements OnChildViewClickListener {
        @BindByTag("itemXJWorkContent")
        CustomPopTextView itemXJWorkContent;

        @BindByTag("itemXJWorkFold")
        ImageView itemXJWorkFold;

        @BindByTag("itemXJWorkResultInput")
        CustomEditText itemXJWorkResultInput;

        @BindByTag("itemXJWorkResultSpinner")
        Spinner itemXJWorkResultSpinner;

        @BindByTag("itemXJWorkResultUnit")
        TextView itemXJWorkResultUnit;

        @BindByTag("itemXJWorkResultSwitch")
        SimpleSwitchView itemXJWorkResultSwitch;

        @BindByTag("itemXJWorkConclusionSpinner")
        Spinner itemXJWorkConclusionSpinner;

        @BindByTag("itemXJWorkResultMultiSelect")
        CustomSpinner itemXJWorkResultMultiSelect;

        @BindByTag("itemXJWorkBtnLayout")
        LinearLayout itemXJWorkBtnLayout;

        @BindByTag("itemXJWorkVibBtn")
        Button itemXJWorkVibBtn;

        @BindByTag("itemXJWorkTempBtn")
        Button itemXJWorkTempBtn;

        @BindByTag("itemXJWorkMoreLayout")
        RelativeLayout itemXJWorkMoreLayout;

        @BindByTag("itemXJWorkPics")
        CustomGalleryView itemXJWorkPics;

        @BindByTag("itemXJWorkPicMoreView")
        ImageView itemXJWorkPicMoreView;

        @BindByTag("itemXJWorkRemarkBtn")
        TextView itemXJWorkRemarkBtn;

        @BindByTag("itemXJWorkYHBtn")
        TextView itemXJWorkYHBtn;

        @BindByTag("itemXJWorkCameraBtn")
        ImageView itemXJWorkCameraBtn;

        XJWorkCameraController mXJCameraController;
        String oldImgUrl = "";

        public XJWorkItemContentViewholder(Context context, ViewGroup parent) {
            super(context, parent);
        }

        @Override
        protected int layoutId() {
            return R.layout.item_xj_work_content;
        }

        @Override
        protected void initView() {
            super.initView();
            mXJCameraController = ((XJDeviceWorkListActivity) context).getController(XJWorkCameraController.class);
            mXJCameraController.init(Constant.IMAGE_SAVE_XJPATH, "xjRecord");

//            itemXJWorkPics.setSheetEntity(new String[]{"拍摄照片", "拍摄短视频"});
            itemXJWorkPics.setTile(context.getResources().getString(R.string.xj_patrol_give_photo_method));

        }


        @SuppressLint({"CheckResult", "ClickableViewAccessibility"})
        @Override
        protected void initListener() {
            super.initListener();
            RxView.clicks(itemXJWorkRemarkBtn).throttleFirst(2, TimeUnit.SECONDS).subscribe(o -> {
                XJWorkEntity xjWorkItemEntity = getItem(getAdapterPosition());

                onItemChildViewClick(itemXJWorkRemarkBtn, 0, xjWorkItemEntity);
            });

            RxView.clicks(itemXJWorkYHBtn).throttleFirst(2, TimeUnit.SECONDS).subscribe(o -> {
                XJWorkEntity xjWorkItemEntity = getItem(getAdapterPosition());

                Bundle bundle = new Bundle();
//                bundle.putSerializable(Constant.IntentKey.YHGL_ENTITY, yhEntityVo);
                IntentRouter.go(context, Constant.Router.HAZARD_ADD, bundle);
            });

            itemXJWorkResultInput.editText().setOnEditorActionListener((textView, i, keyEvent) -> true);

            RxTextView.textChanges(itemXJWorkResultInput.editText())
                    .skipInitialValue()
                    .debounce(500, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(charSequence -> {
                        XJWorkEntity xjWorkItemEntity = getItem(getAdapterPosition());

                        if (xjWorkItemEntity == null) {
                            return;
                        }

                        XJInputTypeEntity xjInputTypeEntity = SupPlantApplication.dao().getXJInputTypeEntityDao().queryBuilder()
                                .where(XJInputTypeEntityDao.Properties.Id.eq(xjWorkItemEntity.inputStandardId.id))
                                .where(XJInputTypeEntityDao.Properties.Ip.eq(SupPlantApplication.getIp()))
                                .unique();

                        if (TextUtils.isEmpty(charSequence)) {
                            if (xjWorkItemEntity != null)
                                xjWorkItemEntity.concluse = "";
                        } else {
                            if ("PATROL_valueType/number".equals(xjInputTypeEntity.valType.id) && "PATROL_editType/input".equals(xjInputTypeEntity.editType.id)) {  //值类型判断：字符/数字

                                if (xjWorkItemEntity.isAutoJudge) {
                                    xjWorkItemEntity.concluse = charSequence.toString();
                                    //结论自动判定
                                    if (autoJudgeConclusion(xjWorkItemEntity, charSequence.toString())) {
                                        if ("PATROL_realValue/normal".equals(xjWorkItemEntity.conclusionID)) {
//                                            initSpinner(itemXJWorkConclusionSpinner, realValueMap.get("PATROL_realValue/normal"), realValues);
                                            itemXJWorkConclusionSpinner.setSelection(realValues.indexOf(realValueMap.get("PATROL_realValue/normal")));
                                        } else {
//                                            initSpinner(itemXJWorkConclusionSpinner, realValueMap.get("PATROL_realValue/abnormal"), realValues);
                                            itemXJWorkConclusionSpinner.setSelection(realValues.indexOf(realValueMap.get("PATROL_realValue/abnormal")));
                                        }

                                    }
                                } else {

                                    if (!charSequence.toString().matches("^-?[0.0-9]+$") || charSequence.toString().indexOf(".") == 0) {
                                        if ("-".equals(charSequence.toString())) {
                                            return;
                                        }
                                        ToastUtils.show(context, context.getResources().getString(R.string.xj_patrol_number_type));
                                    } else {

//                                            if (charSequence.toString().indexOf(".") > 0) {
//                                                if (xjInputTypeEntity.decimalPlace != null) {
//                                                    if (charSequence.toString().substring(charSequence.toString().indexOf(".") + 1).length() > Integer.parseInt(xjInputTypeEntity.decimalPlace)) {
////                                                        ToastUtils.show(context, "结果将四舍五入保留" + xjWorkItemEntity.decimal + "位");
//                                                    }
//                                                }
//                                            }
                                        String result;
                                        BigDecimal bigDecimal = new BigDecimal(charSequence.toString());
                                        if (xjInputTypeEntity.decimalPlace != null) {
                                            result = bigDecimal.setScale(Integer.parseInt(xjInputTypeEntity.decimalPlace), BigDecimal.ROUND_HALF_UP).toString();
                                        } else {
                                            result = charSequence.toString();
                                        }


                                        if (result.equals(xjWorkItemEntity.concluse)) {

                                        } else {
                                            xjWorkItemEntity.concluse = result;
                                            itemXJWorkResultInput.setContent(result);
                                            if (!TextUtils.isEmpty(result))
                                                itemXJWorkResultInput.editText().setSelection(result.length());
//                                            Flowable.timer(200, TimeUnit.MILLISECONDS)
//                                                    .observeOn(AndroidSchedulers.mainThread())
//                                                    .subscribe(new Consumer<Object>() {
//                                                        @Override
//                                                        public void accept(Object o) throws Exception {
//                                                            itemXJWorkResultInput.setContent(result);
//                                                            if(!TextUtils.isEmpty(result))
//                                                            itemXJWorkResultInput.editText().setSelection(result.length());
//                                                        }
//                                                    });

                                        }
                                    }
                                }

                            } else {
                                xjWorkItemEntity.concluse = charSequence.toString();
                            }
                        }

                        onItemChildViewClick(itemXJWorkResultInput, 0, xjWorkItemEntity);
                    });


            int tempMode = SharedPreferencesUtils.getParam(context, Constant.SPKey.TEMP_MODE, 0);
            //触发测温
            if (SBTUtil.isSupportTemp() && tempMode <= 1 && sb2ThermometerHelper != null) {
                itemXJWorkTempBtn.setOnTouchListener((v, motionEvent) -> {

                    if (SharedPreferencesUtils.getParam(context, Constant.SPKey.TEMP_MODE, 0) == TemperatureMode.NULL.getCode()) {
                        ToastUtils.show(context, context.getString(R.string.xj_work_temp_test_warning));
                        return true;
                    }

                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        if (!sb2ThermometerHelper.startOrEnd(true)) {
                            ToastUtils.show(context, context.getResources().getString(R.string.xj_patrol_device_hint));
                        } else {
                            //TODO  createSuccess
                        }
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                        sb2ThermometerHelper.startOrEnd(false);
                    }

                    XJWorkEntity xjWorkItemEntity = getItem(getAdapterPosition());
                    onItemChildViewClick(itemXJWorkTempBtn, 0, xjWorkItemEntity);
                    return true;
                });
            }


            //测温
            RxView.clicks(itemXJWorkTempBtn)
                    .throttleFirst(300, TimeUnit.MILLISECONDS)
                    .subscribe(o -> {

                        int tempMode1 = SharedPreferencesUtils.getParam(context, Constant.SPKey.TEMP_MODE, 0);
                        if (tempMode1 == 0) {
                            ToastUtils.show(context, context.getString(R.string.xj_work_temp_test_warning));
                            return;
                        }

                        onItemChildViewClick(itemXJWorkTempBtn, 0, getItem(getAdapterPosition()));
                    });

            //测振
            RxView.clicks(itemXJWorkVibBtn)
                    .throttleFirst(300, TimeUnit.MILLISECONDS)
                    .subscribe(o -> {

                        if (SharedPreferencesUtils.getParam(context, Constant.SPKey.VIB_MODE, 0) == VibMode.NULL.getCode()) {
                            ToastUtils.show(context, context.getString(R.string.xj_work_vib_test_warning));
                            return;
                        }

                        onItemChildViewClick(itemXJWorkVibBtn, 0, getItem(getAdapterPosition()));
                    });

            RxView.clicks(itemXJWorkFold)
                    .throttleFirst(200, TimeUnit.MILLISECONDS)
                    .subscribe(o -> toggleFoldView());

            RxView.clicks(itemXJWorkCameraBtn)
                    .throttleFirst(200, TimeUnit.MILLISECONDS)
                    .subscribe(o -> itemXJWorkPics.findViewById(R.id.customCameraIv).performClick());


            itemXJWorkResultSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    XJWorkEntity xjWorkItemEntity = getItem(getAdapterPosition());

                    String value = (String) itemXJWorkResultSpinner.getSelectedItem();
                    if (xjWorkItemEntity == null) {
                        return;
                    }

                    if ("空".equals(value) || TextUtils.isEmpty(value)) {
                        xjWorkItemEntity.concluse = "";
                    } else {
                        xjWorkItemEntity.concluse = value;
                    }

                    autoJudge(xjWorkItemEntity);

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            itemXJWorkConclusionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    XJWorkEntity xjWorkItemEntity = getItem(getAdapterPosition());
                    String value = (String) itemXJWorkConclusionSpinner.getSelectedItem();
                    if (TextUtils.isEmpty(value) || xjWorkItemEntity == null) {
                        return;
                    }

                    TextView tv = (TextView) view;

                    tv.setTextColor(context.getResources().getColor(R.color.textColorlightblack));

                    xjWorkItemEntity.conclusionName = value;
                    if (realValueMap.get("PATROL_realValue/normal").equals(xjWorkItemEntity.conclusionName)) {
                        xjWorkItemEntity.conclusionID = "PATROL_realValue/normal";
                        if (xjWorkItemEntity.isAutoJudge) {
                            setSpinnerState(itemXJWorkConclusionSpinner, 9);
                        } else {
                            setSpinnerState(itemXJWorkConclusionSpinner, 0);
                        }
                    } else if (realValueMap.get("PATROL_realValue/abnormal").equals(xjWorkItemEntity.conclusionName)) {
                        xjWorkItemEntity.conclusionID = "PATROL_realValue/abnormal";
                        if (xjWorkItemEntity.isAutoJudge) {
                            setSpinnerState(itemXJWorkConclusionSpinner, 9);
                        } else {
                            setSpinnerState(itemXJWorkConclusionSpinner, 1);

                        }
                        tv.setTextColor(context.getResources().getColor(R.color.customRed));
                    } else if (realValueMap.get("PATROL_realValue/doubtful").equals(xjWorkItemEntity.conclusionName)) {
                        xjWorkItemEntity.conclusionID = "PATROL_realValue/doubtful";
                        if (xjWorkItemEntity.isAutoJudge) {
                            setSpinnerState(itemXJWorkConclusionSpinner, 9);
                        } else {
                            setSpinnerState(itemXJWorkConclusionSpinner, 0);
                        }
                        tv.setTextColor(context.getResources().getColor(R.color.customRed));
                    }

                    xjWorkItemEntity.realValue = SystemCodeManager.getInstance().getSystemCodeEntity(xjWorkItemEntity.conclusionID);

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            itemXJWorkResultSwitch.setOnChildViewClickListener((childView, action, obj) -> {
                String result = (String) obj;
                XJWorkEntity xjWorkEntity = getItem(getAdapterPosition());

                if ("空".equals(result) || TextUtils.isEmpty(result)) {
                    xjWorkEntity.concluse = "";
                } else {
                    xjWorkEntity.concluse = result;
                }
                    autoJudge(xjWorkEntity);
            });

            itemXJWorkResultMultiSelect.setOnChildViewClickListener((childView, action, obj) -> {
                LogUtil.d("" + obj);
                XJWorkEntity xjWorkEntity = getItem(getAdapterPosition());
                onItemChildViewClick(itemXJWorkResultMultiSelect, 0, xjWorkEntity);
            });


            RxView.clicks(itemXJWorkPicMoreView)
                    .throttleFirst(200, TimeUnit.MILLISECONDS)
                    .subscribe(o -> mXJCameraController.viewPic(itemXJWorkPics, itemXJWorkPics.getGalleryAdapter().getList(), 0));

        }

        private void toggleFoldView() {

            XJWorkEntity xjWorkEntity = getItem(getAdapterPosition());
            xjWorkEntity.isFold = !xjWorkEntity.isFold;
            if (xjWorkEntity.isFold) {
                itemXJWorkMoreLayout.setVisibility(View.GONE);
                itemXJWorkFold.setImageResource(R.drawable.ic_xj_work_unfold);
            } else {
                itemXJWorkMoreLayout.setVisibility(View.VISIBLE);
                itemXJWorkFold.setImageResource(R.drawable.ic_xj_work_fold);
            }

        }

        private void autoJudge(XJWorkEntity xjWorkEntity) {
            if (xjWorkEntity.isAutoJudge && xjWorkEntity.normalRange != null && !TextUtils.isEmpty(xjWorkEntity.concluse)) {

                String[] normalRangeArr = xjWorkEntity.normalRange.split(",");
                if (Arrays.asList(normalRangeArr).contains(xjWorkEntity.concluse)) {
                    setNormal(xjWorkEntity);
                } else {
                    setAbnormal(xjWorkEntity);
                }
            }
        }

        @Override
        protected void update(XJWorkEntity data) {
            mXJCameraController.addListener(itemXJWorkPics, getAdapterPosition(), XJDeviceWorkAdapter.this);
//            LogUtil.e("InputType"+SupPlantApplication.dao().getXJInputTypeEntityDao().loadAll());
            XJInputTypeEntity xjInputTypeEntity = SupPlantApplication.dao().getXJInputTypeEntityDao().queryBuilder()
                    .where(XJInputTypeEntityDao.Properties.Ip.eq(SupPlantApplication.getIp()))
                    .where(XJInputTypeEntityDao.Properties.Id.eq(data.inputStandardId.id)).unique();


            if (xjInputTypeEntity == null) {
                ToastUtils.show(context, context.getString(R.string.xj_data_empty_warning));
                ((Activity) context).finish();
                return;
            }

            itemXJWorkContent.setContent(data.content);
            Drawable drawableLeft;

            if (!TextUtils.isEmpty(data.realRemark)) {
                drawableLeft = context.getResources().getDrawable(R.drawable.ic_xj_work_remark_fill);
            } else {
                drawableLeft = context.getResources().getDrawable(R.drawable.ic_xj_work_remark);
            }

            itemXJWorkRemarkBtn.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);

            itemXJWorkRemarkBtn.setCompoundDrawablePadding(5);

            itemXJWorkBtnLayout.setVisibility(View.GONE);
            itemXJWorkTempBtn.setVisibility(View.GONE);
            itemXJWorkVibBtn.setVisibility(View.GONE);

            if (data.isFold && TextUtils.isEmpty(data.xjImgName) && !data.isPhone) {
                itemXJWorkMoreLayout.setVisibility(View.GONE);
                itemXJWorkFold.setImageResource(R.drawable.ic_xj_work_unfold);
            } else {
                data.isFold = false;
                itemXJWorkMoreLayout.setVisibility(View.VISIBLE);
                itemXJWorkFold.setImageResource(R.drawable.ic_xj_work_fold);
            }

            List<String> candidateValues = new ArrayList<>();
            if (!TextUtils.isEmpty(xjInputTypeEntity.candidateValue)) {
                candidateValues.addAll(Arrays.asList(xjInputTypeEntity.candidateValue.split(",")));
            }
            if ("PATROL_editType/input".equals(xjInputTypeEntity.editType.id)) {   //录入框
                itemXJWorkResultSpinner.setVisibility(View.GONE);
                itemXJWorkResultSwitch.setVisibility(View.GONE);
                itemXJWorkResultInput.setVisibility(View.VISIBLE);
                itemXJWorkResultMultiSelect.setVisibility(View.GONE);
                if (data.isThermometric) {  //要求测温
                    itemXJWorkResultInput.setEnabled(false);
                    itemXJWorkResultInput.setEditable(false);
                    itemXJWorkResultInput.setHint(context.getResources().getString(R.string.xj_patrol_temp_value));

                    itemXJWorkBtnLayout.setVisibility(View.VISIBLE);
                    itemXJWorkTempBtn.setVisibility(View.VISIBLE);
                } else if (data.isSeismic) {
                    itemXJWorkResultInput.setEnabled(false);
                    itemXJWorkResultInput.setEditable(false);
                    itemXJWorkResultInput.setHint(context.getResources().getString(R.string.xj_patrol_shock_value));
                    itemXJWorkBtnLayout.setVisibility(View.VISIBLE);
                    itemXJWorkVibBtn.setVisibility(View.VISIBLE);
                } else {
                    itemXJWorkResultInput.setEnabled(true);
                    itemXJWorkResultInput.setEditable(true);
                    itemXJWorkResultInput.setHint(context.getResources().getString(R.string.xj_patrol_check));
                }


                setUnit(xjInputTypeEntity.unitID != null ? xjInputTypeEntity.unitID.name : "");

                String value = "";

                if ("PATROL_valueType/number".equals(xjInputTypeEntity.valType.id)) {
                    DecimalFormat decimalFormat;
                    try {
                        decimalFormat = DecimalFormatUtil.getDecimalFormat(TextUtils.isDigitsOnly(xjInputTypeEntity.decimalPlace) ? Integer.parseInt(xjInputTypeEntity.decimalPlace) : 2);
                    } catch (Exception e) {
                        decimalFormat = DecimalFormatUtil.getDecimalFormat(2);
                    }

                    try {
                        if (!TextUtils.isEmpty(data.defaultVal) && TextUtils.isEmpty(data.concluse)) {
                            value = decimalFormat.format(Float.parseFloat(data.defaultVal));
                        } else if (!TextUtils.isEmpty(data.concluse)) {
                            value = decimalFormat.format(Float.parseFloat(data.concluse));
                        }
                    } catch (NumberFormatException e) {
                        value = "";
                    }

                    itemXJWorkResultInput.setHint(context.getResources().getString(R.string.xj_patrol_number));
                } else {
                    if ("PATROL_valueType/char".equals(xjInputTypeEntity.valType.id)) {
                        itemXJWorkResultInput.setHint(context.getResources().getString(R.string.xj_patrol_input_text));
                    }
                    if (!TextUtils.isEmpty(data.defaultVal) && TextUtils.isEmpty(data.concluse)) {
                        value = data.defaultVal;
                    } else if (!TextUtils.isEmpty(data.concluse)) {
                        value = data.concluse;
                    }

                }

                itemXJWorkResultInput.setContent(value);

                if ("PATROL_valueType/number".equals(xjInputTypeEntity.valType.id)) {
                    itemXJWorkResultInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);  //支持正负浮点数、整数
                } else {
                    itemXJWorkResultInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                }

            } else if ("PATROL_editType/whether".equals(xjInputTypeEntity.editType.id)) {  //是否  单选
                itemXJWorkResultInput.setVisibility(View.GONE);
                itemXJWorkResultSpinner.setVisibility(View.GONE);
                itemXJWorkResultMultiSelect.setVisibility(View.GONE);
                itemXJWorkResultSwitch.setVisibility(View.VISIBLE);

                setUnit(xjInputTypeEntity.unitID != null ? xjInputTypeEntity.unitID.name : "");

                if (!TextUtils.isEmpty(data.defaultVal) && TextUtils.isEmpty(data.concluse)) {
                    data.concluse = data.defaultVal;
                    initSwitch(itemXJWorkResultSwitch, data.defaultVal, candidateValues);
                } else {
                    initSwitch(itemXJWorkResultSwitch, data.concluse, candidateValues);
                }
            } else if ("PATROL_editType/singleSelect".equals(xjInputTypeEntity.editType.id)) {  //是否  单选
                itemXJWorkResultInput.setVisibility(View.GONE);
                itemXJWorkResultSpinner.setVisibility(View.VISIBLE);
                itemXJWorkResultMultiSelect.setVisibility(View.GONE);
                itemXJWorkResultSwitch.setVisibility(View.GONE);

                setUnit(xjInputTypeEntity.unitID != null ? xjInputTypeEntity.unitID.name : "");

                if (!TextUtils.isEmpty(data.defaultVal) && TextUtils.isEmpty(data.concluse)) {
                    data.concluse = data.defaultVal;
                    initSpinner(itemXJWorkResultSpinner, data.defaultVal, candidateValues);

                } else {
                    initSpinner(itemXJWorkResultSpinner, data.concluse, candidateValues);
                }
            } else if ("PATROL_editType/multipleSelect".equals(xjInputTypeEntity.editType.id)) {  //多选
                itemXJWorkResultInput.setVisibility(View.GONE);
                itemXJWorkResultSpinner.setVisibility(View.GONE);
                itemXJWorkResultMultiSelect.setVisibility(View.VISIBLE);
                itemXJWorkResultSwitch.setVisibility(View.GONE);


                setUnit(xjInputTypeEntity.unitID != null ? xjInputTypeEntity.unitID.name : "");

                if (!TextUtils.isEmpty(data.defaultVal) && TextUtils.isEmpty(data.concluse)) {
                    itemXJWorkResultMultiSelect.setSpinner(data.defaultVal);
                    data.concluse = data.defaultVal;
                } else {
                    itemXJWorkResultMultiSelect.setSpinner(data.concluse);
                }
            }
//            initSpinner(itemXJWorkConclusionSpinner, realValueMap.get(data.conclusionID),realValues);
            if ("PATROL_realValue/abnormal".equals(data.conclusionID)) {
                initSpinner(itemXJWorkConclusionSpinner, realValueMap.get("PATROL_realValue/abnormal"), realValues);
            } else if ("PATROL_realValue/doubtful".equals(data.conclusionID)) {
                initSpinner(itemXJWorkConclusionSpinner, realValueMap.get("PATROL_realValue/doubtful"), realValues);
            } else {
                initSpinner(itemXJWorkConclusionSpinner, realValueMap.get("PATROL_realValue/normal"), realValues);
            }

            if (data.isPhone) {
                itemXJWorkCameraBtn.setImageResource(R.drawable.sl_xj_work_camera_n);
            } else {
                itemXJWorkCameraBtn.setImageResource(R.drawable.sl_xj_work_camera);
            }

            //保存图片
            if (!TextUtils.isEmpty(data.xjImgName)) {
                if (oldImgUrl.equals(data.xjImgName)) {
                    return;
                }

                List<String> pics = Arrays.asList(data.xjImgName.split(","));


                itemXJWorkPics.setVisibility(View.VISIBLE);
                if (pics.size() > 3) {
//                    FaultPicHelper.initPics(pics.subList(0, 3), itemXJWorkPics);
                    itemXJWorkPicMoreView.setVisibility(View.VISIBLE);
                } else {
//                    FaultPicHelper.initPics(pics, itemXJWorkPics);
                    itemXJWorkPicMoreView.setVisibility(View.GONE);
                }
                FaultPicHelper.initPics(pics, itemXJWorkPics);
                oldImgUrl = data.xjImgName;
            } else {
                itemXJWorkPics.clear();
                oldImgUrl = "";
                itemXJWorkPicMoreView.setVisibility(View.GONE);
                itemXJWorkPics.setVisibility(View.GONE);
            }
        }

        private void initSwitch(SimpleSwitchView switchView, String defaultVal, Collection<String> candidateValue) {
            List<String> values = new ArrayList<>();
            if (candidateValue != null) {
                values.addAll(candidateValue);
            }
            switchView.setValues(values.toArray(new String[values.size()]), TextUtils.isEmpty(defaultVal) ? -1 : values.indexOf(defaultVal));
        }

        private void initSpinner(Spinner spinner, String defaultVal, Collection<String> candidateValue) {

            List<String> values = new ArrayList<>();
//            if(TextUtils.isEmpty(defaultVal)){
//                values.add("空");
//            }


            if (candidateValue != null) {
                values.addAll(candidateValue);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.ly_spinner_item_dark, values);  //创建一个数组适配器
            adapter.setDropDownViewResource(R.layout.ly_spinner_dropdown_item);     //设置下拉列表框的下拉选项样式
            spinner.setAdapter(adapter);

            if (!TextUtils.isEmpty(defaultVal)) {
                spinner.setSelection(values.indexOf(defaultVal));
            }
        }

        //state 0 正常 1 异常 9 失效
        private void setSpinnerState(Spinner spinner, int state) {
            if (state == 0) {
                spinner.setEnabled(true);
                spinner.setBackgroundResource(R.drawable.sh_xj_work_spinner_drop_down_blue);
            } else if (state == 1) {
                spinner.setEnabled(true);
                spinner.setBackgroundResource(R.drawable.sh_xj_work_spinner_drop_down_red);
            } else {
                spinner.setEnabled(false);
                spinner.setBackgroundResource(R.drawable.sh_xj_work_spinner_drop_down_disable);
            }


        }

        private void setUnit(String unitName) {

            if (!TextUtils.isEmpty(unitName)) {
                itemXJWorkResultUnit.setVisibility(View.VISIBLE);
                itemXJWorkResultUnit.setText(unitName);
            } else {
                itemXJWorkResultUnit.setVisibility(View.GONE);
            }

        }

        @Override
        public void onChildViewClick(View childView, int action, Object obj) {
            XJWorkEntity workItemEntity = getItem(getAdapterPosition());  //注：参数obj为空，实现的接口方法，接口中obj为null，非item对象
            onItemChildViewClick(childView, action, workItemEntity);
        }


        /**
         * @author zhangwenshuai1
         * @date 2018/4/11
         * @description 结论自动变动
         */
        private boolean autoJudgeConclusion(XJWorkEntity xjWorkItemEntity, String charSequence) {

            XJInputTypeEntity xjInputTypeEntity = SupPlantApplication.dao().getXJInputTypeEntityDao().queryBuilder()
                    .where(XJInputTypeEntityDao.Properties.Id.eq(xjWorkItemEntity.inputStandardId.id))
                    .where(XJInputTypeEntityDao.Properties.Ip.eq(SupPlantApplication.getIp()))
                    .unique();

            if (!charSequence.matches("^-?[0.0-9.0]+$") || charSequence.indexOf(".") == 0) {
                if ("-".equals(charSequence)) {
                    return false;
                }
                ToastUtils.show(context, context.getResources().getString(R.string.xj_patrol_number_type));
            } else {

                if (charSequence.indexOf(".") > 0) {
                    if (xjInputTypeEntity.decimalPlace != null) {
                        if (charSequence.substring(charSequence.indexOf(".") + 1).length() > Integer.parseInt(xjInputTypeEntity.decimalPlace)) {
                            ToastUtils.show(context, context.getResources().getString(R.string.xj_patrol_result) + xjInputTypeEntity.decimalPlace + context.getResources().getString(R.string.xj_patrol_local));
                        }
                    }
                }

                BigDecimal bigDecimal = new BigDecimal(charSequence);
                if (xjInputTypeEntity.decimalPlace != null) {
                    xjWorkItemEntity.concluse = bigDecimal.setScale(Integer.parseInt(xjInputTypeEntity.decimalPlace), BigDecimal.ROUND_HALF_UP).toString();
                } else {
                    xjWorkItemEntity.concluse = charSequence;
                }

                //不同正常值范围解析判定
                if (xjWorkItemEntity.normalRange != null) {
                    if (xjWorkItemEntity.normalRange.contains("~")) {  //区间形式eg: (-12.45~-1.00)

                        return intervalJudge(xjWorkItemEntity, charSequence);

                    } else if (xjWorkItemEntity.normalRange.contains("|")) {   //区间之外形式，eg：≥-15.5|≤-35.6，≤-35.6|≥-15.5

                        return orJudge(xjWorkItemEntity, charSequence);

                    } else {  // ≥ 或 ≤ 或 ＞ 或 ＜

                        return unequalJudge(xjWorkItemEntity, charSequence);

                    }
                }

            }

            return false;
        }

        /**
         * @author zhangwenshuai1
         * @date 2018/4/12
         * @description 区间形式判定 eg: -12.45~-1.00
         */
        private boolean intervalJudge(XJWorkEntity xjWorkItemEntity, String charSequence) {
            String[] numArr = xjWorkItemEntity.normalRange.split("~");
            double small = Double.parseDouble(numArr[0]);
            double big = Double.parseDouble(numArr[1]);
            double inputResult = Double.parseDouble(charSequence);

            if (inputResult >= small && inputResult <= big) {  //区间内、正常

                setNormal(xjWorkItemEntity);

            } else {  //异常
                setAbnormal(xjWorkItemEntity);

            }

            return true;
        }

        private void setAbnormal(XJWorkEntity xjWorkItemEntity) {
            xjWorkItemEntity.conclusionID = "PATROL_realValue/abnormal";
            xjWorkItemEntity.conclusionName = realValueMap.get("PATROL_realValue/abnormal");
            itemXJWorkConclusionSpinner.setSelection(realValues.indexOf(xjWorkItemEntity.conclusionName));
        }

        private void setDoubtful(XJWorkEntity xjWorkItemEntity) {
            xjWorkItemEntity.conclusionID = "PATROL_realValue/doubtful";
            xjWorkItemEntity.conclusionName = realValueMap.get("PATROL_realValue/doubtful");
            itemXJWorkConclusionSpinner.setSelection(realValues.indexOf(xjWorkItemEntity.conclusionName));
        }

        private void setNormal(XJWorkEntity xjWorkItemEntity) {
            xjWorkItemEntity.conclusionID = "PATROL_realValue/normal";
            xjWorkItemEntity.conclusionName = realValueMap.get("PATROL_realValue/normal");
            itemXJWorkConclusionSpinner.setSelection(realValues.indexOf(xjWorkItemEntity.conclusionName));
        }


        /**
         * @author zhangwenshuai1
         * @date 2018/4/12
         * @description 区间形式判定 eg: ≥-15.5|≤-35.6，≤-35.6|≥-15.5
         */
        private boolean orJudge(XJWorkEntity xjWorkItemEntity, String charSequence) {

            String regExp = "(≥|≤|＞|＜)?";

            Pattern pattern = Pattern.compile(regExp);

            Matcher matcher = pattern.matcher(xjWorkItemEntity.normalRange);

            String[] numArr = matcher.replaceAll("").split("\\|");

            double small = Double.parseDouble(numArr[0]);
            double big = Double.parseDouble(numArr[1]);

            if (small > big) {
                small = big;
                big = Double.parseDouble(numArr[0]);
            }

            double inputResult = Double.parseDouble(charSequence);

            if (xjWorkItemEntity.normalRange.contains("≥") && xjWorkItemEntity.normalRange.contains("≤")) {
                if (inputResult > small && inputResult < big) {  //区间内、异常
                    setAbnormal(xjWorkItemEntity);
                } else {  //正常
                    setNormal(xjWorkItemEntity);
                }
            } else if (xjWorkItemEntity.normalRange.contains("≥") && xjWorkItemEntity.normalRange.contains("＜")) {
                if (inputResult >= small && inputResult < big) {  //区间内、异常
                    setAbnormal(xjWorkItemEntity);
                } else {  //正常
                    setNormal(xjWorkItemEntity);
                }
            } else if (xjWorkItemEntity.normalRange.contains("＞") && xjWorkItemEntity.normalRange.contains("≤")) {
                if (inputResult > small && inputResult <= big) {  //区间内、异常
                    setAbnormal(xjWorkItemEntity);
                } else {  //正常
                    setNormal(xjWorkItemEntity);
                }
            } else {
                if (inputResult >= small && inputResult <= big) {  //区间内、异常
                    setAbnormal(xjWorkItemEntity);
                } else {  //正常
                    setNormal(xjWorkItemEntity);
                }
            }

            return true;
        }

        /**
         * @author zhangwenshuai1
         * @date 2018/4/12
         * @description 区间形式判定 eg: ≥ 或 ≤ 或 ＞ 或 ＜
         */
        private boolean unequalJudge(XJWorkEntity xjWorkItemEntity, String charSequence) {

            String regExp = "(≥|≤|＞|＜)?";
            Pattern pattern = Pattern.compile(regExp);
            Matcher matcher = pattern.matcher(xjWorkItemEntity.normalRange);
            if (!Str2NumUtil.isDoubleOrFloat(matcher.replaceAll(""))) {
                ToastUtils.show(context, context.getResources().getString(R.string.xj_patrol_range));
                return false;
            }
            double num = Double.parseDouble(matcher.replaceAll(""));

            double inputResult = Double.parseDouble(charSequence);

            if (xjWorkItemEntity.normalRange.contains("≥")) {
                if (inputResult < num) {  //异常
                    setAbnormal(xjWorkItemEntity);
                } else {  //正常
                    setNormal(xjWorkItemEntity);
                }
            } else if (xjWorkItemEntity.normalRange.contains("＞")) {
                if (inputResult <= num) {  //异常
                    setAbnormal(xjWorkItemEntity);
                } else {  //正常
                    setNormal(xjWorkItemEntity);
                }
            } else if (xjWorkItemEntity.normalRange.contains("≤")) {
                if (inputResult > num) {  //异常
                    setAbnormal(xjWorkItemEntity);
                } else {  //正常
                    setNormal(xjWorkItemEntity);
                }
            } else {
                if (inputResult >= num) {  //异常
                    setAbnormal(xjWorkItemEntity);
                } else {  //正常
                    setNormal(xjWorkItemEntity);
                }
            }

            return true;
        }
    }
}
