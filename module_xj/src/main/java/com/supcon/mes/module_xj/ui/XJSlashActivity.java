package com.supcon.mes.module_xj.ui;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;



import com.supcon.common.view.util.LogUtil;
import com.supcon.mes.mbap.MBapApp;
import com.supcon.mes.middleware.SupPlantApplication;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.module_xj.IntentRouter;

/**
 * Created by wangshizhan on 2020/1/10
 * Email:wangshizhan@supcom.com
 */
public class XJSlashActivity extends Activity {

    String activityRouter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityRouter = getActivityRouter();

//        LogUtil.e("XJSlashActivity activityRouter:"+activityRouter);
        if(!TextUtils.isEmpty(activityRouter)){
            SupPlantApplication.exitMain();
        }

        new Handler().postDelayed(() -> {

            Bundle bundle = new Bundle();

//            if (SBTUtil.isSBT()) {
//
//                int initCode = SB2Config.BARCORD;
//
//                if(SBTUtil.isSupportUHF() && SharedPreferencesUtils.getParam(WelcomeActivity.this, Constant.SPKey.UHF_ENABLE, true))
//                {
//                    initCode |= SB2Config.UHF;
//                    SharedPreferencesUtils.setParam(WelcomeActivity.this, Constant.SPKey.UHF_ENABLE, true);
//                }
//
//                if(SBTUtil.isSupportTemp() &&SharedPreferencesUtils.getParam(WelcomeActivity.this, Constant.SPKey.TEMP_MODE, TemperatureMode.SBT.getCode()) == TemperatureMode.SBT.getCode()){
//                    initCode |= SB2Config.TEMPERATURE;
//                    SharedPreferencesUtils.setParam(WelcomeActivity.this, Constant.SPKey.TEMP_MODE, TemperatureMode.SBT.getCode());
//                }
//
//                bundle.putInt(SB2Constant.IntentKey.SB2_CONFIG_CODE, initCode);
//            }

            if (MBapApp.isIsLogin()) {

                bundle.putString(Constant.IntentKey.ACTIVITY_ROUTER, activityRouter);
                IntentRouter.go(this, activityRouter, bundle);

            } else {
//                bundle.putInt(Constant.IntentKey.LOGIN_LOGO_ID, R.drawable.ic_login_logo);
                bundle.putBoolean(Constant.IntentKey.FIRST_LOGIN, true);
                IntentRouter.go(this, Constant.Router.LOGIN, bundle);
            }

            finish();
        }, 500);
    }


    private String getActivityRouter() {

        ActivityInfo info;
        String router = "";
        try {
            info = this.getPackageManager().getActivityInfo(getComponentName(),
                    PackageManager.GET_META_DATA);

            if(info.metaData == null){
                return router;
            }

            router = info.metaData.getString("ACTIVITY_ROUTER");


        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return router;

    }
}
