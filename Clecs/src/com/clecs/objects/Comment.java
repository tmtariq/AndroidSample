package com.clecs.objects;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class Comment implements Serializable
	{
		private static final long serialVersionUID = 1L;
		// "id": 18,
		// "username": "mmmmmm",
		// "avatar": "https://clecs.blob.core.windows.net/profileimages/3-.png",
		// "profileUrl": "/Home/ProfilePage/mmmmmm",
		// "commentText": "Hi this is first comment",
		// "commentPostedDate": "3 Jan",
		// "myComment": false

		int id;
		@SerializedName("username")
		String userName;
		@SerializedName("name")
		String profileName;
		String avatar;
		String profileUrl;
		String commentText;
		String commentTextOriginal;
		String commentPostedDate;
		boolean myComment;
		HashMention[] hashes;
		HashMention[] mentions;

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

		public String getUserNameWithHat()
			{
				return "@" + userName;
			}

		public void setUserName(String userName)
			{
				this.userName = userName;
			}

		public String getProfileName()
			{
				if (profileName == null || profileName.equals("null"))
					return "";
				return profileName;
			}

		public void setProfileName(String profileName)
			{
				this.profileName = profileName;
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

		public String getCommentText()
			{
				return commentText;
			}

		public void setCommentText(String commentText)
			{
				this.commentText = commentText;
			}

		public String getCommentTextOriginal()
			{
				return commentTextOriginal;
			}

		public void setCommentTextOriginal(String commentTextOriginal)
			{
				this.commentTextOriginal = commentTextOriginal;
			}

		public String getCommentPostedDate()
			{
				return commentPostedDate;
			}

		public void setCommentPostedDate(String commentPostedDate)
			{
				this.commentPostedDate = commentPostedDate;
			}

		public boolean isMyComment()
			{
				return myComment;
			}

		public void setMyComment(boolean myComment)
			{
				this.myComment = myComment;
			}

		public HashMention[] getHashes()
			{
				if (hashes == null)
					hashes = new HashMention[0];
				return hashes;
			}

		public void setHashes(HashMention[] hashes)
			{
				this.hashes = hashes;
			}

		public HashMention[] getMentions()
			{
				if (mentions == null)
					mentions = new HashMention[0];
				return mentions;
			}

		public void setMentions(HashMention[] mentions)
			{
				this.mentions = mentions;
			}

	}
