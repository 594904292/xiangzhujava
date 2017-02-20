package com.bbxiaoqu.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.bbxiaoqu.AppInfo;
import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.R;
import com.bbxiaoqu.Session;
import com.bbxiaoqu.api.MarketAPI;
import com.bbxiaoqu.client.baidu.Utils;
import com.bbxiaoqu.comm.tool.CustomerHttpClient;
import com.bbxiaoqu.comm.tool.StreamTool;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.ui.ListConvenienceActivity;
import com.bbxiaoqu.ui.ListInfoActivity;
import com.bbxiaoqu.ui.LoadingView;
import com.bbxiaoqu.ui.MainTabActivity;
import com.bbxiaoqu.ui.PublishActivity;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Random;
import com.bbxiaoqu.api.ApiAsyncTask.ApiRequestListener;
import com.bbxiaoqu.ui.LoadingView.OnRefreshListener;
public class FragmentPage1 extends Fragment implements  ApiRequestListener,OnRefreshListener,ViewSwitcher.ViewFactory, View.OnTouchListener {
	private DemoApplication myapplication;
	View view;

	private RelativeLayout linearlayout_body1;
	private LinearLayout linearlayout_body2;

	public Button sos_btn;
	public Button can_sos_btn;

	/**
	 * ImagaSwitcher 的引用
	 */
	private ImageSwitcher mImageSwitcher;
	/**
	 * 图片id数组
	 */
	private int[] imgIds;
	/**
	 * 当前选中的图片id序号
	 */
	private int currentPosition;
	/**
	 * 按下点的X坐标
	 */
	private float downX;
	/**
	 * 装载点点的容器
	 */
	private LinearLayout linearLayout;
	/**
	 * 点点数组
	 */
	private ImageView[] tips;


	String load_emergency="";
	String load_emergencytelphone="";
	LinearLayout tel0;
	LinearLayout tel1;
	LinearLayout tel2;
	LinearLayout tel3;
	Button tel0_btn;
	Button tel1_btn;
	Button tel2_btn;
	Button tel3_btn;
	LoadingView mLoadView;
	protected Session mSession;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		myapplication = (DemoApplication) this.getActivity().getApplication();
		view= inflater.inflate(R.layout.fragment_1, null);
		mLoadView = (LoadingView) view.findViewById (R.id.loading_view);
		//mLoadView.setStatue(LoadingView.LOADING);
		mLoadView.setRefrechListener(this);
		mSession= Session.get(myapplication);
		initViews();

