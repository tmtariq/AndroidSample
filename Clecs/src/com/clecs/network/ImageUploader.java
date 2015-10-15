package com.clecs.network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class ImageUploader
	{
		private static String Tag = "UPLOADER";
		static HttpURLConnection conn;
		public String filename;

		public String uploadImage(File filepath, String urlString, String filename_)
			{
				String response = "";
				filename = filename_;
				try
					{
						String lineEnd = "\r\n";
						String twoHyphens = "--";
						String boundary = "-------------------------acebdf13572468";

						FileInputStream fileInputStream = new FileInputStream(filepath);
						URL url = new URL(urlString);
						// System.setProperty("http.proxyHost", "my.proxyhost.com");
						// System.setProperty("http.proxyPort", "1234");
						// Proxy proxy = new Proxy(Proxy.DIRECT, new InetSocketAddress(proxyHost, proxyPort));
						// url.openConnection(proxy);
						conn = (HttpURLConnection) url.openConnection();
						conn.setConnectTimeout(5 * 60 * 1000);
						conn.setDoInput(true);
						conn.setDoOutput(true);
						conn.setUseCaches(false);
						conn.setRequestMethod("POST");
						conn.setRequestProperty("Connection", "Keep-Alive");
						conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

						DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
						dos.writeBytes(lineEnd + twoHyphens + boundary + lineEnd); // " + pathToOurFile +"
						dos.writeBytes("Content-Disposition: form-data; name=\"Filedata\"" + ";filename=" + "\"" + filename + ".jpeg\"" + lineEnd);
						dos.writeBytes("Content-Type: image/JPEG" + lineEnd + lineEnd);

						Log.e(Tag, "Headers are written");
						int bytesAvailable = fileInputStream.available();
						int maxBufferSize = 10000;
						byte[] buffer = new byte[bytesAvailable];
						int bytesRead = fileInputStream.read(buffer, 0, bytesAvailable);
						while (bytesRead > 0)
							{
								dos.write(buffer, 0, bytesAvailable);
								bytesAvailable = fileInputStream.available();
								bytesAvailable = Math.min(bytesAvailable, maxBufferSize);
								bytesRead = fileInputStream.read(buffer, 0, bytesAvailable);
							}
						dos.writeBytes(lineEnd);
						dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
						System.out.println(twoHyphens + boundary + twoHyphens + lineEnd);
						Log.e(Tag, "File is written" + filename);
						fileInputStream.close();
						dos.flush();
						dos.close();
						response = appendString();
					}
				catch (Exception e)
					{
						e.printStackTrace();
					}
				return response;
			}

		public static String appendString()
			{
				String line = "";
				StringBuilder sb = new StringBuilder();
				BufferedReader rd = null;
				try
					{
						rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
						while ((line = rd.readLine()) != null)
							{
								sb.append(line + "\n");
							}
						rd.close();
					}
				catch (IOException e)
					{
						e.printStackTrace();
					}
				return sb.toString();
			}
	}
