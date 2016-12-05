package com.bbxiaoqu.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.R;
import com.bbxiaoqu.comm.adapter.EvaluateAdapter;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.StreamTool;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.ui.ViewUserInfoActivity;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentU1 extends Fragment  {
	private DemoApplication myapplication;
	private View view;
	private ListView listview;
	EvaluateAdapter adapter;
	private List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>> ();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		myapplication = (DemoApplication) this.getActivity().getApplication();
		view= inflater.inflate(R.layout.fragment_u_1, null);
		loadlist();
		return view;
	}

	private void loadlist() {
		listview= (ListView) view.findViewById(R.id.evaluatelist);//列表
		LinearLayout emptyView = (LinearLayout) view.findViewById(R.id.nomess);
		listview.setEmptyView(emptyView);
		dataList = new ArrayList<Map<String, Object>>();
		if (listview == null) {
			return;
		}
		getData();
		adapter= new EvaluateAdapter (this.getActivity (), dataList);
		listview.setAdapter(adapter);
	}

	private void getData() {
		if (!NetworkUtils.isNetConnected(myapplication)) {
			T.showShort(myapplication, "当前无网络连接！");
			NetworkUtils.showNoNetWorkDlg(this.getActivity ());
			return;
		}
		String target=myapplication.getlocalhost()+"getmemberevaluates_v1.php?userid="+((ViewUserInfoActivity)getActivity()).getmUserid ();
		dataList = new ArrayList<Map<String, Object>>();
		try {
			List<Map<String, Object>> bfjllist=null;
			HttpGet httprequest = new HttpGet(target);
			HttpClient HttpClient1 = new DefaultHttpClient ();
			HttpResponse httpResponse = null;
			try {
				httpResponse = HttpClient1.execute(httprequest);
			} catch (ClientProtocolException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				InputStream json = null;
				try {
					json = httpResponse.getEntity().getContent();
					bfjllist= parsejson(json);
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			for (Map map: bfjllist) {
				dataList.add(map);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private List<Map<String, Object>> parsejson(InputStream jsonStream)
			throws Exception {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		byte[] data = StreamTool.read(jsonStream);
		String json = new String(data);
		JSONArray jsonarray = new JSONArray(json);
		for (int i = 0; i < jsonarray.length(); i++) {
			JSONObject jsonobject = jsonarray.getJSONObject(i);
			int id = jsonobject.getInt("id");
			String guid = jsonobject.getString("guid");
			String infouser = jsonobject.getString("infouser");
			String username  = jsonobject.getString("username");
			String userid = jsonobject.getString("userid");
			String sex = jsonobject.getString("sex");
			String headface = jsonobject.getString("headface");
			String score = jsonobject.getString("score");
			String evaluate = jsonobject.getString("evaluate");
			String addtime = jsonobject.getString("addtime");
			String evaluatetag = jsonobject.getString("evaluatetag");

			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put("id",id);
			item.put("guid",guid);
			item.put("infouser",infouser);
			item.put("username",username);
			item.put("userid",userid);
			item.put("sex",sex);
			item.put("headface",headface);
			item.put("score",score);
			item.put("evaluate",evaluate.trim());
			item.put("evaluatetag",evaluatetag.trim());
			item.put("addtime",addtime);
			list.add(item);
		}
		return list;
	}
	
}