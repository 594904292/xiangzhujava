package com.bbxiaoqu.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.ImageOptions;
import com.bbxiaoqu.R;
import com.bbxiaoqu.Session;
import com.bbxiaoqu.api.ApiAsyncTask;
import com.bbxiaoqu.api.MarketAPI;
import com.bbxiaoqu.comm.adapter.ReportsAdapter;
import com.bbxiaoqu.comm.service.db.UserService;
import com.bbxiaoqu.comm.tool.CustomerHttpClient;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.StreamTool;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.comm.widget.XCRoundImageView;
import com.bbxiaoqu.ui.InfoPlActivity;
import com.bbxiaoqu.ui.ListConvenienceActivity;
import com.bbxiaoqu.ui.ListConvenienceMyActivity;
import com.bbxiaoqu.ui.LoadingView;
import com.bbxiaoqu.ui.LoginActivity;
import com.bbxiaoqu.ui.ListInfoActivity;
import com.bbxiaoqu.ui.FriendsActivity;
import com.bbxiaoqu.ui.InfoGzActivity;
import com.bbxiaoqu.comm.widget.RoundAngleImageView;
import com.bbxiaoqu.ui.SearchCxhfdmActivity;
import com.bbxiaoqu.ui.SettingsActivity;
import com.bbxiaoqu.ui.config.Constants;
import com.bbxiaoqu.ui.report.SwitchButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

public class FragmentPage4 extends Fragment implements ApiAsyncTask.ApiRequestListener {
	private DemoApplication myapplication;
	private final String TAG = "FragmentPage4";
	private AsyncHttpClient client;

	TextView title;
	TextView brithday_tv;
	TextView sex_tv;
	TextView telphone_tv;
	TextView weixin_tv;
	TextView xiaoqu_tv;
	TextView emergencycontact_tv;
	private TextView username;
	private TextView my_nickname;
	private TextView my_userid;
	private TextView  my_score;
	private String headfacepath = "";
	private String headfacename = "";

	private String load_score="";
	private String load_userid="";
	String load_username = "";
	String load_age = "";
	String load_sex = "";
	String load_telphone = "";
	String load_weixinstr="";
	String load_remote_headface = "";
	String load_emergency="";
	String load_emergencytelphone="";

	String load_xiaoqu="";
	String load_xiaoquid="";

	public ImageView top_more;
	/** ImageView对象 */
	private ImageView iv_photo;
	private RoundAngleImageView headface;
	private String[] items = new String[] { "选择本地图片", "拍照" };
	/** 头像名称 */
	private static final String IMAGE_FILE_NAME = "image.jpg";
	/** 请求码 */
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;

	public static final int RESULT_CANCELED    = 0;



	public static final int SEL_XIAOQU    = 99;


	//获取日期格式器对象
	DateFormat fmtDateAndTime =  new SimpleDateFormat("yyyy-MM-dd");
	//定义一个TextView控件对象
	TextView dateAndTimeLabel = null;
	//获取一个日历对象
	Calendar dateAndTime = Calendar.getInstance(Locale.CHINA);

	View view;
	UserService uService;
	Activity myactivity;
	//final Calendar c = Calendar.getInstance();
	int selectedIndex=0;
	protected Session mSession;

	String load_brithdaystr = "";
	String return_username="";
	String return_brithday="";
	String return_age="";
	String return_sex="";
	String return_telphone="";
	String return_weixin="";
	String return_emergency;
	String return_emergency_telphone;

	private IWXAPI wxApi;
	private void regToWx()
	{
		wxApi = WXAPIFactory.createWXAPI(myactivity, Constants.WX_APP_ID);
		wxApi.registerApp(Constants.WX_APP_ID);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		view= inflater.inflate(R.layout.fragment_4, null);
		myapplication = (DemoApplication) this.getActivity().getApplication();
		uService=new UserService(myapplication);
		myactivity=this.getActivity ();
		mSession = Session.get(myactivity);
		regToWx();
		initView();
		initData();
		return view;
	}


