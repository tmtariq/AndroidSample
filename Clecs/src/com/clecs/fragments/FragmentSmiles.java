package com.clecs.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clecs.R;
import com.clecs.adapters.AdapterFollower;
import com.clecs.network.RequestHandler;
import com.clecs.network.RequestHandler.ServerResponseListner;
import com.clecs.objects.Comment;
import com.clecs.objects.Follower;
import com.clecs.objects.Post;
import com.clecs.objects.SmileorSad;
import com.clecs.utils.AppStatics;
import com.clecs.utils.AppUtils;
import com.clecs.widget.LoadMoreListView;
import com.clecs.widget.LoadMoreListView.OnLoadMoreListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class FragmentSmiles extends Fragment implements ServerResponseListner {
	View root;
	ProgressDialog pd;
	TextView tvTitle;
	LoadMoreListView listView;
	RequestHandler handler;
	final int REQ_SAD_SMILE = 100;

	boolean isSmile;
	AdapterFollower adapterFollower;
	Post mPost;
	int lastId = 0;
	final int REQ_LOAD_MORE = 200;
	ArrayList<Follower> followerList;

	public FragmentSmiles(boolean isSmile, Post post) {
		this.isSmile = isSmile;
		mPost = post;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (root == null) {
			root = inflater.inflate(R.layout.fragment_follower, container,
					false);
			pd = new ProgressDialog(getActivity());
			pd.setCancelable(false);
			
			//pd.setMessage("Getting info...");
			//loading
			pd.setMessage("Llwytho...");
			
			initViews();
			System.out.println("Parent .. on Create");
			AppUtils.setFont(getActivity(), (ViewGroup) root);
		}
		return root;
	}

	void initViews() {
		tvTitle = (TextView) root.findViewById(R.id.ffTvTitle);
		listView = (LoadMoreListView) root.findViewById(R.id.ffLv);
		handler = new RequestHandler(this, true);
		
		//tvTitle.setText(isSmile ? "Who has smiled" : "Who has sad");

		tvTitle.setCompoundDrawablesWithIntrinsicBounds(isSmile ? R.drawable.smile_green : R.drawable.sad_green, 0,0,0);
	    tvTitle.setTextSize(0);    
	    tvTitle.setPadding(50, 20, 0, 10);
	    tvTitle.setBackgroundResource(R.color.white);
		
		if (adapterFollower == null){
			lastId = 0;
			followerList = new ArrayList<Follower>();
			adapterFollower = new AdapterFollower(getActivity(), followerList);
			listView.setAdapter(adapterFollower);
			getSadSmilesUsers(REQ_SAD_SMILE);
		}
		// getSadSmilesUsers((mPost.isShare() || mPost.isQuote()) ?
		// mPost.getTransactAgainstPostId() : mPost.getPostId());
		
		listView.setOnLoadMoreListener(new OnLoadMoreListener() {

			@Override
			public void onLoadMore() {
				//pd.show();
				lastId = followerList.size() > 0 ? followerList.get(followerList.size() - 1).getId() : 0;
				getSadSmilesUsers(REQ_LOAD_MORE);
				//handler.makeGetRequest(AppStatics.getSmilesOrSadsUrl(mPost.getTransactAgainstPostId(), lastId, isSmile), REQ_LOAD_MORE);
			}
		});
	}

	void getSadSmilesUsers(int requestCode) {
		
		handler.makeGetRequest(AppStatics.getSmilesOrSadsUrl(mPost.getTransactAgainstPostId(), lastId, isSmile),
				requestCode);// PostDetailUrl(postId + ""), REQ_POST_DETAIL);
		pd.show();
	}

	@Override
	public void onResponse(String response, long requestCode) {
		
		if (requestCode == REQ_SAD_SMILE) {
			/*ArrayList<SmileorSad> list = new Gson().fromJson(response,
					new TypeToken<List<SmileorSad>>() {
					}.getType());
			ArrayList<SmileorSad> listSadSmile = new ArrayList<SmileorSad>();
			for (int i = 0; i < list.size(); i++) {
				if ((isSmile && list.get(i).isSmileOrSad())
						|| (!isSmile && !list.get(i).isSmileOrSad()))
					listSadSmile.add(list.get(i));
			}
			ArrayList<Follower> followerList = new ArrayList<Follower>();
			for (int i = 0; i < listSadSmile.size(); i++) {
				Follower follower = new Follower();
				follower.setUserName(listSadSmile.get(i).getUserName());
				follower.setId(Integer.parseInt(listSadSmile.get(i).getId()));
				follower.setImageUrl(listSadSmile.get(i).getAvtar());
				follower.setName("");
				followerList.add(follower);
			}*/
			
			ArrayList<Follower> list = new Gson().fromJson(response.replaceAll("\"avatar\"", "\"imageUrl\""),
					new TypeToken<List<Follower>>() {
					}.getType());
			followerList.addAll(list);
			
		} else if (requestCode == REQ_LOAD_MORE) {
			ArrayList<Follower> list = new Gson().fromJson(response.replaceAll("\"avatar\"", "\"imageUrl\""),
					new TypeToken<List<Follower>>() {
					}.getType());
			followerList.addAll(list);
			listView.onLoadMoreComplete();
		}
		notifyAdapter();
		pd.dismiss();
	}
	
	private void notifyAdapter() {
		synchronized (adapterFollower) {
			adapterFollower.notifyDataSetChanged();
		}
	}

	@Override
	public void onError(String error, String description, long requestCode) {
		// TODO Auto-generated method stub
		pd.dismiss();
	}

	@Override
	public void onNoInternet() {
		// TODO Auto-generated method stub
		pd.dismiss();
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
