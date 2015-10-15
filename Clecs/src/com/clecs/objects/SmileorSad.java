package com.clecs.objects;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class SmileorSad implements Serializable
	{

		// "id": "sample string 1",
		// "smileOrSad": true,
		// "username": "sample string 3",
		// "avatar": "sample string 4",
		// "date": "sample string 5"
		//[{"id":"40467","smileOrSad":false,"username":"TheGirl","avatar":"https://clecs.blob.core.windows.net/profileimages/-L6NIkiRikSQObzpz-bI1Q-.png","date":"10h"}
		String id;
		@SerializedName("username")
		String userName;
		boolean smileOrSad;
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

		public String getUserName()
			{
				return userName;
			}

		public void setUserName(String userName)
			{
				this.userName = userName;
			}

		public boolean isSmileOrSad()
			{
				return smileOrSad;
			}

		public void setSmileOrSad(boolean smileOrSad)
			{
				this.smileOrSad = smileOrSad;
			}

		public String getAvtar()
			{
				return avatar;
			}

		public void setAvtar(String avtar)
			{
				this.avatar = avtar;
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
