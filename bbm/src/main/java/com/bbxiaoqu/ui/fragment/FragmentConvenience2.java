package com.bbxiaoqu.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.R;
import com.bbxiaoqu.Session;
import com.bbxiaoqu.api.ApiAsyncTask;
import com.bbxiaoqu.api.MarketAPI;
import com.bbxiaoqu.comm.adapter.NewListViewAdapter;
import com.bbxiaoqu.comm.tool.CustomerHttpClient;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.comm.widget.AutoListView;
import com.bbxiaoqu.comm.widget.NewAutoListView;
import com.bbxiaoqu.ui.GongGaoActivity;
import com.bbxiaoqu.ui.ViewActivity;

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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentConvenience2 extends Fragment  {
	private DemoApplication myapplication;
	private View view;
	private WebView webView;
	protected Session mSession;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		view= inflater.inflate(R.layout.fragment_convenience_2, null);
		myapplication = (DemoApplication) this.getActivity().getApplication();
		mSession = Session.get(myapplication);

		webView = (WebView) view.findViewById(R.id.webView);
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		String url="http://api.bbxiaoqu.com/convenience/Shop.php?lat="+myapplication.getLat()+"&lng="+myapplication.getLng();
		webView.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if( url.startsWith("http:") || url.startsWith("https:") ) {
					return false;
				}

				// Otherwise allow the OS to handle things like tel, mailto, etc.
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				startActivity( intent );
				return true;
			}
		});
		webView.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {  //表示按返回键时的操作
						webView.goBack();   //后退
						//webview.goForward();//前进
						return true;    //已处理
					}
				}
				return false;
			}
		});
		webView.loadUrl(url);
		return view;
	}

}