package com.bbxiaoqu.ui;


import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import com.bbxiaoqu.R;
import com.bbxiaoqu.api.MarketAPI;
import com.bbxiaoqu.comm.crash.UploadUtil;
import com.bbxiaoqu.comm.view.BaseActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.content.Intent;
import com.bbxiaoqu.ui.LoadingView.OnRefreshListener;

/**
 * 项目的主Activity，所有的Fragment都嵌入在这里。
 * 
 */
@SuppressLint("NewApi")
public class SendCrashActivity extends BaseActivity implements OnRefreshListener {
	private TextView textView;
	TextView title;
	TextView back;
	TextView right_text;
	Button upload_btn;
	Button return_btn;
	private static String localFileUrl = "";
	private static final String uploadUrl = "http://api.bbxiaoqu.com/api/ReceiveCrash.php";
	private TimeCount time;
	View view;
	LoadingView mLoadView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sendcrash);

		view = View.inflate (this, R.layout.activity_sendcrash, null);
		setContentView (view);
		mLoadView = (LoadingView) view.findViewById (R.id.loading_view);
		mLoadView.setRefrechListener(this);

		initView();
		initData();
		upload_btn= (Button)findViewById(R.id.upload_btn);
		upload_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				new SendCrashLog().execute("");
			}
		});
		
		return_btn= (Button)findViewById(R.id.return_btn);
		return_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), MainTabActivity.class));
				finish();
			}
		});


		time = new TimeCount(10000, 1000);//构造CountDownTimer对象
		time.start();//开始计时

		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) 
		{
			String sdcardPath = Environment.getExternalStorageDirectory().getPath();
			//localFileUrl = sdcardPath + "/bbm/crash/crash.log";
			//Log.d("TAG", "文     件："+localFileUrl);			
			File file=new File(sdcardPath + "/bbm/crash/");
			File[] tempList = file.listFiles();
			Arrays.sort(tempList, new Comparator<File>() {
				public int compare(File f1, File f2) {
					long diff = f1.lastModified() - f2.lastModified();
					if (diff > 0)
						return 1;
					else if (diff == 0)
						return 0;
					else
						return -1;
				}

				public boolean equals(Object obj) {
					return true;
				}

			});
			localFileUrl=tempList[tempList.length-1].toString();
			Log.d("TAG", "文     件："+tempList[tempList.length-1]);
		}
		
		
	}

	public void onTick(long millisUntilFinished){//计时过程显示
		return_btn.setClickable(false);
		return_btn.setText(millisUntilFinished /1000+"秒");
	}
	private void initView() {
		title = (TextView)findViewById(R.id.title);
		right_text = (TextView)findViewById(R.id.right_text);		
		right_text.setVisibility(View.GONE);
		//right_text.setClickable(true);	
		
		back = (TextView)findViewById(R.id.back);
		back.setVisibility(View.GONE);
	}

	private void initData() {
		title.setText("上传异常");
		right_text.setText("");
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		//overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		finish();
	}
	
	
	
	public class SendCrashLog extends AsyncTask<String, String, Boolean> 
	{
		public SendCrashLog() {  }

		@Override
		protected Boolean doInBackground(String... params) 
		{
			Log.d("TAG", "向服务器发送崩溃信息");
			int status = UploadUtil.uploadFile(new File(localFileUrl), uploadUrl);
			if(status==600)
			{
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						mLoadView.setStatue(LoadingView.NO_NETWORK);
					}
				}, 5 * 1000);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			Toast.makeText(getApplicationContext(), "成功将崩溃信息发送到服务器，感谢您的反馈", 1000).show();
			Log.d("TAG", "发送完成");
			
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
			}
		}, 1 * 1000);

	}


	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
		}
		@Override
		public void onFinish() {//计时完毕时触发
			//return_btn.setText("重新验证");
			return_btn.setClickable(true);
			time.cancel();
			startActivity(new Intent(getApplicationContext(), MainTabActivity.class));
			finish();
		}
		@Override
		public void onTick(long millisUntilFinished){//计时过程显示
			return_btn.setClickable(false);
			return_btn.setText("返回"+millisUntilFinished /1000+"秒");
		}
	}


}