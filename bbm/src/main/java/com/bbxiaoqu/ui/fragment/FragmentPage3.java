package com.bbxiaoqu.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.ImageOptions;
import com.bbxiaoqu.R;
import com.bbxiaoqu.api.MarketAPI;
import com.bbxiaoqu.comm.adapter.ReportsAdapter;
import com.bbxiaoqu.comm.service.db.DatabaseHelper;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.StreamTool;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.comm.widget.RoundAngleImageView;
import com.bbxiaoqu.ui.LoadingView;
import com.bbxiaoqu.ui.ViewUserInfoActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.bbxiaoqu.api.ApiAsyncTask.ApiRequestListener;
import com.bbxiaoqu.ui.LoadingView.OnRefreshListener;
public class FragmentPage3 extends Fragment implements  ApiRequestListener,OnRefreshListener{
	private DemoApplication myapplication;
	private ReportsAdapter adapter;
	private List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
	ListView lstv;
	RoundAngleImageView myheadface;
	ImageView mysex;
	TextView myusername;
	private TextView txt_order;
	private TextView txt_order_desc;
	private DatabaseHelper dbHelper;
	private View view;
	String order="num";
	TextView tv_order_score;
	TextView tv_order_num;
	private Activity activity;
	LoadingView mLoadView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		dbHelper = new DatabaseHelper(this.getActivity());
		myapplication = (DemoApplication) this.getActivity().getApplication();
		activity=this.getActivity();
		view= inflater.inflate(R.layout.fragment_3, null);
		mLoadView = (LoadingView) view.findViewById (R.id.loading_view);
		mLoadView.setRefrechListener(this);
		myheadface= (RoundAngleImageView) view.findViewById(R.id.myheadface);
		mysex= (ImageView) view.findViewById(R.id.mysex);
		myusername= (TextView) view.findViewById(R.id.myusername);
		tv_order_score = (TextView) view.findViewById(R.id.tv_2);
		tv_order_num  = (TextView) view.findViewById(R.id.tv_3);
		tv_order_score.setOnClickListener(new View.OnClickListener () {
			@Override
			public void onClick(View arg0) {
				order="score";
				LoadData();
			}
		});
		tv_order_num.setOnClickListener(new View.OnClickListener () {
			@Override
			public void onClick(View arg0) {
				order="num";
				LoadData();
			}
		});
		txt_order = (TextView) view.findViewById(R.id.order);
		txt_order_desc = (TextView) view.findViewById(R.id.txt_order_desc);
		lstv = (ListView) view.findViewById(R.id.orderlv);
		lstv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
									int location, long arg3) {
				Intent Intent1 = new Intent();
				Intent1.setClass(myapplication, ViewUserInfoActivity.class);
				Bundle arguments = new Bundle();
				arguments.putString("userid",dataList.get(location).get("username").toString());
				Intent1.putExtras(arguments);
				startActivity(Intent1);
			}
		});
		MarketAPI.getMyrank(myapplication, this,myapplication.getUserId());
		LoadData();
		return view;
	}



	Handler refreshhandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 11:
					adapter= new ReportsAdapter(myapplication,activity,dataList);
					lstv.setAdapter(adapter);
					if(order.equals ("num"))
					{
						tv_order_num.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
						tv_order_score.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
					}else
					{
						tv_order_num.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
						tv_order_score.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
					}
					break;
			}
		}
	};
	Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					Bundle data = msg.getData();
					String action = data.getString("action");
					String username = data.getString("username");
					String headface = data.getString("headface");
					String sex = data.getString("sex");
					String pos = data.getString("pos");
					myusername.setText(username);
					if(headface!=null&&headface.length ()>0) {
						String headfaceurl = "https://api.bbxiaoqu.com/uploads/" + headface;
						ImageLoader.getInstance ().displayImage (headfaceurl, myheadface, ImageOptions.getOptions ());
					}
					if(sex.equals ("0")) {
						mysex.setImageResource (R.mipmap.xz_nan_icon);
					}else
					{
						mysex.setImageResource (R.mipmap.xz_nv_icon);
					}
					txt_order.setText(String.valueOf(pos));
					txt_order_desc.setText("你排名第"+pos+"位");
					break;
			}
			super.handleMessage(msg);
		}
	};


	private void LoadData() {
		if(this.order.equals ("num"))
		{
			MarketAPI.getRANK(myapplication, this,"num");
		}else
		{
			MarketAPI.getRANK(myapplication, this,"score");
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
			int order = jsonobject.getInt("order");
			String username = jsonobject.getString("username");
			String nickname = jsonobject.getString("nickname");
			String score = jsonobject.getString("score");
			String nums = jsonobject.getString("nums");
			String headface = jsonobject.getString("headface");
			String sex  = jsonobject.getString("sex");
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put("order", order);
			item.put("username",username);
			item.put("nickname",nickname);
			item.put("score", score);
			item.put("nums", nums);
			item.put("headface", headface);
			item.put("sex", sex);
			list.add(item);
		}
		return list;
	}


	public void onSuccess(int method, Object obj) {
		switch (method) {
			case MarketAPI.ACTION_MYRANK:
				HashMap<String, String> mresult = (HashMap<String, String>) obj;
				String json=mresult.get("result");
				JSONObject jsonobject = null;
				try {
					jsonobject=new JSONObject(json);
					Message message = new Message();
					message.what = 1;
					message.obj=json;
					Bundle bundledata = new Bundle ();
					bundledata.putString ("userid", jsonobject.getString ("userid"));
					bundledata.putString ("username", jsonobject.getString ("username"));
					bundledata.putString ("headface", jsonobject.getString ("headface"));
					bundledata.putString ("sex", jsonobject.getString ("sex"));
					bundledata.putString ("pos", jsonobject.getString ("pos"));
					message.setData (bundledata);
					handler.sendMessage(message);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case MarketAPI.ACTION_RANK:
				dataList = new ArrayList<Map<String, Object>>();
				HashMap<String, String> result = (HashMap<String, String>) obj;
				String JsonContext=result.get("result");
				List<Map<String, Object>> bfjllist=null;
				try {
					InputStream ajson = new ByteArrayInputStream(JsonContext.getBytes());
					bfjllist= parsejson(ajson);
					for (Map map: bfjllist) {
						dataList.add(map);
					}
					adapter= new ReportsAdapter(myapplication,this.getActivity(), dataList);
					lstv.setAdapter(adapter);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				Message message = new Message();
				message.what = 11;

				refreshhandler.sendMessage(message);
				break;
			default:
				break;
		}
	}


	@Override
	public void onError(int method, int statusCode) {
		switch (method) {
			case MarketAPI.ACTION_MYRANK:
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						mLoadView.setStatue(LoadingView.NO_NETWORK);
					}
				}, 5 * 1000);
				break;
			case MarketAPI.ACTION_RANK:
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
				//MarketAPI.getUserInfo(myapplication,this,myapplication.getUserId());
			}
		}, 1 * 1000);
	}

}