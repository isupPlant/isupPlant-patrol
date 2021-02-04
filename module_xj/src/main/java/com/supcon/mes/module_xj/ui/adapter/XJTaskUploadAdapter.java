package com.supcon.mes.module_xj.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.app.annotation.BindByTag;
import com.supcon.common.view.base.adapter.BaseListDataRecyclerViewAdapter;
import com.supcon.common.view.base.adapter.viewholder.BaseRecyclerViewHolder;
import com.supcon.mes.mbap.utils.DateUtil;
import com.supcon.mes.middleware.model.bean.xj.XJTaskEntity;
import com.supcon.mes.module_xj.R;

public class XJTaskUploadAdapter extends BaseListDataRecyclerViewAdapter<XJTaskEntity> {

    public XJTaskUploadAdapter(Context context) {
        super(context);
    }

    @Override
    protected BaseRecyclerViewHolder<XJTaskEntity> getViewHolder(int viewType) {

        return new XJTaskUpAndDownViewHolder(context, parent);
    }

    class XJTaskUpAndDownViewHolder extends BaseRecyclerViewHolder<XJTaskEntity> {

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
            String startTime = DateUtil.dateFormat(data.startTime, "HH:mm");
            String endTime = DateUtil.dateFormat(data.endTime, "HH:mm");
            itemTaskTime.setText(startTime + "-" + endTime);

            itemTaskTime.setChecked(data.isChecked);
        }
    }
}
