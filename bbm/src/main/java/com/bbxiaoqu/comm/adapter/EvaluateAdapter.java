package com.bbxiaoqu.comm.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bbxiaoqu.ImageOptions;
import com.bbxiaoqu.R;
import com.bbxiaoqu.comm.view.XCFlowLayout;
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
public class EvaluateAdapter extends BaseAdapter {

	private ViewHolderimg holder_img;
	private List<Map<String, Object>> list;
	private Context context;

	public EvaluateAdapter(Context context, List<Map<String, Object>> list) {
		
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
	/*private String mNames[] = {
			"2016","天气","风景",
			"浪漫","风光"
	};*/
	private View haveimg(int position, View convertView) {
		//if (convertView == null) {
			holder_img = new ViewHolderimg();			
			convertView = LayoutInflater.from(context).inflate(R.layout.listview_item_evaluate, null);
			holder_img.infouser = (TextView) convertView.findViewById(R.id.infouser);
			holder_img.addtime = (TextView) convertView.findViewById(R.id.addtime);
			holder_img.userhead = (XCRoundImageView) convertView.findViewById(R.id.userhead);//
			holder_img.evaluate = (TextView) convertView.findViewById(R.id.evaluate);
			holder_img.ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);
			holder_img.evaluatetag = (XCFlowLayout) convertView.findViewById(R.id.evaluatetag);
			convertView.setTag(list.get(position).get("id").toString());
		/*} else {
			holder_img = (ViewHolderimg) convertView.getTag();
		}*/
		String headfaceurl = "https://api.bbxiaoqu.com/uploads/"+list.get(position).get("headface").toString();
		if(list.get(position).get("headface").toString().length()>0) {
			ImageLoader.getInstance().displayImage(headfaceurl, holder_img.userhead, ImageOptions.getOptions());
		}else {
			holder_img.userhead.setImageResource(R.mipmap.xz_wo_icon);
		}
		holder_img.evaluate.setText(list.get(position).get("evaluate").toString());
		holder_img.infouser.setText(list.get(position).get("username").toString());
		holder_img.addtime.setText(list.get(position).get("addtime").toString());
		holder_img.ratingBar.setRating(Float.parseFloat(list.get(position).get("score").toString()));

		String tags=list.get(position).get("evaluatetag").toString().trim();
		if(tags!=null&&tags.length()>0) {
			String[] tagsarr = tags.split("\\|");
			if (tagsarr.length > 0) {
				ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(
						ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				lp.leftMargin = 20;
				lp.rightMargin = 20;
				lp.topMargin = 10;
				lp.bottomMargin = 10;
				for (int i = 0; i < tagsarr.length; i++) {
					TextView view = new TextView(context);
					view.setPadding(20, 5, 20, 5);

					view.setTextColor(Color.rgb(255,255,255));
					view.setText(tagsarr[i]);
					view.setTag(tagsarr[i]);
					view.setClickable(true);
					view.setBackgroundColor(Color.rgb(232,103,98));
					//view.setBackgroundResource(R.drawable.tags);
					holder_img.evaluatetag.addView(view, lp);
				}
			}
		}else
		{
			holder_img.evaluatetag.setVisibility(View.INVISIBLE);
		}
		return convertView;
	}
	


	private static class ViewHolderimg {
		TextView infouser;
		TextView addtime;
		XCRoundImageView userhead;
		TextView evaluate;
		RatingBar ratingBar;
		XCFlowLayout evaluatetag;
	}
	
	
	
	
	 
}
