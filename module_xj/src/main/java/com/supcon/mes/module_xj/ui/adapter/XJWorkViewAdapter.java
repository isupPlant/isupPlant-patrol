package com.supcon.mes.module_xj.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.annotation.BindByTag;
import com.jakewharton.rxbinding2.view.RxView;
import com.supcon.common.view.base.adapter.BaseListDataRecyclerViewAdapter;
import com.supcon.common.view.base.adapter.viewholder.BaseRecyclerViewHolder;
import com.supcon.common.view.listener.OnChildViewClickListener;
import com.supcon.common.view.util.ToastUtils;
import com.supcon.mes.mbap.view.CustomGalleryView;
import com.supcon.mes.mbap.view.CustomVerticalTextView;
import com.supcon.mes.middleware.SupPlantApplication;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.model.bean.xj.XJInputTypeEntity;
import com.supcon.mes.middleware.model.bean.xj.XJInputTypeEntityDao;
import com.supcon.mes.middleware.model.bean.xj.XJWorkEntity;
import com.supcon.mes.module_xj.R;
import com.supcon.mes.module_xj.controller.XJCameraController;
import com.supcon.mes.module_xj.ui.XJWorkViewActivity;
import com.supcon.mes.module_xj.util.FaultPicHelper;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;

/**
 * Created by wangshizhan on 2020/4/16
 * Email:wangshizhan@supcom.com
 */
public class XJWorkViewAdapter extends BaseListDataRecyclerViewAdapter<XJWorkEntity> {

    private boolean isEditable = true;

    public XJWorkViewAdapter(Context context, boolean isEditable) {
        super(context);
        this.isEditable = isEditable;
    }

    public XJWorkViewAdapter(Context context) {
        super(context);
    }

    public XJWorkViewAdapter(Context context, List<XJWorkEntity> list) {
        super(context, list);
    }


    @Override
    protected BaseRecyclerViewHolder<XJWorkEntity> getViewHolder(int viewType) {

        if(viewType == 0){
            return new XJWorkItemEamViewholder(context, parent);
        }
        return new XJWorkItemContentViewholder(context, parent);
    }


    @Override
    public int getItemViewType(int position, XJWorkEntity xjWorkEntity) {
        return TextUtils.isEmpty(xjWorkEntity.content)?0:1;
    }

    class XJWorkItemEamViewholder extends BaseRecyclerViewHolder<XJWorkEntity>{

        @BindByTag("itemXJWorkViewEamNum")
        TextView itemXJWorkViewEamNum;

        @BindByTag("itemXJWorkViewEamName")
        TextView itemXJWorkViewEamName;


        public XJWorkItemEamViewholder(Context context, ViewGroup parent) {
            super(context, parent);
        }

        @Override
        protected int layoutId() {
            return R.layout.item_xj_work_view_eam;
        }


        @Override
        protected void initView() {
            super.initView();
//            itemWorkEamParamsList.setLayoutManager(new LinearLayoutManager(context));
        }

