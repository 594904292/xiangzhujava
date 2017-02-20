package com.bbxiaoqu.comm.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.ImageOptions;
import com.bbxiaoqu.R;
import com.bbxiaoqu.comm.widget.XCRoundImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;
import java.util.Map;

/**
 * @author SunnyCoffee
 * @date 2014-2-2
 * @version 1.0
 * @desc 适配器
 * 
 */
public class ReportsAdapter extends BaseAdapter {

	private ViewHolderimg holder_img;
	private List<Map<String, Object>> list;
	private Context context;
	private Activity activity;
	public ReportsAdapter(Context context,Activity activity, List<Map<String, Object>> list) {
		
		this.list = list;
		this.context = context;
		this.activity=activity;

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
			convertView = LayoutInflater.from(context).inflate(R.layout.listview_item_report, null);
			holder_img.userhead = (XCRoundImageView) convertView.findViewById(R.id.userhead);
			holder_img.sex = (ImageView) convertView.findViewById(R.id.sex);
			holder_img.order = (TextView) convertView.findViewById(R.id.order);
			holder_img.nickname = (TextView) convertView.findViewById(R.id.nickname);
			holder_img.nums = (TextView) convertView.findViewById(R.id.nums);
			holder_img.ratingbar = (RatingBar) convertView.findViewById(R.id.ratingbar);
			convertView.setTag(holder_img);
		} else {
			holder_img = (ViewHolderimg) convertView.getTag();
		}
		if(list.get(position).get("nickname").toString().length ()<1) {

			String telphone=list.get (position).get ("username").toString ();
			telphone="".concat("*").concat(telphone.substring(telphone.length()-4,telphone.length()));
			holder_img.nickname.setText (telphone);
		}else
		{
			holder_img.nickname.setText (list.get (position).get ("nickname").toString ());
		}
		if(list.get(position).get("headface")!=null&&list.get(position).get("headface").toString ().length ()>0) {
			String headfaceurl = "https://api.bbxiaoqu.com/uploads/" + list.get (position).get ("headface").toString ();
			ImageLoader.getInstance ().displayImage (headfaceurl, holder_img.userhead, ImageOptions.getOptions ());
		}else
		{
			holder_img.userhead.setImageResource(R.mipmap.xz_wo_icon);
		}
		if(list.get(position).get("sex").toString().equals ("0")) {
			holder_img.sex.setImageResource (R.mipmap.xz_nan_icon);
		}else
		{
			holder_img.sex.setImageResource (R.mipmap.xz_nv_icon);
		}
		/*WindowManager wm = this.activity.getWindowManager();
		int width = wm.getDefaultDisplay().getWidth();
		int leftw=width/3;

        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) holder_img.ratingbar.getLayoutParams();
                *//*new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);*//*
        lp.leftMargin = leftw;*/

		/*WindowManager.LayoutParams param1 = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
		param1.addRule(RelativeLayout.RIGHT_OF, 1);*/
        //holder_img.ratingbar.setLayoutParams(lp);
		holder_img.ratingbar.setRating(Float.parseFloat(list.get(position).get("score").toString()));
		holder_img.nums.setText(list.get(position).get("nums").toString());

		holder_img.order.setText("第"+list.get(position).get("order").toString()+"名");

		return convertView;
	}
	


	private static class ViewHolderimg {
		XCRoundImageView userhead;
		TextView nickname;
		ImageView sex;
		TextView order;
		TextView nums;
		RatingBar ratingbar;

	}
	
	
	
	
	 
}
