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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import com.bbxiaoqu.api.util.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;


/**
 * API 响应结果解析工厂类，所有的API响应结果解析需要在此完成。
 * 
 * @author dzyang
 * @date 2011-4-22
 * 
 */
@SuppressLint("NewApi")
public class ApiResponseFactory {

    public static byte[] read(InputStream inStream)
			throws Exception {
	
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = inStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, len);
			}
			inStream.close();
			// ProgressDialog1.dismiss();
			return outputStream.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
	
		}
		return null;
	}

	/**
     * 解析市场API响应结果
     * 
     * @param action
     *            请求API方法
     *            HTTP Response
     * @return 解析后的结果（如果解析错误会返回Null）
     */
	public static Object getResponse(Context context, int action,
			HttpResponse httpResponse) {
		InputStream in = null;
		if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {			
			try {
				in = httpResponse.getEntity().getContent();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
		String requestMethod = "";
        Object result = null;       
        switch (action) {		
		case MarketAPI.ACTION_REGISTER:
		    // 注册
		    requestMethod = "ACTION_REGISTER";
		    result = parseLoginOrRegisterResult(in);
		    break;
		case MarketAPI.ACTION_LOGIN:
		    // 登录
		    requestMethod = "ACTION_LOGIN";
		    result = parseLoginOrRegisterResult(in);
		    break;
		case MarketAPI.ACTION_GETDYNAMICS:
		    // 小区动态
		    requestMethod = "ACTION_GETDYNAMICS";
		    result = parseGetDaymicResult(in);
		    break;
		case MarketAPI.ACTION_GONGGAO:
		    // 公告
		    requestMethod = "ACTION_GONGGAO";
		    result = parseGongGaoResult(in);
		    break;
		case MarketAPI.ACTION_GETINFO:
		    // 单条信息
		    requestMethod = "ACTION_GETINFO";
		    result = parseInfoResult(in);
		    break;
		case MarketAPI.ACTION_GETITEMNUM:
		    // 单条信息的评论数
		    requestMethod = "ACTION_GETITEMNUM";
		    result = parseItemNumsResult(in);
		    break;
		case MarketAPI.ACTION_GETINFOS:
		    // 很多条消息
		    requestMethod = "ACTION_GETINFOS";
		    result = parseGetInfosResult(in);
		    break;    
		case MarketAPI.ACTION_GETFRIENDS:
		    // 很多条消息
		    requestMethod = "ACTION_GETINFOS";
		    result = parseGetFriendsResult(in);
		    break;
		case MarketAPI.ACTION_GETXIAOQUS:
				// 很多条消息
				requestMethod = "ACTION_GETXIAOQUS";
				result = parseGetXiaoqusResult(in);
				break;
		case MarketAPI.ACTION_GETFWINFOS:
				// 很多条消息
				requestMethod = "ACTION_GETFWINFOS";
				result = parseGetFwInfosResult(in);
				break;
		case MarketAPI.ACTION_SYSTEMINFO:
				// 很多条消息
				requestMethod = "ACTION_SYSTEMINFO";
				result = parseSystemXmlResult(in);
				break;
			case MarketAPI.ACTION_GETUESERINFO:
				// 很多条消息
				requestMethod = "ACTION_GETUESERINFO";
				result = parseGetUserInfoResult(in);
				break;

			case MarketAPI.ACTION_MYRANK:
				// 很多条消息
				requestMethod = "ACTION_MYRANK";
				result = parseResult(in);
				break;
			case MarketAPI.ACTION_RANK:
				// 很多条消息
				requestMethod = "ACTION_RANK";
				result = parseResult(in);
				break;
			case MarketAPI.ACTION_GETUERSUMMARY:
				// 很多条消息
				requestMethod = "ACTION_GETUERSUMMARY";
				result = parseResult(in);
				break;
			case MarketAPI.ACTION_GETUSERVISIBLERANGE:
				// 很多条消息
				requestMethod = "ACTION_GETUERSUMMARY";
				result = parseResult(in);
				break;
			case MarketAPI.ACTION_GETUSERVISIBLECOMMUNITY:
				// 很多条消息
				requestMethod = "ACTION_GETUERSUMMARY";
				result = parseResult(in);
				break;
			case MarketAPI.ACTION_DAILYLOGIN:
				// 很多条消息
				requestMethod = "ACTION_GETUERSUMMARY";
				result = parseResult(in);
				break;
			case MarketAPI.ACTION_GETSHOPINFO:
				// 很多条消息
				requestMethod = "ACTION_GETSHOPINFO";
				result = parseResult(in);
				break;
			default:
		    break;
		}
        if (result != null) {
            Utils.D(requestMethod + "'s Response is : " + result.toString());
        } else {
            Utils.D(requestMethod + "'s Response is null");
        }
        return result;
    }

	
    private static HashMap<String, String> parseLoginOrRegisterResult(InputStream jsonStream) {
    	byte[] data = null;
		try {
			data = read(jsonStream);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		HashMap<String, String> result = new HashMap<String, String>();
		if(data!=null)
		{
			String JsonContext = new String(data);
			result.put("login", JsonContext);
		}else
		{
			result.put("login", "");
		}
        return result;
    }
    
    
    private static HashMap<String, String> parseGetDaymicResult(InputStream jsonStream) {
    	byte[] data = null;
		try {
			data = read(jsonStream);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		HashMap<String, String> result = new HashMap<String, String>(); 
		if(data!=null)
		{
			String JsonContext = new String(data);
			result.put("daymic", JsonContext);
		}else
		{
			result.put("daymic", "");			
		}
        return result;
    }
    
    
    private static HashMap<String, String> parseGongGaoResult(InputStream jsonStream) {
    	byte[] data = null;
		try {
			data = read(jsonStream);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		HashMap<String, String> result = new HashMap<String, String>();     
		String JsonContext = new String(data);
		result.put("gonggao", JsonContext);
        return result;
    }
    
    
    
    
    private static HashMap<String, String> parseInfoResult(InputStream jsonStream) {
    	byte[] data = null;
		try {
			data = read(jsonStream);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		HashMap<String, String> result = new HashMap<String, String>();
		if(data!=null&&data.length>0) {
			String JsonContext = new String(data);
			result.put("guidinfo", JsonContext);
		}else
		{
			result.put("guidinfo", "");
		}
        return result;
    }
    
    
    private static HashMap<String, String> parseItemNumsResult(InputStream jsonStream) {
    	byte[] data = null;
		try {
			data = read(jsonStream);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		HashMap<String, String> result = new HashMap<String, String>();     
		String JsonContext = new String(data);
		result.put("guidnums", JsonContext);
              
        return result;
    }
    
    private static HashMap<String, String> parseGetInfosResult(InputStream jsonStream) {
    	byte[] data = null;
		try {
			data = read(jsonStream);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
		HashMap<String, String> result = new HashMap<String, String>();     
		String JsonContext = new String(data);
		result.put("infos", JsonContext);
              
        return result;
    }
    
    
    
    private static HashMap<String, String> parseGetFriendsResult(InputStream jsonStream) {
    	byte[] data = null;
		try {
			data = read(jsonStream);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		HashMap<String, String> result = new HashMap<String, String>();     
		String JsonContext = new String(data);
		result.put("friends", JsonContext);
              
        return result;
    }


	private static HashMap<String, String> parseGetXiaoqusResult(InputStream jsonStream) {
		byte[] data = null;
		try {
			data = read(jsonStream);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		HashMap<String, String> result = new HashMap<String, String>();
		String JsonContext = new String(data);
		result.put("xiaoqus", JsonContext);

		return result;
	}

	private static HashMap<String, String> parseGetFwInfosResult(InputStream jsonStream) {
		byte[] data = null;
		try {
			data = read(jsonStream);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		HashMap<String, String> result = new HashMap<String, String>();
		String JsonContext = new String(data);
		result.put("infos", JsonContext);

		return result;
	}

	private static HashMap<String, String> parseSystemXmlResult(InputStream jsonStream) {
		byte[] data = null;
		try {
			data = read(jsonStream);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		HashMap<String, String> result = new HashMap<String, String>();
		String JsonContext = new String(data);
		result.put("xml", JsonContext);

		return result;
	}


	private static HashMap<String, String> parseGetUserInfoResult(InputStream jsonStream) {
		byte[] data = null;
		try {
			data = read(jsonStream);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		HashMap<String, String> result = new HashMap<String, String>();
		String JsonContext = new String(data);
		result.put("userinfo", JsonContext);

		return result;
	}



	private static HashMap<String, String> parseResult(InputStream jsonStream) {
		byte[] data = null;
		try {
			data = read(jsonStream);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		HashMap<String, String> result = new HashMap<String, String>();
		String JsonContext = new String(data);
		result.put("result", JsonContext);
		return result;
	}




}