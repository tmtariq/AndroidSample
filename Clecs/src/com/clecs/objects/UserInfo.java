package com.clecs.objects;

import java.io.Serializable;

public class UserInfo implements Serializable
	{
		private static final long serialVersionUID = 1L;

		String userName;
		boolean hasRegistered;
		String loginProvider;
		String userImage;
		

		public String getUsername()
			{
				return userName;
			}

//		public void setUsername(String username)
//			{
//				this.userName = username;
//			}

		public boolean isHasRegistered()
			{
				return hasRegistered;
			}

//		public void setHasRegistered(boolean hasRegistered)
//			{
//				this.hasRegistered = hasRegistered;
//			}

		public String getLoginProvider()
			{
				return loginProvider;
			}

//		public void setLoginProvider(String loginProvider)
//			{
//				this.loginProvider = loginProvider;
//			}

		public String getUserImage()
			{
				return userImage;
			}

//		public void setUserImage(String userImage)
//			{
//				this.userImage = userImage;
//			}

	}
