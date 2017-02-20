package com.bbxiaoqu.ui;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.R;
import com.bbxiaoqu.comm.adapter.NewListViewAdapter;
import com.bbxiaoqu.api.util.Utils;
import com.bbxiaoqu.comm.tool.CustomerHttpClient;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.StreamTool;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.comm.view.XCFlowLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.ViewGroup.MarginLayoutParams;

public class SearchActivity extends Activity implements NewListViewAdapter.Callback{
    private Drawable mIconSearchClear; // 搜索文本框清除文本内容图标
    private EditText etSearch ;
    ImageView  ivDeleteText;
	TextView btnSearch;
    private String mNames[] = {
            "2016","天气","风景",
            "浪漫","风光","租房","培训" ,"2016年奥运会","世界杯","中国南海","足球"
    };
    private XCFlowLayout mFlowLayout;
    String keyword="";
	private ListView lstv;
	private NewListViewAdapter adapter;
	private List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
	private DemoApplication myapplication;
	boolean issou=false;

    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		Resources resource = this.getResources();
		String pkgName = this.getPackageName();
		myapplication = (DemoApplication) this.getApplication();
		 initChildViews();
		initView();

		lstv = (ListView) findViewById(R.id.search_lstv);
		lstv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,int location, long arg3)
			{
				if(dataList.get(location)!=null) {
					Intent Intent1 = new Intent();

					Intent1.setClass(SearchActivity.this, ViewActivity.class);

					Bundle arguments = new Bundle();
					arguments.putString("put", "false");
					arguments.putString("guid", dataList.get(location).get("guid").toString());
					Intent1.putExtras(arguments);
					startActivity(Intent1);
				}
			}
		});
		LinearLayout emptyView = (LinearLayout) findViewById(R.id.nomess);
		lstv.setEmptyView(emptyView);
		adapter = new NewListViewAdapter (SearchActivity.this, dataList,SearchActivity.this);
		lstv.setAdapter(adapter);
		lstv.setDividerHeight (30);

	}
    ArrayList<TextView> allkeyword =new ArrayList<TextView>();
    private void initChildViews() {
        // TODO Auto-generated method stub
        mFlowLayout = (XCFlowLayout) findViewById(R.id.flowlayout);
        MarginLayoutParams lp = new MarginLayoutParams(
                LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        lp.leftMargin = 20;
        lp.rightMargin = 20;
        lp.topMargin = 10;
        lp.bottomMargin = 10;

        for(int i = 0; i < mNames.length; i ++){
            TextView view = new TextView(this);
			view.setPadding(20, 20, 20, 20);
            view.setText(mNames[i]);
            view.setTag(mNames[i]);
			view.setClickable (true);
     		view.setBackgroundResource(R.drawable.selector_searchtxt_background);
            view.setOnClickListener(new OnClickListener() {
            	public void onClick(View v){
					myrefresh(v);
            		keyword=v.getTag().toString();
					issou=true;
    				new Thread(new Runnable() {
    					@Override
    					public void run(){
    						Message msg = handler.obtainMessage();
    						msg.obj = getData();
    						handler.sendMessage(msg);
    					}
    				}).start();
            	}
            });
			allkeyword.add (view);
            mFlowLayout.addView(view,lp);
        }
    }
	public void myrefresh(View v)
	{
		for(int i=0;i<allkeyword.size ();i++)
		{
			TextView obj = allkeyword.get (i);

			if(v==obj)
			{
				v.setPadding(20, 20, 20, 20);
				obj.setBackgroundResource(R.drawable.search_text_view_border_sel);
			}else
			{
				obj.setBackgroundResource(R.drawable.selector_searchtxt_background);
			}
		}

	}

	public void myrefreshall()
	{
		for(int i=0;i<allkeyword.size ();i++)
		{
			TextView obj = allkeyword.get (i);
			obj.setBackgroundResource(R.drawable.selector_searchtxt_background);
		}

	}
	private void initView() {
		final Resources res = getResources();
       // mIconSearchDefault = res.getDrawable(R.mipmap.txt_search_default);
        mIconSearchClear = res.getDrawable(R.mipmap.txt_search_clear);
        ivDeleteText = (ImageView) findViewById(R.id.ivDeleteText);
        etSearch  = (EditText) findViewById(R.id.etSearch);
        ivDeleteText.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                etSearch.setText("");
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                  System.out.println(s);
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
                // TODO Auto-generated method stub
            	 System.out.println(s);
            }

            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    ivDeleteText.setVisibility(View.GONE);
                } else {
                    ivDeleteText.setVisibility(View.VISIBLE);
                }
            }
        });

        btnSearch  = (TextView) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				keyword=etSearch.getText().toString();

				if(keyword.length()==0)
				{
					 Utils.makeEventToast(SearchActivity.this, "请输入关键词",false);
					 return;
				}
				issou=true;
				myrefreshall();
					new Thread(new Runnable() {
					@Override
					public void run(){
						Message msg = handler.obtainMessage();
						msg.obj = getData();
						handler.sendMessage(msg);
					}
				}).start();
			}
		});
	}






	public void doBack(View view) {
		onBackPressed();
	}




	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if(issou)
			{
				mFlowLayout.setVisibility(View.GONE);
				RelativeLayout resultview=(RelativeLayout)findViewById (R.id.resultview);
				resultview.setVisibility (View.VISIBLE);

			}else
			{
				mFlowLayout.setVisibility(View.VISIBLE);
				RelativeLayout resultview=(RelativeLayout)findViewById (R.id.resultview);
				resultview.setVisibility (View.GONE);

			}
			List<Map<String, Object>> partresult = (List<Map<String, Object>>) msg.obj;//不放在这里获取是防界面僵					
			if(partresult!=null&&partresult.size()>0)
			{
				//mFlowLayout.setVisibility(View.GONE);
			}else
			{
				//mFlowLayout.setVisibility(View.VISIBLE);
				T.showShort(myapplication, "没有匹配到\""+keyword+"\"记录！");
				Button viewhistorybtn = (Button) findViewById(R.id.viewhistorybtn);
				viewhistorybtn.setText ("没有匹配到\""+keyword+"\"记录");
			}

			dataList.clear();
			dataList.addAll(partresult);
			adapter.notifyDataSetChanged();
		};
	};





	public ArrayList<Map<String, Object>> getData() {
		List<Map<String, Object>> smalldataList = new ArrayList<Map<String, Object>>();
		String url="";

		dataList.clear();
		int start=0;
		int limit=10;
		//String keyword=etSearch.getText().toString();
		//myapplication.getLat(),myapplication.getLng()
				//+"&latitude="+latitude+"&longitude="+longitude+"
		url="https://api.bbxiaoqu.com/getinfos.php?userid="+myapplication.getUserId()+"&latitude="+myapplication.getLat()+"&longitude="+myapplication.getLng()+"&rang=xiaoqu&keyword="+keyword+"&start="+start+"&limit="+limit;

		HttpGet httprequest = new HttpGet(url);
		HttpClient HttpClient1 = new DefaultHttpClient();
		// 请求超时
		HttpClient1.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
		// 读取超时
		HttpClient1.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
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
		if (httpResponse!=null&&httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			InputStream jsonStream = null;
			try {
				jsonStream = httpResponse.getEntity().getContent();
				byte[] data = StreamTool.read(jsonStream);
				String json = new String(data);
				if(json.length()>0)
				{
					JSONArray jsonarray = null;
					try {
						jsonarray = new JSONArray(json);
						for (int i = 0; i < jsonarray.length(); i++) {
							JSONObject customJson = jsonarray.getJSONObject(i);
							HashMap<String, Object> item = new HashMap<String, Object>();
							double len = getDistance(
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
				}
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


		return (ArrayList<Map<String, Object>>) smalldataList;
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
				//result= String.valueOf (days)+"天前";
				if(days>90)
				{
					result=sendtime.substring (0,10);
				}else
				{
					result= String.valueOf (days)+"天前";
				}
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


	public static double getDistance(double lat1, double lng1, double lat2, double lng2) {
		double earthRadius = 6367000; //approximate radius of earth in meters
		lat1 = (lat1 * Math.PI) / 180;
		lng1 = (lng1 * Math.PI) / 180;
		lat2 = (lat2 * Math.PI) / 180;
		lng2 = (lng2 * Math.PI) / 180;
		double calcLongitude = lng2 - lng1;
		double calcLatitude = lat2 - lat1;
		double stepOne = Math.pow(Math.sin(calcLatitude / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(calcLongitude / 2), 2);
		double stepTwo = 2 * Math.asin(Math.min(1, Math.sqrt(stepOne)));
		double calculatedDistance = earthRadius * stepTwo;
		return Math.round(calculatedDistance); //四舍五入
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
		new AlertDialog.Builder(SearchActivity.this)
				.setTitle("操作")
				.setIcon(android.R.drawable.ic_dialog_info)
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