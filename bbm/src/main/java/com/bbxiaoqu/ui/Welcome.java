package com.bbxiaoqu.ui;



import com.bbxiaoqu.AppInfo;
import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.R;
import com.bbxiaoqu.Session;
import com.bbxiaoqu.api.ApiAsyncTask;
import com.bbxiaoqu.api.MarketAPI;
import com.bbxiaoqu.client.baidu.Utils;
import com.bbxiaoqu.comm.service.User;
import com.bbxiaoqu.comm.service.db.UserService;
import com.bbxiaoqu.update.UpdataInfo;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Random;
import com.bbxiaoqu.api.ApiAsyncTask.ApiRequestListener;
import com.bbxiaoqu.ui.LoadingView.OnRefreshListener;


public class Welcome extends Activity implements ApiRequestListener,OnRefreshListener {
	private DemoApplication myapplication;
	private AlphaAnimation start_anima;
	View view;
	ImageView splashimage;
	Session mSession;
	LoadingView mLoadView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate (savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder ().permitAll ().build ();
		StrictMode.setThreadPolicy (policy);
		view = View.inflate (this, R.layout.welcome, null);
		setContentView (view);
		final Context context = getApplicationContext ();
		mSession = Session.get (context);
		splashimage = (ImageView) view.findViewById (R.id.splash);
		mLoadView = (LoadingView) view.findViewById (R.id.loading_view);
		//mLoadView.setStatue(LoadingView.LOADING);
		mLoadView.setRefrechListener(this);
		int mImageViewArraysel[] = {R.mipmap.splash, R.mipmap.splash1, R.mipmap.splash2};
		myapplication = (DemoApplication) this.getApplication ();
		Random r = new Random();
		int pick=r.nextInt(2);
		//int pick = Math.abs (random.nextInt ()) % 2	;
		splashimage.setImageResource (mImageViewArraysel[pick]);
		/*//判断是不是首次登录，
		if (mSession.getFirststart()==null) {
			//将登录标志位设置为false，下次登录时不在显示首次登录界面
			mSession.setFirststart("false");
			Intent intent=new Intent(Welcome.this, Guide.class);
			startActivity(intent);
			finish();
		}else if(mSession.getFirststart().equals("true"))
		{
			mSession.setFirststart("false");
			Intent intent=new Intent(Welcome.this, Guide.class);
			startActivity(intent);
			finish();
		}*/
		/*List<String> splash_ad = new ArrayList<String>();
		splash_ad.add("splash_ad");
		Intent intent=new Intent(Welcome.this, Guide.class);
		intent.putExtra("splash_ad",
				(Serializable) splash_ad);
		startActivity(intent);
		finish();*/
		MarketAPI.getSystemInfo(getApplicationContext(), this);
		/*new Thread(LoadSysRun).start();
		initData ();*/


	}

	private void initData() {
		start_anima = new AlphaAnimation (0.3f, 1.0f);
		start_anima.setDuration (3000);
		view.startAnimation (start_anima);
		start_anima.setAnimationListener (new AnimationListener () {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				System.out.println (System.currentTimeMillis ());
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				System.out.println (System.currentTimeMillis ());
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				redirectTo ();
			}
		});
	}


	private void redirectTo() {
		UserService uService = new UserService (Welcome.this);
		String[] arr = uService.getonlineuserid ();
		if (arr == null) {
			startActivity (new Intent (getApplicationContext (), LoginActivity.class));
			finish ();
		} else {
			String userid = arr[0];
			myapplication.setUserId (userid);
			uService.online (userid);//更改状态
			Intent intent = new Intent (Welcome.this, MainTabActivity.class);
			startActivity (intent);
		}

	}

/*
	Handler errorhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			String error = data.getString("error");
			Toast.makeText(Welcome.this, error, Toast.LENGTH_SHORT).show();
		}
	};*/
	public static AppInfo getAppInfo(InputStream is) throws Exception {
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(is, "utf-8");//设置解析的数据源
		int type = parser.getEventType();
		AppInfo info = new AppInfo();//实体
		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
				case XmlPullParser.START_TAG:
					if ("xmpphost".equals(parser.getName())) {
						info.setXmpphost(parser.nextText());    //获取版本号
					} else if ("xmppport".equals(parser.getName())) {
						info.setXmppport(Integer.parseInt(parser.nextText()));    //获取要升级的APK文件
					} else if ("xmppdomain".equals(parser.getName())) {
						info.setXmppdomain(parser.nextText());    //获取该文件的信息
					}
					break;
			}
			type = parser.next();
		}
		return info;
	}



	public void onSuccess(int method, Object obj) {
		switch (method) {
			case MarketAPI.ACTION_SYSTEMINFO:
				HashMap<String, String> result = (HashMap<String, String>) obj;
				String JsonContext=result.get("xml");

				InputStream json = new ByteArrayInputStream(JsonContext.getBytes());

				AppInfo info = null;
				try {
					info = getAppInfo(json);
					if (info != null) {
						myapplication.setXmpphost(info.getXmpphost());
						myapplication.setXmppport(info.getXmppport());
						myapplication.setXmppdomain(info.getXmppdomain());
						initData();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			default:
				break;
		}
	}


	@Override
	public void onError(int method, int statusCode) {
		switch (method) {
			case MarketAPI.ACTION_SYSTEMINFO:
				// 隐藏登录框
				System.out.println(statusCode);
				/*String msg = "连接不到服务器";
				Utils.makeEventToast(getApplicationContext(), msg, false);*/
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						mLoadView.setStatue(LoadingView.NO_NETWORK);
					}
				}, 5 * 1000);
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
				MarketAPI.getSystemInfo(getApplicationContext(), Welcome.this);
			}
		}, 1 * 1000);

	}
}
