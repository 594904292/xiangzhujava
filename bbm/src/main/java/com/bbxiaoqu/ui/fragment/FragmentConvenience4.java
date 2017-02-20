package com.bbxiaoqu.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.ImageOptions;
import com.bbxiaoqu.R;
import com.bbxiaoqu.Session;
import com.bbxiaoqu.api.ApiAsyncTask;
import com.bbxiaoqu.api.MarketAPI;
import com.bbxiaoqu.client.baidu.Utils;
import com.bbxiaoqu.comm.tool.CustomerHttpClient;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.ui.SearchCxhfdmActivity;
import com.bbxiaoqu.ui.SelectAddressActivity;
import com.bbxiaoqu.ui.UserInfoActivity;
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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.bbxiaoqu.ui.fragment.FragmentPage4.RESULT_CANCELED;

public class FragmentConvenience4 extends Fragment implements ApiAsyncTask.ApiRequestListener {
	private DemoApplication myapplication;
	private Activity myactivate;
	private View view;
	protected Session mSession;

	private EditText shopname;
	private EditText shoptelphone;
	private EditText  shopteladdress;
	Button saveshopinfo_btn;
	Button selAddress;
	private String shoppicpath = "";
	private String  shoppicname = "";
	/** ImageView对象 */
	private ImageView iv_photo;
	private String[] items = new String[] { "选择本地图片", "拍照" };
	/** 头像名称 */
	private static final String IMAGE_FILE_NAME = "image.jpg";
	private static final int GzXq_REQUEST_CODE=101;
	/** 请求码 */
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;
	public static final int SEL_ADDRESS    = 98;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		view= inflater.inflate(R.layout.fragment_convenience_4, null);

		myactivate=this.getActivity();
		myapplication = (DemoApplication) myactivate.getApplication();

		shopname = (EditText) view.findViewById(R.id.shopname);
		shoptelphone= (EditText) view.findViewById(R.id.shoptelphone);
		shopteladdress= (EditText) view.findViewById(R.id.shopteladdress);
		saveshopinfo_btn = (Button) view.findViewById(R.id.saveshopinfo);
		selAddress = (Button) view.findViewById(R.id.seladdress);

