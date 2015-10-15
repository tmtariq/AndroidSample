package com.clecs.imageUploader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.util.Log;

import com.clecs.utils.AppPref;

/**
 * This class is responsible for uploading data
 * 
 * @author lauro
 * 
 */
public class ImageUploader {

	/**
	 * 
	 */
	private Activity mainActivity;
	String imagePath = "";
	String url;

	/**
	 * @param mainActivity
	 */
	public ImageUploader(Activity mainActivity, String imgPath, String url) {
		this.mainActivity = mainActivity;
		imagePath = imgPath;
		this.url = url;

		// postImage();
	}

	public String postImage() {

		try {

			//InputStream inputStream = new FileInputStream(new File(imagePath));

			// *** CONVERT INPUTSTREAM TO BYTE ARRAY

			//byte[] data = this.convertToByteArray(inputStream);
			HttpClient httpClient = getHttpClient();
			httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
					System.getProperty("http.agent"));

			HttpPost httpPost = new HttpPost(url);
			httpPost.setHeader("Authorization", AppPref.getInstance()
					.getHeader());
			// STRING DATA
			// StringBody dataString = new
			// StringBody("This is the sample image");

			// FILE DATA OR IMAGE DATA
			//InputStreamBody inputStreamBody = new InputStreamBody(
				//	new ByteArrayInputStream(data), "testName");

			// MultipartEntity multipartEntity = new MultipartEntity();
			CustomMultiPartEntity multipartEntity = new CustomMultiPartEntity();

			// SET UPLOAD LISTENER
			// multipartEntity.setUploadProgressListener(this);

			// *** ADD THE FILE
			//multipartEntity.addPart("file", inputStreamBody);
			multipartEntity.addPart("file", new FileBody(new File(imagePath)));

			// *** ADD STRING DATA
			// multipartEntity.addPart("description", dataString);

			httpPost.setEntity(multipartEntity);
			// httpPost.setEntity(multipartEntity);

			// EXECUTE HTTPPOST
			HttpResponse httpResponse = httpClient.execute(httpPost);

			// THE RESPONSE FROM SERVER
			String stringResponse = EntityUtils.toString(httpResponse
					.getEntity());

			// DISPLAY RESPONSE OF THE SERVER
			Log.d("data from server", stringResponse);
			return stringResponse;

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();

			return null;

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			return null;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			return null;
		}

	}

	HttpClient getHttpClient() {
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		// The default value is zero, that means the timeout is not used.
		int timeoutConnection = 90000;
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				timeoutConnection);
		// Set the default socket timeout (SO_TIMEOUT)
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 90000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

		DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
		return httpClient;
	}

	/**
		 * 
		 */
	// @Override
	// public void transferred(long num)
	// {
	//
	// // COMPUTE DATA UPLOADED BY PERCENT
	//
	// long dataUploaded = ((num / 1024) * 100);// /
	// this.mainActivity.imageSize;
	//
	// // PUBLISH PROGRESS
	//
	// this.publishProgress((int) dataUploaded);
	//
	// }

	/**
	 * Convert the InputStream to byte[]
	 * 
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	private byte[] convertToByteArray(InputStream inputStream)
			throws IOException {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		int next = inputStream.read();
		while (next > -1) {
			bos.write(next);
			next = inputStream.read();
		}

		bos.flush();

		return bos.toByteArray();
	}

	// @Override
	// protected void onProgressUpdate(Integer... values)
	// {
	// super.onProgressUpdate(values);
	//
	// // UPDATE THE PROGRESS DIALOG
	//
	// // this.mainActivity.progressDialog.setProgress(values[0]);
	//
	// }

	// @Override
	// protected void onPostExecute(String uploaded)
	// {
	// // TODO Auto-generated method stub
	// super.onPostExecute(uploaded);
	//
	// if (uploaded)
	// {
	//
	// // UPLOADING DATA SUCCESS
	//
	// // this.mainActivity.progressDialog.dismiss();
	// Toast.makeText(this.mainActivity, "File Uploaded",
	// Toast.LENGTH_SHORT).show();
	//
	// }
	// else
	// {
	//
	// // UPLOADING DATA FAILED
	//
	// // this.mainActivity.progressDialog.setMessage("Uploading Failed");
	// // this.mainActivity.progressDialog.setCancelable(true);
	//
	// }
	//
	// }

}