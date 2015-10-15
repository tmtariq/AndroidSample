package com.clecs.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.clecs.R;
import com.clecs.fragments.FragmentUser;
import com.clecs.objects.SmileorSad;
import com.clecs.utils.AppUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class AdapterSadSmiles extends BaseAdapter
	{
		Context context;
		ArrayList<SmileorSad> mList;
		
		FragmentActivity mActivity;

		public AdapterSadSmiles( FragmentActivity avtivity, ArrayList<SmileorSad> smileorsad )
			{
				mActivity = avtivity;
				mList = smileorsad;
				
			}

		@Override
		public int getCount()
			{
				return mList.size();
			}

		@Override
		public Object getItem(int position)
			{
				// TODO Auto-generated method stub
				return null;
			}

		@Override
		public long getItemId(int position)
			{
				// TODO Auto-generated method stub
				return 0;
			}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent)
			{
				SmileorSad mSmileOrSad = mList.get(position);
				LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.post_, null);
				AppUtils.setFont(mActivity, (ViewGroup)convertView);
				TextView tvUserName = (TextView) convertView.findViewById(R.id.rpTvUsername);
				ImageView ivUser = (ImageView) convertView.findViewById(R.id.rpIvUser);
				if (mSmileOrSad != null)
					{

						tvUserName.setText(mSmileOrSad.getUserName());
						ImageLoader.getInstance().displayImage(mSmileOrSad.getAvtar(), ivUser);
						convertView.setOnClickListener(new OnClickListener()
							{

								@Override
								public void onClick(View v)
									{
										if (mList.get(position) != null && mList.get(position).getUserName() != null)
											{
												AppUtils.replaceFragment(new FragmentUser(mList.get(position).getUserName()), mActivity.getSupportFragmentManager(), R.id.amMainLayout);
											}
									}
							});
					}
				else
					ivUser.setVisibility(View.INVISIBLE);
				return convertView;

			}

		public void setListViewHeight(GridView listView)
			{
				if (getCount() == 0 || listView == null)
					return;
				LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View convertView = inflater.inflate(R.layout.post_, null);
				int totalHeight = 0;
				for (int i = 0; i < (getCount() + 1) / 2; i++)
					{
						View listItem = getView(i, convertView, listView);
						if (listItem instanceof ViewGroup)
							{
								listItem.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
							}
						listItem.measure(0, 0);
						totalHeight += listItem.getMeasuredHeight();
					}
				ViewGroup.LayoutParams params = listView.getLayoutParams();
				params.height = totalHeight;// + (listView.getDividerHeight() * (getCount() - 1));
				listView.setLayoutParams(params);
			}
	}
