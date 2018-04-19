package com.ono;

import java.util.List;

import org.junit.Test;

import com.ono.plugin.OnoAutomation;

public class TestOno {
	
	@Test
	public void test() {
		
		OnoAutomation.replyString="诚信为本，有赞必会！！！欢迎回访！！";
		List<String> newList = OnoAutomation.getNewList();
		for (String feedId : newList) {
			OnoAutomation.praise(feedId);
			OnoAutomation.reply(feedId);
		}
		for (String userId : OnoAutomation.userIds) {
			String fristFeed = OnoAutomation.getUserFristFeed(userId);
			OnoAutomation.praise(fristFeed);
			OnoAutomation.reply(fristFeed);
			List<String> replyList = OnoAutomation.replyList(fristFeed);
			for (String id : replyList) {
				String feed =	OnoAutomation.getUserFristFeed(id);
				OnoAutomation.praise(feed);
				OnoAutomation.reply(feed);
			}
		}
	}

}
