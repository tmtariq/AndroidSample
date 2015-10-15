package com.clecs.objects;

import java.io.Serializable;

public class User implements Serializable
	{
		private static final long serialVersionUID = 1L;

		String username;
		String namey;
		String avatar;
		String profileUrl;
		int followedBy;

		public String getUsername()
			{
				return username;
			}

		public void setUsername(String username)
			{
				this.username = username;
			}

		public String getNamey()
			{
				return namey;
			}

		public void setNamey(String namey)
			{
				this.namey = namey;
			}

		public String getAvatar()
			{
				return avatar;
			}

		public void setAvatar(String avatar)
			{
				this.avatar = avatar;
			}

		public String getProfileUrl()
			{
				return profileUrl;
			}

		public void setProfileUrl(String profileUrl)
			{
				this.profileUrl = profileUrl;
			}

		public int getFollowedBy()
			{
				return followedBy;
			}

		public void setFollowedBy(int followedBy)
			{
				this.followedBy = followedBy;
			}
	}
