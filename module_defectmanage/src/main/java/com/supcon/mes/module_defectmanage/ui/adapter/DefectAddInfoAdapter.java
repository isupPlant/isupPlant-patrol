package com.supcon.mes.module_defectmanage.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.annotation.BindByTag;
import com.supcon.common.view.base.adapter.HeaderRecyclerViewAdapter;
import com.supcon.common.view.base.adapter.viewholder.BaseRecyclerViewHolder;
import com.supcon.mes.mbap.view.CustomTextView;
import com.supcon.mes.middleware.ui.view.CustomTitleValueSmall;
import com.supcon.mes.middleware.util.StringUtil;
import com.supcon.mes.module_defectmanage.R;
import com.supcon.mes.module_defectmanage.model.bean.DefectModelEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Time:    2021/3/19  11: 03
 * Author： mac
 * Des:
 */
public class DefectAddInfoAdapter extends HeaderRecyclerViewAdapter<DefectModelEntity> {
    Map<Long, DefectModelEntity> checkedMap = new HashMap<>();
    boolean isChoosing = false;

    public void setChoosing(boolean choosing) {
        isChoosing = choosing;
    }

    public DefectAddInfoAdapter(Context context) {
        super(context);
    }

    @Override
    protected BaseRecyclerViewHolder<DefectModelEntity> getViewHolder(int viewType) {
        return new ViewHolder(context, parent);
    }

    class ViewHolder extends BaseRecyclerViewHolder<DefectModelEntity> {

        @BindByTag("findtime")
        CustomTitleValueSmall findtime;
        @BindByTag("finder")
        CustomTitleValueSmall finder;
        @BindByTag("address")
        CustomTitleValueSmall address;
//        @BindByTag("name")
//        CustomTitleValueSmall name;
        @BindByTag("tableNo")
        TextView tableNo;
        @BindByTag("devicename")
        TextView devicename;
        @BindByTag("iv_check")
        ImageView iv_check;
        @BindByTag("status")
        TextView status;

        @Override
        protected void initListener() {
            super.initListener();
            itemView.setOnClickListener(v -> {
                if (onItemChildViewClickListener != null) {
                    onItemChildViewClick(itemView, 0, getItem(getAdapterPosition()));
                }
            });
        }

        public ViewHolder(Context context) {
            super(context);
        }

        public ViewHolder(Context context, ViewGroup parent) {
            super(context, parent);
        }

        public ViewHolder(View itemView) {
            super(itemView);

        }

        @Override
        protected int layoutId() {
            return R.layout.item_defect_add_off_line;
        }

        @Override
        protected void update(DefectModelEntity data) {
            if (isChoosing) {
                iv_check.setVisibility(View.VISIBLE);
                if (checkedMap.containsKey(data.dbId)) {
                    iv_check.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_check_yes));
                } else {
                    iv_check.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_check_no));
                }
            } else {
                iv_check.setVisibility(View.GONE);
            }

//            name.setValue(data.getName());
            devicename.setText(data.getName());
            if (!StringUtil.isBlank(data.eamName)) {
                devicename.setText(data.eamName);
            }
            finder.setValue(data.finderName);
            findtime.setValue(data.findTime);
            address.setValue(data.areaName);
            tableNo.setText(data.getTableNo() + "");

            if (data.isValid) {
                status.setVisibility(View.INVISIBLE);
            } else {
                status.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setCheckedMap(Map<Long, DefectModelEntity> checkedMap) {
        this.checkedMap = checkedMap;
        notifyDataSetChanged();
    }
}
