package com.clecs.fragments;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.clecs.R;
import com.clecs.adapters.AdapterComments;
import com.clecs.dialog.ImageDialog;
import com.clecs.network.RequestHandler;
import com.clecs.network.RequestHandler.ServerResponseListner;
import com.clecs.objects.Comment;
import com.clecs.objects.Post;
import com.clecs.objects.SmileorSad;
import com.clecs.utils.AppPref;
import com.clecs.utils.AppStatics;
import com.clecs.utils.AppUtils;
import com.clecs.utils.Session;
import com.clecs.widget.MyAutoCompleteTextView;
import com.clecs.widget.SpannableTextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

public class FragmentPostDetail extends Fragment implements
		ServerResponseListner {
	final int REQ_POST_DETAIL = 100;
	final int REQ_COMMENTS = 200;
	final int REQ_POST_COMMENT = 300;
	final int REQ_LATEST_COMMENT = 400;
	final int REQ_SAD_SMILE = 500;
	final int REQ_CODE_DELETE = 600;
	final int REQ_CODE_QUOTE = 700;
	final int REQ_CODE_SHARE = 800;
	ProgressDialog pd;
	long lastIdComment = 0;
	long lastIdSadSmile = 0;
	View root;
	ImageView ivUser, ivPost, ivMenu;
	TextView tvUserName, tvProfileName, tvHappy, tvSad, tvTime, tvViewSads,
			tvViewSmiles;
	SpannableTextView tvPostText;
	TextView tvSharedBy;
	MyAutoCompleteTextView etReply;
	LinearLayout lvComments;
	Button btnLoadPost, btnPostComment;
	Post mPost;
	//View viewSmiles;
	RequestHandler handler;
	ArrayList<Comment> commentList;
	ArrayList<SmileorSad> smileOrSadList;
	AdapterComments adapterComments;
	// long mPostId = 0;
	// TO CALCULATE THE EXACT HEIGHT OF LISTVIEW
	// public static View mDummyContainer;
	// public static SpannableTextView mTvDummyComment;
	// ScrollView scrollView;
	boolean isDataLoaded = false;

	public FragmentPostDetail(Post post) {
		handler = new RequestHandler(this);
		mPost = post;
		// getPostDetail();
		// getComments(mPost.getPostId());
	}

	public FragmentPostDetail(long postId) {
		handler = new RequestHandler(this);
		// mPostId = postId;
		mPost = new Post();
		mPost.setPostId(postId);
		// getPostDetail();
		// getComments(postId);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (root == null) {
			root = inflater.inflate(R.layout.fragment_post_detail, container,
					false);
			initViews();
			AppUtils.setFont(getActivity(), (ViewGroup) root);
		}
		return root;
	}

	void initViews() {
		// mDummyContainer = root.findViewById(R.id.fpdCmntContainerDummy);
		// mTvDummyComment = (SpannableTextView)
		// root.findViewById(R.id.fpdCommentDummy);
		// scrollView = (ScrollView) root.findViewById(R.id.fpdSv);

		pd = new ProgressDialog(getActivity());
		pd.setCancelable(false);
		pd.setMessage("Llwytho...");

		//viewSmiles = root.findViewById(R.id.fpdllSmiles);
		ivUser = (ImageView) root.findViewById(R.id.fpdIvUser);
		ivPost = (ImageView) root.findViewById(R.id.fdpIvPost);
		ivMenu = (ImageView) root.findViewById(R.id.fpdIvMenu);
		tvUserName = (TextView) root.findViewById(R.id.fpdTvUserName);
		tvProfileName = (TextView) root.findViewById(R.id.fpdTvName);
		tvPostText = (SpannableTextView) root.findViewById(R.id.fpdTvPostText);
		tvSharedBy = (TextView) root.findViewById(R.id.fpdTvSharedby);
		tvHappy = (TextView) root.findViewById(R.id.fpdTvHappySmile);
		tvSad = (TextView) root.findViewById(R.id.fpdTvSadSmile);
		tvViewSads = (TextView) root.findViewById(R.id.fpdTvViewSads);
		tvViewSmiles = (TextView) root.findViewById(R.id.fpdTvViewSmile);

		tvTime = (TextView) root.findViewById(R.id.fpdTvPostTime);
		etReply = (MyAutoCompleteTextView) root.findViewById(R.id.fpdEtReply);
		lvComments = (LinearLayout) root.findViewById(R.id.fpdLvComments);
		btnLoadPost = (Button) root.findViewById(R.id.fpdBtnLoadMorePost);
		btnPostComment = (Button) root.findViewById(R.id.fdpBtnPost);

		// lvComments.setAdapter(adapterComments);

		if (commentList == null)
			commentList = new ArrayList<Comment>();
		if (adapterComments == null) {
			adapterComments = new AdapterComments(getActivity(), commentList,
					mPost, lvComments);
		}
		if (smileOrSadList == null)
			smileOrSadList = new ArrayList<SmileorSad>();

		// adapterComments = new AdapterComments(getActivity(), commentList);
		// synchronized (adapterComments)
		// {
		// notifyCommentsAdapter();
		// }

		/*
		 * if (mPost == null) { //populateData(mPost, false); //} else { mPost =
		 * new Post(); mPost.setPostId(mPostId);
		 * 
		 * // populateData(post, false); // getComments(mPost.getPostId()); //
		 * getPostDetail(mPost.getPostId()); //
		 * pd.setMessage("Getting post data..."); // pd.show(); }
		 */

		if (!isDataLoaded) {
			// getComments();
			getPostDetail();
			// pd.setMessage("Getting post data...");
			// mPostId = mPost.getPostId();
		} else {
			populateData(mPost, false);

			// adapterComments.addComments(commentList);
			adapterComments.reloadChild(lvComments);
			notifyCommentsAdapter();
		}

		ivUser.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentUser fragmentUser = new FragmentUser(mPost
						.getCreatedByUsername(), mPost.getCreatedByAvatar(),
						mPost.isMyPost());
				AppUtils.replaceFragment(fragmentUser, getActivity()
						.getSupportFragmentManager(), R.id.amMainLayout, false);
			}
		});
		ivPost.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ImageDialog id = new ImageDialog(getActivity(), mPost
						.getImageUrl());
				id.show();
			}
		});

		btnPostComment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// "commentText": "sample string 1",
				// "postId": "sample string 2"
				if (etReply.getText().toString().trim().length() > 0)
					try {
						JSONObject obj = new JSONObject();
						obj.put("commentText", etReply.getText().toString());
						obj.put("postId", mPost.getTransactAgainstPostId());
						//pd.setMessage("Posting...");
						// we just are saying loading...
						pd.setMessage("Llwytho...");
						
						pd.show();
						handler.makePostRequest(AppStatics.URL_POST_COMMENT,
								obj, REQ_POST_COMMENT);

					} catch (JSONException e) {
						e.printStackTrace();
					}
				else
				{
					//etReply.setError("Write something to add comment");
					//Type your comment first 
					etReply.setError("Teipiwch eich sylw yn gyntaf");
				}
					
			}
		});
		etReply.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEND) {
					btnPostComment.performClick();
					return true;
				}
				return false;
			}
		});
		tvHappy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (AppUtils.isInternetConnected(getActivity())) {
					if (tvHappy.getCurrentTextColor() != Color.GRAY) {
						tvHappy.setCompoundDrawablesWithIntrinsicBounds(
								R.drawable.smile_gary, 0, 0, 0);
						tvHappy.setTextColor(Color.GRAY);
						updateSmile();
					}
				} else{
					
					//AppUtils.showToast("you're offline");
					AppUtils.showToast("Credwch eich bod yn all-lein");	
				}
			}
		});
		tvSad.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (AppUtils.isInternetConnected(getActivity())) {
					if (tvSad.getCurrentTextColor() != Color.GRAY) {
						tvSad.setCompoundDrawablesWithIntrinsicBounds(
								R.drawable.sad_gray, 0, 0, 0);
						tvSad.setTextColor(Color.GRAY);
						updateSadSmile();
					}
				} else
				{	
					//AppUtils.showToast("you're offline");
					AppUtils.showToast("Credwch eich bod yn all-lein");
				}
					
			}
		});
		tvViewSads.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppUtils.replaceFragment(new FragmentSmiles(false, mPost),
						getActivity().getSupportFragmentManager(),
						R.id.amMainLayout, false);

			}
		});
		tvViewSmiles.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppUtils.replaceFragment(new FragmentSmiles(true, mPost),
						getActivity().getSupportFragmentManager(),
						R.id.amMainLayout, false);

			}
		});
		// ivMenu.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		ivMenu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// new ImageDialog(mActivity,
				// post.getImageUrl()).show();
				// mActivity.registerForContextMenu(v);
				// mActivity.openContextMenu(v);
				// mActivity.unregisterForContextMenu(v);

				PopupMenu popup = new PopupMenu(getActivity(), ivMenu);
				// Inflating the Popup using xml file
				popup.getMenuInflater().inflate(R.menu.share_menu,
						popup.getMenu());

				if (mPost.isMyPost())
					popup.getMenu().removeItem(R.id.cnt_mnu_share);
				else
					popup.getMenu().removeItem(R.id.cnt_mnu_delete);
				// registering popup with OnMenuItemClickListener
				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						if (item.getTitle().equals(
								getActivity().getResources().getString(
										R.string.share)))
							sharePost(mPost);
						else if (item.getTitle().equals(
								getActivity().getResources().getString(
										R.string.quote)))
							quotePost(mPost);
						else if (item.getTitle().equals(
								getActivity().getResources().getString(
										R.string.delete)))
							deletePost(mPost);

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

		// }
		// });
		btnLoadPost.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getComments();
			}
		});
		isDataLoaded = true;
	}

	void populateData(Post post, boolean isSmileUpdated) {
		// int index = getArguments().getInt(EXTRA_POST_INDEX);
		// post = Session.listAllPost.get(index);
		if (post == null
				|| (post != null && post.getTransactAgainstPostId() <= 0)) {
			getPostDetail();
			return;
		}

		tvHappy.setTextColor(post.isSmiledByMe() ? Color.GREEN : Color.RED);
		tvSad.setTextColor(post.isSadSmiledByMe() ? Color.GREEN : Color.RED);
		tvViewSads.setVisibility(post.getSadQty() > 0 ? View.VISIBLE
				: View.INVISIBLE);
		tvViewSmiles.setVisibility(post.getSmileQty() > 0 ? View.VISIBLE
				: View.INVISIBLE);
		//viewSmiles.setVisibility(View.VISIBLE);
		tvTime.setText(post.getDateCreated());
		tvHappy.setText(" " + post.getSmileQty());
		tvSad.setText(" " + post.getSadQty());
		tvHappy.setCompoundDrawablesWithIntrinsicBounds(
				post.isSmiledByMe() ? R.drawable.smile_green
						: R.drawable.smile_red, 0, 0, 0);
		tvSad.setCompoundDrawablesWithIntrinsicBounds(
				post.isSadSmiledByMe() ? R.drawable.sad_green
						: R.drawable.sad_red, 0, 0, 0);
		if (isSmileUpdated)
			return;

		ivPost.setVisibility(post.isContainsImage() ? View.VISIBLE : View.GONE);
		ImageLoader.getInstance().displayImage(post.getCreatedByAvatar(),
				ivUser);
		ImageLoader.getInstance().displayImage(post.getImageUrl(), ivPost);
		tvUserName.setText(post.getCreatedByUsernameWithHat());
		tvProfileName.setText(post.getCreatedByName());

		tvPostText.setHashes(post.getHashes());
		tvPostText.setMentions(post.getMentions());
		tvPostText.setText(post.getPostTextOriginal());
		// tvPostText.setSelectAllOnFocus(false);
		// tvPostText.setFocusable(false);
		// tvPostText.setHighlightColor(Color.TRANSPARENT);
		// tvPostText.setHovered(false); //Selector(new
		// ColorDrawable(Color.TRANSPARENT));
		// tvPostText.setLinkTextColor(Color.RED);

		etReply.setHint("Ateb " + post.getCreatedByName() + " ...");
		try {
			etReply.setHorizontallyScrolling(false);
			etReply.setMinLines(1);
			etReply.setMaxLines(10);
			etReply.setHintTextColor(Color.parseColor(getActivity().getString(
					R.color.RedLine)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*if (post.isShare() && post.getSharerUsername() != null) {
			tvSharedBy.setVisibility(View.VISIBLE);
			tvSharedBy.setText("Shared by @" + post.getSharerUsername());
		}*/

	}

	void getComments() {
		// if (lastIdComment == 0 || commentList.size() == 0)
		// {
		if (!pd.isShowing())
			pd.show();
		handler.makeGetRequest(AppStatics.getAllCommentsUrl(
				mPost.getTransactAgainstPostId(), lastIdComment), REQ_COMMENTS);

		// }
		// else
		// notifyCommentsAdapter();
	}

	void getPostDetail() {
		if (!pd.isShowing())
			pd.show();
		handler.makeGetRequest(
				AppStatics.getPostDetailUrl(mPost.getPostId() + ""),
				REQ_POST_DETAIL);
	}

	void getLatestComment() {
		if (!pd.isShowing())
			pd.show();
		handler.makeGetRequest(AppStatics.getAllCommentsUrl(
				mPost.getTransactAgainstPostId(), 0), REQ_LATEST_COMMENT);
	}

	void updateSmile() {
		if (!pd.isShowing())
			pd.show();
		RequestHandler handler = new RequestHandler(this, true);
		try {
			JSONObject obj = new JSONObject();
			obj.put("postId", mPost.getTransactAgainstPostId());
			handler.makePostRequest(AppStatics.URL_POST_SMILE, obj,
					mPost.getPostId());

			mPost.setRevertSmiledByMe();
			AppPref.getInstance().setPostChanged(mPost);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	void updateSadSmile() {
		if (!pd.isShowing())
			pd.show();
		RequestHandler handler = new RequestHandler(this, true);
		try {
			JSONObject obj = new JSONObject();
			obj.put("postId", mPost.getTransactAgainstPostId());
			handler.makePostRequest(AppStatics.URL_POST_SAD_SMILE, obj,
					mPost.getPostId());

			mPost.setRevertSadSmileByMe();
			AppPref.getInstance().setPostChanged(mPost);
			// maintaining the data locally
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	void updateMySmileInList() {
		boolean isFound = false;
		for (int i = 0; i < smileOrSadList.size(); i++) {
			if (smileOrSadList.get(i) != null
					&& smileOrSadList
							.get(i)
							.getUserName()
							.equals(com.clecs.utils.Session.myProfileInfo
									.getUserName())) {
				if (mPost.getSmileSadNothing() == Post.KEY_NOTHING) {
					smileOrSadList.remove(i);
				} else {
					boolean smileStatus = mPost.getSmileSadNothing() == Post.KEY_SMILE ? true
							: false;
					smileOrSadList.get(i).setSmileOrSad(smileStatus);
				}
				isFound = true;
				break;
			}
		}
		if (!isFound && mPost.getSmileSadNothing() != Post.KEY_NOTHING) {
			SmileorSad smileorSad = new SmileorSad();
			smileorSad.setAvtar(Session.myProfileInfo.getProfileImage());
			smileorSad.setDate("0m");
			boolean smileStatus = mPost.getSmileSadNothing() == Post.KEY_SMILE ? true
					: false;
			smileorSad.setSmileOrSad(smileStatus);
			smileorSad.setUserName(Session.myProfileInfo.getUserName());

			smileOrSadList.add(smileorSad);
		}
	}

	/*
	 * void updateCommentCountInList() { for (int i = 0; i <
	 * Session.listAllPost.size(); i++) { if (mPost != null &&
	 * Session.listAllPost.get(i).getPostId() == mPost .getPostId()) {
	 * //Session.listAllPost.get(i).setCommentsQty(commentList.size());
	 * Session.listAllPost.get(i).setCommentsQty(mPost.getCommentsQty()); break;
	 * }
	 * 
	 * } }
	 */

	void quotePost(Post post) {
		AppUtils.replaceFragment(new FragmentNewPost(true, post), getActivity()
				.getSupportFragmentManager(), R.id.amMainLayout);
	}

	void sharePost(Post post) {
		if (!pd.isShowing())
			pd.show();
		try {
			handler = new RequestHandler(this, true);
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
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		AlertDialog dialog = builder.create();
		
		//builder.setMessage("Are you sure you want to delete this post?");
		//builder.setTitle("Clecs Confirmation");
		builder.setMessage("A ydych yn sicr eich bod am ddileu'r swydd hon?");
		builder.setTitle("Clecs");
		
		// yes delete
		builder.setPositiveButton("Iawn",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							if (!pd.isShowing())
								pd.show();
							handler = new RequestHandler(
									FragmentPostDetail.this, true);
							JSONObject obj = new JSONObject();
							obj.put("id", post.getPostId());
							handler.makePostRequest(AppStatics.URL_DELETE_POST,
									obj, REQ_CODE_DELETE);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
		// cancel
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

	@Override
	public void onResponse(String response, long requestCode) {
		// pd.dismiss();
		if (requestCode == REQ_COMMENTS) {
			ArrayList<Comment> list = new Gson().fromJson(response,
					new TypeToken<List<Comment>>() {
					}.getType());
			/*
			 * if (!hasMoreComments && list.size() <= 3){
			 * btnLoadPost.setVisibility(View.GONE); hasMoreComments = false; }
			 * else { btnLoadPost.setVisibility(View.VISIBLE); hasMoreComments =
			 * true; }
			 */

			// if (lastIdComment == 0)
			// while (list.size() > 3)
			// list.remove(3);

			if (list.size() > 0) {
				// commentList.addAll(list);

				adapterComments.addComments(list);
				notifyCommentsAdapter();
			}
			// scrollView.setScrollX(0);
		} else if (requestCode == REQ_POST_DETAIL) {
			Post post = new Gson().fromJson(response, Post.class);
			if (post != null) {
				mPost = post;
				getComments();
				populateData(post, false);
			} /*
			 * else { new AlertDialog.Builder(getActivity()).
			 * setMessage("Post not found,Post might be delete by user")
			 * .setTitle("Opps!") .create().show();
			 * this.getActivity().onBackPressed(); }
			 */
		} else if (requestCode == REQ_POST_COMMENT) {
			//AppUtils.showToast("posted");

			etReply.setText("");
			AppUtils.closeKeyboard(getActivity());
			// requestHandler.makeGetRequest(AppStatics.getAllCommentsUrl(mPost.getPostId(),
			// 0), REQ_COMMENTS);
			getLatestComment();
			// updateCommentCountInList();

		} else if (mPost != null && requestCode == mPost.getPostId()) {
			// TODO: AppPref.getPostCHangedAndClear(); currently it's not
			// clearing the memory
			mPost = AppPref.getInstance().getPostCHanged(requestCode);
			populateData(mPost, true);

			updateMySmileInList();

		} else if (requestCode == REQ_SAD_SMILE) {
			ArrayList<SmileorSad> list = new Gson().fromJson(response,
					new TypeToken<List<SmileorSad>>() {
					}.getType());

			// if (lastIdSadSmile == 0)
			// while (list.size() > 3)
			// list.remove(3);
			if (list.size() > 0) {
				smileOrSadList.addAll(list);
				lastIdSadSmile = Integer.valueOf(list.get(list.size() - 1)
						.getId());
			}
		} else if (requestCode == REQ_LATEST_COMMENT) {
			ArrayList<Comment> list = new Gson().fromJson(response,
					new TypeToken<List<Comment>>() {
					}.getType());
			if (list.size() > 0) {
				// commentList.add(0, list.get(0));

				adapterComments.addComment(0, list.get(0));
				notifyCommentsAdapter();
				if (mPost != null) {
					mPost.setCommentsQty(mPost.getCommentsQty() + 1);
					AppPref.getInstance().setPostChanged(mPost);
				}
				// btnLoadPost.setVisibility(View.VISIBLE);
			} /*
			 * else { btnLoadPost.setVisibility(View.GONE); }
			 */
		} /*
		 * else if (requestCode == REQ_CODE_DELETE) { for(Post post:
		 * Session.listAllPost){ if (post.getPostId() == mPost.getPostId()){
		 * Session.listAllPost.remove(post); } }
		 * getFragmentManager().popBackStack(); }
		 */

		// REFLECTING THE POST DATA TO THE MAIN POST LIST
		try {
			for (Post post : Session.listAllPost) {
				if (post.getPostId() == mPost.getPostId()) {
					if (requestCode == REQ_CODE_DELETE) {
						Session.listAllPost.remove(post);
						getFragmentManager().popBackStack();
					} else {
						Session.listAllPost.set(
								Session.listAllPost.indexOf(post), mPost);
					}
					break;
				}
			}
			/*
			 * for (int i = 0; i < Session.listAllPost.size(); i++) { if
			 * (Session.listAllPost.get(i).getPostId() == mPost.getPostId()) {
			 * if (requestCode == REQ_CODE_DELETE) {
			 * Session.listAllPost.remove(i);
			 * getFragmentManager().popBackStack(); } else {
			 * Session.listAllPost.set(i, mPost); } break; } }
			 */
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (requestCode != REQ_POST_DETAIL && requestCode != REQ_POST_COMMENT)
			pd.dismiss();
	}

	public void notifyCommentsAdapter() {
		if (commentList.size() > 0) {
			lastIdComment = commentList.get(commentList.size() - 1).getId();
			// lvComments.setVisibility(View.VISIBLE);
		} else {
			return;
		}

		if (commentList.size() < mPost.getCommentsQty()) {
			btnLoadPost.setVisibility(View.VISIBLE);
		} else {
			btnLoadPost.setVisibility(View.GONE);
		}

		/*
		 * if(adapterComments == null) { adapterComments = new
		 * AdapterComments(getActivity(), commentList, mPost, lvComments);
		 * //adapterComments.addComments(commentList);
		 * //lvComments.setAdapter(adapterComments); }
		 */

		// adapterComments.addComments(comments);
		/*
		 * lvComments.removeAllViews(); for(int
		 * i=0;i<adapterComments.getCount();i++) { View v =
		 * adapterComments.getView(i, null, null); lvComments.addView(v, new
		 * LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
		 * LinearLayout.LayoutParams.WRAP_CONTENT)); }
		 */
		// adapterComments.notifyDataSetChanged();
		// adapterComments.setListViewHeight(lvComments);
		// AppUtils.setListViewHeightBasedOnChildren(lvComments);
		// AppUtils.setListViewHeightBasedOnChildren(lvComments);

		// scrollView.smoothScrollTo(0, 0);
		// updateCommentCountInList();
	}

	@Override
	public void onError(String error, String reason, long requestCode) {
		pd.dismiss();
		if (mPost != null && requestCode == mPost.getPostId()) {
			populateData(mPost, false);
		} else if (requestCode == REQ_POST_DETAIL) {
			new AlertDialog.Builder(getActivity())
					//.setMessage("Post not found! Post might be deleted by user.")
					.setMessage("Ni chanfuwyd y swydd hon. Effallai y defnyddiwr ddileu hi?")
					.setTitle("Opps!")
					.setPositiveButton(android.R.string.yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									FragmentPostDetail.this.getActivity()
											.onBackPressed();
								}
							}).create().show();
		}
	}

	@Override
	public void onNoInternet() {
		pd.dismiss();
		// populateData(mPost, false);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (root != null) {
			ViewGroup parentViewGroup = (ViewGroup) root.getParent();
			if (parentViewGroup != null) {
				parentViewGroup.removeAllViews();
			}
		}
	}
}
