package com.bbxiaoqu.ui;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.R;
import com.bbxiaoqu.comm.adapter.BmUserAdapter;
import com.bbxiaoqu.comm.adapter.BmUserAdapter.Callback;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.StreamTool;
import com.bbxiaoqu.comm.tool.T;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


public class BmUserActivity extends Activity implements  Callback{
	private DemoApplication myapplication;
	ListView lstv;

	private List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
	//Context mContext;
	BmUserAdapter adapter;	
	TextView title;
	TextView right_text;
	 private static final int MESSAGETYPE_01 = 0x0001;
	private ProgressDialog progressDialog = null;
	private String guid = "";
	private boolean isbm = false;

	private RatingBar ratingBar;
	private EditText content;
	private Button submit;
	private ImageButton closebtn;
	//private RelativeLayout id_info;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.activity_bmuser);
		Bundle Bundle1 = this.getIntent().getExtras();
		guid = Bundle1.getString("guid");
		initView();
		myapplication = (DemoApplication) this.getApplication();			
		init();
	}

	private void init() {
		lstv = (ListView) findViewById(R.id.lvbmuser);
		getData() ;
		adapter= new BmUserAdapter(this, dataList, this,this.isbm);
		lstv.setAdapter(adapter);
		lstv.setOnItemClickListener(new AdapterView.OnItemClickListener (){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				// TODO Auto-generated method stub
				//Map<String, Object> map=dataList.get (position);
				if(dataList.get(position).get("type").toString().equals ("pl"))
				{

				}else if(dataList.get(position).get("type").toString().equals ("chat"))
				{
					//contentid
					if(dataList.get(position).get("senduserid").toString().equals (myapplication.getUserId ())) {
						Toast.makeText(BmUserActivity.this, "请选择与其他人聊天", Toast.LENGTH_SHORT).show();
						return;
					}
					Intent intent = new Intent (BmUserActivity.this, ChattingActivity.class);
					Bundle arguments = new Bundle ();
					arguments.putString ("to", dataList.get(position).get("senduserid").toString());
					arguments.putString ("my", myapplication.getUserId ());
					intent.putExtras (arguments);
					startActivity (intent);
				}
			}
		});
	}

	private void initView() {
		TextView PageTitle = (TextView) findViewById(R.id.PageTitle);
		PageTitle.setText ("帮助我的人");
		ImageView right_image;
		right_image = (ImageView) findViewById(R.id.top_menu_right_image);
		right_image.setVisibility(View.VISIBLE);
		right_image.setOnClickListener(new View.OnClickListener () {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(BmUserActivity.this,SearchActivity.class);
				startActivity(intent);
			}
		});
	}


	
	private void getData() {
		if (!NetworkUtils.isNetConnected(myapplication)) {
			T.showShort(myapplication, "当前无网络连接！");
			NetworkUtils.showNoNetWorkDlg(BmUserActivity.this);
			return;
		}
		String target=myapplication.getlocalhost()+"getbmuserlist_v1.php?guid="+guid;
		dataList = new ArrayList<Map<String, Object>>();
	    	try {
	    		List<Map<String, Object>> bfjllist=null;
	    		HttpGet httprequest = new HttpGet(target);
	    		 HttpClient HttpClient1 = new DefaultHttpClient();	    	
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
			int _id = jsonobject.getInt("id");
			String _senduserid = jsonobject.getString("senduserid");
			String _uerid = jsonobject.getString("userid");
			String _username = jsonobject.getString("username");

			String _telphone = jsonobject.getString("telphone");
			String _headface = jsonobject.getString("headface");
			String _sex = jsonobject.getString("sex");

			String _guid = jsonobject.getString("guid");
			String _infoid = jsonobject.getString("infoid");
			String _type = jsonobject.getString("type");
			String _content = jsonobject.getString("content");
			String _contentid = jsonobject.getString("contentid");

			String _status = jsonobject.getString("status");

			//_username=_username+"_"+_status;
			if(this.isbm == false) {
				if (!_status.equals("0")) {//只要其中一个状态不为零,就说明不是报名,就是交易已经完成
					this.isbm = true;
				}
			}
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put("id",_id);
			item.put("uerid",_uerid);
			item.put("senduserid",_senduserid);
			item.put("username",_username);
			item.put("telphone",_telphone);
			item.put("headface",_headface);
			item.put("sex",_sex);

			item.put("guid",_guid);
			item.put("infoid",_infoid);
			item.put("type",_type);
			item.put("content",_content);
			item.put("contentid",_contentid);
			item.put("status",_status);
			list.add(item);
		}
		return list;
	}



		int selpos=-1;
	     public void click(View v) {
	    	 String selpos=v.getTag().toString();
	    	/* String[] arr=tag.split("_");
	    	 selpos=Integer.parseInt(arr[0]);
	    	 String id=arr[1];*/

			 Intent Intent1 = new Intent();
			 Intent1.setClass(BmUserActivity.this, AppraiseActivity.class);
			 Bundle arguments = new Bundle();
			 Map<String, Object> map=dataList.get(Integer.parseInt (selpos));
			 //arguments.putString("userid", map.get("uerid").toString());
			 arguments.putString("id", map.get("id").toString());
			 arguments.putString("guid", map.get("guid").toString());
			 arguments.putString("infoid", map.get("infoid").toString());
			 arguments.putString("username", map.get("username").toString());
			 arguments.putString("headface", map.get("headface").toString());
			 arguments.putString("senduserid", map.get("senduserid").toString());//帮助我的人的ID
			 arguments.putString("type", map.get("type").toString());//帮助的类别
			 arguments.putString("content", map.get("content").toString());//帮助的类别
			 arguments.putString("contentid", map.get("contentid").toString());//帮助的类别
			 Intent1.putExtras(arguments);
			 startActivity(Intent1);

	     }



		
		public void rsload()
		{
			getData() ;
		    adapter= new BmUserAdapter(this, dataList, this,true);
			lstv.setAdapter(adapter);
		}
	    	 
	    	 


	     public void doBack(View view) {
	 		onBackPressed();
	 	}

}

