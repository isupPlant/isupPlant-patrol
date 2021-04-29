package com.supcon.mes.module_xj.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.app.annotation.BindByTag;
import com.jakewharton.rxbinding2.view.RxView;
import com.supcon.common.view.base.adapter.BaseListDataRecyclerViewAdapter;
import com.supcon.common.view.base.adapter.viewholder.BaseRecyclerViewHolder;
import com.supcon.mes.mbap.utils.DateUtil;
import com.supcon.mes.middleware.IntentRouter;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.model.bean.xj.XJTaskEntity;
import com.supcon.mes.patrol.R;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by wangshizhan on 2020/3/24
 * Email:wangshizhan@supcom.com
 */
public class XJTaskAdapter extends BaseListDataRecyclerViewAdapter<XJTaskEntity> {

    public XJTaskAdapter(Context context) {
        super(context);
    }

    public XJTaskAdapter(Context context, List<XJTaskEntity> list) {
        super(context, list);
    }

    @Override
    protected BaseRecyclerViewHolder<XJTaskEntity> getViewHolder(int viewType) {

        if(viewType == 1){
            return new XJTaskUpAndDownViewHolder(context, parent);
        }

        return new XJTaskViewHolder(context, parent);
    }

    @Override
    public int getItemViewType(int position, XJTaskEntity xjTaskEntity) {
        return xjTaskEntity.viewType;
    }

    class XJTaskViewHolder extends BaseRecyclerViewHolder<XJTaskEntity>{

        @BindByTag("itemTaskTime")
        TextView itemTaskTime;

        @BindByTag("itemTaskStatus")
        TextView itemTaskStatus;

        public XJTaskViewHolder(Context context) {
            super(context);
        }

        public XJTaskViewHolder(Context context, ViewGroup parent) {
            super(context, parent);
        }

        public XJTaskViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected int layoutId() {
            return R.layout.item_xj_task;
        }

        @Override
        protected void initListener() {
            super.initListener();
            RxView.clicks(itemTaskStatus)
                    .throttleFirst(200, TimeUnit.MILLISECONDS)
                    .subscribe(o -> onItemChildViewClick(itemTaskStatus, 0, getItem(getAdapterPosition())));
        }

        @Override
        protected void update(XJTaskEntity data) {
            String startTime = DateUtil.dateFormat(data.startTime,"HH:mm");
            String endTime = DateUtil.dateFormat(data.endTime,"HH:mm");
            itemTaskTime.setText(startTime+"-"+endTime);


            if(data.isFinished){
                itemTaskStatus.setText(context.getString(R.string.xj_task_checked));
                itemTaskStatus.setBackgroundResource(R.drawable.sl_xj_task_blue);
            }
            else{
                itemTaskStatus.setText(context.getString(R.string.xj_task_uncheck));
                itemTaskStatus.setBackgroundResource(R.drawable.sl_xj_task_red);
            }
//            if(data.taskState.id.equals("PATROL_taskState/running")){
//                itemTaskStatus.setText(context.getString(R.string.xj_task_running));
//                itemTaskStatus.setBackgroundResource(R.drawable.sl_xj_task_2ba966);
//            }



        }
    }

    class XJTaskUpAndDownViewHolder extends BaseRecyclerViewHolder<XJTaskEntity>{

        @BindByTag("itemTaskTime")
        CheckBox itemTaskTime;

        public XJTaskUpAndDownViewHolder(Context context) {
            super(context);
        }

        public XJTaskUpAndDownViewHolder(Context context, ViewGroup parent) {
            super(context, parent);
        }

        public XJTaskUpAndDownViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected int layoutId() {
            return R.layout.item_xj_task_up_down;
        }

        @Override
        protected void initView() {
            super.initView();


        }

        @Override
        protected void initListener() {
            super.initListener();


            itemTaskTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    XJTaskEntity xjTaskEntity = getItem(getAdapterPosition());
                    xjTaskEntity.isChecked = isChecked;
                }
            });

        }

        @Override
        protected void update(XJTaskEntity data) {
            String startTime = DateUtil.dateFormat(data.startTime,"HH:mm");
            String endTime = DateUtil.dateFormat(data.endTime,"HH:mm");
            itemTaskTime.setText(startTime+"-"+endTime);

            itemTaskTime.setChecked(data.isChecked);
        }
    }
}
