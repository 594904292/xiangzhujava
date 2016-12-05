package com.bbxiaoqu.comm.adapter;

import java.util.List;
import java.util.Map;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.ImageOptions;
import com.bbxiaoqu.R;
import com.bbxiaoqu.comm.widget.RoundAngleImageView;
import com.bbxiaoqu.comm.widget.XCRoundImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View.OnClickListener;
/**
 * <p>
 * 列表的LazyAdapter类
 * </p>
 * 
 * @author Ramboo
 * @date 2014-09-12 15:30
 */
public class ListLazyAdapter extends BaseAdapter implements OnClickListener {

	@Override
	public void onClick(View v) {
		mCallback.click(v);
	}

	public final class ViewHolder {
		XCRoundImageView thumbImage;
		ImageView sex_img;
		TextView disTitleText;
		TextView distimeText;
		TextView disContentText;
		Button button;
	}
	private Callback mCallback;
	private List<Map<String, String>> datas;
	private static LayoutInflater inflater = null;
	private DemoApplication myapplication;
	private String username;
	private boolean issolution=false;
	private String solutionid;
	private Activity myactivity;

	public interface Callback {
		public void click(View v);
	}

	public ListLazyAdapter(Activity activity, List<Map<String, String>> datas,Callback callback,String senduser, boolean issolution, String solutionid) {

		//getSystemService是Android很重要的一个API，它是Activity的一个方法，根据传入的NAME来取得对应的Object，然后转换成相应的服务对象
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		myapplication = (DemoApplication) activity.getApplication();
		this.myactivity=activity;
		this.datas = datas;
		this.mCallback = callback;;
		this.username=senduser;
		this.issolution=issolution;
		this.solutionid=solutionid;
	}

	public int getCount() {
		return datas.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	/**
	 * 为评论列表中每一行数据设置值, 之前在列表里已经判断过,因此此处不用判断数据是否已经被删除
	 */
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.group_list_item, null);
			holder.thumbImage = (XCRoundImageView) convertView.findViewById(R.id.row_user_image);
			holder.sex_img = (ImageView) convertView.findViewById(R.id.sex_img);
			holder.disTitleText = (TextView) convertView.findViewById(R.id.row_discuss); // 评论的抬头,比如可能需要显示 查无此人咋办 回复
			holder.distimeText = (TextView) convertView.findViewById(R.id.row_discuss_time); // 评论时间
			holder.disContentText = (TextView) convertView.findViewById(R.id.row_discuss_content); // 评论的一行
			holder.button = (Button) convertView.findViewById(R.id.button1);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Map<String, String> row = datas.get(position);
		String id = "0";
		if(row.get("id")!=null)
		{
			id=row.get("id");
		}
		String headface = row.get("headface");

		String distime = row.get("distime");
		String content = row.get("content");
		holder.disTitleText.setText(this.rowTextForResult(row));
		if(row.get("sex").equals ("0"))
		{
			holder.sex_img.setImageResource (R.mipmap.xz_nan_icon);
		}else
		{
			holder.sex_img.setImageResource (R.mipmap.xz_nv_icon);
		}
		holder.distimeText.setText(distime);
		holder.disContentText.setText(content);
		if(this.issolution)
		{
			if(id.equals(this.solutionid))
			{
				holder.button.setText("最佳");
				holder.button.setVisibility(View.VISIBLE);
			}else
			{
				holder.button.setVisibility(View.GONE);
			}
			holder.button.setEnabled(false);//禁用
		}else
		{
			holder.button.setEnabled(false);//禁用
			holder.button.setVisibility(View.GONE);
		}
		holder.thumbImage.setOnClickListener(this);
		holder.thumbImage.setTag("head_"+position);
		String fileName = myapplication.getlocalhost() + "uploads/" + headface;
		if(headface.length()>0) {
			ImageLoader.getInstance().displayImage(fileName, holder.thumbImage, ImageOptions.getOptions());
		}else
		{
			holder.thumbImage.setImageResource(R.mipmap.xz_wo_icon);
		}
		return convertView;
	}

	private String rowTextForResult(Map<String, String> row) {
		StringBuilder res = new StringBuilder();
		String username = row.get("username").toString();// 当前人
	/*	String uid = row.get("uid").toString();// 当前uid
		String puid = row.get("puid").toString();
		String pname = row.get("pname").toString();*/
		res.append(username);
		return res.toString();
	}

}