	AlertDialog nicknamedlg;
	AlertDialog sexdlg;
	AlertDialog sharedlg;
	AlertDialog brithdaydlg;
	AlertDialog telphonedlg;
	AlertDialog weixindlg;
	AlertDialog emergencydlg;
	public static final void dialogTitleLineColor(Dialog dialog, int color) {
		Context context = dialog.getContext();
		int divierId = context.getResources().getIdentifier("android:id/titleDivider", null, null);
		View divider = dialog.findViewById(divierId);
		//divider.setBackgroundColor (Color.rgb (0xb0,0xb0,0xb0));
		if(divider!=null) {
			divider.setBackgroundColor (Color.argb (1, 0xb0, 0xb0, 0xb0));
		}
		//divider.setBackgroundColor(color);
	}
	private void initView() {
		title = (TextView) view.findViewById(R.id.title);
		my_nickname = (TextView) view.findViewById(R.id.my_nickname);
		my_userid = (TextView) view.findViewById(R.id.my_userid);
		my_score = (TextView) view.findViewById(R.id.score);
		username = (TextView) view.findViewById(R.id.username);
		brithday_tv= (TextView) view.findViewById(R.id.brithday_tv);
		sex_tv= (TextView)view.findViewById(R.id.sex_tv);
		telphone_tv = (TextView) view.findViewById(R.id.telphone_tv);
		weixin_tv = (TextView) view.findViewById(R.id.weixin_tv);
		xiaoqu_tv = (TextView) view.findViewById(R.id.xiaoqu_tv);
		emergencycontact_tv=  (TextView) view.findViewById(R.id.emergencycontact_tv);
		headface= (RoundAngleImageView)view.findViewById(R.id.headface);
		iv_photo = (ImageView)view.findViewById(R.id.iv_photo);

		RelativeLayout tag1_view=(RelativeLayout) view.findViewById(R.id.tag1_view);
		tag1_view.setOnClickListener(new View.OnClickListener () {
			public void onClick(View v) {
				Intent intent =new Intent(myactivity,ListInfoActivity.class);
				//用Bundle携带数据
				Bundle bundle=new Bundle();
				//传递name参数为tinyphp
				bundle.putInt ("page",2);
				intent.putExtras(bundle);
				myactivity.startActivity(intent);
				myactivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			}
		});

		RelativeLayout tag3_view=(RelativeLayout) view.findViewById(R.id.tag3_view);
		tag3_view.setOnClickListener(new View.OnClickListener () {
			public void onClick(View v) {
				myactivity.startActivity(new Intent(myactivity,FriendsActivity.class));
				myactivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			 }
		});

		RelativeLayout tag5_view=(RelativeLayout) view.findViewById(R.id.tag5_view);
		tag5_view.setOnClickListener(new View.OnClickListener () {
			public void onClick(View v) {
				myactivity.startActivity(new Intent(myactivity,InfoGzActivity.class));
				myactivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			}
		});

		RelativeLayout tag6_view=(RelativeLayout) view.findViewById(R.id.tag6_view);
		tag6_view.setOnClickListener(new View.OnClickListener () {
			public void onClick(View v) {
				myactivity.startActivity(new Intent(myactivity, InfoPlActivity.class));
				myactivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			}
		});

		LinearLayout update_headface=(LinearLayout)view.findViewById (R.id.update_headface);
		LinearLayout update_nickname=(LinearLayout)view.findViewById (R.id.update_nickname);
		LinearLayout update_brithday=(LinearLayout)view.findViewById (R.id.update_brithday);
		LinearLayout update_sex=(LinearLayout)view.findViewById (R.id.update_sex);
		LinearLayout update_tel=(LinearLayout)view.findViewById (R.id.update_tel);
		LinearLayout update_weixin=(LinearLayout)view.findViewById (R.id.update_weixin);
		LinearLayout update_othertel=(LinearLayout)view.findViewById (R.id.update_othertel);
		LinearLayout update_xiaoqu=(LinearLayout)view.findViewById (R.id.update_xiaoqu);
		LinearLayout update_recmess=(LinearLayout)view.findViewById (R.id.update_recmess);
		LinearLayout update_setting=(LinearLayout)view.findViewById (R.id.update_setting);
		LinearLayout update_share=(LinearLayout)view.findViewById (R.id.update_share);
		LinearLayout update_shop=(LinearLayout)view.findViewById (R.id.update_shop);
		LinearLayout exit=(LinearLayout)view.findViewById (R.id.exit);
		update_headface.setOnClickListener(new View.OnClickListener () {
			public void onClick(View v) {
				showDialog();
			}
		});
		update_nickname.setOnClickListener(new View.OnClickListener () {
			public void onClick(View v) {
				LayoutInflater factory = LayoutInflater.from(myactivity);
				final View textEntryView = factory.inflate(R.layout.dialog, null);
				if(nicknamedlg==null) {
					final EditText username_edit = (EditText) textEntryView.findViewById (R.id.dialog_username_edit);
					username_edit.setHint ("最多不超过7个字");
					nicknamedlg = new AlertDialog.Builder (  new ContextThemeWrapper (myactivity, R.style.PopDialog))
							.setTitle ("用户昵称")
							.setView (textEntryView)
							.setPositiveButton ("确定", new DialogInterface.OnClickListener () {
								public void onClick(DialogInterface dialog, int whichButton) {
									new AlertDialog.Builder(myactivity).setTitle("确认修改吗？")
											.setIcon(android.R.drawable.ic_dialog_info)
											.setPositiveButton("确定", new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
													// 点击“确认”后的操作
													return_username = username_edit.getText ().toString ();
													new Thread (UpdateNicknameThread).start ();
													dialog.dismiss ();
												}
											})
											.setNegativeButton("返回", new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
													// 点击“返回”后的操作,这里不设置没有任何操作
												}
											}).show();
									nicknamedlg.dismiss ();
								}
							})
							.setNegativeButton ("取消", new DialogInterface.OnClickListener () {
								public void onClick(DialogInterface dialog, int whichButton) {
									System.out.println ("-------------->2");
								}
							})
							.create ();
					View mTitleView = factory.inflate(R.layout.title, null);
					TextView mtxtPatient=(TextView) mTitleView.findViewById (R.id.txtPatient);
					mtxtPatient.setText ("用户昵称");
					nicknamedlg.setCustomTitle(mTitleView);
				}
				nicknamedlg.show();
				dialogTitleLineColor(nicknamedlg,R.color.gray_light);
			}
		});

		update_brithday.setOnClickListener(new View.OnClickListener () {
			public void onClick(View v) {
				String str="1979-01-01";
				if(brithday_tv.getText().length()>8)
				{
					str=brithday_tv.getText().toString();
				}
				SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
				Date date = null;
				try {
					date = sdf.parse(str);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				//初始化Calendar日历对象
				Calendar mycalendar=Calendar.getInstance(Locale.CHINA);
				mycalendar.setTime(date);
				int  year=mycalendar.get(Calendar.YEAR); //获取Calendar对象中的年
				int  month=mycalendar.get(Calendar.MONTH);//获取Calendar对象中的月
				int  day=mycalendar.get(Calendar.DAY_OF_MONTH);//获取这个月的第几天
				DatePickerDialog dialog = new DatePickerDialog(myactivity, new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
						Calendar c = Calendar.getInstance();
						c.set(year, monthOfYear, dayOfMonth);
						String pattern="yyyy-MM-dd";
						DateFormat dateFormat=new SimpleDateFormat(pattern);
						Date date=c.getTime ();
						brithday_tv.setText (dateFormat.format (date));

						DateFormat fmtDateAndTime1 =  new SimpleDateFormat("yyyy");
						String agea=fmtDateAndTime1.format(date.getTime());
						String ageb=fmtDateAndTime1.format(new Date().getTime());
						int agecle=Integer.parseInt(ageb)-Integer.parseInt(agea);

						return_brithday=dateFormat.format (date);
						return_age=String.valueOf (agecle);
						new AlertDialog.Builder(myactivity).setTitle("确认修改吗？")
								.setIcon(android.R.drawable.ic_dialog_info)
								.setPositiveButton("确定", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// 点击“确认”后的操作
										new Thread(UpdateBrithdayThread).start();
										dialog.dismiss ();
									}
								})
								.setNegativeButton("返回", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// 点击“返回”后的操作,这里不设置没有任何操作
									}
								}).show();
					}
				}, year, month, day);
				dialog.show();
			}
		});

		update_sex.setOnClickListener(new View.OnClickListener () {
			public void onClick(View v) {
				LayoutInflater factory = LayoutInflater.from(myactivity);
				if(sexdlg==null) {
					sexdlg = new AlertDialog.Builder (myactivity)
							.setTitle ("性别")
							.setPositiveButton ("确定", new DialogInterface.OnClickListener () {

								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									Toast.makeText (myactivity,
											arrayFruit[selectedIndex], Toast.LENGTH_SHORT)
											.show ();
									sex_tv.setText (arrayFruit[selectedIndex]);
									return_sex = String.valueOf (selectedIndex);
									new AlertDialog.Builder(myactivity).setTitle("确认修改吗？")
											.setIcon(android.R.drawable.ic_dialog_info)
											.setPositiveButton("确定", new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
													// 点击“确认”后的操作
													new Thread (UpdatSexThread).start ();
													dialog.dismiss ();
												}
											})
											.setNegativeButton("返回", new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
													// 点击“返回”后的操作,这里不设置没有任何操作
												}
											}).show();
								}
							})
							//this ;
							.setSingleChoiceItems (arrayFruit, 0,
									new DialogInterface.OnClickListener () {

										public void onClick(DialogInterface dialog,
															int which) {
											// TODO Auto-generated method stub
											selectedIndex = which;
										}
									})
							.setNegativeButton ("取消", new DialogInterface.OnClickListener () {

								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub

								}
							}).create ();
							View mTitleView = factory.inflate(R.layout.title, null);
							TextView mtxtPatient=(TextView) mTitleView.findViewById (R.id.txtPatient);
							mtxtPatient.setText ("性别");
							sexdlg.setCustomTitle(mTitleView);
				}
				sexdlg.show();
				dialogTitleLineColor(sexdlg,R.color.gray_light);
			}
		});

		update_tel.setOnClickListener(new View.OnClickListener () {
			public void onClick(View v) {
				LayoutInflater factory = LayoutInflater.from(myactivity);
				final View Tel_EntryView = factory.inflate(R.layout.dialog, null);
				if(telphonedlg==null) {
					final EditText username_edit = (EditText) Tel_EntryView.findViewById (R.id.dialog_username_edit);
					username_edit.setHint ("请输入你的电话号码");
					telphonedlg = new AlertDialog.Builder (  new ContextThemeWrapper (myactivity, R.style.PopDialog))
							.setTitle ("电话")
							.setView (Tel_EntryView)
							.setPositiveButton ("保存", new DialogInterface.OnClickListener () {
								public void onClick(DialogInterface dialog, int whichButton) {
									System.out.println ("-------------->6");
									EditText edit = (EditText) Tel_EntryView.findViewById (R.id.dialog_username_edit);
									T.showShort (myactivity, edit.getText ().toString ());
									telphone_tv.setText (edit.getText ());
									return_telphone = edit.getText ().toString ();

									new AlertDialog.Builder(myactivity).setTitle("确认修改吗？")
											.setIcon(android.R.drawable.ic_dialog_info)
											.setPositiveButton("确定", new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
													// 点击“确认”后的操作
													new Thread (UpdatTelphoeThread).start ();
													dialog.dismiss ();
												}
											})
											.setNegativeButton("返回", new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
													// 点击“返回”后的操作,这里不设置没有任何操作
												}
											}).show();

								}
							})
							.setNegativeButton ("取消", new DialogInterface.OnClickListener () {
								public void onClick(DialogInterface dialog, int whichButton) {
									System.out.println ("-------------->2");

								}
							})
							.create ();
					View mTitleView = factory.inflate(R.layout.title, null);
					TextView mtxtPatient=(TextView) mTitleView.findViewById (R.id.txtPatient);
					mtxtPatient.setText ("电话");
					telphonedlg.setCustomTitle(mTitleView);
				}
				telphonedlg.show();
				dialogTitleLineColor(telphonedlg,R.color.gray_light);
			}
		});

		update_weixin.setOnClickListener(new View.OnClickListener () {
			public void onClick(View v) {
				LayoutInflater factory = LayoutInflater.from(myactivity);
				final View weixin_EntryView = factory.inflate(R.layout.dialog, null);
				if(weixindlg==null) {
					weixindlg = new AlertDialog.Builder (  new ContextThemeWrapper (myactivity, R.style.PopDialog))
							.setTitle ("微信")
							.setView (weixin_EntryView)
							.setPositiveButton ("保存", new DialogInterface.OnClickListener () {
								public void onClick(DialogInterface dialog, int whichButton) {
									System.out.println ("-------------->6");
									EditText edit = (EditText) weixin_EntryView.findViewById (R.id.dialog_username_edit);
									T.showShort (myactivity, edit.getText ().toString ());
									weixin_tv.setText (edit.getText ());
									return_weixin = edit.getText ().toString ();

									new AlertDialog.Builder(myactivity).setTitle("确认修改吗？")
											.setIcon(android.R.drawable.ic_dialog_info)
											.setPositiveButton("确定", new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
													// 点击“确认”后的操作
													new Thread (UpdatWeixinThread).start ();
													dialog.dismiss ();
												}
											})
											.setNegativeButton("返回", new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
													// 点击“返回”后的操作,这里不设置没有任何操作
												}
											}).show();

								}
							})
							.setNegativeButton ("取消", new DialogInterface.OnClickListener () {
								public void onClick(DialogInterface dialog, int whichButton) {
									System.out.println ("-------------->2");

								}
							})
							.create ();
					View mTitleView = factory.inflate(R.layout.title, null);
					TextView mtxtPatient=(TextView) mTitleView.findViewById (R.id.txtPatient);
					mtxtPatient.setText ("微信");
					weixindlg.setCustomTitle(mTitleView);
				}
					weixindlg.show();
				dialogTitleLineColor(weixindlg,R.color.gray_light);
			}
		});

		update_xiaoqu.setOnClickListener(new View.OnClickListener () {
			public void onClick(View v) {

				Intent intent = new Intent(myactivity, SearchCxhfdmActivity.class);
				startActivityForResult(intent, SEL_XIAOQU);// 请求码
			}
		});

		update_othertel.setOnClickListener(new View.OnClickListener () {
			public void onClick(View v) {
				LayoutInflater factory = LayoutInflater.from(myactivity);
				final View EntryView = factory.inflate(R.layout.dialog_emergency, null);
				EditText edit1 = (EditText) EntryView.findViewById(R.id.dialog_edit1);
				EditText edit2 = (EditText) EntryView.findViewById(R.id.dialog_edit2);
				edit1.setHint ("紧急联系人名称");
				edit2.setHint ("紧急联系人电话号码");
				if(emergencydlg==null) {
					emergencydlg = new AlertDialog.Builder (  new ContextThemeWrapper (myactivity, R.style.PopDialog))
							.setTitle ("紧急联系人设置")
							.setView (EntryView)
							.setPositiveButton ("保存", new DialogInterface.OnClickListener () {
								public void onClick(DialogInterface dialog, int whichButton) {
									EditText edit1 = (EditText) EntryView.findViewById (R.id.dialog_edit1);
									EditText edit2 = (EditText) EntryView.findViewById (R.id.dialog_edit2);
									return_emergency = edit1.getText ().toString ();
									return_emergency_telphone = edit2.getText ().toString ();
									emergencycontact_tv.setText (return_emergency);
									new AlertDialog.Builder(myactivity).setTitle("确认修改吗？")
											.setIcon(android.R.drawable.ic_dialog_info)
											.setPositiveButton("确定", new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
													new Thread (UpdatEmergencyThread).start ();
													dialog.dismiss ();
												}
											})
											.setNegativeButton("返回", new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
													// 点击“返回”后的操作,这里不设置没有任何操作
												}
											}).show();
								}
							})
							.setNegativeButton ("取消", new DialogInterface.OnClickListener () {
								public void onClick(DialogInterface dialog, int whichButton) {
									System.out.println ("-------------->2");
								}
							})
							.create ();
					View mTitleView = factory.inflate(R.layout.title, null);
					TextView mtxtPatient=(TextView) mTitleView.findViewById (R.id.txtPatient);
					mtxtPatient.setText ("紧急联系人");
					emergencydlg.setCustomTitle(mTitleView);
				}
				emergencydlg.show();
				dialogTitleLineColor(emergencydlg,R.color.gray_light);
			}
		});

		SwitchButton switch_recmess=(SwitchButton)view.findViewById (R.id.switch_recmess);
		switch_recmess.setOnChangeListener(new SwitchButton.OnChangeListener () {

			@Override
			public void onChange(SwitchButton sb, boolean state) {
				// TODO Auto-generated method stub
				if(state)
				{
					mSession.setIsNotic (true);
				}else {
					mSession.setIsNotic (false);
				}
				Log.d("switchButton", state ? "开":"关");
				Toast.makeText(myactivity, state ? "开":"关", Toast.LENGTH_SHORT).show();
			}
		});

		update_setting.setOnClickListener(new View.OnClickListener () {
			public void onClick(View v) {
				myactivity.startActivity(new Intent(myactivity,SettingsActivity.class));
				myactivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			}
		});

		update_share.setOnClickListener(new View.OnClickListener () {
			public void onClick(View v) {
				//wechatShare(0);//分享到微信好友
				//wechatShare(1);//分享到微信朋友圈
				LayoutInflater factory = LayoutInflater.from(myactivity);
				if(sharedlg==null) {
					sharedlg = new AlertDialog.Builder (myactivity)
							.setTitle ("分享到")
							.setItems (shareitem,
									new DialogInterface.OnClickListener () {

										public void onClick(DialogInterface dialog,int which) {
											// TODO Auto-generated method stub
											if(which==0)
											{
												wechatShare(0);//分享到微信好友
											}else
											{
												wechatShare(1);//分享到微信朋友圈
											}
										}
									})
							.setNegativeButton ("取消", new DialogInterface.OnClickListener () {
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
								}
							}).create ();
					View mTitleView = factory.inflate(R.layout.title, null);
					TextView mtxtPatient=(TextView) mTitleView.findViewById (R.id.txtPatient);
					mtxtPatient.setText ("分享到:");
					sharedlg.setCustomTitle(mTitleView);
				}
				sharedlg.show();
				dialogTitleLineColor(sharedlg,R.color.gray_light);
			}
		});

		update_shop.setOnClickListener(new View.OnClickListener () {
			public void onClick(View v) {
				myactivity.startActivity(new Intent(myactivity,ListConvenienceMyActivity.class));
				myactivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			}
		});

		exit.setOnClickListener(new View.OnClickListener () {
			public void onClick(View v) {
				myapplication.exit ();
				myactivity.startActivity(new Intent(myactivity,LoginActivity.class));
				myactivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			}
		});
	}

	private void wechatShare(int flag){

		if (!wxApi.isWXAppInstalled()) {
			Toast.makeText(this.getActivity(), "您还未安装微信客户端",
					Toast.LENGTH_SHORT).show();
			return;
		}
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = "http://www.bbxiaoqu.com/wap/about.html";
		WXMediaMessage msg = new WXMediaMessage(webpage);
		//TextView content = (TextView) findViewById(R.id.info_content);
		msg.title = "襄助何必曾相识";
		msg.description = "襄助是基于位置的是传播正能量的联网互助平台。让附近的人互相帮忙，我们希望把大众的力量组织起来，有一技之长的人可以通过“襄助”为附近的人提供帮助；普通大众可以通过“襄助” 快速寻求帮助。 “涓滴之水成海洋，颗颗爱心变希望”。";
		//这里替换一张自己工程里的图片资源
			String thumb="http://www.bbxiaoqu.com/pc/img/qrcode.png";
			Bitmap bmp=ImageLoader.getInstance().loadImageSync(thumb);
			//msg.setThumbImage(bitmap);
			Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 100, 100, true);
			bmp.recycle();
			msg.setThumbImage(thumbBmp);
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = msg;
		req.scene = flag==0?SendMessageToWX.Req.WXSceneSession:SendMessageToWX.Req.WXSceneTimeline;
		wxApi.sendReq(req);
	}


	final String[] arrayFruit = new String[] {"男","女"};
	final String[] shareitem = new String[] { "微信好友", "微信朋友圈"};
	private void initData() {
		if (!NetworkUtils.isNetConnected(myapplication)) {
			T.showShort(myapplication, "当前无网络连接,请稍后再试！");
			return;
		}
		MarketAPI.getUserInfo(myapplication, this,myapplication.getUserId());
		MarketAPI.getUserSummaryInfo(myapplication, this,myapplication.getUserId());

	}
	/**
	 * 显示选择对话框
	 */
	private void showDialog() {
		new AlertDialog.Builder(this.getActivity ())
				.setTitle("设置头像")
				.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							case 0:
								Intent intentFromGallery = new Intent();
								intentFromGallery.setType("image/*"); // 设置文件类型
								intentFromGallery
										.setAction(Intent.ACTION_GET_CONTENT);
								startActivityForResult(intentFromGallery,
										IMAGE_REQUEST_CODE);
								break;
							case 1:
								Intent intentFromCapture = new Intent(
										MediaStore.ACTION_IMAGE_CAPTURE);
								// 判断存储卡是否可以用，可用进行存储
								String state = Environment
										.getExternalStorageState();
								if (state.equals(Environment.MEDIA_MOUNTED)) {
									File path = Environment
											.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
									File file = new File(path, IMAGE_FILE_NAME);
									intentFromCapture.putExtra(
											MediaStore.EXTRA_OUTPUT,
											Uri.fromFile(file));
								}
								startActivityForResult(intentFromCapture,
										CAMERA_REQUEST_CODE);
								break;
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();

	}





	Handler publishhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what==1) {
				Bundle data = msg.getData();
				int result = data.getInt("result");
				Log.i("mylog", "请求结果-->" + result);
				if (result == 1) {
					Toast.makeText(myapplication, "更新成功",
							Toast.LENGTH_SHORT).show();
				} else {
					// Toast.makeText(SendFragment.this,
					// "推送失败",Toast.LENGTH_SHORT).show();
				}
			}
			if(msg.what==2) {
				Toast.makeText(myapplication, "昵称已存在",Toast.LENGTH_SHORT).show();
				username.setText("");
			}
			if(msg.what==11) {
				Toast.makeText(myapplication, "头像更新成功",Toast.LENGTH_SHORT).show();
			}
			if(msg.what==12) {
				TextView my_nickname_tv = (TextView) view.findViewById(R.id.my_nickname);
				TextView username_tv = (TextView) view.findViewById(R.id.username);
				my_nickname_tv.setText (return_username);
				username_tv.setText (return_username);
				Toast.makeText(myapplication, "昵称更新成功",Toast.LENGTH_SHORT).show();
			}
			if(msg.what==13) {
				Toast.makeText(myapplication, "生日更新成功",Toast.LENGTH_SHORT).show();
			}
			if(msg.what==14) {
				Toast.makeText(myapplication, "性别更新成功",Toast.LENGTH_SHORT).show();
			}

			if(msg.what==15) {
				Toast.makeText(myapplication, "电话更新成功",Toast.LENGTH_SHORT).show();
			}

			if(msg.what==16) {
				Toast.makeText(myapplication, "微信更新成功",Toast.LENGTH_SHORT).show();
			}

			if(msg.what==17) {
				Toast.makeText(myapplication, "紧急联系人更新成功",Toast.LENGTH_SHORT).show();
			}
			if(msg.what==18) {
				Toast.makeText(myapplication, "所属小区更新成功",Toast.LENGTH_SHORT).show();
			}
		}
	};
	Handler laodhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage (msg);
			if (msg.what == 1) {
				Bundle data = msg.getData ();
				my_nickname.setText (data.getString ("username"));
				my_userid.setText (data.getString ("userid"));
				my_score.setText ("积分:"+data.getString ("score"));
				username.setText (data.getString ("username"));
				brithday_tv.setText (data.getString ("brithday"));
				telphone_tv.setText (data.getString ("telphone"));
				weixin_tv.setText (data.getString ("weixin"));
				xiaoqu_tv.setText(data.getString ("xiaoqu"));
				if(data.getString ("sex").equals ("0"))
				{
					sex_tv.setText ("男");
				}else
				{
					sex_tv.setText ("女");

				}
				if (data.getString ("headface") != null && data.getString ("headface").length () > 0) {
					String fileName = myapplication.getlocalhost () + "uploads/" + data.getString ("headface");
					ImageLoader.getInstance ().displayImage (fileName, iv_photo, ImageOptions.getOptions ());
					ImageLoader.getInstance ().displayImage (fileName, headface, ImageOptions.getOptions ());
				}
				if (load_emergency != null && load_emergency.length () > 0) {
					emergencycontact_tv.setText (load_emergency);
				} else {
					emergencycontact_tv.setText ("未设置");
				}

			}else if (msg.what == 2) {//
				Bundle data = msg.getData ();

				TextView tag1_top = (TextView) view.findViewById(R.id.tag1_top);
				tag1_top.setText (data.getString ("load_num1"));

				TextView tag3_top = (TextView) view.findViewById(R.id.tag3_top);
				tag3_top.setText (data.getString ("load_num3"));

				TextView tag5_top = (TextView) view.findViewById(R.id.tag5_top);
				tag5_top.setText (data.getString ("load_num5"));


				TextView tag6_top = (TextView) view.findViewById(R.id.tag6_top);
				tag6_top.setText (data.getString ("load_num6"));
			}
		}
	};



	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 结果码不等于取消时候
		if (resultCode != RESULT_CANCELED) {
			switch (requestCode) {
				case IMAGE_REQUEST_CODE:
					startPhotoZoom(data.getData());
					break;
				case CAMERA_REQUEST_CODE:
					// 判断存储卡是否可以用，可用进行存储
					String state = Environment.getExternalStorageState();
					if (state.equals(Environment.MEDIA_MOUNTED)) {
						File path = Environment
								.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
						File tempFile = new File(path, IMAGE_FILE_NAME);
						startPhotoZoom(Uri.fromFile(tempFile));
					} else {
						Toast.makeText(this.getActivity (), "未找到存储卡，无法存储照片！",
								Toast.LENGTH_SHORT).show();
					}
					break;
				case RESULT_REQUEST_CODE: // 图片缩放完成后
					if (data != null) {
						getImageToView(data);
					}
					break;
				case SEL_XIAOQU: // 选择小区
					if (data != null) {
						load_xiaoquid = data.getStringExtra("code");
						load_xiaoqu = data.getStringExtra("name");
						new AlertDialog.Builder(this.getActivity()).setTitle("确认修改为\""+load_xiaoqu+"\"吗？")
								.setIcon(android.R.drawable.ic_dialog_info)
								.setPositiveButton("确定", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// 点击“确认”后的操作
										xiaoqu_tv.setText(load_xiaoqu);
										mSession.setXiaoquid(load_xiaoquid);
										mSession.setXiaoquname(load_xiaoqu);
										new Thread (UpdatXiaoquThread).start ();
										dialog.dismiss ();
									}
								})
								.setNegativeButton("返回", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// 点击“返回”后的操作,这里不设置没有任何操作
									}
								}).show();
					}
					break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 裁剪图片方法实现
	 *
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 340);
		intent.putExtra("outputY", 340);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, RESULT_REQUEST_CODE);
	}

	/**
	 * 保存裁剪之后的图片数据
	 *
	 */
	private void getImageToView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			photo= ThumbnailUtils.extractThumbnail(photo, 150, 150);
			java.text.DateFormat format2 = new java.text.SimpleDateFormat(
					"yyyyMMddHHmmss");
			headfacename = myapplication.getUserId()+"_"+format2.format(new Date()) + ".jpg";
			headfacepath = saveBitmap(photo, headfacename);
			Drawable drawable = new BitmapDrawable (this.getResources(), photo);
			iv_photo.setImageDrawable(drawable);
			headface.setImageDrawable (drawable);

			//上传头像

			String target1 = myapplication.getlocalhost()+"upload.php";
			if(headfacepath!=null&&headfacepath.length()>0)
			{
				try {
					upLoadByAsyncHttpClient(target1);
				} catch (FileNotFoundException e) {
					e.printStackTrace ();
				}
			}
			//更新域
			new Thread(UpdateHeadfaceThread).start();
		}
	}
	private String saveBitmap(Bitmap imgThumb, String fileName) {
		// TODO Auto-generated method stub
		FileOutputStream out = null;
		File yygypath = this.getActivity ().getFilesDir();// this.getCacheDir();
		String yygypathstr = yygypath.toString();
		try {
			out = new FileOutputStream(yygypathstr + "/" + fileName);
			imgThumb.compress(Bitmap.CompressFormat.PNG, 90, out);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (Throwable ignore) {
			}
		}
		return yygypathstr + "/" + fileName;
	}



	private void upLoadByAsyncHttpClient(String uploadUrl)
			throws FileNotFoundException {
		AsyncBody(uploadUrl, headfacepath);

	}

	private void AsyncBody(String uploadUrl, String localpath)
			throws FileNotFoundException {
		RequestParams params = new RequestParams();
		client = new AsyncHttpClient();
		params.put("uploadfile", new File (localpath));
		client.post(uploadUrl, params, new AsyncHttpResponseHandler () {
			@Override
			public void onSuccess(int arg0, String arg1) {
				super.onSuccess(arg0, arg1);
				Log.i(TAG, arg1);
			}
		});
	}

	/*
	* 更新头像
	* */
	Runnable UpdateHeadfaceThread = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int result;
			String target = myapplication.getlocalhost()+"updateuserfield.php";
			HttpPost httprequest = new HttpPost(target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			paramsList.add(new BasicNameValuePair("userid", myapplication.getUserId()));
			paramsList.add(new BasicNameValuePair("field", "headface"));
			paramsList.add(new BasicNameValuePair("fieldvalue", headfacename));
			HttpClient HttpClient1;
			HttpResponse httpResponse = null;
			try {
				httprequest.setEntity(new UrlEncodedFormEntity(paramsList,"UTF-8"));
				HttpClient1 = CustomerHttpClient.getHttpClient();
				httpResponse = HttpClient1.execute(httprequest);
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					result = 1;
				} else {
					result = 0;
				}
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putInt("result", result);
				msg.setData(data);
				msg.what=11;
				publishhandler.sendMessage(msg);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				try {
					if (httpResponse != null) {
						httpResponse.getEntity().getContent().close();
					}
				} catch (IllegalStateException e) {

				} catch (IOException e) {

				}
			}
		}
	};



	/*
	* 更新头像
	* */
	Runnable UpdateNicknameThread = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int result;
			String target = myapplication.getlocalhost()+"updateuserfield.php";
			HttpPost httprequest = new HttpPost(target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			paramsList.add(new BasicNameValuePair("userid", myapplication.getUserId()));
			paramsList.add(new BasicNameValuePair("field", "username"));
			paramsList.add(new BasicNameValuePair("fieldvalue", return_username));
			HttpClient HttpClient1;
			HttpResponse httpResponse = null;
			try {
				httprequest.setEntity(new UrlEncodedFormEntity(paramsList,"UTF-8"));
				HttpClient1 = CustomerHttpClient.getHttpClient();
				httpResponse = HttpClient1.execute(httprequest);
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					result = 1;
				} else {
					result = 0;
				}
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putInt("result", result);
				msg.setData(data);
				msg.what=12;
				publishhandler.sendMessage(msg);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				try {
					if (httpResponse != null) {
						httpResponse.getEntity().getContent().close();
					}
				} catch (IllegalStateException e) {

				} catch (IOException e) {

				}
			}
		}
	};

	Runnable UpdateBrithdayThread = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int result;
			String target = myapplication.getlocalhost()+"updateuserfield.php";
			HttpPost httprequest = new HttpPost(target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			paramsList.add(new BasicNameValuePair("userid", myapplication.getUserId()));
			paramsList.add(new BasicNameValuePair("field", "brithday,age"));
			paramsList.add(new BasicNameValuePair("fieldvalue", return_brithday+","+return_age));
			HttpClient HttpClient1;
			HttpResponse httpResponse = null;
			try {
				httprequest.setEntity(new UrlEncodedFormEntity(paramsList,"UTF-8"));
				HttpClient1 = CustomerHttpClient.getHttpClient();
				httpResponse = HttpClient1.execute(httprequest);
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					result = 1;
				} else {
					result = 0;
				}
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putInt("result", result);
				msg.setData(data);
				msg.what=13;
				publishhandler.sendMessage(msg);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				try {
					if (httpResponse != null) {
						httpResponse.getEntity().getContent().close();
					}
				} catch (IllegalStateException e) {

				} catch (IOException e) {

				}
			}
		}
	};



	Runnable UpdatSexThread = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int result;
			String target = myapplication.getlocalhost()+"updateuserfield.php";
			HttpPost httprequest = new HttpPost(target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			paramsList.add(new BasicNameValuePair("userid", myapplication.getUserId()));
			paramsList.add(new BasicNameValuePair("field", "sex"));
			paramsList.add(new BasicNameValuePair("fieldvalue", return_sex));
			HttpClient HttpClient1;
			HttpResponse httpResponse = null;
			try {
				httprequest.setEntity(new UrlEncodedFormEntity(paramsList,"UTF-8"));
				HttpClient1 = CustomerHttpClient.getHttpClient();
				httpResponse = HttpClient1.execute(httprequest);
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					result = 1;
				} else {
					result = 0;
				}
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putInt("result", result);
				msg.setData(data);
				msg.what=14;
				publishhandler.sendMessage(msg);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				try {
					if (httpResponse != null) {
						httpResponse.getEntity().getContent().close();
					}
				} catch (IllegalStateException e) {

				} catch (IOException e) {

				}
			}
		}
	};




	Runnable UpdatTelphoeThread = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int result;
			String target = myapplication.getlocalhost()+"updateuserfield.php";
			HttpPost httprequest = new HttpPost(target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			paramsList.add(new BasicNameValuePair("userid", myapplication.getUserId()));
			paramsList.add(new BasicNameValuePair("field", "telphone"));
			paramsList.add(new BasicNameValuePair("fieldvalue", return_telphone));
			HttpClient HttpClient1;
			HttpResponse httpResponse = null;
			try {
				httprequest.setEntity(new UrlEncodedFormEntity(paramsList,"UTF-8"));
				HttpClient1 = CustomerHttpClient.getHttpClient();
				httpResponse = HttpClient1.execute(httprequest);
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					result = 1;
				} else {
					result = 0;
				}
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putInt("result", result);
				msg.setData(data);
				msg.what=15;
				publishhandler.sendMessage(msg);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				try {
					if (httpResponse != null) {
						httpResponse.getEntity().getContent().close();
					}
				} catch (IllegalStateException e) {

				} catch (IOException e) {

				}
			}
		}
	};




	Runnable UpdatWeixinThread = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int result;
			String target = myapplication.getlocalhost () + "updateuserfield.php";
			HttpPost httprequest = new HttpPost (target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair> ();
			paramsList.add (new BasicNameValuePair ("userid", myapplication.getUserId ()));
			paramsList.add (new BasicNameValuePair ("field", "weixin"));
			paramsList.add (new BasicNameValuePair ("fieldvalue", return_weixin));
			HttpClient HttpClient1;
			HttpResponse httpResponse = null;
			try {
				httprequest.setEntity (new UrlEncodedFormEntity (paramsList, "UTF-8"));
				HttpClient1 = CustomerHttpClient.getHttpClient ();
				httpResponse = HttpClient1.execute (httprequest);
				if (httpResponse.getStatusLine ().getStatusCode () == HttpStatus.SC_OK) {
					result = 1;
				} else {
					result = 0;
				}
				Message msg = new Message ();
				Bundle data = new Bundle ();
				data.putInt ("result", result);
				msg.setData (data);
				msg.what = 16;
				publishhandler.sendMessage (msg);
			} catch (ClientProtocolException e) {
				e.printStackTrace ();
			} catch (IOException e) {
				e.printStackTrace ();
			} finally {
				try {
					if (httpResponse != null) {
						httpResponse.getEntity ().getContent ().close ();
					}
				} catch (IllegalStateException e) {

				} catch (IOException e) {

				}
			}
		}
	};

	Runnable UpdatEmergencyThread = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int result;
			String target = myapplication.getlocalhost () + "updateuserfield.php";
			HttpPost httprequest = new HttpPost (target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair> ();
			paramsList.add (new BasicNameValuePair ("userid", myapplication.getUserId ()));
			paramsList.add (new BasicNameValuePair ("field", "emergencycontact,emergencycontacttelphone"));
			paramsList.add (new BasicNameValuePair ("fieldvalue", return_emergency+","+return_emergency_telphone));
			HttpClient HttpClient1;
			HttpResponse httpResponse = null;
			try {
				httprequest.setEntity (new UrlEncodedFormEntity (paramsList, "UTF-8"));
				HttpClient1 = CustomerHttpClient.getHttpClient ();
				httpResponse = HttpClient1.execute (httprequest);
				if (httpResponse.getStatusLine ().getStatusCode () == HttpStatus.SC_OK) {
					result = 1;
				} else {
					result = 0;
				}
				Message msg = new Message ();
				Bundle data = new Bundle ();
				data.putInt ("result", result);
				msg.setData (data);
				msg.what = 17;
				publishhandler.sendMessage (msg);
			} catch (ClientProtocolException e) {
				e.printStackTrace ();
			} catch (IOException e) {
				e.printStackTrace ();
			} finally {
				try {
					if (httpResponse != null) {
						httpResponse.getEntity ().getContent ().close ();
					}
				} catch (IllegalStateException e) {

				} catch (IOException e) {

				}
			}
		}
	};


	Runnable UpdatXiaoquThread = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int result;
			String target = myapplication.getlocalhost () + "updateuserfield.php";
			HttpPost httprequest = new HttpPost (target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair> ();
			paramsList.add (new BasicNameValuePair ("userid", myapplication.getUserId ()));
			paramsList.add (new BasicNameValuePair ("field", "community,community_id"));
			paramsList.add (new BasicNameValuePair ("fieldvalue", load_xiaoqu+","+load_xiaoquid));
			HttpClient HttpClient1;
			HttpResponse httpResponse = null;
			try {
				httprequest.setEntity (new UrlEncodedFormEntity (paramsList, "UTF-8"));
				HttpClient1 = CustomerHttpClient.getHttpClient ();
				httpResponse = HttpClient1.execute (httprequest);
				if (httpResponse.getStatusLine ().getStatusCode () == HttpStatus.SC_OK) {
					result = 1;
				} else {
					result = 0;
				}
				Message msg = new Message ();
				Bundle data = new Bundle ();
				data.putInt ("result", result);
				msg.setData (data);
				msg.what = 18;
				publishhandler.sendMessage (msg);
			} catch (ClientProtocolException e) {
				e.printStackTrace ();
			} catch (IOException e) {
				e.printStackTrace ();
			} finally {
				try {
					if (httpResponse != null) {
						httpResponse.getEntity ().getContent ().close ();
					}
				} catch (IllegalStateException e) {

				} catch (IOException e) {

				}
			}
		}
	};


	public void onSuccess(int method, Object obj) {
		switch (method) {
			case MarketAPI.ACTION_GETUERSUMMARY:
				String load_num1 = "0";
				String load_num2 = "0";
				String load_num3 = "0";
				String load_num4 = "0";
				String load_num5 = "0";
				String load_num6 = "0";
				HashMap<String, String> result = (HashMap<String, String>) obj;
				String json=result.get("result");
				JSONObject jsonobject = null;
				try {
					jsonobject=new JSONObject(json);
					load_num1 = jsonobject.getString("num1");
					load_num2 = jsonobject.getString("num2");
					load_num3 = jsonobject.getString("num3");
					load_num4 = jsonobject.getString("num4");
					load_num5 = jsonobject.getString("num5");
					load_num6 = jsonobject.getString("num6");

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putString("load_num1", load_num1);
				data.putString("load_num2", load_num2);
				data.putString("load_num3", load_num3);
				data.putString("load_num4", load_num4);
				data.putString("load_num5", load_num5);
				data.putString("load_num6", load_num6);
				msg.what=2;
				msg.setData(data);
				laodhandler.sendMessage(msg);
				break;
			case MarketAPI.ACTION_GETUESERINFO:
				HashMap<String, String> results = (HashMap<String, String>) obj;
				String jsons=results.get("userinfo");
				if(jsons.length()>0)
				{
					JSONArray jsonarray;
					try {
						jsonarray = new JSONArray(jsons);
						JSONObject ajsonobject = jsonarray.getJSONObject(0);
						load_username = ajsonobject.getString("username");
						return_username = load_username;
						load_age = ajsonobject.getString("age");
						load_brithdaystr = ajsonobject.getString("brithday");
						load_sex = ajsonobject.getString("sex");
						load_telphone = ajsonobject.getString("telphone");
						load_weixinstr = ajsonobject.getString("weixin");
						load_remote_headface = ajsonobject.getString("headface");
						load_userid = ajsonobject.getString("userid");
						load_score = ajsonobject.getString("score");

						load_xiaoquid= ajsonobject.getString("community_id");
						load_xiaoqu = ajsonobject.getString("community");
						if (ajsonobject.getString("emergencycontact") != null && !ajsonobject.getString("emergencycontact").toString().equals("null") && ajsonobject.getString("emergencycontact").length() > 0) {//emergencycontact
							load_emergency = ajsonobject.getString("emergencycontact");
							load_emergencytelphone = ajsonobject.getString("emergencycontacttelphone");
						} else {
							load_emergency = "";
							load_emergencytelphone = "";
						}
						uService.updatenickname(load_username, load_userid);//更新用户昵称
						uService.updatenickname(load_username, myapplication.getUserId());
						uService.updateheadface(load_remote_headface, myapplication.getUserId());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else
				{
					load_username = "";
					return_username  = "";;
					load_age  = "";
					load_brithdaystr  = "";
					load_sex  = "";
					load_telphone  = "";
					load_weixinstr = "";
					load_remote_headface  = "";
					load_userid = "";
					load_score = "";
					load_emergency = "";
					load_emergencytelphone = "";
				}
				Message msg1 = new Message();
				Bundle data1 = new Bundle();
				data1.putString("username", load_username);
				data1.putString("age", load_age);
				data1.putString("brithday", load_brithdaystr);
				data1.putString("sex", load_sex);
				data1.putString("telphone", load_telphone);
				data1.putString("weixin", load_weixinstr);
				data1.putString("headface", load_remote_headface);
				data1.putString("userid", load_userid);
				data1.putString("score", load_score);

				data1.putString("xiaoqu", load_xiaoqu);
				data1.putString("xiaoquid", load_xiaoquid);
				data1.putString("emergency", load_emergency);
				data1.putString("emergencytelphone", load_emergencytelphone);
				msg1.what=1;
				msg1.setData(data1);
				laodhandler.sendMessage(msg1);
				break;
			default:
				break;
		}
	}


	@Override
	public void onError(int method, int statusCode) {
		switch (method) {
			case MarketAPI.ACTION_MYRANK:

				break;
			case MarketAPI.ACTION_RANK:

				break;
			default:
				break;
		}
	}
}