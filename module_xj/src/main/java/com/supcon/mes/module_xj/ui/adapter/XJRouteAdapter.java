package com.supcon.mes.module_xj.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.annotation.BindByTag;
import com.supcon.common.view.base.adapter.BaseListDataRecyclerViewAdapter;
import com.supcon.common.view.base.adapter.viewholder.BaseRecyclerViewHolder;
import com.supcon.mes.middleware.model.bean.xj.XJRouteEntity;
import com.supcon.mes.module_xj.R;

import java.util.List;

/**
 * Created by wangshizhan on 2020/5/30
 * Email:wangshizhan@supcom.com
 */
public class XJRouteAdapter extends BaseListDataRecyclerViewAdapter<XJRouteEntity> {
    public XJRouteAdapter(Context context) {
        super(context);
    }

    public XJRouteAdapter(Context context, List<XJRouteEntity> list) {
        super(context, list);
    }

    @Override
    protected BaseRecyclerViewHolder<XJRouteEntity> getViewHolder(int viewType) {
        return new XJRouteViewHolder(context);
    }

    class XJRouteViewHolder extends BaseRecyclerViewHolder<XJRouteEntity>{

        @BindByTag("xjRouteName")
        TextView xjRouteName;

        @BindByTag("xjRouteType")
        TextView xjRouteType;

        @BindByTag("xjRouteRemark")
        TextView xjRouteRemark;

        public XJRouteViewHolder(Context context) {
            super(context,parent);
        }

        @Override
        protected int layoutId() {
            return R.layout.item_xj_route;
        }

        @Override
        protected void initListener() {
            super.initListener();
            itemView.setOnClickListener(v -> onItemChildViewClick(itemView, 0, getItem(getAdapterPosition())));
        }

        @Override
        protected void update(XJRouteEntity data) {
            xjRouteName.setText(data.name);
            if(data.dept!=null&&data.dept.name!=null)
                xjRouteType.setText(data.dept.name);
            else xjRouteType.setText("");

            if(!TextUtils.isEmpty(data.remark)){
                xjRouteRemark.setVisibility(View.VISIBLE);
                xjRouteRemark.setText(data.remark);
            }
            else{
                xjRouteRemark.setVisibility(View.GONE);
                xjRouteRemark.setText("");
            }
        }
    }

}
