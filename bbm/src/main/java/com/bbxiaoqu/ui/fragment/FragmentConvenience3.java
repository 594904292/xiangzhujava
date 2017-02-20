package com.bbxiaoqu.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.R;
import com.bbxiaoqu.Session;

public class FragmentConvenience3 extends Fragment  {
	private DemoApplication myapplication;
	private View view;
	private WebView webView;
	protected Session mSession;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		view= inflater.inflate(R.layout.fragment_convenience_3, null);
		myapplication = (DemoApplication) this.getActivity().getApplication();
		mSession = Session.get(myapplication);

		webView = (WebView) view.findViewById(R.id.webView);
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		String url="http://api.bbxiaoqu.com/convenience/myinfo.php?userid="+myapplication.getUserId();
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

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//其中webView.canGoBack()在webView含有一个可后退的浏览记录时返回true
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			webView.goBack();
			return true;
		}
		return this.getActivity().onKeyDown(keyCode, event);
	}


}