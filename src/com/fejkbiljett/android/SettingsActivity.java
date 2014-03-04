package com.fejkbiljett.android;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;

public class SettingsActivity extends PreferenceActivity {
	protected ProgressDialog mProgressDialog=null;
	
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		
		Preference permissionButton = (Preference)findPreference("sms_permission");
		Preference updateButton = (Preference)findPreference("update");

		updateButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				upgrade(getApplicationContext());
				return true;
		    }
		});

		if (android.os.Build.VERSION.SDK_INT >= 19) {
			permissionButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference arg0) { 		    
					Intent intent = Utils.getAppOpsIntent();
					startActivity(intent);				
					return true;
			    }
			});
		} else {
			permissionButton.setShouldDisableView(true);
			permissionButton.setEnabled(false);
		}
		
		
		mProgressDialog = new ProgressDialog(SettingsActivity.this);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setMessage(getString(R.string.checking_version));
		mProgressDialog.setCancelable(true);
		
		/* Someone asking for update? */
		if(getIntent().getBooleanExtra("upgrade", false)) {
			upgrade(getApplicationContext());
		}
	}
	
	public void checkVersion(Context context) {
		final UpdateTask updateTask = new UpdateTask(context, false);
		updateTask.execute();
	}
	
	public void upgrade(Context context) {
		final UpdateTask updateTask = new UpdateTask(context, true);
		updateTask.execute();
	}
	
	public static boolean isVersionUpToDate(Context c) {
		int latestVersionCode = c.getSharedPreferences("version_check",MODE_PRIVATE)
									.getInt("latest_version_code", 0);
		return latestVersionCode <= Utils.getVersionCode(c);
	}

	public static boolean markOutOfDate(Context c, ActionBar ab) {
		boolean versionUpToDate = isVersionUpToDate(c);
		if(!versionUpToDate) {
			/* COLORFULL NOTICE */
			ColorDrawable cd = new ColorDrawable();
			cd.setColor(Color.RED);
			ab.setBackgroundDrawable(cd);
		}
		return !versionUpToDate;
	}
	
	public class UpdateTask extends AsyncTask<Void, Integer, Void> {
		
		private Context context;
		private boolean doUpgrade;
		private boolean doneFirstProgressUpdate=false;
		private int latestVersionCode = 0;
		private String latestVersionURL = "";
		private String latestVersion = "";
		private String checkVersionURL = "https://github.com/Olangu/Fejkbiljett-Android/raw/master/README.md";
		private String localBinary = "";
		private boolean versionUpToDate;
		
		public UpdateTask(Context context, boolean doUpgrade) {
			this.doUpgrade = doUpgrade;
			this.context = context;
		}
		@Override
		protected Void doInBackground(Void... params) {
			populate();
			versionUpToDate = latestVersionCode <= Utils.getVersionCode(context);
			if(doUpgrade && latestVersion.length() > 0 && !versionUpToDate)
				localBinary = download(latestVersionURL);
			return null;
		}
		
		protected void onPreExecute() {
			super.onPreExecute();
			if(mProgressDialog != null) {
		        mProgressDialog.setMax(1);
		        if (android.os.Build.VERSION.SDK_INT >= 11) {
		        	mProgressDialog.setProgressNumberFormat("");
		        	mProgressDialog.setProgressPercentFormat(null);
		        }
				mProgressDialog.setMessage(getString(R.string.checking_version));
				mProgressDialog.setIndeterminate(true);
				mProgressDialog.show();
			}
		}

	    @Override
	    protected void onProgressUpdate(Integer... progress) {
	        if(!doneFirstProgressUpdate) { //do once
	        	doneFirstProgressUpdate=true;
	        	if (android.os.Build.VERSION.SDK_INT >= 11) {
	        		mProgressDialog.setProgressNumberFormat("%1d/%2d");
	        		mProgressDialog.setProgressPercentFormat(NumberFormat.getPercentInstance());
	        	}
		        mProgressDialog.setMessage(getString(R.string.downloading_update));
	        	mProgressDialog.setIndeterminate(false);
	        }
	        
	        mProgressDialog.setMax(progress[1]);
	        mProgressDialog.setProgress(progress[0]);
	    }

		
		@Override
	    protected void onPostExecute(Void v) {
			if(mProgressDialog != null) {
				mProgressDialog.dismiss();
			}
			if(latestVersion.length() <= 0) {
				Toast.makeText(context, R.string.settings_version_cant_connect, Toast.LENGTH_LONG).show();
			} else if (localBinary.length() <= 0) {
				Editor editor = context.getSharedPreferences("version_check",Context.MODE_PRIVATE).edit();
				
				editor.putLong("latest_version_check", new Date().getTime());
				editor.putString("latest_version_url", latestVersionURL);
				editor.putString("latest_version", latestVersion);
				editor.putInt("latest_version_code", latestVersionCode);
				
				editor.commit();

				if(versionUpToDate) {
					Toast.makeText(context, R.string.settings_version_up_to_date, Toast.LENGTH_LONG).show();
				} 
			} else {
		        if(localBinary != null) {
		        	Intent promptInstall = new Intent(Intent.ACTION_VIEW)
		        	.setDataAndType(Uri.parse("file://"+localBinary),"application/vnd.android.package-archive");
		        	startActivity(promptInstall);
		        }
			}
		}

		private void populate() {
			HttpsURLConnection urlConnection = null;
			String str = "";
	        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
	        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
	             getClass().getName());
	        wl.acquire();
			try {
				URL url = new URL(checkVersionURL);
				urlConnection = (HttpsURLConnection) url.openConnection();
				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				str = Utils.readStream(in).toString();
				} catch (Exception e) {
					e.printStackTrace();
					return;
				} finally {
					if(urlConnection != null) {
						urlConnection.disconnect();
					}
					wl.release();
				}
			
			if(str.length() <= 0)
			{
				return;
			}
			
			str = str.replace("\n", ";");
			//First URL after "Ladda ned"
			latestVersionURL = str.replaceAll(".*Ladda ned.*?(https://.*?);.*", "$1");
			latestVersion = str.replaceAll(".*Ladda ned.*[*]+Fejkbiljett ([0-9.]+)[*]+.*", "$1");
			latestVersionCode = (int) (Float.parseFloat(latestVersion)*100);
		}
		
		private String download(String strUrl) {
			HttpsURLConnection urlConnection = null;
			FileOutputStream output = null;
	        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
	        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
		             getClass().getName());
	        wl.acquire();
	        String filename = strUrl.substring(strUrl.lastIndexOf('/')+1);
	        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
			try {
				//FIXME check if file exist and give up in that case, not nice to overwrite files silently
				output = new FileOutputStream(path + "/" + filename);
				URL url = new URL(strUrl);
				urlConnection = (HttpsURLConnection) url.openConnection();
				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
	            int fileLength = urlConnection.getContentLength();

				byte data[] = new byte[4096];
		        int count;
		        int total=0;
				publishProgress(total, fileLength);
		        while((count = in.read(data)) != -1) {
		        	if(isCancelled()) {
		        		return "";
		        	}
		        	output.write(data, 0, count);
		        	total += count;
		        	if(fileLength>0) {
	                    publishProgress(total, fileLength);
		        	}
		        		
		        }
		
				} catch (Exception e) {
					Log.e("download", Log.getStackTraceString(e));
					return "";
				} finally {
					if(urlConnection != null) {
						urlConnection.disconnect();
					}
					if(output != null) {
						try {
							output.close();
						} catch (IOException e) {
							Log.e("download", Log.getStackTraceString(e));
						}
					}
					wl.release();						
				}
			return path + "/" + filename;
		}
	}
}
