package com.clecs.gcm;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.clecs.MainActivity;
import com.clecs.R;
import com.clecs.utils.Session;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService
	{
		public static final int NOTIFICATION_ID = 1;
		private NotificationManager mNotificationManager;
		NotificationCompat.Builder builder;

		private static final String TAG = "GCM Intenet Service";

		public GcmIntentService( String name )
			{
				super(name);
			}

		public GcmIntentService()
			{
				super("GCM test");
			}

		@Override
		protected void onHandleIntent(Intent intent)
			{
				Bundle extras = intent.getExtras();
				GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
				// The getMessageType() intent parameter must be the intent you received
				// in your BroadcastReceiver.
				String messageType = gcm.getMessageType(intent);

				if (!extras.isEmpty())
					{ // has effect of unparcelling Bundle
						/*
						 * Filter messages based on message type. Since it is likely that GCM
						 * will be extended in the future with new message types, just ignore
						 * any message types you're not interested in, or that you don't
						 * recognize.
						 */
						if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType))
							{
								sendNotification(extras);
							}
						else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType))
							{
								sendNotification(extras);
								// If it's a regular GCM message, do some work.
							}
						else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType))
							{
								// This loop represents the service doing some work.
								// for (int i = 0; i < 5; i++)
								// {
								// Log.i(TAG, "Working... " + (i + 1) + "/5 @ " + SystemClock.elapsedRealtime());
								// try
								// {
								// Thread.sleep(5000);
								// }
								// catch (InterruptedException e)
								// {
								// }
								// }
								Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
								// Post notification of received message.
								sendNotification(extras);
								Log.i(TAG, "Received: " + extras.toString());
							}
					}
				// Release the wake lock provided by the WakefulBroadcastReceiver.
				GcmBroadcastReceiver.completeWakefulIntent(intent);
			}

		private void sendNotification(Bundle extras)
			{
				// Received: Bundle[{postId=70634, from=1077946359951, alert=New Emoticon from @TheGirl, badge=54, sound=sound.caf, notificationId=71406, android.support.content.wakelockid=1, collapse_key=do_not_collapse}]
				PushNotification notification = new PushNotification();
				if (extras.containsKey("postId"))
					notification.setPostId(extras.getString("postId"));
				if (extras.containsKey("username"))
					notification.setUserName(extras.getString("username"));
				if (extras.containsKey("from"))
					notification.setFrom(extras.getString("from"));
				if (extras.containsKey("alert"))
					notification.setAlert(extras.getString("alert"));
				if (extras.containsKey("notificationId"))
					notification.setNotificationId(extras.getString("notificationId"));
				if (extras.containsKey("badge"))
					notification.setBadge(extras.getString("badge"));
				
				notification.setId(Session.getID());

				// PushNotification notification = new Gson().fromJson(msg, PushNotification.class);
				// PushNotification notification = new PushNotification();

				


				if (MainActivity.isRunning)
					MainActivity.mActivity.showDialog(notification);
				else
					{
						mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
						
						Intent resultIntent = new Intent(this, MainActivity.class);
						resultIntent.putExtra("notification", notification);
						resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  
							    Intent.FLAG_ACTIVITY_NEW_TASK);
						// Because clicking the notification opens a new ("special") activity, there's
						// no need to create an artificial back stack.
						PendingIntent contentIntent =
						    PendingIntent.getActivity(
						    this,
						    Integer.parseInt(notification.getNotificationId()),
						    resultIntent,
						    0
						);
						
						//PendingIntent contentIntent = PendingIntent.getActivity(this, (int) (Math.random() * 100), new Intent(this, MainActivity.class).putExtra("notification", notification), 0);

						builder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_stat_gcm).setContentTitle("Clecs").setStyle(new NotificationCompat.BigTextStyle().bigText(notification.getAlert()))
								.setContentText(notification.getAlert()).setAutoCancel(true);
						 builder.setContentIntent(contentIntent);
						 builder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 }); 
					     builder.setLights(Color.RED, 3000, 3000); 
					     builder.setDefaults(Notification.DEFAULT_ALL);
						 
						mNotificationManager.notify(Integer.parseInt(notification.getNotificationId()), builder.build());
					}

			}
		void showDialog(final Activity act, final PushNotification notification)
			{
				act.runOnUiThread(
				new Runnable()
					{
						public void run()
							{
								AlertDialog.Builder builder = new AlertDialog.Builder(act);
								AlertDialog dialog = builder.create();
								builder.setMessage(notification.getAlert());
								builder.setTitle(R.string.app_name);
								
								// SHOW
								builder.setPositiveButton("Dangos", new DialogInterface.OnClickListener()
									{

										@Override
										public void onClick(DialogInterface dialog, int which)
											{
												// AppPref.setTokenExpireTime(0);
												
											}
									});
								// cancel
								builder.setNegativeButton("Canslo", new DialogInterface.OnClickListener()
									{

										@Override
										public void onClick(DialogInterface dialog, int which)
											{
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
		boolean isAppForeGround()
			{
				try
					{
						ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
						// The first in the list of RunningTasks is always the foreground task.
						RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
						// Thats it, then you can easily access details of the foreground app/activity:

						String foregroundTaskPackageName = foregroundTaskInfo.topActivity.getPackageName();
						PackageManager pm = getPackageManager();
						PackageInfo foregroundAppPackageInfo = pm.getPackageInfo(foregroundTaskPackageName, 0);
						String foregroundTaskAppName = foregroundAppPackageInfo.applicationInfo.loadLabel(pm).toString();

						return foregroundTaskPackageName.equals(getApplication().getPackageName());
					}
				catch (SecurityException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				catch (NameNotFoundException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				return false;
			}
	}
