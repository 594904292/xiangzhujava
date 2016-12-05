package com.bbxiaoqu.ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.ImageOptions;
import com.bbxiaoqu.R;
import com.bbxiaoqu.comm.adapter.EvaluateAdapter;
import com.bbxiaoqu.comm.jsonservices.GetJson;
import com.bbxiaoqu.comm.tool.CustomerHttpClient;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.StreamTool;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.comm.widget.XCRoundImageView;
import com.bbxiaoqu.ui.fragment.FragmentU1;
import com.bbxiaoqu.ui.fragment.FragmentU2;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.io.File;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

public class ViewUserInfoActivity extends FragmentActivity implements TabHost.OnTabChangeListener {
	private DemoApplication myapplication;
	private TextView title;
	private TextView username_tv;
	private TextView score_tv;
	private TextView telphone_tv;
	private Button save,chat;
	private String username = "";
	private String telphone = "";
	private String headface = "";
	private String score="";
	private String headfacepath = "";
	/** ImageView对象 */
	private XCRoundImageView iv_photo;
	private  String userid;
	private final String TAG = "UserInfoActivity";
	private AsyncHttpClient client;

	//private Class fragmentArray[] = {FragmentU1.class, FragmentU2.class, FragmentU3.class, FragmentU4.class};
	//private String mTextviewArray[] = {"收到感谢", "求助", "回复", "参与"};

	private Class fragmentArray[] = {FragmentU1.class, FragmentU2.class};
	private String mTextviewArray[] = {"收到感谢", "求助"};
	private FragmentTabHost mTabHost;
	private LayoutInflater layoutInflater;


	private String mUserid;

	public String getmUserid() {
		return mUserid;
	}

