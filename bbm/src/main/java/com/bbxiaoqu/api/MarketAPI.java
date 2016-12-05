/*
 * Copyright (C) 2010 mAPPn.Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bbxiaoqu.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.bbxiaoqu.api.ApiAsyncTask.ApiRequestListener;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;



/**
 * GfanMobile aMarket API utility class
 * 
 * @author dzyang
 * @date 2010-10-29
 * @since Version 0.4.0
 */
public class MarketAPI {

    /** 机锋市场API host地址 */
    public static final String API_BASE_URL = 
        // real host
      "http://api.bbxiaoqu.com/";
        // test host

     // 机锋市场 API URLS
    static final String[] API_URLS = {
            // ACTION_LOGIN
            API_BASE_URL + "login.php",
            // ACTION_REGISTER
            API_BASE_URL + "register",
            // ACTION_GETDYNAMICS
            API_BASE_URL + "getdynamics.php",
            // ACTION_GONGGAO
            API_BASE_URL + "gonggao.php",
            // ACTION_GETINFO
            API_BASE_URL + "getinfo_v1.php",
            // ACTION_GETITEMNUM
            API_BASE_URL + "getitemnum.php",
            // ACTION_GETINFOS
            API_BASE_URL + "getinfos_v2.php",
            // ACTION_GETINFOS
            API_BASE_URL + "getfriends.php",
            //ACTION_GETXIAOQUS
             API_BASE_URL + "getxiaoqu.php",
             // ACTION_GETFWINFOS
             API_BASE_URL + "getfwinfos.php",
             // ACTION_SYSINFO
             API_BASE_URL + "sys.php",
            // ACTION_GETUESERINFO
             API_BASE_URL + "getuserinfo.php",
             // ACTION_GETMYRANK
             API_BASE_URL + "myrank_v1.php",
             // ACTION_GETRANK
             API_BASE_URL + "rank_v1.php",

             // ACTION_GETUSERSUMMARYINFO
             API_BASE_URL + "getusersummary.php"
     };
    
    /** 登录 */
    public static final int ACTION_LOGIN = 0;
    /** 注册 */
    public static final int ACTION_REGISTER = 1;
    /** 小区动态 */
    public static final int ACTION_GETDYNAMICS = 2;    
    /**系统公告 */
    public static final int ACTION_GONGGAO = 3;    
    /**获取信息 */
    public static final int ACTION_GETINFO = 4;
    /**获取信息统计数 */
    public static final int ACTION_GETITEMNUM = 5;
    /**获取信息 */
    public static final int ACTION_GETINFOS = 6;
    /**获取朋友 */
    public static final int ACTION_GETFRIENDS =7;
    /**获取小区 */
    public static final int ACTION_GETXIAOQUS =8;
    /**获取服务 */
    public static final int ACTION_GETFWINFOS = 9;
    /**获取系统服务器信息 */
    public static final int ACTION_SYSTEMINFO = 10;
    /**获取系统服务器信息 */
    public static final int ACTION_GETUESERINFO = 11;
    /**获取系统服务器信息 */
    public static final int ACTION_MYRANK = 12;
    /**获取系统服务器信息 */
    public static final int ACTION_RANK = 13;
    /**获取系统服务器信息 */
    public static final int ACTION_GETUERSUMMARY = 14;
    /**
	 * Register API<br>
	 */
	public static void register(Context context, ApiRequestListener handler,
			String userid, String password, String email) {
		final HashMap<String, Object> params = new HashMap<String, Object>(3);
		params.put("username", userid);
		params.put("password", password);
		params.put("email", email);
		new ApiAsyncTask(context, ACTION_REGISTER, handler, params).execute();
	}
	
	/**
    * Login API<br>
    * Do the login process, UserName, Password must be provided.<br>
    */
   public static void login(Context context, ApiRequestListener handler,
           String username, String password) {
       final HashMap<String, Object> params = new HashMap<String, Object>(2);
       params.put("_userid", username);
       params.put("password", password);
       new ApiAsyncTask(context,ACTION_LOGIN, handler, params).execute();
   }
    
   
	/**
    * dynamics API<br>
    * Do the login process, UserName, Password must be provided.<br>
    */
   public static void dynamics(Context context, ApiRequestListener handler,
           String userid, String rang,String start,String limit) {
       final HashMap<String, Object> params = new HashMap<String, Object>(2);
       params.put("userid", userid);
       params.put("rang", rang);
       params.put("start", start);
       params.put("limit", limit);
       new ApiAsyncTask(context,ACTION_GETDYNAMICS, handler, params).execute();
   }
   
   
   
