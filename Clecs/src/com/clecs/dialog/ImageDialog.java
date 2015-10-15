package com.clecs.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.clecs.ImageViewerActivity;
import com.clecs.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ImageDialog extends DialogFragment
{
	FragmentActivity mActivity;
	String mUrl;
	ImageView iv;

	public ImageDialog( FragmentActivity activity, String imageUrl )
		{
			mActivity = activity;
			mUrl = imageUrl;
		}

	@Override
	public void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MyDialog);
		}

	@Override
	@NonNull
	public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			LayoutInflater inflater = getActivity().getLayoutInflater();
			View view = inflater.inflate(R.layout.dialog_image, null);
			iv = (ImageView) view.findViewById(R.id.diIv);
			// mActivity.getWindow().setBackgroundDrawableResource(android.R.color.black);
			// mActivity.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			ImageLoader.getInstance().displayImage(mUrl, iv);
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setView(view);
			return builder.create();
			// return super.onCreateDialog(savedInstanceState);
		}

	// @Override
	// public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	// {
	// View view = inflater.inflate(R.layout.dialog_image, container,false);
	// iv = (ImageView) view.findViewById(R.id.diIv);
	// // mActivity.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
	// new ImageLoader(mActivity).displayImage(mUrl, iv);
	// return view;// super.onCreateView(inflater, container, savedInstanceState);
	// }

	public void show()
		{
			Intent intent = new Intent(mActivity, ImageViewerActivity.class);
			intent.putExtra(ImageViewerActivity.KEY_IMG_URL, mUrl);
			mActivity.startActivity(intent);
			// final String TAG = "dialog";
			// FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
			// Fragment prev = mActivity.getSupportFragmentManager().findFragmentByTag(TAG);
			// if (prev != null)
			// ft.remove(prev);
			// ft.addToBackStack(null);
			//
			// // ft.commit();
			// // Create and show the dialog.
			// show(ft, TAG);
		}
}

// public class ImageDialog extends Fragment
// {
// // FragmentActivity mActivity;
// // String mUrl;
// public static final String EXTRA_IMG_URL = "imgUrl";
// ImageView iv;
//
// // public ImageDialog( FragmentActivity activity, String imageUrl )
// // {
// // mActivity = activity;
// // mUrl = imageUrl;
// // }
//
// // @Override
// // @NonNull
// // public Dialog onCreateDialog(Bundle savedInstanceState)
// // {
// // LayoutInflater inflater = getActivity().getLayoutInflater();
// // View view = inflater.inflate(R.layout.dialog_image, null);
// // iv = (ImageView) view.findViewById(R.id.diIv);
// // mActivity.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
// // AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
// // builder.setView(view);
// // return builder.create();
// // // return super.onCreateDialog(savedInstanceState);
// // }
//
// @Override
// public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
// {
// View view = inflater.inflate(R.layout.dialog_image, null,false);
// iv = (ImageView) view.findViewById(R.id.diIv);
// String imgUrl = getArguments().getString(EXTRA_IMG_URL);
// new ImageLoader(getActivity()).displayImage(imgUrl, iv);
// return view;// super.onCreateView(inflater, container, savedInstanceState);
// }
//
// // public void show()
// // {
// // final String TAG = "dialog";
// // FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
// // Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag(TAG);
// // if (prev != null)
// // ft.remove(prev);
// // ft.addToBackStack(null);
// //
// // AppUtils.replaceFragment(this, getActivity().getSupportFragmentManager(), R.id.amMainLayout);
// // // Create and show the dialog.
// // // show(ft, TAG);
// // }
// }