	public void setmUserid(String userid) {
		this.mUserid = userid;
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewuserinfo);
		myapplication = (DemoApplication) this.getApplication();
		myapplication.getInstance().addActivity(this);
		Bundle Bundle1 = this.getIntent().getExtras();
		userid = Bundle1.getString("userid");
		setmUserid(userid);
		initView();
		initData();
		//loadlist();
		initTabHost();

	}


	private void initTabHost() {
		mTabHost = (FragmentTabHost) findViewById(R.id.usertabhost);
		//点击动作参考http://www.jerehedu.com/fenxiang/162138_for_detail.htm
		/*final TabWidget tabWidget = mTabHost.getTabWidget();
		for (int i =0; i < tabWidget.getChildCount(); i++) {
			tabWidget.getChildAt(i).getLayoutParams().height = 60;
			tabWidget.getChildAt(i).getLayoutParams().width = 65;
		}*/

		final TabWidget tabWidget = mTabHost.getTabWidget();
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		//mTabHost.getTabWidget().setMinimumHeight(30);// 设置tab的高度

		int height =30;

		mTabHost.getTabWidget ().setShowDividers (LinearLayout.SHOW_DIVIDER_MIDDLE);
		mTabHost.getTabWidget().setDividerDrawable(new ColorDrawable (Color.GRAY));
		int count = fragmentArray.length;
		for (int i = 0; i < count; i++) {
			//对Tab按钮添加标记和图片
			TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
			//添加Fragment

			mTabHost.addTab(tabSpec, fragmentArray[i], null);
			//ea194a
			mTabHost.getTabWidget().getChildAt(i).setBackgroundResource (R.drawable.selector_tab_background);


		}

		mTabHost.setOnTabChangedListener(this);

	}


	private View getTabItemView(int index) {
		layoutInflater = LayoutInflater.from(this);

		View view = layoutInflater.inflate(R.layout.tab_item_tag_view, null);
		TextView textView = (TextView) view.findViewById(R.id.textview);
		textView.setTextColor(Color.GRAY);
		textView.setText(mTextviewArray[index]);
		return view;
	}



	@Override
	public void onTabChanged(String tabId) {
		TabWidget tabw = mTabHost.getTabWidget();
		for(int i=0;i<tabw.getChildCount();i++){
			View view=tabw.getChildAt(i);
			if(i==mTabHost.getCurrentTab()){
				((TextView)view.findViewById(R.id.textview)).setTextColor(Color.RED);
			}else{
				((TextView)view.findViewById(R.id.textview)).setTextColor(getResources().getColor(R.color.gray));

			}

		}
	}

	EvaluateAdapter adapter;
	private List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
	private static final String[] sexs ={ " 男 " , "女" };
	private void initView() {

		username_tv = (TextView) findViewById(R.id.username);
		score_tv = (TextView) findViewById(R.id.score);
		telphone_tv = (TextView) findViewById(R.id.telphone);
		save = (Button) findViewById(R.id.save);

		save.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!NetworkUtils.isNetConnected(myapplication)) {			
					T.showShort(myapplication, "当前无网络连接,请稍后再试！");
					NetworkUtils.showNoNetWorkDlg(ViewUserInfoActivity.this);
					return;
				}
				new Thread(addfriends).start();
			}
		});
		chat= (Button) findViewById(R.id.chat);
		chat.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ViewUserInfoActivity.this,ChattingActivity.class);
				Bundle arguments = new Bundle();
				arguments.putString("to", userid);
				arguments.putString("my",myapplication.getUserId());
				intent.putExtras(arguments);
				
				startActivity(intent);
			}
		});
		iv_photo = (XCRoundImageView) findViewById(R.id.iv_photo);
	}




	Runnable addfriends = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int result;
			String target = myapplication.getlocalhost()+"addfriends.php";
			HttpPost httprequest = new HttpPost(target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			paramsList.add(new BasicNameValuePair("mid1", myapplication.getUserId()));
			paramsList.add(new BasicNameValuePair("mid2", userid));
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = sDateFormat.format(new java.util.Date());
			paramsList.add(new BasicNameValuePair("addtime", date));
			if(save.getTag().equals("关注"))
			{
				paramsList.add(new BasicNameValuePair("action", "add"));
			}else
			{
				paramsList.add(new BasicNameValuePair("action", "del"));
			}			
			try {
				httprequest.setEntity(new UrlEncodedFormEntity(paramsList,
						"UTF-8"));
				HttpClient HttpClient1 = CustomerHttpClient.getHttpClient();
				HttpResponse httpResponse = HttpClient1.execute(httprequest);
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					String json = EntityUtils.toString(httpResponse.getEntity());
					System.out.println(json);					
					result = 1;
				} else {
					result = 0;
				}
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putInt("result", result);
				msg.setData(data);
				publishhandler.sendMessage(msg);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	Handler publishhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			int result = data.getInt("result");
			Log.i("mylog", "请求结果-->" + result);			
			if (result == 1) {
				if(save.getTag().equals("关注"))
				{
					save.setText("取消关注");
					save.setTag("取消关注");
					Toast.makeText(ViewUserInfoActivity.this, "关注成功",Toast.LENGTH_SHORT).show();
				}else
				{
					save.setText("关注");
					save.setTag("关注");
					Toast.makeText(ViewUserInfoActivity.this, "取消关注成功",Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(ViewUserInfoActivity.this,"操作失败",Toast.LENGTH_SHORT).show();
			}
		}
	};
	PopupMenu popupMenu;
	Menu menu;
	final String[] tsarr = new String[] { "广告类", "政治有害","暴恐类","淫秽色情","赌博类","诈骗类","其它有害类"};
	int selectedIndex=0;
	private void initData() {
		TextView PageTitle = (TextView) findViewById(R.id.PageTitle);
		PageTitle.setText ("个人详情");
		if (!NetworkUtils.isNetConnected(myapplication)) {			
			T.showShort(myapplication, "当前无网络连接,请稍后再试！");
			NetworkUtils.showNoNetWorkDlg(ViewUserInfoActivity.this);
			return;
		}
		TextView popupmenu_btn = (TextView) findViewById(R.id.popupmenu_btn);
		popupmenu_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				popupMenu.show();
			}
		});
		popupMenu = new PopupMenu(this, findViewById(R.id.popupmenu_btn));
		menu = popupMenu.getMenu();
		// 通过代码添加菜单项
		menu.add(Menu.NONE, Menu.FIRST + 0, 0, "举报");
		//menu.add(Menu.NONE, Menu.FIRST + 1, 1, "加入黑名单");
		//menu.add(Menu.NONE, Menu.FIRST + 2, 2, "屏蔽他的话题");

		// 通过XML文件添加菜单项
		/*MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.popupmenu, menu);*/

		// 监听事件
		popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener () {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
					case Menu.FIRST + 0:
						Dialog dialog = new AlertDialog.Builder (ViewUserInfoActivity.this)
								.setTitle ("选择举报类别")
								.setPositiveButton ("确定", new DialogInterface.OnClickListener () {

									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										Toast.makeText (ViewUserInfoActivity.this,
												tsarr[selectedIndex], Toast.LENGTH_SHORT)
												.show ();

											new Thread (savetsThread).start ();


									}
								})
								.setSingleChoiceItems (tsarr, 0,
										new DialogInterface.OnClickListener () {

											public void onClick(DialogInterface dialog,
																int which) {
												// TODO Auto-generated method stub
												selectedIndex = which;
											}
										})
								.setNegativeButton ("取消", new DialogInterface.OnClickListener () {

									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub

									}
								}).create ();
						dialog.show ();
						break;
					case Menu.FIRST + 1:
						Toast.makeText(ViewUserInfoActivity.this, "加入黑名单",
								Toast.LENGTH_LONG).show();
						break;
					case Menu.FIRST + 2:
						Toast.makeText(ViewUserInfoActivity.this, "屏蔽他的话题",
								Toast.LENGTH_LONG).show();
						break;
					default:
						break;
				}
				return false;
			}
		});

	new Thread(loaduserinfo).start();
	}

	public void popupmenu(View v) {
		popupMenu.show();
	}

	private void upLoadByAsyncHttpClient(String uploadUrl)
			throws FileNotFoundException {
		AsyncBody(uploadUrl, headfacepath);
	}

	private void AsyncBody(String uploadUrl, String localpath)
			throws FileNotFoundException {
		RequestParams params = new RequestParams();
		client = new AsyncHttpClient();
		params.put("uploadfile", new File(localpath));
		client.post(uploadUrl, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, String arg1) {
				super.onSuccess(arg0, arg1);
				Log.i(TAG, arg1);
			}
		});
	}

	Runnable loaduserinfo = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int result;
			String target = myapplication.getlocalhost()+"getuserinfo.php?userid="+ userid;
			HttpGet httprequest = new HttpGet(target);
			try {
				HttpClient HttpClient1 = CustomerHttpClient.getHttpClient();
				HttpResponse httpResponse = HttpClient1.execute(httprequest);
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					InputStream jsonStream = null;
					jsonStream = httpResponse.getEntity().getContent();
					byte[] data = null;
					try {
						data = StreamTool.read(jsonStream);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String json = new String(data);
					JSONArray jsonarray;
					try {
						jsonarray = new JSONArray(json);
						JSONObject jsonobject = jsonarray.getJSONObject(0);
						username = jsonobject.getString("username");
						telphone = jsonobject.getString("telphone");
						headface = jsonobject.getString("headface");
						score = jsonobject.getString("score");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println(json);
					result = 1;
				} else {
					result = 0;
				}
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putString("username", username);
				data.putString("score", score);
				data.putString("telphone", telphone);
				data.putString("headface", headface);
				msg.setData(data);
				laodhandler.sendMessage(msg);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	Handler laodhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			username_tv.setText(""+data.getString("username"));
			score_tv.setText("积分:"+data.getString("score"));
			String telphone=data.getString("telphone");
			telphone=telphone.substring(0,3).concat("****").concat(telphone.substring(telphone.length()-4,telphone.length()));
			//telphone_tv.setText("电话："+data.getString("telphone"));
			telphone_tv.setText(telphone);
			String fileName = myapplication.getlocalhost()+"uploads/"+ data.getString("headface");
			ImageLoader.getInstance().displayImage(fileName, iv_photo, ImageOptions.getOptions());
			new Thread(ajaxloadfriend).start();//得到是否关注
		}
	};

	JSONObject jsonobject;//通过GUID获取的消息
	String isfriend="";
	Runnable ajaxloadfriend = new Runnable() {
		@Override
		public void run() {
			if (!NetworkUtils.isNetConnected(myapplication)) {
				T.showShort(myapplication, "当前无网络连接！");
				NetworkUtils.showNoNetWorkDlg(ViewUserInfoActivity.this);
				return;
			}
			String target = myapplication.getlocalhost()+"getisfriends.php?mid1="+myapplication.getUserId()+"&mid2="+userid;
			String json = GetJson.GetJson(target);
			if(json.startsWith("Error:"))
			{
				T.showShort(ViewUserInfoActivity.this, "网络错误:"+json);
				return;
			}
			JSONArray jsonarray;
			try {
				jsonobject = new JSONObject(json);
				//jsonobject = jsonarray.getJSONObject(0);
				isfriend = jsonobject.getString("isfriend");				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Message msg = new Message();
			Bundle bundle = new Bundle();
			bundle.putString("isfriend", isfriend);
			msg.setData(bundle);
			friendhandler.sendMessage(msg);
		}};
	
		Handler friendhandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Bundle data = msg.getData();
				if(data.getString("isfriend").equals("yes"))
				{
					save.setText("取消关注");
					save.setTag("取消关注");
				}else
				{
					save.setText("关注");
					save.setTag("关注");
				}
			}};


	Runnable savetsThread = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int result;
			String tsaction = "add";
			String target = myapplication.getlocalhost() + "addusertsmember.php";
			HttpPost httprequest = new HttpPost(target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			paramsList.add(new BasicNameValuePair("_uid", myapplication.getUserId()));// 本人
			paramsList.add(new BasicNameValuePair("_tsuid", getmUserid()));// 本人
			paramsList.add(new BasicNameValuePair("_tsreason", tsarr[selectedIndex]));// 本人
			paramsList.add(new BasicNameValuePair("_action", tsaction));// 本人
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

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

	public void doBack(View view) {
		onBackPressed();
	}
	
}
