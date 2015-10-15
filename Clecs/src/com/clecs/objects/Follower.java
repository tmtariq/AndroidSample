package com.clecs.objects;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class Follower implements Serializable
	{
		private static final long serialVersionUID = 1L;
		// "id": "20017",
		// "username": "staceyrheannon",
		// "imageUrl": "https://clecs.blob.core.windows.net/profileimages/LW2XSLuywEC--BsKKoa-4Q-.png"
		int id;
		@SerializedName("username")
		String userName;
		String imageUrl;
		private String name;

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

		public String getImageUrl()
			{
				return imageUrl;
			}

		public void setImageUrl(String imageUrl)
			{
				this.imageUrl = imageUrl;
			}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}
