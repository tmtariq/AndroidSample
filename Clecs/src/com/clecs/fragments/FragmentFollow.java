package com.clecs.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clecs.R;
import com.clecs.adapters.AdapterFollower;

import com.clecs.network.RequestHandler;
import com.clecs.network.RequestHandler.ServerResponseListner;
import com.clecs.objects.Follower;
import com.clecs.utils.AppStatics;
import com.clecs.utils.AppUtils;
import com.clecs.widget.LoadMoreListView;
import com.clecs.widget.LoadMoreListView.OnLoadMoreListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class FragmentFollow extends Fragment implements ServerResponseListner {
	// ImageLoader imageLoader;
	int lastId = 0;

	View root;
	TextView tvTitle;
	LoadMoreListView listView;
	AdapterFollower adapter;
	RequestHandler handler;

	final int REQ_LOAD_MORE = 100;
	ArrayList<Follower> followerList;

	String url;
	String userName = "";

	ProgressDialog pd;

	// final int REQ_LATEST = 200;
	boolean hasUserName;

	public FragmentFollow(boolean hasUserName, String userName) {
		this.hasUserName = hasUserName;
		if (hasUserName)
			if (!this.userName.equals(userName)
					|| this.userName.trim().equals("")) {
				lastId = 0;
			}
		this.userName = userName;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (root == null) {
			root = inflater.inflate(R.layout.fragment_follower, container,
					false);
			pd = new ProgressDialog(getActivity());
			pd.setCancelable(false);
			//pd.setMessage("Getting info...");
			pd.setMessage("Llwytho...");
			initViews();
			System.out.println("Parent .. on Create");
			AppUtils.setFont(getActivity(), (ViewGroup) root);
		}
		return root;
	}

	void initViews() {
		// lastId = 0;
		tvTitle = (TextView) root.findViewById(R.id.ffTvTitle);
		listView = (LoadMoreListView) root.findViewById(R.id.ffLv);
		handler = new RequestHandler(this, true);
		if (followerList == null)// || isNew)
		{
			followerList = new ArrayList<Follower>();
			lastId = 0;
		}
		if (adapter == null)// || isNew)
			adapter = new AdapterFollower(getActivity(), followerList);
		else
			notifyAdapter();
		listView.setAdapter(adapter);

		listView.setOnLoadMoreListener(new OnLoadMoreListener() {

			@Override
			public void onLoadMore() {
				if (hasUserName)
					makeRequest(url, userName);
				else
					makeRequest(url);
			}
		});
	}

	void makeRequest(String url) {
		pd.show();
		handler.makeGetRequest(AppStatics.getNextUrl(url, lastId),
				REQ_LOAD_MORE);
		this.url = url;

	}

	void makeRequest(String url, String userName) {
		pd.show();
		handler.makeGetRequest(AppStatics.getNextUrl(url, userName, lastId),
				REQ_LOAD_MORE);
		this.url = url;
		this.userName = userName;
	}

	public void setTitle(String title) {
		tvTitle.setText(title);
	}

	@Override
	public void onResponse(String response, long requestCode) {
		ArrayList<Follower> list = new Gson().fromJson(response,
				new TypeToken<List<Follower>>() {
				}.getType());
		// API RETURNING THE DUPLICATE
		// for (int i = 0; i < list.size(); i++)
		// {
		// if (!followerList.contains(list.get(i)))
		// followerList.add(list.get(i));
		// }

		// TO PREVENT FROM DUPLICATION
		if (followerList.size() == 0 || lastId > 0)
			followerList.addAll(list);
		notifyAdapter();
		if (list.size() > 0) {
			lastId = list.get(list.size() - 1).getId();
			// lastId += 1;
		}
		listView.setVisibility(View.VISIBLE);
		listView.onLoadMoreComplete();
		pd.dismiss();
	}

	private void notifyAdapter() {
		synchronized (adapter) {
			// listView.setAdapter(adapter);
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onError(String error, String description, long requestCode) {
		listView.onLoadMoreComplete();
		pd.dismiss();
	}

	@Override
	public void onNoInternet() {
		listView.onLoadMoreComplete();
		pd.dismiss();
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
