package com.bbxiaoqu.ui;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.ImageOptions;
import com.bbxiaoqu.R;
import com.bbxiaoqu.comm.tool.CustomerHttpClient;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.comm.view.XCFlowLayout;
import com.bbxiaoqu.comm.view.BaseActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AppraiseActivity extends BaseActivity {
	public ImageView right_image;
	String id;
	String infoid;
	String guid;
	String volunteeruserid;//帮助我的人志愿者的ID
	String type;
	private DemoApplication myapplication;
	RatingBar ratingBar;
	EditText content_edit;
	TextView chCounterText;
	TextView save_btn;
	String content;
	String contentid;
	String username;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.activity_appraise);
		myapplication = (DemoApplication) this.getApplication();
		myapplication.getInstance().addActivity(this);
		Bundle Bundle1 = this.getIntent().getExtras();
		id = Bundle1.getString("id");
		infoid = Bundle1.getString("infoid");
		guid = Bundle1.getString("guid");
		volunteeruserid = Bundle1.getString("senduserid");
		type = Bundle1.getString("type");
		content = Bundle1.getString("content");
		contentid = Bundle1.getString("contentid");
		username = Bundle1.getString("username");
		String headface = Bundle1.getString("headface");


		ImageView headface_iv = (ImageView) findViewById (R.id.headface);
		if(headface.length()>0) {
			String fileName = DemoApplication.getInstance().getlocalhost()
					+ "uploads/" + headface;
			ImageLoader.getInstance().displayImage(fileName,
					headface_iv, ImageOptions.getOptions());
		}else
		{
			headface_iv.setImageResource(R.mipmap.xz_wo_icon);
		}
		TextView info_sendname = (TextView) findViewById (R.id.info_sendname);
		info_sendname.setText (username);

		initView ();
		ratingBar = (RatingBar) findViewById (R.id.ratingBar);
		content_edit = (EditText) findViewById (R.id.content);
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
					T.showShort (AppraiseActivity.this,"超过最大输入字符数");

				}else {
					chCounterText.setText(content.length() + "/200");
				}

			}

		});
		TextView addtag = (TextView) findViewById (R.id.addtag);
		addtag.setOnClickListener(new View.OnClickListener () {
			@Override
			public void onClick(View v) {
				LayoutInflater factory = LayoutInflater.from(AppraiseActivity.this);
				final View textEntryView = factory.inflate(R.layout.dialog, null);
				AlertDialog dlg = new AlertDialog.Builder(AppraiseActivity.this)
						.setTitle("添加标签")
						.setView(textEntryView)
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								System.out.println("-------------->6");
								EditText username_edit = (EditText) textEntryView.findViewById(R.id.dialog_username_edit);
								//T.showShort (AppraiseActivity.this,username_edit.getText ().toString ());
								refreshtagview("add",username_edit.getText ().toString ());


							}
						})
						.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								System.out.println("-------------->2");

							}
						})
						.create();
				dlg.show();

			}
		});
		save_btn = (TextView) findViewById (R.id.save_btn);
		save_btn.setOnClickListener(new View.OnClickListener () {
			@Override
			public void onClick(View v) {
				//采纳动作主要操作三个表 info status改为2
				// 针对评论discuss表POS,升降位
				//info_act表 action为2的为评论 状态变1
				//infohelpuser表status改为1 所有帮助信息 把基本一条有用的状态变为1

				// infosolve添加一条解决记录
				//member_evaluate增加一条事件评论

				// TODO Auto-generated method stub
				new Thread(saveThread).start();
				new Thread(solutionThread).start();
				save_btn.setEnabled (false);

			}
		});
	}

	private String[] SelmNames = {
			"大好人"
	};

	private String[] mNames = {
			"80后", "暖男", "大叔",
			"雷锋", "百事通"
	};

	private void initView() {
		TextView PageTitle = (TextView) findViewById (R.id.PageTitle);
		PageTitle.setText ("评价");
		right_image = (ImageView) findViewById (R.id.top_menu_right_image);
		right_image.setOnClickListener (new View.OnClickListener () {
			public void onClick(View v) {
				if (!NetworkUtils.isNetConnected (AppraiseActivity.this)) {
					NetworkUtils.showNoNetWorkDlg (AppraiseActivity.this);
					return;
				}

				Intent intent = new Intent (AppraiseActivity.this, SearchActivity.class);
				startActivity (intent);

			}
		});
		ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams (
				ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		lp.leftMargin = 5;
		lp.rightMargin = 5;
		lp.topMargin = 5;
		lp.bottomMargin = 5;
		XCFlowLayout flowlayout1 = (XCFlowLayout) findViewById (R.id.flowlayout1);
		for (int i = 0; i < SelmNames.length; i++) {
			TextView view = new TextView (this);
			view.setText (SelmNames[i]);
			view.setTag (SelmNames[i]);
			view.setBackgroundResource (R.mipmap.xz_y4_icon);
			view.setOnClickListener (new View.OnClickListener () {
				public void onClick(View v) {
					T.showShort (AppraiseActivity.this, v.getTag ().toString ());
					refreshtagview("sub",v.getTag ().toString ());
				}
			});
			flowlayout1.addView (view, lp);
		}
		XCFlowLayout flowlayout2 = (XCFlowLayout) findViewById (R.id.flowlayout2);
		for (int i = 0; i < mNames.length; i++) {
			TextView view = new TextView (this);
			view.setText (mNames[i]);
			view.setTag (mNames[i]);
			view.setBackgroundResource (R.drawable.selector_searchtxt_background);
			view.setOnClickListener (new View.OnClickListener () {
				public void onClick(View v) {
					T.showShort (AppraiseActivity.this, v.getTag ().toString ());
					refreshtagview("add",v.getTag ().toString ());
				}
			});
			flowlayout2.addView (view, lp);
		}


	}





	/**
	 * 删除标签
	 * @param tag
	 */
	public void refreshtagview(String type,String tag) {
		if(type.equals ("add")) {
			SelmNames = inserttag (SelmNames, tag);
			mNames = deltag (mNames, tag);
		}else
		{//sub
			SelmNames=deltag(SelmNames,tag);
			mNames=inserttag(mNames,tag);

		}
		ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams (
				ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		lp.leftMargin = 5;
		lp.rightMargin = 5;
		lp.topMargin = 5;
		lp.bottomMargin = 5;

		XCFlowLayout flowlayout1 = (XCFlowLayout) findViewById (R.id.flowlayout1);
		flowlayout1.removeAllViews ();
		for (int i = 0; i < SelmNames.length; i++) {
			TextView view = new TextView (this);
			view.setText (SelmNames[i]);
			view.setTag (SelmNames[i]);
			view.setBackgroundResource (R.mipmap.xz_y4_icon);
			view.setOnClickListener (new View.OnClickListener () {
				public void onClick(View v) {
					T.showShort (AppraiseActivity.this, v.getTag ().toString ());
					refreshtagview("sub",v.getTag ().toString ());
				}
			});
			flowlayout1.addView (view, lp);
		}

		XCFlowLayout flowlayout2 = (XCFlowLayout) findViewById (R.id.flowlayout2);
		flowlayout2.removeAllViews ();
		for (int i = 0; i < mNames.length; i++) {
			TextView view = new TextView (this);
			view.setText (mNames[i]);
			view.setTag (mNames[i]);
			view.setBackgroundResource (R.drawable.selector_searchtxt_background);
			view.setOnClickListener (new View.OnClickListener () {
				public void onClick(View v) {
					refreshtagview("add",v.getTag ().toString ());
				}
			});
			flowlayout2.addView (view, lp);
		}


	}

	public String[] deltag(String[] arrays,String tag) {
		String[] arr;
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < arrays.length; i++) {
			if(tag.equals(arrays[i])){

			}else
			{
				list.add(arrays[i]);
			}
		}
		if(list.size ()>0) {
			arr = list.toArray (new String[1]);
		}else
		{
			arr=new String[0];
		}
		return arr;
	}

	public String[] inserttag(String[] arrays,String tag) {
		int size = arrays.length;
		String[] tmp = new String[size + 1];
		System.arraycopy(arrays, 0, tmp, 0, size);

		tmp[size] = tag;
		return tmp;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed ();
		finish ();
	}


	Runnable saveThread = new Runnable() {
		public void run() {
			//SELECT * FROM `info_act` where action=2
			//生成订单
			//订单号:guid
			//用户id:dataList.get((Integer) v.getTag()).get("uerid")
			//String userid = dataList.get (pos).get ("uerid").toString ();
			//更改状态 status=1
			String target = myapplication.getlocalhost () + "genfinshorder_v1.php";
			HttpPost httprequest = new HttpPost (target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair> ();
			paramsList.add (new BasicNameValuePair ("_guid", guid));//产品id
			paramsList.add (new BasicNameValuePair ("_fromuser", volunteeruserid));//志愿者,解决问题的人
			paramsList.add (new BasicNameValuePair ("_userid", myapplication.getUserId ()));//求肋人，给予志愿者
			paramsList.add (new BasicNameValuePair ("_status", "2"));//状态

			paramsList.add (new BasicNameValuePair ("_rating", String.valueOf (ratingBar.getRating ())));//用户id
			paramsList.add (new BasicNameValuePair ("_content", content_edit.getText ().toString ()));//用户id


			StringBuffer sb = new StringBuffer();
			for(int i = 0; i < SelmNames.length; i++){
				sb. append(SelmNames[i]);
				if(i<SelmNames.length-1)
				{
					sb.append ("|");
				}
			}
			String s = sb.toString();

			paramsList.add (new BasicNameValuePair ("_evaluatetag", s));//用户id
			try {
				httprequest.setEntity (new UrlEncodedFormEntity (paramsList,
						"UTF-8"));
				HttpClient HttpClient1 = CustomerHttpClient.getHttpClient ();
				HttpResponse httpResponse = HttpClient1.execute (httprequest);
				String code = "";
				if (httpResponse.getStatusLine ().getStatusCode () == HttpStatus.SC_OK) {
					code = EntityUtils.toString (httpResponse.getEntity ());

				} else {
					code = "";
				}
				Message msg = new Message ();
				Bundle data = new Bundle ();
				data.putString ("savecode", code);
				msg.setData (data);
				handler.sendMessage (msg);
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace ();
			} catch (ClientProtocolException e) {
				e.printStackTrace ();
			} catch (IOException e) {
				e.printStackTrace ();
			}
		}
	};

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			//rsload();
			/*save_btn.setEnabled (false);
			onBackPressed();*/
		}
	};


	Runnable solutionThread = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int result;
			String target = myapplication.getlocalhost() + "solution.php";
			HttpPost httprequest = new HttpPost(target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();

			paramsList.add(new BasicNameValuePair("_infoid", infoid));// 信息id
			paramsList.add(new BasicNameValuePair("_guid", guid));// 信息唯一标识
			if(type.equals ("pl")) {
				paramsList.add (new BasicNameValuePair ("_solutiontype", "1"));// 留言
			}else
			{
				paramsList.add (new BasicNameValuePair ("_solutiontype", "2"));// 私聊
			}
			paramsList.add(new BasicNameValuePair("_solutionpostion", contentid));// 留言项
			paramsList.add(new BasicNameValuePair("_solutionuserid", volunteeruserid));// 志愿者ID
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String solutiondate = sDateFormat.format(new java.util.Date());

			paramsList
					.add(new BasicNameValuePair("_solutiontime", solutiondate));// 本人

			try {
				httprequest.setEntity(new UrlEncodedFormEntity(paramsList,
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
				freshhandler.sendMessage(msg);
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	};


	Handler freshhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			int result = data.getInt("result");
			Log.i("mylog", "请求结果-->" + result);
			if (result == 1) {
				T.showShort (AppraiseActivity.this,"评价成功");
				Intent intent = new Intent(AppraiseActivity.this, MainTabActivity.class);
				startActivity(intent);


			}
		}
	};


}
