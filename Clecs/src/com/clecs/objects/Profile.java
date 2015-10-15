package com.clecs.objects;

import java.io.Serializable;

public class Profile implements Serializable
	{
		private static final long serialVersionUID = 1L;
		// {"username":"Mike","profileImage":"https://clecs.blob.core.windows.net/profileimages/l9Z9IupBVEusweLLbrLQ1w-.png","profileName":"Micky","profileFollowStartDate":"23d",
		// "youAreFollowing":true,"thisIsMyProfile":false,"qtyFollowing":"2","qtyBeingFollowedBy":"4"}
		String username;
		String profileImage;
		String profileName;
		String profileEmail;
		int qtyBeingFollowedBy = 0;
		String profileFollowStartDate;
		boolean youAreFollowing;
		boolean thisIsMyProfile;
		int qtyFollowing = 0;

		public String getUsername()
			{
				return username;
			}

		public String getUsernameWithHat()
			{
				return "@" + username;
			}

		public void setUsername(String username)
			{
				this.username = username;
			}

		public String getProfileImage()
			{
				return profileImage;
			}

		public void setProfileImage(String profileImage)
			{
				this.profileImage = profileImage;
			}

		public String getProfileName()
			{
				return profileName;
			}

		public void setProfileName(String profileName)
			{
				this.profileName = profileName;
			}

		public String getProfileEmail()
			{
				return profileEmail;
			}

		public void setProfileEmail(String profileEmail)
			{
				this.profileEmail = profileEmail;
			}

		public int getQtyOfFollowers()
			{
				return qtyBeingFollowedBy;
			}

		public void setQtyOfFollowers(int qtyOfFollowers)
			{
				this.qtyBeingFollowedBy = qtyOfFollowers;
			}

		public int getQtyOfFollowing()
			{
				return qtyFollowing;
			}

		public void setQtyOfFollowing(int qtyOfFollowing)
			{
				this.qtyFollowing = qtyOfFollowing;
			}

		public String getProfileFollowStartDate()
			{
				return profileFollowStartDate;
			}

		public void setProfileFollowStartDate(String profileFollowStartDate)
			{
				this.profileFollowStartDate = profileFollowStartDate;
			}

		public boolean isYouAreFollowing()
			{
				return youAreFollowing;
			}

		public void setYouAreFollowing(boolean youAreFollowing)
			{
				this.youAreFollowing = youAreFollowing;
			}

		public boolean isThisIsMyProfile()
			{
				return thisIsMyProfile;
			}

		public void setThisIsMyProfile(boolean thisIsMyProfile)
			{
				this.thisIsMyProfile = thisIsMyProfile;
			}

	}
