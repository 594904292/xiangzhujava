package com.bbxiaoqu.ui;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.R;
import com.bbxiaoqu.api.util.Utils;
import com.bbxiaoqu.comm.adapter.NewListViewAdapter;
import com.bbxiaoqu.comm.tool.CustomerHttpClient;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.StreamTool;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.comm.view.XCFlowLayout;

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

import static com.bbxiaoqu.ui.fragment.FragmentPage4.SEL_XIAOQU;

public class SearchCxhfdmActivity extends Activity{
    private Drawable mIconSearchClear; // 搜索文本框清除文本内容图标
    private EditText etSearch ;
    ImageView  ivDeleteText;
	TextView btnSearch;

    String keyword="";
	private ListView lstv;
	private ArrayList<ListItem> mList;
	private DemoApplication myapplication;

    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_cxhfdm);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		Resources resource = this.getResources();
		String pkgName = this.getPackageName();
		myapplication = (DemoApplication) this.getApplication();
		initView();
		lstv = (ListView) findViewById(R.id.search_lstv);
		// 获取Resources对象

		mList = new ArrayList<ListItem>();

		lstv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,int location, long arg3)
			{
				/*if(dataList.get(location)!=null) {
					Intent Intent1 = new Intent();

					Intent1.setClass(SearchCxhfdmActivity.this, ViewActivity.class);

					Bundle arguments = new Bundle();
					arguments.putString("put", "false");
					arguments.putString("guid", dataList.get(location).get("guid").toString());
					Intent1.putExtras(arguments);
					startActivity(Intent1);
				}*/
				ListItem item=mList.get(location);

				Intent intent = new Intent();
				intent.putExtra("code", item.getCode());// 放入返回值
				intent.putExtra("name", item.getName());// 放入返回值
				setResult(SEL_XIAOQU, intent);// 放入回传的值,并添加一个Code,方便区分返回的数据
				finish();// 结束当前的activity,等于点击返回按钮
			}
		});

		loadlist();
	}
	SearchCxhfdmListViewAdapter adapter;


	private void loadlist() {
		if (lstv == null) {
			return;
		}
		mList=getData("");
		LinearLayout emptyView = (LinearLayout) findViewById(R.id.nomess);
		lstv.setEmptyView(emptyView);
		adapter = new SearchCxhfdmListViewAdapter();
		lstv.setAdapter(adapter);
		lstv.setDividerHeight (10);
		lstv.setAdapter(adapter);
	}

	private ArrayList<ListItem> getData(String namepart) {
		if (!NetworkUtils.isNetConnected(myapplication)) {
			T.showShort(myapplication, "当前无网络连接！");
			NetworkUtils.showNoNetWorkDlg(SearchCxhfdmActivity.this);
			return null;
		}
		mList.clear();
		String target="";
		if(namepart.length()>0) {
			target = myapplication.getlocalhost() + "getcxhfdm_v1.php?name=" + namepart;
		}else
		{
			String province=myapplication.getProvince();
			String city=myapplication.getCity();
			String district=myapplication.getDistrict();
			target = myapplication.getlocalhost() + "getcxhfdm_v1.php?province=" + province+"&city=" + city+"&district=" + district;
		}
		ArrayList<ListItem> smalllist=new ArrayList<ListItem>();
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
				InputStream jsonStream = null;
				try {
					jsonStream = httpResponse.getEntity().getContent();
					byte[] data = StreamTool.read(jsonStream);
					String json = new String(data);
					JSONArray jsonarray = new JSONArray(json);
					for (int i = 0; i < jsonarray.length(); i++) {
						JSONObject jsonobject = jsonarray.getJSONObject(i);
						int id = jsonobject.getInt("id");
						String province = jsonobject.getString("province");
						String city = jsonobject.getString("city");
						String district = jsonobject.getString("district");
						String street = jsonobject.getString("street");
						String name = jsonobject.getString("name");
						String code = jsonobject.getString("code");

						Resources res = this.getResources();
						ListItem item = new ListItem();
						item.setImage(res.getDrawable(R.mipmap.xz_fang_icon));
						item.setProvince(province);
						item.setDistrict(district);
						item.setCity(city);
						item.setStreet(street);
						item.setCode(code);
						item.setName(name);
						//mList.add(item);
						smalllist.add(item);
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
		return smalllist;
	}



	private void initView() {
		final Resources res = getResources();
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
				keyword=s.toString();
                // TODO Auto-generated method stub
				new Thread(new Runnable() {
					@Override
					public void run(){
						Message msg = handler.obtainMessage();
						msg.obj = getData(keyword);
						handler.sendMessage(msg);
					}
				}).start();
            }
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
				keyword=s.toString();
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
					 Utils.makeEventToast(SearchCxhfdmActivity.this, "请输入关键词",false);
					 return;
				}
				new Thread(new Runnable() {
					@Override
					public void run(){
						Message msg = handler.obtainMessage();
						msg.obj = getData(keyword);
						handler.sendMessage(msg);
					}
				}).start();
			}
		});
	}



	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			List<ListItem> partresult = (List<ListItem>) msg.obj;//不放在这里获取是防界面僵



			mList.clear();
			mList.addAll(partresult);
			adapter.notifyDataSetChanged();
		};
	};

	public void doBack(View view) {
		onBackPressed();
	}
	class SearchCxhfdmListViewAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mList.size();
		}

		/**
		 * 返回item的内容
		 */
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mList.get(position);
		}

		/**
		 * 返回item的id
		 */
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		/**
		 * 返回item的视图
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder listItemView;
			if (convertView == null) {
				convertView = LayoutInflater.from(SearchCxhfdmActivity.this).inflate(
						R.layout.cxhfdm_items, null);
				listItemView = new ViewHolder();
				listItemView.imageView = (ImageView) convertView
						.findViewById(R.id.image);
				listItemView.name = (TextView) convertView
						.findViewById(R.id.name);
				listItemView.code = (TextView) convertView
						.findViewById(R.id.code);
				listItemView.address = (TextView) convertView
						.findViewById(R.id.address);
				convertView.setTag(listItemView);
			} else {
				listItemView = (ViewHolder) convertView.getTag();
			}
			Drawable img = mList.get(position).getImage();
			String title = mList.get(position).getName();
			// 将资源传递给ListItemView的两个域对象
			listItemView.imageView.setImageDrawable(img);
			listItemView.name.setText(title);
			listItemView.code.setText("编码:"+mList.get(position).getCode());
			StringBuffer sb=new StringBuffer();
			sb.append(mList.get(position).getProvince());
			sb.append(",");
			if(!mList.get(position).getProvince().equals(mList.get(position).getCity())) {
				sb.append(mList.get(position).getCity());
				sb.append(",");
			}
			sb.append(mList.get(position).getDistrict());
			sb.append(",");
			sb.append(mList.get(position).getStreet());
			listItemView.address.setText("行政区:"+sb.toString());
			// 返回convertView对象
			return convertView;
		}
	}

	/**
	 * 封装两个视图组件的类
	 */
	class ViewHolder {
		ImageView imageView;
		TextView name;
		TextView code;
		TextView address;
	}

	/**
	 * 封装了两个资源的类
	 */
	class ListItem {
		private Drawable image;
		private String province;
		private String city;
		private String district;
		private String street;
		private String code;
		private String name;

		public String getProvince() {
			return province;
		}

		public void setProvince(String province) {
			this.province = province;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getDistrict() {
			return district;
		}

		public void setDistrict(String district) {
			this.district = district;
		}

		public String getStreet() {
			return street;
		}

		public void setStreet(String street) {
			this.street = street;
		}

		public Drawable getImage() {
			return image;
		}

		public void setImage(Drawable image) {
			this.image = image;
		}
		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}

}