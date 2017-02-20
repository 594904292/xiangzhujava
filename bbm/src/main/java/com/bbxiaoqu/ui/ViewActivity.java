package com.bbxiaoqu.ui;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.ImageOptions;
import com.bbxiaoqu.R;
import com.bbxiaoqu.api.MarketAPI;
import com.bbxiaoqu.api.ApiAsyncTask.ApiRequestListener;
import com.bbxiaoqu.comm.gallery.BigImgActivity;
import com.bbxiaoqu.comm.service.db.MessTsService;
import com.bbxiaoqu.comm.tool.ListViewUtil;
import com.bbxiaoqu.comm.widget.ImageViewSubClass;
import com.bbxiaoqu.comm.widget.XCRoundImageView;
import com.bbxiaoqu.comm.view.HorizontalListView;
import com.bbxiaoqu.comm.adapter.HorizontalListViewAdapter;
import com.bbxiaoqu.comm.jsonservices.GetJson;
import com.bbxiaoqu.comm.service.db.MessGzService;
import com.bbxiaoqu.comm.tool.CustomerHttpClient;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.comm.adapter.ListLazyAdapter;
import com.bbxiaoqu.comm.view.BaseActivity;
import com.bbxiaoqu.comm.widget.MyListView;
import com.bbxiaoqu.comm.widget.XCRoundImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bbxiaoqu.comm.adapter.ListLazyAdapter.Callback;

