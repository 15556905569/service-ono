package com.ono.plugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ono.util.common.StringUtil;
import com.ono.util.http.DefaultHttpHelper;
import com.ono.util.http.HttpHelper;

public class OnoAutomation {
	private static Logger LOGGER = LoggerFactory.getLogger(OnoAutomation.class); 
	
	public static String onoToken="9a07ec2a437411e899ff00163e0c817a";//登陆之后获得的token
	private static HttpHelper httpHelper = new DefaultHttpHelper();
	public static HashSet<String> userIds = new HashSet<>();
	public static String replyString = "";
	
	static {
		httpHelper.config.addHeader("os_type", "android");
		httpHelper.config.addHeader("User-Agent", "Dalvik/1.6.0 (Linux; U; Android 4.4.2; MI 4W Build/KTU84P)");
		httpHelper.config.addHeader("Host", "api.ono.chat");
		httpHelper.config.addHeader("Accept-Encoding", "gzip");
	}

	/**
	 * 获取新帖子（默认获取20条）
	 */
	public static List<String> getNewList() {
		List<String> ids = new ArrayList<>();
		String url = "https://api.ono.chat/api/v1/feed/query/new_list?token="+onoToken+"&count=20&last_id=";
		String result = httpHelper.goGet(url);
		if (StringUtil.isTimeout(result)) {
			LOGGER.error("获取新帖超时。。。。。。可能为token失效导致");
		}
		JSONObject newList = JSON.parseObject(result);
		JSONObject status = newList.getJSONObject("status");
		String message = status.getString("message");
		if ("ok".equals(message)) {
			LOGGER.info("获取新帖响应正确。。。。。。");
			JSONArray feedList = newList.getJSONObject("data").getJSONArray("feed_list");
			for (Object obj : feedList) {
				JSONObject feed = (JSONObject) obj;
				String id = feed.getString("id");
				String userId = feed.getString("user_id");
				userIds.add(userId);
				ids.add(id);
			}
		}
		return ids;
	}
	
	/**
	 * 回复帖子
	 */
	public static void reply(String feedId) {
		try {
			Thread.sleep(30*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String url = "https://api.ono.chat/api/v1/comment/create";
		String params = "id="+feedId+"&content="+replyString+"&token="+onoToken+"&";
		String result = httpHelper.goPost(url, params);
		if (StringUtil.isTimeout(result)) {
			LOGGER.error("回复帖子超时。。。。。。可能为token失效导致");
		}
		JSONObject praiseResult = JSON.parseObject(result);
		JSONObject status = praiseResult.getJSONObject("status");
		String message = status.getString("message");
		if ("ok".equals(message)) {
			LOGGER.info("回复帖子成功。。。。。。");
			return;
		}
		LOGGER.error("回复帖子失败。。。。。。失败信息："+result);
	}
	
	/**
	 * 点赞帖子
	 */
	public static void praise(String feedId) {
		try {
			Thread.sleep(10*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String url = "https://api.ono.chat/api/v1/feed/praise";
		String params = "id="+feedId+"&token="+onoToken+"&";
		String result = httpHelper.goPost(url, params);
		if (StringUtil.isTimeout(result)) {
			LOGGER.error("点赞帖子超时。。。。。。可能为token失效导致");
		}
		JSONObject praiseResult = JSON.parseObject(result);
		JSONObject status = praiseResult.getJSONObject("status");
		String message = status.getString("message");
		if ("ok".equals(message)) {
			LOGGER.info("点赞帖子成功。。。。。。");
			return;
		}
		LOGGER.error("点赞帖子失败。。。。。。失败信息："+result);
	}
	
	/**
	 * 获取指定人第一条帖子
	 */
	public static String getUserFristFeed(String userId) {
		try {
			Thread.sleep(10*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String url = "https://api.ono.chat/api/v1/feed/query/others_by_id?token="+onoToken+"&count=20&last_id=&user_id="+userId+"";
		String result = httpHelper.goGet(url);
		if (StringUtil.isTimeout(result)) {
			LOGGER.error("获取指定用户帖子超时。。。。。。可能为token失效导致");
		}
		JSONObject userDetail = JSON.parseObject(result);
		JSONObject status = userDetail.getJSONObject("status");
		String message = status.getString("message");
		if ("ok".equals(message)) {
			LOGGER.info("获取用户朋友圈信息正确。。。。。。");
			JSONArray feedList = userDetail.getJSONObject("data").getJSONArray("feed_list");
				JSONObject fristFeed = feedList.getJSONObject(0);
				String id = fristFeed.getString("id");
			return id;
		}
		LOGGER.error("获取用户信息失败。。。。。。失败信息："+result);
		return "";
	}
	
	/**
	 * 获取本人第一条帖子信息
	 */
	public static String myFristFeed() {
		try {
			Thread.sleep(10*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String url = "https://api.ono.chat/api/v1/feed/query/my?token="+onoToken+"&count=20&last_id=";
		String result = httpHelper.goGet(url);
		if (StringUtil.isTimeout(result)) {
			LOGGER.error("获取本人帖子集合超时。。。。。。可能为token失效导致");
		}
		JSONObject myDetail = JSON.parseObject(result);
		JSONObject status = myDetail.getJSONObject("status");
		String message = status.getString("message");
		if ("ok".equals(message)) {
			LOGGER.info("获取本人朋友圈信息正确。。。。。。");
			 JSONArray feedList = myDetail.getJSONObject("data").getJSONArray("feed_list");
				JSONObject fristFeed = feedList.getJSONObject(0);
				String id = fristFeed.getString("id");
			return id;
		}
		LOGGER.error("获取本人信息失败。。。。。。失败信息："+result);
		return "";
	}
	
	/**
	 * 获取帖子最新回复，默认20条(获取回复人id)
	 */
	public static List<String> replyList(String feedId){
		try {
			Thread.sleep(10*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		List<String> userids  = new ArrayList<>();
		String url = "https://api.ono.chat/api/v1/comment/query/list?token="+onoToken+"&feed_id="+feedId+"&type=3&count=20&last_comment_id=";
		String result = httpHelper.goGet(url);
		if (StringUtil.isTimeout(result)) {
			LOGGER.error("获取帖子最新回复超时。。。。。。可能为token失效导致");
		}
		JSONObject myDetail = JSON.parseObject(result);
		JSONObject status = myDetail.getJSONObject("status");
		String message = status.getString("message");
		if ("ok".equals(message)) {
			JSONArray feedList = myDetail.getJSONObject("data").getJSONArray("comment_list");
			for (Object object : feedList) {
				JSONObject comment = (JSONObject) object;
				String userId = comment.getString("user_id");
				userids.add(userId);
			}
		}
		return userids;
	}
	
	/**
	 * 发帖  不包括图片
	 */
	public static void createFeed(String content,String title) {
		try {
			Thread.sleep(10*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String url = "https://api.ono.chat/api/v1/feed/publish";
		String params = "content="+content+"&at_user_names=&title="+title+"&address=&video_url=&token="+onoToken+"&range_type=1&image_urls=&longitude=&latitude=&";
		String result = httpHelper.goPost(url, params);
		if (StringUtil.isTimeout(result)) {
			LOGGER.error("发布帖子超时。。。。。。可能为token失效导致");
		}
		JSONObject praiseResult = JSON.parseObject(result);
		JSONObject status = praiseResult.getJSONObject("status");
		String message = status.getString("message");
		if ("ok".equals(message)) {
			LOGGER.info("发布帖子成功。。。。。。");
			return;
		}
		LOGGER.error("发布帖子失败。。。。。。失败信息："+result);
	}
}
