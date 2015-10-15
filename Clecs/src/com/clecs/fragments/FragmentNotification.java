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
import android.widget.Toast;

import com.clecs.MainActivity;
import com.clecs.R;
import com.clecs.adapters.AdapterNotification;
import com.clecs.network.RequestHandler;
import com.clecs.network.RequestHandler.ServerResponseListner;
import com.clecs.objects.AppNotification;
import com.clecs.utils.AppStatics;
import com.clecs.utils.AppUtils;
import com.clecs.utils.Session;
import com.clecs.widget.*;
import com.clecs.widget.PullAndLoadListView.OnLoadMoreListener;
import com.clecs.widget.PullToRefreshListView.OnRefreshListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class FragmentNotification extends Fragment implements
		ServerResponseListner {
	// final int REQ_CODE_NOTIF = 100;
	final int REQ_LATEST_NOTIF = 200;
	final int REQ_LOAD_MORE = 300;
	View rootView;
	PullAndLoadListView lv;
	AdapterNotification adapter;
	RequestHandler requestHandler;
	ProgressDialog pd;
	// int lastId = 0;
	String lastDate = null;

	// ArrayList<AppNotification> listNotif;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_notification,
					container, false);
			initViews();
			setListners();
			AppUtils.setFont(getActivity(), (ViewGroup) rootView);
		}
		MainActivity.mActivity.cancelAllPush();
		return rootView;
	}

	void initViews() {
		lv = (PullAndLoadListView) rootView
				.findViewById(R.id.fnLvNotifications);
		requestHandler = new RequestHandler(this, true);

		// To Test expiry
		// AppPref.setToken("test");
		// AppPref.setTokenExpireTime(60);

		if (adapter == null) {
			adapter = new AdapterNotification(getActivity(),
					Session.listNotififs);
			pd = new ProgressDialog(getActivity());
			//pd.setMessage("Getting Nofications...");
			// we just say loading...
			pd.setMessage("Llwytho...");
			
			pd.setCancelable(false);
			lv.setAdapter(adapter);
		}
		
		/*if (Session.listNotififs.size() > 0)
			lv.setVisibility(View.VISIBLE);
		else
			lv.setVisibility(View.INVISIBLE);
		*/
		requestHandler.makeGetRequest(AppStatics.getAllNotificationsUrl(null), REQ_LATEST_NOTIF);

		pd.show();

		// if (Session.listNotififs.size() > 0)
		// lv.setVisibility(View.VISIBLE);
		// notifyAdapter();
		// adapter = new AdapterNotification(getActivity(),
		// Session.listNotififs);
		// lv.setAdapter(adapter);

		lv.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// requestHandler.makeGetRequest(AppStatics.URL_GET_TOP_NOTFICATIONS,
				// REQ_LATEST_NOTIF);
				requestHandler.makeGetRequest(
						AppStatics.getAllNotificationsUrl(null),
						REQ_LATEST_NOTIF);
			}
		});
		lv.setOnLoadMoreListener(new OnLoadMoreListener() {

			@Override
			public void onLoadMore() {
				lastDate = Session.listNotififs.size() == 0 ? null
						: Session.listNotififs.get(
								Session.listNotififs.size() - 1).getDateOrig();
				requestHandler.makeGetRequest(
						AppStatics.getAllNotificationsUrl(lastDate),
						REQ_LOAD_MORE);
			}
		});
	}

	void setListners() {

	}

	@Override
	public void onResponse(String response, long requestCode) {
		if (requestCode == REQ_LOAD_MORE) {
			ArrayList<AppNotification> list = new Gson().fromJson(response,
					new TypeToken<List<AppNotification>>() {
					}.getType());

			if (list.size() > 0) {
				// lv.setAdapter(adapter);
				lv.setVisibility(View.VISIBLE);

				if (Session.listNotififs.size() == 0)
					Session.listNotififs.addAll(list);
				else {
					if (Session.listNotififs.get(
							Session.listNotififs.size() - 1).getId() > list
							.get(0).getId())
						Session.listNotififs.addAll(list);
				}
			}
			notifyAdapter();
			// MainActivity.updateNotification();
			// pd.dismiss();
			lv.onLoadMoreComplete();
			if (list.size() > 0)
				lastDate = list.get(list.size() - 1).getDateOrig();
		} else if (requestCode == REQ_LATEST_NOTIF) {
			ArrayList<AppNotification> list = new Gson().fromJson(response,
					new TypeToken<List<AppNotification>>() {
					}.getType());
			/*for (int i = 0; i < list.size(); i++) {
				if (Session.listNotififs.get(0).getId() < list.get(i).getId())
					Session.listNotififs.add(0, list.get(i));
				// if (!Session.listNotififs.contains(list.get(i)))
				// Session.listNotififs.addAll(0, list);
			}*/
			/*
			 // Get Latest notifications (OLD)
			 // to avoid loop
			if(list.size() > 0 && Session.listNotififs.size() > 0 && list.get(0).getId() == Session.listNotififs.get(0).getId())
				list.clear();
			// Load latest notifications and add into list
			while (list.size() > 0) {
				if (Session.listNotififs.size() > 0 &&  list.get(list.size() - 1).getId() <= Session.listNotififs
						.get(0).getId())
					list.remove(list.size() - 1);
				else {
					Session.listNotififs.addAll(0, list);
					break;
				}
			}*/
			
			//Refresh List (NEW)
			Session.listNotififs.clear();
			Session.listNotififs.addAll(list);
			
			notifyAdapter();

			// MainActivity.updateNotification();
			// pd.dismiss();
			lv.onRefreshComplete();
		}
		pd.dismiss();
		MainActivity.mActivity.updateNotification();
	}

	void notifyAdapter() {
		if (adapter != null)
			synchronized (adapter) {
				//lv.setAdapter(adapter);
				lv.setVisibility(View.VISIBLE);
				adapter.notifyDataSetChanged();
			}
	}

	@Override
	public void onError(String error, String reason, long requestCode) {
		AppUtils.showToast(reason);
		pd.dismiss();
	}

	@Override
	public void onNoInternet() {
		//Toast.makeText(getActivity(), "No Internet", Toast.LENGTH_SHORT).show();
		Toast.makeText(getActivity(), "Credwch eich bod yn all-lein", Toast.LENGTH_SHORT).show();
		pd.dismiss();
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
}
