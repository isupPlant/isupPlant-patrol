package com.supcon.mes.module_xj.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;



import com.app.annotation.BindByTag;
import com.app.annotation.apt.Widget;
import com.supcon.common.view.base.view.BaseWidgetLayout;
import com.supcon.common.view.util.LogUtil;
import com.supcon.mes.middleware.IntentRouter;
import com.supcon.mes.middleware.SupPlantApplication;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.model.listener.OnAPIResultListener;
import com.supcon.mes.module_xj.R;
import com.supcon.mes.module_xj.controller.XJTaskIssuedController;
import com.supcon.mes.module_xj.controller.XJTaskNoIssuedController;
import com.supcon.mes.module_xj.controller.XJTaskUploadController;

/**
 * Created by wangshizhan on 2019/10/28
 * Email:wangshizhan@supcom.com
 */
@Widget(Constant.Widget.XJHZ)
public class XJTaskWidget extends BaseWidgetLayout {

    @BindByTag("widgetTitle")
    TextView widgetTitle;

    @BindByTag("widgetXJTaskNewLayout")
    LinearLayout widgetXJTaskNewLayout;

    @BindByTag("taskNewNum")
    TextView taskNewNum;

    @BindByTag("taskExecuteLayout")
    LinearLayout taskExecuteLayout;

    @BindByTag("taskExecuteNum")
    TextView taskExecuteNum;

    @BindByTag("taskDoneLayout")
    LinearLayout taskDoneLayout;

    @BindByTag("taskDoneNum")
    TextView taskDoneNum;

//    @BindByTag("taskCancelLayout")
//    LinearLayout taskCancelLayout;

    @BindByTag("widgetMore")
    ImageView widgetMore;

    XJTaskUploadController mXJTaskUploadController;
    XJTaskNoIssuedController mXJTaskNoIssuedController;
    XJTaskIssuedController mXJTaskIssuedController;


    private Activity mActivity;

    public XJTaskWidget(Context context) {
        super(context);
        mActivity = (Activity) context;
    }

    public XJTaskWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void bind(ViewStub view) {
        super.bind(view);
    }

    @Override
    public void doRefresh(long intervel) {
        super.doRefresh(intervel);
    }

    @Override
    protected void refresh() {
        super.refresh();

//        if(isFrontActivity()) {
            mXJTaskUploadController.onResume();
            mXJTaskIssuedController.onResume();
            mXJTaskNoIssuedController.onResume();
//        }
    }

    @Override
    protected int layoutId() {
        return R.layout.v_widget_xj_task;
    }

    @Override
    protected void initView() {
        super.initView();
        widgetTitle.setText(getContext().getString(R.string.xj_task_total));
        mXJTaskUploadController = new XJTaskUploadController(context);
        mXJTaskUploadController.onInit();
        mXJTaskUploadController.initData();
        mXJTaskIssuedController = new XJTaskIssuedController(context);
        mXJTaskIssuedController.onInit();
        mXJTaskNoIssuedController = new XJTaskNoIssuedController(context);
        mXJTaskNoIssuedController.onInit();

    }

    @Override
    protected void initListener() {
        super.initListener();
        widgetXJTaskNewLayout.setOnClickListener(v -> goXJTaskList());

        taskExecuteLayout.setOnClickListener(v -> goXJTaskList());

        taskDoneLayout.setOnClickListener(v -> goXJTaskList());

//        taskCancelLayout.setOnClickListener(v -> LogUtil.d("taskCancelLayout click"));

        widgetMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                goXJTaskList();
            }
        });

        mXJTaskNoIssuedController.setOnResultListener(new OnAPIResultListener<Integer>() {
            @Override
            public void onFail(String errorMsg) {

            }

            @Override
            public void onSuccess(Integer result) {
                taskNewNum.setText(""+result);
            }
        });

        mXJTaskIssuedController.setOnResultListener(new OnAPIResultListener<Integer>() {
            @Override
            public void onFail(String errorMsg) {

            }

            @Override
            public void onSuccess(Integer result) {
                taskExecuteNum.setText(""+result);
            }
        });

        mXJTaskUploadController.setOnResultListener(new OnAPIResultListener<Integer>() {
            @Override
            public void onFail(String errorMsg) {

            }

            @Override
            public void onSuccess(Integer result) {
                taskDoneNum.setText(""+result);
            }
        });

    }

    private void goXJTaskList() {

        IntentRouter.go(getContext(), Constant.AppCode.MPS_Patrol);

    }


//    private boolean isFrontActivity(){
//
//        return mActivity.equals(SupPlantApplication.getAppContext().getCurActivity());
//
//    }
}
