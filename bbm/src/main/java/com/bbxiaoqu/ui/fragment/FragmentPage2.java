package com.bbxiaoqu.ui.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.R;
import com.bbxiaoqu.comm.adapter.RecentAdapter;
import com.bbxiaoqu.client.baidu.BbPushMessageReceiver;
import com.bbxiaoqu.comm.bean.BbMessage;
import com.bbxiaoqu.comm.service.db.DatabaseHelper;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.ui.ChattingActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bbxiaoqu.client.baidu.BbPushMessageReceiver.freshtableListeners;



public class FragmentPage2 extends Fragment implements BbPushMessageReceiver.RfeshListener {

	@Override
	public void freshtable() {
		loadData();
	}
	private DemoApplication myapplication;
	private RecentAdapter adapter;
	private List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
	ListView lstv;
	private TextView title;
	private TextView right_text;
	public ImageView top_more;
	private DatabaseHelper dbHelper;
	View view;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		dbHelper = new DatabaseHelper(this.getActivity());
		myapplication = (DemoApplication) this.getActivity().getApplication();
		freshtableListeners.add(this);
		view= inflater.inflate(R.layout.fragment_2, null);
		loadData();
		return view;
	}


	public void updataUI(String name) {
	System.out.println(name);;
	}


	private void loadData() {
		// TODO Auto-generated method stub
		lstv = (ListView) view.findViewById(R.id.recentlv);
		lstv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
									int location, long arg3) {
				//更新用户未读数
				String userid=dataList.get(location).get("userid").toString();
				SQLiteDatabase sdb = dbHelper.getReadableDatabase();
				String sql = "update friend set messnum=0 where userid='"+userid+"'";
				sdb.execSQL(sql);
				//变为全读
				String sql1 = "update chat set readed=1 where senduserid='"+userid+"' or touserid='"+userid+"'";
				sdb.execSQL(sql1);
				//通知主界面,刷新未读数

				Message msg = new Message();
				msg.what=1;
				loadhandler.sendMessage(msg);

				//提取用户id
				Intent intent = new Intent(myapplication,ChattingActivity.class);
				Bundle arguments = new Bundle();
				arguments.putString("to", userid);
				arguments.putString("my",myapplication.getUserId());
				intent.putExtras(arguments);
				startActivity(intent);


			}
		});
		LinearLayout emptyView = (LinearLayout) view.findViewById(R.id.nomess);
		Button viewhistorybtn = (Button) view.findViewById(R.id.viewhistorybtn);
		viewhistorybtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				T.showShort(myapplication,"无历史消息");
			}
		});
		lstv.setEmptyView(emptyView);
		if (lstv == null)
		{
			return;
		}
		showlist();
	}


	Handler loadhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			showlist();
			for (int i = 0; i < BbPushMessageReceiver.msgReadListeners.size(); i++)
				BbPushMessageReceiver.msgReadListeners.get(i).onReadMessage();
		}
	};

	private void showlist() {
		getData() ;
		adapter = new RecentAdapter(myapplication, dataList);
		lstv.setAdapter(adapter);
	}


	private void getData() {
		dataList.clear();
		SQLiteDatabase sdb = dbHelper.getReadableDatabase();
		String sql = "";
		sql = "select userid,nickname,usericon,lastinfo,lasttime,messnum,lastnickname from friend order by lasttime desc";
		Cursor c = sdb.rawQuery(sql, null);
		while (c.moveToNext()) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put("userid", String.valueOf(c.getString(0)));
			item.put("username", String.valueOf(c.getString(1)));
			item.put("usericon", String.valueOf(c.getString(2)));
			item.put("lastinfo", String.valueOf(c.getString(3)));
			item.put("lastchattimer", String.valueOf(c.getString(4)));
			item.put("messnum", String.valueOf(c.getString(5)));
			item.put("lastnickname", String.valueOf(c.getString(6)));
			dataList.add(item);
		}
		if(c != null)
		{
			c.close();
		}
		if(sdb!=null) {
			sdb.close();
		}

	}

}