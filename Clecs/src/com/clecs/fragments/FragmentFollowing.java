package com.clecs.fragments;

import com.clecs.utils.AppStatics;
import com.clecs.utils.Session;

public class FragmentFollowing extends FragmentFollow {

	String mUrl;
	String mUserName;
	String title = "";
	boolean hasUserName, isStarted = false;

	void setMyProfileData() {
		mUrl = AppStatics.URL_WHO_I_FOLLOW;
		title = "Who I Follow";
	}

	public FragmentFollowing(boolean hasUserName, String userName) {
		super(hasUserName, userName);
		this.hasUserName = hasUserName;

		if (hasUserName)
			mUserName = userName;
		else
			setMyProfileData();

		mUrl = AppStatics.URL_WHO_THEY_FOLLOW;
		//title = "Who follow's";// "@" + userName + "'s Followings";
		title = "Pwy sy'n dilyn @" + userName;// "@" + userName + "'s Followings";

	}

	@Override
	public void onStart() {
		super.onStart();
		if(isStarted) return;
		setTitleUrl();
		setTitle(title);
		if (followerList.size() == 0)
			if (hasUserName) {
				if (mUserName.trim().length() > 0)
					makeRequest(mUrl, mUserName);
			} else
				makeRequest(mUrl);
		isStarted = true;
	}

	void setTitleUrl() {
		if (hasUserName) {
			if (mUserName.equals(Session.myProfileInfo.getUserName())) {
				//title = "Who I Follow";
				title = "Pwy yr wyf yn dilyn";
				mUrl = AppStatics.URL_WHO_I_FOLLOW;
			} else{
				//title = "Who Follow's";	
				title = "Pwy sy'n dilyn @" + userName;
			}
		} else {
			//title = "Who I Follow";
			title = "Pwy yr wyf yn dilyn";
			mUrl = AppStatics.URL_WHO_I_FOLLOW;
		}
	}
	// ImageLoader imageLoader;
	// int lastId = 0;
	//
	// View root;
	// TextView tvTitle;
	// PullAndLoadListView listView;
	// AdapterFollower adapter;
	//
	// @Override
	// public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup
	// container, @Nullable Bundle savedInstanceState)
	// {
	// root = inflater.inflate(R.layout.fragment_follower, container, false);
	// initViews();
	// return root;
	// // return super.onCreateView(inflater, container, savedInstanceState);
	// }
	//
	// void initViews()
	// {
	// tvTitle = (TextView) root.findViewById(R.id.ffTvTitle);
	// listView = (PullAndLoadListView) root.findViewById(R.id.ffLv);
	// RequestHandler handler = new RequestHandler(this, true);
	// tvTitle.setText("Who I Follow");
	// handler.makeGetRequest(AppStatics.getWhoIFollowUrl(lastId), 100);
	//
	// }
	//
	// @Override
	// public void onResponse(String response, int requestCode)
	// {
	// ArrayList<Follower> followerList = new Gson().fromJson(response, new
	// TypeToken<List<Follower>>()
	// {
	// }.getType());
	// adapter = new AdapterFollower(getActivity(), followerList);
	// listView.setAdapter(adapter);
	// }
	//
	// @Override
	// public void onError(String error, String description, int requestCode)
	// {
	//
	// }
	//
	// @Override
	// public void onNoInternet()
	// {
	//
	// }
}
