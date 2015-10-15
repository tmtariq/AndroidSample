package com.clecs.network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentTransaction;




import com.clecs.LoginActivity;
import com.clecs.MainActivity;
import com.clecs.R;
import com.clecs.fragments.FragmentLogin;
import com.clecs.utils.App;
import com.clecs.utils.AppPref;
import com.clecs.utils.AppUtils;
import com.clecs.utils.Session;

public class RequestHandler {
	public interface ServerResponseListner {
		public void onResponse(String response, long requestCode);

		public void onError(String error, String description, long requestCode);

		public void onNoInternet();
	}

	ServerResponseListner mListner;
	static boolean mHeaderRequired;
	static int statusCode = 0;

	public RequestHandler(ServerResponseListner listner) {
		mListner = listner;
	}

	public RequestHandler(ServerResponseListner listner,
			boolean isHeaderRequired) {
		mListner = listner;
		mHeaderRequired = isHeaderRequired;
	}

	public void makeGetRequest(String url, long requestCode) {
		if (checkPreReq())
			new NetworkHandler(url, requestCode);
	}

	public void makePostRequest(String url, JSONObject object, long requestCode) {
		if (checkPreReq())
			new NetworkHandler(url, object, requestCode);
	}

	public void makePostRequest(String url, List<NameValuePair> nameValuePairs,
			long requestCode) {
		if (checkPreReq())
			new NetworkHandler(url, nameValuePairs, requestCode);
	}

	boolean checkPreReq() {
		if (mListner == null)
			return false;

		if (!AppUtils.isInternetConeected(App.getInstance().getContext())) {
			mListner.onNoInternet();
			return false;
		}
		
		/*if (mHeaderRequired && !AppPref.isTokenValid()) {
			FragmentLogin fragmentLogin = new FragmentLogin();
			AppUtils.replaceFragment(fragmentLogin, ((Fragment)mListner).getFragmentManager(), R.id.alMainLayout);
			return false;
		}*/
		
		return true;
	}

	class NetworkHandler extends AsyncTask<Void, Void, String> {
		boolean isPostRequest;
		String url;
		JSONObject object;
		List<NameValuePair> nameValuePairs;
		long requestCode;
		boolean isCancelled;

		public NetworkHandler(String url, long requestCode) {
			isPostRequest = false;
			this.url = url;
			this.requestCode = requestCode;

			this.executeOnExecutor(THREAD_POOL_EXECUTOR);
		}

		public NetworkHandler(String url, JSONObject object, long requestCode) {
			isPostRequest = true;

			this.url = url;
			this.object = object;
			this.requestCode = requestCode;
			this.executeOnExecutor(THREAD_POOL_EXECUTOR);
		}

		public NetworkHandler(String url, List<NameValuePair> nameValuePairs,
				long requestCode) {
			isPostRequest = true;

			this.url = url;
			this.nameValuePairs = nameValuePairs;
			this.requestCode = requestCode;
			this.executeOnExecutor(THREAD_POOL_EXECUTOR);
		}

