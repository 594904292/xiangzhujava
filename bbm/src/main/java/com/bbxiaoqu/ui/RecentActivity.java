package com.bbxiaoqu.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.R;
import com.bbxiaoqu.comm.adapter.RecentAdapter;
import com.bbxiaoqu.client.baidu.BbPushMessageReceiver;
import com.bbxiaoqu.comm.service.db.DatabaseHelper;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.T;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class RecentActivity extends Activity{  
	private DemoApplication myapplication;
	private RecentAdapter adapter;
	private List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
	ListView lstv;
	private TextView title;
	private TextView right_text;
	public ImageView top_more;
	private DatabaseHelper dbHelper;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.activity_recent);		
		
		dbHelper = new DatabaseHelper(RecentActivity.this);
		myapplication = (DemoApplication) this.getApplication();
		myapplication.getInstance().addActivity(this);		
		initView() ;
		initData();
		loadData();
	}
	
	
	
	private void initView() {
		title = (TextView)findViewById(R.id.title);
		right_text = (TextView)findViewById(R.id.right_text);
		right_text.setVisibility(View.VISIBLE);
		right_text.setClickable(true);		
		top_more = (ImageView) findViewById(R.id.top_more);	
		top_more.setVisibility(View.VISIBLE);
		top_more.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(RecentActivity.this,SearchActivity.class);									
				startActivity(intent);
				
				
			}
		});
	}

	private void initData() {
		title.setText("会话列表");
		right_text.setText("");
		if (!NetworkUtils.isNetConnected(myapplication)) {
			NetworkUtils.showNoNetWorkDlg(RecentActivity.this);
			T.showShort(myapplication, "当前无网络连接,请稍后再试！");
			return;
		}
	}	

	
	private void loadData() {
		// TODO Auto-generated method stub
		lstv = (ListView) findViewById(R.id.recentlv);
		lstv.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int location, long arg3) {								
					//更新用户未读数
					String userid=dataList.get(location).get("userid").toString();
					SQLiteDatabase sdb = dbHelper.getReadableDatabase();
					String sql = "update friend set messnum=0 where userid='"+userid+"'";
					sdb.execSQL(sql);
					//变为全读
					String sql1 = "update chat set readed=1 where senduserid='"+userid+"' or touserid='"+userid+"'";
					sdb.execSQL(sql1);
					//通知主界面,刷新未读数
					
					Message msg = new Message();
					msg.what=1;
					loadhandler.sendMessage(msg);	
					
					//提取用户id
					Intent intent = new Intent(RecentActivity.this,ChattingActivity.class);					
					Bundle arguments = new Bundle();
					arguments.putString("to", userid);
					arguments.putString("my",myapplication.getUserId());
					intent.putExtras(arguments);					
					startActivity(intent);
					
					
			}
		}); 		
	    if (lstv == null)
        {
          return;
        }
	    showlist();
	}


	 Handler loadhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			showlist();
			 for (int i = 0; i < BbPushMessageReceiver.msgReadListeners.size(); i++)
				 BbPushMessageReceiver.msgReadListeners.get(i).onReadMessage();			
		}
	};

	private void showlist() {
		getData() ;  
	    adapter = new RecentAdapter(RecentActivity.this, dataList);
	    lstv.setAdapter(adapter);
	}
	
	
private void getData() {	
		dataList.clear();
		SQLiteDatabase sdb = dbHelper.getReadableDatabase();
		String sql = "";
		//
		sql = "select userid,nickname,usericon,lastinfo,lasttime,messnum,lastnickname from friend order by lasttime desc";
		Cursor c = sdb.rawQuery(sql, null);
		while (c.moveToNext()) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put("userid", String.valueOf(c.getString(0)));
			item.put("username", String.valueOf(c.getString(1)));
			item.put("usericon", String.valueOf(c.getString(2)));
			item.put("lastinfo", String.valueOf(c.getString(3)));
			item.put("lastchattimer", String.valueOf(c.getString(4)));
			item.put("messnum", String.valueOf(c.getString(5)));
			item.put("lastnickname", String.valueOf(c.getString(6)));
			dataList.add(item);
		}
		c.close();
		sdb.close();

	}

public void doBack(View view) {
	onBackPressed();
}


	
}  