package com.clecs;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.clecs.utils.AppPref;
import com.clecs.utils.AppUtils;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class SplashActivity extends Activity
	{
		private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
		public static final String EXTRA_MESSAGE = "message";
		private static final String PROPERTY_APP_VERSION = "appVersion";
		
		// Androit One
		//String SENDER_ID = "1077946359951";
		
		// Clecs One
		String SENDER_ID = "191478196332";
		
		
		static final String TAG = "GCMDemo";

		GoogleCloudMessaging gcm;
		AtomicInteger msgId = new AtomicInteger();
		Context context;
		String regId;

		@Override
		protected void onCreate(Bundle savedInstanceState)
			{
				super.onCreate(savedInstanceState);
				setContentView(R.layout.activity_splash);
				//new AppPref(this);
				gcm = GoogleCloudMessaging.getInstance(this);
				regId = getRegistrationId(context);

				if (regId.isEmpty())
					{
						registerInBackground();
					}

				else
					{
						Log.i(TAG, "No valid Google Play Services APK found.");
					}
				startLoginActivity();
			}

		void startLoginActivity()
			{
				new Handler().postDelayed(new Runnable()
					{

						@Override
						public void run()
							{
								startActivity(new Intent(SplashActivity.this, LoginActivity.class));
								finish();
							}
					}, 1500);
			}

		private String getRegistrationId(Context context)
			{
				String registrationId = AppPref.getInstance().getRegId();
				if (registrationId.isEmpty())
					{
						Log.i(TAG, "Registration not found.");
						return "";
					}
				// int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
				// int currentVersion = getAppVersion(context);
				// if (registeredVersion != currentVersion)
				// {
				// Log.i(TAG, "App version changed.");
				// return "";
				// } ///Test to commit
				return registrationId;
			}

		private static int getAppVersion(Context context)
			{
				try
					{
						PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
						return packageInfo.versionCode;
					}
				catch (NameNotFoundException e)
					{
						// should never happen
						throw new RuntimeException("Could not get package name: " + e);
					}
			}

		private void registerInBackground()
			{
				new AsyncTask<String, String, String>()
					{
						@Override
						protected String doInBackground(String... params)
							{
								String msg = "";
								try
									{
										if (gcm == null)
											{
												gcm = GoogleCloudMessaging.getInstance(context);
											}
										regId = gcm.register(SENDER_ID);
										msg = "Device registered, registration ID=" + regId;

										// You should send the registration ID to your server over HTTP,
										// so it can use GCM/HTTP or CCS to send messages to your app.
										// The request to your server should be authenticated if your app
										// is using accounts.
										sendRegistrationIdToBackend();

										// For this demo: we don't need to send it because the device
										// will send upstream messages to a server that echo back the
										// message using the 'from' address in the message.

										// Persist the registration ID - no need to register again.
										// storeRegistrationId(context, regid);
										if (regId.length() > 0)
											AppPref.getInstance().setRegId(regId);

										// startLoginActivity();
									}
								catch (IOException ex)
									{
										msg = "Error :" + ex.getMessage();
										// If there is an error, don't just keep trying to register.
										// Require the user to click a button again, or perform
										// exponential back-off.
										AppUtils.showToast("Device not registered");
									}
								return msg;
							}

						@Override
						protected void onPostExecute(String msg)
							{
								// mDisplay.append(msg + "\n");
							}
					}.execute(null, null, null);
			}

		private void sendRegistrationIdToBackend()
			{
				// Your implementation here.
			}

		// private void storeRegistrationId(Context context, String regId)
		// {
		// final SharedPreferences prefs = getGCMPreferences(context);
		// int appVersion = getAppVersion(context);
		// Log.i(TAG, "Saving regId on app version " + appVersion);
		// SharedPreferences.Editor editor = prefs.edit();
		// editor.putString(PROPERTY_REG_ID, regId);
		// editor.putInt(PROPERTY_APP_VERSION, appVersion);
		// editor.commit();
		// }
	}
