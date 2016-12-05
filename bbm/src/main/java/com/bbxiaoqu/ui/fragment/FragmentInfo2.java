package com.bbxiaoqu.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.R;
import com.bbxiaoqu.Session;
import com.bbxiaoqu.comm.adapter.NewListViewAdapter;
import com.bbxiaoqu.api.ApiAsyncTask;
import com.bbxiaoqu.api.MarketAPI;
import com.bbxiaoqu.comm.tool.CustomerHttpClient;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.ui.ViewActivity;
import com.bbxiaoqu.comm.widget.AutoListView;
import com.bbxiaoqu.comm.widget.NewAutoListView;

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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentInfo2 extends Fragment  implements NewListViewAdapter.Callback,NewAutoListView.OnRefreshListener, NewAutoListView.OnLoadListener,ApiAsyncTask.ApiRequestListener {
	private DemoApplication myapplication;
	private View view;
	private NewAutoListView lstv;
	protected Session mSession;

	private List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>> ();
	private BaseAdapter adapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		view= inflater.inflate(R.layout.fragment_info_2, null);
		myapplication = (DemoApplication) this.getActivity().getApplication();
		mSession = Session.get(myapplication);
		lstv = (NewAutoListView) view.findViewById(R.id.infolstv_2);
		adapter = new NewListViewAdapter (this.getActivity(), dataList,this);
		lstv.setAdapter(adapter);
		lstv.setDividerHeight (30);
		lstv.setOnRefreshListener(this);
		lstv.setOnLoadListener(this);
		lstv.setOnItemClickListener(new AdapterView.OnItemClickListener () {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int location, long arg3) {
				if (!NetworkUtils.isNetConnected(myapplication)) {
					T.showShort(myapplication, "当前无网络连接！");
					NetworkUtils.showNoNetWorkDlg(myapplication);
					return;
				}

				Intent Intent1 = new Intent();
				Intent1.setClass(myapplication, ViewActivity.class);
				Bundle arguments = new Bundle();
				arguments.putString("put", "false");
				arguments.putString("guid", dataList.get(location - 1).get("guid").toString());
				Intent1.putExtras(arguments);
				startActivity(Intent1);
			}
		});
		loadData(AutoListView.REFRESH);
		return view;
	}

	int action = 0;
	private int current_sel = 0;
	private void loadData(final int what) {
		if (what == AutoListView.REFRESH) {
			action = AutoListView.REFRESH;
			int start = 0;
			int limit = 10;
			MarketAPI.getINfos(myapplication, this, myapplication.getUserId(), myapplication.getLat(), myapplication.getLng(), "xiaoqu", mSession.getRang(),mSession.getXiaoquid(),"1", start, limit);
		} else {
			action = AutoListView.LOAD;
			int start = dataList.size ();
			int limit = 10;
			MarketAPI.getINfos(myapplication, this, myapplication.getUserId(), myapplication.getLat(), myapplication.getLng(), "xiaoqu",mSession.getRang(),mSession.getXiaoquid(), "1", start, limit);
		}
	}

	public String gethour(String sendtime)
	{
		DateFormat df = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
		String result="";
		try
		{
			Date d1 = new Date ();
			Date d2 = df.parse(sendtime);

			long diff = d1.getTime() - d2.getTime();//这样得到的差值是微秒级别
			long days = diff / (1000 * 60 * 60 * 24);
			long hours = (diff-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60);
			long minutes = (diff-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60))/(1000* 60);
			//System.out.println(""+days+"天"+hours+"小时"+minutes+"分");
			if(days>0)
			{
				result= String.valueOf (days)+"天前";
			}else if(hours>0)
			{
				result= String.valueOf (hours)+"小时前";
			}else if(minutes>0)
			{
				result= String.valueOf (minutes)+"分钟前";
			}

		}
		catch (Exception e)
		{
		}
		return result;
	}

	@Override
	public void onSuccess(int method, Object obj) {
		// TODO Auto-generated method stub
		List<Map<String, Object>> smalldataList = new ArrayList<Map<String, Object>>();
		HashMap<String, String> result = (HashMap<String, String>) obj;
		String json = result.get("infos");
		switch (method) {
			case MarketAPI.ACTION_GETINFOS:
				if (json.length() > 0  && !json.equals("[]")) {
					JSONArray jsonarray = null;
					try {
						jsonarray = new JSONArray(json);
						for (int i = 0; i < jsonarray.length(); i++) {
							JSONObject customJson = jsonarray.getJSONObject(i);
							HashMap<String, Object> item = new HashMap<String, Object>();
							double len = InfoUtil.getDistance(
									Double.parseDouble(myapplication.getLat()),
									Double.parseDouble(myapplication.getLng()),
									Double.parseDouble(customJson.getString("lat").toString()),
									Double.parseDouble(customJson.getString("lng").toString())
							);
							String len1 = "";
							if (len > 1000) {
								len1 = String.valueOf(Math.round(len / 100d) / 10d) + "千米";
							} else {
								len1 = String.valueOf(len) + "米";
							}
							item.put("senduserid", String.valueOf(customJson.getString("senduser").toString()));
							item.put("sendnickname", String.valueOf(customJson.getString("username").toString()));
							item.put("headface", String.valueOf(customJson.getString("headface").toString()));
							item.put("sex", String.valueOf(customJson.getString("sex").toString()));
							item.put("community", String.valueOf(customJson.getString("community").toString()));
							item.put("city", String.valueOf(customJson.getString("city").toString()));
							item.put("street", String.valueOf(customJson.getString("street").toString()));
							item.put("address", String.valueOf(customJson.getString("address").toString()));
							item.put("distance", String.valueOf(len1));
							item.put("lng", String.valueOf(customJson.getString("lng").toString()));
							item.put("lat", String.valueOf(customJson.getString("lat").toString()));
							item.put("guid", String.valueOf(customJson.getString("guid").toString()));
							item.put("infocatagroy", String.valueOf(customJson.getString("infocatagroy").toString()));
							String content = customJson.getString("content").toString();
							if (content.length() > 80) {
								content = content.substring(0, 80) + "...";
							}
							item.put("content", content);
							item.put("icon", String.valueOf(customJson.getString("photo").toString()));
							item.put("date", gethour(String.valueOf(customJson.getString("sendtime").toString())));
							item.put("status", String.valueOf(customJson.getString("status").toString()));
							item.put("visit", String.valueOf(customJson.getString("visit").toString()));
							item.put("tag1", "访客数:" + String.valueOf(customJson.getString("visit").toString()));
							item.put("tag2", "评论数:" + String.valueOf(customJson.getString("plnum").toString()));
							smalldataList.add(item);
						}
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if (action == AutoListView.REFRESH) {
						lstv.onRefreshComplete();
						dataList.clear();
						dataList.addAll(smalldataList);
					} else if (action == AutoListView.LOAD) {
						lstv.onLoadComplete();
						dataList.addAll(smalldataList);
					}
					lstv.setResultSize(smalldataList.size());
					adapter.notifyDataSetChanged();
				}else
				{
					lstv.onLoadComplete();
					lstv.setResultSize(0);
					adapter.notifyDataSetChanged();
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
			case MarketAPI.ACTION_GETINFOS:
				break;
			default:
				break;
		}
	}



	@Override
	public void onRefresh() {
		loadData(AutoListView.REFRESH);
	}

	@Override
	public void onLoad() {
		loadData(AutoListView.LOAD);
	}


	private String[] PK = new String[] { "删除" };
	String sel_guid="";
	@Override
	public void click(View v) {
		/*Toast.makeText( this.getActivity (),
				                "listview的内部的按钮被点击了！，位置是-->" + (Integer) v.getTag() + ",内容是-->"
				                         + dataList.get((Integer) v.getTag()).get ("guid"),
				                Toast.LENGTH_SHORT).show();
		*/
		sel_guid = dataList.get((Integer) v.getTag()).get ("guid").toString ();
		new AlertDialog.Builder(this.getActivity ())
				.setTitle("操作")
				.setIcon(android.R.drawable.ic_dialog_info)
				//.setSingleChoiceItems(new String[] {"choice 1","choice 2","choice 3","choice 4"}, 0, new DialogInterface.OnClickListener() {
				.setItems(PK, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if (PK[which].equals("删除")) {
							/*sel_guid = dataList.get(location - 1).get("guid").toString();
							System.out.println(sel_guid);*/
							new Thread(DelItem).start();

						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				}).show();
	}


	Runnable DelItem = new Runnable() {
		@Override
		public void run() {
			if (!NetworkUtils.isNetConnected(myapplication)) {
				T.showShort(myapplication, "当前无网络连接！");
				NetworkUtils.showNoNetWorkDlg(myapplication);
				return;
			}
			int result;
			String target = myapplication.getlocalhost()+"delinfo.php";
			HttpPost httprequest = new HttpPost(target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();

			paramsList.add(new BasicNameValuePair ("_guid", sel_guid));// 本人
			try {
				httprequest.setEntity(new UrlEncodedFormEntity (paramsList,
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
				msg.what=1;
				Noticehandler.sendMessage(msg);
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	Handler Noticehandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what==1) {
				Bundle data = msg.getData();
				int result = data.getInt("result");
				Log.i("mylog", "请求结果-->" + result);
				if (result == 1) {
					T.showShort(myapplication, "删除成功！");
				}else
				{
					T.showShort(myapplication, "删除失败！");
				}
			}
		}
	};
}