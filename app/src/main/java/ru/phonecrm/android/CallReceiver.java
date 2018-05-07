package ru.phonecrm.android;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class CallReceiver extends BroadcastReceiver {
    private static boolean incomingCall = false;
    private static WindowManager windowManager;
    private static ViewGroup windowLayout;
    private void isSupportTelephone() {
        PackageManager pm = null;//getPackageManager();
        boolean isTelephonySupported = pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
        boolean isGSMSupported = pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_GSM);
        /*
        PhoneStateListener stateListener = new PhoneStateListener() {
        public void onCallStateChanged(int state, String incomingNumber) {
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE: break;
            case TelephonyManager.CALL_STATE_OFFHOOK: break;
            case TelephonyManager.CALL_STATE_RINGING:
                doMagicWork(incomingNumber); // Поступил звонок с номера incomingNumber
                break;
                }
            }
        };
        https://xakep.ru/2017/03/14/android-hidden-call/
         */
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            if (phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                //Трубка не поднята, телефон звонит
                incomingCall = true;
                showWindow(context, phoneNumber);//добавили
            } else if (phoneState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                //Телефон находится в режиме звонка (набор номера при исходящем звонке / разговор)
                if (incomingCall) {
                    incomingCall = false;
                    closeWindow();
                }
            } else if (phoneState.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                //Телефон находится в ждущем режиме - это событие наступает по окончанию разговора
                //или в ситуации "отказался поднимать трубку и сбросил звонок".
                if (incomingCall) {
                    incomingCall = false;
                    closeWindow();
                }
            }
        }
    }

    private void showWindow(Context context, String phone) {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP;

        windowLayout = (ViewGroup) layoutInflater.inflate(R.layout.info, null);

        TextView textViewNumber=(TextView) windowLayout.findViewById(R.id.textViewNumber);
        Button buttonClose=(Button) windowLayout.findViewById(R.id.buttonClose);
        textViewNumber.setText(phone);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeWindow();
            }
        });
        windowManager.addView(windowLayout, params);
    }

    private void closeWindow() {
        if (windowLayout !=null){
            windowManager.removeView(windowLayout);
            windowLayout =null;
        }
    }
}
