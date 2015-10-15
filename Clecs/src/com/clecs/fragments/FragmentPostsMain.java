package com.clecs.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import com.clecs.MainActivity;
import com.clecs.R;
import com.clecs.adapters.AdapterPost;
import com.clecs.network.RequestHandler;
import com.clecs.network.RequestHandler.ServerResponseListner;
import com.clecs.objects.AppNotification;
import com.clecs.objects.Post;
import com.clecs.utils.AppStatics;
import com.clecs.utils.AppUtils;
import com.clecs.utils.Session;
import com.clecs.widget.PullAndLoadListView;
import com.clecs.widget.PullAndLoadListView.OnLoadMoreListener;
import com.clecs.widget.PullToRefreshListView.OnRefreshListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

public class FragmentPostsMain extends Fragment implements
		ServerResponseListner {
	final int REQ_ALL_POST = 100;
	final int REQ_NOTIFICATION = 200;
	final int REQ_LATEST_POSTS = 300;
	final int REQ_LOAD_MORE = 400;
	RequestHandler requestHandler;
	AdapterPost adapter;
	// int lastId = 0;
	View root;
	PullAndLoadListView lv;
	ProgressDialog pd;
	boolean isReloading;

	public static FragmentPostsMain instance;
	protected boolean pauseOnScroll = true;
	protected boolean pauseOnFling = true;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (root == null) {
			root = inflater.inflate(R.layout.fragment_posts_main, container,
					false);
			instance = this;
			initView();
			AppUtils.setFont(getActivity(), (ViewGroup) root);
		}
		return root;
	}

	void initView() {

		pd = new ProgressDialog(getActivity());
		pd.setCancelable(false);
		lv = (PullAndLoadListView) root.findViewById(R.id.fpmLv);
		
		//pd.setMessage("Getting Posts...");
		/// we are saying loading...
		pd.setMessage("Llwytho...");
		
		requestHandler = new RequestHandler(this, true);
		//requestHandler.makeGetRequest(AppStatics.getAllNotificationsUrl(null), REQ_NOTIFICATION);
		if (Session.listAllPost == null) {
			pd.show();
			requestHandler.makeGetRequest(
					AppStatics.getAllPostsWithLinksUrl(getLastId()),
					REQ_ALL_POST);
		} else {
			// if (adapter == null) {
			adapter = new AdapterPost(getActivity(), Session.listAllPost);
			// }
			changeListViewVisibility();
			lv.setAdapter(adapter);
			notifyAdapter();
			// adapter = new AdapterPost(getActivity(), Session.listAllPost);
			// adapter.notifyDataSetChanged();
		}

		lv.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				refreshPosts();
			}
		});
		lv.setOnLoadMoreListener(new OnLoadMoreListener() {

			@Override
			public void onLoadMore() {
				requestHandler.makeGetRequest(
						AppStatics.getAllPostsWithLinksUrl(getLastId()),
						REQ_LOAD_MORE);
			}
		});
		//lv.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), pauseOnScroll, pauseOnFling));

	}

	/*@Override
	public void onAttach(Activity activity) {
		MainActivity.mActivity.updateNotification();
		requestHandler = new RequestHandler(this, true);
		requestHandler.makeGetRequest(AppStatics.getAllNotificationsUrl(null), REQ_NOTIFICATION);
		super.onAttach(activity);
	}*/

	@SuppressWarnings("unchecked")
	@Override
	public void onResponse(String response, long requestCode) {
		if (requestCode == REQ_ALL_POST) {
			ArrayList<Post> postList = new Gson().fromJson(response,
					new TypeToken<List<Post>>() {
					}.getType());
			System.out.println(response);
			if (postList != null)
				Session.listAllPost = (ArrayList<Post>) postList.clone();
			loadCashedList();
			adapter = new AdapterPost(getActivity(), Session.listAllPost);
			lv.setAdapter(adapter);
			pd.dismiss();
			changeListViewVisibility();
		} else if (requestCode == REQ_NOTIFICATION) {
			// "id":30176,"read":true,"notRead":false,"text":"New Smiley from TheGirl","date":"22 Jan","type":"Smiley"
			ArrayList<AppNotification> list = new Gson().fromJson(response,
					new TypeToken<List<AppNotification>>() {
					}.getType());
			//Session.listNotififs = (ArrayList<AppNotification>) list.clone();
			Session.listNotififs = list;
			//MainActivity.mActivity.updateNotification();
		} else if (requestCode == REQ_LATEST_POSTS) {
			// lastId += 1;
			ArrayList<Post> postList = new Gson().fromJson(response,
					new TypeToken<List<Post>>() {
					}.getType());
			if (postList.size() > 0) {
				Session.listAllPost.addAll(0, postList);
				notifyAdapter();
				changeListViewVisibility();
			}
			lv.onRefreshComplete();
		} else if (requestCode == REQ_LOAD_MORE) {
			ArrayList<Post> postList = new Gson().fromJson(response,
					new TypeToken<List<Post>>() {
					}.getType());
			Session.listAllPost.addAll(postList);
			// adapter = new AdapterPost(getActivity(), Session.listAllPost);
			// lv.setAdapter(adapter);
			notifyAdapter();
			lv.onLoadMoreComplete();
			changeListViewVisibility();
		}
	}

	private void loadCashedList() {
		if (Session.listAllPost != null)
			if (Session.listAllPost.size() == 0
					&& Session.listAllPostsLocal != null)
				Session.listAllPost = (ArrayList<Post>) Session.listAllPostsLocal
						.clone();
		notifyAdapter();
	}

	void notifyAdapter() {
		if (adapter != null)
			synchronized (adapter) {
				changeListViewVisibility();
				adapter.notifyDataSetChanged();
			}
	}

	private void changeListViewVisibility() {
		if (Session.listAllPost.size() == 0)
			lv.setVisibility(View.INVISIBLE);
		else
			lv.setVisibility(View.VISIBLE);
	}

	@Override
	public void onError(String error, String reason, long requestCode) {
		if (pd != null)
			pd.dismiss();
		if (requestCode == REQ_LOAD_MORE)
			lv.onLoadMoreComplete();
		else if (requestCode == REQ_LATEST_POSTS)
			lv.onRefreshComplete();
		else if (requestCode == REQ_ALL_POST)
			loadCashedList();
	}

	@Override
	public void onNoInternet() {
		if (pd != null)
			pd.dismiss();
		if (lv != null) {
			lv.onLoadMoreComplete();
			lv.onRefreshComplete();
		}
		loadCashedList();
	}

	long getLastId() {
		if (Session.listAllPost != null) {
			int size = Session.listAllPost.size();
			if (size > 0)
				return Session.listAllPost.get(size - 1).getPostId();
		}
		return 0;
	}

	long getLatestId() {
		if (Session.listAllPost != null) {
			int size = Session.listAllPost.size();
			if (size > 0)
				return Session.listAllPost.get(0).getPostId();
		}
		return 0;
	}

	public void refreshPosts() {
		if (getLastId() > 0)
			requestHandler.makeGetRequest(
					AppStatics.getLatestPostWithLinksUrl(getLatestId()),
					REQ_LATEST_POSTS);
		else {
			pd.show();
			requestHandler.makeGetRequest(
					AppStatics.getAllPostsWithLinksUrl(getLastId()),
					REQ_ALL_POST);
		}
	}

	/*@Override
	public void onResume() {
		updateList();
		super.onResume();
	}*/

	public void updateList() {
		notifyAdapter();
		if (Session.listAllPost == null)
			Session.listAllPost = new ArrayList<Post>();
		if (adapter == null)
			adapter = new AdapterPost(getActivity(), Session.listAllPost);
		refreshPosts();
		if (Session.listAllPost != null)
			if (Session.listAllPost.size() == 0)
				lv.setVisibility(View.INVISIBLE);
	}

	public void reloadList() {
		if (AppUtils.isInternetConeected(getActivity())) {
			isReloading = true;
			if (Session.listAllPost != null && Session.listAllPost.size() > 0) {
				Session.listAllPostsLocal = (ArrayList<Post>) Session.listAllPost
						.clone();
				Session.listAllPost.clear();
			}
			updateList();
		} else
		{
			//AppUtils.showToast("No internet");
			AppUtils.showToast("Credwch eich bod yn all-lein");
			
		}
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
