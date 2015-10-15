package com.clecs.objects;

import java.io.Serializable;
import java.util.ArrayList;

import com.clecs.utils.Session;

public class AppNotification implements Serializable
	{

		private static final long serialVersionUID = 1L;
		public final static String TYPE_SMILEY = "Smiley";
		public final static String TYPE_COMMENT = "Comment";
		public final static String TYPE_MENTION = "Mention";
		public final static String TYPE_FOLLOWER = "Follower";

		int id;
		boolean read;
		boolean notRead;
		String profileImage;// "https://clecs.blob.core.windows.net/profileimages/6-.png",
		String date;
		String dateOrig;
		String text;
		String type;
		String userId;
		String destinationUrl;
		String postId;
		String commentId;

		public int getId()
			{
				return id;
			}

		public void setId(int id)
			{
				this.id = id;
			}

		public boolean isRead()
			{
				return read;
			}

		public void setRead(boolean read)
			{
				this.read = read;
			}

		public boolean isNotRead()
			{
				return notRead;
			}

		public void setNotRead(boolean notRead)
			{
				this.notRead = notRead;
			}

		public String getProfileImage()
			{
				return profileImage;
			}

		public void setProfileImage(String profileImage)
			{
				this.profileImage = profileImage;
			}

		public String getDate()
			{
				return date;
			}

		public void setDate(String date)
			{
				this.date = date;
			}

		public String getText()
			{
				return text;
			}

		public void setText(String text)
			{
				this.text = text;
			}

		public String getType()
			{
				return type;
			}

		public void setType(String type)
			{
				this.type = type;
			}

		public String getUserId()
			{
				return userId;
			}

		public void setUserId(String userId)
			{
				this.userId = userId;
			}

		public String getDestinationUrl()
			{
				return destinationUrl;
			}

		public void setDestinationUrl(String destinationUrl)
			{
				this.destinationUrl = destinationUrl;
			}

		public String getPostId()
			{
				return postId;
			}

		public void setPostId(String postId)
			{
				this.postId = postId;
			}

		public String getCommentId()
			{
				return commentId;
			}

		public void setCommentId(String commentId)
			{
				this.commentId = commentId;
			}

		/*static int notifCount;

		public static void setUreadNotif(int count)
			{
				notifCount = count;
			}

		public static void decreaseNotifCount()
			{
				notifCount -= 11;
			}

		public static int getUnreadNotif()
			{
				// ArrayList<AppNotification> notificationList = Session.listNotififs;
				// int count = 0;
				// for (int i = 0; i < notificationList.size(); i++)
				// count += notificationList.get(i).isNotRead() ? 1 : 0;
				// return count;
				return notifCount;
			}*/

		public String getDateOrig() {
			return dateOrig;
		}

		public void setDateOrig(String dateOrig) {
			this.dateOrig = dateOrig;
		}

		// "type":"Mention",
		// "userId":null,
		// "profileImage":"https://clecs.blob.core.windows.net/profileimages/zagwcMp05EebsuFyC-2GNg-.png",
		// "destinationUrl":"/Home/ReadNotification/30192",
		// "postId":"30149",
		// "commentId":null

	}
