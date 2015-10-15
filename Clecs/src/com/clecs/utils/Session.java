package com.clecs.utils;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import com.clecs.objects.AppNotification;
import com.clecs.objects.Follower;
import com.clecs.objects.MyProfileInfo;
import com.clecs.objects.Post;
import com.clecs.objects.Token;

public class Session
	{
		public static Token token;
		public static ArrayList<Post> listAllPost;
		public static ArrayList<Post> listSearchPost;
		public static ArrayList<Post> listAllPostsLocal;
		public static ArrayList<AppNotification> listNotififs = new ArrayList<AppNotification>();
		public static ArrayList<Follower> listSearchUser;
		// public static UserInfo userInfo;
		//public static long unreadNotifCount = 0;
		public static MyProfileInfo myProfileInfo = AppPref.getInstance().getMyProfileInfo();

		public static void clearSession()
			{
				listAllPost = null;
				listSearchPost = null;
				listAllPostsLocal = null;
				myProfileInfo = null;
				listSearchUser = null;
				listNotififs = new ArrayList<AppNotification>();
			}

		private final static AtomicInteger c = new AtomicInteger(0);

		public static int getID()
			{
				return c.incrementAndGet();
			}
	}
