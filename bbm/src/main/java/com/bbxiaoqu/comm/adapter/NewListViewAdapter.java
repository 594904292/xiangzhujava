package com.bbxiaoqu.comm.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.ImageOptions;
import com.bbxiaoqu.R;
import com.bbxiaoqu.comm.widget.XCRoundImageView;
import com.bbxiaoqu.comm.widget.ImageViewSubClass;
import com.bbxiaoqu.comm.widget.XCRoundImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.util.List;
import java.util.Map;
import android.view.View.OnClickListener;

public class NewListViewAdapter extends BaseAdapter implements OnClickListener {

	//private ViewHolderimg holder_img;
	private List<Map<String, Object>> list;

	private Activity activity;
	private Callback mCallback;


	public interface Callback {
	         public void click(View v);
	     }

	public NewListViewAdapter(Activity activity, List<Map<String, Object>> list,Callback callback) {
		this.activity=activity;

		this.list = list;
		mCallback = callback;

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
		if(list.get(position).get("infocatagroy").toString().equals("gg")) {
			ViewHolderimg holder_img = new ViewHolderimg();
			convertView = LayoutInflater.from(activity).inflate(R.layout.listview_item_gonggao, null);
			holder_img.headface = (XCRoundImageView) convertView.findViewById(R.id.headface);
			holder_img.username = (TextView) convertView.findViewById(R.id.username);
			holder_img.timeago = (TextView) convertView.findViewById(R.id.timeago);
			holder_img.sendcontent = (TextView) convertView.findViewById(R.id.sendcontent);
			holder_img.headface.setImageResource(R.mipmap.xz_wo_icon);
			holder_img.username.setText(list.get(position).get("sendnickname").toString());
			holder_img.timeago.setText(list.get(position).get("date").toString());
			if (list.get(position).get("content") != null) {
				holder_img.sendcontent.setText(list.get(position).get("content").toString());
			} else {
				holder_img.sendcontent.setText("");
			}
			return convertView;
		}else {
			ViewHolderimg holder_img = new ViewHolderimg();
			convertView = LayoutInflater.from(activity).inflate(R.layout.listview_item_info, null);
			holder_img.headface = (XCRoundImageView) convertView.findViewById(R.id.headface);
			holder_img.username = (TextView) convertView.findViewById(R.id.username);
			holder_img.sex = (ImageView) convertView.findViewById(R.id.sex_img);
			holder_img.score = (TextView) convertView.findViewById(R.id.score);


//			Typeface tf=((DemoApplication)this.activity.getApplication()).getHannotateSCfont();
//			holder_img.score.setTypeface(tf);


			holder_img.address = (TextView) convertView.findViewById(R.id.address);
			holder_img.distance = (TextView) convertView.findViewById(R.id.distance);
			holder_img.timeago = (TextView) convertView.findViewById(R.id.timeago);
			holder_img.sendcontent = (TextView) convertView.findViewById(R.id.sendcontent);

			holder_img.img_row = (RelativeLayout) convertView.findViewById(R.id.img_row);

			holder_img.del = (TextView) convertView.findViewById(R.id.del);
			holder_img.tag1 = (TextView) convertView.findViewById(R.id.tag1);
			holder_img.tag2 = (TextView) convertView.findViewById(R.id.tag2);

			holder_img.statusimg = (ImageView) convertView.findViewById(R.id.statusimg);
			convertView.setTag(list.get(position).get("guid").toString().trim());

			String headfaceurl = "https://api.bbxiaoqu.com/uploads/" + list.get(position).get("headface").toString();
			if (list.get(position).get("headface").toString().length() > 0) {
				ImageLoader.getInstance().displayImage(headfaceurl, holder_img.headface, ImageOptions.getOptions());
			} else {
				holder_img.headface.setImageResource(R.mipmap.xz_wo_icon);
			}
			holder_img.username.setText(list.get(position).get("sendnickname").toString());
			if (list.get(position).get("sex").toString().equals("0")) {
				holder_img.sex.setImageResource(R.mipmap.xz_nan_icon);
			} else {
				holder_img.sex.setImageResource(R.mipmap.xz_nv_icon);
			}
			holder_img.score.setText(list.get(position).get("score").toString()+"积分");
			holder_img.address.setText(list.get(position).get("street").toString());
			holder_img.distance.setText(list.get(position).get("distance").toString());
			holder_img.timeago.setText(list.get(position).get("date").toString());
			if (list.get(position).get("content") != null) {
				holder_img.sendcontent.setText(list.get(position).get("content").toString());
			} else {
				holder_img.sendcontent.setText("");
			}
			int num = 0;
			if (list.get(position).get("icon").toString().trim().length() > 0) {
				WindowManager wm1 = this.activity.getWindowManager();
				int width = wm1.getDefaultDisplay().getWidth() - 30;//左右有15
				int w = 0;
				int h = 0;
				String photo = list.get(position).get("icon").toString().trim();
				String[] arr = photo.split(",");
				if (arr.length > 0) {
					num = arr.length;
					if (num == 1) {
						w = width / 4;
						h = width / 4;
					} else {
						w = width / 4;
						h = width / 4;
					}
					LinearLayout m_LinearLayout = new LinearLayout(activity);
					m_LinearLayout.setOrientation(LinearLayout.HORIZONTAL);
					LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(width - 20, h + 20);
					int pic_num = arr.length;
					if (pic_num > 4) {
						pic_num = 4;
					}
					for (int i = 0; i < pic_num; i++) {
						ImageViewSubClass image = new ImageViewSubClass(activity);
						image.setTag(list.get(position).get("guid").toString().trim() + "_" + i);
						image.setAdjustViewBounds(true);
						ImageLoader.getInstance().displayImage(DemoApplication.getInstance().getlocalhost() + "uploads/" + arr[i], image, ImageOptions.getOptions());
						image.setScaleType(ImageView.ScaleType.FIT_XY);
						image.setPadding(0, 0, 0, 0);
						LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(w - 20, h - 10);
						layoutParams.setMargins(10, 10, 10, 10);
						m_LinearLayout.addView(image, layoutParams);
					}
					holder_img.img_row.addView(m_LinearLayout, param);
				}
			}
			holder_img.del.setTag(position);
			DemoApplication myapplication = (DemoApplication) this.activity.getApplicationContext();
			if (list.get(position).get("senduserid").toString().equals(myapplication.getUserId())) {
				holder_img.del.setText("删除");
				holder_img.del.setOnClickListener(this);
			} else {
				holder_img.del.setVisibility(View.GONE);
			}
			holder_img.del.setCompoundDrawablePadding(10);
			holder_img.tag1.setCompoundDrawablePadding(10);
			holder_img.tag2.setCompoundDrawablePadding(10);
			holder_img.tag1.setText(list.get(position).get("tag1").toString());
			holder_img.tag2.setText(list.get(position).get("tag2").toString());
			if (list.get(position).get("status").toString().equals("0")) {
				holder_img.statusimg.setImageResource(R.mipmap.xz_qiuzhu_icon);
			} else if (list.get(position).get("status").toString().equals("1")) {
				holder_img.statusimg.setImageResource(R.mipmap.xz_qiuzhu_icon);
			} else if (list.get(position).get("status").toString().equals("2")) {
				holder_img.statusimg.setImageResource(R.mipmap.xz_yijiejue_icon);
			}
			return convertView;
		}
	}
	
	
	

	private static class ViewHolderimg {
		XCRoundImageView headface;
		TextView username;
		ImageView sex;
		TextView score;
		TextView address;
		TextView distance;
		TextView timeago;
		TextView sendcontent;
		RelativeLayout img_row;
		TextView del;
		TextView tag1;
		TextView tag2;
		ImageView statusimg;
	}



	@Override
	public void onClick(View v) {
		mCallback.click(v);
	}


}
