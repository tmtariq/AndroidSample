package com.clecs.fragments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.clecs.LoginActivity;
import com.clecs.R;
import com.clecs.imageCroper.InternalStorageContentProvider;
import com.clecs.network.RequestHandler;
import com.clecs.network.RequestHandler.ServerResponseListner;
import com.clecs.objects.Post;
import com.clecs.utils.AppStatics;
import com.clecs.utils.AppUtils;
import com.clecs.utils.Session;
import com.clecs.widget.MyAutoCompleteTextView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class FragmentNewPost extends Fragment implements ServerResponseListner {
	public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
	private static final String TAG = "FragmentNewPost";
	final int REQ_CAMERA = 100;
	final int REQ_GALLERY = 200;
	static final int REQ_POST = 300;
	final int REQ_QUOTE = 400;
	final int TEXT_MAX_LENGTH = 200;
	private File mFileTemp;

	View root;
	Button btnImg, btnPost, btnRemoveImg;
	MyAutoCompleteTextView etPost;
	ImageView ivPost, ivUser;
	TextView tvCount;

	String imagePath;
	Uri outputFileUri;

	ProgressDialog pd;
	AlertDialog.Builder dialog;
	boolean isImagePost;
	RequestHandler handler;

	Post mPost;
	boolean isQuote;

	public FragmentNewPost() {
		// TODO Auto-generated constructor stub
	}

	public FragmentNewPost(boolean isQuote, Post post) {
		this.isQuote = isQuote;
		mPost = post;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (root == null) {
			root = inflater.inflate(R.layout.fragment_new_post, container,
					false);
			initViews();
			setListner();
			AppUtils.setFont(getActivity(), (ViewGroup) root);
		}
		return root;
		// return super.onCreateView(inflater, container, savedInstanceState);
	}

	void initViews() {
		handler = new RequestHandler(this, true);
		tvCount = (TextView) root.findViewById(R.id.fnpTvCount);
		etPost = (MyAutoCompleteTextView) root.findViewById(R.id.fnpEtPost);
		etPost.setHorizontallyScrolling(false);
		
		TextView theUsername = (TextView) root.findViewById(R.id.usernameOfPoster);
		theUsername.setText("@" + Session.myProfileInfo.getUserName());
		
		// etPost.setMinLines(5);
		// etPost.setMaxLines(10);
		AppUtils.openKeyboard(this.getActivity());
		etPost.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (etPost.length() > TEXT_MAX_LENGTH) {
					tvCount.setTextColor(root.getResources().getColor(
							R.color.gray));
					btnPost.setEnabled(false);
				} else {
					tvCount.setTextColor(root.getResources().getColor(
							R.color.RedLine));
					btnPost.setEnabled(true);
				}

				tvCount.setText(String.valueOf((TEXT_MAX_LENGTH - s.length())));

			}
		});

		btnImg = (Button) root.findViewById(R.id.fnpBtnAddImg);
		btnRemoveImg = (Button) root.findViewById(R.id.fnpBtnRemoveImg);
		btnPost = (Button) root.findViewById(R.id.fnpBtnPost);
		ivPost = (ImageView) root.findViewById(R.id.fnpIvPost);
		ivUser = (ImageView) root.findViewById(R.id.fnpIvUser);

		pd = new ProgressDialog(getActivity());
		pd.setMessage("Postio...");
		//pd.setMessage("Posting...");
		pd.setCancelable(false);
		if (Session.myProfileInfo != null)
			ImageLoader.getInstance().displayImage(
					Session.myProfileInfo.getProfileImage(), ivUser);

		initDir();
		if (mPost != null) {
			if (isQuote) {
				etPost.setText("\"" + mPost.getCreatedByUsernameWithHat()
						+ " : " + mPost.getPostTextOriginal() + "\"");
				if (mPost.isContainsImage()) {
					ImageLoader.getInstance().displayImage(mPost.getImageUrl(),
							ivPost);
					btnImg.setVisibility(View.GONE);
					btnRemoveImg.setVisibility(View.GONE);
				}
			} else {

			}
		} else if (LoginActivity.imgShare != null) {
			readStreamFromPhone(LoginActivity.imgShare);
			setPostImage();
		}
	}

	void setListner() {
		btnImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				initDir();
				showChosser();
			}
		});
		btnRemoveImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				clearImage();
			}
		});
		btnPost.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (etPost.getText().toString().trim().length() == 0) {
					//etPost.setError("Enter some text to post");
					etPost.setError("Teipiwch rhywfaint o destun i bostio");
				} else {
					handler = new RequestHandler(FragmentNewPost.this, true);
					// List<NameValuePair> nameValuePairs = new
					// ArrayList<NameValuePair>(2);
					// nameValuePairs.add(new BasicNameValuePair("postText",
					// etPost.getText().toString()));
					try {
						JSONObject obj = new JSONObject();
						obj.put("postText", etPost.getText().toString());
						// nameValuePairs.add(new BasicNameValuePair("password",
						// etPasssword.getText().toString()));
						if (isQuote) {
							// "postText": "sample string 1",
							// "quotedPostId": 2,
							// "containsImage": true,
							// "imageUrl": "sample string 4"
							obj.put("quotedPostId", mPost.getPostId());
							obj.put("containsImage", mPost.isContainsImage());
							if (mPost.isContainsImage())
								obj.put("imageUrl",
										getLastBitFromUrl(mPost.getImageUrl()));
							if (!isImagePost)
								handler.makePostRequest(
										AppStatics.URL_QUOTE_POST, obj,
										REQ_QUOTE);
							else {
								if (AppUtils.isInternetConeected(getActivity())) {
									pd.show();
									new ImageUploadingTask(mFileTemp,
											"postImage");// new File(imagePath),
															// "postImage");
								} else
								{
									//AppUtils.showToast("Internet not connected");
									AppUtils.showToast("Credwch eich bod yn all-lein");
								}
							}

						} else if (!isImagePost) {
							pd.show();
							handler.makePostRequest(AppStatics.URL_POST_A_POST,
									obj, REQ_POST);
						} else {
							if (AppUtils.isInternetConeected(getActivity())) {
								pd.show();
								new ImageUploadingTask(mFileTemp, "postImage");// new
																				// File(imagePath),
																				// "postImage");
							} else
							{
								//AppUtils.showToast("Internet not connected");
								AppUtils.showToast("Credwch eich bod yn all-lein");
							}
						}
						AppUtils.closeKeyboard(getActivity());
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	public static String getLastBitFromUrl(final String url) {
		// return url.replaceFirst("[^?]*/(.*?)(?:\\?.*)","$1);" <-- incorrect
		return url.replaceFirst(".*/([^/?]+).*", "$1");
	}

	public void showChosser() {
		dialog = new AlertDialog.Builder(getActivity());
		
		
		// add image
		dialog.setMessage("Ychwanegu delwedd");
		//dialog.setMessage("Get your photo");
		
		// Gallery - we use choose exising
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
		// camera - we use Take Photo
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
	public void onActivityResult(int requestCode, int resultCode,
			Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
		if (resultCode != Activity.RESULT_OK)
			return;

		switch (requestCode) {

		case REQ_GALLERY:

			try {
				readStreamFromPhone(imageReturnedIntent.getData());
				setPostImage();
			} catch (Exception e) {
				clearImage();
				AppUtils.showToast("Memory low");
				Log.e(TAG, "Error while creating temp file", e);
			} catch (OutOfMemoryError e) {
				clearImage();
				// btnRemoveImg.setVisibility(View.GONE);
				AppUtils.showToast("Image size is too big, Memory low");
			}

			break;
		case REQ_CAMERA:
			setPostImage();
			break;

		}
	}

	void readStreamFromPhone(Uri uri) {
		try {
			InputStream inputStream = getActivity().getContentResolver()
					.openInputStream(uri);
			FileOutputStream fileOutputStream = new FileOutputStream(mFileTemp);
			copyStream(inputStream, fileOutputStream);
			fileOutputStream.close();
			inputStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressLint("NewApi")
	private void setPostImage() {
		try {
			Bitmap bitmap;
			String path = mFileTemp.getPath();// imageReturnedIntent.getStringExtra(CropImage.IMAGE_PATH);
			if (path == null)
				return;

			bitmap = getRotatedBitmap(mFileTemp.getPath());
			ivPost.setImageBitmap(bitmap);
			// ivPost.setBackground(new BitmapDrawable(getResources(),bitmap));
			isImagePost = true;

			btnImg.setVisibility(View.GONE);
			btnRemoveImg.setVisibility(View.VISIBLE);
		} catch (Exception e) {
			clearImage();
			AppUtils.showToast("Memory low");
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			clearImage();
			AppUtils.showToast("Image size is too big, Memory low");
		}

	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFromResource(String filePath,
			int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filePath, options);
	}

	public static Bitmap getRotatedBitmap(String filePath) {
		// Bitmap bitmap = BitmapFactory.decodeFile(filePath);
		Bitmap bitmap = decodeSampledBitmapFromResource(filePath, 1000, 1000);
		ExifInterface ei = null;
		try {
			ei = new ExifInterface(filePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
				ExifInterface.ORIENTATION_NORMAL);
		float angle = 0;
		switch (orientation) {
		case ExifInterface.ORIENTATION_ROTATE_90:
			angle = 90;
			break;
		case ExifInterface.ORIENTATION_ROTATE_180:
			angle = 180;
			break;
		// etc.
		}

		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
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
			// we just say loading... no uploading text yet
			pd.setMessage("Llwytho...");
			pd.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			String imgUrl = null;

			imgUrl = new com.clecs.imageUploader.ImageUploader(getActivity(),
					mFileTemp.getPath(), AppStatics.URL_UPLOAD_IMAGE)
					.postImage();

			return imgUrl;
		}

		@Override
		protected void onPostExecute(String result) {
			pd.dismiss();
			if (result == null) {
				//AppUtils.showToast("Image uploading failed");
				// this just says somethign went wrong please try again
				AppUtils.showToast("Aeth rhywbeth o'i le, os gwelwch yn dda ceisiwch eto");
				return;
			}

			// List<NameValuePair> nameValuePairs = new
			// ArrayList<NameValuePair>(2);
			// nameValuePairs.add(new BasicNameValuePair("postText",
			// etPost.getText().toString()));
			// nameValuePairs.add(new BasicNameValuePair("containsImage",
			// "true"));
			// nameValuePairs.add(new BasicNameValuePair("imageUrl", result));
			sharePost(result);
			super.onPostExecute(result);
		}
	}

	void sharePost(String url) {
		JSONObject obj = new JSONObject();
		try {
			pd.show();
			obj.put("postText", etPost.getText().toString());
			obj.put("containsImage", true);
			obj.put("imageUrl", url.replaceAll("\"", ""));
			if (isQuote) {
				obj.put("quotedPostId", mPost.getPostId());
				handler.makePostRequest(AppStatics.URL_QUOTE_POST, obj,
						REQ_QUOTE);
			} else
				handler.makePostRequest(AppStatics.URL_POST_A_POST, obj,
						REQ_POST);
		} catch (JSONException e) {
			e.printStackTrace();
			pd.dismiss();
		}
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
				/*
				 * The solution is taken from here:
				 * http://stackoverflow.com/questions
				 * /10042695/how-to-get-camera-result-as-a-uri-in-data-folder
				 */
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

	void clearImage() {
		ivPost.setImageBitmap(null);
		isImagePost = false;
		btnImg.setVisibility(View.VISIBLE);
		btnRemoveImg.setVisibility(View.GONE);
	}

	@Override
	public void onResponse(String response, long requestCode) {
		//AppUtils.showToast("Success");
		AppUtils.showToast("Llwyddiant");
		pd.dismiss();
		if (requestCode == REQ_POST || requestCode == REQ_QUOTE) {
			//AppUtils.showToast("Post published");
			try {
				backToMain();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onError(String error, String description, long requestCode) {
		pd.dismiss();
	}

	@Override
	public void onNoInternet() {
		pd.dismiss();
	}

	void backToMain() {
		clearImage();
		if (LoginActivity.imgShare != null) {
			LoginActivity.imgShare = null;
			getActivity().finish();
		} else {
			//AppUtils.replaceFragment(new FragmentPostsMain(), getFragmentManager(), R.id.amMainLayout);
			FragmentPostsMain.instance.refreshPosts();
			getFragmentManager().popBackStack();
		}
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