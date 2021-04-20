package com.supcon.mes.module_xj_temp.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.annotation.BindByTag;
import com.jakewharton.rxbinding2.view.RxView;
import com.supcon.common.view.base.adapter.BaseListDataRecyclerViewAdapter;
import com.supcon.common.view.base.adapter.viewholder.BaseRecyclerViewHolder;
import com.supcon.mes.mbap.utils.GsonUtil;
import com.supcon.mes.middleware.SupPlantApplication;
import com.supcon.mes.middleware.model.bean.xj.XJTaskAreaEntity;
import com.supcon.mes.middleware.model.bean.xj.XJTaskWorkEntity;
import com.supcon.mes.middleware.model.bean.xj.XJWorkEntity;
import com.supcon.mes.middleware.model.bean.xj.XJWorkEntityDao;
import com.supcon.mes.patrol.R;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;

/**
 * Created by wangshizhan on 2020/3/24
 * Email:wangshizhan@supcom.com
 */
public class XJTempAreaAdapter extends BaseListDataRecyclerViewAdapter<XJTaskAreaEntity> {

    public XJTempAreaAdapter(Context context) {
        super(context);
    }

    public XJTempAreaAdapter(Context context, List<XJTaskAreaEntity> list) {
        super(context, list);
    }

    @Override
    protected BaseRecyclerViewHolder<XJTaskAreaEntity> getViewHolder(int viewType) {

        return new XJTempAreaViewHolder(context, parent);
    }


    class XJTempAreaViewHolder extends BaseRecyclerViewHolder<XJTaskAreaEntity>{

        @BindByTag("itemXJTempAreaTag")
        ImageView itemXJTempAreaTag;

        @BindByTag("itemXJTempAreaName")
        TextView itemXJTempAreaName;

        @BindByTag("itemXJTempAreaWorkNum")
        TextView itemXJTempAreaWorkNum;

        @BindByTag("itemXJTempAreaCheckbox")
        ImageView itemXJTempAreaCheckbox;

        public XJTempAreaViewHolder(Context context) {
            super(context);
        }

        public XJTempAreaViewHolder(Context context, ViewGroup parent) {
            super(context, parent);
        }

        public XJTempAreaViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected int layoutId() {
            return R.layout.item_xj_temp_area2;
        }

        @Override
        protected void initView() {
            super.initView();

        }

        @SuppressLint("CheckResult")
        @Override
        protected void initListener() {
            super.initListener();

            RxView.clicks(itemXJTempAreaCheckbox)
                    .throttleFirst(200, TimeUnit.MILLISECONDS)
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object o) throws Exception {
                            XJTaskAreaEntity xjAreaEntity = getItem(getAdapterPosition());
                            if(xjAreaEntity != null){
                                xjAreaEntity.isChecked = !xjAreaEntity.isChecked;
                                notifyItemChanged(getAdapterPosition());
                            }

                        }
                    });
            RxView.clicks(itemView)
                    .throttleFirst(200, TimeUnit.MILLISECONDS)
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object o) throws Exception {
                            XJTaskAreaEntity xjAreaEntity = getItem(getAdapterPosition());
                            if(xjAreaEntity != null){
                                xjAreaEntity.isChecked = !xjAreaEntity.isChecked;
                                notifyItemChanged(getAdapterPosition());
                            }

                        }
                    });
        }


        @Override
        protected void update(XJTaskAreaEntity data) {
            itemXJTempAreaName.setText(String.valueOf((getAdapterPosition() + 1) + " - " + data.name));

            if (data.isChecked) {
                itemXJTempAreaCheckbox.setImageResource(R.drawable.ic_choose_yes);
            } else {
                itemXJTempAreaCheckbox.setImageResource(R.drawable.ic_choose_no);
            }

            if (data.works != null) {
                itemXJTempAreaWorkNum.setText(String.valueOf(data.works.size()));
                itemXJTempAreaWorkNum.setText(String.valueOf(data.works.size()));
            } else {
                List<XJWorkEntity> xjWorkEntities = SupPlantApplication.dao().getXJWorkEntityDao().queryBuilder()
                        .where(XJWorkEntityDao.Properties.AreaLongId.eq(data.id))
                        .where(XJWorkEntityDao.Properties.Ip.eq(SupPlantApplication.getIp()))
                        .orderAsc(XJWorkEntityDao.Properties.Sort)
                        .list();


                if (xjWorkEntities != null && xjWorkEntities.size() != 0) {
                    itemXJTempAreaWorkNum.setText(String.valueOf(xjWorkEntities.size()));
                } else {
                    itemXJTempAreaWorkNum.setText("0");
                }

                String s= GsonUtil.gsonString(xjWorkEntities);
                List<XJTaskWorkEntity> xjTaskWorkEntities=GsonUtil.jsonToList(s,XJTaskWorkEntity.class);
                data.works = xjTaskWorkEntities;
            }


        }

    }


}
