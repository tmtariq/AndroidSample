package com.clecs.utils;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.clecs.objects.MyProfileInfo;
import com.clecs.objects.Post;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class AppPref {
	static SharedPreferences sp;
	static Editor editor;
	static Context context;

	static final String KEY_TOKEN = "token";
	static final String KEY_TOKEN_EXPIRE = "tokenExpire";
	static final String KEY_LAST_RESPONSE = "lastResponse";
	private static final String KEY_REG_ID = "registrationId";
	static final String KEY_NOTIF_COUNT = "notifCount";
	private static final String KEY_PROFILE = "myProfile";
	
	private volatile static AppPref instance;
	
	/** Returns singleton class instance */
	public static AppPref getInstance() {
		if (instance == null) {
			synchronized (AppPref.class) {
				if (instance == null) {
					instance = new AppPref();
				}
			}
		}
		return instance;
	}
	
	@SuppressLint("CommitPrefEdits")
	private AppPref() {
		context =  App.getInstance().getContext(); //context;// new App().getContext();
		sp = PreferenceManager.getDefaultSharedPreferences(context);
		editor = sp.edit();
	}
	
	public void clearPref() {
		String regId = getRegId();
		editor.clear().commit();
		setRegId(regId);
	}

	public String getToken() {
		return sp != null ? sp.getString(KEY_TOKEN, "") : "";
	}

	public void setToken(String token) {
		editor.putString(KEY_TOKEN, token);
		commitChanges();
	}

	public long getTokenExipreTime() {
		return sp.getLong(KEY_TOKEN_EXPIRE, 0);
	}

	public void setTokenExpireTime(long expireInDiff) {
		int seconds = 1000;
		long expireTime = Calendar.getInstance().getTimeInMillis()
				+ expireInDiff * seconds;
		editor.putLong(KEY_TOKEN_EXPIRE, expireTime);
		commitChanges();
	}

	public boolean isTokenValid() {
		return Calendar.getInstance().getTimeInMillis() < getTokenExipreTime();
	}

	public String getLastResponse() {
		return sp.getString(KEY_LAST_RESPONSE, "");
	}

	public void setLastResponse(String lastRespnse) {
		editor.putString(KEY_LAST_RESPONSE, lastRespnse);
		commitChanges();
	}

	public void setPostChanged(Post post) {
		editor.putString(post.getPostId() + "", new Gson().toJson(post));
		commitChanges();
	}

	public Post getPostCHanged(long id) {
		try {
			String postJson = sp.getString(id + "", null);
			return new Gson().fromJson(postJson, Post.class);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Post getPostCHangedAndClear(long id) {
		try {
			String postJson = sp.getString(id + "", null);
			Post post = new Gson().fromJson(postJson, Post.class);
			editor.remove(id + "").commit();
			return post;
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getHeader() {
		return "Bearer " + getToken();
	}

	public String getRegId() {
		return sp != null ? sp.getString(KEY_REG_ID, "") : "";
	}

	public void setRegId(String regId) {
		editor.putString(KEY_REG_ID, regId);
		commitChanges();
	}

	public String getNotifCount() {
		//return sp.getInt(KEY_NOTIF_COUNT, 0) == 0 ? "" : sp.getInt(KEY_NOTIF_COUNT, 0) + "";
		return sp.getInt(KEY_NOTIF_COUNT, 0) + "";
	}

	public void setNotifCount(int notifCount) {
		editor.putInt(KEY_NOTIF_COUNT, notifCount);
		commitChanges();
	}

	public void setMyProfileInfo(String profileJson) {
		editor.putString(KEY_PROFILE, profileJson);
		commitChanges();
	}

	public MyProfileInfo getMyProfileInfo() {
		MyProfileInfo profileInfo = new Gson().fromJson(
				sp.getString(KEY_PROFILE, null), MyProfileInfo.class);
		return profileInfo;
	}

	void commitChanges() {
		editor.commit();
	}
}