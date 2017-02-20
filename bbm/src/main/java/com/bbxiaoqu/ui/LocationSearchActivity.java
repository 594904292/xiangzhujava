package com.bbxiaoqu.ui;

import java.util.List;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult.ERRORNO;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.bbxiaoqu.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class LocationSearchActivity extends Activity implements TextWatcher,OnItemClickListener,
		OnGetPoiSearchResultListener{
	private EditText edt_address;
	private ListView listView;
	private PoiSearch search;
	private String city;
	private List<PoiInfo> poiInfos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_list);
		initView();
		//获取PoiSearch实例并为之添加监听事件
		search = PoiSearch.newInstance();
		search.setOnGetPoiSearchResultListener(this);
	}

	private void initView() {
		edt_address = (EditText) findViewById(R.id.edt_address);
		listView = (ListView) findViewById(R.id.listView);

		city = getIntent().getStringExtra("city");
		edt_address.setText(getIntent().getStringExtra("address"));

		edt_address.addTextChangedListener(this);
		listView.setOnItemClickListener(this);


	}



	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		search.destroy();
		super.onDestroy();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
								  int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterTextChanged(Editable s) {
		String str = s.toString().trim();
		if(!TextUtils.isEmpty(str)){
			PoiCitySearchOption option = new PoiCitySearchOption();
			option.city(city).keyword(str);//.pageCapacity(10).pageNum(10);

			search.searchInCity(option);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
							long id) {
		PoiInfo info = poiInfos.get(position);
		edt_address.setText(info.address);

		Intent intent = new Intent();
		intent.putExtra("address", info.address);
		intent.putExtra("latitude", info.location.latitude);
		intent.putExtra("longitude", info.location.longitude);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		if(result==null || result.error==ERRORNO.RESULT_NOT_FOUND){
			return;
		}
		poiInfos = result.getAllPoi();
		listView.setAdapter(new MyAdapter());
	}

	class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			if(poiInfos ==null)
				return 0;
			return poiInfos.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView==null){
				convertView = View.inflate(parent.getContext(), android.R.layout.simple_list_item_1, null);
			}
			((TextView)convertView).setText(poiInfos.get(position).address);
			return convertView;
		}



	}
}
