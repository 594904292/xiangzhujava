package com.bbxiaoqu.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;
import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.R;
import com.bbxiaoqu.comm.tool.CustomerHttpClient;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.ScreenUtils;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.comm.widget.ImageViewSubClass;
import com.bbxiaoqu.comm.widget.RoundAngleImageView;
import com.bbxiaoqu.ui.config.Config;
import com.bbxiaoqu.ui.config.Constants;
import com.bbxiaoqu.comm.tool.ImageUtil;
import com.bbxiaoqu.comm.tool.BitmapUtils;
import com.bbxiaoqu.comm.tool.StringUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

public class PublishActivity extends Activity implements OnClickListener {
	private static final String TAG = "PublishActivity";
	/** 头像 */
	public ImageView top_head;
	/** 更多 */
	public ImageView top_more;

	private DemoApplication myapplication;
	EditText content_edit;
	TextView chCounterText;
	//EditText fee_edit;
	//RadioGroup radioGroup;

	private TextView yybtn;
	public static final int TO_SELECT_PHOTO1 = 2;
	private String picPath = null;
	private LocationClient mLocationClient;
	public Double nLatitude; // 经度 给gps定位用
	public Double nLontitude; // 纬度 给gps定位用
	public String address; // 纬度 给gps定位用
	public String Country = "";
	public String Province = "";
	public String City = "";
	public String CityCode = "";
	public String District = "";
	public String Street = "";
	public String StreetNumber = "";
	public String Floor = "";
	public String addr = "";
	public String networklocationtype = "";
	public String operators = "";
	public String direction = "";
	public String radius = "";
	public String speed = "";

	public static final int STATUS_None = 0;
	public static final int STATUS_WaitingReady = 2;
	public static final int STATUS_Ready = 3;
	public static final int STATUS_Speaking = 4;
	public static final int STATUS_Recognition = 5;
	private SpeechRecognizer speechRecognizer;
	private int status = STATUS_None;
	private long speechEndTime = -1;
	private static final int EVENT_ERROR = 11;
	private LayoutInflater mInflater;


	private LinearLayout mLayout;

	private List<ImageViewSubClass> mImageButtonList;
	private List<ImageView> close_mImageButtonList;

	private List<String> mPicturePathList;
	private List<String> compmPicturePathList;
	private int mCurrent;
	//private LinearLayout mpirceLayout;
	//private ToggleButton togglebutton;
	/** 屏幕宽度 */
	//private int mScreenWidth = 0;
	/** Item宽度 */
	//private int mItemWidth = 0;
	private int infocatagroy = 0;

	private ProgressBar pb;
	private TextView pbtip;
	private TextView pbtip1;
	private BaiduASRDigitalDialog mDialog = null;
	private DialogRecognitionListener mRecognitionListener;
	private int mCurrentTheme = Config.DIALOG_THEME;


