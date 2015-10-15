package com.clecs.fragments;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.clecs.R;
import com.clecs.adapters.AdapterPost;
import com.clecs.dialog.ImageDialog;
import com.clecs.network.RequestHandler;
import com.clecs.network.RequestHandler.ServerResponseListner;
import com.clecs.objects.MyProfileInfo;
import com.clecs.objects.Post;
import com.clecs.objects.Profile;
import com.clecs.utils.AppStatics;
import com.clecs.utils.AppUtils;
import com.clecs.utils.Session;
import com.clecs.widget.PullAndLoadListView;
import com.clecs.widget.PullAndLoadListView.OnLoadMoreListener;
import com.clecs.widget.PullToRefreshListView.OnRefreshListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

public class FragmentUser extends Fragment implements ServerResponseListner,
		OnClickListener {
	// public static final String EXTRA_USER_NAME = "extraString";
	// public static final String EXTRA_USER_IMAGE = "extraImage";
	// public static final String EXTRA_POST = "extraPost";
	final int REQ_LOAD_MORE = 100;
	final int REQ_LATEST = 200;
	final int REQ_CODE_USER_INFO = 300;
	final int REQ_FOLLOW = 400;
	final int REQ_REFRESH = 500;

	View rootView, emptyView;
	ImageView ivUser;
	TextView tvUserName, tvName, tvUserTitle;
	Button btnFollowing, btnFollowers, btnEditProfile, btnFollow;
	PullAndLoadListView lvPosts;
	ProgressDialog pd;
	boolean isMyPost;
	RequestHandler requestHandler;
	String userName, userImage;
	Profile profileOtherUser;
	AdapterPost adapter;
	int lastId = 0;
	ArrayList<Post> listPosts = new ArrayList<Post>();

	public FragmentUser(String userName) {
		isMyPost = false;
		userImage = null;
		this.userName = userName;
		System.out.println("UserName is :: " + userName);
	}

	public FragmentUser(String userName, String imgUrl, boolean isMyPost) {
		this.isMyPost = isMyPost;
		userImage = imgUrl;
		this.userName = userName;
		System.out.println("UserName is :: " + userName);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (rootView != null) {
			ViewGroup parentViewGroup = (ViewGroup) rootView.getParent();
			if (parentViewGroup != null) {
				parentViewGroup.removeAllViews();
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_user, container,
					false);
			initViews();
			AppUtils.setFont(getActivity(), (ViewGroup) rootView);
		}
		return rootView;
	}

	void initViews() {
		pd = new ProgressDialog(getActivity());

		ivUser = (ImageView) rootView.findViewById(R.id.fuIvUser);
		tvUserName = (TextView) rootView.findViewById(R.id.fuTvUserName);
		tvUserTitle = (TextView) rootView.findViewById(R.id.fuTvUserTitle);
		tvName = (TextView) rootView.findViewById(R.id.fuTvProfileName);
		btnEditProfile = (Button) rootView.findViewById(R.id.fuBtnEditProfile);
		btnFollow = (Button) rootView.findViewById(R.id.fuBtnFollow);
		btnFollowers = (Button) rootView.findViewById(R.id.fuBtnFollowers);
		btnFollowing = (Button) rootView.findViewById(R.id.fuBtnFollowing);
		lvPosts = (PullAndLoadListView) rootView.findViewById(R.id.fuLvPost);

		/*emptyView = rootView.findViewById(R.id.fuEmptyView);
		emptyView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				lvPosts.onRefresh();
			}
		});
		lvPosts.setEmptyView(emptyView);*/

		requestHandler = new RequestHandler(this, true);
		setListners();

	}

	void setListners() {
		// if (adapter == null)
		//{
			// lastId = 0;
			// listPosts = new ArrayList<Post>();
		adapter = new AdapterPost(getActivity(), listPosts, true);
		lvPosts.setAdapter(adapter);
		notifyAdapter();
		populateData();
		//}
		// else
		// notifyAdapter();

		btnEditProfile.setOnClickListener(this);
		btnFollow.setOnClickListener(this);
		btnFollowers.setOnClickListener(this);
		btnFollowing.setOnClickListener(this);
		ivUser.setOnClickListener(this);

		// lvPosts.setOnRefreshListener(new OnRefreshListener()
		// {
		//
		// @Override
		// public void onRefresh()
		// {
		// requestHandler.makeGetRequest(AppStatics.getAllPostsForUser(lastId,
		// userName), REQ_LOAD_MORE);
		// }
		// });
		lvPosts.setOnLoadMoreListener(new OnLoadMoreListener() {
			@Override
			public void onLoadMore() {
				requestHandler.makeGetRequest(
						AppStatics.getAllPostsForUser(lastId, userName),
						REQ_LOAD_MORE);
			}
		});
		lvPosts.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				refreshPosts();
			}
		});
	}

	void populateData() {
		//pd.setMessage("Getting posts...");
		//loading
		pd.setMessage("Llwytho...");

		getUserInfo(userName);

		setUserImage(userImage);

		if (isMyPost) {
			btnEditProfile.setVisibility(View.VISIBLE);
			btnFollow.setVisibility(View.GONE);

			MyProfileInfo profileInfo = Session.myProfileInfo;
			
			tvUserTitle.setText("My Posts");
			
			setFollower(profileInfo);
		} else {
			btnEditProfile.setVisibility(View.GONE);
			btnFollow.setVisibility(View.VISIBLE);

			// tvName.setText(userName);
			tvUserName.setText("@" + userName);
			tvUserTitle.setText("@" + userName + " Posts");
			
			
			
		}
		if (listPosts.size() == 0) {
			pd.show();
			requestHandler.makeGetRequest(
					AppStatics.getAllPostsForUser(lastId, userName),
					REQ_LOAD_MORE);

		} else {
			notifyAdapter();
		}

	}

	void refreshPosts() {
		requestHandler.makeGetRequest(
				AppStatics.getAllPostsForUser(0, userName),
				listPosts.size() == 0 ? REQ_LOAD_MORE : REQ_REFRESH);
	}

	void getUserInfo(String userName) {
		if (profileOtherUser == null)
			requestHandler.makeGetRequest(AppStatics.URL_GET_USER_DETAIL
					+ "?id=" + userName, REQ_CODE_USER_INFO);
		else
			updateUserInfo();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fuIvUser:
			new ImageDialog(getActivity(), userImage).show();// isMyPost ?
																// Session.myProfileInfo.getProfileImage()
																// :
																// post.getCreatedByAvatar()).show();
			break;
		case R.id.fuBtnEditProfile:
			AppUtils.replaceFragment(new FragmentEditProfile(), getActivity()
					.getSupportFragmentManager(), R.id.amMainLayout, true);
			break;
		case R.id.fuBtnFollow: {
			try {
				JSONObject object = new JSONObject();
				object.put("user", profileOtherUser.getUsername());
				object.put("follow", !profileOtherUser.isYouAreFollowing());
				requestHandler.makePostRequest(AppStatics.URL_FOLLOW_USER,
						object, REQ_FOLLOW);
				btnFollow.setEnabled(false);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
			break;
		case R.id.fuBtnFollowers: {
			AppUtils.replaceFragment(new FragmentFollower(userName),
					getActivity().getSupportFragmentManager(),
					R.id.amMainLayout);
		}
			break;
		case R.id.fuBtnFollowing:
			AppUtils.replaceFragment(new FragmentFollowing(true, userName),
					getActivity().getSupportFragmentManager(),
					R.id.amMainLayout);
			break;
		default:
			break;
		}
	}

	@Override
	public void onResponse(String response, long requestCode) {
		if (requestCode == REQ_REFRESH) {
			ArrayList<Post> postList = new Gson().fromJson(response,
					new TypeToken<List<Post>>() {
					}.getType());
			if (postList.size() > 0) {
				while (postList.size() > 0 && listPosts.size() > 0) {
					if (postList.get(postList.size() - 1).getPostId() <= listPosts
							.get(0).getPostId())
						postList.remove(postList.size() - 1);
					else {
						listPosts.addAll(0, postList);
						break;
					}
				}
				notifyAdapter();
				lvPosts.onRefreshComplete();
			}
		}
		if (requestCode == REQ_LOAD_MORE) {
			ArrayList<Post> postList = new Gson().fromJson(response,
					new TypeToken<List<Post>>() {
					}.getType());

			if (postList.size() > 0) {
				listPosts.addAll(postList);
				lastId = (int) listPosts.get(postList.size() - 1).getPostId();
			}

			notifyAdapter();
			lvPosts.onLoadMoreComplete();
			pd.dismiss();
		} else if (requestCode == REQ_LATEST) {
			ArrayList<Post> postList = new Gson().fromJson(response,
					new TypeToken<List<Post>>() {
					}.getType());

			if (postList.size() > 0) {
				listPosts.addAll(0, postList);
				lastId = (int) listPosts.get(postList.size() - 1).getPostId();
			}

			notifyAdapter();
			lvPosts.onRefreshComplete();
		} else if (requestCode == REQ_CODE_USER_INFO) {
			profileOtherUser = new Gson().fromJson(response, Profile.class);
			updateUserInfo();
		} else if (requestCode == REQ_FOLLOW) {
			profileOtherUser.setYouAreFollowing(!profileOtherUser
					.isYouAreFollowing());
			setFollower(profileOtherUser);
			btnFollow.setEnabled(true);
		}

	}

	void updateUserInfo() {
		if (profileOtherUser != null) {
			setUserImage(profileOtherUser.getProfileImage());
			setFollower(profileOtherUser);

			if (profileOtherUser.getUsername().equals(
					Session.myProfileInfo.getUserName())) {
				Session.myProfileInfo.setProfileImage(profileOtherUser
						.getProfileImage());
				updateUserImageLocally(Session.myProfileInfo.getProfileImage());
			}
		}
	}

	private void notifyAdapter() {
		//adapter = new AdapterPost(getActivity(), listPosts);
		if (listPosts.size() > 0)
			lvPosts.setVisibility(View.VISIBLE);
		else
			lvPosts.setVisibility(View.GONE);

		if (adapter != null)
			synchronized (adapter) {
				adapter.notifyDataSetChanged();
			}
	}

	void setUserImage(String path) {
		if (path != null && ivUser != null)
			ImageLoader.getInstance().displayImage(path, ivUser);
	}

	void setFollower(MyProfileInfo profileInfo) {
		//btnFollowers.setText(profileInfo.getFollowersCount() + " Follower's");
		//btnFollowing.setText("Following " + profileInfo.getFollowingCount());
		btnFollowers.setText(profileInfo.getFollowersCount() + " Dilynwyr");
		btnFollowing.setText("Dilyn " + profileInfo.getFollowingCount());
	}

	void setFollower(Profile profile) {
		//btnFollowers.setText(profile.getQtyOfFollowers() + " Follower's");
		//btnFollowing.setText("Following " + profile.getQtyOfFollowing());
		//btnFollow.setText(profile.isYouAreFollowing() ? "You're following" : "Follow");
		btnFollowers.setText(profile.getQtyOfFollowers() + " Dilynwyr");
		btnFollowing.setText("Dilyn " + profile.getQtyOfFollowing());
		btnFollow.setText(profile.isYouAreFollowing() ? "Chi'n dilyn": "Dilyn");
		
		
		btnFollow.setTextColor(profile.isYouAreFollowing() ? Color.parseColor("black")
				: Color.parseColor("white"));

		btnFollow.setBackgroundResource(profile.isYouAreFollowing() ? R.drawable.selector_btn_green : R.drawable.selector_btn_pink);
		

		btnEditProfile.setVisibility(profile.isThisIsMyProfile() ? View.VISIBLE
				: View.GONE);
		btnFollow.setVisibility(profile.isThisIsMyProfile() ? View.GONE
				: View.VISIBLE);

		tvName.setText(profile.getProfileName());
		tvUserName.setText(profile.getUsernameWithHat());

	}

	@Override
	public void onError(String error, String reason, long requestCode) {
		AppUtils.showToast(reason);
		pd.dismiss();
		btnFollow.setEnabled(true);
	}

	@Override
	public void onNoInternet() {
		//Toast.makeText(getActivity(), "No Internet", Toast.LENGTH_SHORT).show();
		Toast.makeText(getActivity(), "Credwch eich bod yn all-lein", Toast.LENGTH_SHORT).show();
		pd.dismiss();
		btnFollow.setEnabled(true);
	}

	void updateUserImageLocally(String createdByAvatar) {
		for (int i = 0; i < Session.listAllPost.size(); i++) {
			if (Session.listAllPost.get(i).isMyPost())
				Session.listAllPost.get(i).setCreatedByAvatar(createdByAvatar);
		}
	}
}
