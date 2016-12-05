package com.bbxiaoqu.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.R;
import com.bbxiaoqu.api.ApiAsyncTask;
import com.bbxiaoqu.api.ApiAsyncTask.ApiRequestListener;
import com.bbxiaoqu.api.MarketAPI;
import com.bbxiaoqu.client.baidu.Utils;
import com.bbxiaoqu.comm.service.User;
import com.bbxiaoqu.comm.service.db.UserService;
import com.bbxiaoqu.comm.widget.TextViewPlus;
import com.bbxiaoqu.comm.view.BaseActivity;
import com.bbxiaoqu.ui.LoadingView.OnRefreshListener;

public class LoginActivity extends BaseActivity implements OnFocusChangeListener,ApiRequestListener ,OnRefreshListener{
	private DemoApplication myapplication;
	View view;
	EditText etUsername;
	EditText etPassword;
	Button login, register;
	TextView searchpass;
	TextViewPlus savepass;
	UserService uService = new UserService(LoginActivity.this);
	private static final int DIALOG_PROGRESS = 0;

	LoadingView mLoadView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utils.logStringCache = Utils.getLogText(getApplicationContext());
		//view=setContentView(R.layout.activity_login);
		view = View.inflate (this, R.layout.activity_login, null);
		setContentView (view);

