package com.clecs.objects;

import com.clecs.utils.AppPref;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class MyProfileInfo
	{
		// "username": "sample string 1",
		// "profileImage": "sample string 2",
		// "profileName": "sample string 3",
		// "profileEmail": "sample string 4",
		// "qtyOfFollowers": "sample string 5",
		// "qtyOfFollowing": "sample string 6"
		@SerializedName("username")
		String userName;
		String profileImage;
		@SerializedName("profileEmail")
		String email;
		String profileName;
		@SerializedName("qtyOfFollowers")
		int followersCount;
		@SerializedName("qtyOfFollowing")
		int followingCount;

		public String getUserName()
			{
				return userName;
			}

		public String getUserNameWithHat()
			{
				return "@" + userName;
			}

		public void setUserName(String userName)
			{
				this.userName = userName;
				AppPref.getInstance().setMyProfileInfo(new Gson().toJson(this));
			}

		public String getProfileImage()
			{
				return profileImage;
			}

		public void setProfileImage(String profileImage)
			{
				this.profileImage = profileImage;
				AppPref.getInstance().setMyProfileInfo(new Gson().toJson(this));
			}

		public String getEmail()
			{
				return email;
			}

		public void setEmail(String email)
			{
				this.email = email;
			}

		public String getProfileName()
			{
				return profileName;
			}

		public void setProfileName(String profileName)
			{
				this.profileName = profileName;
				AppPref.getInstance().setMyProfileInfo(new Gson().toJson(this));
			}

		public int getFollowersCount()
			{
				return followersCount;
			}

		public void setFollowersCount(int followersCount)
			{
				this.followersCount = followersCount;
			}

		public int getFollowingCount()
			{
				return followingCount;
			}

		public void setFollowingCount(int followingCount)
			{
				this.followingCount = followingCount;
			}

	}