		@Override
		protected void onPreExecute() {
			if (!AppUtils.isInternetConeected(App.getInstance().getContext())) {
				isCancelled = true;
				try {
					mListner.onNoInternet();
				} catch (Exception e) {
					AppUtils.showToast("No Network");
					// e.printStackTrace();
				}
				if (!isCancelled())
					this.cancel(true);
			}
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			if (isCancelled || isCancelled())
				this.cancel(true);
			String response = null;
			try {
				response = isPostRequest ? (nameValuePairs != null ? postReqToServer(
						url, nameValuePairs) : postReqToServer(url, object))
						: getReqToServer(url);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			if (isCancelled || isCancelled())
				this.cancel(true);
			if (result == null || result.equals("null"))
			{
				//mListner.onError("Response Null", "unable to connect to network", requestCode);
				mListner.onError("Response Null", "Roedd problem cysylltu a'r rhyngrwyd", requestCode);
			}
			else if (statusCode == 200) {
				mListner.onResponse(result, requestCode);
			} else {
				final String KEY_ERROR = "error";
				final String KEY_DESCRIPTION = "error_description";
				final String KEY_MESSAGE = "message";

				// {"error":"invalid_grant","error_description":"The Username/Email or Password is incorrect."}
				try {
					String infoForUser = result;
					JSONObject object = new JSONObject(result);
					if (object.has(KEY_ERROR) && object.has(KEY_DESCRIPTION)) {
						String error = object.getString(KEY_ERROR);
						String description = object.getString(KEY_DESCRIPTION);
						mListner.onError(error, description, requestCode);
						infoForUser = description;
					} else if (object.has(KEY_MESSAGE)) {
						String errorMsg = object.getString(KEY_MESSAGE);
						mListner.onError("unable to parse", errorMsg,
								requestCode);
						infoForUser = errorMsg;
					} else
						mListner.onError("unable to parse", result, requestCode);
					AppUtils.showToast(infoForUser);
					if (statusCode == 401 || infoForUser.equalsIgnoreCase("Authorization has been denied for this request.")){
						
						AppPref.getInstance().setTokenExpireTime(0);
						AppPref.getInstance().clearPref();
						Session.clearSession();
						MainActivity.mActivity.startActivity(new Intent(MainActivity.mActivity, LoginActivity.class));
						MainActivity.mActivity.finish();
						//MainActivity.mActivity.logout();
						//App.getInstance().startActivity(new Intent(App.getContext(), LoginActivity.class));
						
						
						//AppUtils.replaceFragment(new FragmentLogin(), ((Fragment)mListner).getFragmentManager(), com.clecs.R.id.amMainLayout);
					}
				} catch (JSONException e) {
					// if
					// (e.getMessage().endsWith("cannot be converted to JSONObject"))
					// mListner.onResponse(result, requestCode);
					mListner.onError("undefined error", result, requestCode);
					e.printStackTrace();
				}
			}
			super.onPostExecute(result);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	static String getReqToServer(String url) throws ClientProtocolException,
			IOException {
		url = url.replaceAll(" ", "%20");
		url = url.replaceAll("	", "%20");
		url = url.replaceAll("\n", "%0A");

		String response = null;
		HttpClient httpClient = getHttpClient();
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("Content-type", "application/json");
		if (mHeaderRequired)
			httpGet.setHeader("Authorization", AppPref.getInstance().getHeader());
		ResponseHandler responseHandler = getRespHandler();// new
															// BasicResponseHandler();
		response = httpClient.execute(httpGet, responseHandler).toString();
		// responce = new JSONObject(stringresponce1);
		return response;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	static String postReqToServer(String url, JSONObject jObject) {
		// url = url.replaceAll(" ", "%20");
		// url = url.replaceAll("	", "%20");
		// url = url.replaceAll("\n", "%0A");

		String response = null;
		HttpClient httpClient = getHttpClient();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader(HTTP.CONTENT_TYPE, "application/json");
		if (mHeaderRequired)
			httpPost.setHeader("Authorization", AppPref.getInstance().getHeader());
		ResponseHandler responseHandler = getRespHandler();// new
															// BasicResponseHandler();
		try {
			if (jObject != null) {
				// StringEntity se = new
				// StringEntity(URLEncoder.encode(jObject.toString(),
				// HTTP.UTF_8));
				StringEntity se = new StringEntity(jObject.toString(),
						HTTP.UTF_8);
				httpPost.setEntity(se);
			}
			response = httpClient.execute(httpPost, responseHandler);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	static String postReqToServer(String url, List<NameValuePair> nameValuePairs) {
		// url = url.replaceAll(" ", "%20");
		// url = url.replaceAll("	", "%20");
		// url = url.replaceAll("\n", "%0A");

		String response = null;
		HttpClient httpClient = getHttpClient();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader(HTTP.CONTENT_TYPE, "application/json");
		if (mHeaderRequired)
			httpPost.setHeader("Authorization", AppPref.getInstance().getHeader());
		// ResponseHandler<String> responseHandler = new
		// ResponseHandler<String>()
		// {
		// @Override
		// public String handleResponse(HttpResponse response) throws
		// ClientProtocolException, IOException
		// {
		// return EntityUtils.toString(response.getEntity());
		// }
		// };
		ResponseHandler responseHandler = getRespHandler();// new
															// BasicResponseHandler();
		try {
			if (nameValuePairs != null) {
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			}
			response = httpClient.execute(httpPost, responseHandler);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	static ResponseHandler<String> getRespHandler() {
		return new ResponseHandler<String>() {
			@Override
			public String handleResponse(HttpResponse response)
					throws ClientProtocolException, IOException {
				String result = EntityUtils.toString(response.getEntity());
				statusCode = response.getStatusLine().getStatusCode();
				try {
					if (statusCode != 200)
						result = result.split("\":\\[\"")[1].substring(0,
								result.split("\":\\[\"")[1].length() - 4);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.d("Request Handler", response.getStatusLine()
						.getReasonPhrase() + ", " + statusCode);
				return result;
			}
		};
	}

	static HttpClient getHttpClient() {
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		// The default value is zero, that means the timeout is not used.
		int timeoutConnection = 5000;
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				timeoutConnection);
		// Set the default socket timeout (SO_TIMEOUT)
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 10000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

		DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
		return httpClient;
	}
}