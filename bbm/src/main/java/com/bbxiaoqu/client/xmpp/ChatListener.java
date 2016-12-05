package com.bbxiaoqu.client.xmpp;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;

import android.content.Context;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.comm.bean.BbMessage;
import com.bbxiaoqu.comm.bean.ChatMessage;
import com.bbxiaoqu.client.baidu.BbPushMessageReceiver;
import com.bbxiaoqu.comm.service.db.ChatDB;
import com.bbxiaoqu.comm.service.db.FriendDB;
import com.bbxiaoqu.comm.service.db.NoticeDB;

public class ChatListener implements ChatManagerListener {
	private Context mContext;
	public ChatListener(Context context)
	{
		this.mContext = context;
	}


	  private static ChatListener instance;  
	  private ChatListener (){}   
	  public static ChatListener getInstance(Context context) {  
	  if (instance == null) {  
	           instance = new ChatListener(context);  
	       }  
	       return instance;  
	       }  
	
	@Override
	public void chatCreated(Chat chat, boolean create) {
		System.out.println(chat.toString());
		System.out.println(chat.toString());
		chat.addMessageListener(new ChatMessageListener() {
			@Override
			public void processMessage(Chat chat, org.jivesoftware.smack.packet.Message message) {

				String from=message.getFrom().toString().split("@")[0];
				String to=message.getTo().toString().split("@")[0];
				if(from.equals(DemoApplication.getInstance().getUserId()))
				{//不处理自己的消息
					return;
				}else if(from.equals("admin"))
				{//广播消息


				}else {
					ChatMessage mess=new ChatMessage();
					mess.setSenduserId(from);
					mess.setTouserId(to);
					mess.setGuid(message.getPacketID());
					mess.setMessage(message.getBody());
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					mess.setDate(new Date());

					mess.setDateStr(df.format(new Date()));
					NoticeDB noticedb=new NoticeDB(mContext);
					ChatDB chatdb=new ChatDB(mContext);
					//if(!chatdb.isexit(mess.getGuid()))
					//{

					chatdb.add(mess,false);
					//}
					//if(!noticedb.isexit(mess.getGuid()))
					//{
					//查询chat表,form用户未读数据
					if(noticedb.unreadnum(from,"私信")==0)
					{//不存在这人的私信
						noticedb.add(df.format(new Date()), "私信", from, from+"发送了一条私信","0");
					}else
					{//已经有一条通知了
						long num=chatdb.unreadnum(from,to);
						noticedb.updateusercontent(from, from+"发送了"+num+"条私信");
					}
					//}
					FriendDB fb=new FriendDB(mContext);
					//fb.removall();
					if(!fb.isexit(from))
					{//添加朋友联系人


						fb.add(from, "", "",from, "", message.getBody(), df.format(new Date()), 0);
						GetUserThread thread = new GetUserThread(mContext,from);
						thread.start();
					}else
					{//更新最后联系人

					}
					//直接查数据库中信息
					fb.newmess(from, from,message.getBody(), df.format(new Date()));
					for (int i = 0; i < BbPushMessageReceiver.chatmsgListeners.size(); i++) {
						BbPushMessageReceiver.chatmsgListeners.get (i).onChatMessage (new BbMessage ());
					}


				}
			}


			
		});
		
	}
	
	
}
