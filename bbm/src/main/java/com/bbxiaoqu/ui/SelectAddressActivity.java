package com.bbxiaoqu.ui;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.bbxiaoqu.R;
import com.bbxiaoqu.api.util.Utils;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.T;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static com.bbxiaoqu.ui.fragment.FragmentPage4.SEL_XIAOQU;

public class SelectAddressActivity extends Activity implements OnMapClickListener,
		BDLocationListener, OnMapStatusChangeListener,OnGetGeoCoderResultListener{


	private LocationClient client;	//定位用的client
	private MapView mapView;	//地图空间
	private BaiduMap baiduMap;	//地图的控制器

	private double latitude;	//纬度
	private double longitude;	//经度
	private String address,city; //选中的地址、城市字符串
	private TextView txt_address;
	private ImageButton locButton;

	private GeoCoder geoCoder; //地理位置编码工具 将经纬度与地理位置字符串信息互转
	private TextView top_menu_right_Text;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_address);

		TextView PageTitle = (TextView) findViewById(R.id.PageTitle);
		PageTitle.setText ("位置选择");
		top_menu_right_Text = (TextView) findViewById(R.id.top_menu_right_Text);
		top_menu_right_Text.setText ("确定");
		top_menu_right_Text.setVisibility(View.VISIBLE);
		top_menu_right_Text.setOnClickListener(new View.OnClickListener () {
			@Override
			public void onClick(View v) {
			// TODO Auto-generated method stub
				if(!TextUtils.isEmpty(city) && !TextUtils.isEmpty(address)){
					//跳转到位置搜索的activity
					/*Intent intent = new Intent(SelectAddressActivity.this,LocationSearchActivity.class);
					intent.putExtra("city", city);
					intent.putExtra("address", address);
					startActivityForResult(intent, 12);*/
					Intent intent = new Intent();
					intent.putExtra("address", address);// 放入返回值
					intent.putExtra("latitude", latitude);// 放入返回值
					intent.putExtra("longitude", longitude);// 放入返回值
					setResult(SEL_XIAOQU, intent);// 放入回传的值,并添加一个Code,方便区分返回的数据
					finish();// 结束当前的activity,等于点击返回按钮
				}

			}
		});
		//初始化地图配置
		initLocation();
		//定位当前位置
		client.start();
	}



	public void doBack(View view) {
		onBackPressed();
	}


	private void initLocation() {
		txt_address = (TextView) findViewById(R.id.address);
		txt_address.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!TextUtils.isEmpty(city) && !TextUtils.isEmpty(address)){
					//跳转到位置搜索的activity
					/*Intent intent = new Intent(SelectAddressActivity.this,LocationSearchActivity.class);
					intent.putExtra("city", city);
					intent.putExtra("address", address);
					startActivityForResult(intent, 12);*/
					Intent intent = new Intent();
					intent.putExtra("address", address);// 放入返回值
					intent.putExtra("latitude", latitude);// 放入返回值
					intent.putExtra("longitude", longitude);// 放入返回值
					setResult(SEL_XIAOQU, intent);// 放入回传的值,并添加一个Code,方便区分返回的数据
					finish();// 结束当前的activity,等于点击返回按钮
				}
			}
		});
		locButton = (ImageButton) findViewById(R.id.mylocation);
		locButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (client != null && !client.isStarted()) {
					client.start();//点击开始定位
				}
			}
		});
		// 获取baiduMap对象 地图的操作都是在上面进行的
		mapView = (MapView) findViewById(R.id.mapView);
		baiduMap = mapView.getMap();

		mapView.setClickable(true);
		baiduMap.setOnMapClickListener(this);//为地图添加点击监听
		// 增加地图状态改变的监听 监听方法中返回的坐标就是当前地图中心位置的坐标
		baiduMap.setOnMapStatusChangeListener(this);

		// 开启地图的定位位置显示功能
		baiduMap.setMyLocationEnabled(true);
		//三个参数的意思分别是 地图的显示方式 是否显示方向 自定义的定位图标
		MyLocationConfiguration configuration = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, false, null);
		baiduMap.setMyLocationConfigeration(configuration);
		//更新地图状态默认缩放登记为15
		baiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15.0f));

		//获取geoCoder 地理编码转化工具的实例 并添加监听
		geoCoder = GeoCoder.newInstance();
		geoCoder.setOnGetGeoCodeResultListener(this);

		// 定位功能设置
		client = new LocationClient(getApplicationContext());
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系
		int span = 1000;
		option.setScanSpan(span);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);// 可选，默认false,设置是否使用gps
		option.setLocationNotify(true);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setIsNeedLocationDescribe(true);// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationPoiList(true);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		option.setIgnoreKillProcess(false);// 可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
		option.SetIgnoreCacheException(false);// 可选，默认false，设置是否收集CRASH信息，默认收集
		option.setEnableSimulateGps(false);// 可选，默认false，设置是否需要过滤gps仿真结果，默认需要
		client.setLocOption(option);
		//添加定位监听
		client.registerLocationListener(this);
	}

	@Override
	public void onReceiveLocation(BDLocation bdLocation) {
		latitude = bdLocation.getLatitude();
		longitude = bdLocation.getLongitude();
		address = bdLocation.getAddrStr();
		txt_address.setText(address);
		client.stop();//结束定位
		// 清空地图所有的 Overlay 覆盖物以及 InfoWindow
		baiduMap.clear();
		//更新地图状态 将定位到的位置移动到屏幕中心
		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 17f);
		baiduMap.animateMapStatus(msu);
		//设置定位位置显示
		MyLocationData.Builder builder = new MyLocationData.Builder();
		MyLocationData myLocationData = builder.latitude(latitude).longitude(longitude).build();
		baiduMap.setMyLocationData(myLocationData);

		city=bdLocation.getCity();
	}

	@Override
	public void onMapStatusChange(MapStatus mapStatus) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMapStatusChangeFinish(MapStatus mapStatus) {
		// TODO Auto-generated method stub
		latitude = mapStatus.target.latitude;
		longitude = mapStatus.target.longitude;
		//mapStatus中没有地址字符串所以要处理一下
		geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(mapStatus.target));
		baiduMap.clear();
		BitmapDescriptor bitmap = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_gcoding);
		// 构建MarkerOption，用于在地图上添加Marker
		OverlayOptions option = new MarkerOptions().position(mapStatus.target).icon(bitmap);
		// 在地图上添加Marker，并显示
		baiduMap.addOverlay(option);
	}

	@Override
	public void onMapStatusChangeStart(MapStatus mapStatus) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {

	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			//没有检索到结果
			return;
		}
		//获取地理编码结果
		address = result.getAddress();
		txt_address.setText(address);
		if(result.getAddressDetail()!=null && !TextUtils.isEmpty(result.getAddressDetail().city)){
			city = result.getAddressDetail().city;
		}
	}

	@Override
	public void onMapClick(LatLng latLng) {
		latitude = latLng.latitude;
		longitude = latLng.longitude;

		baiduMap.clear();
		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(new LatLng(
				latitude, longitude));
		baiduMap.animateMapStatus(msu);


	}

	@Override
	public boolean onMapPoiClick(MapPoi arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==12 && resultCode==RESULT_OK){
			address = data.getStringExtra("address");
			longitude = data.getDoubleExtra("longitude", 0);
			latitude = data.getDoubleExtra("latitude", 0);

			baiduMap.clear();
			MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(new LatLng(
					latitude, longitude));
			baiduMap.animateMapStatus(msu);
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item = menu.add("确定");
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent();
				intent.putExtra("address", address);
				intent.putExtra("latitude", latitude);
				intent.putExtra("longitude", longitude);
				setResult(RESULT_OK, intent);
				finish();
				return true;
			}
		});
		return super.onCreateOptionsMenu(menu);
	}


}
