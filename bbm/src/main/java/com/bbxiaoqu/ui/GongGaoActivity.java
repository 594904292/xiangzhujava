package com.bbxiaoqu.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.bbxiaoqu.R;
import com.bbxiaoqu.comm.view.BaseActivity;


public class GongGaoActivity extends BaseActivity {
	private TextView textView;
	TextView title;
	TextView right_text;
	ImageView top_more;
	WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gonggao);
		initView();
		initData();


		Bundle Bundle1 = this.getIntent().getExtras();

		String guid = Bundle1.getString("guid");

		webView =(WebView)findViewById(R.id.webView);
		webView.loadUrl("https://api.bbxiaoqu.com/wap/gonggao.php?id="+guid);
		//启用支持javascript
		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		//个自己的WebViewClient，通过setWebViewClient关联
		webView.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				//返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
				view.loadUrl(url);
				return true;
			}
		});
	}

	private void initView() {
		title = (TextView)findViewById(R.id.title);
		right_text = (TextView)findViewById(R.id.right_text);
		right_text.setVisibility(View.VISIBLE);
		right_text.setClickable(true);		
		top_more = (ImageView) findViewById(R.id.top_more);	
		top_more.setVisibility(View.VISIBLE);
		top_more.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent=new Intent(GongGaoActivity.this,SearchActivity.class);
			startActivity(intent);
			}
		});
	}

	private void initData() {
		title.setText("系统公告");
		right_text.setText("");
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		//overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		finish();
	}

}
