package com.fejkbiljett.android;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.net.ssl.HttpsURLConnection;
import java.net.URL;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

public class CheckVersion extends AsyncTask<Context, Void, Context> {
	private static int latestVersionCode = 0;
	private static String latestVersionURL = "";
	private static String latestVersion = "";
	private static String checkVersionURL = "https://github.com/Olangu/Fejkbiljett-Android/raw/master/README.md";
	
	@Override
	protected Context doInBackground(Context... params) {
		populate();
		return params[0];
	}
	
	@Override
    protected void onPostExecute(Context c) {
		if(latestVersion.isEmpty()) {
			Toast.makeText(c, R.string.settings_version_cant_connect, Toast.LENGTH_LONG).show();
		} else {
			if(latestVersionCode > Utils.getVersionCode(c)) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(latestVersionURL));
				browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				c.startActivity(browserIntent);
			} else {
				Toast.makeText(c, R.string.settings_version_up_to_date, Toast.LENGTH_LONG).show();
			}
		}		
	}

	private void populate() {
		String str = "";
		try {
			URL url = new URL(checkVersionURL);
			HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
			//urlConnection.setConnectTimeout(100);
			//urlConnection.setReadTimeout(500);
			InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			str = readStream(in);
			urlConnection.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		
		if(str.isEmpty())
		{
			return;
		}
		
		str = str.replace("\n", ";");
		//First URL after "Ladda ned"
		latestVersionURL = str.replaceAll(".*Ladda ned.*?(http://.*?);.*", "$1");
		latestVersion = str.replaceAll(".*Ladda ned.*[*]+Fejkbiljett ([0-9.]+)[*]+.*", "$1");
		latestVersionCode = (int) (Float.parseFloat(latestVersion)*100);
	}
	
	private static String readStream(InputStream is) {
	    try {
	      ByteArrayOutputStream bo = new ByteArrayOutputStream();
	      int i = is.read();
	      while(i != -1) {
	        bo.write(i);
	        i = is.read();
	      }
	      return bo.toString();
	    } catch (IOException e) {
	      return "";
	    }
	}
}