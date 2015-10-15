package com.clecs.gcm;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class PushNotification implements Serializable
	{
		private static final long serialVersionUID = 1L;
		@SerializedName("username")
		String userName;
		String postId, from, alert, badge, sound, notificationId;
		int id;

		public int getId()
			{
				return id;
			}

		public void setId(int id)
			{
				this.id = id;
			}

		public String getUserName()
			{
				return userName;
			}

		public void setUserName(String userName)
			{
				this.userName = userName;
			}

		public String getPostId()
			{
				return postId;
			}

		public void setPostId(String postId)
			{
				this.postId = postId;
			}

		public String getFrom()
			{
				return from;
			}

		public void setFrom(String from)
			{
				this.from = from;
			}

		public String getAlert()
			{
				return alert;
			}

		public void setAlert(String alert)
			{
				this.alert = alert;
			}

		public String getBadge()
			{
				return badge;
			}

		public void setBadge(String badge)
			{
				this.badge = badge;
			}

		public String getSound()
			{
				return sound;
			}

		public void setSound(String sound)
			{
				this.sound = sound;
			}

		public String getNotificationId()
			{
				return notificationId;
			}

		public void setNotificationId(String notificationId)
			{
				this.notificationId = notificationId;
			}

		public boolean hasUserName()
			{
				if (userName == null)
					return false;
				return userName.trim().length() > 0;
			}

		public boolean hasPostId()
			{
				if (postId == null)
					return false;
				return postId.trim().length() > 0;
			}

	}