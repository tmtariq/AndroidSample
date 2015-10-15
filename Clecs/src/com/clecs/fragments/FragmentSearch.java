package com.clecs.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.clecs.R;
import com.clecs.adapters.AdapterFollower;
import com.clecs.adapters.AdapterPost;
import com.clecs.network.RequestHandler;
import com.clecs.network.RequestHandler.ServerResponseListner;
import com.clecs.objects.Follower;
import com.clecs.objects.Post;
import com.clecs.utils.AppStatics;
import com.clecs.utils.AppUtils;
import com.clecs.utils.Session;
import com.clecs.widget.PullAndLoadListView;
import com.clecs.widget.PullAndLoadListView.OnLoadMoreListener;
import com.clecs.widget.PullToRefreshListView.OnRefreshListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class FragmentSearch extends Fragment implements ServerResponseListner {
	final int REQ_SEARCH_POSTS = 100;
	final int REQ_LATEST_POSTS = 101;
	final int REQ_LOAD_MORE_POSTS = 102;
	final int REQ_SEARCH_USER = 200;
	final int REQ_LATEST_USER = 201;
	final int REQ_LOAD_MORE_USER = 202;

	// RequestHandler requestHandler;
	Adapter adapter;
	long lastId = 0;
	View root;
	// PullAndLoadListView lv;
	PullAndLoadListView lvSearchResult;
	ProgressDialog pd;

	TextWatcher mTextWatcher;
	EditText etSearch;
	TextView tvClear;
	View mResultHolder;
	// ArrayList<Post> postList;
	private boolean isUserSearch = false;

	String searchText = null;

	public FragmentSearch() {
		isUserSearch = false;
	}

	public FragmentSearch(boolean userSearch) {

		isUserSearch = userSearch;
	}

	public FragmentSearch(String text) {
		searchText = text;
		isUserSearch = false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (root == null) {
			root = inflater.inflate(R.layout.fragment_search, container, false);
			initView();
			AppUtils.setFont(getActivity(), (ViewGroup) root);
			if (searchText == null) AppUtils.openKeyboard(getActivity());
		}
		return root;
	}

	void initView() {

		pd = new ProgressDialog(getActivity());
		//pd.setMessage("Loading ...");
		pd.setMessage("Llwytho ...");
		lvSearchResult = (PullAndLoadListView) root.findViewById(R.id.fsLv);
		etSearch = (EditText) root.findViewById(R.id.fsEtSearch);
		tvClear = (TextView) root.findViewById(R.id.fsTvClear);
		mResultHolder = root.findViewById(R.id.fsLlResultText);
		//if (isUserSearch)
			//(root.findViewById(R.id.fsTvResultTitle)).setVisibility(View.GONE);
		// requestHandler = new RequestHandler(this, true);
		/*if (!isUserSearch && Session.listSearchPost != null) {
			adapter = new AdapterPost(getActivity(), Session.listSearchPost);
			lvSearchResult.setAdapter((ListAdapter) adapter);
		} else if (isUserSearch && Session.listSearchUser != null) {
			adapter = new AdapterFollower(getActivity(), Session.listSearchUser);
			lvSearchResult.setAdapter((ListAdapter) adapter);
		}*/
		
		//changeListViewVisibility();
		
		lvSearchResult.setOnLoadMoreListener(new OnLoadMoreListener() {
			@Override
			public void onLoadMore() {
				//requestHandler.makeGetRequest(AppStatics.getAllPostsForUser(lastId, userName),REQ_LOAD_MORE);
				//AppUtils.showToast("Not implmented yet");
				//doSearch();
				if(!isUserSearch) {
					lastId = (int) Session.listSearchPost.get(Session.listSearchPost.size() - 1).getPostId();
					new RequestHandler(FragmentSearch.this, true).makeGetRequest(AppStatics.getSearchWithLinksURL(etSearch.getText().toString(), lastId), REQ_LOAD_MORE_POSTS);
				} else {
					lvSearchResult.onLoadMoreComplete();
				}
				//lvSearchResult.onLoadMoreComplete();
			}
		});
		lvSearchResult.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				//refreshPosts();
				//AppUtils.showToast("Not implmented yet");
				lvSearchResult.onRefreshComplete();
			}
		});
		
		// mTextWatcher = new TextWatcher() {
		//
		// @Override
		// public void onTextChanged(CharSequence s, int start, int before,
		// int count) {
		//
		// }
		//
		// @Override
		// public void beforeTextChanged(CharSequence s, int start, int count,
		// int after) {
		//
		// }
		//
		// @Override
		// public void afterTextChanged(Editable s) {
		// doSearch();
		// }
		// };

		OnEditorActionListener onEditActionListner = new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
						|| (actionId == EditorInfo.IME_ACTION_DONE)) {
					lastId = 0;
					doSearch();
				}
				return false;
			}
		};

		etSearch.setOnEditorActionListener(onEditActionListner);
		// etContainer.setVisibility(View.VISIBLE);
		// etSearch.setText("Hello");
		// lv.setOnRefreshListener(null);
		// etSearch.addTextChangedListener(mTextWatcher);
		tvClear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				lastId = 0;
				etSearch.setText("");
				onStop();
				clearPostList();
				changeListViewVisibility();
				AppUtils.openKeyboard(getActivity());
				tvClear.setVisibility(View.INVISIBLE);
			}
		});
		
		if (searchText != null) {
			etSearch.setText(searchText);
			doSearch();
		}
	}

	/*@Override
	public void onStop() {
		if (Session.listSearchPost != null)
			Session.listSearchPost.clear();
		Session.listSearchPost = null;
		if (Session.listSearchUser != null)
			Session.listSearchUser.clear();
		Session.listSearchUser = null;
		super.onStop();
	}*/

	void doSearch() {
		pd.show();
		tvClear.setVisibility(etSearch.getText().length() > 0 ? View.VISIBLE
				: View.GONE);
		//mResultHolder.setVisibility(View.GONE);

		if (etSearch.getText().toString().trim().length() > 0) {
			// RequestHandler requestHandler = new RequestHandler(this, true);
			if (isUserSearch) {
				new RequestHandler(this, true).makeGetRequest(AppStatics
						.getFindUserUrl(etSearch.getText().toString()), REQ_SEARCH_USER);
			} else {
				new RequestHandler(this, true).makeGetRequest(AppStatics.getSearchWithLinksURL(etSearch.getText().toString(), lastId), REQ_SEARCH_POSTS);
			}
		} else {
			clearPostList();
			if (adapter != null)
				synchronized (adapter) {
					// notify() is being called here when the thread and
					// synchronized block does not own the lock on the object.
					adapter.notify();
				}
			// adapter.notify();
		}
	}

	@Override
	public void onResponse(String response, long requestCode) {
		etSearch.requestFocus();
		//clearPostList();
		// postList = new Gson().fromJson(response, new TypeToken<List<Post>>()
		// {
		// }.getType());
		// System.out.println(response);
		// if (etSearch.getText().toString().trim().length() == 0)
		// clearPostList();

		// Session.listSearchPost = (ArrayList<Post>) postList.clone();
		// adapter = new AdapterPost(getActivity(), postList, etSearch.getText()
		// .toString());
		// lv.setAdapter(adapter);

		if (requestCode == REQ_SEARCH_USER) {
			Session.listSearchUser = new Gson().fromJson(
					response.replaceAll("\"avatar\"", "\"imageUrl\"")
							.replaceAll("\"namey\"", "\"name\""), new TypeToken<List<Follower>>() {}.getType());
			adapter = new AdapterFollower(getActivity(), Session.listSearchUser);
			lvSearchResult.setAdapter((ListAdapter) adapter);
		} else if (requestCode == REQ_SEARCH_POSTS) {
			Session.listSearchPost = new Gson().fromJson(response, new TypeToken<List<Post>>() {}.getType());
			
			//if (postList.size() > 0) {
				//if(lastId==0)
					//Session.listSearchPost = postList;
				//else
					
			//}
			
			adapter = new AdapterPost(getActivity(), Session.listSearchPost, etSearch.getText().toString());
			lvSearchResult.setAdapter((ListAdapter) adapter);
			//lastId = (int) Session.listSearchPost.get(Session.listSearchPost.size() - 1).getPostId();
		} else if (requestCode == REQ_LOAD_MORE_POSTS) {
			ArrayList<Post> postList = new Gson().fromJson(response, new TypeToken<List<Post>>() {}.getType());
			if(Session.listSearchPost == null ) 
				Session.listSearchPost = postList;
			else
				Session.listSearchPost.addAll(postList);
			//lastId = (int) postList.get(postList.size() - 1).getPostId();
			notifyAdapter();
			lvSearchResult.onLoadMoreComplete();
		}
		//lvSearchResult.setAdapter((ListAdapter) adapter);
		changeListViewVisibility();
		pd.dismiss();
	}
	
	private void changeListViewVisibility() {
		
		if (Session.listSearchPost != null && Session.listSearchPost.size() > 0
				|| Session.listSearchUser != null
				&& Session.listSearchUser.size() > 0){
			mResultHolder.setVisibility(View.VISIBLE);
			lvSearchResult.setVisibility(View.VISIBLE);
		} else {
			mResultHolder.setVisibility(View.GONE);
			lvSearchResult.setVisibility(View.GONE);
		}
	}
	
	void notifyAdapter() {
		if (adapter != null)
			synchronized (adapter) {
				//changeListViewVisibility();
				if(isUserSearch) 
					((AdapterFollower)adapter).notifyDataSetChanged();
				else
					((AdapterPost)adapter).notifyDataSetChanged();
			}
	}

	void clearPostList() {
		if (Session.listSearchPost != null)
			Session.listSearchPost.clear();
		Session.listSearchPost = null;
		if (Session.listSearchUser != null)
			Session.listSearchUser.clear();
		Session.listSearchUser = null;
		
		lvSearchResult.setAdapter(null);
	}

	@Override
	public void onError(String error, String reason, long requestCode) {
		pd.dismiss();
	}

	@Override
	public void onNoInternet() {
		pd.dismiss();
		AppUtils.showToast("Credwch eich bod yn all-lein");
		//AppUtils.showToast("No Network");
	}

	@Override
	public void onPause() {
		AppUtils.closeKeyboard(getActivity(), etSearch);
		super.onPause();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (root != null) {
			ViewGroup parentViewGroup = (ViewGroup) root.getParent();
			if (parentViewGroup != null) {
				parentViewGroup.removeAllViews();
			}
		}
	}
}
