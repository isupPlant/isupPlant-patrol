package com.supcon.mes.module_defectmanage.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.annotation.BindByTag;
import com.supcon.common.view.base.adapter.HeaderRecyclerViewAdapter;
import com.supcon.common.view.base.adapter.viewholder.BaseRecyclerViewHolder;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.model.bean.SystemCodeEntity;
import com.supcon.mes.middleware.ui.view.CustomTitleValueSmall;
import com.supcon.mes.middleware.util.StringUtil;
import com.supcon.mes.middleware.util.SystemCodeManager;
import com.supcon.mes.module_defectmanage.R;
import com.supcon.mes.module_defectmanage.model.bean.DefectOnlineEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Time:    2021/3/19  11: 03
 * Author： mac
 * Des:
 */
public class DefectOnlineAdapter extends HeaderRecyclerViewAdapter<DefectOnlineEntity> {
    List<SystemCodeEntity> systemCodeEntityList;
    Map<String, String> map = new HashMap<>();

    public DefectOnlineAdapter(Context context) {
        super(context);
       systemCodeEntityList = SystemCodeManager.getInstance().getSystemCodeListByCode(Constant.SystemCode.DefectManage_problemClass);
        if (systemCodeEntityList != null && systemCodeEntityList.size() > 0) {
            for (SystemCodeEntity systemCodeEntity : systemCodeEntityList) {
                map.put(systemCodeEntity.key, systemCodeEntity.getValue());
            }
        }
    }

    @Override
    protected BaseRecyclerViewHolder<DefectOnlineEntity> getViewHolder(int viewType) {
        return new ViewHolder(context, parent);
    }

    class ViewHolder extends BaseRecyclerViewHolder<DefectOnlineEntity> {

        @BindByTag("findtime")
        CustomTitleValueSmall findtime;
        @BindByTag("finder")
        CustomTitleValueSmall finder;
        @BindByTag("address")
        CustomTitleValueSmall address;
        @BindByTag("name")
        CustomTitleValueSmall name;
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
            return R.layout.item_defect_add_on_line;
        }

        @Override
        protected void update(DefectOnlineEntity data) {

            name.setValue(data.getName());
            if (data.getFinder() != null) {
                finder.setValue(data.getFinder().getName());
            } else {
                finder.setValue("");
            }

            findtime.setValue(data.getFindTime());

            if (data.getArea() != null) {
                address.setValue(data.getArea().getName());
            }

            devicename.setText(data.getName());
            if (data.getEam() != null) {
                devicename.setText(data.getEam().getName());
            }
            if (!StringUtil.isBlank(data.getTableNo())) {
                tableNo.setText(data.getTableNo() + "");
            } else {
                tableNo.setText("");
            }

            status.setVisibility(View.VISIBLE);

            //根据系统编码来做
            if (data.getDefectState() != null) {
                if (systemCodeEntityList == null) {
                    systemCodeEntityList = SystemCodeManager.getInstance().getSystemCodeListByCode(Constant.SystemCode.DefectManage_problemClass);

                }
                if (systemCodeEntityList != null && systemCodeEntityList.size() > 0) {
                    if (systemCodeEntityList != null && systemCodeEntityList.size() > 0) {
                        for (SystemCodeEntity systemCodeEntity : systemCodeEntityList) {
                            map.put(systemCodeEntity.key, systemCodeEntity.getValue());
                        }
                    }
                }
                status.setText(map.get(data.getDefectState().getCode()));
            } else {
                status.setText("");

            }

        }
    }

}
