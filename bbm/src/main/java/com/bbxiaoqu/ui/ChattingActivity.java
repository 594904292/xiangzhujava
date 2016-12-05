package com.bbxiaoqu.ui;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.R;
import com.bbxiaoqu.comm.adapter.ChatMessageAdapter;
import com.bbxiaoqu.comm.bean.BbMessage;
import com.bbxiaoqu.comm.bean.ChatMessage;
import com.bbxiaoqu.comm.bean.User;
import com.bbxiaoqu.client.baidu.BbPushMessageReceiver;
import com.bbxiaoqu.client.baidu.BbPushMessageReceiver.onChatMessageListener;
import com.bbxiaoqu.comm.jsonservices.GetJson;
import com.bbxiaoqu.comm.service.db.ChatDB;
import com.bbxiaoqu.comm.service.db.DatabaseHelper;
import com.bbxiaoqu.comm.service.db.FriendDB;
import com.bbxiaoqu.comm.service.db.NoticeDB;
import com.bbxiaoqu.comm.tool.CustomerHttpClient;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.SharePreferenceUtil;
import com.bbxiaoqu.comm.tool.StreamTool;
import com.bbxiaoqu.comm.tool.T;
import com.google.gson.Gson;


public class ChattingActivity extends Activity implements onChatMessageListener
{

	
	private String infoid = "0";
	private String  content= "";
	private String senduserid = "";
	private String sendusername = "";
	private boolean issolution = false;//是否解决
	
	private TextView mNickName;
	private EditText mMsgInput;
	private Button mMsgSend;
	
/*	private RelativeLayout ly_chat_top;
	private Button topbtn;
	private TextView event_name;*/
	private ListView mChatMessagesListView;
	private List<ChatMessage> mDatas = new ArrayList<ChatMessage>();
	private ChatMessageAdapter mAdapter;
	private DemoApplication myapplication;

	private User mFromUser;	
	private Gson mGson;
	private SharePreferenceUtil mSpUtil;
	private String from;//对方
	private String myself;//本人
	
	private String msg;
	

	
	String fromusername = "";
	String fromheadface = "";
	
	Map<String, Map> UserMap = new HashMap<String, Map>();
	FriendDB db=new FriendDB(ChattingActivity.this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_chatting);
		BbPushMessageReceiver.chatmsgListeners.add(this);//新聊天记录
		myapplication = (DemoApplication) this.getApplication();
		Bundle Bundle1 = this.getIntent().getExtras();		
		from = Bundle1.getString("to");
		myself = Bundle1.getString("my");
		if(from==null||from.trim().length()<1)
		{
			T.showShort(myapplication, "获取不到对方参数！");
			return;
		}
		if(myself==null||myself.trim().length()<1)
		{
			T.showShort(myapplication, "获取不到自己的用户参数！");
			return;
		}
		if (!NetworkUtils.isNetConnected(myapplication)) {
			NetworkUtils.showNoNetWorkDlg(ChattingActivity.this);
			T.showShort(myapplication, "当前无网络连接,请稍后再试！");
			return;
		}
		db.updateuserheadface(myself, myapplication.getheadface());
		Map mymap=new HashMap();
		mymap.put("headface", myapplication.getheadface());
		mymap.put("username", myapplication.getNickname());
		UserMap.put(myself, mymap);
		if(Bundle1.getString("guid")!=null)
		{
			guid = Bundle1.getString("guid");				
		}else
		{
			
		}

		new Thread(loaduserinfo).start();
		//new Thread(ajaxloadinfo).start();

		new Thread(xmpprunnable).start();
		//清掉通知里的消息
		ChatDB cdb=new ChatDB(ChattingActivity.this);
		cdb.readchat(from, myself);//标记已读
		
