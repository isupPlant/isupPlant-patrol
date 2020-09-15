package com.supcon.mes.module_xj.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.supcon.common.view.util.LogUtil;
import com.supcon.common.view.util.SharedPreferencesUtils;
import com.supcon.mes.middleware.SupPlantApplication;
import com.supcon.mes.middleware.constant.Constant;
import com.supcon.mes.middleware.model.bean.BAP5CommonEntity;
import com.supcon.mes.module_xj.model.contract.XJRealTimeUploadLoactionContract;
import com.supcon.mes.module_xj.presenter.XJUploadLoactionPresneter;
import com.supcon.mes.module_xj.util.GaoDeToGpsLocationUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by wangshizhan on 2017/12/18.
 * Email:wangshizhan@supcon.com
 */

public class RealTimeUploadLoactionService extends IntentService implements XJRealTimeUploadLoactionContract.View{

    private static final String LOCATION_LOOP_START = "LOCATION_LOOP_START";
    private static final String LOCATION_LOOP_STOP = "LOCATION_LOOP_STOP";
    private static final String LOCATION_LOOP_RESET = "LOCATION_LOOP_RESET";

    private XJUploadLoactionPresneter mXJUploadLoactionPresneter;
    Disposable timer;
    private Map<String,Object> params=new HashMap<>();
    long delay=60;
    public  static boolean locationIsStart;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public RealTimeUploadLoactionService() {
        super("RealTimeUploadLoactionService");
        if (mXJUploadLoactionPresneter==null){
            mXJUploadLoactionPresneter = new XJUploadLoactionPresneter();
            mXJUploadLoactionPresneter.attachView(this);
        }
    }

    public static void startUploadLoactionLoop(Context context){
        LogUtil.i("startUploadLoactionLoop");
        Intent intent = new Intent(context, RealTimeUploadLoactionService.class);
        intent.setAction(LOCATION_LOOP_START);
        context.startService(intent);
    }

    public static void stopUploadLoactionLoop(Context context){
        LogUtil.i("stopUploadLoactionLoop");
        Intent intent = new Intent(context, RealTimeUploadLoactionService.class);
        intent.setAction(LOCATION_LOOP_STOP);
        context.startService(intent);
    }

    public static void resetUploadLoactionLoop(Context context){
        Intent intent = new Intent(context, RealTimeUploadLoactionService.class);
        intent.setAction(LOCATION_LOOP_RESET);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action = intent.getAction();
        handleAction(action);
    }

    private void handleAction(String action) {

        switch (action){

            case LOCATION_LOOP_START:
                locationIsStart=true;
                startTimer();
                break;

            case LOCATION_LOOP_RESET:
                resetTimer();
                break;
            case LOCATION_LOOP_STOP:
                locationIsStart=false;
                stopTimer();
               break;


        }

    }

    public void startLoginLoop(){

        startTimer();
    }

    public void stopLoginLoop(){
        stopTimer();
    }

    public void resetLoginLoop(){
        resetTimer();
    }


    private void startTimer() {
        if (locationIsStart) {
            params.clear();
            params.put("userId", SupPlantApplication.getAccountInfo().userId);
            timer = Flowable.timer(delay, TimeUnit.SECONDS)
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                        if (!TextUtils.isEmpty( SharedPreferencesUtils.getParam(SupPlantApplication.getAppContext(),Constant.SPKey.LONGITUADE,""))&&
                                !TextUtils.isEmpty( SharedPreferencesUtils.getParam(SupPlantApplication.getAppContext(),Constant.SPKey.LATITUADE,""))){
                                //判断是否获取到经纬度，如果没有获取到重启定时器
                            double[]  gcj02 = GaoDeToGpsLocationUtils.gcj02_To_Gps84(Double.parseDouble(SharedPreferencesUtils.getParam(SupPlantApplication.getAppContext(),Constant.SPKey.LATITUADE,"")),
                                    Double.parseDouble(SharedPreferencesUtils.getParam(SupPlantApplication.getAppContext(),Constant.SPKey.LONGITUADE,"")));
                            params.put("longitude", gcj02[1]);
                            params.put("latitude", gcj02[0]);
                            mXJUploadLoactionPresneter.uploadLoaction(params);
                        }else{
                            resetTimer();
                        }
                             //本地测试代码
//                            double[]  gcj02 = GaoDeToGpsLocationUtils.gcj02_To_Gps84(30.179695,120.13925);
//                            params.put("longitude", gcj02[1]);
//                            params.put("latitude", gcj02[0]);
//                            mXJUploadLoactionPresneter.uploadLoaction(params);
                        }
                    });
        }
    }

    private void stopTimer() {
        if(timer!=null){
            timer.dispose();
            timer = null;
        }
    }

    private void resetTimer(){
        stopTimer();
        startTimer();
    }


    @Override
    public void uploadLoactionSuccess(BAP5CommonEntity entity) {
        resetTimer();
    }

    @Override
    public void uploadLoactionFailed(String errorMsg) {
        LogUtil.w(errorMsg);
        resetTimer();
    }

}
