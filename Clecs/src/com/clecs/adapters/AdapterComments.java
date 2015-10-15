package com.clecs.adapters;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.clecs.R;
import com.clecs.adapters.AdapterPost.ViewHolder;
import com.clecs.fragments.FragmentUser;
import com.clecs.network.RequestHandler;
import com.clecs.network.RequestHandler.ServerResponseListner;
import com.clecs.objects.Comment;
import com.clecs.objects.Post;
import com.clecs.utils.AppStatics;
import com.clecs.utils.AppUtils;
import com.clecs.utils.Session;
import com.clecs.widget.SpannableTextView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class AdapterComments extends BaseAdapter implements
		ServerResponseListner {
	FragmentActivity mActivity;
	ArrayList<Comment> mList;
	// ImageLoader imgLoader;
	final int REQ_CODE_DEL_COMMENT = 100;
	long idToBeDelete = -1;
	Post mPost;
	LinearLayout lvComments;

	public AdapterComments(FragmentActivity activity,
			ArrayList<Comment> listComment, Post post, LinearLayout llView) {
		mActivity = activity;
		mList = listComment;
		mPost = post;
		lvComments = llView;
		// imgLoader = new ImageLoader(mActivity);
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
	
	class ViewHolder {
		TextView tvUserName,tvPostTime, tvName;
		ImageView ivUser, ivMenu;
		SpannableTextView tvPostTxt;
		Comment mComment;
		
		public void setListners(final int position,
				final View convertView) {
		
		ivUser.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppUtils.replaceFragment(new FragmentUser(mComment.getUserName(), mComment.getAvatar(), mComment.isMyComment()), mActivity.getSupportFragmentManager(), R.id.amMainLayout, false);
			}
		});
		ivMenu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PopupMenu popup = new PopupMenu(mActivity, ivMenu);
				popup.getMenu().add("Dileu");
				//popup.getMenu().add("Delete");
				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						// System.out.print("viewIndex: " + viewIndex);
						// FragmentPostDetail.this.notifyCommentsAdapter();
						// AdapterComments.this.
						// .getParent().removeViewAt(viewIndex);
						// deleteComment(mComment, position, convertView,
						// parent);
						deleteComment(mComment);
						return true;
					}
				});

				popup.show();
			}
		});
	}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
		LayoutInflater inflater = (LayoutInflater) mActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(R.layout.row_comment, null, false);
		holder = new ViewHolder();
		holder.mComment = mList.get(position);
		AppUtils.setFont(mActivity, (ViewGroup) convertView);
		holder.ivUser = (ImageView) convertView.findViewById(R.id.rcIvUser);
		holder.tvUserName = (TextView) convertView.findViewById(R.id.rcTvUserName);
		holder.ivMenu = (ImageView) convertView.findViewById(R.id.rcIvMenu);
		holder.tvPostTxt = (SpannableTextView) convertView.findViewById(R.id.rcTvPostText);
		holder.tvPostTime = (TextView) convertView.findViewById(R.id.rcTvPostTime);
		holder.tvName = (TextView) convertView.findViewById(R.id.rcTvName);
		//ImageView imgPost = (ImageView) convertView.findViewById(R.id.rcIvPost);
		holder.setListners(position, convertView);
		convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		holder.tvUserName.setText(holder.mComment.getProfileName());
		holder.tvName.setText(holder.mComment.getUserNameWithHat());// + "==" +
														// getViewExactHeight(position));
		holder.tvPostTxt.setHashes(holder.mComment.getHashes());
		holder.tvPostTxt.setMentions(holder.mComment.getMentions());
		holder.tvPostTxt.setText(holder.mComment.getCommentTextOriginal());
		holder.tvPostTime.setText(holder.mComment.getCommentPostedDate());
		holder.ivMenu.setVisibility(holder.mComment.isMyComment() ? View.VISIBLE : View.GONE);
		ImageLoader.getInstance().displayImage(holder.mComment.getAvatar(), holder.ivUser);

		

		return convertView;
	}
	
	public void reloadChild(LinearLayout lvComments){
		this.lvComments = lvComments;
		lvComments.removeAllViews();
		for(int i=0;i<this.getCount();i++) {
		    View v = this.getView(i, null, null);
		    lvComments.addView(v, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		}
	}
	
	public void addComment(int index, Comment comment) {
		mList.add(index, comment);
		
		View v = getView(index, null, null);
		lvComments.addView(v, index, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
	}

	public void addComments(ArrayList<Comment> comments) {
		//int i = mList.size();
		mList.addAll(comments);
		for (Comment comment: comments){
			View v = getView(mList.indexOf(comment), null, null);
			lvComments.addView(v, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));
		}
		
		/*for (i = 0; i < comments.size(); i++) {
			View v = getView(i, null, null);
			lvComments.addView(v, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));
		}*/
	}

	// void deleteComment(final Comment comment, int position, final View
	// convertView, final ViewGroup parent)
	void deleteComment(final Comment comment) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		AlertDialog dialog = builder.create();
		
		//builder.setMessage("Are you sure you want to delete this comment?");
		//builder.setTitle("Clecs Confirmation");
		builder.setMessage("Are you sure you want to delete this comment?");
		builder.setTitle("Clecs Confirmation");
		
		// Yes
		builder.setPositiveButton("Iawn",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							RequestHandler handler = new RequestHandler(
									AdapterComments.this, true);
							JSONObject obj = new JSONObject();
							obj.put("id", comment.getId());
							idToBeDelete = comment.getId();
							handler.makePostRequest(
									AppStatics.URL_DELETE_COMMENT, obj,
									REQ_CODE_DEL_COMMENT);
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

	void updateCommentCountInList() {
		for (Post post : Session.listAllPost) {
			if (post.getPostId() == mPost.getPostId()) {
				post.setCommentsQty(mPost.getCommentsQty() - 1);
				break;
			}
		}
		/*
		 * for (int i = 0; i < Session.listAllPost.size(); i++) { if
		 * (Session.listAllPost.get(i).getPostId() == mPost.getPostId()) {
		 * Session.listAllPost.get(i).setCommentsQty(mList.size()); break; }
		 * 
		 * }
		 */
	}

	@Override
	public void onResponse(String response, long requestCode) {
		if (requestCode == REQ_CODE_DEL_COMMENT) {
			for (int i = 0; i < mList.size(); i++) {
				if (idToBeDelete == mList.get(i).getId()) {
					mList.remove(i);
					lvComments.removeViewAt(i);
				}
			}
			idToBeDelete = -1;
			updateCommentCountInList();
		}
		notifyDataSetChanged();
	}

	@Override
	public void onError(String error, String reason, long requestCode) {

	}

	@Override
	public void onNoInternet() {

	}

	/*
	 * public void setListViewHeight(final ListView listView) { if (listView ==
	 * null) return; int i = 0; int totalHeight = 0; // LayoutInflater inflater
	 * = (LayoutInflater)
	 * mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE); // View
	 * convertView = inflater.inflate(R.layout.row_comment, null, false); //
	 * final ViewTreeObserver observer =
	 * FragmentPostDetail.mTvDummyComment.getViewTreeObserver(); //
	 * observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() // { //
	 * // @SuppressLint("NewApi") // @Override // public void onGlobalLayout()
	 * // { // int viewHeight = FragmentPostDetail.mDummyContainer.getHeight();
	 * // // if (i < getCount()) // { // totalHeight += viewHeight; //
	 * System.out.println(i + " >> in Global : " + viewHeight +
	 * " :: tvHeight = " + FragmentPostDetail.mTvDummyComment.getHeight()); //
	 * FragmentPostDetail
	 * .mTvDummyComment.setText(mList.get(i).getCommentTextOriginal()); // //
	 * i++; // } // else if (i == getCount()) // { // ViewGroup.LayoutParams
	 * params = listView.getLayoutParams(); // params.height = totalHeight +
	 * (listView.getDividerHeight() * (getCount() - 1)); //
	 * listView.setLayoutParams(params); //
	 * observer.removeOnGlobalLayoutListener(this); // i = 0; // } // // } //
	 * }); for (i = 0; i < getCount(); i++) { //
	 * FragmentPostDetail.mTvDummyComment.setSingleLine(true);
	 * //FragmentPostDetail
	 * .mTvDummyComment.setText(mList.get(i).getCommentTextOriginal()); //
	 * FragmentPostDetail.mTvDummyComment.setSingleLine(false); //
	 * FragmentPostDetail
	 * .mTvDummyComment.setText(mList.get(i).getCommentTextOriginal());
	 * //FragmentPostDetail.mDummyContainer.invalidate();
	 * //FragmentPostDetail.mTvDummyComment.invalidate(); //int viewHieght =
	 * FragmentPostDetail.mDummyContainer.getHeight(); //int tvHieght =
	 * FragmentPostDetail.mTvDummyComment.getHeight(); //int lineCount =
	 * FragmentPostDetail.mTvDummyComment.getLineCount(); //int tvExactHieght =
	 * lineCount * tvHieght;// (lineCount < 2 ? 2 : lineCount) * tvHieght +
	 * (FragmentPostDetail.mTvDummyComment.getPaddingTop());
	 * //System.out.println(i + " >> in FOR : " + viewHieght + " :: tvHeight = "
	 * + tvHieght); int viewExactHeight = (viewHieght - tvHieght +
	 * tvExactHieght); viewExactHeight = viewExactHeight == 0 ? 150 :
	 * viewExactHeight; totalHeight += viewExactHeight;
	 * FragmentPostDetail.mTvDummyComment.setText(""); // totalHeight +=
	 * viewHieght; } ViewGroup.LayoutParams params = listView.getLayoutParams();
	 * params.height = totalHeight + (listView.getDividerHeight() * (getCount()
	 * - 1)); listView.setLayoutParams(params); }
	 */

	/*
	 * String getViewExactHeight(int pos) { //
	 * FragmentPostDetail.mTvDummyComment.setSingleLine(true);
	 * FragmentPostDetail
	 * .mTvDummyComment.setText(mList.get(pos).getCommentTextOriginal()); //
	 * FragmentPostDetail.mTvDummyComment.setSingleLine(false); //
	 * FragmentPostDetail
	 * .mTvDummyComment.setText(mList.get(i).getCommentTextOriginal());
	 * FragmentPostDetail.mDummyContainer.invalidate();
	 * FragmentPostDetail.mTvDummyComment.invalidate(); int viewHieght =
	 * FragmentPostDetail.mDummyContainer.getHeight(); int tvHieght =
	 * FragmentPostDetail.mTvDummyComment.getHeight(); int lineCount =
	 * FragmentPostDetail.mTvDummyComment.getLineCount(); int tvExactHieght =
	 * lineCount * tvHieght;// (lineCount < 2 ? 2 : lineCount) * tvHieght +
	 * (FragmentPostDetail.mTvDummyComment.getPaddingTop());
	 * System.out.println(pos + " >> in FOR : " + viewHieght + " :: tvHeight = "
	 * + tvHieght); int viewExactHeight = (viewHieght - tvHieght +
	 * tvExactHieght); FragmentPostDetail.mTvDummyComment.setText(""); return
	 * "vh:" + viewHieght + "th:" + tvHieght + "lc:" + lineCount + "te:" +
	 * tvExactHieght + "|" + viewExactHeight; // totalHeight += viewExactHeight;
	 * // totalHeight += viewHieght; }
	 */
}