package com.clecs.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.clecs.R;
import com.clecs.adapters.AdapterComments.ViewHolder;
import com.clecs.fragments.FragmentUser;
import com.clecs.objects.Follower;
import com.clecs.utils.AppUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class AdapterFollower extends BaseAdapter {
	/**
 * 
 */
	ArrayList<Follower> mList;
	FragmentActivity mActivity;

	public AdapterFollower(FragmentActivity fragmentActivity,
			ArrayList<Follower> followerList) {
		mActivity = fragmentActivity;
		mList = followerList;

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
		ImageView img;
		TextView tvTitle, tvName, tvSpace;
		
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
		LayoutInflater inflater = (LayoutInflater) mActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(R.layout.row_follow, null, false);
		AppUtils.setFont(mActivity, (ViewGroup) convertView);
		holder = new ViewHolder();
		
		holder.img = (ImageView) convertView.findViewById(R.id.rfIvUser);
		holder.tvTitle = (TextView) convertView.findViewById(R.id.rfTvPostText);
		holder.tvName = (TextView) convertView.findViewById(R.id.rfTvName);
		holder.tvSpace = (TextView) convertView.findViewById(R.id.rfTvDash);
		convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final Follower mFollower = mList.get(position);
		if (mFollower.getName() != null) {
			holder.tvName.setText(mFollower.getName());
		} else {
			holder.tvName.setVisibility(View.GONE);
			holder.tvSpace.setVisibility(View.GONE);
		}
		if (mFollower.getUserName() != null)
			holder.tvTitle.setText("@" + mFollower.getUserName());
		if (mFollower.getImageUrl() != null)
			ImageLoader.getInstance().displayImage(mFollower.getImageUrl(), holder.img);

		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentUser fragmentUser = new FragmentUser(mFollower
						.getUserName(), mFollower.getImageUrl(), false);
				AppUtils.replaceFragment(fragmentUser,
						mActivity.getSupportFragmentManager(),
						R.id.amMainLayout, false);
			}
		});
		return convertView;
	}

}