	/**
    * gonggao API<br>
    */
   public static void gonggao(Context context, ApiRequestListener handler) {
       final HashMap<String, Object> params = new HashMap<String, Object>(2);      
       new ApiAsyncTask(context,ACTION_GONGGAO, handler, params).execute();
   }
   
	/**
    * getinfo API<br>
    */
   public static void getinfo(Context context, ApiRequestListener handler,String idtype,String guid) {
       final HashMap<String, Object> params = new HashMap<String, Object>(2);     
       params.put("idtype", idtype);
       params.put("guid", guid);
       new ApiAsyncTask(context,ACTION_GETINFO, handler, params).execute();      
   }


    /**
     * getinfo API<br>
     */
    public static void getinfo(Context context, ApiRequestListener handler,String guid) {
        final HashMap<String, Object> params = new HashMap<String, Object>(2);
        params.put("idtype", "guid");
        params.put("guid", guid);
        new ApiAsyncTask(context,ACTION_GETINFO, handler, params).execute();
    }


    /**
    * getItemNum API<br>
    */
   public static void getItemNum(Context context, ApiRequestListener handler,String guid) {
       final HashMap<String, Object> params = new HashMap<String, Object>(2);     
       params.put("_guid", guid);
       new ApiAsyncTask(context,ACTION_GETITEMNUM, handler, params).execute();
   }
   
   
	/**
    * getinfos API<br>
    */
   public static void getINfos(Context context, ApiRequestListener handler,String userid,String latitude,String longitude,String rang,String visiblerange,String community_id,String status,int start,int limit) {
       final HashMap<String, Object> params = new HashMap<String, Object>(2);
       params.put("_userid", userid);
       params.put("latitude", latitude);
       params.put("longitude", longitude);
       params.put("_rang", rang);
       params.put("_visiblerange", visiblerange);
       params.put("_community_id", community_id);
       params.put("_status", status);
       params.put("_start", String.valueOf(start));
       params.put("_limit", String.valueOf(limit));
       new ApiAsyncTask(context,ACTION_GETINFOS, handler, params).execute();
   }
   
   public static void getFriends(Context context, ApiRequestListener handler,String userid) {
       final HashMap<String, Object> params = new HashMap<String, Object>(2);
       params.put("_userid", userid);
       new ApiAsyncTask(context,ACTION_GETFRIENDS, handler, params).execute();
   }


    public static void geXiaoqus(Context context, ApiRequestListener handler,String latitude,String longitude,String keyword) {
        final HashMap<String, Object> params = new HashMap<String, Object>(2);
        params.put("latitude", latitude);
        params.put("longitude", longitude);
        if(keyword!=null&&keyword.length()>0) {
            params.put("keyword", keyword);
        }else
        {
            params.put("keyword", "");
        }
        new ApiAsyncTask(context,ACTION_GETXIAOQUS, handler, params).execute();
    }


    /**
     * getinfos API<br>
     */
    public static void getFwINfos(Context context, ApiRequestListener handler,String userid,String latitude,String longitude,String rang,String status,int start,int limit) {
        final HashMap<String, Object> params = new HashMap<String, Object>(7);
        params.put("_userid", userid);
        params.put("latitude", latitude);
        params.put("longitude", longitude);
        params.put("_rang", rang);
        params.put("_status", status);
        params.put("_start", String.valueOf(start));
        params.put("_limit", String.valueOf(limit));
        new ApiAsyncTask(context,ACTION_GETFWINFOS, handler, params).execute();
    }


    public static void getSystemInfo(Context context, ApiRequestListener handler) {
        final HashMap<String, Object> params = new HashMap<String, Object>(1);

        new ApiAsyncTask(context,ACTION_SYSTEMINFO, handler,params).execute();
    }


    public static void getUserInfo(Context context, ApiRequestListener handler,String userid) {
        final HashMap<String, Object> params = new HashMap<String, Object>(7);
        params.put("_userid", userid);
        new ApiAsyncTask(context,ACTION_GETUESERINFO, handler, params).execute();
    }

    public static void getMyrank(Context context, ApiRequestListener handler,String userid) {
        final HashMap<String, Object> params = new HashMap<String, Object>(7);
        params.put("_userid", userid);
        new ApiAsyncTask(context,ACTION_MYRANK, handler, params).execute();
    }


    public static void getRANK(Context context, ApiRequestListener handler,String order) {
        final HashMap<String, Object> params = new HashMap<String, Object>(7);
        params.put("_order", order);
        new ApiAsyncTask(context,ACTION_RANK, handler, params).execute();
    }



    public static void getUserSummaryInfo(Context context, ApiRequestListener handler,String userid) {
        final HashMap<String, Object> params = new HashMap<String, Object>(7);
        params.put("_userid", userid);
        new ApiAsyncTask(context,ACTION_GETUERSUMMARY, handler, params).execute();
    }
}