public class ViewActivity extends BaseActivity implements OnItemClickListener,ApiRequestListener,
		Callback {
	private static final String TAG = ViewActivity.class.getSimpleName();
	private DemoApplication myapplication;
	private String infoid = "0";
	private String senduserid = "";
	private String sendusername = "";
	private String sex="";
	private String current_login_userid = "";
	private String current_login_usernickname = "";
	private String title = "";
	private String content = "";
	private String headface = "";
	private String sendtime = "";
	private String infocatagroy = "";
	private String address = "";
	private String photo = "";
	public static final int chatflag = 1;
	private AssetManager assetManager;
	private ArrayList<String> potolist = new ArrayList<String>();
	private List<Map<String, String>> discussList = new ArrayList<Map<String, String>>();
	public ImageView right_image;
	private MyListView mylistView;
	private ImageView groupPopup;
	private Button group_discuss_submit;
	private RelativeLayout rl_bottom;
	//private RelativeLayout gallery;
	//private HorizontalListView hListView;
	RelativeLayout img_row;
	//img_row =(RelativeLayout) convertView.findViewById(R.id.img_row);
	private String getdatamethon = "";
	private String guid = "";
	private String gzaction = "";
	private String tsaction = "";
	private String bmaction = "";
	private String adduserhelpinfotype = "";


	private JSONObject jsonobject;// 通过GUID获取的消息
	private XCRoundImageView headface_img;
	private TextView sendusertv ;
	private ImageView sex_img;
	private TextView contenttv ;
	private ImageView statusimg ;
	private TextView sendtimetv ;
	private TextView sendaddresstv ;

	private TextView left_chat_btn;//底部
	private TextView right_chat_btn;//底部
	private TextView btn_gz;//右边
	private TextView inforeport_btn;//举报
	private Button evaluate_btn;//评价帮助我的人

	private boolean issolution = false;// 是否解决
	private String solutionid = "0";// 解决,也就是评论对应的ID
	private static final int DIALOG_PROGRESS = 0;
	// 用户不存在（用户名错误）
	private static final int ERROR_CODE_USERNAME_NOT_EXIST = 211;
	// 用户密码错误
	private static final int ERROR_CODE_PASSWORD_INVALID = 212;


	private LocationClient mLocationClient;
	public Double nLatitude;
	public Double nLontitude;


	Dialog tsdialog;
	AlertDialog seltypedialog;

	HorizontalListViewAdapter hListViewAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view);
		myapplication = (DemoApplication) this.getApplication();
		myapplication.getInstance().addActivity(this);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		assetManager = this.getAssets();
		initView();
		Bundle Bundle1 = this.getIntent().getExtras();
		getdatamethon = Bundle1.getString("put");
		infoid = Bundle1.getString("infoid");
		guid = Bundle1.getString("guid");
		infocatagroy = Bundle1.getString("infocatagroy");
		if (!NetworkUtils.isNetConnected(myapplication)) {
			T.showShort(myapplication, "当前无网络连接！");
			NetworkUtils.showNoNetWorkDlg(ViewActivity.this);
			return;
		}
		LoadData();
		LoadLbsThread m = new LoadLbsThread();
		new Thread(m).start();
	}

	private void LoadData() {
		if (!isFinishing()) {
            showDialog(DIALOG_PROGRESS);
	    } else {
	       // 如果当前页面已经关闭，不进行登录操作
	       return;
	    }
		if(guid!=null&&guid.length ()>0) {
			MarketAPI.getinfo (getApplicationContext (), this,  guid);
		}else
		{
			MarketAPI.getinfo (getApplicationContext (), this, "infoid", infoid);
		}
	}

	
	private Handler basehandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				findViewById(R.id.info_sendname).setVisibility(View.GONE);
				findViewById(R.id.info_sendtime).setVisibility(View.GONE);
				findViewById(R.id.info_sendaddress).setVisibility(View.GONE);
				findViewById(R.id.info_gzbutton1).setVisibility(View.GONE);
				findViewById(R.id.headface).setVisibility(View.GONE);
				findViewById(R.id.view_rl_bottom).setVisibility(View.GONE);
				TextView content = (TextView) findViewById(R.id.info_content);
				content.setText("本条记录有不良、敏感信息,管理员已删除....");
				T.showShort(ViewActivity.this, "数据服务端已删除");
				break;
			case 2:
				Bundle data = msg.getData();
				String error = data.getString("error");
				T.showShort(ViewActivity.this, "网络错误:" + error);
				break;
			default:
				break;
			}
		};
	};

	void init() {
		int num=0;
		WindowManager wm1 = getWindowManager();
		int width = wm1.getDefaultDisplay().getWidth();
		int w=0;
		int h=0;
			num=potolist.size();
			if(num==1)
			{
				w=width/4;
				h=width/4;
			}else
			{
				w=width/4;
				h=width/4;
			}
			LinearLayout m_LinearLayout = new LinearLayout(this);
			m_LinearLayout.setOrientation(LinearLayout.HORIZONTAL);
			LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(width-20,h+20);
			int pic_num=potolist.size();
			if(pic_num>4)
			{
				pic_num=4;
			}
			for (int i = 0; i < pic_num; i++) {
				ImageViewSubClass image = new ImageViewSubClass (this);
				image.setTag (i);
				image.setAdjustViewBounds(true);
				ImageLoader.getInstance().displayImage(potolist.get (i).toString (), image, ImageOptions.getOptions());
				image.setScaleType (ImageView.ScaleType.FIT_XY);
				image.setPadding (0,0,0,0);
				image.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(ViewActivity.this,
								BigImgActivity.class);
						String position=v.getTag ().toString ();
						StringBuilder sb=new StringBuilder();
						for(int i=0;i<potolist.size();i++)
						{
							sb.append(potolist.get(i).toString());
							if(i<potolist.size()-1)
							{
								sb.append(",");
							}
						}
						intent.putExtra("imageName", position);
						intent.putExtra("imageNames", sb.toString());
						startActivity(intent);
					}
				});
				LinearLayout.LayoutParams layoutParams =new LinearLayout.LayoutParams(w-20,h-10);
				layoutParams.setMargins (10,10,10,10);
				m_LinearLayout.addView(image, layoutParams);
			}
			img_row.addView (m_LinearLayout,param);

	}



	Handler tsactionhandle = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			String action = data.getString("action");
			if (action.equals("add")) {
				MessTsService messtsService = new MessTsService(
						ViewActivity.this);
				messtsService.addts(guid, myapplication.getUserId());
				inforeport_btn.setText("取消投诉");
				inforeport_btn.setTag("取消投诉");
			} else if (action.equals("remove")) {
				MessTsService messtsService = new MessTsService(
						ViewActivity.this);
				messtsService.removets(guid, myapplication.getUserId());
				inforeport_btn.setText("投诉");
				inforeport_btn.setTag("投诉");
			}
		}
	};

	Handler gzactionhandle = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			String action = data.getString("action");
			if (action.equals("add")) {
				MessGzService messgzService = new MessGzService(
						ViewActivity.this);
				messgzService.addgz(guid, myapplication.getUserId());
				btn_gz.setText("取消收藏");
				btn_gz.setTag("取消收藏");
			} else if (action.equals("remove")) {
				MessGzService messgzService = new MessGzService(
						ViewActivity.this);
				messgzService.removegz(guid, myapplication.getUserId());
				btn_gz.setText("收藏");
				btn_gz.setTag("收藏");
			}
		}
	};

	final String[] tsarr = new String[] { "广告类", "政治有害","暴恐类","淫秽色情","赌博类","诈骗类","其它有害类"};
	int selectedIndex=0;
	private void initView() {

		TextView PageTitle = (TextView) findViewById(R.id.PageTitle);
		PageTitle.setText ("我能帮");
		sendusertv = (TextView) findViewById(R.id.info_sendname);
		sex_img = (ImageView) findViewById(R.id.sex_img);
		contenttv = (TextView) findViewById(R.id.info_content);
		statusimg = (ImageView) findViewById(R.id.statusimg);
		sendtimetv = (TextView) findViewById(R.id.info_sendtime);
		sendaddresstv = (TextView) findViewById(R.id.info_sendaddress);
		mylistView = (MyListView) findViewById(R.id.pl_list);
		mylistView.setDividerHeight (1);

		/*gallery = (RelativeLayout) findViewById(R.id.gallery);
		hListView = (HorizontalListView)findViewById(R.id.horizon_listview);*/
		//ext_mLayout = (LinearLayout) findViewById(R.id.layout_container_ext);
		img_row =(RelativeLayout) findViewById(R.id.img_row);
		rl_bottom = (RelativeLayout) findViewById(R.id.view_rl_bottom);// 评论布局
		headface_img = (XCRoundImageView) findViewById(R.id.headface);
		headface_img.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!NetworkUtils.isNetConnected(myapplication)) {
					NetworkUtils.showNoNetWorkDlg(ViewActivity.this);
					return;
				}
				Intent Intent1 = new Intent();
				Intent1.setClass(ViewActivity.this, ViewUserInfoActivity.class);
				Bundle arguments = new Bundle();
				arguments.putString("userid", senduserid);
				Intent1.putExtras(arguments);
				startActivity(Intent1);
			}
		});
		right_image = (ImageView) findViewById(R.id.top_menu_right_image);
		right_image.setVisibility(View.VISIBLE);
		right_image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(ViewActivity.this,SearchActivity.class);									
				startActivity(intent);
			}
		});
		inforeport_btn = (TextView) findViewById(R.id.info_report_btn);
		inforeport_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//T.showShort (ViewActivity.this,"举报未实现");
				String name = inforeport_btn.getTag().toString();
				if (name.equals("取消投诉")) {
					Message msg = new Message ();
					Bundle data = new Bundle ();
					data.putString ("action", "remove");
					msg.setData (data);
					tsactionhandle.sendMessage (msg);
					tsaction = "remove";
					new Thread (savetsThread).start ();
				}else {
					if(tsdialog==null) {
						tsdialog = new AlertDialog.Builder (ViewActivity.this)
								.setTitle ("选择举报类别")
								.setPositiveButton ("确定", new DialogInterface.OnClickListener () {

									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										Toast.makeText (ViewActivity.this,
												tsarr[selectedIndex], Toast.LENGTH_SHORT)
												.show ();
										String name = inforeport_btn.getTag ().toString ();
										if (name.equals ("投诉")) {
											Message msg = new Message ();
											Bundle data = new Bundle ();
											data.putString ("action", "add");
											msg.setData (data);
											tsactionhandle.sendMessage (msg);
											tsaction = "add";
											new Thread (savetsThread).start ();

										}
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
					}
					tsdialog.show ();
				}
			}
		});
		evaluate_btn = (Button) findViewById(R.id.info_evaluate_btn);
		evaluate_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ViewActivity.this,BmUserActivity.class);
				Bundle arguments = new Bundle();
				arguments.putString("guid", guid);
				intent.putExtras(arguments);
				startActivity(intent);
			}
		});
		btn_gz = (TextView) findViewById(R.id.info_gzbutton1);
		btn_gz.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!NetworkUtils.isNetConnected(myapplication)) {
					T.showShort(myapplication, "当前无网络连接！");
					NetworkUtils.showNoNetWorkDlg(ViewActivity.this);
					return;
				}
				String name = v.getTag().toString();
				if (name.equals("收藏")) {
					Message msg = new Message();
					Bundle data = new Bundle();
					data.putString("action", "add");
					msg.setData(data);
					gzactionhandle.sendMessage(msg);
					gzaction = "add";
					new Thread(savegzThread).start();

				} else if (name.equals("取消收藏")) {
					Message msg = new Message();
					Bundle data = new Bundle();
					data.putString("action", "remove");
					msg.setData(data);
					gzactionhandle.sendMessage(msg);
					gzaction = "remove";
					new Thread(savegzThread).start();
				}
			}
		});
		left_chat_btn = (TextView) findViewById(R.id.left_chat_btn);
		left_chat_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!NetworkUtils.isNetConnected(myapplication)) {
					T.showShort(myapplication, "当前无网络连接！");
					NetworkUtils.showNoNetWorkDlg(ViewActivity.this);
					return;
				}
				if(senduserid.equals (myapplication.getUserId ())) {
					Toast.makeText(ViewActivity.this, "请选择与其他人聊天", Toast.LENGTH_SHORT).show();
				}else{
					bmaction = "add";
					new Thread (savebmThread).start ();
					adduserhelpinfotype = "chat";
					new Thread (AddInfoHelpUserThread).start ();
					Intent intent = new Intent (ViewActivity.this, ChattingActivity.class);
					Bundle arguments = new Bundle ();
					arguments.putString ("to", senduserid);
					arguments.putString ("my", myapplication.getUserId ());
					intent.putExtras (arguments);
					startActivity (intent);
				}
			}
		});

		right_chat_btn = (TextView) findViewById(R.id.right_chat_btn);
		right_chat_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!NetworkUtils.isNetConnected(myapplication)) {
					T.showShort(myapplication, "当前无网络连接！");
					NetworkUtils.showNoNetWorkDlg(ViewActivity.this);
					return;
				}
				Intent Intent1 = new Intent ();
				Intent1.setClass (myapplication, DiscuzzActivity.class);
				adduserhelpinfotype = "pl";
				Bundle arguments = new Bundle ();
				arguments.putString ("put", "false");
				arguments.putString ("guid", guid);
				Intent1.putExtras (arguments);
				startActivity (Intent1);
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
				NetworkUtils.showNoNetWorkDlg(ViewActivity.this);
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
				paramsList.add (new BasicNameValuePair ("_content", "已通过私信方式与发布者进行了沟通"));//消息,评论就是评论,非评论就是自编一段话
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
	Runnable savebmThread = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (!NetworkUtils.isNetConnected(myapplication)) {
				T.showShort(myapplication, "当前无网络连接！");
				NetworkUtils.showNoNetWorkDlg(ViewActivity.this);
				return;
			}
			int result;
			String target = myapplication.getlocalhost()+"adduserbminfo.php";
			HttpPost httprequest = new HttpPost(target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			paramsList.add(new BasicNameValuePair("_userid", myapplication.getUserId()));// 本人
			paramsList.add(new BasicNameValuePair("_infoid", infoid));// 本人
			paramsList.add(new BasicNameValuePair("_guid", guid));// 本人
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


	Runnable loaddiscuzzThread = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			loaddiscuzzBody();
		}

	};

	private void loaddiscuzzBody() {
		if (!NetworkUtils.isNetConnected(myapplication)) {
			T.showShort(myapplication, "当前无网络连接！");
			NetworkUtils.showNoNetWorkDlg(ViewActivity.this);
			return;
		}
		String target = myapplication.getlocalhost()+ "/getdiscuzz_v1.php?infoid=" + this.infoid;
		try {
			String json = GetJson.GetJson(target);
			Message msg = new Message();
			Bundle bundledata = new Bundle();
			bundledata.putString("json", json);
			msg.setData(bundledata);
			discuzzhhandler.sendMessage(msg);
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	}

	Handler discuzzhhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			String json = data.getString("json");
			try {
				discuzzhandle(json);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	private void discuzzhandle(String json) throws JSONException {
		JSONArray jsonarray = new JSONArray(json);
		for (int i = 0; i < jsonarray.length(); i++) {
			JSONObject jsonobject = jsonarray.getJSONObject(i);
			Map<String, String> map = new HashMap<String, String>();
			map.put("id", jsonobject.getString("id").toString());

			map.put("headface", jsonobject.getString("headface").toString());
			map.put("sex", jsonobject.getString("sex").toString());
			// 给map设置要显示的值
			map.put("distime", jsonobject.getString("sendtime").toString());
			map.put("content", jsonobject.getString("message").toString());

			// 设置父贴的发帖人信息
			map.put("puid", jsonobject.getString("touserid").toString());
			map.put("pname", jsonobject.getString("touser").toString());

			// 设置自己的信息
			map.put("uid", jsonobject.getString("senduserid").toString());
			map.put("username", jsonobject.getString("senduser").toString());
			discussList.add(map);
		}
		showhiddendiscuss();
	}

	private void showhiddendiscuss() {

			ListLazyAdapter adapter = new ListLazyAdapter(this, discussList,this, senduserid, this.issolution, this.solutionid);
			mylistView.setAdapter(adapter);
			ListViewUtil.setListViewHeightBasedOnChildren(mylistView);
			LinearLayout emptyView = (LinearLayout) findViewById(R.id.nomess);
			mylistView.setEmptyView(emptyView);
	}


	private void alertDialog(Context context, String title, String message) {
		new AlertDialog.Builder(context)
				.setIcon(getResources().getDrawable(R.mipmap.no_image))
				.setTitle(title).setMessage(message).create().show();
	}


	Runnable savetsThread = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int result;
			String target = myapplication.getlocalhost() + "addusertsinfo.php";
			HttpPost httprequest = new HttpPost(target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			paramsList.add(new BasicNameValuePair("_uid", myapplication.getUserId()));// 本人
			paramsList.add(new BasicNameValuePair("_tsuid", senduserid));// 本人
			paramsList.add(new BasicNameValuePair("_infoid", infoid));// 本人
			paramsList.add(new BasicNameValuePair("_guid", guid));// 本人
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


	Runnable savegzThread = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int result;
			String target = myapplication.getlocalhost() + "addusergzinfo.php";
			HttpPost httprequest = new HttpPost(target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			paramsList.add(new BasicNameValuePair("_userid", myapplication.getUserId()));// 本人
			paramsList.add(new BasicNameValuePair("_infoid", infoid));// 本人
			paramsList.add(new BasicNameValuePair("_guid", guid));// 本人
			paramsList.add(new BasicNameValuePair("_action", gzaction));// 本人
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

	Handler savehandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			int result = data.getInt("result");
			Log.i("mylog", "请求结果-->" + result);
			if (result == 1) {
				Toast.makeText(ViewActivity.this, "评论发表成功", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(ViewActivity.this, "评论发表失败", Toast.LENGTH_SHORT)
						.show();
			}
		}
	};

	String pos = "";

	@Override
	public void click(View v) {
		// TODO Auto-generated method stub
		// Toast.makeText(this,
		// v.getTag().toString(),Toast.LENGTH_SHORT).show();
		String tag = v.getTag().toString();
		String cat = tag.split("_")[0];
		pos = tag.split("_")[1];
		/*if (cat.startsWith("good")) {
			new Thread(solutionThread).start();
		} else*/ if (cat.startsWith("head")) {// 聊天
			Map<String, String> map = discussList.get(Integer.parseInt(pos));
			String itemuid = map.get("uid").toString();
			if (itemuid.equals(myapplication.getUserId())) {
				Toast.makeText(ViewActivity.this, "不能与本人聊天", Toast.LENGTH_SHORT).show();
			} else {


				Intent Intent1 = new Intent();
				Intent1.setClass(ViewActivity.this, ViewUserInfoActivity.class);
				Bundle arguments = new Bundle();
				arguments.putString("userid",itemuid);
				Intent1.putExtras(arguments);
				startActivity(Intent1);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long id) {
		// TODO Auto-generated method stub
		// Toast.makeText(this, "listview的item被点击了！，点击的位置是-->" +
		// position,Toast.LENGTH_SHORT).show();

	}

	Runnable solutionThread = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int result;
			String target = myapplication.getlocalhost() + "solution.php";
			HttpPost httprequest = new HttpPost(target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			paramsList.add(new BasicNameValuePair("_infoid", infoid));// 信息id
			paramsList.add(new BasicNameValuePair("_guid", guid));// 信息唯一标识
			Map<String, String> map = discussList.get(Integer.parseInt(pos));
			paramsList.add(new BasicNameValuePair("_solutiontype", "1"));// 留言
			paramsList.add(new BasicNameValuePair("_solutionpostion", map.get(
					"id").toString()));// 留言项
			paramsList.add(new BasicNameValuePair("_solutionuserid", map.get(
					"uid").toString()));// 留言人

			SimpleDateFormat sDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			String solutiondate = sDateFormat.format(new java.util.Date());

			paramsList
					.add(new BasicNameValuePair("_solutiontime", solutiondate));// 本人

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
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putInt("result", result);
				msg.setData(data);
				freshhandler.sendMessage(msg);
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	};

	Handler freshhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			int result = data.getInt("result");
			Log.i("mylog", "请求结果-->" + result);
			if (result == 1) {
				discussList.clear();
				MarketAPI.getinfo(getApplicationContext(), ViewActivity.this, guid);
			}
		}
	};

	@Override
	public void onSuccess(int method, Object obj) {
		// TODO Auto-generated method stub
		switch (method) {
        case MarketAPI.ACTION_GETINFO:
        	 try{
                 dismissDialog(DIALOG_PROGRESS);
             }catch (IllegalArgumentException e) {
             }
            HashMap<String, String> result = (HashMap<String, String>) obj;
            String JsonContext=result.get("guidinfo");           
            if (JsonContext == null || JsonContext.equals("")
					|| JsonContext.toString().length() == 0) {
				Message msg = basehandler.obtainMessage();
				msg.what = 1;
				basehandler.sendMessage(msg);
				return;
			} else {
				if (JsonContext.startsWith("Error:")) {
					Message msg = basehandler.obtainMessage();
					msg.what = 2;
					Bundle data = new Bundle();
					data.putString("error", "json");
					msg.setData(data);
					basehandler.sendMessage(msg);
					return;
				}
				JSONArray jsonarray;
				try {
					jsonarray = new JSONArray(JsonContext);
					jsonobject = jsonarray.getJSONObject(0);
					senduserid = jsonobject.getString("senduser");
					infoid = jsonobject.getString("id");
					if (senduserid.equals(myapplication.getUserId())) {
						inforeport_btn.setVisibility (View.GONE);//举报
						evaluate_btn.setVisibility (View.VISIBLE);//我来帮按钮
						btn_gz.setVisibility(View.GONE);
						rl_bottom.setVisibility (View.VISIBLE);//我来帮按钮
						left_chat_btn.setVisibility (View.VISIBLE);//我来帮按钮
						right_chat_btn.setVisibility (View.VISIBLE);//我来帮按钮

					}else
					{
						inforeport_btn.setVisibility (View.VISIBLE);//举报
						evaluate_btn.setVisibility (View.GONE);//我来帮按钮
						btn_gz.setVisibility(View.VISIBLE);
						rl_bottom.setVisibility (View.VISIBLE);//我来帮按钮
						left_chat_btn.setVisibility (View.VISIBLE);//我来帮按钮
						right_chat_btn.setVisibility(View.VISIBLE);//我来帮按钮
						MessGzService messgzService = new MessGzService(ViewActivity.this);
						boolean ishavesave = messgzService.isexit(guid,myapplication.getUserId());
						if(ishavesave) {
							btn_gz.setText("取消收藏");
							btn_gz.setTag("取消收藏");
						}else {
							btn_gz.setText("收藏");
							btn_gz.setTag("收藏");
						}
						MessTsService messtsService = new MessTsService (ViewActivity.this);
						boolean ishavets = messtsService.isexit(guid,myapplication.getUserId());
						if (ishavets) {
							inforeport_btn.setText("取消投诉");
							inforeport_btn.setTag("取消投诉");
						} else {
							inforeport_btn.setText("投诉");
							inforeport_btn.setTag("投诉");
						}
					}
					title = jsonobject.getString("title");
					content = jsonobject.getString("content");
					headface = jsonobject.getString("headface");
					sex = jsonobject.getString("sex");
					sendtime = jsonobject.getString("sendtime");
					infocatagroy = jsonobject.getString("infocatagroy");
					address = jsonobject.getString("address");
					photo = jsonobject.getString("photo");
					current_login_userid = myapplication.getUserId();
					current_login_usernickname = myapplication.getNickname();
					sendusername = jsonobject.getString("username");
					if (jsonobject.getString("status").equals("2")) {
						issolution = true;
						evaluate_btn.setText ("查看帮助我的人");
					} else {
						issolution = false;

					}
					solutionid = jsonobject.getString("solutionid");
					if (sendusername.equals("")) {
						sendusername = "匿名";
					}
					if(sex.equals ("0"))
					{
						sex_img.setImageResource (R.mipmap.xz_nan_icon);
					}else
					{
						sex_img.setImageResource (R.mipmap.xz_nv_icon);
					}
					sendusertv.setText(sendusername);
					contenttv.setText(content);
					sendtimetv.setText(sendtime);
					sendaddresstv.setText(address);
					if (issolution) {
						rl_bottom.setVisibility (View.GONE);
						statusimg.setImageResource (R.mipmap.xz_yijiejue_icon);
					} else {
						rl_bottom.setVisibility (View.VISIBLE);
						statusimg.setImageResource (R.mipmap.xz_qiuzhu_icon);
					}
					if (headface.length() > 0) {
						String headfaceurl = myapplication.getlocalhost() + "/uploads/"+headface;
						ImageLoader.getInstance().displayImage(headfaceurl,headface_img, ImageOptions.getOptions());
					}else
					{
						headface_img.setImageResource(R.mipmap.xz_wo_icon);
					}
					if (photo.indexOf(",") > 0) {
						for (int i = 0; i < photo.split(",").length; i++) {
							String fileName = myapplication.getlocalhost()+"uploads/"
									+ photo.split(",")[i];
							potolist.add(fileName);
						}
					} else {
						if (photo.length() > 0) {
							String fileName = myapplication.getlocalhost()+"uploads/"
									+ photo;
							potolist.add(fileName);
						}
					}

					if (potolist.size() > 0) {
						init();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}								
				new Thread(loaddiscuzzThread).start();
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
	            try{
	                dismissDialog(DIALOG_PROGRESS);
	            }catch (IllegalArgumentException e) {
	            }            		            
	            break;
	        default:
	            break;
	        }
	}


	private void initlsb() {
		// TODO Auto-generated method stub
		mLocationClient = new LocationClient(this.myapplication);
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType("gcj02");// 返回的定位结果是百度经纬度，默认值gcj02
		int span = 1000*20;
		option.setScanSpan(span);// 设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
		mLocationClient.registerLocationListener(new BDLocationListener() {

			@Override
			public void onReceiveLocation(BDLocation location) {
				// TODO Auto-generated method stub
				if (location == null) {
					return;
				}
				nLatitude = location.getLatitude();
				nLontitude = location.getLongitude();
				Log.i("mylog", nLatitude+"-" + nLontitude);
				myapplication.setLat(String.valueOf(nLatitude));
				myapplication.setLng(String.valueOf(nLontitude));
				myapplication.updatelocation();
				mLocationClient.stop();
			}
		});
		mLocationClient.start();
		mLocationClient.requestLocation();
	}



	class LoadLbsThread implements Runnable {
		public void run() {
			Message message = new Message();
			message.what = 1;
			handler.sendMessage(message);
		}
	}

	Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					initlsb();
					break;
			}
			super.handleMessage(msg);
		}

	};

}
