package com.supcon.mes.module_xj.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.supcon.common.view.listener.OnItemChildViewClickListener;
import com.supcon.common.view.util.DisplayUtil;
import com.supcon.common.view.util.LogUtil;
import com.supcon.common.view.util.ToastUtils;
import com.supcon.mes.mbap.utils.SpaceItemDecoration;
import com.supcon.mes.mbap.view.CustomEditText;
import com.supcon.mes.middleware.model.bean.BaseIdValueEntity;
import com.supcon.mes.middleware.model.listener.OnSuccessListener;
import com.supcon.mes.middleware.util.DensityUtil;
import com.supcon.mes.middleware.util.SystemCodeManager;
import com.supcon.mes.module_xj.R;
import com.supcon.mes.module_xj.ui.adapter.XJAbnormalChoiceAdapter;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class XJAbnormalSelectDialog extends Dialog{

    private Context context;
    TextView tvAbnormalTitle;
    CustomEditText editReasonDescription;
    TextView btnCancel;
    TextView btnEnsure;
    RecyclerView contentView;
    String title;
    XJAbnormalChoiceAdapter adapter;
    String selectAbnormalName = null;
    private Map<String, String> abnormalReasonMap;     //异常原因
    public XJAbnormalSelectDialog(Context context,String title, Map<String, String> abnormalReasonMap){
        super(context, R.style.DialogStyle);
        this.context=context;
        this.title=title;
        this.abnormalReasonMap=abnormalReasonMap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_abnormal_reason_popup);
        tvAbnormalTitle = findViewById(R.id.tvAbnormalTitle);
        contentView = findViewById(R.id.recyAbnormalReason);
        editReasonDescription =findViewById(R.id.editReasonDescription);
        btnEnsure=findViewById(R.id.btn_sure);
        btnCancel=findViewById(R.id.btn_cancel);

        adapter=new XJAbnormalChoiceAdapter(context);
        LinearLayoutManager layoutManager=new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        contentView.addItemDecoration(new SpaceItemDecoration(DisplayUtil.dip2px(1, context)));
        contentView.setLayoutManager(layoutManager);
        Window window=getWindow();
        window.setGravity(Gravity.CENTER);
        //获得window窗口的属性
        WindowManager.LayoutParams params = window.getAttributes();
        //设置窗口宽度为充满全屏
        params.width = WindowManager.LayoutParams.MATCH_PARENT;//如果不设置,可能部分机型出现左右有空隙,也就是产生margin的感觉
        //设置窗口高度为包裹内容
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //将设置好的属性set回去
        window.setAttributes(params);

        this.setCanceledOnTouchOutside(false);
    }



    @Override
    protected void onStart() {
        super.onStart();
       tvAbnormalTitle.setText(title);
        List<String> values = new ArrayList<>();
        values.addAll(abnormalReasonMap.values());
        adapter.setList(values);
        adapter.selectPostion=0;
        selectAbnormalName=values.get(0);
        contentView.setAdapter(adapter);

        ViewGroup.LayoutParams lp = contentView.getLayoutParams();
        if (values.size() > 4) {
            lp.height = DensityUtil.dp2px(context,40 * 4);
        } else {
            lp.height = DensityUtil.dp2px(context,40 * values.size());
        }
        contentView.setLayoutParams(lp);

        adapter.setOnItemChildViewClickListener(new OnItemChildViewClickListener() {
            @Override
            public void onItemChildViewClick(View childView, int position, int action, Object obj) {
                adapter.selectPostion=position;
                selectAbnormalName=(String) obj;
                adapter.notifyDataSetChanged();
            }
        });
        RxView.clicks(btnEnsure)
                .throttleFirst(2000, TimeUnit.MILLISECONDS)
                .subscribe(o->{
                    String selectAbnormalId = null;
                    for (String key : abnormalReasonMap.keySet()) {
                        String value = abnormalReasonMap.get(key);
                        if (value != null && value.equals(selectAbnormalName)) {
                            selectAbnormalId = key;
                            break;
                        }
                    }
                    if (selectAbnormalId==null){
                        return;
                    }
                    //TODO 这个判断值需要改
                    if (selectAbnormalId.equals("其他")&&TextUtils.isEmpty(editReasonDescription.getContent())) {
                        ToastUtils.show(context, context.getResources().getString(R.string.please_input_eason_description));
                        return;
                    }
                    OnSureListener.onSuccess(selectAbnormalId, editReasonDescription.getContent());
                    dismiss();
                });
        RxView.clicks(btnCancel)
                .throttleFirst(2000, TimeUnit.MILLISECONDS)
                .subscribe(o->{
                    dismiss();
                });

    }



    OnSureListener OnSureListener;
    public void setOnSureListener(OnSureListener onSureListener) {
        this.OnSureListener = onSureListener;
    }
    public interface OnSureListener {

        void onSuccess(String selectAbnormalId,String season);

    }

}
