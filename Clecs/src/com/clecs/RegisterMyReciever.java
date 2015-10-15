package com.clecs;

import java.io.IOException;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class RegisterMyReciever
	{
		private static final String PROPERTY_REG_ID = "PROPERTY_REG_ID";
		private static final String PROPERTY_APP_VERSION = "PROPERTY_APP_VERSION";
		private static final String TAG = "Reciever";
		Context mContext;
		String SENDER_ID = "";
		String DISPLAY_MESSAGE_ACTION = "";
		String EXTRA_MESSAGE = "";
		GoogleCloudMessaging gcm;
		String regid;

		public RegisterMyReciever( Context context )
			{
				mContext = context;
			}

		public void checkDeviceDependencies()
			{
				GCMRegistrar.checkDevice(mContext);
			}

		public void checkMenifestPermissions()
			{
				GCMRegistrar.checkManifest(mContext);
			}

		public void registerReciver()
			{
				mContext.registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));
			}

		public void register()
			{
				gcm = GoogleCloudMessaging.getInstance(mContext);
				regid = getRegistrationId(mContext);

			}

		private String getRegistrationId(Context context)
			{
				final SharedPreferences prefs = getGCMPreferences(context);
				String registrationId = prefs.getString(PROPERTY_REG_ID, "");
				if (registrationId.isEmpty())
					{
						Log.i(TAG, "Registration not found.");
						return "";
					}
				// Check if app was updated; if so, it must clear the registration ID
				// since the existing registration ID is not guaranteed to work with
				// the new app version.
				int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
				int currentVersion = getAppVersion(context);
				if (registeredVersion != currentVersion)
					{
						Log.i(TAG, "App version changed.");
						return "";
					}
				return registrationId;
			}

		/**
		 * @return Application's {@code SharedPreferences}.
		 */
		private SharedPreferences getGCMPreferences(Context context)
			{
				// This sample app persists the registration ID in shared preferences, but
				// how you store the registration ID in your app is up to you.
				return getSharedPreferences(mContext.getApplicationInfo().getClass().getSimpleName(), Context.MODE_PRIVATE);
			}

		private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver()
			{
				@Override
				public void onReceive(Context context, Intent intent)
					{
						System.out.println("recived message");
						String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
						// WakeLocker.acquire(mContext);
						Toast.makeText(mContext, "New Message: " + newMessage, Toast.LENGTH_LONG).show();
						// WakeLocker.release();
					}
			};

		/**
		 * @return Application's version code from the {@code PackageManager}.
		 */
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

		/**
		 * Registers the application with GCM servers asynchronously.
		 * <p>
		 * Stores the registration ID and app versionCode in the application's shared preferences.
		 */
		private void registerInBackground()
			{
				new AsyncTask()
					{
						@Override
						protected String doInBackground(Void... params)
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
										storeRegistrationId(mContext, regid);
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

		/**
		 * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
		 * or CCS to send messages to your app. Not needed for this demo since the
		 * device sends upstream messages to a server that echoes back the message
		 * using the 'from' address in the message.
		 */
		private void sendRegistrationIdToBackend()
			{
				// Your implementation here.
			}

		/**
		 * Stores the registration ID and app versionCode in the application's {@code SharedPreferences}.
		 * 
		 * @param context
		 *            application's context.
		 * @param regId
		 *            registration ID
		 */
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

		public class GcmBroadcastReceiver extends WakefulBroadcastReceiver
			{
				@Override
				public void onReceive(Context context, Intent intent)
					{
						// Explicitly specify that GcmIntentService will handle the intent.
						ComponentName comp = new ComponentName(context.getPackageName(), GcmIntentService.class.getName());
						// Start the service, keeping the device awake while it is launching.
						startWakefulService(context, (intent.setComponent(comp)));
						setResultCode(Activity.RESULT_OK);
					}
			}
	}