        @SuppressLint("CheckResult")
        @Override
        protected void initListener() {
            super.initListener();


            itemXJWorkViewEamNum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    XJWorkEntity xjWorkItemEntity = getItem(getAdapterPosition());
                    if (xjWorkItemEntity.eamId == null || xjWorkItemEntity.eamId.id == null) {
                        ToastUtils.show(context, "无设备详情可查看！");
                        return;
                    }
//                    Bundle bundle = new Bundle();
//                    bundle.putLong(Constant.IntentKey.SBDA_ENTITY_ID,  xjWorkItemEntity.eamId.id);
//                    IntentRouter.go(context, Constant.Router.SBDA_VIEW, bundle);
                }
            });



        }

        @Override
        protected void update(XJWorkEntity data) {


            itemXJWorkViewEamNum.setText(""+data.eamNum);
            itemXJWorkViewEamName.setText(data.eamName);

        }
    }

    class XJWorkItemContentViewholder extends BaseRecyclerViewHolder<XJWorkEntity> implements OnChildViewClickListener {


        @BindByTag("itemXJWorkViewFlag")
        ImageView itemXJWorkViewFlag;

        @BindByTag("itemXJWorkViewContent")
        TextView itemXJWorkViewContent;

        @BindByTag("itemXJWorkViewRedo")
        ImageView itemXJWorkViewRedo;

        @BindByTag("itemXJWorkViewResult")
        TextView itemXJWorkViewResult;

        @BindByTag("itemXJWorkViewResultUnit")
        TextView itemXJWorkViewResultUnit;

        @BindByTag("itemXJWorkViewConclusion")
        TextView itemXJWorkViewConclusion;

        @BindByTag("itemXJWorkViewMoreLayout")
        RelativeLayout itemXJWorkViewMoreLayout;

        @BindByTag("itemXJWorkViewPics")
        CustomGalleryView itemXJWorkViewPics;

        @BindByTag("itemXJWorkViewPicMoreView")
        ImageView itemXJWorkViewPicMoreView;

        @BindByTag("itemXJWorkViewRemark")
        CustomVerticalTextView itemXJWorkViewRemark;

        XJCameraController mXJCameraController;
        String oldImgUrl = "";

        public XJWorkItemContentViewholder(Context context, ViewGroup parent) {
            super(context, parent);
        }

        @Override
        protected int layoutId() {
            return R.layout.item_xj_work_view;
        }

        @Override
        protected void initView() {
            super.initView();
            mXJCameraController = ((XJWorkViewActivity) context).getController(XJCameraController.class);
            mXJCameraController.init(Constant.IMAGE_SAVE_XJPATH, "xjRecord");

        }


        @SuppressLint({"CheckResult", "ClickableViewAccessibility"})
        @Override
        protected void initListener() {
            super.initListener();




            RxView.clicks(itemXJWorkViewRedo)
                    .throttleFirst(200, TimeUnit.MILLISECONDS)
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object o) throws Exception {
                            //redo
                            onItemChildViewClick(itemXJWorkViewRedo, 0, getItem(getAdapterPosition()));
                        }
                    });

            RxView.clicks(itemXJWorkViewPicMoreView)
                    .throttleFirst(200, TimeUnit.MILLISECONDS)
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object o) throws Exception {
                            mXJCameraController.viewPic(itemXJWorkViewPics, itemXJWorkViewPics.getGalleryAdapter().getList(), 0);
                        }
                    });

            itemXJWorkViewPics.setOnChildViewClickListener((childView, action, obj) -> {

                onItemChildViewClick(itemXJWorkViewPics, action, obj);

            });


        }

        @Override
        protected void update(XJWorkEntity data) {
            mXJCameraController.addListener(itemXJWorkViewPics, getAdapterPosition(), XJWorkViewAdapter.this);
            XJInputTypeEntity xjInputTypeEntity = SupPlantApplication.dao().getXJInputTypeEntityDao().queryBuilder()
                    .where(XJInputTypeEntityDao.Properties.Id.eq(data.inputStandardId.id)).unique();
            itemXJWorkViewContent.setText(data.content);
            itemXJWorkViewResult.setText(data.concluse);

            if(data.isRealPass && TextUtils.isEmpty(data.concluse)){
                itemXJWorkViewResult.setText("跳检");
            }

            if(xjInputTypeEntity!=null)
            setUnit(xjInputTypeEntity.unitID!=null?xjInputTypeEntity.unitID.name:"");
            else setUnit("");

            itemXJWorkViewConclusion.setText(data.conclusionName);


            if(data.isConclusionModify && isEditable){
                itemXJWorkViewRedo.setVisibility(View.VISIBLE);
            }
            else{
                itemXJWorkViewRedo.setVisibility(View.GONE);
            }

            if("PATROL_realValue/normal".equals(data.conclusionID)){
                itemXJWorkViewConclusion.setTextColor(context.getResources().getColor(R.color.textColorlightblack));
            }
            else{
                itemXJWorkViewConclusion.setTextColor(context.getResources().getColor(R.color.customRed));
            }

            if(TextUtils.isEmpty(data.xjImgName) && TextUtils.isEmpty(data.realRemark)){
                itemXJWorkViewMoreLayout.setVisibility(View.GONE);
            }
            else{
                itemXJWorkViewMoreLayout.setVisibility(View.VISIBLE);

                //保存图片
                if (!TextUtils.isEmpty(data.xjImgName)) {
                    if (oldImgUrl.equals(data.xjImgName)) {
                        return;
                    }

                    List<String> pics = Arrays.asList(data.xjImgName.split(","));


                    itemXJWorkViewPics.setVisibility(View.VISIBLE);
                    if(pics.size() > 3){

                        itemXJWorkViewPicMoreView.setVisibility(View.VISIBLE);
                    }
                    else{
                        itemXJWorkViewPicMoreView.setVisibility(View.GONE);
                    }
                    FaultPicHelper.initPics(pics, itemXJWorkViewPics);
                    oldImgUrl = data.xjImgName;
                } else {
                    itemXJWorkViewPics.clear();
                    oldImgUrl = "";
                    itemXJWorkViewPicMoreView.setVisibility(View.GONE);
                    itemXJWorkViewPics.setVisibility(View.GONE);
                }

                if(data.realRemark!=null) {
                    itemXJWorkViewRemark.setVisibility(View.VISIBLE);
                    itemXJWorkViewRemark.setContent(data.realRemark);
                    data.remark = data.realRemark;
                }
                else{
                    itemXJWorkViewRemark.setVisibility(View.GONE);
                    itemXJWorkViewRemark.setContent("");
                    data.remark = "";
                }
            }

        }



        private void setUnit(String unitName) {

            if (!TextUtils.isEmpty(unitName)) {
                itemXJWorkViewResultUnit.setVisibility(View.VISIBLE);
                itemXJWorkViewResultUnit.setText(unitName);
            } else {
                itemXJWorkViewResultUnit.setVisibility(View.GONE);
            }

        }

        @Override
        public void onChildViewClick(View childView, int action, Object obj) {
            XJWorkEntity workItemEntity = getItem(getAdapterPosition());  //注：参数obj为空，实现的接口方法，接口中obj为null，非item对象
            onItemChildViewClick(childView, action, workItemEntity);
        }



    }
}
