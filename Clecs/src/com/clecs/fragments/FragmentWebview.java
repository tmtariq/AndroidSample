package com.clecs.fragments;

import com.clecs.R;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class FragmentWebview extends Fragment {
	WebView mWebView;
	View root;
	String mUrl;
	ProgressDialog progDailog;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		root = inflater.inflate(R.layout.fragment_webview, container, false);
		initViews();
		return root;
		// return super.onCreateView(inflater, container, savedInstanceState);
	}

	public FragmentWebview(String url) {
		// TODO Auto-generated constructor stub
		mUrl = url;
	}

	@SuppressLint("SetJavaScriptEnabled")
	void initViews() {
		
		//progDailog = ProgressDialog.show(getActivity(), "Loading", "Please wait...", true);
		progDailog = ProgressDialog.show(getActivity(), "Llwytho", "Llwytho...", true);
		
		progDailog.setCancelable(true);
		mWebView = (WebView) root.findViewById(R.id.fwWebView);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setLoadWithOverviewMode(true);
		mWebView.getSettings().setUseWideViewPort(true);

		loadUrl(mUrl);
		mWebView.loadUrl(mUrl);
	}

	void loadUrl(String url) {
		mWebView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				progDailog.show();
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, final String url) {
				progDailog.dismiss();
			}
		});
	}
}
