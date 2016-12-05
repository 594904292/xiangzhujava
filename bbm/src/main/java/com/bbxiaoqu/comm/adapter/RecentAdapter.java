package com.bbxiaoqu.comm.adapter;

import java.util.List;
import java.util.Map;

import com.bbxiaoqu.ImageOptions;
import com.bbxiaoqu.R;
import com.bbxiaoqu.comm.widget.XCRoundImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author SunnyCoffee
 * @date 2014-2-2
 * @version 1.0
 * @desc 适配器
 * 
 */
public class RecentAdapter extends BaseAdapter {
	
	private ViewHolderimg holder_img;
	private List<Map<String, Object>> list;
	private Context context;	

	public RecentAdapter(Context context, List<Map<String, Object>> list) {
		
		this.list = list;
		this.context = context;				

	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		
		return haveimg(position, convertView);
		
	}

	private View haveimg(int position, View convertView) {
		if (convertView == null) {
			holder_img = new ViewHolderimg();			
			convertView = LayoutInflater.from(context).inflate(R.layout.listview_item_recent, null);
			holder_img.username = (TextView) convertView.findViewById(R.id.username);
			holder_img.lastchattimer = (TextView) convertView.findViewById(R.id.lastchattimer);
			holder_img.userhead = (XCRoundImageView) convertView.findViewById(R.id.userhead);
			holder_img.lastinfo = (TextView) convertView.findViewById(R.id.evaluate);
			holder_img.messnum = (TextView) convertView.findViewById(R.id.messnum);
			convertView.setTag(holder_img);
		} else {
			holder_img = (ViewHolderimg) convertView.getTag();
		}
		

	
	   	
		String fileName =  "http://api.bbxiaoqu.com/uploads/" + list.get(position).get("usericon").toString();




		if(list.get(position).get("usericon").toString().length()>0) {
			ImageLoader.getInstance().displayImage(fileName,holder_img.userhead,
					ImageOptions.getOptions());
		}else
		{
			holder_img.userhead.setImageResource(R.mipmap.xz_wo_icon);
		}


		holder_img.lastinfo.setText(list.get(position).get("lastnickname").toString()+":"+list.get(position).get("lastinfo").toString());
		if(list.get(position).get("messnum").toString().equals("0"))
		{
			holder_img.messnum.setText("");
			holder_img.messnum.setVisibility(View.GONE);
		}else
		{
			holder_img.messnum.setText(list.get(position).get("messnum").toString());
			holder_img.messnum.setVisibility(View.VISIBLE);
		}
		
		holder_img.username.setText(list.get(position).get("username").toString());
		holder_img.lastchattimer.setText(list.get(position).get("lastchattimer").toString());	
		return convertView;
	}
	


	private static class ViewHolderimg {
		TextView username;		
		TextView lastchattimer;
		XCRoundImageView userhead;
		TextView lastinfo;
		TextView messnum;
	}
	
	
	
	
	 
}
