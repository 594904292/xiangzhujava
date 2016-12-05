package com.bbxiaoqu.comm.service.db;

import com.bbxiaoqu.comm.bean.ChatMessage;
import com.bbxiaoqu.comm.tool.L;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class ChatDB
{
	public DatabaseHelper dbHelper;
	public ChatDB(Context context)
	{		
		dbHelper=new DatabaseHelper(context);
	}
	//select * from chat where senduserid<>'369' and touserid=369
	public void add(ChatMessage chatMessage,boolean isreaded)
	{
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		if(isreaded) {
			sdb.execSQL(
					"insert into [chat] (senduserid,touserid,guid,message,date,readed) values(?,?,?,?,?,?)",
					new Object[]{
							chatMessage.getSenduserId(),
							chatMessage.getTouserId(),
							chatMessage.getGuid(),
							chatMessage.getMessage(),
							chatMessage.getDateStr(),
							'1'
					});
		}else
		{
			sdb.execSQL(
					"insert into [chat] (senduserid,touserid,guid,message,date,readed) values(?,?,?,?,?,?)",
					new Object[]{
							chatMessage.getSenduserId(),
							chatMessage.getTouserId(),
							chatMessage.getGuid(),
							chatMessage.getMessage(),
							chatMessage.getDateStr(),
							'0'
					});
		}
		sdb.close();		
	}
	

	public boolean isexit(String guid){
		 boolean v=false;
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		String sql="select * from [chat] where guid=?";
		Cursor cursor=sdb.rawQuery(sql, new String[]{guid});			
		if(cursor.moveToFirst()==true)
		{		
			L.i("ChatDB","guid hava:"+guid+","+cursor.getString(cursor.getColumnIndex("guid")));
			cursor.close();
			sdb.close();
			v= true;
		}else
		{
			L.i("ChatDB","guid not hava:"+guid);
			cursor.close();
			sdb.close();
			v= false;
		}
		return v;
	}
	
	public long unreadnum(String from,String to){
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		String sql="select count(*) from [chat] where senduserid=? and touserid=? and readed=0";
		Cursor cursor=sdb.rawQuery(sql, new String[]{from,to});
		cursor.moveToFirst();  
		long num= cursor.getLong(0);  	
	    cursor.close();
		sdb.close();
		return num;
	}

	public long allunreadnum(){
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		String sql="select count(*) from [chat] where  readed=0";
		Cursor cursor=sdb.rawQuery(sql, null);
		cursor.moveToFirst();
		long num= cursor.getLong(0);
		cursor.close();
		sdb.close();
		return num;
	}


	public void readchat(String from,String to){
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		String sql="update [chat] set readed='1' where senduserid ='"+from+"' and touserid='"+to+"'";
		sdb.execSQL(sql);
		sdb.close();		
	}

	
}
