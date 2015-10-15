package com.clecs;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.clecs.adapters.NavDrawerListAdapter;
import com.clecs.fragments.FragmentEditProfile;
import com.clecs.fragments.FragmentFollower;
import com.clecs.fragments.FragmentFollowing;
import com.clecs.fragments.FragmentNewPost;
import com.clecs.fragments.FragmentNotification;
import com.clecs.fragments.FragmentPostDetail;
import com.clecs.fragments.FragmentPostsMain;
import com.clecs.fragments.FragmentSearch;
import com.clecs.fragments.FragmentSuggestions;
import com.clecs.fragments.FragmentUser;
import com.clecs.fragments.FragmentWebview;
import com.clecs.gcm.PushNotification;
import com.clecs.network.RequestHandler;
import com.clecs.network.RequestHandler.ServerResponseListner;
import com.clecs.objects.AppNotification;
import com.clecs.objects.MyProfileInfo;
import com.clecs.objects.NavDrawerItem;
import com.clecs.utils.AppPref;
import com.clecs.utils.AppStatics;
import com.clecs.utils.AppUtils;
import com.clecs.utils.Session;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class MainActivity extends FragmentActivity implements ServerResponseListner {
	private static final int REQ_CODE_USER_INFO = 100;
	private static final int REQ_CODE_UNREGISTER = 300;
	private static final int REQ_CODE_LOGOUT = 400;

	public static boolean isRunning;
	TextView tvPostNew;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	// private ActionBarDrawerToggle mDrawerToggle;
	static TextView tvNotif;

	// nav drawer title
	// private CharSequence mDrawerTitle;

	// used to store app title
	// private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	NavDrawerListAdapter adapter;

	TextView tvLogout;
	public static MainActivity mActivity;
	Button btnSave;

	@Override
	protected void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.activity_main);
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		AppUtils.setFont(this, (ViewGroup) (inflater.inflate(R.layout.activity_main, null)));
		AppPref.getInstance();
		mActivity = this;
		tvLogout = (TextView) findViewById(R.id.drawerTvLogout);
		tvLogout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				logout();
			}
		});
		initDrawer();
		setActionBar();
		getUserInfo();
		Fragment fragment = LoginActivity.imgShare == null ? new FragmentPostsMain() : new FragmentNewPost();
		AppUtils.replaceFragment(fragment, getSupportFragmentManager(), R.id.amMainLayout, true);

		tvPostNew = (TextView) findViewById(R.id.amTvPostNew);
		tvPostNew.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AppUtils.replaceFragment(new FragmentNewPost(),
						getSupportFragmentManager(), R.id.amMainLayout, true);
			}
		});
		getSupportFragmentManager().addOnBackStackChangedListener(
				new FragmentManager.OnBackStackChangedListener() {

					@Override
					public void onBackStackChanged() {
						int count = getSupportFragmentManager()
								.getBackStackEntryCount();
						if (count == 0) {
							finish();
							return;
						}
						int index = count - 1;
						String backClassName = (getSupportFragmentManager()
								.getBackStackEntryAt(index).getName());
						if (backClassName != null) {
							if (backClassName.endsWith(FragmentNewPost.class
									.getName())
									|| backClassName
											.endsWith(FragmentPostDetail.class
													.getName())
									|| backClassName
											.endsWith(FragmentUser.class
													.getName())
									|| backClassName
											.endsWith(FragmentWebview.class
													.getName()))
								hideFooter();
							else
								showFooter();
							if (backClassName
									.endsWith(FragmentEditProfile.class
											.getName())) {
								if (btnSave != null) {
									btnSave.setVisibility(View.VISIBLE);
									tvNotif.setVisibility(View.GONE);
								}
							} else if (btnSave != null) {
								btnSave.setVisibility(View.GONE);
								tvNotif.setVisibility(View.VISIBLE);
							}
							// if(backClassName.equalsIgnoreCase(FragmentPostsMain.class.getName()))
						}
					}
				});
		// mTitle = getTitle().toString();
		// Set up the drawer.
		tvNotif.setText("");
		updateNotification();
		if (getIntent().getExtras() != null)
			if (getIntent().getExtras().containsKey("notification"))
				openNotificationDetail((PushNotification) getIntent()
						.getExtras().get("notification"));
	}
	
	public Fragment getActiveFragment() {
	    if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
	        return null;
	    }
	    String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
	    return (Fragment) getSupportFragmentManager().findFragmentByTag(tag);
	}

	void setActionBar() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View actionBarLayout = inflater.inflate(R.layout.action_bar, null);
		ImageView ivMenu = (ImageView) actionBarLayout
				.findViewById(R.id.abIvMenu);
		ImageView ivLogo = (ImageView) actionBarLayout
				.findViewById(R.id.abIvLogo);
		View vNotfi = actionBarLayout.findViewById(R.id.abllNotifHolder);
		btnSave = (Button) actionBarLayout.findViewById(R.id.abBtnSave);
		tvNotif = (TextView) actionBarLayout.findViewById(R.id.abTvNotif);

		ivMenu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mDrawerLayout.isDrawerOpen(findViewById(R.id.rlDrawerMain)))
					mDrawerLayout.closeDrawer(findViewById(R.id.rlDrawerMain));
				else {
					mDrawerLayout.openDrawer(findViewById(R.id.rlDrawerMain));
				}
			}
		});
		tvNotif.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String backClassName = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
				if (backClassName != null && !backClassName.endsWith(FragmentNotification.class.getName()))
						AppUtils.replaceFragment(new FragmentNotification(), getSupportFragmentManager(), R.id.amMainLayout, false);
				
				AppUtils.closeKeyboard(MainActivity.this);
				mDrawerLayout.closeDrawer(findViewById(R.id.rlDrawerMain));
			}
		});
		ivLogo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int count = getSupportFragmentManager()
						.getBackStackEntryCount();
				String backClassName = (getSupportFragmentManager()
						.getBackStackEntryAt(count - 1).getName());
				if (backClassName != null)
					if (backClassName.equalsIgnoreCase(FragmentPostsMain.class
							.getName()))
						// FragmentPostsMain.instance.updateList();
						FragmentPostsMain.instance.reloadList();
					else
						AppUtils.replaceFragment(new FragmentPostsMain(),
								getSupportFragmentManager(), R.id.amMainLayout);
				AppUtils.closeKeyboard(MainActivity.this);
				mDrawerLayout.closeDrawer(findViewById(R.id.rlDrawerMain));
			}
		});
		vNotfi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tvNotif.performClick();
			}
		});
		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentEditProfile.btnSave.performClick();
				// getSupportFragmentManager().popBackStack();
			}
		});
		
		ActionBar actionBar = getActionBar();
		//actionBar.hide();
	    //actionBar.setDisplayShowHomeEnabled(false);
	    //actionBar.setDisplayShowCustomEnabled(true);
	    //actionBar.setDisplayShowTitleEnabled(false);
	    
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setCustomView(actionBarLayout);
		//ActionBar mActionBar = getActionBar();
		// mActionBar.setDisplayHomeAsUpEnabled(true);
		// mActionBar.setHomeButtonEnabled(true);
		//mActionBar.setCustomView(actionBarLayout);

	}

	public void updateNotification() {
		new RequestHandler(new ServerResponseListner() {

			@Override
			public void onResponse(String response, long requestCode) {
				//System.out.println("response");
				//AppNotification.setUreadNotif(Integer.parseInt(response));
				AppPref.getInstance().setNotifCount(Integer.parseInt(response));
				//tvNotif.setText(AppNotification.getUnreadNotif() == 0 ? "" : AppNotification.getUnreadNotif() + "");
				tvNotif.setText(AppPref.getInstance().getNotifCount());
				
			}

			@Override
			public void onNoInternet() {
				//AppUtils.showToast("No Network");
				AppUtils.showToast("Os gwelch yn dda gwiriwch eich cysylltiadau rhyngrwyd");
				
			}

			@Override
			public void onError(String error, String description, long requestCode) {
				// TODO Auto-generated method stub

			}
		}, true).makeGetRequest(AppStatics.URL_GET_NOTIFICATION_AMOUNT, 200);
	}

	public void showFooter() {
		// tvPostNew.setVisibility(View.VISIBLE);
		findViewById(R.id.rlMain).setVisibility(View.VISIBLE);
	}

	public void hideFooter() {
		// tvPostNew.setVisibility(View.GONE);
		findViewById(R.id.rlMain).setVisibility(View.GONE);
	}

	void initDrawer() {
		// mTitle = mDrawerTitle = getTitle();

		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		navDrawerItems = new ArrayList<NavDrawerItem>();

		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons
				.getResourceId(0, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons
				.getResourceId(1, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons
				.getResourceId(2, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons
				.getResourceId(3, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons
				.getResourceId(4, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons
				.getResourceId(5, -1)));

		// Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
		mDrawerLayout.setDrawerListener(new DrawerListener() {

			@Override
			public void onDrawerStateChanged(int arg0) {

				// adapter.notifyDataSetChanged();
				//AppUtils.closeKeyboard(MainActivity.this);
			}

			@Override
			public void onDrawerSlide(View arg0, float arg1) {
				// TODO Auto-generated method stub
				// findViewById(R.id.amMainLayout).animate().x(arg1 *
				// 500).setDuration(100).start();

			}

			@Override
			public void onDrawerOpened(View arg0) {
				// mDrawerList.setAdapter(adapter);
				if (FragmentEditProfile.isProfilePicUpdated) {
					adapter.notifyDataSetChanged();
					FragmentEditProfile.isProfilePicUpdated = false;
				}
				AppUtils.closeKeyboard(MainActivity.this);
			}

			@Override
			public void onDrawerClosed(View arg0) {

			}
		});
		mDrawerLayout.setOnDragListener(new OnDragListener() {

			@Override
			public boolean onDrag(View v, DragEvent event) {
				//AppUtils.closeKeyboard(mActivity, new EditText(MainActivity.this));
				return false;
			}
		});

		adapter = new NavDrawerListAdapter(this, navDrawerItems);
		mDrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button
		// getActionBar().setDisplayHomeAsUpEnabled(true);
		// getActionBar().setHomeButtonEnabled(true);

		// mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
		// R.drawable.ic_drawer, R.string.app_name, R.string.app_name)
		// {
		// public void onDrawerClosed(View view)
		// {
		// getActionBar().setTitle(mTitle);
		// invalidateOptionsMenu();
		//
		// }
		//
		// public void onDrawerOpened(View drawerView)
		// {
		// getActionBar().setTitle(mDrawerTitle);
		// // calling onPrepareOptionsMenu() to hide action bar icons
		// invalidateOptionsMenu();
		// AppUtils.closeKeyboard(mActivity, new EditText(MainActivity.this));
		// }
		// };
		// mDrawerLayout.setDrawerListener(mDrawerToggle);

		// if (savedInstanceState == null)
		// {
		// // on first time display view for first nav item
		// displayView(0);
		// }
	}

	public void notifyDrawer() {
		if (adapter != null)
			synchronized (adapter) {
				adapter.notifyDataSetChanged();
			}
	}

	@Override
	protected void onResume() {
		notifyDrawer();
		isRunning = true;
		super.onResume();
	}

	@Override
	protected void onStop() {
		isRunning = false;
		super.onStop();
	}

	public void logout() {
		showConfirmLogout();
	}

	void showConfirmLogout() {
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		AlertDialog dialog = builder.create();
		
		//builder.setMessage("Are you sure you want to logout?");
		//builder.setTitle("Clecs Confirmation");
		builder.setMessage("A ydych yn sicr eich bod am allgofnodi?");
		builder.setTitle("Clecs");
		
		// YES
		builder.setPositiveButton("Iawn",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						RequestHandler handler = new RequestHandler(
								MainActivity.this, true);
						try {
							JSONObject obj = new JSONObject();
							obj.put("deviceToken", AppPref.getInstance().getRegId());
							handler.makePostRequest(
									AppStatics.URL_UNREGISTER_DEVICE, obj,
									REQ_CODE_UNREGISTER);
							handler.makePostRequest(AppStatics.URL_LOGOUT,
									new JSONObject(), REQ_CODE_LOGOUT);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
		// Cancel
		builder.setNegativeButton("Canslo",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.setCancelable(true);
		dialog = builder.create();
		dialog.show();
	}

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
	}

	private void displayView(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		// Bundle b = new Bundle();
		switch (position) {
		case 0:
			String img = Session.myProfileInfo.getProfileImage();
			// post.setMyPost(true);
			String userName = Session.myProfileInfo.getUserName();
			fragment = new FragmentUser(userName, img, true);
			// b.putSerializable(FragmentUser.EXTRA_POST, post);
			break;
		case 1:
			fragment = new FragmentFollower(Session.myProfileInfo.getUserName());
			break;
		case 2:
			fragment = new FragmentFollowing(false,
					Session.myProfileInfo.getUserName());
			break;
		case 3:
			fragment = new FragmentSuggestions();
			break;
		case 4:
			fragment = new FragmentSearch();
			break;
		case 5:
			fragment = new FragmentSearch(true);
			break;

		default:
			break;
		}

		if (fragment != null) {
			// FragmentManager fragmentManager = getFragmentManager();
			// fragmentManager.beginTransaction().replace(R.id.frame_container,
			// fragment).commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(navMenuTitles[position]);
			// Toast.makeText(this, "Selected " + position,
			// Toast.LENGTH_LONG).show();
			AppUtils.replaceFragment(fragment, getSupportFragmentManager(),
					R.id.amMainLayout, false);
		}
		mDrawerLayout.closeDrawer(findViewById(R.id.rlDrawerMain));
		// else
		// {
		// // error in creating fragment
		// Log.e("MainActivity", "Error in creating fragment");
		// }
	}

	@Override
	public void setTitle(CharSequence title) {
		// mTitle = title;
		// getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		// mDrawerToggle.syncState();
	}

	// RcA7jOJgdK2YsCG9EKcsG1GPewyv4cOiYgIEFX3milbj2bqIUCbEXHQDnvCOnbjroIuG5i03dWyzgiqb5vZp6JVTqh6qAW3BOz7V_M97rU7zJ1pNTmAHsor7ghNKadtHmYRjZKMpyC7XFRXqkfQqmoluYNZAkEA2PbWlY5PZJClJPD739geEOB0RfB8iP5AGv87rqADwi3hYdaVv0w-N5coGGS2CjmwhAUD_bjggoTRSnPKO_mnZVMhieZ1f7i8CBi-YKdXB71c2VJHkrk_93rGRsys70pCbZoWkuZKg5EE8Gh4Gheir82ARwS3hwG4aI_H-yOvAYW7QzQX1lG3yWVTSniErMooLxE53XTVBpsP_DDtwXGEeXj6qexV6l7uMJH45gBPDIm9PWT1evcw5QN1LOPAvgYEsLj8FPO9xAcTyw4243XMxvOw4gIzW0TeCUg8kCz0fWvBnSi-HT6k5laFe_clDI9TEyYXSY0Qu86E
	@Override
	public void onBackPressed() {
		// int count = getSupportFragmentManager().getBackStackEntryCount();
		//
		// if (count > 2)
		// super.onBackPressed();
		// // additional code
		// else
		// {
		// // getSupportFragmentManager().popBackStack();
		// String backClassName =
		// (getSupportFragmentManager().getBackStackEntryAt(count -
		// 2)).getName();
		// Fragment fragment =
		// getSupportFragmentManager().findFragmentByTag(backClassName);
		// AppUtils.replaceFragment(fragment, getSupportFragmentManager(),
		// R.id.amMainLayout);
		// }
		super.onBackPressed();
	}

	void getUserInfo() {

		RequestHandler requestHandler = new RequestHandler(this, true);
		requestHandler.makeGetRequest(AppStatics.URL_GET_PROFILE_DETAIL,
				REQ_CODE_USER_INFO);
	}

	@Override
	public void onResponse(String response, long requestCode) {
		if (requestCode == REQ_CODE_USER_INFO) {
			try {
				MyProfileInfo myProfileInfo = new Gson().fromJson(response,
						MyProfileInfo.class);
				Session.myProfileInfo = myProfileInfo;
				MainActivity.mActivity.notifyDrawer();
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			}
			// } else if (requestCode == REQ_CODE_UNREGISTER) {
			// handler.makePostRequest(AppStatics.URL_UNREGISTER_DEVICE, obj,
			// 1);
			// RequestHandler handler = new RequestHandler(this);
			// handler.makePostRequest(AppStatics.URL_LOGOUT, new JSONObject(),
			// REQ_CODE_LOGOUT);
		} else if (requestCode == REQ_CODE_LOGOUT) {
			AppPref.getInstance().setTokenExpireTime(0);
			AppPref.getInstance().clearPref();
			Session.clearSession();
			startActivity(new Intent(mActivity, LoginActivity.class));
			finish();
		}

	}

	@Override
	public void onError(String error, String description, long requestCode) {
		if (requestCode == REQ_CODE_LOGOUT) {
			//AppUtils.showToast("No Network, unable to logout");
			
			//There was a problem connecting to the internet   
			AppUtils.showToast("Roedd problem cysylltu a'r rhyngrwyd");
		}
	}

	@Override
	public void onNoInternet() {
		//AppUtils.showToast("No Network");
		AppUtils.showToast("Roedd problem cysylltu a'r rhyngrwyd");
	}

	public void showDialog(final PushNotification notification) {
		updateNotification();
		
		runOnUiThread(new Runnable() {
			public void run() {
				AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
				AlertDialog dialog = builder.create();
				builder.setMessage(notification.getAlert());
				builder.setTitle(R.string.app_name);
				// VIEW
				builder.setPositiveButton("Golwg",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// AppPref.setTokenExpireTime(0);
								openNotificationDetail(notification);

							}
						});
				// CANCEL
				builder.setNegativeButton("Canslo",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				builder.setCancelable(true);
				dialog = builder.create();
				dialog.show();

				// mBuilder.setContentIntent(contentIntent);
			}
		});
	}

	void openNotificationDetail(PushNotification notification) {
		NotificationManager mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(Integer.parseInt(notification
				.getNotificationId()));
		if (notification.hasUserName()) {
			AppUtils.replaceFragment(
					new FragmentUser(notification.getUserName()),
					getSupportFragmentManager(), R.id.amMainLayout);
			sendNotifRead(notification.getNotificationId());
		} else if (notification.hasPostId()) {
			AppUtils.replaceFragment(
					new FragmentPostDetail(Long.parseLong(notification
							.getPostId())), getSupportFragmentManager(),
					R.id.amMainLayout);
			sendNotifRead(notification.getNotificationId());
		}
	}
	
	public void cancelAllPush(){
		NotificationManager mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancelAll();
	}

	void sendNotifRead(String notifId) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("id", notifId);
			RequestHandler handler = new RequestHandler(this);
			handler.makePostRequest(AppStatics.URL_NOTIFICATION_READ, obj,
					Long.parseLong(notifId));
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
}