	private TextView top_menu_right_Text;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String strVer = android.os.Build.VERSION.RELEASE;
		strVer = strVer.substring(0,3).trim();
		float fv=Float.valueOf(strVer);
		if(fv>2.3)
		{
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectDiskReads()
					.detectDiskWrites()
					.detectNetwork() // 这里可以替换为detectAll() 就包括了磁盘读写和网络I/O
					.penaltyLog() //打印logcat，当然也可以定位到dropbox，通过文件保存相应的log
					.build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectLeakedSqlLiteObjects() //探测SQLite数据库操作
					.penaltyLog() //打印logcat
					.penaltyDeath()
					.build());
		}
		setContentView(R.layout.activity_publish);
		myapplication = (DemoApplication) this.getApplication();
		myapplication.getInstance().addActivity(this);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		Resources resource = this.getResources();
		String pkgName = this.getPackageName();
		PushManager.startWork(getApplicationContext(),PushConstants.LOGIN_TYPE_API_KEY, Constants.API_KEY);
		TextView PageTitle = (TextView) findViewById(R.id.PageTitle);
		PageTitle.setText ("发布求助");
		top_menu_right_Text = (TextView) findViewById(R.id.top_menu_right_Text);
		top_menu_right_Text.setVisibility(View.VISIBLE);
		top_menu_right_Text.setOnClickListener(new View.OnClickListener () {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (!NetworkUtils.isNetConnected(myapplication)) {
					T.showShort(myapplication, "当前无网络连接！");
					NetworkUtils.showNoNetWorkDlg(PublishActivity.this);
					return;
				}
				String content = content_edit.getText().toString();
				if (content.length() > 0) {
					pb.setVisibility(View.VISIBLE);
					pbtip.setVisibility(View.VISIBLE);
					pbtip1.setVisibility(View.VISIBLE);
					pbtip.setText("保存中");
					new Thread(sendRun).start();

				} else {
					Toast.makeText(v.getContext(), "信息为空,发布不成功", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
		Bundle b = this.getIntent().getExtras();
		infocatagroy = b.getInt("infocatagroy");
		myapplication = (DemoApplication) this.getApplication();
		//mScreenWidth = ScreenUtils.getWindowsWidth(this)-20;
		//mItemWidth = mScreenWidth / 4;// 一个Item宽度为屏幕的1/7
		initbaidu(resource, pkgName);

		close_mImageButtonList= new ArrayList<ImageView>();
		mImageButtonList = new ArrayList<ImageViewSubClass>();
		mPicturePathList = new ArrayList<String>();
		compmPicturePathList = new ArrayList<String>();
		content_edit = (EditText) findViewById(R.id.content);

		content_edit.setFocusableInTouchMode(true);
		content_edit.requestFocus();
		chCounterText = (TextView) findViewById(R.id.chCounterText);
		Timer timer = new Timer ();
		timer.schedule(new TimerTask ()

					   {

						   public void run()

						   {

							   InputMethodManager inputManager =

									   (InputMethodManager)content_edit.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

							   inputManager.showSoftInput(content_edit, 0);

						   }

					   },

				998);
		content_edit.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {
				String content = content_edit.getText().toString();
				if(content.length()>200)
				{
					content_edit.setText(content.substring(0,200));

					String newcontent = content_edit.getText().toString();
					chCounterText.setText(newcontent.length() + "/200");
					T.showShort (PublishActivity.this,"超过最大输入字符数");

				}else {
					chCounterText.setText(content.length() + "/200");
				}

			}

		});
		if (infocatagroy == 0) {
			content_edit.setHint("请输入您的求助信息");
			Resources resources = PublishActivity.this.getResources();
			//Drawable btnDrawable = resources.getDrawable(R.drawable.button_help);
		} else if (infocatagroy == 1) {
			content_edit.setHint("请输入您的求助信息");
		} else if (infocatagroy == 2) {
			content_edit.setHint("请输入您的转让信息");
		} else if (infocatagroy == 3) {
			content_edit.setHint("请输入您的能帮助信息");
		}
		mLayout = (LinearLayout) findViewById(R.id.layout_container);
		yybtn = (TextView) findViewById(R.id.yybtn);
		yybtn.setCompoundDrawablePadding(20);
		yybtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
					mCurrentTheme = Config.DIALOG_THEME;
					if (mDialog != null) {
						mDialog.dismiss();
					}
					Bundle params = new Bundle();
					params.putString(BaiduASRDigitalDialog.PARAM_API_KEY, Constants.API_KEY);
					params.putString(BaiduASRDigitalDialog.PARAM_SECRET_KEY, Constants.SECRET_KEY);
					params.putInt(BaiduASRDigitalDialog.PARAM_DIALOG_THEME, Config.DIALOG_THEME);
					mDialog = new BaiduASRDigitalDialog(PublishActivity.this, params);
					mDialog.setDialogRecognitionListener(mRecognitionListener);
//	                }
					mDialog.getParams().putInt(BaiduASRDigitalDialog.PARAM_PROP, Config.CURRENT_PROP);
					mDialog.getParams().putString(BaiduASRDigitalDialog.PARAM_LANGUAGE,
							Config.getCurrentLanguage());
					Log.e("DEBUG", "Config.PLAY_START_SOUND = "+Config.PLAY_START_SOUND);
					mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_START_TONE_ENABLE, Config.PLAY_START_SOUND);
					mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_END_TONE_ENABLE, Config.PLAY_END_SOUND);
					mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_TIPS_TONE_ENABLE, Config.DIALOG_TIPS_SOUND);
					mDialog.show();
			}
		});
		pb = (ProgressBar) findViewById(R.id.myProgressBar1);
		pbtip=(TextView) findViewById(R.id.myProgressBar1Tip);
		pbtip1=(TextView) findViewById(R.id.myProgressBar1Tip1);
		pb.setVisibility(View.GONE);
		pbtip.setVisibility(View.GONE);
		pbtip1.setVisibility(View.GONE);
		mCurrent = 0;
		init_imgui();
		mRecognitionListener = new DialogRecognitionListener() {

			@Override
			public void onResults(Bundle results) {
				ArrayList<String> rs = results != null ? results
						.getStringArrayList(RESULTS_RECOGNITION) : null;
				if (rs != null && rs.size() > 0) {
					content_edit.setText(content_edit.getText().toString()+rs.get(0));
				}

			}
		};
		LoadLbsThread m = new LoadLbsThread();
		new Thread(m).start();
	}

	private void init_imgui() {
		final int count = 4; // 9格
		final int rowCount = 1;
		View.inflate(this, R.layout.layout_line_horizonal_white, mLayout);
		WindowManager wm1 = getWindowManager();
		int width = wm1.getDefaultDisplay().getWidth()-30;//左右有15
		int w=0;
		int h=0;
		w=width/4;
		h=width/4;
		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(width,h+20);
			// 创建布局对象，设置按下颜色
		final RelativeLayout m_LinearLayout = new RelativeLayout(this);
		m_LinearLayout.setBackgroundResource(R.drawable.row_selector);
		for (int i = 0; i < 4; i++) {
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					w,h);
			lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);//与父容器的左侧对齐
			lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);//与父容器的上侧对齐
			lp.leftMargin=i*w;
			lp.topMargin=0;

			ImageViewSubClass image = new ImageViewSubClass(this);
			image.setBackgroundResource(R.drawable.row_selector);
			int indextag = i;
			image.setTag(indextag);
			image.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
				if (v.getTag() != null) {
					Intent intent;
					intent = new Intent(v.getContext(),SelectPhotoActivity.class);
					Bundle arguments = new Bundle();
					arguments.putInt("pos", Integer.parseInt(v.getTag().toString()));
					intent.putExtras(arguments);
					startActivityForResult(intent, TO_SELECT_PHOTO1);
					intent = null;
				} else {
					// super.onClick(v);
				}
			}
				});
			image.setEnabled(false);
			image.setScaleType (ImageView.ScaleType.FIT_XY);
			m_LinearLayout.addView(image, lp);


			RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(
					60,60);
			lp1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);//与父容器的左侧对齐
			lp1.addRule(RelativeLayout.ALIGN_PARENT_TOP);//与父容器的上侧对齐
			lp1.leftMargin=i*w+(w-60);
			lp1.topMargin=0;
			ImageView closeimage = new ImageView(this);
			closeimage.setTag(indextag);
			closeimage.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					int rpos=Integer.parseInt(v.getTag().toString());
					mPicturePathList.remove (rpos);
					for(int i=0;i<4;i++) {
						if (i >= rpos) {
							if(i==rpos) {
								final ImageViewSubClass currentImageButton = mImageButtonList.get(i);
								currentImageButton.setImageResource(R.mipmap.xz_jiji_icon);
								currentImageButton.setScaleType(ScaleType.CENTER);
								currentImageButton.setEnabled(true);
							}else
							{

								final ImageViewSubClass currentImageButton = mImageButtonList.get(i);
								currentImageButton.setImageResource(R.mipmap.xz_jiji_bg);
								currentImageButton.setScaleType(ScaleType.CENTER);
								currentImageButton.setEnabled(true);
							}
						}
					}
					for(int i=0;i<4;i++) {

						int perspos = rpos - 1;
						if (perspos ==i) {
							ImageView currentcloseImageButton = close_mImageButtonList.get (i);
							currentcloseImageButton.setEnabled (true);
							currentcloseImageButton.setVisibility (View.VISIBLE);
						}else
						{
							ImageView currentcloseImageButton = close_mImageButtonList.get (i);
							currentcloseImageButton.setEnabled (true);
							currentcloseImageButton.setVisibility (View.GONE);

						}
					}

				}
			});
			//closeimage.setBackgroundResource(R.drawable.row_selector);
			closeimage.setImageResource(R.mipmap.xz_quxiao_icon);
			closeimage.setEnabled(false);
			closeimage.setVisibility (View.GONE);
			closeimage.setScaleType (ImageView.ScaleType.FIT_XY);
			m_LinearLayout.addView(closeimage, lp1);
			// 将imageButton对象添加到列表
			mImageButtonList.add(image);
			close_mImageButtonList.add (closeimage);

		}
		mLayout.addView(m_LinearLayout, param);
		final ImageViewSubClass currentImageButton = mImageButtonList.get(mCurrent);
		currentImageButton.setImageResource(R.mipmap.xz_jiji_icon);
		currentImageButton.setScaleType(ScaleType.CENTER);
		currentImageButton.setEnabled(true);
	}

	public static void setMargins (View view, int left, int top, int right, int bottom) {
		if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
			ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
			p.setMargins(left, top, right, bottom);
			view.requestLayout();
		}
	}
	private void initbaidu(Resources resource, String pkgName) {
		// Push: 以apikey的方式登录，一般放在主Activity的onCreate中。
		// 这里把apikey存放于manifest文件中，只是一种存放方式，
		// 您可以用自定义常量等其它方式实现，来替换参数中的Utils.getMetaValue(PushDemoActivity.this,
		// "api_key")
		PushManager.startWork(this.myapplication,
				PushConstants.LOGIN_TYPE_API_KEY, DemoApplication.API_KEY);
		// Push: 如果想基于地理位置推送，可以打开支持地理位置的推送的开关
		// PushManager.enableLbs(getApplicationContext());

		// Push: 设置自定义的通知样式，具体API介绍见用户手册，如果想使用系统默认的可以不加这段代码
		// 请在通知推送界面中，高级设置->通知栏样式->自定义样式，选中并且填写值：1，
		// 与下方代码中 PushManager.setNotificationBuilder(this, 1, cBuilder)中的第二个参数对应
		/*
		 * CustomPushNotificationBuilder cBuilder = new
		 * CustomPushNotificationBuilder( this.getApplicationContext(),
		 * resource.getIdentifier( "notification_custom_builder", "layout",
		 * pkgName), resource.getIdentifier("notification_icon", "id", pkgName),
		 * resource.getIdentifier("notification_title", "id", pkgName),
		 * resource.getIdentifier("notification_text", "id", pkgName));
		 * cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
		 * cBuilder.setNotificationDefaults(Notification.DEFAULT_SOUND |
		 * Notification.DEFAULT_VIBRATE);
		 * cBuilder.setStatusbarIcon(this.getApplicationInfo().icon);
		 * cBuilder.setLayoutDrawable(resource.getIdentifier(
		 * "simple_notification_icon", "drawable", pkgName));
		 * PushManager.setNotificationBuilder(this, 1, cBuilder);
		 */
	}

	private void initlsb() {
		// TODO Auto-generated method stub
		mLocationClient = new LocationClient(this.myapplication);
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType("gcj02");// 返回的定位结果是百度经纬度，默认值gcj02
		int span = 1000;
		option.setScanSpan(span);// 设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
		mLocationClient.registerLocationListener(new BDLocationListener() {

			@Override
			public void onReceiveLocation(BDLocation location) {
				// TODO Auto-generated method stub
				if (location == null) {
					return;
				}
				nLatitude = location.getLatitude();
				nLontitude = location.getLongitude();
				Country = location.getCountry();
				Province = location.getProvince();
				City = location.getCity();
				CityCode = location.getCityCode();
				District = location.getDistrict();
				Street = location.getStreet();
				StreetNumber = location.getStreetNumber();
				Floor = location.getFloor();
				addr = location.getAddrStr();

				networklocationtype = location.getNetworkLocationType();
				operators = String.valueOf(location.getOperators());
				direction = String.valueOf(location.getDirection());
				radius = String.valueOf(location.getRadius());
				speed = String.valueOf(location.getSpeed());

				mLocationClient.stop();

				myapplication.setLat(String.valueOf(nLatitude));
				myapplication.setLng(String.valueOf(nLontitude));
				myapplication.updatelocation();

				Message msg = new Message();
				Bundle data = new Bundle();
				data.putDouble("nLatitude", nLatitude);
				data.putDouble("nLontitude", nLontitude);
				msg.setData(data);
				lbsfinshhandler.sendMessage(msg);
			}

		});
		mLocationClient.start();
		mLocationClient.requestLocation();
	}

	class LoadLbsThread implements Runnable {
		public void run() {
			Message message = new Message();
			message.what = 1;
			handler.sendMessage(message);
		}
	}

	Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					initlsb();
					break;
			}
			super.handleMessage(msg);
		}

	};

	Handler lbsfinshhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}
	};

	Runnable sendRun = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			int result;
			String content = content_edit.getText().toString();
			String fee="0";
			String target = myapplication.getlocalhost()+"send.php";
			HttpPost httprequest = new HttpPost(target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = sDateFormat.format(new java.util.Date());
			paramsList.add(new BasicNameValuePair("title", ""));// 标题为空
			paramsList.add(new BasicNameValuePair("content", content));// 正文
			paramsList.add(new BasicNameValuePair("senduser", myapplication.getUserId()));
			paramsList.add(new BasicNameValuePair("lat", String.valueOf(nLatitude)));
			paramsList.add(new BasicNameValuePair("lng", String.valueOf(nLontitude)));
			paramsList.add(new BasicNameValuePair("country", Country));
			paramsList.add(new BasicNameValuePair("province", Province));
			paramsList.add(new BasicNameValuePair("city", City));
			paramsList.add(new BasicNameValuePair("citycode", CityCode));
			paramsList.add(new BasicNameValuePair("district", District));
			paramsList.add(new BasicNameValuePair("street", Street));
			paramsList.add(new BasicNameValuePair("guid", UUID.randomUUID().toString()));
			paramsList.add(new BasicNameValuePair("infocatagroy", String.valueOf(infocatagroy)));
			paramsList.add(new BasicNameValuePair("fee", String.valueOf(fee)));
			/*统一压缩*/
			for (int i = 0; i < mPicturePathList.size(); i++) {
				if (mPicturePathList.get(i).length() > 0) {
					String localpicpath = mPicturePathList.get(i);
					ImageUtil imgutil=new ImageUtil();
					String compresslocalpicpath=imgutil.compressBmpToFile(getApplicationContext(),localpicpath,i);
					compmPicturePathList.add(compresslocalpicpath);
				}
			}
			/*用户目录+压缩后的文件名*/
			picPath = "";
			for (int i = 0; i < compmPicturePathList.size(); i++) {
				if (compmPicturePathList.get(i).length() > 0) {
					String path = compmPicturePathList.get(i);
					String picname ="/"+myapplication.getUserId()+"/"+path.substring(path.lastIndexOf("/") + 1);
					picPath = picPath + picname;
					if (i < compmPicturePathList.size() - 1) {
						picPath = picPath + ",";
					}
				}
			}
			paramsList.add(new BasicNameValuePair("photo", picPath));
			paramsList.add(new BasicNameValuePair("streetnumber", StreetNumber));
			paramsList.add(new BasicNameValuePair("village", ""));
			paramsList.add(new BasicNameValuePair("floor", Floor));
			paramsList.add(new BasicNameValuePair("address", addr));
			paramsList.add(new BasicNameValuePair("catagory", "1"));
			paramsList.add(new BasicNameValuePair("sendtime", date));
			paramsList.add(new BasicNameValuePair("networklocationtype",networklocationtype));
			paramsList.add(new BasicNameValuePair("operators", operators));
			paramsList.add(new BasicNameValuePair("direction", direction));
			paramsList.add(new BasicNameValuePair("radius", radius));
			paramsList.add(new BasicNameValuePair("speed", speed));
			try {
				UploadPic();
				httprequest.setEntity(new UrlEncodedFormEntity(paramsList,"UTF-8"));
				HttpClient HttpClient1 = CustomerHttpClient.getHttpClient();
				HttpResponse httpResponse = HttpClient1.execute(httprequest);
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					String json = EntityUtils.toString(httpResponse.getEntity());
					System.out.println(json);
					result = 1;
				} else {
					result = 0;
				}
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putInt("result", result);
				msg.setData(data);
				publishhandler.sendMessage(msg);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void UploadPic() throws FileNotFoundException {
			for (int i = 0; i < compmPicturePathList.size(); i++) {
				if (compmPicturePathList.get(i).length() > 0) {
					String localpicpath = compmPicturePathList.get(i).toString();
					String actionUrl = myapplication.getlocalhost()+"upload.php?user="+myapplication.getUserId();//存到指定文件夹
					//压缩文件
					upLoadByAsyncHttpClient(actionUrl, localpicpath);
					Message msg = new Message();
					msg.what=1;
					Bundle data = new Bundle();
					data.putString("tip", "上传"+new File(localpicpath).getName()+",（"+(i+1) + " / " + compmPicturePathList.size()+")");
					msg.setData(data);
					showtiphandler.sendMessage(msg);
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	};
	Handler publishhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			int result = data.getInt("result");
			Log.i("mylog", "请求结果-->" + result);
			if (result == 1) {
				content_edit.setText("");
				pbtip.setVisibility(View.GONE);
				pbtip1.setVisibility(View.GONE);
				pb.setVisibility(View.GONE);
				finish();
			} else {
				pbtip.setText("发送失败,重新发送");
				pbtip1.setText("");
				pb.setVisibility(View.GONE);
			}

		}
	};


	Handler showtiphandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what==1)
			{
				Bundle data = msg.getData();
				String tip = data.getString("tip");
				pbtip.setText(tip);
			}else if(msg.what==2)
			{
				Bundle data = msg.getData();
				String tip = data.getString("tip");
				pbtip1.setText(tip);

			}
		}
	};

	//private AsyncHttpClient client;
	private SyncHttpClient client;
	private void upLoadByAsyncHttpClient(String uploadUrl, String localpath)
			throws FileNotFoundException {
		AsyncBody(uploadUrl, localpath);
	}

	private void AsyncBody(String uploadUrl, String localpath)
			throws FileNotFoundException {
		RequestParams params = new RequestParams();
		client = new SyncHttpClient();
		params.put("uploadfile", new File(localpath));
		client.post(uploadUrl, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, String arg1) {
				super.onSuccess(arg0, arg1);
				Log.v("upload", arg1);
				//progress.setProgress(0);
				/*progressDialog.setProgress(0);
				progressDialog.dismiss();*/
				Message msg = new Message();
				msg.what=2;
				Bundle data = new Bundle();
				data.putString("tip", "已完成");
				msg.setData(data);
				showtiphandler.sendMessage(msg);
			}

			@Override
			public void onProgress(int bytesWritten, int totalSize) {
				// TODO Auto-generated method stub
				super.onProgress(bytesWritten, totalSize);
				int count = (int) ((bytesWritten * 1.0 / totalSize) * 100);
				// 上传进度显示
				//progressDialog.setProgress(count);
				Log.e("上传 Progress>>>>>", bytesWritten + " / " + totalSize);

				Message msg = new Message();
				msg.what=2;
				Bundle data = new Bundle();
				data.putString("tip", "已上传:"+bytesWritten + " / " + totalSize);
				msg.setData(data);
				showtiphandler.sendMessage(msg);
			}
		});
	}

	@Override
	public void onDestroy() {
		if (mDialog != null) {
			mDialog.dismiss();
		}
		super.onDestroy();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.v("onActivityResult:", requestCode + "," + resultCode);
		if (resultCode == Activity.RESULT_OK && requestCode == TO_SELECT_PHOTO1) {
			String selpicPath = data
					.getStringExtra(SelectPhotoActivity.KEY_PHOTO_PATH);
			String PATH_HOME = Environment.getExternalStorageDirectory().getPath()+"/temp/";
			File dir = new File(PATH_HOME);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			String targetPath = PATH_HOME + StringUtils.toRegularHashCode(selpicPath) + ".jpg";
			BitmapUtils.compressBitmap(selpicPath, targetPath, 640);
			mPicturePathList.add(targetPath);
			Bitmap bitmap = BitmapUtils.decodeBitmap(targetPath, 150);// 压缩大小
			//Bitmap newbitmap = BitmapUtils.GetRoundedCornerBitmap(bitmap);// 压缩大小


			Bundle Bundle1 = data.getExtras();
			int pos = Bundle1.getInt("pos");
			final ImageViewSubClass imageButton = mImageButtonList.get(pos);
			imageButton.setImageBitmap(bitmap);
			imageButton.setScaleType(ScaleType.FIT_XY);
			imageButton.setEnabled(false);
			if (pos < mImageButtonList.size() - 1) {
				mCurrent = pos + 1;
				final ImageViewSubClass nextImageButton = mImageButtonList
						.get(mCurrent);
				nextImageButton.setImageResource(R.mipmap.xz_jiji_icon);

				nextImageButton.setScaleType(ScaleType.CENTER);
				nextImageButton.setEnabled(true);
			}
			//
			for(int i=0;i<4;i++)
			{
				if(i==pos)
				{
					ImageView closeimageButton=close_mImageButtonList.get (i);
					closeimageButton.setVisibility (View.VISIBLE);
					closeimageButton.setEnabled (true);

				}else
				{
					ImageView closeimageButton=close_mImageButtonList.get (i);
					closeimageButton.setVisibility (View.GONE);
					closeimageButton.setEnabled (false);
				}
			}

		}
	}


	private void print(String msg) {
		// txtLog.append(msg + "\n");
		// ScrollView sv = (ScrollView) txtLog.getParent();
		// sv.smoothScrollTo(0, 1000000);
		Log.d(TAG, "----" + msg);
	}



	public void doBack(View view) {
		onBackPressed();
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.yybtn:
				///content_edit.setText(null);
//	                if (mDialog == null || mCurrentTheme != Config.DIALOG_THEME) {
				mCurrentTheme = Config.DIALOG_THEME;
				if (mDialog != null) {
					mDialog.dismiss();
				}
				Bundle params = new Bundle();
				params.putString(BaiduASRDigitalDialog.PARAM_API_KEY, Constants.API_KEY);
				params.putString(BaiduASRDigitalDialog.PARAM_SECRET_KEY, Constants.SECRET_KEY);
				params.putInt(BaiduASRDigitalDialog.PARAM_DIALOG_THEME, Config.DIALOG_THEME);
				mDialog = new BaiduASRDigitalDialog(this, params);
				mDialog.setDialogRecognitionListener(mRecognitionListener);
//	                }
				mDialog.getParams().putInt(BaiduASRDigitalDialog.PARAM_PROP, Config.CURRENT_PROP);
				mDialog.getParams().putString(BaiduASRDigitalDialog.PARAM_LANGUAGE,
						Config.getCurrentLanguage());
				Log.e("DEBUG", "Config.PLAY_START_SOUND = "+Config.PLAY_START_SOUND);
				mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_START_TONE_ENABLE, Config.PLAY_START_SOUND);
				mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_END_TONE_ENABLE, Config.PLAY_END_SOUND);
				mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_TIPS_TONE_ENABLE, Config.DIALOG_TIPS_SOUND);
				mDialog.show();
				break;
			default:
				break;
		}

	}
}