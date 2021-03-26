package com.supcon.mes.module_xj.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.annotation.BindByTag;
import com.jakewharton.rxbinding2.view.RxView;
import com.supcon.common.view.base.adapter.BaseListDataRecyclerViewAdapter;
import com.supcon.common.view.base.adapter.viewholder.BaseRecyclerViewHolder;
import com.supcon.mes.middleware.model.bean.BaseIdValueEntity;
import com.supcon.mes.module_xj.R;


import java.util.concurrent.TimeUnit;


/**
 * @author yangkai2
 * @email yangkai2@supcon.com
 * ------------- Description -------------
*/
public class XJAbnormalChoiceAdapter extends BaseListDataRecyclerViewAdapter<String> {
    public XJAbnormalChoiceAdapter(Context context) {
        super(context);
    }

    @Override
    protected BaseRecyclerViewHolder<String> getViewHolder(int viewType) {
        return new SearchWareViewHolder(context);
    }


    public int selectPostion=-1;
    class SearchWareViewHolder extends BaseRecyclerViewHolder<String> implements View.OnClickListener {


        @BindByTag("choiceTv")
        TextView choiceTv;
        @BindByTag("choiceIv")
        ImageView choiceIv;

        public SearchWareViewHolder(Context context) {
            super(context);
        }

        @Override
        protected void initListener() {
            super.initListener();
            RxView.clicks(itemView)
                    .throttleFirst(2000, TimeUnit.MILLISECONDS)
                    .subscribe(o->{
                       onItemChildViewClick(itemView,0);
                    });
        }


        @Override
        protected int layoutId() {
            return R.layout.item_xj_abnormal_choice;
        }

        @Override
        protected void update(String data) {
            if (selectPostion==getAdapterPosition()){
                choiceIv.setVisibility(View.VISIBLE);
            }else {
                choiceIv.setVisibility(View.GONE);
            }
            choiceTv.setText(data);
        }

        @Override
        public void onClick(View v) {
            onItemChildViewClick(v, 0);
        }
    }


}
