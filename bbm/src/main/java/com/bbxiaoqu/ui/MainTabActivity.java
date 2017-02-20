package com.bbxiaoqu.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.util.Xml;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.platform.comapi.map.C;
import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.R;
import com.bbxiaoqu.Session;
import com.bbxiaoqu.api.ApiAsyncTask;
import com.bbxiaoqu.api.MarketAPI;
import com.bbxiaoqu.comm.bean.BbMessage;

import com.bbxiaoqu.client.baidu.Utils;
import com.bbxiaoqu.comm.service.db.ChatDB;
import com.bbxiaoqu.comm.service.db.NoticeDB;
import com.bbxiaoqu.comm.service.db.UserService;
import com.bbxiaoqu.comm.tool.CustomerHttpClient;
import com.bbxiaoqu.comm.tool.DoubleClickExitHelper;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.StreamTool;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.ui.fragment.FragmentPage1;
import com.bbxiaoqu.ui.fragment.FragmentPage2;
import com.bbxiaoqu.ui.fragment.FragmentPage3;
import com.bbxiaoqu.ui.fragment.FragmentPage4;
import com.bbxiaoqu.update.UpdataInfo;
import com.bbxiaoqu.update.UpdateService;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import com.bbxiaoqu.client.baidu.BbPushMessageReceiver;
import com.bbxiaoqu.client.baidu.BbPushMessageReceiver.onNewMessageListener;
import com.bbxiaoqu.client.baidu.BbPushMessageReceiver.onChatMessageListener;
import com.bbxiaoqu.client.baidu.BbPushMessageReceiver.onMessageReadListener;

public class MainTabActivity extends FragmentActivity implements TabHost.OnTabChangeListener, onNewMessageListener,onChatMessageListener, onMessageReadListener,ApiAsyncTask.ApiRequestListener {
	private static final String TAG = "MainTabActivity";
	private static final int DIALOG_PROGRESS = 0;

	private Context context = this;
	private DemoApplication myapplication;
	private LocationClient mLocationClient;
	protected Session mSession;

	public static final String appName = "updatebbm";
	public static final String downUrl = "bbm.apk";
	private String OldVersionName = "";
	private String NewVersionName = "";


	public Double nLatitude;
	public Double nLontitude;
	private FragmentTabHost mTabHost;

	private LayoutInflater layoutInflater;

	private Class fragmentArray[] = {FragmentPage1.class, FragmentPage2.class, FragmentPage3.class, FragmentPage4.class};

	private int mImageViewArray[] = {R.drawable.tab_home_btn, R.drawable.tab_message_btn, R.drawable.tab_top_btn,
			R.drawable.tab_my_btn};
	private int mImageViewArraysel[] = {R.drawable.tab_home_btn, R.drawable.tab_message_btn, R.drawable.tab_top_btn,
			R.drawable.tab_my_btn};

	private String mTextviewArray[] = {"首页", "会话", "襄助榜", "我的"};
	private SoundPool soundPool;
	private ImageView left_image;
	private ImageView right_image;

	private static int getStatusBarHeight(Context context) {
		int statusBarHeight = 0;
		Resources res = context.getResources();
		int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			statusBarHeight = res.getDimensionPixelSize(resourceId);
		}
		return statusBarHeight;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//this.setTheme(R.style.AppTheme);
		setContentView(R.layout.main_tab_layout);
		if (Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		BbPushMessageReceiver.msgListeners.add(this);//新消息
		BbPushMessageReceiver.chatmsgListeners.add(this);//新聊天记录
		BbPushMessageReceiver.msgReadListeners.add(this);//点了阅读
		myapplication = (DemoApplication) this.getApplication();
		myapplication.getInstance().addActivity(this);
		final Context context = getApplicationContext();
		mSession = Session.get(context);
		if(mSession.getUid().equals(""))
		{
			Intent Intent1 = new Intent();
			Intent1.setClass(MainTabActivity.this, LoginActivity.class);
			startActivity(Intent1);
			return;
		}
		Resources resource = this.getResources();
		String pkgName = this.getPackageName();
		initbaidu(resource, pkgName);
		initlsb();

		PushManager.startWork(getApplicationContext(),
				PushConstants.LOGIN_TYPE_API_KEY,
				Utils.getMetaValue(MainTabActivity.this, "api_key"));
		layoutInflater = LayoutInflater.from(this);
		if (myapplication.ifpass(myapplication.getUserId())) {//判断用户是否登录
			UserService uService = new UserService(MainTabActivity.this);
			if (myapplication.getNickname().equals("")) {//昵称为空,弹出用户基本信息修改
				Intent intent = new Intent(MainTabActivity.this, UserInfoActivity.class);
				startActivity(intent);
			}
			initTabHost();
			new Thread(updataRun).start();
		} else {
			Intent Intent1 = new Intent();
			Intent1.setClass(MainTabActivity.this, LoginActivity.class);
			startActivity(Intent1);
			return;
		}
		soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
		soundPool.load(MainTabActivity.this, R.raw.msg, 1);

		new Thread(xmpprunnable).start();
		if (!isFinishing()) {
			showDialog(DIALOG_PROGRESS);
		} else {
			// 如果当前页面已经关闭，不进行登录操作
			return;
		}
		MarketAPI.gonggao(getApplicationContext(), this);
	}