		saveshopinfo_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				System.out.println(shopname.getText());
				System.out.println(shoptelphone.getText());
				System.out.println(shoppicpath);
				System.out.println(load_address);
				System.out.println(load_latitude);
				System.out.println(load_longitude);
				new Thread(saveshopinfo).start();
			}
		});
		selAddress.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(myactivate.getApplicationContext(), SelectAddressActivity.class);
				startActivityForResult(intent, SEL_ADDRESS);// 请求码
			}

		});
		iv_photo = (ImageView) view.findViewById(R.id.iv_photo);
		iv_photo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog();
			}
		});
		MarketAPI.GetShopInfo(myapplication, this,myapplication.getUserId());
		return view;
	}

	/**
	 * 显示选择对话框
	 */
	private void showDialog() {
		new AlertDialog.Builder(myactivate)
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
	String load_address="";
	Double load_latitude=0.00;
	Double load_longitude=0.00;
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
						File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
						File tempFile = new File(path, IMAGE_FILE_NAME);
						startPhotoZoom(Uri.fromFile(tempFile));
					} else {
						//Toast.makeText(getApplicationContext(), "未找到存储卡，无法存储照片！",Toast.LENGTH_SHORT).show();
					}
					break;
				case SEL_ADDRESS: // 选择小区
					if (data != null) {
						load_address = data.getStringExtra("address");
						load_latitude = data.getDoubleExtra("latitude",0.00);
						load_longitude = data.getDoubleExtra("longitude",0.00);
						shopteladdress.setText(load_address);
					}
					break;
				case RESULT_REQUEST_CODE: // 图片缩放完成后
					if (data != null) {
						getImageToView(data);
					}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/*System.out.println(shopname.getText());
	System.out.println(shoptelphone.getText());
	System.out.println(shoppicpath);
	System.out.println(load_address);
	System.out.println(load_latitude);
	System.out.println(load_longitude);
	*/
	Runnable saveshopinfo = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int result;
			String target = myapplication.getlocalhost()+"saveshopinfo.php";
			HttpPost httprequest = new HttpPost(target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			paramsList.add(new BasicNameValuePair("userid", myapplication.getUserId()));
			if(shoppicname.length()>0) {//保证选择了图片
				String picname = "/shop/" + myapplication.getUserId() + "/" + shoppicname;
				paramsList.add(new BasicNameValuePair("cover_image", picname));
			}
			paramsList.add(new BasicNameValuePair("shopname", shopname.getText().toString()));
			paramsList.add(new BasicNameValuePair("telphone", shoptelphone.getText().toString()));
			paramsList.add(new BasicNameValuePair("address", load_address));
			if(load_latitude!=0.0) {
				paramsList.add(new BasicNameValuePair("lat", String.valueOf(load_latitude)));
			}else
			{
				paramsList.add(new BasicNameValuePair("lat", ""));
			}
			if(load_longitude!=0.0) {
				paramsList.add(new BasicNameValuePair("lng", String.valueOf(load_longitude)));
			}else
			{
				paramsList.add(new BasicNameValuePair("lng", ""));
			}
			try {
				httprequest.setEntity(new UrlEncodedFormEntity(paramsList,"UTF-8"));
				HttpClient HttpClient1 = CustomerHttpClient.getHttpClient();
				HttpResponse httpResponse = HttpClient1.execute(httprequest);
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					String json = EntityUtils.toString(httpResponse.getEntity());
					if(shoppicpath!=null&&shoppicpath.length()>0)
					{
						String target1 = myapplication.getlocalhost()+"upload_v1.php?shopid="+myapplication.getUserId();//存到指定文件夹
						upLoadByAsyncHttpClient(target1);
					}
					result = 1;
				} else {
					result = 0;
				}
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putInt("result", result);
				msg.setData(data);
				msg.what=1;
				publishhandler.sendMessage(msg);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};


	private final String TAG = "UserInfoActivity";
	private AsyncHttpClient client;
	private void upLoadByAsyncHttpClient(String uploadUrl)
			throws FileNotFoundException {
		AsyncBody(uploadUrl, shoppicpath);

	}

	private void AsyncBody(String uploadUrl, String localpath)
			throws FileNotFoundException {
		RequestParams params = new RequestParams();
		client = new AsyncHttpClient();
		params.put("uploadfile", new File(localpath));
		client.post(uploadUrl, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, String arg1) {
				super.onSuccess(arg0, arg1);
				Log.i(TAG, arg1);
			}
		});
	}

	Handler publishhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what==1) {
				Bundle data = msg.getData();
				int result = data.getInt("result");
				if (result == 1) {
					Toast.makeText(myactivate, "更新成功",
							Toast.LENGTH_SHORT).show();
				} else {
					// Toast.makeText(SendFragment.this,
					// "推送失败",Toast.LENGTH_SHORT).show();
				}
			}
		}
	};

	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 340);
		intent.putExtra("outputY", 340);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, RESULT_REQUEST_CODE);
	}

	private void getImageToView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			photo= ThumbnailUtils.extractThumbnail(photo, 150, 150);
			java.text.DateFormat format2 = new java.text.SimpleDateFormat(
					"yyyyMMddHHmmss");
			shoppicname = format2.format(new Date()) + ".jpg";
			shoppicpath = saveBitmap(photo, shoppicname);
			Drawable drawable = new BitmapDrawable(this.getResources(), photo);
			iv_photo.setImageDrawable(drawable);
		}
	}

	private String saveBitmap(Bitmap imgThumb, String fileName) {
		FileOutputStream out = null;
		File yygypath = myactivate.getFilesDir();// this.getCacheDir();
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

	@Override
	public void onSuccess(int method, Object obj) {
		switch (method) {
			case MarketAPI.ACTION_GETSHOPINFO:
				HashMap<String, String> result = (HashMap<String, String>) obj;
				String json = result.get("result");
				JSONArray jsonarray;
				try {
					jsonarray = new JSONArray(json);
					JSONObject jsonobject = jsonarray.getJSONObject(0);
					String name=jsonobject.getString("name");
					String telphone=jsonobject.getString("telphone");

					load_address=jsonobject.getString("address");
					//load_latitude=jsonobject.getDouble("latitude");
					//load_longitude=jsonobject.getDouble("longitude");


					String cover_image=jsonobject.getString("cover_image");
					Message msg = new Message();
					msg.what=1;
					Bundle data = new Bundle();
					data.putString("name", name);
					data.putString("address", load_address);
					//data.putDouble("lat", load_latitude);
					//data.putDouble("lng", load_longitude);
					data.putString("telphone", telphone);
					data.putString("cover_image", cover_image);
					msg.setData(data);
					laodhandler.sendMessage(msg);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Utils.makeEventToast(this.myapplication, "result xml解释错误", false);
				}
				break;
			default:
				break;
		}
	}

	Handler laodhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage (msg);
			if (msg.what == 1) {
				Bundle data = msg.getData();
				shopname.setText(data.getString("name"));
				shoptelphone.setText(data.getString("telphone"));
				shopteladdress.setText(data.getString("address"));

				if (data.getString ("cover_image") != null && data.getString ("cover_image").length () > 0) {
					String fileName = myapplication.getlocalhost () + "uploads/" + data.getString ("cover_image");
					ImageLoader.getInstance ().displayImage (fileName, iv_photo, ImageOptions.getOptions ());
				}
			}
		}
	};
	@Override
	public void onError(int method, int statusCode) {

	}
}