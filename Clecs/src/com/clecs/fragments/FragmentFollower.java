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
import com.clecs.utils.Session;
import com.clecs.widget.LoadMoreListView;
import com.clecs.widget.LoadMoreListView.OnLoadMoreListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class FragmentFollower extends Fragment implements ServerResponseListner {

	int lastId = 0;

	View root;
	TextView tvTitle;
	LoadMoreListView listView;
	AdapterFollower adapter;
	RequestHandler handler;
	String userName, url;
	ArrayList<Follower> followerList;
	ProgressDialog pd;

	public FragmentFollower(String userName) {
		this.userName = userName;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (root == null) {
			root = inflater.inflate(R.layout.fragment_follower, container,
					false);
			initViews();
			System.out.println("Parent .. on Create");
			AppUtils.setFont(getActivity(), (ViewGroup) root);
		}
		return root;
	}

	void initViews() {
		pd = new ProgressDialog(getActivity());
		pd.setCancelable(false);
		pd.setMessage("Getting info...");
		tvTitle = (TextView) root.findViewById(R.id.ffTvTitle);
		setTitleURL();

		listView = (LoadMoreListView) root.findViewById(R.id.ffLv);
		handler = new RequestHandler(this, true);
		if (adapter == null || lastId == 0) {
			followerList = new ArrayList<Follower>();
			adapter = new AdapterFollower(getActivity(), followerList);
			pd.show();
			handler.makeGetRequest(getURL(), 100);
		} else
			notifyAdapter();
		
		listView.setOnLoadMoreListener(new OnLoadMoreListener() {

			@Override
			public void onLoadMore() {
				//pd.show();
				handler.makeGetRequest(getURL(), 100);
			}
		});

	}

	void setTitleURL() {
		if (userName.equalsIgnoreCase(Session.myProfileInfo.getUserName())) {
			//tvTitle.setText("Who Following Me");
			tvTitle.setText("Pwy sy'n dilyn fi");
			url = AppStatics.getNextUrl(AppStatics.URL_WHO_FOLLOW_ME, lastId);
		} else
		{
			//tvTitle.setText("Who Follow's Them");
			tvTitle.setText("Pwy sy'n eu dilyn");
		}
	}

	String getURL() {
		if (userName.equalsIgnoreCase(Session.myProfileInfo.getUserName()))
			return AppStatics.getNextUrl(AppStatics.URL_WHO_FOLLOW_ME, lastId);
		else
			return AppStatics.getNextUrl(AppStatics.URL_WHO_FOLLOW_THEM,
					userName, lastId);
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
		if (lastId == 0)
			listView.setAdapter(adapter);
		else
			notifyAdapter();

		listView.onLoadMoreComplete();
		pd.dismiss();

		if (followerList.size() > 0)
			lastId = followerList.get(followerList.size() - 1).getId();

	}

	void notifyAdapter() {
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