	Runnable xmpprunnable = new Runnable(){
		@Override
		public void run() {
			// TODO: http request.
			Message msg = new Message();
			msg.what=4;
			handler.sendMessage(msg);
		}
	};

	//Activity创建或者从后台重新回到前台时被调用
	@Override
	protected void onStart() {
		super.onStart();
		Log.i(TAG, "onStart called.");
	}

	//Activity从后台重新回到前台时被调用
	@Override
	protected void onRestart() {
		super.onRestart();
		Log.i(TAG, "onRestart called.");
	}

	//Activity创建或者从被覆盖、后台重新回到前台时被调用
	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "onResume called.");
	}

	//Activity窗口获得或失去焦点时被调用,在onResume之后或onPause之后
    /*@Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.i(TAG, "onWindowFocusChanged called.");
    }*/

	//Activity被覆盖到下面或者锁屏时被调用
	@Override
	protected void onPause() {
		super.onPause();
		Log.i(TAG, "onPause called.");
		//有可能在执行完onPause或onStop后,系统资源紧张将Activity杀死,所以有必要在此保存持久数据
	}

	//退出当前Activity或者跳转到新Activity时被调用
	@Override
	protected void onStop() {
		super.onStop();
		Log.i(TAG, "onStop called.");
	}

	//退出当前Activity时被调用,调用之后Activity就结束了
	@Override
	protected void onDestroy() {
		mLocationClient.stop ();//不再获取经纬度
		BbPushMessageReceiver.chatmsgListeners.remove (this);
		BbPushMessageReceiver.msgListeners.remove(this);
		PushManager.stopWork(getApplicationContext());
		super.onDestroy();

	}
	DoubleClickExitHelper doubleClick = new DoubleClickExitHelper(this);
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			return doubleClick.onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	}

	int currenttab = 0;
	private void initTabHost() {
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		//点击动作参考http://www.jerehedu.com/fenxiang/162138_for_detail.htm
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		int count = fragmentArray.length;
		for (int i = 0; i < count; i++) {
			//对Tab按钮添加标记和图片
			TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
			//添加Fragment
			mTabHost.addTab(tabSpec, fragmentArray[i], null);
			//ea194a
			//mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.selector_tab_background);
		}
		mTabHost.setOnTabChangedListener(this);
		left_image = (ImageView) findViewById(R.id.top_menu_left_image);
		right_image = (ImageView) findViewById(R.id.top_menu_right_image);
		left_image.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(myapplication, NoticeActivity.class);
				startActivity(intent);
				/*Intent intent = new Intent(MainTabActivity.this, UserInfoActivity.class);
				startActivity(intent);*/
			}
		});
		right_image.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(myapplication, SearchActivity.class);
				startActivity(intent);
			}
		});
		//mTabHost.setCurrentTab (1);
		//addtag();
	}

	private View getTabItemView(int index) {
		View view;
		if(index==1) {
			view = layoutInflater.inflate (R.layout.tab_item_view, null);
		}else
		{
			view = layoutInflater.inflate (R.layout.tab_item_view, null);
		}
		ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
		imageView.setImageResource(mImageViewArray[index]);
		TextView textView = (TextView) view.findViewById(R.id.textview);
		textView.setTextColor(Color.GRAY);
		textView.setText(mTextviewArray[index]);
		TextView numbertextView = (TextView) view.findViewById(R.id.number);
		numbertextView.setVisibility (View.GONE);
		return view;
	}

	public  void addtag(String num)
	{
		TabWidget tabw = mTabHost.getTabWidget();
		if(mTabHost.getCurrentTab()!=1) {
			View view = tabw.getChildAt(1);
			((TextView) view.findViewById(R.id.number)).setVisibility(View.VISIBLE);
			((TextView) view.findViewById(R.id.number)).setText(num);
		}else
		{
			//刷新子
			for (int i = 0; i < BbPushMessageReceiver.freshtableListeners.size(); i++) {
				BbPushMessageReceiver.freshtableListeners.get (i).freshtable();
			}
		}
	}

	@Override
	public void onTabChanged(String tabId) {
		TabWidget tabw = mTabHost.getTabWidget();
		for(int i=0;i<tabw.getChildCount();i++){
			View view=tabw.getChildAt(i);
			ImageView iv=(ImageView)view.findViewById(R.id.imageview);
			if(i==mTabHost.getCurrentTab()){
				((TextView)view.findViewById(R.id.textview)).setTextColor(Color.RED);
				iv.setImageResource(mImageViewArraysel[i]);
				if(i==1)
				{
					((TextView) view.findViewById (R.id.number)).setVisibility (View.GONE);
					((TextView) view.findViewById (R.id.number)).setText ("0");
				}

			}else{
				((TextView)view.findViewById(R.id.textview)).setTextColor(getResources().getColor(R.color.gray));
				iv.setImageResource(mImageViewArray[i]);
			}


		}
		new Thread(xmpprunnable).start();
	}


	private void initbaidu(Resources resource, String pkgName) {
		// Push: 以apikey的方式登录，一般放在主Activity的onCreate中。
		// 这里把apikey存放于manifest文件中，只是一种存放方式，
		// 您可以用自定义常量等其它方式实现，来替换参数中的Utils.getMetaValue(PushDemoActivity.this,
		// "api_key")
		PushManager.startWork(this.myapplication,
				PushConstants.LOGIN_TYPE_API_KEY, DemoApplication.API_KEY);
		// Push: 如果想基于地理位置推送，可以打开支持地理位置的推送的开关
		// PushManager.enableLbs(getApplicationContext());

		// Push: 设置自定义的通知样式，具体API介绍见用户手册，如果想使用系统默认的可以不加这段代码
		// 请在通知推送界面中，高级设置->通知栏样式->自定义样式，选中并且填写值：1，
		// 与下方代码中 PushManager.setNotificationBuilder(this, 1, cBuilder)中的第二个参数对应
		/*
		 * CustomPushNotificationBuilder cBuilder = new
		 * CustomPushNotificationBuilder( this.getApplicationContext(),
		 * resource.getIdentifier( "notification_custom_builder", "layout",
		 * pkgName), resource.getIdentifier("notification_icon", "id", pkgName),
		 * resource.getIdentifier("notification_title", "id", pkgName),
		 * resource.getIdentifier("notification_text", "id", pkgName));
		 * cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
		 * cBuilder.setNotificationDefaults(Notification.DEFAULT_SOUND |
		 * Notification.DEFAULT_VIBRATE);
		 * cBuilder.setStatusbarIcon(this.getApplicationInfo().icon);
		 * cBuilder.setLayoutDrawable(resource.getIdentifier(
		 * "simple_notification_icon", "drawable", pkgName));
		 * PushManager.setNotificationBuilder(this, 1, cBuilder);
		 */
	}


	public String Country = "";
	public String Province = "";
	public String City = "";
	public String District = "";
	public String Street = "";

	private void initlsb() {
		// TODO Auto-generated method stub
		mLocationClient = new LocationClient(this.myapplication);
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType("gcj02");// 返回的定位结果是百度经纬度，默认值gcj02
		int span = 1000 * 20;
		option.setScanSpan(span);// 设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
		mLocationClient.registerLocationListener(new BDLocationListener() {
			@Override
			public void onReceiveLocation(BDLocation location) {
				// TODO Auto-generated method stub
				if (location == null) {
					//Log.v(TAG, "HomeActivity location empty");
					return;
				}
				nLatitude = location.getLatitude();
				nLontitude = location.getLongitude();
				Country = location.getCountry();
				Province = location.getProvince();
				City = location.getCity();
				District = location.getDistrict();
				Street = location.getStreet();
				//查询可见范围和小区
				Log.i("mylog", nLatitude + "-" + nLontitude+ "-" + Country+ "-" + Province+ "-" + City+ "-" + District+ "-" + Street);
				myapplication.setCountry(Country);
				myapplication.setProvince(Province);
				myapplication.setCity(City);
				myapplication.setDistrict(District);

				myapplication.setLat(String.valueOf(nLatitude));
				myapplication.setLng(String.valueOf(nLontitude));
				myapplication.updatelocation();
				mLocationClient.stop();
				/*new Thread(loaduservisiblerange).start();
				new Thread(loaduserxiaoqu).start();*/
				loaduserdata();



			}
		});
		mLocationClient.start();
		mLocationClient.requestLocation();
	}

	public void loaduserdata()
	{
		MarketAPI.GetUserVisibleRang(getApplicationContext(),this,Country,Province,City,District,Street);
		MarketAPI.GetUserVisibleCommunity(getApplicationContext(),this,myapplication.getUserId());

	}


	Runnable updataRun = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Looper.prepare();
			CheckVersionTask();
			Looper.loop();
		}

	};


	private String getVersionName() {
		//获取packagemanager的实例
		PackageManager packageManager = getPackageManager();
		//getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return packInfo.versionName;
	}

	public static UpdataInfo getUpdataInfo(InputStream is) throws Exception {
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(is, "utf-8");//设置解析的数据源
		int type = parser.getEventType();
		UpdataInfo info = new UpdataInfo();//实体
		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
				case XmlPullParser.START_TAG:
					if ("version".equals(parser.getName())) {
						info.setVersion(parser.nextText());    //获取版本号
					} else if ("url".equals(parser.getName())) {
						info.setUrl(parser.nextText());    //获取要升级的APK文件
					} else if ("description".equals(parser.getName())) {
						info.setDescription(parser.nextText());    //获取该文件的信息
					}
					break;
			}
			type = parser.next();
		}
		return info;
	}

	private void CheckVersionTask() {
		OldVersionName = getVersionName();
		UpdataInfo info = null;
		String target = myapplication.getlocalhost() + "/update.xml?t=" + System.currentTimeMillis();
		URL url = null;
		try {
			url = new URL(target);//构造一个url对象
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		if (url != null) {
			HttpURLConnection urlConnection;
			try {
				urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setConnectTimeout(50000);
				urlConnection.setRequestMethod("GET");
				if (urlConnection.getResponseCode() == 200) {
					InputStream json = urlConnection.getInputStream();
					info = getUpdataInfo(json);
					NewVersionName = info.getVersion();
				}else
				{
					Message msg = new Message();
					Bundle data = new Bundle();
					data.putString("error", "连接不到服务器");
					msg.setData(data);
					errorhandler.sendMessage(msg);
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putString("error", "请检查设备网络连接");
				msg.setData(data);
				errorhandler.sendMessage(msg);
				//e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (info != null) {
				if (info.getVersion().equals(OldVersionName)) {
					//T.showShort(MainActivity.this, "版本号相同无需升级！");
				} else {
					if (Double.parseDouble(OldVersionName) < Double.parseDouble(info.getVersion())) {
						new AlertDialog.Builder(MainTabActivity.this).setTitle("确认升级吗？")
								.setIcon(android.R.drawable.ic_dialog_info)
								.setPositiveButton("升级", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										Intent intent = new Intent(MainTabActivity.this, UpdateService.class);
										intent.putExtra("Key_App_Name", appName);
										intent.putExtra("Key_Down_Url", myapplication.getlocalhost() + downUrl);
										startService(intent);
									}
								})
								.setNegativeButton("暂不升级", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// 点击“返回”后的操作,这里不设置没有任何操作
									}
								}).show();
					}
				}
			}
		}
	}

	Handler errorhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			String error = data.getString("error");
			Toast.makeText(MainTabActivity.this, error, Toast.LENGTH_SHORT).show();
		}
	};


	@Override
	public void onReadMessage() {
		//headtop_left_count.setVisibility(View.GONE);
		//headtop_left_count.setText("0");
	}

	@Override
	public void onNewMessage(BbMessage message) {
		Message msg = handler.obtainMessage();
		msg.what = 2;
		Bundle data = new Bundle();
		NoticeDB db = new NoticeDB(MainTabActivity.this);
		data.putInt("num", Integer.parseInt(String.valueOf(db.unreadnum())));
		msg.setData(data);
		handler.sendMessage(msg);
	}

	@Override
	public void onChatMessage(BbMessage message) {
		Message msg = handler.obtainMessage();
		msg.what = 3;
		Bundle data = new Bundle();
		ChatDB db = new ChatDB(MainTabActivity.this);
		data.putInt("num", Integer.parseInt(String.valueOf(db.allunreadnum())));
		msg.setData(data);
		handler.sendMessage(msg);
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					new Thread(updataRun).start();
					break;
				case 2://针对百度推送
					Bundle data = msg.getData();
					int num = data.getInt("num");
						//下面的也要更新吗
					soundPool.play(1, 1, 1, 0, 0, 1);
					break;
				case 3://针对私聊
					Bundle data1 = msg.getData();
					int num1 = data1.getInt("num");
					addtag(String.valueOf (num1));
					soundPool.play(1, 1, 1, 0, 0, 1);
					break;
				case 4:
					if (!NetworkUtils.isNetConnected(myapplication)) {
						T.showShort(myapplication, "当前无网络连接！");
						NetworkUtils.showNoNetWorkDlg(MainTabActivity.this);
						return;
					}
					myapplication.getInstance().startxmpp();
					break;
				default:
					break;
			}
		}

		;
	};

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
				mProgressDialog.setMessage(getString(R.string.init_data));
				return mProgressDialog;

			default:
				return super.onCreateDialog(id);
		}
	}
	@Override
	public void onSuccess(int method, Object obj) {
		// TODO Auto-generated method stub
		switch (method) {
			case MarketAPI.ACTION_GONGGAO:
				try{
					dismissDialog(DIALOG_PROGRESS);
				}catch (IllegalArgumentException e) {
				}
				HashMap<String, String> result1 = (HashMap<String, String>) obj;
				String JsonContext1=result1.get("gonggao");
				if(JsonContext1.length()>0)
				{
					JSONArray jsonarray;
					try {
						jsonarray = new JSONArray(JsonContext1);
						JSONObject jsonobject = jsonarray.getJSONObject(0);
						String id = jsonobject.getString("id");
						String title = jsonobject.getString("title");
						String content = jsonobject.getString("content");
						String publishdate = jsonobject.getString("publishdate");
						mSession.seGgid(id);
						mSession.setGgcontent(title);
						mSession.setGgdate(publishdate);
						//gonggao.setText(title);
						//gonggao.setTag(id);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Utils.makeEventToast(MainTabActivity.this, "gonggao xml解释错误",false);
					}
				}
				break;
			case MarketAPI.ACTION_GETUSERVISIBLERANGE:
				HashMap<String, String> result2 = (HashMap<String, String>) obj;
				String JsonContext2=result2.get("result");
				try {
					JSONObject jsonobject2 = new JSONObject(JsonContext2);
					String rang = jsonobject2.getString("rang");
					mSession.settRang(rang);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case MarketAPI.ACTION_GETUSERVISIBLECOMMUNITY:
				HashMap<String, String> result3 = (HashMap<String, String>) obj;
				String JsonContext3=result3.get("result");
				try {
					JSONObject jsonobject3 = new JSONObject(JsonContext3);
					String community = jsonobject3.getString("community");
					String community_id = jsonobject3.getString("community_id");
					String community_lat = jsonobject3.getString("community_lat");
					String community_lng = jsonobject3.getString("community_lng");
					mSession.setXiaoquname(community);
					mSession.setXiaoquid(community_id);
				} catch (JSONException e) {
					e.printStackTrace();
				}

				break;
			default:
				break;
		}
	}


	@Override
	public void onError(int method, int statusCode) {
		// TODO Auto-generated method stub
		switch (method) {
			case MarketAPI.ACTION_LOGIN:
				// 隐藏登录框
				try{
					dismissDialog(DIALOG_PROGRESS);
				}catch (IllegalArgumentException e) {
				}
				break;
			case MarketAPI.ACTION_GONGGAO:
				// 隐藏登录框
				break;
			default:
				break;
		}
	}


}
