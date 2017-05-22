package com.example.shinz.gsdemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by ShinZ on 2017/5/7.
 */

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnSendMsg, activity_register_register;
    private EditText activity_register_username, activity_register_password, activity_register_phone, etCode;
    private int i = 60;//倒计时

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        activity_register_username = (EditText) findViewById(R.id.activity_register_username);
        activity_register_password = (EditText) findViewById(R.id.activity_register_password);
        activity_register_phone = (EditText) findViewById(R.id.activity_register_phone);
        etCode = (EditText) findViewById(R.id.etCode);
        btnSendMsg = (Button) findViewById(R.id.btnSendMsg);
        activity_register_register = (Button) findViewById(R.id.activity_register_register);

        // 启动短信验证sdk
        SMSSDK.initSDK(RegisterActivity.this, "1da1ff6f02d9f", "7e6daf31177d836651751871f34bb720");

        //initSDK方法是短信SDK的入口，需要传递您从MOB应用管理后台中注册的SMSSDK的应用AppKey和AppSecrete，如果填写错误，后续的操作都将不能进行
        EventHandler eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        //注册回调监听接口
        SMSSDK.registerEventHandler(eventHandler);

        btnSendMsg.setOnClickListener(this);
        activity_register_register.setOnClickListener(this);
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == -1) {
                btnSendMsg.setText(i + " s");
            } else if (msg.what == -2) {
                btnSendMsg.setText("重新发送");
                btnSendMsg.setClickable(true);
                i = 60;
            } else {
                int event = msg.arg1;
                int result = msg.arg2;
                Object data = msg.obj;
                Log.e("asd", "event=" + event + "  result=" + result + "  ---> result=-1 success , result=0 error");
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // 短信注册成功后，返回MainActivity,然后提示
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        // 提交验证码成功,调用注册接口，之后直接登录
                        //当号码来自短信注册页面时调用登录注册接口
                        //当号码来自绑定页面时调用绑定手机号码接口

                        Toast.makeText(getApplicationContext(), "短信验证成功",
                                Toast.LENGTH_SHORT).show();

                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        Toast.makeText(getApplicationContext(), "验证码已经发送",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        ((Throwable) data).printStackTrace();
                    }
                } else if (result == SMSSDK.RESULT_ERROR) {
                    try {
                        Throwable throwable = (Throwable) data;
                        throwable.printStackTrace();
                        JSONObject object = new JSONObject(throwable.getMessage());
                        String des = object.optString("detail");//错误描述
                        int status = object.optInt("status");//错误代码
                        if (status > 0 && !TextUtils.isEmpty(des)) {
                            Log.e("asd", "des: " + des);
                            Toast.makeText(RegisterActivity.this, des, Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (Exception e) {
                        //do something
                    }
                }
            }
        }
    };

    @Override
    public void onClick(View v) {
        String phoneNum = activity_register_phone.getText().toString().trim();
        switch (v.getId()) {
            case R.id.btnSendMsg:
                if (TextUtils.isEmpty(phoneNum)) {
                    Toast.makeText(getApplicationContext(), "手机号码不能为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                SMSSDK.getVerificationCode("86", phoneNum);
                btnSendMsg.setClickable(false);
                //开始倒计时
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (; i > 0; i--) {
                            handler.sendEmptyMessage(-1);
                            if (i <= 0) {
                                break;
                            }
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        handler.sendEmptyMessage(-2);
                    }
                }).start();
                break;

            case R.id.activity_register_register:
                String code = etCode.getText().toString().trim();
                String username = activity_register_username.getText().toString().trim();
                String password = activity_register_password.getText().toString().trim();
                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(getApplicationContext(), "用户名不能为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(),"密码不能为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(phoneNum)) {
                    Toast.makeText(getApplicationContext(), "手机号码不能为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(code)) {
                    Toast.makeText(getApplicationContext(), "验证码不能为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                SMSSDK.submitVerificationCode("86", phoneNum, code);
                break;
            default:
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 销毁回调监听接口
        SMSSDK.unregisterAllEventHandler();
    }
}
