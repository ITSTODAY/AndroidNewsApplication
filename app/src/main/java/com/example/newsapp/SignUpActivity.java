package com.example.newsapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.*;


/**
 * 登录界面
 */

public class SignUpActivity extends Activity
        implements View.OnClickListener{
    //布局内的控件
    private EditText et_name;
    private EditText et_password;
    private EditText et_password_re;
    private ImageView iv_see_password;
    private ImageView iv_see_password_re;
    private Button myRegistBtn;

    private LoadingDialog mLoadingDialog; //显示正在加载的对话框


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initViews();
        setupEvents();

    }


    private void initViews() {
        //mLoginBtn = (Button) findViewById(R.id.btn_login);
        et_name = (EditText) findViewById(R.id.et_account_2);
        et_password = (EditText) findViewById(R.id.et_password_2);
        et_password_re = (EditText) findViewById(R.id.et_password_re_2);
        //checkBox_password = (CheckBox) findViewById(R.id.checkBox_password);
        //checkBox_login = (CheckBox) findViewById(R.id.checkBox_login);
        iv_see_password = (ImageView) findViewById(R.id.iv_see_password_2);
        iv_see_password_re = (ImageView) findViewById(R.id.iv_see_password_re_2);
        myRegistBtn = (Button) findViewById(R.id.btn_signup_2);
    }

    private void setupEvents() {
        myRegistBtn.setOnClickListener(this);
        //mLoginBtn.setOnClickListener(this);
        //checkBox_password.setOnCheckedChangeListener(this);
        //checkBox_login.setOnCheckedChangeListener(this);
        iv_see_password.setOnClickListener(this);
        iv_see_password_re.setOnClickListener(this);
    }


    /**
     * 模拟登录情况
     * 用户名csdn，密码123456，就能登录成功，否则登录失败
     */
    private void signup() {

        //先做一些基本的判断，比如输入的用户命为空，密码为空，网络不可用多大情况，都不需要去链接服务器了，而是直接返回提示错误
        if (getAccount().isEmpty()){
            showToast("你输入的账号为空！");
            return;
        }

        if (getPassword().isEmpty()){
            showToast("你输入的密码为空！");
            return;
        }

        if(getPassword_re().isEmpty()){
            showToast("请再次输入您的密码！");
            return;
        }

        if(!getPassword_re().equals(getPassword())){
            showToast("两次密码输入不一致，请检查！");
            return;
        }
        //登录一般都是请求服务器来判断密码是否正确，要请求网络，要子线程
        showLoading();//显示加载框
        Thread loginRunnable = new Thread() {

            @Override
            public void run() {
                super.run();
                setLoginBtnClickable(false);//点击登录后，设置登录按钮不可点击状态


                //睡眠3秒
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                String userId = getAccount();
                String passWord = getPassword();

                User checkUser = new User(userId,passWord);

                int state = checkUser.SignUp();
                if(state==0){
                    showToast("这个用户名已被注册，请重新选择！");
                }else{
                    showToast("注册成功");
                    Intent intent = new Intent();
                    intent.putExtra("userID",userId);
                    intent.putExtra("password",passWord);
                    intent.putExtra("result","1");
                    SignUpActivity.this.setResult(RESULT_OK,intent);
                    finish();
                }

                setLoginBtnClickable(true);  //这里解放登录按钮，设置为可以点击
                hideLoading();//隐藏加载框
            }
        };
        loginRunnable.start();


    }


    /**
     * 保存用户账号
     */
    public void loadUserName() {
        if (!getAccount().equals("") || !getAccount().equals("请输入登录账号")) {
            SharedPreferencesUtils helper = new SharedPreferencesUtils(this, "setting");
            helper.putValues(new SharedPreferencesUtils.ContentValue("name", getAccount()));
        }

    }

    /**
     * 设置密码可见和不可见的相互转换
     */
    private void setPasswordVisibility() {
        if (iv_see_password.isSelected()) {
            iv_see_password.setSelected(false);
            //密码不可见
            et_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        } else {
            iv_see_password.setSelected(true);
            //密码可见
            et_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }

    }

    private void setPasswordVisibility_re() {
        if (iv_see_password_re.isSelected()) {
            iv_see_password_re.setSelected(false);
            //密码不可见
            et_password_re.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        } else {
            iv_see_password_re.setSelected(true);
            //密码可见
            et_password_re.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }

    }

    /**
     * 获取账号
     */
    public String getAccount() {
        return et_name.getText().toString().trim();//去掉空格
    }

    /**
     * 获取密码
     */
    public String getPassword() {
        return et_password.getText().toString().trim();//去掉空格
    }

    public String getPassword_re() {
        return et_password_re.getText().toString().trim();//去掉空格
    }


    /**
     * 保存用户选择“记住密码”和“自动登陆”的状态
     */



    /**
     * 是否可以点击登录按钮
     *
     * @param clickable
     */
    public void setLoginBtnClickable(boolean clickable) {
        myRegistBtn.setClickable(clickable);
    }


    /**
     * 显示加载的进度款
     */
    public void showLoading() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(this, getString(R.string.loading), false);
        }
        mLoadingDialog.show();
    }


    /**
     * 隐藏加载的进度框
     */
    public void hideLoading() {
        if (mLoadingDialog != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLoadingDialog.hide();
                }
            });

        }
    }




    /**
     * 监听回退键
     */
    @Override
    public void onBackPressed() {
        if (mLoadingDialog != null) {
            if (mLoadingDialog.isShowing()) {
                mLoadingDialog.cancel();
            } else {
                finish();
            }
        } else {
            finish();
        }

    }

    /**
     * 页面销毁前回调的方法
     */
    protected void onDestroy() {
        if (mLoadingDialog != null) {
            mLoadingDialog.cancel();
            mLoadingDialog = null;
        }
        super.onDestroy();
    }


    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SignUpActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_signup_2:
                signup();
                break;
            case R.id.iv_see_password_2:
                setPasswordVisibility();    //改变图片并设置输入框的文本可见或不可见
                break;
            case R.id.iv_see_password_re_2:
                setPasswordVisibility_re();
                break;
        }
    }

}