		NoticeDB ndb=new NoticeDB(ChattingActivity.this);
		ndb.delnotice(from, "私信");//从通知栏删除
	}

	Runnable xmpprunnable = new Runnable(){
		@Override
		public void run() {
			// TODO: http request.
			Message msg = new Message();
			msg.what=1;
			handler.sendMessage(msg);
		}
	};

	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what==1) {
				if (!NetworkUtils.isNetConnected(myapplication)) {
					T.showShort(myapplication, "当前无网络连接！");
					NetworkUtils.showNoNetWorkDlg(ChattingActivity.this);
					return;
				}
				myapplication.getInstance().startxmpp();
			}
		}
	};


	private JSONObject jsonobject;//通过GUID获取的消息

	
	Runnable loaduserinfo = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int result;
			String target = myapplication.getlocalhost()+"getuserinfo.php?userid="+ from;
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
						fromusername = jsonobject.getString("username");
						fromheadface = jsonobject.getString("headface");
						
						
						 
						db.updatenickname(from, fromusername);
						db.updateuserheadface(from, fromheadface);
						
						Map tomap=new HashMap();
						tomap.put("headface", fromheadface);
						tomap.put("username", fromusername);
						UserMap.put(from, tomap);
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println(json);
					result = 1;
				} else {
					result = 0;
				}				
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Message msg = new Message();
			
			changehandler.sendMessage(msg);

		}
	};
	
	Handler changehandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			initView();
			initEvent();
			TextView PageTitle = (TextView) findViewById(R.id.PageTitle);
			PageTitle.setText (fromusername);
			
		}
	};


	public void doBack(View view) {
		onBackPressed();
	}
	
	private void initView()
	{

		TextView PageTitle = (TextView) findViewById(R.id.PageTitle);
		PageTitle.setText (from);

		ImageView right_image;
		right_image = (ImageView) findViewById(R.id.top_menu_right_image);
		right_image.setVisibility(View.VISIBLE);
		right_image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent (ChattingActivity.this,SearchActivity.class);
				startActivity(intent);
			}
		});
		mChatMessagesListView = (ListView) findViewById(R.id.id_chat_listView);
		mMsgInput = (EditText) findViewById(R.id.id_chat_msg);
		mMsgSend = (Button) findViewById(R.id.id_chat_send);

	
		mDatas = getdata();
		mAdapter = new ChatMessageAdapter(this, mDatas);
		mChatMessagesListView.setAdapter(mAdapter);
		mChatMessagesListView.setSelection(mDatas.size() - 1);		
	}
	
	
	public List<ChatMessage>  getdata()
	{	
		List<ChatMessage> chatMessages = new ArrayList<ChatMessage>();
		DatabaseHelper dbHelper=new DatabaseHelper(this);
		SQLiteDatabase sdb = dbHelper.getReadableDatabase();
		//senduserid,touserid,guid,message,date,readed
		String sql = "select * from  [chat] where (senduserid ='"+from+"' and touserid='"+myself+"') or (senduserid ='"+myself+"' and touserid='"+from+"') ";
			Cursor c = sdb.rawQuery(sql, null);
			while (c.moveToNext()) {				
				//Message message = new Message(to,to,System.currentTimeMillis(), str);
				ChatMessage chatMessage = new ChatMessage();
				
				if(myself.equals(c.getString(2)))
				{
					chatMessage.setComing(false);
				}else
				{
					chatMessage.setComing(true);
				}
				 SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm");
				try {
					chatMessage.setDate(df.parse(c.getString(5).toString()));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				chatMessage.setSenduserId(c.getString(2));
				if(c.getString(2).equals(myself))
				{
					String myselfname="-";
					if(UserMap.get(myself).get("username")!=null)
					{
						myselfname=UserMap.get(myself).get("username").toString();
					}
					String myselfheadface="";
					if(UserMap.get(myself).get("headface")!=null)
					{
						myselfheadface=UserMap.get(myself).get("headface").toString();
					}
					chatMessage.setSendnickname(myselfname);
					chatMessage.setSenduserIcon(myselfheadface);
				}else
				{
					String fromname="-";
					if(UserMap.get(from).get("username")!=null)
					{
						fromname=UserMap.get(from).get("username").toString();
					}
					String fromheadface="";
					if(UserMap.get(myself).get("headface")!=null)
					{
						fromheadface=UserMap.get(from).get("headface").toString();
					}
					chatMessage.setSendnickname(fromname);
					chatMessage.setSenduserIcon(fromheadface);
				}			
				chatMessage.setMessage(c.getString(4));
				chatMessage.setReaded(true);
				chatMessages.add(chatMessage);								
			}
			c.close();
			sdb.close();
			return chatMessages;
	}

	private void initEvent()
	{
		mMsgSend.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				msg = mMsgInput.getText().toString();
				if (TextUtils.isEmpty(msg))
				{
					T.showShort(myapplication, "您还未填写消息呢!");

					return;
				}

				if (!NetworkUtils.isNetConnected(myapplication))
				{
					T.showShort(myapplication, "当前无网络连接！");
					NetworkUtils.showNoNetWorkDlg(ChattingActivity.this);
					return;
				}
				
				String guid=UUID.randomUUID().toString();
				ChatMessage chatMessage = new ChatMessage();
				chatMessage.setComing(false);
				chatMessage.setSenduserId(myself);
				chatMessage.setTouserId(from);
				
				
				chatMessage.setSendnickname(UserMap.get(myself).get("username").toString());
				chatMessage.setSenduserIcon(UserMap.get(myself).get("headface").toString());					

				Date d=new Date();
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				chatMessage.setGuid(guid);
				chatMessage.setMessage(msg);
				chatMessage.setDate(new Date());
				chatMessage.setDateStr(df.format(d));
				mDatas.add(chatMessage);
				 ChatDB db=new ChatDB(myapplication);
			     if(!db.isexit(chatMessage.getGuid()))
			     {
					 if(chatMessage.getSenduserId().equals(myself)) {
						 db.add(chatMessage,true);
					 }else
					 {
						 db.add(chatMessage,false);
					 }
			     }
			     FriendDB fb=new FriendDB(myapplication);				    
				 if(!fb.isexit(from))
				 {//添加朋友联系人
				  	fb.add(from, fromusername, fromheadface,myself,myapplication.getNickname(), msg, df.format(d), 0);
				 }
				  fb.newmess(from, myself,msg, df.format(new Date()));
				  
				mAdapter.notifyDataSetChanged();
				mChatMessagesListView.setSelection(mDatas.size() - 1);
				mMsgInput.setText("");
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				//得到InputMethodManager的实例
				if (imm.isActive())
				{
					// 如果开启,关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
					imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,InputMethodManager.HIDE_NOT_ALWAYS);
				}
				new Thread(savechatThread).start();//先发送到服务器
			}
		});
	}
	

	 
	String guid="";
	Runnable savechatThread = new Runnable() {
		@Override
		public void run() {
//			if(myself.equals(DemoApplication.getInstance().getUserId()))
//			{//本人	
//				sendxmpp(from, msg);
//			}else
//			{
//				sendxmpp(myself, msg);
//			}
			int result;
			String target = myapplication.getlocalhost()+"chat.php";
			HttpPost httprequest = new HttpPost(target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = sDateFormat.format(new java.util.Date());

			paramsList.add(new BasicNameValuePair("_catatory", "chat"));// 本人
			paramsList.add(new BasicNameValuePair("_senduserid", myself));// 本人
			paramsList.add(new BasicNameValuePair("_sendnickname", myself));// 本人
			paramsList.add(new BasicNameValuePair("_sendusericon", myself));// 本人
			paramsList.add(new BasicNameValuePair("_touserid", from));// 对方
			paramsList.add(new BasicNameValuePair("_tonickname", from));//对方
			paramsList.add(new BasicNameValuePair("_tousericon", from));//对方
			paramsList.add(new BasicNameValuePair("_guid", guid));//消息
			paramsList.add(new BasicNameValuePair("_message", msg));//消息
			paramsList.add(new BasicNameValuePair("_channelid", ""));// 通道
			try {
				httprequest.setEntity(new UrlEncodedFormEntity(paramsList,
						"UTF-8"));
				HttpClient HttpClient1 = CustomerHttpClient.getHttpClient();
				HttpResponse httpResponse = HttpClient1.execute(httprequest);
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					String json = EntityUtils
							.toString(httpResponse.getEntity());
					result = 1;
					//
				} else {
					result = 0;
				}
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putInt("result", result);
				msg.setData(data);
				savehandler.sendMessage(msg);
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// 发送到服务器

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
				Toast.makeText(ChattingActivity.this, "已发送", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(ChattingActivity.this, "发送失败", Toast.LENGTH_SHORT)
						.show();
			}
		}
	};
	
	@Override
	protected void onDestroy()
	{
		BbPushMessageReceiver.chatmsgListeners.remove(this);
		super.onDestroy();

	}

	Handler newmesshandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			mDatas = getdata();
			mAdapter = new ChatMessageAdapter(ChattingActivity.this, mDatas);
			mChatMessagesListView.setAdapter(mAdapter);
			if(mDatas!=null&&mDatas.size()>0)
			{
				mChatMessagesListView.setSelection(mDatas.size() - 1);
			}

		}
	};

	public void onChatMessage(BbMessage message)
	{//强制刷新就OK
		
		Message msg = new Message();
		newmesshandler.sendMessage(msg);
		
		
	}

}
