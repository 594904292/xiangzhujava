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
import com.bbxiaoqu.ImageOptions;
import com.bbxiaoqu.R;
import com.bbxiaoqu.comm.bean.InfoBase;
import com.bbxiaoqu.comm.service.db.MessGzService;
import com.bbxiaoqu.comm.service.db.XiaoquService;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.StreamTool;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.comm.tool.CommUtil;
import com.bbxiaoqu.comm.widget.ImageViewSubClass;
import com.bbxiaoqu.comm.widget.XCRoundImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class InfoGzActivity extends Activity {
	private DemoApplication myapplication;

	ListView lstv;
	private List<Map<String, Object>> data;
	private List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
	//Context mContext;
	MyAdapter adapter;
	XiaoquService xiaoquService;
	TextView title ;
	TextView right_text;

	public ImageView right_image;

	private static final int MESSAGETYPE_01 = 0x0001;
	private ProgressDialog progressDialog = null;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.activity_gzinfo);
		 initView();

		myapplication = (DemoApplication) this.getApplication();
		xiaoquService = new XiaoquService(this);
		lstv = (ListView) findViewById(R.id.lvgz);
		lstv.setDividerHeight (30);
		lstv.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int location, long arg3) {
				Intent Intent1=null;
				Intent1 = new Intent(InfoGzActivity.this, ViewActivity.class);
				Bundle arguments = new Bundle();
				arguments.putString("put", "false");
				arguments.putString("guid",dataList.get(location).get("guid").toString());
				Intent1.putExtras(arguments);
				startActivity(Intent1);
			}
		});
		LinearLayout emptyView = (LinearLayout) findViewById(R.id.nomess);
		Button viewhistorybtn = (Button) findViewById(R.id.viewhistorybtn);
		viewhistorybtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				T.showShort(myapplication,"无收藏记录");
			}
		});
		lstv.setEmptyView(emptyView);
		adapter = new MyAdapter(this.getApplicationContext());

		downData();
		loadData();

	}

	private void initView() {
		TextView PageTitle = (TextView) findViewById(R.id.PageTitle);
		PageTitle.setText ("我的收藏");
		right_image = (ImageView) findViewById(R.id.top_menu_right_image);
		right_image.setVisibility(View.VISIBLE);
		right_image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(InfoGzActivity.this,SearchActivity.class);
				startActivity(intent);
			}
		});

	}


	private  void downData()
	{
		MessGzService db=new MessGzService(myapplication.getApplicationContext());
		db.removeallgz (myapplication.getUserId());
		String target=myapplication.getlocalhost()+"getgzguid.php?userid="+myapplication.getUserId();
		dataList = new ArrayList<Map<String, Object>>();
		List<InfoBase> bfjllist=null;
		HttpGet httprequest = new HttpGet(target);
		HttpClient HttpClient1 = new DefaultHttpClient();
		HttpResponse httpResponse = null;
		try {
			///////////////////////////////
			httpResponse = HttpClient1.execute(httprequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				InputStream jsonStream = null;
				try {
					jsonStream = httpResponse.getEntity().getContent();
					byte[] data = StreamTool.read(jsonStream);
					String json = new String(data);
					JSONArray jsonarray = new JSONArray(json);
					for (int i = 0; i < jsonarray.length(); i++) {
						JSONObject jsonobject = jsonarray.getJSONObject (i);
						String guid = jsonobject.getString ("guid");
						db.addgz (guid,myapplication.getUserId());
					}
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (httpResponse != null) {
					httpResponse.getEntity ().getContent ().close ();
				}
			} catch (IllegalStateException e) {

			} catch (IOException e) {

			}
		}
		Message msg = new Message();
		msg.what=1;
		downhandler.sendMessage(msg);
	}

	Handler downhandler = new Handler () {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what==1)
			{
				loadData();
			}
		}
	};

	private void loadData() {
		// TODO Auto-generated method stub
	    if (lstv == null)
        {
          return;
        }
	    getData() ;
	    lstv.setAdapter(adapter);
	}

	private void getData() {
		MessGzService db=new MessGzService(myapplication.getApplicationContext());
		 ArrayList<String> list=db.getguids(myapplication.getUserId());
		 StringBuffer buf=new StringBuffer();
		 for(int i=0;i<list.size();i++)
		 {
			 buf.append("'").append(list.get(i).toString()).append("'");
			 if(i<list.size()-1)
			 {
				 buf.append(",");
			 }
			 System.out.println(list.get(i).toString());
		 }
		 if (!NetworkUtils.isNetConnected(InfoGzActivity.this)) {
				T.showShort(InfoGzActivity.this, "当前无网络连接,请稍后再试！");
				return;
			}
		String target=myapplication.getlocalhost()+"getgzinfo.php?guid="+buf.toString();
		 dataList = new ArrayList<Map<String, Object>>();
	    	try {
	    		///////////////////////////////
	    		List<InfoBase> bfjllist=null;
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
	    			InputStream jsonStream = null;
	    			try {
						jsonStream = httpResponse.getEntity().getContent();
						byte[] data = StreamTool.read(jsonStream);
						String json = new String(data);
						JSONArray jsonarray = new JSONArray(json);
						for (int i = 0; i < jsonarray.length(); i++) {
							JSONObject customJson = jsonarray.getJSONObject(i);
							HashMap<String, Object> item = new HashMap<String, Object>();
							double len = CommUtil.getDistance(
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
							item.put("photo", String.valueOf(customJson.getString("photo").toString()));
							item.put("icon", String.valueOf(customJson.getString("photo").toString()));
							item.put("date", CommUtil.gethour(String.valueOf(customJson.getString("sendtime").toString())));
							item.put("status", String.valueOf(customJson.getString("status").toString()));
							item.put("visit", String.valueOf(customJson.getString("visit").toString()));
							item.put("tag1", "访客数:" + String.valueOf(customJson.getString("visit").toString()));
							item.put("tag2", "评论数:" + String.valueOf(customJson.getString("plnum").toString()));
							dataList.add(item);
						}
	    			} catch (IllegalStateException e) {
	    				// TODO Auto-generated catch block
	    				e.printStackTrace();
	    			} catch (IOException e) {
	    				// TODO Auto-generated catch block
	    				e.printStackTrace();
	    			}
	    		}


			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}










	 public final class ViewHolder{

		 XCRoundImageView headface;
		 TextView username;
		 ImageView sex;
		 TextView address;
		 TextView distance;
		 TextView timeago;
		 TextView sendcontent;
		 RelativeLayout img_row;
		 TextView tag1;
		 TextView tag2;
		 ImageView statusimg;
		}

	    public class MyAdapter extends BaseAdapter{
	    	 private Context ctx;
		    	private LayoutInflater mInflater;


				public MyAdapter(Context context){
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
					//ViewHolder holder_img = new ViewHolder();
					ViewHolder holder_img = null;
					//if (convertView == null) {
						holder_img=new ViewHolder();
						convertView = mInflater.inflate(R.layout.listview_item_infogz, null);
						holder_img.headface = (XCRoundImageView) convertView.findViewById(R.id.headface);
						holder_img.username = (TextView) convertView.findViewById(R.id.username);
						holder_img.sex = (ImageView) convertView.findViewById(R.id.sex_img);
						holder_img.address = (TextView) convertView.findViewById(R.id.address);
						holder_img.distance = (TextView) convertView.findViewById(R.id.distance);
						holder_img.timeago = (TextView) convertView.findViewById(R.id.timeago);
						holder_img.sendcontent = (TextView) convertView.findViewById(R.id.sendcontent);
						holder_img.img_row =(RelativeLayout) convertView.findViewById(R.id.img_row);
						holder_img.tag1 = (TextView) convertView.findViewById(R.id.tag1);
						holder_img.tag2 = (TextView) convertView.findViewById(R.id.tag2);
						holder_img.statusimg = (ImageView) convertView.findViewById(R.id.statusimg);
						convertView.setTag(holder_img);
				/*	}else {
						holder_img = (ViewHolder)convertView.getTag();
					}*/
					convertView.setTag(dataList.get(position).get("guid").toString().trim());
					String headfaceurl = "https://api.bbxiaoqu.com/uploads/"+dataList.get(position).get("headface").toString();
					if(dataList.get(position).get("headface").toString().length ()>0) {
						ImageLoader.getInstance ().displayImage (headfaceurl, holder_img.headface, ImageOptions.getOptions ());
					}else
					{
						holder_img.headface.setImageResource (R.mipmap.xz_wo_icon);
					}
					holder_img.username.setText(dataList.get(position).get("sendnickname").toString());
					//holder_img.sex.setImageDrawable (new Drawable (R.mipmap.xz_xiang_icon));
					if(dataList.get(position).get("sex").toString().equals ("1"))
					{
						holder_img.sex.setImageResource (R.mipmap.xz_nan_icon);
					}else
					{
						holder_img.sex.setImageResource (R.mipmap.xz_nv_icon);
					}
					holder_img.address.setText(dataList.get(position).get("street").toString());
					holder_img.distance.setText(dataList.get(position).get("distance").toString());
					holder_img.timeago.setText(dataList.get(position).get("date").toString());
					if(dataList.get(position).get("content") != null) {
						holder_img.sendcontent.setText(dataList.get(position).get("content").toString());
					}else
					{
						holder_img.sendcontent.setText("");
					}
					int num=0;
					if(dataList.get(position).get("icon").toString().trim().length()>0)
					{
						WindowManager wm1 = getWindowManager();
						int width = wm1.getDefaultDisplay().getWidth();
						int w=0;
						int h=0;
						String photo=dataList.get(position).get("icon").toString().trim();
						String[] arr=photo.split(",");
						if(arr.length>0) {
							num=arr.length;
							if(num==1)
							{
								w=width/4;
								h=width/4;
							}else
							{
								w=width/4;
								h=width/4;
							}
							LinearLayout m_LinearLayout = new LinearLayout(InfoGzActivity.this);
							m_LinearLayout.setOrientation(LinearLayout.HORIZONTAL);
							LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,h+20);
							for (int i = 0; i < arr.length; i++) {
								ImageViewSubClass image = new ImageViewSubClass (InfoGzActivity.this);
								image.setTag (dataList.get(position).get("guid").toString().trim()+"_"+i);
								image.setAdjustViewBounds(true);

								ImageLoader.getInstance().displayImage(DemoApplication.getInstance().getlocalhost()+"uploads/"+arr[i], image, ImageOptions.getOptions());
								image.setScaleType (ImageView.ScaleType.FIT_XY);
								image.setPadding (0,0,0,0);
								LinearLayout.LayoutParams layoutParams =new LinearLayout.LayoutParams(w,h);
								layoutParams.setMargins (10,10,10,10);
								m_LinearLayout.addView(image, layoutParams);
							}
							holder_img.img_row.addView (m_LinearLayout,param);
						}
					}
					holder_img.tag1.setText(dataList.get(position).get("tag1").toString());
					holder_img.tag2.setText(dataList.get(position).get("tag2").toString());
					if(dataList.get(position).get("status").toString().equals ("0"))
					{
						holder_img.statusimg.setImageResource (R.mipmap.xz_qiuzhu_icon);
					}else if(dataList.get(position).get("status").toString().equals ("1"))
					{
						holder_img.statusimg.setImageResource (R.mipmap.xz_qiuzhu_icon);
					}else if(dataList.get(position).get("status").toString().equals ("2"))
					{
						holder_img.statusimg.setImageResource (R.mipmap.xz_yijiejue_icon);
					}

					return convertView;
				}
	    }


	    public void doBack(View view) {
			onBackPressed();
		}
}

