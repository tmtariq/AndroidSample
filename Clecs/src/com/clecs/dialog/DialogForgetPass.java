package com.clecs.dialog;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.clecs.R;
import com.clecs.network.RequestHandler;
import com.clecs.utils.AppStatics;
import com.clecs.utils.AppUtils;

public class DialogForgetPass extends DialogFragment
	{
		FragmentActivity mActivity;
		Button btnCancel, btnOk;
		EditText etEmail;
		RequestHandler handler;
		int requestCode;

		public DialogForgetPass( FragmentActivity activity, RequestHandler handler, int requestCode )
			{
				mActivity = activity;
				this.handler = handler;
				this.requestCode = requestCode;
			}

		@Override
		@NonNull
		public Dialog onCreateDialog(Bundle savedInstanceState)
			{
				LayoutInflater inflater = getActivity().getLayoutInflater();
				View view = inflater.inflate(R.layout.dialog_forget_password, null);
				AppUtils.setFont(getActivity(), (ViewGroup)view);
				btnOk = (Button) view.findViewById(R.id.dfpBtnOk);
				btnCancel = (Button) view.findViewById(R.id.dfpBtnCancel);
				etEmail = (EditText) view.findViewById(R.id.dfpEtEmail);

				btnOk.setOnClickListener(new OnClickListener()
					{

						@Override
						public void onClick(View v)
							{
								if (etEmail.getText().toString().trim().length() == 0)
									{
										//etEmail.setError("Email Required");
										// please enter all fields
										etEmail.setError("Os gwelch yn dda nodwch yr holl feysydd");
									
									}
								else if (!Patterns.EMAIL_ADDRESS.matcher(etEmail.getText()).matches())
									{
										etEmail.setError("Rhowch gyfeiriad e-bost dilys");
										//etEmail.setError("Enter Valid Email Address");
									}
								else
									{
										JSONObject obj = new JSONObject();
										try
											{
												obj.put("email", etEmail.getText().toString());
											}
										catch (JSONException e)
											{
												e.printStackTrace();
											}
										// List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
										// nameValuePairs.add(new BasicNameValuePair("email", etEmail.getText().toString()));
										handler.makePostRequest(AppStatics.URL_FORGET_PASSWORD, obj, requestCode);
										dismiss();
									}
							}
					});
				btnCancel.setOnClickListener(new OnClickListener()
					{

						@Override
						public void onClick(View v)
							{
								dismiss();
							}
					});

				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setView(view);
				return builder.create();
				// return super.onCreateDialog(savedInstanceState);
			}

		public void show()
			{
				final String TAG = "dialog";
				FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
				Fragment prev = mActivity.getSupportFragmentManager().findFragmentByTag(TAG);
				if (prev != null)
					ft.remove(prev);
				ft.addToBackStack(null);

				// ft.commit();
				// Create and show the dialog.
				show(ft, TAG);
			}
	}
