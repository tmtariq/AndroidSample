package com.clecs.widget;

import java.net.URLEncoder;

import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;

import com.clecs.network.RequestHandler;
import com.clecs.network.RequestHandler.ServerResponseListner;
import com.clecs.objects.MyTokenizer;
import com.clecs.utils.AppStatics;

public class MyAutoCompleteTextView extends MultiAutoCompleteTextView implements ServerResponseListner
	{
		final int REQ_SEARCH = 400;
		RequestHandler handler;
		ArrayAdapter<String> adapter;
		TextWatcher mTextWatcher;

		public MyAutoCompleteTextView( Context context )
			{
				super(context);
				initialize();
			}

		public MyAutoCompleteTextView( Context context, AttributeSet attrs, int defStyleAttr )
			{
				super(context, attrs, defStyleAttr);
				initialize();
			}

		public MyAutoCompleteTextView( Context context, AttributeSet attrs )
			{
				super(context, attrs);
				initialize();
			}

		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event)
			{
				if (keyCode == KeyEvent.KEYCODE_ENTER)
					{
						// Just ignore the [Enter] key
						return true;
					}
				// Handle all other keys in the default way
				return super.onKeyDown(keyCode, event);
			}

		void initialize()
			{
				handler = new RequestHandler(this, true);
				mTextWatcher = new TextWatcher()
					{

						@Override
						public void onTextChanged(CharSequence s, int start, int before, int count)
							{
								try
									{
										Layout layout = getLayout();
										int pos = getSelectionStart();
										int line = layout.getLineForOffset(pos);
										int baseline = layout.getLineBaseline(line);
										int bottom = getHeight();
										setDropDownVerticalOffset(baseline - bottom + 10);
									}
								catch (Exception e)
									{
										e.printStackTrace();
									}

							}

						@Override
						public void beforeTextChanged(CharSequence s, int start, int count, int after)
							{
								// TODO Auto-generated method stub

							}

						@Override
						public void afterTextChanged(Editable s)
							{
								try
									{
										String toSearch = s.toString().substring(0, getSelectionStart());
										toSearch = toSearch.substring(toSearch.lastIndexOf(" ") + 1);
										if (toSearch.length() > 1)
											if (toSearch.startsWith("@"))
												{
													// String word = s.toString().substring(s.toString().lastIndexOf("@"), s.length() - 1);
													setTokenizer(new MyTokenizer('@'));
													handler.makeGetRequest(AppStatics.getMentionSearchURL(toSearch.replace("@", "")), REQ_SEARCH);
												}
											else if (toSearch.startsWith("#"))
												{
													// String word = s.toString().substring(s.toString().lastIndexOf("@"), s.length() - 1);
													setTokenizer(new MyTokenizer('#'));
													handler.makeGetRequest(AppStatics.getHashTagSearchURL(URLEncoder.encode(toSearch, HTTP.UTF_8)), REQ_SEARCH);
												}
									}
								catch (Exception e)
									{
										e.printStackTrace();
									}
							}
					};

				addTextChangedListener(mTextWatcher);
				setAdapter(adapter);
				setThreshold(1);
				// setTokenizer(new MyTokenizer());
			}

		@Override
		public void onResponse(String response, long requestCode)
			{
				try
					{
						JSONArray jsonArray = new JSONArray(response);
						String[] suggestionList = new String[jsonArray.length()];
						for (int i = 0; i < jsonArray.length(); i++)
							{
								suggestionList[i] = jsonArray.getString(i).replace("#", "");
							}
						adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, suggestionList);
						setAdapter(adapter);
					}
				catch (JSONException e)
					{
						e.printStackTrace();
					}
			}

		@Override
		public void onError(String error, String description, long requestCode)
			{

			}

		@Override
		public void onNoInternet()
			{

			}

	}
