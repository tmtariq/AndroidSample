package com.clecs.fragments;

import com.clecs.utils.AppStatics;

public class FragmentSuggestions extends FragmentFollow {
	
	boolean isStarted = false;
	public FragmentSuggestions() {
		super(false, null);
	}

	@Override
	public void onStart() {
		super.onStart();
		if (isStarted) return;
		//tvTitle.setText("Suggestions");
		tvTitle.setText("Awgrymiadau");
		makeRequest(AppStatics.URL_WHO_SHOULD_I_FOLLOW);
		isStarted = true;
	}
}

// extends Fragment implements ServerResponseListner
// {
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
// }
//
// void initViews()
// {
// tvTitle = (TextView) root.findViewById(R.id.ffTvTitle);
// listView = (PullAndLoadListView) root.findViewById(R.id.ffLv);
// RequestHandler handler = new RequestHandler(this, true);
// tvTitle.setText("Suggestions");
// handler.makeGetRequest(AppStatics.URL_WHO_SHOULD_I_FOLLOW, 100);
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
// }
