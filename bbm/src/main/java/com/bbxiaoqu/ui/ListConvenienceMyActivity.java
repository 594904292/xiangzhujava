package com.bbxiaoqu.ui;

import android.content.Intent;
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
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.R;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.ui.fragment.FragmentConvenience1;
import com.bbxiaoqu.ui.fragment.FragmentConvenience2;
import com.bbxiaoqu.ui.fragment.FragmentConvenience3;
import com.bbxiaoqu.ui.fragment.FragmentConvenience4;
import com.google.android.gms.common.api.GoogleApiClient;

public class ListConvenienceMyActivity extends FragmentActivity implements TabHost.OnTabChangeListener{
	private DemoApplication myapplication;
	private LocationClient mLocationClient;


	private FragmentTabHost mTabHost;

	private LayoutInflater layoutInflater;

	private Class fragmentArray[] = {FragmentConvenience3.class, FragmentConvenience4.class};


	private String mTextviewArray[] = {"我的动态", "商家信息"};
	private SoundPool soundPool;
	private ImageView left_image;
	private TextView top_menu_right_Text;
	int page=0;
	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	private GoogleApiClient client;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listconvenienceinfo_layout);
		myapplication = (DemoApplication) this.getApplication();
		myapplication.getInstance().addActivity(this);
		initView();
		initTabHost();



	}
	private void initView()
	{
		TextView PageTitle = (TextView) findViewById(R.id.PageTitle);
		PageTitle.setText ("便民");

//		right_image = (ImageView) findViewById(R.id.top_menu_right_image);
//		right_image.setVisibility(View.VISIBLE);
//		right_image.setOnClickListener(new View.OnClickListener () {
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Intent intent=new Intent(myapplication,PublishShopInfoActivity.class);
//				Bundle bundle = new Bundle();
//				bundle.putInt("infocatagroy", 0);
//				intent.putExtras(bundle);
//				startActivity(intent);
//			}
//		});

		top_menu_right_Text = (TextView) findViewById(R.id.top_menu_right_Text);
		top_menu_right_Text.setVisibility(View.VISIBLE);
		top_menu_right_Text.setOnClickListener(new View.OnClickListener () {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(myapplication,PublishShopInfoActivity.class);
				Bundle bundle = new Bundle();
				intent.putExtras(bundle);
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



}
