package com.bbxiaoqu.ui;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.ImageOptions;
import com.bbxiaoqu.R;
import com.bbxiaoqu.comm.adapter.HorizontalListViewAdapter;
import com.bbxiaoqu.api.ApiAsyncTask.ApiRequestListener;
import com.bbxiaoqu.api.MarketAPI;
import com.bbxiaoqu.comm.service.db.MessGzService;
import com.bbxiaoqu.comm.service.db.UserService;
import com.bbxiaoqu.comm.tool.CustomerHttpClient;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.comm.view.BaseActivity;
import com.bbxiaoqu.ui.popup.DateUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DiscuzzActivity extends BaseActivity implements ApiRequestListener {

	private static final String TAG = DiscuzzActivity.class.getSimpleName ();
	private DemoApplication myapplication;
	private String infoid = "0";
	private String senduserid = "";
	private String sendusername = "";
	private String sex="";
	private String current_login_userid = "";
	private String current_login_usernickname = "";
	private String content = "";
	private String headface = "";
	private String sendtime = "";
	private String infocatagroy = "";
	private String address = "";
	private String photo = "";
	public static final int chatflag = 1;
	private AssetManager assetManager;
	private ArrayList<String> potolist = new ArrayList<String> ();
	private List<Map<String, String>> discussList = new ArrayList<Map<String, String>> ();
	public ImageView right_image;
	EditText discussContent;
	TextView chCounterText;
	public Button save_btn;
	private ImageView groupPopup;
	private Button group_discuss_submit;
	/*	private RelativeLayout gallery;
		HorizontalListView hListView;*/
	private String discuzz_content = "";
	private String getdatamethon = "";
	private String guid = "";
	private String gzaction = "";
	private String bmaction = "";
	private String adduserhelpinfotype = "";
	private JSONObject jsonobject;// 通过GUID获取的消息
	private ImageView sex_img;
	private ImageView headface_img;
	private TextView sendusertv;
	private TextView contenttv;
	private TextView sendtimetv;
	private TextView sendaddresstv;
	private boolean issolution = false;// 是否解决
	private String solutionid = "0";// 解决ID
	private static final int DIALOG_PROGRESS = 0;
	// 用户不存在（用户名错误）
	private static final int ERROR_CODE_USERNAME_NOT_EXIST = 211;
	// 用户密码错误
	private static final int ERROR_CODE_PASSWORD_INVALID = 212;


	private LocationClient mLocationClient;
	public Double nLatitude;
	public Double nLontitude;


	HorizontalListViewAdapter hListViewAdapter;
	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_discuzz);
		myapplication = (DemoApplication) this.getApplication();
		myapplication.getInstance().addActivity(this);
		getWindow ().setSoftInputMode (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		assetManager = this.getAssets ();
		initView ();
		Bundle Bundle1 = this.getIntent ().getExtras ();
		getdatamethon = Bundle1.getString ("put");
		guid = Bundle1.getString ("guid");
		infocatagroy = Bundle1.getString ("infocatagroy");
		if (!NetworkUtils.isNetConnected (myapplication)) {
			T.showShort (myapplication, "当前无网络连接！");
			NetworkUtils.showNoNetWorkDlg (DiscuzzActivity.this);
			return;
		}
		LoadData ();
		LoadLbsThread m = new LoadLbsThread ();
		new Thread (m).start ();
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.

	}

	private void LoadData() {
		if (!isFinishing ()) {
			showDialog (DIALOG_PROGRESS);
		} else {
			// 如果当前页面已经关闭，不进行登录操作
			return;
		}
		MarketAPI.getinfo (getApplicationContext (), this, guid);
	}




	Handler gzactionhandle = new Handler () {
		public void handleMessage(Message msg) {
			super.handleMessage (msg);
			Bundle data = msg.getData ();
			String action = data.getString ("action");
			if (action.equals ("add")) {
				MessGzService messgzService = new MessGzService (
						DiscuzzActivity.this);
				messgzService.addgz (guid, myapplication.getUserId ());

			} else if (action.equals ("remove")) {
				MessGzService messgzService = new MessGzService (
						DiscuzzActivity.this);
				messgzService.removegz (guid, myapplication.getUserId ());

			}
		}
	};

	private void initView() {
		/*title_tv = (TextView) findViewById(R.id.title);
		right_text_tv = (TextView) findViewById(R.id.right_text);
		right_text_tv.setVisibility(View.GONE);*/
		TextView PageTitle = (TextView) findViewById (R.id.PageTitle);
		PageTitle.setText ("发表评论");

		sendusertv = (TextView) findViewById (R.id.info_sendname);
		contenttv = (TextView) findViewById (R.id.info_content);
		sendtimetv = (TextView) findViewById (R.id.info_sendtime);
		sendaddresstv = (TextView) findViewById (R.id.info_sendaddress);


		//ext_mLayout = (LinearLayout) findViewById(R.id.layout_container_ext);
		sex_img = (ImageView) findViewById(R.id.sex_img);
		headface_img = (ImageView) findViewById (R.id.headface);
		headface_img.setOnClickListener (new OnClickListener () {
			public void onClick(View v) {
				if (!NetworkUtils.isNetConnected (myapplication)) {
					NetworkUtils.showNoNetWorkDlg (DiscuzzActivity.this);
					return;
				}
				Intent Intent1 = new Intent ();
				Intent1.setClass (DiscuzzActivity.this, ViewUserInfoActivity.class);
				Bundle arguments = new Bundle ();
				arguments.putString ("userid", senduserid);
				Intent1.putExtras (arguments);
				startActivity (Intent1);
			}
		});
		right_image = (ImageView) findViewById (R.id.top_menu_right_image);
		right_image.setVisibility (View.GONE);


		save_btn  = (Button) findViewById (R.id.save_btn);
		save_btn.setOnClickListener (new OnClickListener () {
			public void onClick(View v) {
				if (!NetworkUtils.isNetConnected (myapplication)) {
					NetworkUtils.showNoNetWorkDlg (DiscuzzActivity.this);
					return;
				}
				discussSubmit();

			}
		});
		discussContent = (EditText) findViewById (R.id.view_group_discuss);
		chCounterText = (TextView) findViewById(R.id.chCounterText);
		discussContent.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {
				String content = discussContent.getText().toString();
				if(content.length()>200)
				{
					discussContent.setText(content.substring(0,200));

					String newcontent = discussContent.getText().toString();
					chCounterText.setText(newcontent.length() + "/200");
					T.showShort (DiscuzzActivity.this,"超过最大输入字符数");

				}else {
					chCounterText.setText(content.length() + "/200");
				}

			}

		});
	}



	/*针对用户第一次会话做一次报名记录*/
	Runnable AddInfoHelpUserThread = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (!NetworkUtils.isNetConnected(myapplication)) {
				T.showShort(myapplication, "当前无网络连接！");
				NetworkUtils.showNoNetWorkDlg(DiscuzzActivity.this);
				return;
			}
			int result;
			String target = myapplication.getlocalhost()+"addinfohelpuser_v1.php";
			HttpPost httprequest = new HttpPost(target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			paramsList.add(new BasicNameValuePair("_userid",senduserid));// 被帮助的人
			paramsList.add(new BasicNameValuePair("_senduserid", myapplication.getUserId()));//志愿者
			paramsList.add(new BasicNameValuePair("_infoid", infoid));// 本人
			paramsList.add(new BasicNameValuePair("_guid", guid));// 本人

			paramsList.add(new BasicNameValuePair("_type",adduserhelpinfotype));// 本人
			if(adduserhelpinfotype.equals ("chat")) {
				paramsList.add (new BasicNameValuePair ("_content", "已通过私信方式与发布者进行了沟通"));//消息,评论就是评论,非评论就是自编一段话
			}else
			{
				paramsList.add (new BasicNameValuePair ("_content", discuzz_content));//消息,评论就是评论,非评论就是自编一段话
			}
			paramsList.add(new BasicNameValuePair("_action", bmaction));// 本人
			try {
				httprequest.setEntity(new UrlEncodedFormEntity(paramsList,
						"UTF-8"));
				HttpClient HttpClient1 = CustomerHttpClient.getHttpClient();
				HttpResponse httpResponse = HttpClient1.execute(httprequest);
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					String json = EntityUtils
							.toString(httpResponse.getEntity());
					result = Integer.parseInt(json);
				} else {
					result = 0;
				}
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	};

	/*针对用户第一次会话做一次报名记录*/
	Runnable savebmThread = new Runnable () {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (!NetworkUtils.isNetConnected (myapplication)) {
				T.showShort (myapplication, "当前无网络连接！");
				NetworkUtils.showNoNetWorkDlg (DiscuzzActivity.this);
				return;
			}
			int result;
			String target = myapplication.getlocalhost () + "adduserbminfo.php";
			HttpPost httprequest = new HttpPost (target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair> ();
			paramsList.add (new BasicNameValuePair ("_userid", myapplication.getUserId ()));// 本人
			paramsList.add (new BasicNameValuePair ("_infoid", infoid));// 本人
			paramsList.add (new BasicNameValuePair ("_guid", guid));// 本人
			paramsList.add (new BasicNameValuePair ("_action", bmaction));// 本人
			try {
				httprequest.setEntity (new UrlEncodedFormEntity (paramsList,
						"UTF-8"));
				HttpClient HttpClient1 = CustomerHttpClient.getHttpClient ();
				HttpResponse httpResponse = HttpClient1.execute (httprequest);
				if (httpResponse.getStatusLine ().getStatusCode () == HttpStatus.SC_OK) {
					String json = EntityUtils
							.toString (httpResponse.getEntity ());
					result = Integer.parseInt (json);
				} else {
					result = 0;
				}
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace ();
			} catch (ClientProtocolException e) {
				e.printStackTrace ();
			} catch (IOException e) {
				e.printStackTrace ();
			}
		}

	};


	private void alertDialog(Context context, String title, String message) {
		new AlertDialog.Builder (context)
				.setIcon (getResources ().getDrawable (R.mipmap.no_image))
				.setTitle (title).setMessage (message).create ().show ();
	}

	Runnable savediscussThread = new Runnable () {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			BasicHttpParams httpParameters = new BasicHttpParams ();
			HttpConnectionParams.setConnectionTimeout (httpParameters, 30000);
			HttpConnectionParams.setSoTimeout (httpParameters, 30000);
			int result;
			String target = myapplication.getlocalhost () + "discuzz_v1.php";
			HttpPost httprequest = new HttpPost (target);
			httprequest.setParams (httpParameters);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair> ();
			SimpleDateFormat sDateFormat = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
			String date = sDateFormat.format (new Date ());
			paramsList.add (new BasicNameValuePair ("_infoid", infoid));// 本人
			paramsList.add (new BasicNameValuePair ("_sendtime", date));// 本人
			paramsList.add (new BasicNameValuePair ("_puserid", senduserid));// 本人
			paramsList.add (new BasicNameValuePair ("_puser", sendusername));// 本人
			paramsList.add (new BasicNameValuePair ("_touserid", current_login_userid));// 发帐人
			paramsList.add (new BasicNameValuePair ("_touser", current_login_usernickname));// 发帐人
			paramsList.add (new BasicNameValuePair ("_message", discuzz_content));// 公司代号
			try {
				httprequest.setEntity (new UrlEncodedFormEntity (paramsList,
						"UTF-8"));
				HttpClient HttpClient1 = CustomerHttpClient.getHttpClient ();
				HttpResponse httpResponse = HttpClient1.execute (httprequest);
				if (httpResponse.getStatusLine ().getStatusCode () == HttpStatus.SC_OK) {
					String json = EntityUtils
							.toString (httpResponse.getEntity ());
					System.out.println (json);
					result = 1;
				} else {
					result = 0;
				}
				Message msg = new Message ();
				Bundle data = new Bundle ();
				data.putInt ("result", result);
				msg.setData (data);
				savehandler.sendMessage (msg);
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace ();
			} catch (ClientProtocolException e) {
				e.printStackTrace ();
			} catch (IOException e) {
				e.printStackTrace ();
			}
		}
	};

	Runnable savegzThread = new Runnable () {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int result;
			String target = myapplication.getlocalhost () + "addusergzinfo.php";
			HttpPost httprequest = new HttpPost (target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair> ();
			paramsList.add (new BasicNameValuePair ("_userid", myapplication.getUserId ()));// 本人
			paramsList.add (new BasicNameValuePair ("_infoid", infoid));// 本人
			paramsList.add (new BasicNameValuePair ("_guid", guid));// 本人
			paramsList.add (new BasicNameValuePair ("_action", gzaction));// 本人
			try {
				httprequest.setEntity (new UrlEncodedFormEntity (paramsList,
						"UTF-8"));
				HttpClient HttpClient1 = CustomerHttpClient.getHttpClient ();
				HttpResponse httpResponse = HttpClient1.execute (httprequest);
				if (httpResponse.getStatusLine ().getStatusCode () == HttpStatus.SC_OK) {
					String json = EntityUtils
							.toString (httpResponse.getEntity ());
					result = Integer.parseInt (json);
				} else {
					result = 0;
				}
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace ();
			} catch (ClientProtocolException e) {
				e.printStackTrace ();
			} catch (IOException e) {
				e.printStackTrace ();
			}
		}

	};

	Handler savehandler = new Handler () {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage (msg);
			Bundle data = msg.getData ();
			int result = data.getInt ("result");
			Log.i ("mylog", "请求结果-->" + result);
			if (result == 1) {
				Toast.makeText (DiscuzzActivity.this, "评论发表成功", Toast.LENGTH_SHORT)
						.show ();
				/*确保评论入库*/
				adduserhelpinfotype="pl";
				new Thread(AddInfoHelpUserThread).start();

			} else {
				Toast.makeText (DiscuzzActivity.this, "评论发表失败", Toast.LENGTH_SHORT)
						.show ();
			}
			Intent Intent1 = new Intent();
			Intent1.setClass(myapplication, ViewActivity.class);
			Bundle arguments = new Bundle();
			arguments.putString("put", "false");
			arguments.putString("guid", guid);
			Intent1.putExtras(arguments);
			startActivity(Intent1);
		}
	};
	String pos = "";
	@Override
	public void onSuccess(int method, Object obj) {
		// TODO Auto-generated method stub
		switch (method) {
			case MarketAPI.ACTION_GETINFO:
				try {
					dismissDialog (DIALOG_PROGRESS);
				} catch (IllegalArgumentException e) {
				}
				HashMap<String, String> result = (HashMap<String, String>) obj;
				String JsonContext = result.get ("guidinfo");
				if (JsonContext == null || JsonContext.equals ("")
						|| JsonContext.toString ().length () == 0) {
					return;
				} else {

					JSONArray jsonarray;
					try {
						jsonarray = new JSONArray (JsonContext);
						jsonobject = jsonarray.getJSONObject (0);
						senduserid = jsonobject.getString ("senduser");
						if (senduserid.equals (myapplication.getUserId ())) {

						} else {

						}
						infoid = jsonobject.getString ("id");
						content = jsonobject.getString ("content");
						headface = jsonobject.getString ("headface");
						sex = jsonobject.getString("sex");
						sendtime = jsonobject.getString ("sendtime");
						infocatagroy = jsonobject.getString ("infocatagroy");
						address = jsonobject.getString ("address");
						photo = jsonobject.getString ("photo");
						current_login_userid = myapplication.getUserId ();
						current_login_usernickname = myapplication.getNickname ();
						sendusername = jsonobject.getString ("username");
						if (jsonobject.getString ("issolution").equals ("1")) {
							issolution = true;
						} else {
							issolution = false;
						}
						solutionid = jsonobject.getString ("solutionid");
						if (sendusername.equals ("")) {
							sendusername = "匿名";
						}
						if(sex.equals ("0"))
						{
							sex_img.setImageResource (R.mipmap.xz_nan_icon);
						}else
						{
							sex_img.setImageResource (R.mipmap.xz_nv_icon);
						}
						sendusertv.setText (sendusername);
						contenttv.setText (content);
						sendtimetv.setText (sendtime);
						sendaddresstv.setText (address);

						if (headface.length () > 0) {
							String headfaceurl = myapplication.getlocalhost () + "/uploads/" + headface;
							ImageLoader.getInstance ().displayImage (headfaceurl, headface_img, ImageOptions.getOptions ());
						}
						if (photo.indexOf (",") > 0) {
							for (int i = 0; i < photo.split (",").length; i++) {
								String fileName = myapplication.getlocalhost () + "uploads/"
										+ photo.split (",")[i];
								potolist.add (fileName);
							}
						} else {
							if (photo.length () > 0) {
								String fileName = myapplication.getlocalhost () + "uploads/"
										+ photo;
								potolist.add (fileName);
							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace ();
					}
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
			case MarketAPI.ACTION_GETINFO:
				// 隐藏登录框
				try {
					dismissDialog (DIALOG_PROGRESS);
				} catch (IllegalArgumentException e) {
				}
				break;
			default:
				break;
		}
	}


	private void initlsb() {
		// TODO Auto-generated method stub
		mLocationClient = new LocationClient (this.myapplication);
		LocationClientOption option = new LocationClientOption ();
		option.setLocationMode (LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType ("gcj02");// 返回的定位结果是百度经纬度，默认值gcj02
		int span = 1000 * 20;
		option.setScanSpan (span);// 设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress (true);
		mLocationClient.setLocOption (option);
		mLocationClient.registerLocationListener (new BDLocationListener () {

			@Override
			public void onReceiveLocation(BDLocation location) {
				// TODO Auto-generated method stub
				if (location == null) {
					//Log.v(TAG, "HomeActivity location empty");
					return;
				}
				nLatitude = location.getLatitude ();
				nLontitude = location.getLongitude ();
				Log.i ("mylog", nLatitude + "-" + nLontitude);
				myapplication.setLat (String.valueOf (nLatitude));
				myapplication.setLng (String.valueOf (nLontitude));
				myapplication.updatelocation ();
				mLocationClient.stop ();
			}
		});
		mLocationClient.start ();
		mLocationClient.requestLocation ();
	}



	class LoadLbsThread implements Runnable {
		public void run() {
			Message message = new Message ();
			message.what = 1;
			handler.sendMessage (message);
		}
	}

	Handler handler = new Handler () {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					initlsb ();
					break;
			}
			super.handleMessage (msg);
		}

	};

	public void discussSubmit() {

		discuzz_content = discussContent.getText ().toString ();
		discussContent.setText ("");
		InputMethodManager imm = (InputMethodManager) getSystemService (Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput (0, InputMethodManager.HIDE_NOT_ALWAYS); // 隐软键盘
		// 判断content,不能为null或者特定字符
		if (discuzz_content == null || "".equals (discuzz_content)) {
			this.alertDialog (DiscuzzActivity.this, "Error", "请输入回复内容! ");
			Log.e (TAG, "discuss content is null ! ");
			return;
		}
		Map<String, String> map = new HashMap<String, String> ();
		// 给map设置要显示的值
		UserService uService = new UserService (DiscuzzActivity.this);
		String headfaceurl = uService.getheadface (current_login_userid);
		map.put ("headface", headfaceurl);
		map.put ("distime", DateUtils.formaterDate2YMDHm (new Date (System
				.currentTimeMillis ())));
		map.put ("content", discuzz_content);
		// 设置父贴的发帖人信息
		map.put ("puid", senduserid);
		map.put ("pname", sendusername);
		// 设置自己的信息
		map.put ("uid", current_login_userid);
		map.put ("username", current_login_usernickname);
		discussList.add (map);
		//showhiddendiscuss ();
		new Thread (savediscussThread).start ();
		//关联关系
		bmaction = "add";
		new Thread (savebmThread).start ();
	}

}
