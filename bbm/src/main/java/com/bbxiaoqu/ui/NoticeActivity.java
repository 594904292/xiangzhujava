package com.bbxiaoqu.ui;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.R;
import com.bbxiaoqu.comm.service.db.NoticeDB;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.StreamTool;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.comm.view.BaseActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

public class NoticeActivity extends BaseActivity {
	TextView back;
	TextView title;
	TextView right_text;
	private DemoApplication myapplication;
	NoticeDB db=new NoticeDB(this);
	ListView lstv;

	private List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
	//Context mContext;
	NoticeAdapter adapter;
	private ImageView right_image;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notice);	
		initView();
		myapplication = (DemoApplication) this.getApplication();
		myapplication.getInstance().addActivity(this);
		lstv = (ListView) findViewById(R.id.lvnotice);
		lstv.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int location, long arg3) {
				if(dataList.get(location).get("catagory").toString().equals("评论"))
				{
					Intent Intent1 = new Intent();
					Intent1.setClass(NoticeActivity.this, ViewActivity.class);
					Bundle arguments = new Bundle();
					arguments.putString("put", "false");
					arguments.putString("infoid",dataList.get(location).get("relation").toString());
					Intent1.putExtras(arguments);
					startActivity(Intent1);
				}
			/*	//T.showShort(NoticeActivity.this, dataList.get(location).get("relativeid").toString());
				if(dataList.get(location).get("catagory").toString().equals("评论"))
				{
					rnotice(dataList.get(location).get("relativeid").toString());
					Intent Intent1 = new Intent();
					Intent1.setClass(NoticeActivity.this, ViewActivity.class);
					Bundle arguments = new Bundle();
					arguments.putString("put", "false");
					arguments.putString("guid",dataList.get(location).get("relativeid").toString());
					Intent1.putExtras(arguments);
					startActivity(Intent1);
				}else if(dataList.get(location).get("catagory").toString().equals("消息"))
				{
					rnotice(dataList.get(location).get("relativeid").toString());
					Intent Intent1 = new Intent();
					Intent1.setClass(NoticeActivity.this, ViewActivity.class);
					Bundle arguments = new Bundle();
					arguments.putString("put", "false");
					arguments.putString("guid",dataList.get(location).get("relativeid").toString());
					Intent1.putExtras(arguments);
					startActivity(Intent1);
				}else if(dataList.get(location).get("catagory").toString().equals("服务"))
				{
					rnotice(dataList.get(location).get("relativeid").toString());
					Intent Intent1 = new Intent();
					Intent1.setClass(NoticeActivity.this, ViewFwActivity.class);
					Bundle arguments = new Bundle();
					arguments.putString("put", "false");
					arguments.putString("guid",dataList.get(location).get("relativeid").toString());
					Intent1.putExtras(arguments);
					startActivity(Intent1);
				}else if(dataList.get(location).get("catagory").toString().equals("私信"))
				{
					rnotice(dataList.get(location).get("relativeid").toString());
					Intent intent = new Intent(NoticeActivity.this,ChattingActivity.class);
					
					Bundle arguments = new Bundle();
					arguments.putString("to", dataList.get(location).get("relativeid").toString());
					arguments.putString("my",myapplication.getUserId());
					intent.putExtras(arguments);					
					startActivity(intent);	
					
				}
				else if(dataList.get(location).get("catagory").toString().equals("订单"))
				{
					rnotice(dataList.get(location).get("relativeid").toString());
					Intent intent = new Intent(NoticeActivity.this,ChattingActivity.class);
					System.out.println("订单跳到对应的guidViewActivity");
					
					Intent Intent1 = new Intent();
					Intent1.setClass(NoticeActivity.this, ViewActivity.class);
					Bundle arguments = new Bundle();
					arguments.putString("put", "false");
					arguments.putString("guid",dataList.get(location).get("relativeid").toString());
					Intent1.putExtras(arguments);
					startActivity(Intent1);

 				}*/
			}
		});

		loadlist();
	}

	private void initView()
	{
		TextView PageTitle = (TextView) findViewById(R.id.PageTitle);
		PageTitle.setText ("消息中心");

		right_image = (ImageView) findViewById(R.id.top_menu_right_image);
		right_image.setVisibility(View.VISIBLE);
		right_image.setOnClickListener(new View.OnClickListener () {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(NoticeActivity.this,SearchActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();	
		finish();
	}


	private void loadlist() {
		dataList = new ArrayList<Map<String, Object>>();
		if (lstv == null) {
			return;
		}
		getData();
		adapter = new NoticeAdapter(this.getApplicationContext());
		lstv.setAdapter(adapter);
	}

	private void getData() {
		if (!NetworkUtils.isNetConnected(myapplication)) {
			T.showShort(myapplication, "当前无网络连接！");
			NetworkUtils.showNoNetWorkDlg(NoticeActivity.this);
			return;
		}
		String target=myapplication.getlocalhost()+"getnotices.php?userid="+myapplication.getUserId ();
		dataList = new ArrayList<Map<String, Object>>();
		try {
			List<Map<String, Object>> bfjllist=null;
			HttpGet httprequest = new HttpGet(target);
			HttpClient HttpClient1 = new DefaultHttpClient ();
			HttpResponse httpResponse = null;
			try {
				httpResponse = HttpClient1.execute(httprequest);
			} catch (ClientProtocolException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				InputStream json = null;
				try {
					json = httpResponse.getEntity().getContent();
					bfjllist= parsejson(json);
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			for (Map map: bfjllist) {
				dataList.add(map);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private List<Map<String, Object>> parsejson(InputStream jsonStream)
			throws Exception {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		byte[] data = StreamTool.read(jsonStream);
		String json = new String(data);
		JSONArray jsonarray = new JSONArray(json);
		for (int i = 0; i < jsonarray.length(); i++) {
			JSONObject jsonobject = jsonarray.getJSONObject(i);
			int id = jsonobject.getInt("id");
			String notictime = jsonobject.getString("notictime");
			String catagory = jsonobject.getString("catagory");
			if(catagory.equals ("pl"))
			{
				catagory="评论";
			}else if(catagory.equals ("xmpp"))
			{
				catagory="私信";
			}else if(catagory.equals ("sys"))
			{
				catagory="系统消息";
			}
			String senduserid  = jsonobject.getString("senduser");
			String username  = jsonobject.getString("username");
			String content = jsonobject.getString("content");
			String relation = jsonobject.getString("relation");
			String readed = jsonobject.getString("readed");

			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put("id",id);
			item.put("date",notictime);
			item.put("catagory",catagory);
			item.put("senduser",senduserid);
			item.put("username",username);
			item.put("relation",relation);
			item.put("content",content);
			item.put("readed",readed);
			list.add(item);
		}
		return list;
	}
	
	
	
	
	



	
	 public final class ViewHolder{

			TextView catagory;
		 	TextView date;
			TextView content;
			
		}
		
	    public class NoticeAdapter extends BaseAdapter{
	    	 private Context ctx;  
		    	private LayoutInflater mInflater;
				
				
				public NoticeAdapter(Context context){
					this.ctx = context;
					this.mInflater = LayoutInflater.from(context);
				}
				
				@Override
				public int getCount() {
					// TODO Auto-generated method stub
					return dataList == null ? 0 : dataList.size();  
				}

				@Override
				public Object getItem(int position) {
					// TODO Auto-generated method stub
					return dataList.get(position); 
				}

				@Override
				public long getItemId(int position) {
					// TODO Auto-generated method stub
					return position;
				}


				
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					// TODO Auto-generated method stub
					ViewHolder holder = null;
					if (convertView == null) {				
						holder=new ViewHolder();  
						convertView = mInflater.inflate(R.layout.list_item_notice, null);
						holder.date = (TextView) convertView.findViewById(R.id.notice_time);
						holder.catagory = (TextView) convertView.findViewById(R.id.notice_catagory);
						holder.content = (TextView) convertView.findViewById(R.id.notice_content);						
						convertView.setTag(holder);  
					}else {				
						holder = (ViewHolder)convertView.getTag();
					}					
					holder.date.setText(dataList.get(position).get("date").toString());
					holder.catagory.setText(dataList.get(position).get("catagory").toString());
					holder.content.setText(dataList.get(position).get("content").toString());					
					return convertView;
				}
	    }
	

}
