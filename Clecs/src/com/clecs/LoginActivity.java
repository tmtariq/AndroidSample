package com.clecs;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.clecs.fragments.FragmentLogin;
import com.clecs.network.RequestHandler;
import com.clecs.network.RequestHandler.ServerResponseListner;
import com.clecs.objects.MyProfileInfo;
import com.clecs.utils.App;
import com.clecs.utils.AppPref;
import com.clecs.utils.AppStatics;
import com.clecs.utils.AppUtils;
import com.clecs.utils.Session;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class LoginActivity extends FragmentActivity implements ServerResponseListner
	{
		// public static boolean isFromShare;
		public static Uri imgShare = null;

		@Override
		protected void onCreate(Bundle b)
			{
				super.onCreate(b);
				requestWindowFeature(Window.FEATURE_NO_TITLE);
				setContentView(R.layout.activity_login);
				imgShare = null;
				//App app = new App();
				App.getInstance().setContext(this);
				//new AppPref(this);

				// CHECK IF IT'S CALLED FROM SHARE MENU
				Intent intent = getIntent();
				Bundle extras = intent.getExtras();
				String action = intent.getAction();
				// if this is from the share menu
				if (Intent.ACTION_SEND.equals(action))
					{
						if (extras.containsKey(Intent.EXTRA_STREAM))
							{
								// Get resource path
								imgShare = (Uri) extras.get(Intent.EXTRA_STREAM);
							}
					}
				// setActionBar();
				if (AppPref.getInstance().isTokenValid())
					{
						// GET USER INFO
						new RequestHandler(this, true).makeGetRequest(AppStatics.URL_GET_PROFILE_DETAIL, 100);
						startActivity(new Intent(this, MainActivity.class));
						finish();
					}
				else
					{
						FragmentLogin fragmentLogin = new FragmentLogin();
						AppUtils.replaceFragment(fragmentLogin, getSupportFragmentManager(), R.id.alMainLayout);
					}

			}

		@Override
		public void onResponse(String response, long requestCode)
			{
				try
					{
						MyProfileInfo profileInfo = new Gson().fromJson(response, MyProfileInfo.class);
						AppPref.getInstance().setMyProfileInfo(response);
						Session.myProfileInfo = profileInfo;
					}
				catch (JsonSyntaxException e)
					{
						e.printStackTrace();
					}
			}

		@Override
		public void onError(String error, String description, long requestCode)
			{

			}

		@Override
		public void onNoInternet()
			{

			}

		@SuppressLint("InflateParams")
		void setActionBar()
			{
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View actionBarLayout = inflater.inflate(R.layout.action_bar, null);
				ImageView ivMenu = (ImageView) actionBarLayout.findViewById(R.id.abIvMenu);
				ImageView ivLogo = (ImageView) actionBarLayout.findViewById(R.id.abIvLogo);
				TextView tvNotif = (TextView) actionBarLayout.findViewById(R.id.abTvNotif);
				ivMenu.setVisibility(View.INVISIBLE);
				tvNotif.setVisibility(View.INVISIBLE);

				getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
				ActionBar mActionBar = getActionBar();
				mActionBar.setCustomView(actionBarLayout);
			}
	}
