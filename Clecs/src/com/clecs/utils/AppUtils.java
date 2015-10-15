package com.clecs.utils;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.clecs.fragments.FragmentEditProfile;
import com.clecs.fragments.FragmentPostsMain;
import com.clecs.network.RequestHandler;
import com.clecs.network.RequestHandler.ServerResponseListner;

public class AppUtils {
	public static boolean isPopped;

	ArrayList<Contacts> getContactList(Context context) {
		ContentResolver cr = context.getContentResolver();
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
				null, null, null);
		ArrayList<Contacts> contactList = new ArrayList<Contacts>();

		if (cur.getCount() > 0) {
			while (cur.moveToNext()) {
				String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
				String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

				if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
					Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[] { id }, null);
					while (pCur.moveToNext()) {
						String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						Toast.makeText(context,
								"Name: " + name + ", Phone No: " + phoneNo,
								Toast.LENGTH_SHORT).show();
					}
					pCur.close();
				}
			}
		}
		return contactList;
	}

	public static final boolean isInternetConeected(Context context) {
		boolean status = false;
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			// NetworkInfo[] info = connectivity.getAllNetworkInfo();
			// if (info != null)
			// for (int i = 0; i < info.length; i++)
			// if (info[i].getState() == NetworkInfo.State.CONNECTED)
			// {
			// status = true;
			// }
			NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();
			return activeNetwork != null && activeNetwork.isConnected();
		}
		return status;
	}

	public static CharSequence highlight(String search, String originalText) {
		// ignore case and accents
		// the same thing should have been done for the search text
		String normalizedText = Normalizer
				.normalize(originalText, Normalizer.Form.NFD)
				.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
				.toLowerCase();

		int start = normalizedText.indexOf(search);
		if (start < 0) {
			// not found, nothing to to
			return originalText;
		} else {
			// highlight each appearance in the original text
			// while searching in normalized text
			Spannable highlighted = new SpannableString(originalText);
			while (start >= 0) {
				int spanStart = Math.min(start, originalText.length());
				int spanEnd = Math.min(start + search.length(),
						originalText.length());

				highlighted.setSpan(
						new BackgroundColorSpan(Color.parseColor("#FF00AA")),
						spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

				start = normalizedText.indexOf(search, spanEnd);
			}

			return highlighted;
		}
	}

	// NOTIFICATION READ
	void notificationRead(int id, ServerResponseListner listner, int requestCode) {
		JSONObject object = new JSONObject();
		try {
			object.put("Id", id);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		new RequestHandler(listner).makePostRequest(
				AppStatics.URL_NOTIFICATION_READ, object, requestCode);
	}

	public static void replaceFragment(Fragment fragment,
			FragmentManager manager, int containerId, boolean shouldPop) {
		replaceFragment(fragment, manager, containerId);
	}

	public static void replaceFragment(Fragment fragment,
			FragmentManager manager, int containerId) {
		String backStateName = fragment.getClass().getName();
		if (!backStateName.equalsIgnoreCase(FragmentPostsMain.class.getName())
				&& !backStateName.equalsIgnoreCase(FragmentEditProfile.class
						.getName()))
			backStateName = System.currentTimeMillis() + backStateName;

		// if (tag != null)
		// backStateName = tag;
		String fragmentTag = backStateName;

		// shouldPop = false;
		// MANAGING PROFILE DETAIL, CREATING NEW INSTANCE
		boolean fragmentPopped = false;
		// if (!shouldPop)
		// {
		// manager.beginTransaction().remove(fragment).commit();
		// }
		// else
		fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

		if (!fragmentPopped)// && manager.findFragmentByTag(fragmentTag) ==
							// null)
		{ // fragment not in back stack, create it.
			FragmentTransaction ft = manager.beginTransaction();
			// ft.setCustomAnimations(R.anim.pop_enter, R.anim.pop_exit);
			ft.replace(containerId, fragment, fragmentTag);
			// ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.addToBackStack(backStateName);
			ft.commit();
		}
		isPopped = fragmentPopped;
	}

	// public static void replaceFragment(Fragment fragment, FragmentManager
	// manager, int containerId)
	// {
	// String backStateName = System.currentTimeMillis() + "";//
	// fragment.getClass().getName();
	// String fragmentTag = backStateName;
	//
	// // MANAGING PROFILE DETAIL, CREATING NEW INSTANCE
	// boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);
	//
	// if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null)
	// { // fragment not in back stack, create it.
	// FragmentTransaction ft = manager.beginTransaction();
	// // ft.setCustomAnimations(R.anim.pop_enter, R.anim.pop_exit);
	// ft.replace(containerId, fragment, fragmentTag);
	// // ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
	// ft.addToBackStack(backStateName);
	// ft.commit();
	// }
	// isPopped = fragmentPopped;
	// }

	public static boolean isInternetConnected(Context ctx) {
		ConnectivityManager cm = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();

		return netInfo != null && netInfo.isConnected();
	}

	public static void enableControls(boolean enable, View v) {
		if (v instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup) v;
			for (int i = 0; i < vg.getChildCount(); i++) {
				View child = vg.getChildAt(i);
				child.setEnabled(enable);
				if (child instanceof ViewGroup) {
					enableControls(enable, (ViewGroup) child);
				}
			}
		}
	}

	public final static boolean isValidEmail(CharSequence target) {
		return TextUtils.isEmpty(target) ? false
				: android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
	}

	public static void showToast(String msg) {
		Toast.makeText(App.getInstance().getContext(), msg, Toast.LENGTH_SHORT).show();
	}

	public static void showToast(int stringId) {
		String msg = App.getInstance().getContext().getString(stringId);
		showToast(msg);
	}

	public static ArrayList<NameValuePair> getPairListFromJSON(JSONObject object) {
		ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
		Iterator<String> iter = object.keys();
		while (iter.hasNext()) {
			String key = iter.next();
			try {
				String value = (String) object.get(key);
				list.add(new BasicNameValuePair(key, value));
			} catch (JSONException e) {
			}
		}
		return list;
	}

	public static void openKeyboard(Context mContext) {
		((InputMethodManager) mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE))
				.toggleSoftInput(InputMethodManager.SHOW_FORCED,
						InputMethodManager.HIDE_IMPLICIT_ONLY);
	}

	public static void closeKeyboard(Context mContext, EditText et) {
		((InputMethodManager) mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(et.getWindowToken(), 0);

	}

	public static void closeKeyboard(Activity activity) {
		InputMethodManager inputMethodManager = (InputMethodManager) activity
				.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus()
				.getWindowToken(), 0);
	}

	/*
	 * public static void setListViewHeightBasedOnChildren(ListView listView) {
	 * ListAdapter listAdapter = listView.getAdapter(); if (listAdapter == null)
	 * { // pre-condition return; }
	 * 
	 * int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
	 * for (int i = 0; i < listAdapter.getCount(); i++) { try { View listItem =
	 * listAdapter.getView(i, null, listView); if (listItem instanceof
	 * ViewGroup) { listItem.setLayoutParams(new
	 * LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)); }
	 * listItem.measure(0, 0); totalHeight += listItem.getMeasuredHeight(); }
	 * catch (Exception e) { e.printStackTrace(); } }
	 * 
	 * ViewGroup.LayoutParams params = listView.getLayoutParams(); params.height
	 * = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() -
	 * 1)); listView.setLayoutParams(params); }
	 * 
	 * public static void setListViewHeightBasedOnChildren(ListView listView) {
	 * ListAdapter listAdapter = listView.getAdapter(); if (listAdapter == null)
	 * { // pre-condition return; }
	 * 
	 * int totalHeight = 0; int i; for (i = 0; i < listAdapter.getCount(); i++)
	 * { View listItem = listAdapter.getView(i, null, listView); //now it will
	 * not throw a NPE if listItem is a ViewGroup instance if (listItem
	 * instanceof ViewGroup) { listItem.setLayoutParams(new LayoutParams(
	 * LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)); }
	 * listItem.measure(0, 0); totalHeight += listItem.getMeasuredHeight();
	 * 
	 * System.out.print("getScrollBarSize: " + listView.getScrollBarSize() +
	 * " getMeasuredHeight: " + listItem.getMeasuredHeight()); } totalHeight +=
	 * listView.getDividerHeight() * i; System.out.print("getScrollBarSize: " +
	 * listView.getScrollBarSize());
	 * 
	 * ViewGroup.LayoutParams params = listView.getLayoutParams(); params.height
	 * = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() -
	 * 1)); listView.setLayoutParams(params); listView.requestLayout(); }
	 */

	public static void setFont(Context context, ViewGroup vg) {
		final String FONT_NAME_BOLD = "robot_bold.ttf";
		final String FONT_NAME_REGULAR = "robot_regular.ttf";

		for (int i = 0; i < vg.getChildCount(); i++) {
			View v = vg.getChildAt(i);
			if (v instanceof ViewGroup)
				setFont(context, (ViewGroup) v);
			else if (v instanceof TextView) {
				((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), ((TextView) v).getTypeface() != null && ((TextView) v).getTypeface().isBold() ? FONT_NAME_BOLD : FONT_NAME_REGULAR));
			} else if (v instanceof EditText) {
				((EditText) v).setTypeface(Typeface.createFromAsset(context.getAssets(), ((EditText) v).getTypeface() != null && ((EditText) v).getTypeface().isBold() ? FONT_NAME_BOLD : FONT_NAME_REGULAR));
			} else if (v instanceof Button) {
				((Button) v).setTypeface(Typeface.createFromAsset(context.getAssets(), ((Button) v).getTypeface() != null && ((Button) v).getTypeface().isBold() ? FONT_NAME_BOLD : FONT_NAME_REGULAR));
			} else if (v instanceof CheckBox) {
				((CheckBox) v).setTypeface(Typeface.createFromAsset(context.getAssets(), ((CheckBox) v).getTypeface() != null && ((CheckBox) v).getTypeface().isBold() ? FONT_NAME_BOLD : FONT_NAME_REGULAR));
			} else if (v instanceof RadioButton) {
				((RadioButton) v).setTypeface(Typeface.createFromAsset(context.getAssets(), ((RadioButton) v).getTypeface() != null && ((RadioButton) v).getTypeface().isBold() ? FONT_NAME_BOLD : FONT_NAME_REGULAR));
			}
		}
	}
}
