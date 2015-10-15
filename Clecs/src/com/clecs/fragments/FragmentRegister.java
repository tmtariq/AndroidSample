package com.clecs.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.clecs.R;
import com.clecs.network.RequestHandler;
import com.clecs.network.RequestHandler.ServerResponseListner;
import com.clecs.utils.AppPref;
import com.clecs.utils.AppStatics;
import com.clecs.utils.AppUtils;

public class FragmentRegister extends Fragment implements ServerResponseListner
{
	// final int REQ_CODE_LOGIN = 100;
	View rootView;
	EditText etUserName, etName, etEmail, etPasssword, etConfirmPass;
	Button btnLogin, btnRegister;
	CheckBox termsCheck;
	ProgressDialog pd;
	private Pattern pattern;
	//private Matcher matcher;

	private static final String USERNAME_PATTERN = "^[a-z0-9_-]{3,15}$";

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
		{
			rootView = inflater.inflate(R.layout.fragment_register, container, false);
			initViews();
			setListners();
			AppUtils.setFont(getActivity(), (ViewGroup)rootView);
			return rootView;
		}

	void initViews()
		{
			pattern = Pattern.compile(USERNAME_PATTERN);
			//new AppPref(getActivity());
			etUserName = (EditText) rootView.findViewById(R.id.frEtUserName);
			etName = (EditText) rootView.findViewById(R.id.frEtName);
			etEmail = (EditText) rootView.findViewById(R.id.frEtEmail);
			etPasssword = (EditText) rootView.findViewById(R.id.frEtPassword);
			etConfirmPass = (EditText) rootView.findViewById(R.id.frEtPasswordConfirm);

			btnLogin = (Button) rootView.findViewById(R.id.frBtnLogin);
			btnRegister = (Button) rootView.findViewById(R.id.frBtnRegister);
			
			termsCheck = (CheckBox) rootView.findViewById(R.id.checkBoxAcceptTerms);

			TextView textViewTerms = (TextView) rootView.findViewById(R.id.textView1);
			textViewTerms.setText(
				        Html.fromHtml(
				            "<a href=\"https://www.clecs.cymru/i/Telerau\">Tichiwch cytuno telerau au amodau</a> "));
			textViewTerms.setMovementMethod(LinkMovementMethod.getInstance());

			
			pd = new ProgressDialog(getActivity());
		}

	void setListners()
		{
			btnRegister.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v)
						{
							if (isValidate())
								{
									RequestHandler requestHandler = new RequestHandler(FragmentRegister.this, false);
									// grant_type=password&username=mmmmmm&password=mmmmmm
									try
										{
											// "userName": "sample string 1",
											// "password": "sample string 2",
											// "confirmPassword": "sample string 3",
											// "email": "sample string 4",
											// "name": "sample string 5"
											/*List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
											nameValuePairs.add(new BasicNameValuePair("userName", etUserName.getText().toString()));
											nameValuePairs.add(new BasicNameValuePair("password", etPasssword.getText().toString()));
											nameValuePairs.add(new BasicNameValuePair("confirmPassword", etConfirmPass.getText().toString()));
											nameValuePairs.add(new BasicNameValuePair("email", etEmail.getText().toString()));
											nameValuePairs.add(new BasicNameValuePair("name", etName.getText().toString()));
											*/

											JSONObject object = new JSONObject();
											object.put("userName", etUserName.getText().toString());
											object.put("password", etPasssword.getText().toString());
											object.put("confirmPassword", etConfirmPass.getText().toString());
											object.put("email", etEmail.getText().toString());
											object.put("name", etName.getText().toString());

											//pd.setMessage("Signing you up...");
											// we are just saying loading...
											pd.setMessage("Llwytho...");
											
											pd.show();
											requestHandler.makePostRequest(AppStatics.URL_REGISTER, object, 100);// "?grant_type=password&username=mmmmmm&password=mmmmmm");

											
										}
									catch (Exception e)
										{
											e.printStackTrace();
										}

								}
						}
				});
			btnLogin.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v)
						{
							getFragmentManager().popBackStack();
						}
				});
		}

	@Override
	public void onResponse(String response, long requestCode)
		{
			// try
			// {
			// Token token = new Gson().fromJson(response, Token.class);
			// AppPref.setToken(token.getAccessToken());
			// System.out.println(response);
			// startActivity(new Intent(getActivity(), MainActivity.class));
			// }
			// catch (JsonSyntaxException e)
			// {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			pd.dismiss();
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			
			
			//builder.setMessage("Register successfully , Please check your email to confirm your account");
			builder.setMessage("Cadarnhewch ddefnyddio'r e-bost a anfonwyd atoch");
			
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick(DialogInterface dialog, int which)
						{
							FragmentRegister.this.getFragmentManager().popBackStack();
						}
				});
			builder.setNegativeButton("Done", new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick(DialogInterface dialog, int which)
						{
							FragmentRegister.this.getFragmentManager().popBackStack();
						}
				});
			builder.create().show();

		}

	@Override
	public void onError(String error, String reason, long requestCode)
		{
			AppUtils.showToast(reason);
			pd.dismiss();
		}

	@Override
	public void onNoInternet()
		{
			AppUtils.showToast(R.string.no_internet_available);
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
			 if (!AppUtils.isValidEmail(etEmail.getText().toString()))
			 {
				 etEmail.setError(getActivity().getString(R.string.invalid_email));
				 isValid = false;
			 }
			if (etPasssword.length() == 0)
				{
					etPasssword.setError(getActivity().getString(R.string.password_require));
					isValid = false;
				}
			if (!etPasssword.getText().toString().equals(etConfirmPass.getText().toString()))
				{
					//etConfirmPass.setError("password mismatch");
					etConfirmPass.setError("Nid yw'r cyfrinair a chadarnhau cyfrinair yn cyfateb");
					isValid = false;
				}
			if (etUserName.getText().toString().length() == 0)
				{
					//etUserName.setError("This field required");
					// please enter all fields
					etUserName.setError("Os gwelch yn dda nodwch yr holl feysydd");
					isValid = false;
			} 
			if (!pattern.matcher(etUserName.getText().toString()).matches()) {
				etUserName.setError("Enter valid uername. min 3, max 15 and a-z0-9_- charachters are allowed");
				isValid = false;
			}
			if (etName.getText().toString().length() == 0)
				{
					//etName.setError("This field required");
					// please enter all fields
					etName.setError("Os gwelch yn dda nodwch yr holl feysydd");
					isValid = false;
				}
			if (!termsCheck.isChecked())
			{
				//termsCheck.setError("Please accept the Terms and conditions");
				termsCheck.setError("Os gwelwch yn dda derbyn y telerau a'r amodau ");
				isValid = false;
			}

			return isValid;
		}

}
