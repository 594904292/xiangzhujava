package com.bbxiaoqu.comm.service;

import com.bbxiaoqu.DemoApplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Android Service 示例
 * 
 * @author dev
 * 
 */
public class ServiceDemo extends Service {
	private static final String TAG = "ServiceDemo" ;
	public static final String ACTION = "com.bbxiaoqu.comm.service.ServiceDemo";	
	private DemoApplication myapplication;
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.v(TAG, "ServiceDemo onBind");
		return null;
	}
	
	@Override
	public void onCreate() {
		Log.v(TAG, "ServiceDemo onCreate");
		super.onCreate();
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		Log.v(TAG, "ServiceDemo onStart");
		super.onStart(intent, startId);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(TAG, "ServiceDemo onStartCommand");
		myapplication = (DemoApplication) getApplication();
		//new Thread(new Threadlocaltion()).start();  //开启线程
		//new Thread(new Threaddatasync()).start();  //开启线程
		
		while(true)
		{
			
			System.out.println("check");
			try {
				Thread.sleep(1000*30);
			} catch (InterruptedException e) {
				
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	
    
    // 线程类  
    class Threaddatasync implements Runnable {  
        @Override  
        public void run() {  
            // TODO Auto-generated method stub  
        	while(true)
        	{
        	//获取最大的			
        		//myapplication.getInstance().startxmpp();
			try {
				Thread.sleep(1000*60*1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
        	}
        }

	}
    
}
