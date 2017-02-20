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
public class FriendsAdapter extends BaseAdapter {
	
	private ViewHolderimg holder_img;
	private List<Map<String, Object>> list;
	private Context context;	

	public FriendsAdapter(Context context, List<Map<String, Object>> list) {
		
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
			convertView = LayoutInflater.from(context).inflate(R.layout.listview_item_friends, null);
			holder_img.username = (TextView) convertView.findViewById(R.id.username);
			holder_img.userhead = (XCRoundImageView) convertView.findViewById(R.id.userhead);
			convertView.setTag(holder_img);
		} else {
			holder_img = (ViewHolderimg) convertView.getTag();
		}
		
		String fileName =  "https://api.bbxiaoqu.com/uploads/" + list.get(position).get("headface").toString();
		ImageLoader.getInstance().displayImage(fileName,holder_img.userhead,
				ImageOptions.getOptions());
		holder_img.username.setText(list.get(position).get("username").toString());
		return convertView;
	}
	


	private static class ViewHolderimg {
		TextView username;
		XCRoundImageView userhead;
	
	}
	
	
	
	
	 
}
