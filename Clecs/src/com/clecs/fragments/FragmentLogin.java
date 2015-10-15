package com.clecs.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.clecs.MainActivity;
import com.clecs.R;
import com.clecs.dialog.DialogForgetPass;
import com.clecs.network.RequestHandler;
import com.clecs.network.RequestHandler.ServerResponseListner;
import com.clecs.objects.MyProfileInfo;
import com.clecs.objects.Token;
import com.clecs.utils.AppPref;
import com.clecs.utils.AppStatics;
import com.clecs.utils.AppUtils;
import com.clecs.utils.Session;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class FragmentLogin extends Fragment implements ServerResponseListner
	{
		final int REQ_CODE_LOGIN = 100;
		final int REQ_CODE_USER_INFO = 200;
		final int REQ_CODE_FORGET_PASS = 300;
		final int REQ_CODE_REGISTER_USER = 400;
		View rootView;
		EditText etEmail, etPasssword;
		TextView tvForgetPass;
		Button btnLogin, btnRegister;
		ProgressDialog pd;
		RequestHandler requestHandler;

		@Override
		public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
			{
				rootView = inflater.inflate(R.layout.fragment_login, container, false);
				initViews();
				setListners();
				AppUtils.setFont(getActivity(), (ViewGroup)rootView);
				return rootView;
			}

		void initViews()
			{
				//new AppPref(getActivity());
				pd = new ProgressDialog(getActivity());
				pd.setCancelable(false);
				etEmail = (EditText) rootView.findViewById(R.id.flEtUserName);
				etPasssword = (EditText) rootView.findViewById(R.id.flEtPassword);
				tvForgetPass = (TextView) rootView.findViewById(R.id.flTvForgotPassword);

				btnLogin = (Button) rootView.findViewById(R.id.flBtnLogin);
				btnRegister = (Button) rootView.findViewById(R.id.flBtnRegister);

			}

		void setListners()
			{
				btnLogin.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v)
							{
								if (isValidate())
									{
										requestHandler = new RequestHandler(FragmentLogin.this, false);
										// grant_type=password&username=mmmmmm&password=mmmmmm
										try
											{
												List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
												nameValuePairs.add(new BasicNameValuePair("grant_type", "password"));
												nameValuePairs.add(new BasicNameValuePair("username", etEmail.getText().toString()));
												nameValuePairs.add(new BasicNameValuePair("password", etPasssword.getText().toString()));
												JSONObject object = new JSONObject();
												object.put("grant_type", "password");
												object.put("username", etEmail.getText().toString());
												object.put("password", etPasssword.getText().toString());

												// Requesting for userInfo with Login...
												//pd.setMessage("signing you in...");
												pd.setMessage("Arwyddo mewn...");
												
												pd.show();
												requestHandler.makePostRequest(AppStatics.URL_TOKEN, nameValuePairs, REQ_CODE_LOGIN);// "?grant_type=password&username=mmmmmm&password=mmmmmm");

												
											}
										catch (Exception e)
											{
												e.printStackTrace();
											}
									}
							}
					});
				btnRegister.setOnClickListener(new OnClickListener()
					{

						@Override
						public void onClick(View v)
							{
								AppUtils.replaceFragment(new FragmentRegister(), getFragmentManager(), R.id.alMainLayout);
							}
					});
				tvForgetPass.setOnClickListener(new OnClickListener()
					{

						@Override
						public void onClick(View v)
							{
								requestHandler = new RequestHandler(FragmentLogin.this);
								new DialogForgetPass((FragmentActivity) getActivity(), requestHandler, REQ_CODE_FORGET_PASS).show();
							}
					});
			}

		@Override
		public void onResponse(String response, long requestCode)
			{
				if (requestCode == REQ_CODE_LOGIN)
					{
						// {"expires_in":1209599,".issued":"Wed, 21 Jan 2015 17:57:26 GMT",".expires":"Wed, 04 Feb 2015 17:57:26 GMT"}
						Token token = new Gson().fromJson(response, Token.class);
						AppPref.getInstance().setToken(token.getAccessToken());
						AppPref.getInstance().setTokenExpireTime(token.getExpiresIn());
						// startNextActivity();
						// System.out.println(response);
						// pd.dismiss();
						
						registerDevice();
						requestHandler = new RequestHandler(this, true);
						requestHandler.makeGetRequest(AppStatics.URL_GET_PROFILE_DETAIL, REQ_CODE_USER_INFO);
					}
				else if (requestCode == REQ_CODE_USER_INFO)
					{
						try
							{
								MyProfileInfo myProfileInfo = new Gson().fromJson(response, MyProfileInfo.class);
								Session.myProfileInfo = myProfileInfo;
								MainActivity.mActivity.notifyDrawer();
							}
						catch (JsonSyntaxException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						catch(NullPointerException e)
							{
								e.printStackTrace();
							}
					}
				else if (requestCode == REQ_CODE_FORGET_PASS)
					{
						AlertDialog alertDialog = new ProgressDialog(getActivity());
						//alertDialog.setMessage("Please check your email.");
						// please enter a valid email addrtess
						alertDialog.setMessage("Rhowch gyfeiriad e-bost dilys");
						alertDialog.show();
					}
				else if (requestCode == REQ_CODE_REGISTER_USER)
					{
						startNextActivity();
						System.out.println(response);
						pd.dismiss();
					}
			}

		@Override
		public void onError(String error, String description, long requestCode)
			{
				AppUtils.showToast(description);
				pd.dismiss();
			}

		@Override
		public void onNoInternet()
			{
				Toast.makeText(getActivity(), "Credwch eich bod yn all-lein", Toast.LENGTH_SHORT).show();
				//Toast.makeText(getActivity(), "No Internet", Toast.LENGTH_SHORT).show();
				pd.dismiss();
			}

		boolean isValidate()
			{
				boolean isValid = true;
				if (etEmail.getText().toString().length() == 0)
					{
						etEmail.setError(getActivity().getString(R.string.email_required));
						isValid = false;
					}
				// else if (AppUtils.isValidEmail(etEmail.getText()))
				// {
				// etEmail.setError(getActivity().getString(R.string.invalid_email));
				// isValid = false;
				// }
				if (etPasssword.length() == 0)
					{
						etPasssword.setError(getActivity().getString(R.string.password_require));
						isValid = false;
					}
				return isValid;
			}

		void registerDevice()
			{
				// "deviceType": "sample string 1",
				// "deviceToken": "sample string 2",
				// "appVersion": "sample string 3",
				// "timeZone": "sample string 4"
				try
					{
						JSONObject obj = new JSONObject();
						obj.put("deviceType", "android");
						obj.put("deviceToken", AppPref.getInstance().getRegId());
						obj.put("appVersion", getAppVersion(getActivity()));
						obj.put("timeZone", TimeZone.getDefault());

						requestHandler.makePostRequest(AppStatics.URL_REGISTER_DEVICE, obj, REQ_CODE_REGISTER_USER);
					}
				catch (JSONException e)
					{
						e.printStackTrace();
					}

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

		void startNextActivity()
			{
				startActivity(new Intent(getActivity(), MainActivity.class));
				getActivity().finish();
			}
	}
