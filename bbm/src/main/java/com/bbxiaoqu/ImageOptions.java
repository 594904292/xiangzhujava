package com.bbxiaoqu;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class ImageOptions {

	public static DisplayImageOptions options=null;

	public static DisplayImageOptions getOptions() {
		if(options==null)
		{
			options = new DisplayImageOptions.Builder()  
		     .showStubImage(R.mipmap.xz_pic_loading) //设置图片在下载期间显示的图片
		     .showImageForEmptyUri(R.mipmap.xz_pic_noimg) //设置图片Uri为空或是错误的时候显示的图片
		     .showImageOnFail(R.mipmap.xz_pic_loaderr) //设置图片加载/解码过程中错误时候显示的图片
		     .cacheInMemory(true)  //设置下载的图片是否缓存在内存中
		     .cacheOnDisk(true)  //设置下载的图片是否缓存在SD卡中
			 .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
			 .imageScaleType(ImageScaleType.IN_SAMPLE_INT)//设置图片以如何的编码方式显示
			 .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型
					//.decodingOptions(BitmapFactory.Options decodingOptions)//设置图片的解码配置
					//.delayBeforeLoading(0)//int delayInMillis为你设置的下载前的延迟时间
					//设置图片加入缓存前，对bitmap进行设置
					//.preProcessor(BitmapProcessor preProcessor)
					//.resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
					//.displayer(new RoundedBitmapDisplayer(20))//不推荐用！！！！是否设置为圆角，弧度为多少
					//.displayer(new FadeInBitmapDisplayer(100))//是否图片加载好后渐入的动画时间，可能会出现闪动
			 .build();
		}
		return options;
	}

	public static void setOptions(DisplayImageOptions options) {
		ImageOptions.options = options;
	}
	
}
