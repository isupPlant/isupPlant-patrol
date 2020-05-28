package com.supcon.mes.module_xj.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.annotation.BindByTag;
import com.supcon.common.view.base.adapter.BaseListDataRecyclerViewAdapter;
import com.supcon.common.view.base.adapter.viewholder.BaseRecyclerViewHolder;
import com.supcon.mes.middleware.SupPlantApplication;
import com.supcon.mes.middleware.model.bean.xj.XJAreaEntity;
import com.supcon.mes.middleware.model.bean.xj.XJWorkEntity;
import com.supcon.mes.middleware.model.bean.xj.XJWorkEntityDao;
import com.supcon.mes.module_xj.R;

import java.util.List;

/**
 * Created by wangshizhan on 2020/3/24
 * Email:wangshizhan@supcom.com
 */
public class XJTempAreaAdapter extends BaseListDataRecyclerViewAdapter<XJAreaEntity> {

    public XJTempAreaAdapter(Context context) {
        super(context);
    }

    public XJTempAreaAdapter(Context context, List<XJAreaEntity> list) {
        super(context, list);
    }

    @Override
    protected BaseRecyclerViewHolder<XJAreaEntity> getViewHolder(int viewType) {

        return new XJAreaViewHolder(context, parent);
    }


    class XJAreaViewHolder extends BaseRecyclerViewHolder<XJAreaEntity>{

        @BindByTag("itemXJAreaTag")
        ImageView itemXJAreaTag;

        @BindByTag("itemXJAreaName")
        TextView itemXJAreaName;

        @BindByTag("itemXJAreaProcess")
        TextView itemXJAreaProcess;

        @BindByTag("itemXJAreaYHTag")
        ImageView itemXJAreaYHTag;

        public XJAreaViewHolder(Context context) {
            super(context);
        }

        public XJAreaViewHolder(Context context, ViewGroup parent) {
            super(context, parent);
        }

        public XJAreaViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected int layoutId() {
            return R.layout.item_xj_area;
        }

        @Override
        protected void initListener() {
            super.initListener();
            itemView.setOnClickListener(v -> onItemChildViewClick(itemView, 0, getItem(getAdapterPosition())));
        }


        @Override
        protected void update(XJAreaEntity data) {
            itemXJAreaName.setText(" "+(getAdapterPosition()+1)+" - "+data.name);

            if(data.hasYH){
                itemXJAreaYHTag.setVisibility(View.VISIBLE);
            }
            else{
                itemXJAreaYHTag.setVisibility(View.GONE);
            }
            itemXJAreaProcess.setTextColor(context.getResources().getColor(R.color.xjTextColor));
            if(TextUtils.isEmpty(data.process)){
                List<XJWorkEntity> xjWorkEntities = SupPlantApplication.dao().getXJWorkEntityDao().queryBuilder()
                        .where(XJWorkEntityDao.Properties.AreaLongId.eq(data.id))
                        .where(XJWorkEntityDao.Properties.Ip.eq(SupPlantApplication.getIp()))
                        .orderAsc(XJWorkEntityDao.Properties.Sort)
                        .list();

                if(xjWorkEntities!=null && xjWorkEntities.size()!=0){
                    data.process = String.format(context.getString(R.string.xj_area_process), "0",""+xjWorkEntities.size());
                    itemXJAreaProcess.setText(data.process);
                    itemXJAreaTag.setImageResource(R.drawable.ic_xj_area_undone);
                }
                else{
//                    data.isFinished = true;
                    data.process = "0 / 0";
                    itemXJAreaProcess.setText(data.process);
//                    itemXJAreaTag.setImageResource(R.drawable.ic_xj_area_done);
                }

                data.works = xjWorkEntities;
            }
            else{
                data.process = String.format(context.getString(R.string.xj_area_process), ""+data.finishNum,""+data.works.size());
                itemXJAreaProcess.setText(data.process);

                if(data.isFinished){
                    itemXJAreaTag.setImageResource(R.drawable.ic_xj_area_done);
                    itemXJAreaProcess.setTextColor(context.getResources().getColor(R.color.xjBtnColor));
                }
                else if(data.cardTime == 0){
                    itemXJAreaTag.setImageResource(R.drawable.ic_xj_area_undone);
                }
                else{
                    itemXJAreaTag.setImageResource(R.drawable.ic_xj_area_halfdone);

                }
            }
        }

    }


}
