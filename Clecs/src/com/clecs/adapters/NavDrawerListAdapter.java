package com.clecs.adapters;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.clecs.R;
import com.clecs.objects.MyProfileInfo;
import com.clecs.objects.NavDrawerItem;
import com.clecs.utils.AppUtils;
import com.clecs.utils.Session;
import com.nostra13.universalimageloader.core.ImageLoader;

public class NavDrawerListAdapter extends BaseAdapter
	{

		private Activity mActivity;
		private ArrayList<NavDrawerItem> navDrawerItems;

		public NavDrawerListAdapter( Activity activity, ArrayList<NavDrawerItem> navDrawerItems )
			{
				this.mActivity = activity;
				this.navDrawerItems = navDrawerItems;
			}

		@Override
		public int getCount()
			{
				return navDrawerItems.size();
			}

		@Override
		public Object getItem(int position)
			{
				return navDrawerItems.get(position);
			}

		@Override
		public long getItemId(int position)
			{
				return position;
			}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent)
			{
				LayoutInflater mInflater = (LayoutInflater) mActivity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
				if (position == 0)
					{
						convertView = mInflater.inflate(R.layout.row_drawer_user, null);
						AppUtils.setFont(mActivity, (ViewGroup)convertView);
						ImageView iv = (ImageView) convertView.findViewById(R.id.rduIvUser);
						TextView tvUserName = (TextView) convertView.findViewById(R.id.rduTvName);
						TextView tvEmail = (TextView) convertView.findViewById(R.id.rduTvEmail);

						MyProfileInfo myProfileInfo = Session.myProfileInfo;
						if (myProfileInfo != null)
							{
								ImageLoader.getInstance().displayImage(myProfileInfo.getProfileImage(), iv);
								tvUserName.setText("@" + myProfileInfo.getUserName());
								tvEmail.setText(myProfileInfo.getEmail());
							}
					}
				else
					{
						convertView = mInflater.inflate(R.layout.drawer_list_item, null);
						// ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
						TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
						// TextView txtCount = (TextView) convertView.findViewById(R.id.counter);

						// imgIcon.setImageResource(navDrawerItems.get(position).getIcon());
						txtTitle.setText(navDrawerItems.get(position).getTitle());

						// RelativeLayout.LayoutParams head_params = new RelativeLayout.LayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
						// head_params.setMargins(15, 0, 15, 0); // substitute parameters for left, top, right, bottom
						// // head_params.height = 60; head_params.width = 200;
						// convertView.setLayoutParams(head_params);

					}
				return convertView;
			}
	}
