package com.clecs.gcm;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class MainActivity extends Activity
{
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	
	// Androit One
	//String SENDER_ID = "1077946359951";
	
	// Clecs One
	String SENDER_ID = "191478196332";
	
	
	static final String TAG = "GCMDemo";

	TextView mDisplay;
	GoogleCloudMessaging gcm;
	AtomicInteger msgId = new AtomicInteger();
	SharedPreferences prefs;
	Context context;

	String regid;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_main);
			mDisplay = (EditText) findViewById(R.id.tvDisplay);
			context = this;
			if (checkPlayServices())
				{
					gcm = GoogleCloudMessaging.getInstance(this);
					regid = getRegistrationId(context);

					if (regid.isEmpty())
						{
							registerInBackground();
						}
				}
			else
				{
					Log.i(TAG, "No valid Google Play Services APK found.");
				}
		}

	public void onClick(final View view)
		{
			if (view == findViewById(R.id.send))
				{
					new AsyncTask<String, String, String>()
						{
							@Override
							protected String doInBackground(String... params)
								{
									String msg = "";
									try
										{
											Bundle data = new Bundle();
											data.putString("my_message", "Hello World");
											data.putString("my_action", "com.google.android.gcm.demo.app.ECHO_NOW");
											String id = Integer.toString(msgId.incrementAndGet());
											gcm.send(SENDER_ID + "@gcm.googleapis.com", id, data);
											msg = "Sent message";
										}
									catch (IOException ex)
										{
											msg = "Error :" + ex.getMessage();
										}
									return msg;
								}

							@Override
							protected void onPostExecute(String msg)
								{
									mDisplay.append(msg + "\n");
								}
						}.execute(null, null, null);
				}
			else if (view == findViewById(R.id.clear))
				{
					mDisplay.setText("");
				}
		}

	protected void onResume()
		{
			super.onResume();
			checkPlayServices();
		}

	@SuppressLint("NewApi")
	private String getRegistrationId(Context context)
		{
			final SharedPreferences prefs = getGCMPreferences(context);
			String registrationId = prefs.getString(PROPERTY_REG_ID, "");
			if (registrationId.isEmpty())
				{
					Log.i(TAG, "Registration not found.");
					return "";
				}
			int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
			int currentVersion = getAppVersion(context);
			if (registeredVersion != currentVersion)
				{
					Log.i(TAG, "App version changed.");
					return "";
				}
			return registrationId;
		}

	private SharedPreferences getGCMPreferences(Context context)
		{
			// This sample app persists the registration ID in shared preferences, but
			// how you store the registration ID in your app is up to you.
			return getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
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
									regid = gcm.register(SENDER_ID);
									msg = "Device registered, registration ID=" + regid;

									// You should send the registration ID to your server over HTTP,
									// so it can use GCM/HTTP or CCS to send messages to your app.
									// The request to your server should be authenticated if your app
									// is using accounts.
									sendRegistrationIdToBackend();

									// For this demo: we don't need to send it because the device
									// will send upstream messages to a server that echo back the
									// message using the 'from' address in the message.

									// Persist the registration ID - no need to register again.
									storeRegistrationId(context, regid);
								}
							catch (IOException ex)
								{
									msg = "Error :" + ex.getMessage();
									// If there is an error, don't just keep trying to register.
									// Require the user to click a button again, or perform
									// exponential back-off.
								}
							return msg;
						}

					@Override
					protected void onPostExecute(String msg)
						{
							mDisplay.append(msg + "\n");
						}
				}.execute(null, null, null);
		}

	private void sendRegistrationIdToBackend()
		{
			// Your implementation here.
		}

	private void storeRegistrationId(Context context, String regId)
		{
			final SharedPreferences prefs = getGCMPreferences(context);
			int appVersion = getAppVersion(context);
			Log.i(TAG, "Saving regId on app version " + appVersion);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString(PROPERTY_REG_ID, regId);
			editor.putInt(PROPERTY_APP_VERSION, appVersion);
			editor.commit();
		}

	private boolean checkPlayServices()
		{
			// int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
			// if (resultCode != ConnectionResult.SUCCESS)
			// {
			// if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
			// {
			// GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
			// }
			// else
			// {
			// Log.i(TAG, "This device is not supported.");
			// finish();
			// }
			// return false;
			// }
			return true;
		}
}
