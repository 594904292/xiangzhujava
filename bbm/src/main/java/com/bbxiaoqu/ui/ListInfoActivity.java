package com.bbxiaoqu.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

import com.baidu.location.LocationClient;
import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.R;
import com.bbxiaoqu.api.ApiAsyncTask;
import com.bbxiaoqu.api.MarketAPI;
import com.bbxiaoqu.client.baidu.Utils;
import com.bbxiaoqu.ui.fragment.FragmentInfo1;
import com.bbxiaoqu.ui.fragment.FragmentInfo2;
import com.bbxiaoqu.ui.fragment.FragmentInfo3;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ListInfoActivity extends FragmentActivity implements TabHost.OnTabChangeListener, ApiAsyncTask.ApiRequestListener {
	private DemoApplication myapplication;
	private LocationClient mLocationClient;


	private FragmentTabHost mTabHost;

	private LayoutInflater layoutInflater;

	private Class fragmentArray[] = {FragmentInfo1.class, FragmentInfo2.class, FragmentInfo3.class};

	private String mTextviewArray[] = {"待解决", "全部", "我的"};
	private SoundPool soundPool;
	private ImageView left_image;
	private ImageView right_image;
	int page=0;
	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	private GoogleApiClient client;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listinfo_layout);
		myapplication = (DemoApplication) this.getApplication();
		myapplication.getInstance().addActivity(this);
		initView();
		//新页面接收数据
		Bundle bundle = this.getIntent().getExtras();
		//接收name值
		if(bundle.containsKey ("page")) {
			page = bundle.getInt ("page");
		}


		initTabHost();


		MarketAPI.DAILYLOGIN(myapplication, this,myapplication.getUserId());

	}
	private void initView()
	{
		TextView PageTitle = (TextView) findViewById(R.id.PageTitle);
		PageTitle.setText ("我能帮");

		right_image = (ImageView) findViewById(R.id.top_menu_right_image);
		right_image.setVisibility(View.VISIBLE);
		right_image.setOnClickListener(new View.OnClickListener () {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(ListInfoActivity.this,SearchActivity.class);
				startActivity(intent);
			}
		});
	}

	public void doBack(View view) {
		onBackPressed();
	}

	int currenttab = 0;

	private void initTabHost() {
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		//点击动作参考http://www.jerehedu.com/fenxiang/162138_for_detail.htm

		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		mTabHost.getTabWidget().setMinimumHeight(30);// 设置tab的高度
		mTabHost.getTabWidget ().setShowDividers (LinearLayout.SHOW_DIVIDER_MIDDLE);
		mTabHost.getTabWidget().setDividerDrawable(R.mipmap.xz_xi_icon);


		int count = fragmentArray.length;
		for (int i = 0; i < count; i++) {
			//对Tab按钮添加标记和图片
			//TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
			TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
			//添加Fragment
			mTabHost.addTab(tabSpec, fragmentArray[i], null);
			//ea194a
			mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.selector_tab_background);
		}
		mTabHost.setOnTabChangedListener(this);
		mTabHost.setCurrentTab (page);
	}

	private View getTabItemView(int index) {
		layoutInflater = LayoutInflater.from(this);
		View view = layoutInflater.inflate(R.layout.tab_item_tag_view, null);
		TextView textView = (TextView) view.findViewById(R.id.textview);
		textView.setTextSize (18);
		if(index==page) {
			textView.setTextColor (getResources().getColor(R.color.xz_tab_sel_redcolor));
		}else
		{
			textView.setTextColor  (getResources().getColor(R.color.xz_tab_sel_graycolor));
		}
		textView.setText(mTextviewArray[index]);
		return view;
	}

	@Override
	public void onTabChanged(String tabId) {
		TabWidget tabw = mTabHost.getTabWidget();
		for(int i=0;i<tabw.getChildCount();i++){
			View view=tabw.getChildAt(i);
			if(i==mTabHost.getCurrentTab()){
				((TextView)view.findViewById(R.id.textview)).setTextColor(getResources().getColor(R.color.xz_tab_sel_redcolor));
			}else{
				((TextView)view.findViewById(R.id.textview)).setTextColor(getResources().getColor(R.color.xz_tab_sel_graycolor));

			}

		}
	}
	@Override
	public void onSuccess(int method, Object obj) {
		// TODO Auto-generated method stub
		switch (method) {
			case MarketAPI.ACTION_DAILYLOGIN:
				HashMap<String, String> result3 = (HashMap<String, String>) obj;
				String JsonContext3=result3.get("result");
				System.out.println(JsonContext3);
				break;

			default:
				break;
		}
	}


	@Override
	public void onError(int method, int statusCode) {
		// TODO Auto-generated method stub
		switch (method) {
			case MarketAPI.ACTION_DAILYLOGIN:
				break;
			default:
				break;
		}
	}


}
