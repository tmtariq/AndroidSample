package com.clecs.adapters;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import com.clecs.R;
import com.clecs.dialog.ImageDialog;
import com.clecs.fragments.FragmentNewPost;
import com.clecs.fragments.FragmentPostDetail;
import com.clecs.fragments.FragmentPostsMain;
import com.clecs.fragments.FragmentUser;
import com.clecs.network.RequestHandler;
import com.clecs.network.RequestHandler.ServerResponseListner;
import com.clecs.objects.Post;
import com.clecs.utils.AppPref;
import com.clecs.utils.AppStatics;
import com.clecs.utils.AppUtils;
import com.clecs.utils.Session;
import com.clecs.widget.SpannableTextView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class AdapterPost extends BaseAdapter implements ServerResponseListner,
		OnClickListener {
	// final int REQ_SMILE = 100;
	// final int REQ_SAD_SMILE = 100;
	ArrayList<Post> mList;
	FragmentActivity mActivity;

	String searchText;
	boolean isUserProfile;
	RequestHandler handler;
	final int REQ_CODE_SHARE = 200;
	final int REQ_CODE_DELETE = 300;

	long idToBeDelete = -1;

	public AdapterPost(FragmentActivity activity, ArrayList<Post> voucherList,
			boolean isUserProfile) {
		mList = voucherList;
		mActivity = activity;
		handler = new RequestHandler(this, true);

		this.isUserProfile = isUserProfile;
	}

	public AdapterPost(FragmentActivity activity, ArrayList<Post> postList) {
		mList = postList;
		mActivity = activity;
		handler = new RequestHandler(this, true);
	}

	public AdapterPost(FragmentActivity activity, ArrayList<Post> voucherList,
			String searchText) {
		mList = voucherList;
		mActivity = activity;
		handler = new RequestHandler(this, true);

		this.searchText = searchText;
	}

	@Override
	public int getCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

//	public DisplayImageOptions displayImageOptions() {
//		DisplayImageOptions options = new DisplayImageOptions.Builder()
//				.showImageOnLoading(R.drawable.dummy)
//				.showImageForEmptyUri(R.drawable.dummy)
//				.showImageOnFail(R.drawable.dummy).cacheInMemory(true)
//				.cacheOnDisk(true).considerExifParams(true)
//				.displayer(new RoundedBitmapDisplayer(20)).build();
//
//		return options;
//	}

	@SuppressLint({ "ViewHolder", "InflateParams" })
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		updatePostInLists(mList.get(position).getPostId());
		final Post post = mList.get(position);
		String padding = " ";
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mActivity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.row_posts, null);
			AppUtils.setFont(mActivity, (ViewGroup) convertView);
			holder = new ViewHolder();
			holder.tvSharedBy = (TextView) convertView
					.findViewById(R.id.rpTvSharedby);
			holder.tvUserName = (TextView) convertView
					.findViewById(R.id.rpTvUserName);
			holder.tvHappy = (TextView) convertView
					.findViewById(R.id.rpTvHappySmile);
			holder.tvSad = (TextView) convertView
					.findViewById(R.id.rpTvSadSmile);
			holder.tvComment = (TextView) convertView
					.findViewById(R.id.rpTvCommentsCount);
			holder.tvPostTxt = (SpannableTextView) convertView
					.findViewById(R.id.rpTvPostText);
			holder.tvPostTime = (TextView) convertView
					.findViewById(R.id.rpTvPostTime);
			holder.tvName = (TextView) convertView.findViewById(R.id.rpTvName);
			holder.imgUser = (ImageView) convertView
					.findViewById(R.id.rpIvUser);
			holder.imgPost = (ImageView) convertView
					.findViewById(R.id.rpIvPost);
			holder.tvPostTxtSearch = (TextView) convertView
					.findViewById(R.id.rpTvPostTextSearch);
			holder.imgMenu = (ImageView) convertView
					.findViewById(R.id.rpIvMenu);

			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		if (post.isShare() && post.getSharerUsername() != null) {
			holder.tvSharedBy.setVisibility(View.VISIBLE);
			//holder.tvSharedBy.setText("Shared by @" + post.getSharerUsername());
			holder.tvSharedBy.setText("Rhannu gan @" + post.getSharerUsername());
		} else {
			holder.tvSharedBy.setVisibility(View.GONE);
		}
		holder.setListners(post, position, convertView);
		ImageLoader.getInstance().displayImage(post.getCreatedByAvatar(),
				holder.imgUser);
		if (post.isContainsImage()) {
			holder.imgPost.setVisibility(View.VISIBLE);

			ImageLoader.getInstance().displayImage(post.getImageUrl(),
					holder.imgPost);
		} else
			holder.imgPost.setVisibility(View.GONE);

		holder.tvPostTxt.setHashes(post.getHashes());
		holder.tvPostTxt.setMentions(post.getMentions());
		holder.tvUserName.setText(post.getCreatedByUsernameWithHat());
		holder.tvName.setText(post.getCreatedByName());
		// tvPostTxt.setHashes(post.get)
		
		//holder.tvComment.setText(padding + post.getCommentsQty() + (post.getCommentsQty() > 0 ? " Comments" : " Comment"));
		holder.tvComment.setText(padding + post.getCommentsQty() + (post.getCommentsQty() > 0 ? " Sylwadau" : " Sylwad"));
		
		// if (searchText != null)
		// {
		// tvPostTxtSearch.setVisibility(View.VISIBLE);
		// tvPostTxt.setVisibility(View.GONE);
		// tvPostTxtSearch.setText(AppUtils.highlight(searchText,
		// post.getPostTextOriginal()));
		// }
		// else
		if (searchText != null)
			holder.tvPostTxt.setSearchedText(searchText);
		holder.tvPostTxt.setText(post.getPostTextOriginal());

		holder.tvPostTime.setText(post.getDateCreated());

		holder.tvHappy.setText(padding + post.getSmileQty() + "");
		holder.tvSad.setText(padding + post.getSadQty() + "");
		holder.tvHappy.setTextColor(post.isSmiledByMe() ? Color.GREEN
				: Color.RED);
		holder.tvSad.setTextColor(post.isSadSmiledByMe() ? Color.GREEN
				: Color.RED);
		holder.tvHappy.setCompoundDrawablesWithIntrinsicBounds(
				post.isSmiledByMe() ? R.drawable.smile_green
						: R.drawable.smile_red, 0, 0, 0);
		holder.tvSad.setCompoundDrawablesWithIntrinsicBounds(post
				.isSadSmiledByMe() ? R.drawable.sad_green : R.drawable.sad_red,
				0, 0, 0);

		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentPostDetail fPostDetail = new FragmentPostDetail(post);
				AppUtils.replaceFragment(fPostDetail,
						mActivity.getSupportFragmentManager(),
						R.id.amMainLayout, false);
			}
		});

		// tvPostTxt.setText(post.getPostTextOriginal());

		return convertView;
	}

	@Override
	public void onClick(View v) {

	}

	class ViewHolder {
		TextView tvUserName, tvHappy, tvSad, tvComment, tvPostTime, tvName,
				tvPostTxtSearch, tvSharedBy;
		SpannableTextView tvPostTxt;
		ImageView imgUser, imgPost, imgMenu;

		public void setListners(final Post post, final int position,
				final View convertView) {
			/*// Already exits please see 2nd next
			 * tvSad.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (tvSad.getCurrentTextColor() != Color.GRAY) {
						tvSad.setCompoundDrawablesWithIntrinsicBounds(
								R.drawable.sad_gray, 0, 0, 0);
						tvSad.setTextColor(Color.GRAY);
						updateSadSmile(tvSad, post.getTransactAgainstPostId(), position);
					}
				}
			});*/

			tvHappy.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (tvHappy.getCurrentTextColor() != Color.GRAY) {
						tvHappy.setCompoundDrawablesWithIntrinsicBounds(
								R.drawable.smile_gary, 0, 0, 0);
						tvHappy.setTextColor(Color.GRAY);
						updateSmile(tvHappy, post.getTransactAgainstPostId(), position);
					}
				}
			});
			tvSad.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (tvSad.getCurrentTextColor() != Color.GRAY) {
						tvSad.setCompoundDrawablesWithIntrinsicBounds(
								R.drawable.sad_gray, 0, 0, 0);
						tvSad.setTextColor(Color.GRAY);
						updateSadSmile(tvSad, post.getTransactAgainstPostId(), position);
					}
				}
			});
			((View) tvPostTxt.getParent())
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							convertView.performClick();
						}
					});
			tvPostTxtSearch.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					convertView.performClick();
				}
			});
			imgUser.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (isUserProfile & !post.isShare()) {
						new ImageDialog(mActivity, post.getCreatedByAvatar()).show();
					} else {
						FragmentUser fragmentUser = new FragmentUser(post
								.getCreatedByUsername(), post
								.getCreatedByAvatar(), false);
						AppUtils.replaceFragment(fragmentUser,
								mActivity.getSupportFragmentManager(),
								R.id.amMainLayout, false);
					}
				}
			});
			imgPost.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					new ImageDialog(mActivity, post.getImageUrl()).show();
					// ImageDialog fragmentImg = new ImageDialog();
					// Bundle b = new Bundle();
					// b.putString(ImageDialog.EXTRA_IMG_URL,
					// post.getImageUrl());
					// fragmentImg.setArguments(b);
					// AppUtils.replaceFragment(fragmentImg,
					// mActivity.getSupportFragmentManager(),
					// R.id.amMainLayout);

				}
			});
			// Menu
			// mActivity.registerForContextMenu(imgMenu);
			imgMenu.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// new ImageDialog(mActivity, post.getImageUrl()).show();
					// mActivity.registerForContextMenu(v);
					// mActivity.openContextMenu(v);
					// mActivity.unregisterForContextMenu(v);

					PopupMenu popup = new PopupMenu(mActivity, imgMenu);
					// Inflating the Popup using xml file
					popup.getMenuInflater().inflate(R.menu.share_menu,
							popup.getMenu());

					if (post.isMyPost())
						popup.getMenu().removeItem(R.id.cnt_mnu_share);
					else
						popup.getMenu().removeItem(R.id.cnt_mnu_delete);
					// registering popup with OnMenuItemClickListener
					popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
						@Override
						public boolean onMenuItemClick(MenuItem item) {
							if (item.getTitle().equals(
									mActivity.getResources().getString(
											R.string.share)))
								sharePost(post);
							else if (item.getTitle().equals(
									mActivity.getResources().getString(
											R.string.quote)))
								quotePost(post);
							else if (item.getTitle().equals(
									mActivity.getResources().getString(
											R.string.delete)))
								deletePost(post);

							return true;
						}
					});

					popup.show(); // showing popup menu
					// ImageDialog fragmentImg = new ImageDialog();
					// Bundle b = new Bundle();
					// b.putString(ImageDialog.EXTRA_IMG_URL,
					// post.getImageUrl());
					// fragmentImg.setArguments(b);
					// AppUtils.replaceFragment(fragmentImg,
					// mActivity.getSupportFragmentManager(),
					// R.id.amMainLayout);

				}
			});
		}

		void quotePost(Post post) {
			AppUtils.replaceFragment(new FragmentNewPost(true, post),
					mActivity.getSupportFragmentManager(), R.id.amMainLayout);
		}

		void sharePost(Post post) {
			try {
				handler = new RequestHandler(AdapterPost.this, true);
				JSONObject obj = new JSONObject();
				obj.put("postId", post.getPostId());
				handler.makePostRequest(AppStatics.URL_SHARE_POST, obj,
						REQ_CODE_SHARE);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		void deletePost(final Post post) {
			AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
			AlertDialog dialog = builder.create();
			
			
			//builder.setMessage("Are you sure you want to delete this post?");
			//builder.setTitle("Clecs Confirmation");
			builder.setMessage("A ydych yn sicr eich bod am ddileu'r swydd hon?");
			builder.setTitle("Clecs");
			
			// Yes
			builder.setPositiveButton("Iawn",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							try {
								handler = new RequestHandler(AdapterPost.this,
										true);
								JSONObject obj = new JSONObject();
								obj.put("id", post.getPostId());
								idToBeDelete = post.getPostId();
								handler.makePostRequest(
										AppStatics.URL_DELETE_POST, obj,
										REQ_CODE_DELETE);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
			// Cancel
			builder.setNegativeButton("Canslo",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			builder.setCancelable(true);
			dialog = builder.create();
			dialog.show();
		}
	}

	@Override
	public void onResponse(String response, long requestCode) {
		if (requestCode == REQ_CODE_SHARE) {
			AppUtils.showToast("Post shared");
			FragmentPostsMain.instance.refreshPosts();
			return;
		} else if (requestCode == REQ_CODE_DELETE) {
			/*
			 * for (int i = 0; i < Session.listAllPost.size(); i++) { if
			 * (idToBeDelete == Session.listAllPost.get(i).getPostIdForcly()) {
			 * Session.listAllPost.remove(i); } }
			 */
			for (Post post : Session.listAllPost) {
				if (idToBeDelete == post.getPostId()) {
					Session.listAllPost.remove(post);
					break;
				}
			}

			/*
			 * for (int i = 0; i < mList.size(); i++) { if (idToBeDelete ==
			 * mList.get(i).getPostIdForcly()) { mList.remove(i); } }
			 */
			for (Post post : mList) {
				if (idToBeDelete == post.getPostId()) {
					mList.remove(post);
					break;
				}
			}
		} else {
			//int index = -1;
			/*for (int i = 0; i < mList.size(); i++) {
				if (mList.get(i).getPostId() == requestCode) {
					//index = i;
					Post post = AppPref.getPostCHangedAndClear(requestCode);
					if (post != null) mList.set(i, post);
					break;
				}
			}*/
			/*if (index != -1) {
				Post post = AppPref.getPostCHangedAndClear(requestCode);
				if (post != null)
					mList.set(index, post);
			}*/
			// if (Session.listAllPost != null)
			// mList = (ArrayList<Post>) Session.listAllPost.clone();
		}
		notifyDataSetChanged();
	}
	
	public void updatePostInLists(long postId){
		Post changedPost = AppPref.getInstance().getPostCHangedAndClear(postId);
		if (changedPost != null) {
			for (int i = 0; i < mList.size(); i++) {
				if (mList.get(i).getPostId() == postId) {
					 mList.set(i, changedPost);
					break;
				}
			}
			
			for (int i = 0; i < Session.listAllPost.size(); i++) {
				if (Session.listAllPost.get(i).getPostId() == postId) {
					Session.listAllPost.set(i, changedPost);
					break;
				}
			}
		}
	}

	@Override
	public void onError(String error, String reason, long requestCode) {
		if (Session.listAllPostsLocal != null)
			if (Session.listAllPostsLocal.size() > 0)
				mList = (ArrayList<Post>) Session.listAllPostsLocal.clone();
		notifyDataSetChanged();
	}

	@Override
	public void onNoInternet() {
		//AppUtils.showToast("You're offline");
		AppUtils.showToast("Ni allai cysylltu â'r gweinydd");
		if (Session.listAllPostsLocal != null)
			if (Session.listAllPostsLocal.size() > 0)
				mList = (ArrayList<Post>) Session.listAllPostsLocal.clone();
		notifyDataSetChanged();
	}

	@SuppressWarnings("unchecked")
	void updateSmile(TextView tv, long postId, int position) {
		try {
			handler = new RequestHandler(this, true);
			JSONObject obj = new JSONObject();
			obj.put("postId", postId);
			Post post = mList.get(position);
			handler.makePostRequest(AppStatics.URL_POST_SMILE, obj, postId);

			post.setRevertSmiledByMe();
			AppPref.getInstance().setPostChanged(post);

			// maintaining the data locally
			//Session.listAllPostsLocal = (ArrayList<Post>) mList.clone();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// mList.get(position).setRevertSmiledByMe();

	}

	@SuppressWarnings("unchecked")
	void updateSadSmile(TextView tv, long postId, int position) {
		try {
			handler = new RequestHandler(this, true);
			JSONObject obj = new JSONObject();
			obj.put("postId", postId);
			handler.makePostRequest(AppStatics.URL_POST_SAD_SMILE, obj, postId);

			Post post = mList.get(position);
			post.setRevertSadSmileByMe();
			AppPref.getInstance().setPostChanged(post);

			// maintaining the data locally
			//Session.listAllPostsLocal = (ArrayList<Post>) mList.clone();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

}