		Resources resource = this.getResources();
		String pkgName = this.getPackageName();
		myapplication = (DemoApplication) this.getApplication();
		initView();
		mLoadView = (LoadingView) view.findViewById (R.id.loading_view);
		mLoadView.setRefrechListener(this);
	}

	private void initView() {
		// TODO Auto-generated method stub
		etUsername = (EditText) findViewById(R.id.username);
		etPassword = (EditText) findViewById(R.id.password);
		//etPassword.setImeOptions(EditorInfo.IME_ACTION_SEND);
		etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId,KeyEvent event)  {
				if (actionId==EditorInfo.IME_ACTION_SEND)
				{
					login();
					return true;
				}
				return false;
			}
		});


		String userid="";
		if(mSession.getUid()!=null)
		{
			userid=mSession.getUid();
		}
		etUsername.setText(userid);
		login = (Button) findViewById(R.id.login);
		register = (Button) findViewById(R.id.register);
		searchpass = (TextView) findViewById(R.id.searchpass);
		savepass= (TextViewPlus) findViewById(R.id.savepass);
		savepass.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				TextViewPlus obj=(TextViewPlus)v;
				if(mSession.getIssavepass ())
				{

					Drawable drawable= getResources()
							.getDrawable(R.mipmap.against);
					drawable.setBounds(0, 0, drawable.getMinimumWidth(),
							drawable.getMinimumHeight());
					obj.setCompoundDrawables(drawable,null,null,null);

					mSession.setIsSavepass (false);
				}else
				{
					Drawable drawable= getResources()
							.getDrawable(R.mipmap.agree);
					drawable.setBounds(0, 0, drawable.getMinimumWidth(),
							drawable.getMinimumHeight());
					obj.setCompoundDrawables(drawable,null,null,null);
					mSession.setIsSavepass (true);
				}
			}
		});
		login.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String userid = etUsername.getText().toString();
				String pass = etPassword.getText().toString();
				login();

			}
		});
		register.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this,	RegisterActivity.class);
				startActivity(intent);
			}
		});

		searchpass.setLinksClickable(true);
		searchpass.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this,	SearchPassActivity.class);
				startActivity(intent);
			}
		});
	}
	
	
		private void login() {
	        if (!isFinishing()) {
	            showDialog(DIALOG_PROGRESS);
	        } else {
	            // 如果当前页面已经关闭，不进行登录操作
	            return;
	        }
			String userName = etUsername.getText().toString();
			String passWord= etPassword.getText().toString();			
			MarketAPI.login(getApplicationContext(), this, userName, passWord);
		}

		@Override
		protected void onPrepareDialog(int id, Dialog dialog) {
			super.onPrepareDialog(id, dialog);
		    if (dialog.isShowing()) {
		    	dialog.dismiss();
		    }
		}
		 @Override
		 protected Dialog onCreateDialog(int id) {
			 switch (id) {
		     case DIALOG_PROGRESS:
		        ProgressDialog mProgressDialog = new ProgressDialog(this);
		        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		        mProgressDialog.setMessage(getString(R.string.singin));
		        return mProgressDialog;
		     default:
		     	return super.onCreateDialog(id);
		     }
		 }
	

	protected void onDestroy() {
		super.onDestroy();
		if (uService.dbHelper != null) {
			uService.close();
		}
	}

	public void onSuccess(int method, Object obj) {
	    switch (method) {
        case MarketAPI.ACTION_LOGIN:   
        	 try{
                 dismissDialog(DIALOG_PROGRESS);
             }catch (IllegalArgumentException e) {
             }
            HashMap<String, String> result = (HashMap<String, String>) obj;
            String JsonContext=result.get("login");
			if(JsonContext.equals("[]"))
			{
				Toast.makeText(LoginActivity.this, "用户不存在", Toast.LENGTH_LONG).show();
				return;
			}
			JSONObject jsonobj = null;
			try {
				jsonobj = new JSONObject(JsonContext);
				String userid = jsonobj.getString("userid");
				if (userid != "") {
					String pass = etPassword.getText().toString();
					String password = jsonobj.getString("pass");
					if(!pass.equals(password))
					{
						Toast.makeText(LoginActivity.this, "密码错误",Toast.LENGTH_LONG).show();
						return;
					}else {
						String telphone = jsonobj.getString("telphone");
						String headface = jsonobj.getString("headface");
						String username = jsonobj.getString("username");
						String isrecvmess = jsonobj.getString("isrecvmess");
						String isopenvoice = jsonobj.getString("isopenvoice");
						User user = new User();
						user.setNickname(username);
						user.setUsername(userid);
						user.setPassword(password);
						user.setTelphone(telphone);
						user.setHeadface(headface);
						uService.register(user);// 注册一个

						mSession.setUid(userid);
						mSession.setUserName(username);
						mSession.setHeadface(headface);
						mSession.setPassword(pass);
						mSession.setIslogin(true);


						boolean flag = uService.login(userid, password);
						myapplication.setUserId(userid);
						myapplication.setPassword(password);
						myapplication.setNickname(username);
						myapplication.setHeadface(headface);
						if (flag) {
							Log.i("TAG", "登录成功");
							uService.online(userid);// 更改状态
							mSession.setUid(userid);
							mSession.setUserName(username);
							mSession.setPassword(password);
							if(isrecvmess.equals("1"))
							{
								mSession.setIsNotic(true);
							}else
							{
								mSession.setIsNotic(false);
							}
							Intent intent = new Intent(LoginActivity.this, MainTabActivity.class);
							startActivity(intent);
						} else {
							Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_LONG).show();
						}
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}// 转换为JSONObject
            break;
        default:
            break;
        }
	}

	@Override
    public void onError(int method, int statusCode) {
	    switch (method) {
        case MarketAPI.ACTION_LOGIN:            
            // 隐藏登录框
            try{
                dismissDialog(DIALOG_PROGRESS);
            }catch (IllegalArgumentException e) {
            }            
            String msg = null;
			if(statusCode == ApiAsyncTask.TIMEOUT_ERROR) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						mLoadView.setStatue(LoadingView.NO_NETWORK);
					}
				}, 5 * 1000);
			}else {
                msg = getString(R.string.error_login_other);
				Utils.makeEventToast(getApplicationContext(), msg, false);

			}
            break;
        default:
            break;
        }
    }
	
	 /*
     * 检查用户名合法性
     * 1 不能为空
     * 2 长度在 3 - 16 个字符之间
     */
    private boolean checkUserName() {
        String input = etUsername.getText().toString();
        if (TextUtils.isEmpty(input)) {
            etUsername.setError(getString(R.string.error_username_empty));
            return false;
        } else {
            etUsername.setError(null);
        }
        int length = input.length();
        if (length < 3 || length > 16) {
            etUsername.setError(getString(R.string.error_username_length_invalid));
            return false;
        } else {
            etUsername.setError(null);
        }
        return true;
    }
    
    /*
     * 检查用户密码合法性
     * 1 不能为空
     * 2 长度在1 - 32 个字符之间
     */
    private boolean checkPassword(EditText input) {
        String passwod = input.getText().toString();
        if (TextUtils.isEmpty(passwod)) {
            input.setError(getString(R.string.error_password_empty));
            return false;
        } else {
            input.setError(null);
        }
        int length = passwod.length();
        if (length > 32) {
            input.setError(getString(R.string.error_password_length_invalid));
            return false;
        } else {
            input.setError(null);
        }
        return true;
    }



	@Override
	public void onFocusChange(View v, boolean flag) {
        switch (v.getId()) {
        case R.id.username:
            if (!flag) {
                checkUserName();
            }
            break;
        case R.id.password:
            if (!flag) {
                checkPassword(etPassword);
            }
            break;
        default:
            break;
        }
    }


	//刷新界面方法
	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mLoadView.setStatue(LoadingView.GONE);//loadingview消失
				MarketAPI.getSystemInfo(getApplicationContext(), LoginActivity.this);
			}
		}, 1 * 1000);

	}
}