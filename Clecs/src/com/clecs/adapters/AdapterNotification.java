package com.clecs.adapters;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.clecs.MainActivity;
import com.clecs.R;
import com.clecs.adapters.AdapterPost.ViewHolder;
import com.clecs.fragments.FragmentPostDetail;
import com.clecs.fragments.FragmentUser;
import com.clecs.network.RequestHandler;
import com.clecs.network.RequestHandler.ServerResponseListner;
import com.clecs.objects.AppNotification;
import com.clecs.utils.AppStatics;
import com.clecs.utils.AppUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class AdapterNotification extends BaseAdapter implements
		ServerResponseListner {
	FragmentActivity mActivity;
	ArrayList<AppNotification> mList;

	RequestHandler requestHandler;

	public AdapterNotification(FragmentActivity activity,
			ArrayList<AppNotification> notificationList) {
		mActivity = activity;
		mList = notificationList;

		requestHandler = new RequestHandler(this, true);
	}

	@Override
	public int getCount() {
		return mList.size();
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
		TextView tvNotify, tvPostTime;
		ImageView ivUser;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		AppNotification mNotif = mList.get(position);
		if (convertView == null) {
		LayoutInflater inflater = (LayoutInflater) mActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(R.layout.row_notification, null, false);
		AppUtils.setFont(mActivity, (ViewGroup) convertView);
		holder = new ViewHolder();
		holder.ivUser = (ImageView) convertView.findViewById(R.id.rnIvUser);
		holder.tvNotify = (TextView) convertView.findViewById(R.id.rnTvNotifDesc);
		//TextView tvPostTime = (TextView) convertView.findViewById(R.id.rnTvPostTime);

		//tvPostTime.setText(mNotif.getDate());// + " : " + mNotif.getId() );
		convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();
		
		holder.tvNotify.setText(Html.fromHtml(mNotif.getText() + " - <font color=#ff0000>" + mNotif.getDate() + "</font>"));
		ImageLoader.getInstance()
				.displayImage(mNotif.getProfileImage(), holder.ivUser);

		if (mNotif.isRead()) {
			convertView.setBackgroundColor(Color.WHITE);
		} else {
			convertView.setBackgroundColor(Color.parseColor("#EBEBEB"));
		}
		holder.ivUser.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppNotification mNotif = mList.get(position);
				if (mNotif.getUserId() != null
						&& !mNotif.getUserId().equals("")) {
					FragmentUser userFragmentUser = new FragmentUser(mNotif
							.getUserId(), mNotif.getProfileImage(), false);
					AppUtils.replaceFragment(userFragmentUser,
							mActivity.getSupportFragmentManager(),
							R.id.amMainLayout, false);
				}
			}
		});

		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// if(mNotif.getType().equals(AppNotification.TYPE_COMMENT)||
				// mNotif.getType().equals(AppNotification.TYPE_MENTION)) {
				// //POST DETAIL
				// } else
				// if(mNotif.getType().equals(AppNotification.TYPE_SMILEY)) {
				// //
				// }
				AppNotification mNotif = mList.get(position);
				// if (mNotif.getUserId() != null &&
				// !mNotif.getUserId().equals(""))
				if (mNotif.getType().equals(AppNotification.TYPE_FOLLOWER)) {
					FragmentUser userFragmentUser = new FragmentUser(mNotif
							.getUserId(), mNotif.getProfileImage(), false);
					AppUtils.replaceFragment(userFragmentUser,
							mActivity.getSupportFragmentManager(),
							R.id.amMainLayout, false);
				} else if (mNotif.getPostId() != null) {
					FragmentPostDetail fragmentPostDetail = new FragmentPostDetail(
							Integer.parseInt(mNotif.getPostId()));
					AppUtils.replaceFragment(fragmentPostDetail,
							mActivity.getSupportFragmentManager(),
							R.id.amMainLayout, false);
				}
				JSONObject obj = new JSONObject();
				try {
					obj.put("id", mNotif.getId() + "");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				requestHandler.makePostRequest(
						AppStatics.URL_NOTIFICATION_READ, obj, mNotif.getId());
			}
		});

		return convertView;
	}

	@Override
	public void onResponse(String response, long requestCode) {
		for (int i = 0; i < mList.size(); i++) {
			if (mList.get(i).getId() == requestCode) {
				mList.get(i).setRead(true);
				mList.get(i).setNotRead(false);
				synchronized (this) {
					notifyDataSetChanged();
				}
				MainActivity.mActivity.updateNotification();
				break;
			}
		}
	}

	@Override
	public void onError(String error, String description, long requestCode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNoInternet() {
		// TODO Auto-generated method stub

	}

}
