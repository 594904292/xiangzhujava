package com.bbxiaoqu.comm.service.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bbxiaoqu.comm.tool.L;

import java.util.ArrayList;
import java.util.List;

public class MemberTsService {
	public DatabaseHelper dbHelper;
	public MemberTsService(Context context){
		dbHelper=new DatabaseHelper(context);
	}
	
	public ArrayList<String> getmemberids(String userid) {
		List<String> guidlist = new ArrayList<String>();
		SQLiteDatabase sdb = dbHelper.getReadableDatabase();
		String sql="";		
		sql = "select * from memberts  where userid=?";
		L.i("query",sql);
		String obj[]=new String[]{userid};
		Cursor c = sdb.rawQuery(sql, obj);
		while (c.moveToNext()) {
			guidlist.add(String.valueOf(c.getString(1).toString()));
		}
		c.close();
		sdb.close();
		return (ArrayList<String>) guidlist;
	}
	
	public boolean addts(String memberid,String userid){
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		String sql="insert into memberts (memberid,userid) values(?,?)";
		Object obj[]={memberid,userid};
		sdb.execSQL(sql, obj);
		sdb.close();
		return true;
	}
	
	
	public boolean removets(String memberid,String userid){
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		String sql="delete from memberts where memberid=? and userid=?";
		Object obj[]={memberid,userid};
		sdb.execSQL(sql, obj);
		sdb.close();
		return true;
	}
	
	
	
	public boolean isexit(String memberid,String userid) {
		// 读写数据库
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		String sql="select * from memberts where memberid=? and userid=?";
		Cursor cursor=sdb.rawQuery(sql, new String[]{memberid,userid});
		if(cursor.moveToFirst()==true){
			cursor.close();
			sdb.close();
			return true;
		}	
		cursor.close();
		sdb.close();
		return false;
	}
	
	public void close() {  
	     if (dbHelper != null) {  
	    	 dbHelper.close();  
	     }  
	 }  
}
