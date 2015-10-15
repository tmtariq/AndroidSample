package com.clecs.objects;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class UserSmileSad implements Serializable
	{
		private static final long serialVersionUID = 1L;
		// "id": "sample string 1",
		// "smileOrSad": true,
		// "username": "sample string 3",
		// "avatar": "sample string 4",
		// "date": "sample string 5"
		String id;
		boolean smileOrSad;
		@SerializedName("username")
		String userName;
		String avatar;
		String date;

		public String getId()
			{
				return id;
			}

		public void setId(String id)
			{
				this.id = id;
			}

		public boolean isSmileOrSad()
			{
				return smileOrSad;
			}

		public void setSmileOrSad(boolean smileOrSad)
			{
				this.smileOrSad = smileOrSad;
			}

		public String getUserName()
			{
				return userName;
			}

		public void setUserName(String userName)
			{
				this.userName = userName;
			}

		public String getAvatar()
			{
				return avatar;
			}

		public void setAvatar(String avatar)
			{
				this.avatar = avatar;
			}

		public String getDate()
			{
				return date;
			}

		public void setDate(String date)
			{
				this.date = date;
			}

	}