		//imgIds = new int[]{R.mipmap.banner1,R.mipmap.banner2,R.mipmap.banner3,R.mipmap.banner4};
		imgIds = new int[]{R.mipmap.banner1};
		//实例化ImageSwitcher
		mImageSwitcher  = (ImageSwitcher) view.findViewById(R.id.imageSwitcher1);
		//设置Factory
		mImageSwitcher.setFactory(this);
		//设置OnTouchListener，我们通过Touch事件来切换图片
		mImageSwitcher.setOnTouchListener(this);
		linearLayout = (LinearLayout)  view.findViewById(R.id.viewGroup);
		tips = new ImageView[imgIds.length];
		for(int i=0; i<imgIds.length; i++){
			ImageView mImageView = new ImageView(this.getActivity());
			tips[i] = mImageView;
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT));
			layoutParams.rightMargin = 3;
			layoutParams.leftMargin = 3;

			mImageView.setBackgroundResource(R.mipmap.page_indicator_unfocused);
			linearLayout.addView(mImageView, layoutParams);
		}
		//这个我是从上一个界面传过来的，上一个界面是一个GridView
		currentPosition = this.getActivity().getIntent().getIntExtra("position", 0);
		mImageSwitcher.setImageResource(imgIds[currentPosition]);
		setImageBackground(currentPosition);

		MarketAPI.getUserInfo(myapplication, this,myapplication.getUserId());
		MarketAPI.gonggao(myapplication, this);
		return view;
	}

	private void initViews() {
		linearlayout_body1= (RelativeLayout) view.findViewById(R.id.body1);
		linearlayout_body2= (LinearLayout) view.findViewById(R.id.body2);
		sos_btn=(Button) view.findViewById(R.id.sos_btn);
		can_sos_btn=(Button) view.findViewById(R.id.can_sos_btn);

		//求帮助
		sos_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(myapplication,PublishActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("infocatagroy", 0);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		//我能帮
		can_sos_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent intent=new Intent(myapplication,ListInfoActivity.class);
				Bundle bundle = new Bundle();
				//bundle.putInt("infocatagroy", 3);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		tel0=(LinearLayout) view.findViewById(R.id.line_0);
		tel1=(LinearLayout) view.findViewById(R.id.line_1);
		tel2=(LinearLayout) view.findViewById(R.id.line_2);
		tel3=(LinearLayout) view.findViewById(R.id.line_3);
		tel0_btn=(Button) view.findViewById(R.id.tel0);
		tel1_btn=(Button) view.findViewById(R.id.tel1);
		tel2_btn=(Button) view.findViewById(R.id.tel2);
		tel3_btn=(Button) view.findViewById(R.id.tel3);
		tel0.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Intent call = new Intent(Intent.ACTION_DIAL, uri); //显示拨号界面

				Intent intent=new Intent(myapplication, ListConvenienceActivity.class);
				Bundle bundle = new Bundle();
				intent.putExtras(bundle);
				startActivity(intent);

			}
		});
		tel1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Intent call = new Intent(Intent.ACTION_DIAL, uri); //显示拨号界面
				Intent intent = new Intent(Intent.ACTION_CALL);//直接播出电话
				Uri data = Uri.parse("tel:" + "110");
				intent.setData(data);
				startActivity(intent);
			}
		});
		tel2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Uri uri = Uri.parse("tel:" + "120");
				Intent call = new Intent(Intent.ACTION_CALL, uri); //直接播出电话
				startActivity(call);
			}
		});
		tel3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(load_emergencytelphone.length ()>0)
				{
					Uri uri = Uri.parse("tel:" + load_emergencytelphone);
					Intent call = new Intent(Intent.ACTION_CALL, uri); //直接播出电话
					startActivity(call);
				}else
				{
					T.showShort (myapplication,"未设置");
				}
			}
		});
		tel0_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(myapplication, ListConvenienceActivity.class);
				Bundle bundle = new Bundle();
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		tel1_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Intent call = new Intent(Intent.ACTION_DIAL, uri); //显示拨号界面
				Intent intent = new Intent(Intent.ACTION_CALL);//直接播出电话
				Uri data = Uri.parse("tel:" + "110");
				intent.setData(data);
				startActivity(intent);
			}
		});
		tel2_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Uri uri = Uri.parse("tel:" + "120");
				Intent call = new Intent(Intent.ACTION_CALL, uri); //直接播出电话
				startActivity(call);
			}
		});
		tel3_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(load_emergencytelphone.length ()>0)
				{
					Uri uri = Uri.parse("tel:" + load_emergencytelphone);
					Intent call = new Intent(Intent.ACTION_CALL, uri); //直接播出电话
					startActivity(call);
				}else
				{
					T.showShort (myapplication,"未设置");
				}
			}
		});



	}


	@Override
	public View makeView() {
		final ImageView i = new ImageView(this.getActivity());
		i.setBackgroundColor(0xff000000);
		i.setScaleType(ImageView.ScaleType.CENTER_CROP);
		i.setLayoutParams(new ImageSwitcher.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT));
		return i ;
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:{
				//手指按下的X坐标
				downX = event.getX();
				break;
			}
			case MotionEvent.ACTION_UP:{
				float lastX = event.getX();
				//抬起的时候的X坐标大于按下的时候就显示上一张图片
				if(lastX > downX){
					if(currentPosition > 0){
						//设置动画，这里的动画比较简单，不明白的去网上看看相关内容
						mImageSwitcher.setInAnimation(AnimationUtils.loadAnimation(myapplication, R.anim.left_in));
						mImageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(myapplication, R.anim.right_out));
						currentPosition --;
						//Bitmap bitmap = ImageLoader.getInstance().loadImageSync(imgIds[currentPosition % imgIds.length]);
						//mImageSwitcher.setImageDrawable(new BitmapDrawable(bitmap));

						mImageSwitcher.setImageResource(imgIds[currentPosition % imgIds.length]);
						setImageBackground(currentPosition);
					}else{
						Toast.makeText(myapplication, "已经是第一张", Toast.LENGTH_SHORT).show();
					}
				}

				if(lastX < downX){
					if(currentPosition < imgIds.length - 1){
						mImageSwitcher.setInAnimation(AnimationUtils.loadAnimation(myapplication, R.anim.right_in));
						mImageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(myapplication, R.anim.lift_out));
						currentPosition ++ ;
						//Bitmap bitmap = ImageLoader.getInstance().loadImageSync(imgIds[currentPosition]);
						//mImageSwitcher.setImageDrawable(new BitmapDrawable(bitmap));

						mImageSwitcher.setImageResource(imgIds[currentPosition]);
						setImageBackground(currentPosition);
					}else{
						Toast.makeText(myapplication, "到了最后一张", Toast.LENGTH_SHORT).show();
					}
				}
			}

			break;
		}

		return true;
	}

	/**
	 * 设置选中的tip的背景
	 * @param selectItems
	 */
	private void setImageBackground(int selectItems){
		for(int i=0; i<tips.length; i++){
			if(i == selectItems){
				tips[i].setBackgroundResource(R.mipmap.page_indicator_focused);
			}else{
				tips[i].setBackgroundResource(R.mipmap.page_indicator_unfocused);
			}
		}
	}



	Handler laodhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			TextView tel3_name=(TextView) view.findViewById(R.id.tel3_name);
			if(load_emergency!=null&&load_emergency.length ()>0) {
				//emergencycontact_tv.setText(load_emergency);
				tel3.setTag (load_emergencytelphone);
				tel3_name.setText (load_emergency);
			}

		}
	};



	public void onSuccess(int method, Object obj) {
		switch (method) {
			case MarketAPI.ACTION_GETUESERINFO:
				HashMap<String, String> result = (HashMap<String, String>) obj;
				String json=result.get("userinfo");
				JSONArray jsonarray;
				try {
					jsonarray = new JSONArray(json);
					JSONObject jsonobject = jsonarray.getJSONObject(0);

					if(jsonobject.getString ("emergencycontact")!=null&&!jsonobject.getString ("emergencycontact").toString ().equals ("null")&&jsonobject.getString ("emergencycontact").length ()>0)
					{//emergencycontact
						load_emergency= jsonobject.getString("emergencycontact");
						load_emergencytelphone= jsonobject.getString("emergencycontacttelphone");
					}else
					{
						load_emergency="未设置";
						load_emergencytelphone="";
					}
					Message msg = new Message();
					Bundle data = new Bundle();
					data.putString("emergency", load_emergency);
					data.putString("emergencytelphone", load_emergencytelphone);
					msg.setData(data);
					laodhandler.sendMessage(msg);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Utils.makeEventToast(this.myapplication, "userinfo xml解释错误",false);
				}
				break;
			case MarketAPI.ACTION_GONGGAO:
				HashMap<String, String> result1 = (HashMap<String, String>) obj;
				String JsonContext1=result1.get("gonggao");
				if(JsonContext1.length()>0)
				{
					JSONArray ggjsonarray;
					try {
						ggjsonarray = new JSONArray(JsonContext1);
						JSONObject ggjsonobject = ggjsonarray.getJSONObject(0);
						String id = ggjsonobject.getString("id");
						String title = ggjsonobject.getString("title");
						String content = ggjsonobject.getString("content");
						String publishdate = ggjsonobject.getString("publishdate");
						mSession.seGgid(id);
						mSession.setGgcontent(title);
						mSession.setGgdate(publishdate);
						//gonggao.setText(title);
						//gonggao.setTag(id);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Utils.makeEventToast(this.myapplication, "gonggao xml解释错误",false);
					}
				}
				break;

			default:
				break;
		}
	}


	@Override
	public void onError(int method, int statusCode) {
		switch (method) {
			case MarketAPI.ACTION_SYSTEMINFO:
				// 隐藏登录框
				System.out.println(statusCode);
				/*String msg = "连接不到服务器";
				Utils.makeEventToast(getApplicationContext(), msg, false);*/
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
