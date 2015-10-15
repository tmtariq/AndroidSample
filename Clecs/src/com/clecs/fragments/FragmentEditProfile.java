package com.clecs.fragments;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.clecs.R;
import com.clecs.imageCroper.InternalStorageContentProvider;
import com.clecs.network.RequestHandler;
import com.clecs.network.RequestHandler.ServerResponseListner;
import com.clecs.utils.AppStatics;
import com.clecs.utils.AppUtils;
import com.clecs.utils.Session;
import com.nostra13.universalimageloader.core.ImageLoader;

import eu.janmuller.android.simplecropimage.CropImage;

public class FragmentEditProfile extends Fragment implements
		ServerResponseListner {
	public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
	private static final String TAG = "FragmentEditProfile";
	final int REQ_CAMERA = 100;
	final int REQ_GALLERY = 200;
	final int REQ_CROP_IMAGE = 300;
	final int REQ_DP = 400;
	final int REQ_NAME = 500;
	View root;
	ImageView ivUser;
	EditText etName;
	public static Button btnSave, btnCancel;

	String imagePath;
	Uri outputFileUri;
	AlertDialog.Builder dialog;
	private File mFileTemp;
	boolean isDpChanged;
	RequestHandler requestHandler;
	ProgressDialog pd;

	// TO UPDATE IMAGE IN DRAWER
	public static boolean isProfilePicUpdated;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (root == null) {
			root = inflater.inflate(R.layout.fragment_eidt_profile, container,
					false);
			initViews();
			AppUtils.setFont(getActivity(), (ViewGroup) root);
		}
		return root;
		// return super.onCreateView(inflater, container, savedInstanceState);
	}

	void initViews() {
		requestHandler = new RequestHandler(this, true);
		pd = new ProgressDialog(getActivity());
		pd.setCancelable(false);

		ivUser = (ImageView) root.findViewById(R.id.fepIv);
		etName = (EditText) root.findViewById(R.id.fepEtName);
		btnCancel = (Button) root.findViewById(R.id.fepBtnCancel);
		btnSave = (Button) root.findViewById(R.id.fepBtnSave);

		etName.setText(Session.myProfileInfo.getProfileName());
		ImageLoader.getInstance().displayImage(
				Session.myProfileInfo.getProfileImage(), ivUser);

		ivUser.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showChosser();
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getFragmentManager().popBackStack();
			}
		});
		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean isNameChanged = !etName.getText().toString()
						.equals(Session.myProfileInfo.getProfileName());
				if (isNameChanged || isDpChanged) {
					if (isNameChanged) {
						if (etName.getText().toString().trim().length() < 2) {
							// your name is too short
							etName.setError("eich enw yn rhy fyr");
							//etName.setError("Enter some name");
						} else {
							try {
								JSONObject object = new JSONObject();
								object.put("newName", etName.getText()
										.toString());
								pd.setMessage("Diweddaru...");
								//pd.setMessage("updating profile");
								pd.show();
								requestHandler.makePostRequest(
										AppStatics.URL_CHANGE_USER_DETAIL,
										object, REQ_NAME);

							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
					if (isDpChanged) {
						new ImageUploadingTask(mFileTemp, "userDp");
					}
				} else {
					getFragmentManager().popBackStack();
				}
			}
		});

		initDir();
	}

	public void showChosser() {
		dialog = new AlertDialog.Builder(getActivity());
		
		//dialog.setMessage("Get your photo");
		// add image to your post
		dialog.setMessage("Ychwanegu delwedd at eich swydd");
		
		// gallery - we are calling it Choose Existing
		dialog.setPositiveButton("Dewis presennol",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent photoPickerIntent = new Intent(
								Intent.ACTION_PICK);
						photoPickerIntent.setType("image/*");
						startActivityForResult(photoPickerIntent, REQ_GALLERY);
						dialog.dismiss();
					}
				});
		// camera - we are calling it Take Photo
		dialog.setNegativeButton("Cymryd llun",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						takePicture();
						dialog.dismiss();
					}
				});
		dialog.create().show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode != Activity.RESULT_OK) {
			return;
		}

		Bitmap bitmap;

		switch (requestCode) {

		case REQ_GALLERY:

			try {

				InputStream inputStream = getActivity().getContentResolver()
						.openInputStream(data.getData());
				FileOutputStream fileOutputStream = new FileOutputStream(
						mFileTemp);
				copyStream(inputStream, fileOutputStream);
				fileOutputStream.close();
				inputStream.close();

				startCropImage();

			} catch (Exception e) {
				Log.e(TAG, "Error while creating temp file", e);
			}
			break;

		case REQ_CAMERA:
			startCropImage();
			break;

		case REQ_CROP_IMAGE:
			String path = data.getStringExtra(CropImage.IMAGE_PATH);
			if (path == null)
				return;

			bitmap = BitmapFactory.decodeFile(mFileTemp.getPath());
			ivUser.setImageBitmap(bitmap);
			isDpChanged = true;
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public static void copyStream(InputStream input, OutputStream output)
			throws IOException {

		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = input.read(buffer)) != -1) {
			output.write(buffer, 0, bytesRead);
		}
	}

	void initDir() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			mFileTemp = new File(Environment.getExternalStorageDirectory(),
					TEMP_PHOTO_FILE_NAME);
		} else {
			mFileTemp = new File(getActivity().getFilesDir(),
					TEMP_PHOTO_FILE_NAME);
		}
	}

	private void takePicture() {

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		try {
			Uri mImageCaptureUri = null;
			String state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state)) {
				mImageCaptureUri = Uri.fromFile(mFileTemp);
			} else {
				mImageCaptureUri = InternalStorageContentProvider.CONTENT_URI;
			}
			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
					mImageCaptureUri);
			intent.putExtra("return-data", true);
			startActivityForResult(intent, REQ_CAMERA);
		} catch (ActivityNotFoundException e) {

			Log.d(TAG, "cannot take picture", e);
		}
	}

	private void startCropImage() {

		Intent intent = new Intent(getActivity(), CropImage.class);
		intent.putExtra(CropImage.IMAGE_PATH, mFileTemp.getPath());
		intent.putExtra(CropImage.SCALE, true);

		intent.putExtra(CropImage.ASPECT_X, 2);
		intent.putExtra(CropImage.ASPECT_Y, 2);

		startActivityForResult(intent, REQ_CROP_IMAGE);
	}

	@Override
	public void onResponse(String response, long requestCode) {
		if (requestCode == REQ_NAME) {
			//AppUtils.showToast("name updated " + response);
			AppUtils.showToast("enw newid " + response);
			
			
			Session.myProfileInfo.setProfileName(etName.getText().toString());
			updateUserNameLocally(etName.getText().toString());
			openUserProfile();
		}
		pd.dismiss();
	}

	void updateUserNameLocally(String userName) {
		for (int i = 0; i < Session.listAllPost.size(); i++) {
			if (Session.listAllPost.get(i).isMyPost())
				Session.listAllPost.get(i).setCreatedByName(userName);
		}
		Session.myProfileInfo.setProfileName(userName);
	}

	@Override
	public void onError(String error, String description, long requestCode) {
		AppUtils.showToast(description);
		pd.dismiss();
	}

	@Override
	public void onNoInternet() {
		pd.dismiss();
	}

	class ImageUploadingTask extends AsyncTask<String, Integer, String> {
		File filePath;
		String fileName;

		public ImageUploadingTask(File filepath, String fileName) {
			this.filePath = filepath;
			this.fileName = fileName;

			this.execute("");
		}

		@Override
		protected void onPreExecute() {
			//pd.setMessage("Uploading image...");
			pd.setMessage("Llwytho...");
			pd.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			String imgUrl = null;
			imgUrl = new com.clecs.imageUploader.ImageUploader(getActivity(),
					mFileTemp.getPath(), AppStatics.URL_UPLOAD_USER_DP)
					.postImage();
			return imgUrl;
		}

		@Override
		protected void onPostExecute(String result) {
			pd.dismiss();
			// String imgUrl =
			// "https://clecs.blob.core.windows.net/profileimages/" +
			// result.replaceAll("\"", "");
			// ImageLoader.getInstance().clearMemoryCache();;
			Session.myProfileInfo.setProfileImage(result);
			isProfilePicUpdated = true;
			openUserProfile();
			super.onPostExecute(result);
		}
	}

	void openUserProfile() {
		FragmentUser fragmentUser = new FragmentUser(
				Session.myProfileInfo.getUserName(),
				Session.myProfileInfo.getProfileImage(), true);
		AppUtils.replaceFragment(fragmentUser, getFragmentManager(),
				R.id.amMainLayout, false);
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
