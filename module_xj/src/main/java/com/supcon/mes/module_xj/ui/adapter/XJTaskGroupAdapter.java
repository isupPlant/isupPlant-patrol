package com.supcon.mes.module_xj.ui.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.annotation.BindByTag;
import com.supcon.common.view.base.adapter.BaseListDataRecyclerViewAdapter;
import com.supcon.common.view.base.adapter.viewholder.BaseRecyclerViewHolder;
import com.supcon.mes.mbap.utils.DateUtil;
import com.supcon.mes.module_xj.R;
import com.supcon.mes.module_xj.model.bean.XJTaskEntity;
import com.supcon.mes.module_xj.model.bean.XJTaskGroupEntity;

import java.util.List;

/**
 * Created by wangshizhan on 2020/1/9
 * Email:wangshizhan@supcom.com
 */
public class XJTaskGroupAdapter extends BaseListDataRecyclerViewAdapter<XJTaskGroupEntity> {
    public XJTaskGroupAdapter(Context context) {
        super(context);
    }

    @Override
    protected BaseRecyclerViewHolder<XJTaskGroupEntity> getViewHolder(int viewType) {
        return new XJTaskRouterViewHolder(context, parent);
    }
    private List<XJTaskEntity> mXJTaskEntity;
    class XJTaskRouterViewHolder extends BaseRecyclerViewHolder<XJTaskGroupEntity>{


        @BindByTag("xjTaskGroupName")
        TextView xjTaskGroupName;

        @BindByTag("xjTaskGroupType")
        TextView xjTaskGroupType;

        @BindByTag("xjTaskGroupStaff")
        TextView xjTaskGroupStaff;

        @BindByTag("xjTaskGroupDate")
        TextView xjTaskGroupDate;

        @BindByTag("xjTaskGroupRecyclerView")
        RecyclerView xjTaskGroupRecyclerView;

        private XJTaskAdapter mXJTaskAdapter;

        public XJTaskRouterViewHolder(Context context, ViewGroup parent) {
            super(context, parent);
        }

        @Override
        protected int layoutId() {
            return R.layout.item_xj_task_group;
        }

        @Override
        protected void initView() {
            super.initView();
            mXJTaskAdapter = new XJTaskAdapter(context);
        }

        @Override
        protected void initListener() {
            super.initListener();
            mXJTaskAdapter.setOnItemChildViewClickListener(
                    (childView, position, action, obj) ->
                            onItemChildViewClick(xjTaskGroupRecyclerView, action, obj));
        }

        @Override
        protected void update(XJTaskGroupEntity data) {
            xjTaskGroupName.setText(data.name);
            xjTaskGroupType.setText(data.typeValue);

            xjTaskGroupStaff.setText(data.staffName);
            xjTaskGroupDate.setText(DateUtil.dateFormat(data.date));

            if(data.taskEntities!=null){

//                if(mXJTaskAdapter == null){
//                    mXJTaskAdapter = new XJTaskAdapter(context);
//
//                    xjTaskGroupRecyclerView.setLayoutManager(new GridLayoutManager(context, data.spanCount));
//                    xjTaskGroupRecyclerView.setAdapter(mXJTaskAdapter);
//                }
                xjTaskGroupRecyclerView.setLayoutManager(new GridLayoutManager(context, data.spanCount));
                xjTaskGroupRecyclerView.setAdapter(mXJTaskAdapter);
                mXJTaskAdapter.setList(data.taskEntities);
                mXJTaskAdapter.notifyDataSetChanged();
                setXJTaskEntity(data.taskEntities);
            }

        }

    }
    public void setXJTaskEntity(List<XJTaskEntity> xjTaskEntity){
        this.mXJTaskEntity=xjTaskEntity;
    }
    public List<XJTaskEntity> getXJTaskEntity(){
        return mXJTaskEntity;
    }

}
