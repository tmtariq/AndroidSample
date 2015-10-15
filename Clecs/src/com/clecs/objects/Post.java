package com.clecs.objects;

import java.io.Serializable;
import java.util.ArrayList;

public class Post implements Serializable
	{
		private static final long serialVersionUID = 1L;

		public static final int KEY_SMILE = 1;

		public static final int KEY_SAD = 2;

		public static final int KEY_NOTHING = 0;

		long postId;
		long transactAgainstPostId;
		long quotedPostId;
		boolean quote;
		boolean share;
		boolean myPost;
		String sharerUsername;
		String postText;
		String postTextOriginal;
		String dateCreated;
		String createdByUsername;
		String createdByName;
		String createdByAvatar; // ": "https://clecs.blob.core.windows.net/profileimages/3-.png",
		String profileUrl;// Home/ProfilePage/mmmmmm",
		int commentsQty;
		int smileQty;
		int sadQty;
		int smileSadNothing;
		boolean containsImage;
		String imageUrl;
		HashMention[] hashes;
		HashMention[] mentions;
		ArrayList<Link> inks;

		/**
		 * @return transactAgainstPostId instead of postId, if you want to get original Post ID call <code> getPostIdForcly()</code> instead
		 */
		public long getPostId()
			{
				//return transactAgainstPostId != -1 ? transactAgainstPostId : postId;
				return postId;
			}
		/*public long getPostIdForcly()
			{
				return postId;
			}*/

		public void setPostId(long postId)
			{
				this.postId = postId;
			}

		public boolean isMyPost()
			{
				return myPost;
			}

		public void setMyPost(boolean myPost)
			{
				this.myPost = myPost;
			}

		public String getPostText()
			{
				return postText;
			}

		public void setPostText(String postText)
			{
				this.postText = postText;
			}

		public String getPostTextOriginal()
			{
				return postTextOriginal==null ? "" : postTextOriginal;
			}

		public void setPostTextOriginal(String postTextOriginal)
			{
				this.postTextOriginal = postTextOriginal;
			}

		public long getTransactAgainstPostId()
			{
				return transactAgainstPostId;
			}

		public void setTransactAgainstPostId(long transactAgainstPostId)
			{
				this.transactAgainstPostId = transactAgainstPostId;
			}

		public long getQuotedPostId()
			{
				return quotedPostId;
			}

		public void setQuotedPostId(long quotedPostId)
			{
				this.quotedPostId = quotedPostId;
			}

		public boolean isQuote()
			{
				return quote;
			}

		public void setQuote(boolean quote)
			{
				this.quote = quote;
			}

		public boolean isShare()
			{
				return share;
			}

		public void setShare(boolean share)
			{
				this.share = share;
			}

		public String getSharerUsername()
			{
				return sharerUsername;
			}

		public void setSharerUsername(String sharerUsername)
			{
				this.sharerUsername = sharerUsername;
			}

		public String getDateCreated()
			{
				return dateCreated;
			}

		public void setDateCreated(String dateCreated)
			{
				this.dateCreated = dateCreated;
			}

		public String getCreatedByUsername()
			{
				return createdByUsername;
			}

		public String getCreatedByUsernameWithHat()
			{
				return "@" + createdByUsername;
			}

		public void setCreatedByUsername(String createdByUsername)
			{
				this.createdByUsername = createdByUsername;
			}

		public String getCreatedByName()
			{
				return createdByName;
			}

		public void setCreatedByName(String createdByName)
			{
				this.createdByName = createdByName;
			}

		public String getCreatedByAvatar()
			{
				return createdByAvatar;
			}

		public void setCreatedByAvatar(String createdByAvatar)
			{
				this.createdByAvatar = createdByAvatar;
			}

		public String getProfileUrl()
			{
				return profileUrl;
			}

		public void setProfileUrl(String profileUrl)
			{
				this.profileUrl = profileUrl;
			}

		public int getCommentsQty()
			{
				return commentsQty;
			}

		public void setCommentsQty(int commentsQty)
			{
				this.commentsQty = commentsQty;
			}

		public int getSmileQty()
			{
				return smileQty;
			}

		public void setSmileQty(int smileQty)
			{
				this.smileQty = smileQty;
			}

		public int getSadQty()
			{
				return sadQty;
			}

		public void setSadQty(int sadQty)
			{
				this.sadQty = sadQty;
			}

		public int getSmileSadNothing()
			{
				return smileSadNothing;
			}

		public void setSmileSadNothing(int smileSadNothing)
			{
				this.smileSadNothing = smileSadNothing;
			}

		public boolean isContainsImage()
			{
				return containsImage;
			}

		public void setContainsImage(boolean containsImage)
			{
				this.containsImage = containsImage;
			}

		public String getImageUrl()
			{
				return imageUrl;
			}

		public void setImageUrl(String imageUrl)
			{
				this.imageUrl = imageUrl;
			}

		public boolean isSmiledByMe()
			{
				return getSmileSadNothing() == KEY_SMILE;
			}

		public boolean isSadSmiledByMe()
			{
				return getSmileSadNothing() == KEY_SAD;
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

		public ArrayList<Link> getInks()
			{
				return inks;
			}

		public void setInks(ArrayList<Link> inks)
			{
				this.inks = inks;
			}

		public void setRevertSmiledByMe()
			{
				if (isSmiledByMe())
					{
						setSmileSadNothing(KEY_NOTHING);
						setSmileQty(getSmileQty() - 1);
					}
				else
					{
						if (isSadSmiledByMe())
							{
								setSadQty(getSadQty() - 1);
							}

						setSmileSadNothing(KEY_SMILE);
						setSmileQty(getSmileQty() + 1);

					}

			}

		public void setRevertSadSmileByMe()
			{
				if (isSadSmiledByMe())
					{
						setSmileSadNothing(KEY_NOTHING);
						setSadQty(getSadQty() - 1);
					}
				else
					{
						if (isSmiledByMe())
							{
								setSmileQty(getSmileQty() - 1);
							}

						setSmileSadNothing(KEY_SAD);
						setSadQty(getSadQty() + 1);

					}
			}

		// "hashes":[
		// {
		// "text":"#testing",
		// "index":0,
		// "length":8
		// }
		// ],
		// "mentions":[
		// {
		// "text":"@TheGirl",
		// "index":13,
		// "length":8
		// "links":[
		// {
		// "link":"http://goo.gl/pRMvl",
		// "index":0,
		// "length":19
		// }
		// ]
		// 0 - The logged in user has NOT smiled or sad
		// 1 - The logged in user has smiled
		// 2 - The logged in user has Sad

		// So smileSadNothing": 2 means that the logged in user has "Sad" the post so the sad face should be green.

	}
