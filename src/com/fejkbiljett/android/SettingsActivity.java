package com.fejkbiljett.android;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

public class SettingsActivity extends PreferenceActivity {
	protected ProgressDialog mProgressDialog=null;
	protected boolean mUpdateAfterVersionCheck=false;
	
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		
		Preference permissionButton = (Preference)findPreference("sms_permission");
		Preference updateButton = (Preference)findPreference("update");

		updateButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				mUpdateAfterVersionCheck=true;
				mProgressDialog.show();
				update(getApplicationContext());
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
		
		if(getIntent().getBooleanExtra("update", false)) {
			update(getApplicationContext());
		}
	}
	
	public void update(Context c) {
		final CheckVersionTask checkVersionTask = new CheckVersionTask(c);
		checkVersionTask.execute();
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
	
	public class GetUpdateTask extends AsyncTask<Void, Integer, String> {		
	    private Context context;
	    
	    public GetUpdateTask(Context c) {
	    	context = c;
	    }
		@Override
		protected String doInBackground(Void... params) {
			String url = context.getSharedPreferences("version_check",Context.MODE_PRIVATE).getString("latest_version_url", "");
			return download(url);
		}
		
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
			mProgressDialog.setMessage(getString(R.string.downloading_update));
			mProgressDialog.setIndeterminate(true);
	        mProgressDialog.show();
	    }

	    @Override
	    protected void onProgressUpdate(Integer... progress) {
	        super.onProgressUpdate(progress);
	        // if we get here, length is known, now set indeterminate to false
	        mProgressDialog.setIndeterminate(false);
	        mProgressDialog.setMax(progress[1]);
	        mProgressDialog.setProgress(progress[0]);
	    }
	    
		@Override
	    protected void onPostExecute(String filePath) {
	        mProgressDialog.dismiss();
	        
	        if(filePath != null) {
	        	Intent promptInstall = new Intent(Intent.ACTION_VIEW)
	        	.setDataAndType(Uri.parse("file://"+filePath),"application/vnd.android.package-archive");
	        	startActivity(promptInstall);
	        }
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
				output = new FileOutputStream(path + "/" + filename);
				URL url = new URL(strUrl);
				urlConnection = (HttpsURLConnection) url.openConnection();
				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
	            int fileLength = urlConnection.getContentLength();
	            
				byte data[] = new byte[4096];
		        int count;
		        int total=0;
		        while((count = in.read(data)) != -1) {
		        	if(isCancelled()) {
		        		return null;
		        	}
		        	output.write(data, 0, count);
		        	total += count;
		        	if(fileLength>0) {
	                    publishProgress(total, fileLength);
		        	}
		        		
		        }
		
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				} finally {
					if(urlConnection != null) {
						urlConnection.disconnect();
					}
					if(output != null) {
						try {
							output.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					wl.release();						
				}
			return path + "/" + filename;
		}
	}
	
	public class CheckVersionTask extends AsyncTask<Void, Void, Void> {
		
		private Context context;
		private int latestVersionCode = 0;
		private String latestVersionURL = "";
		private String latestVersion = "";
		private String checkVersionURL = "https://github.com/Olangu/Fejkbiljett-Android/raw/master/README.md";

		public CheckVersionTask(Context c) {
			context = c;
		}
		@Override
		protected Void doInBackground(Void... params) {
			populate();
			return null;
		}
		
		protected void onPreExecute() {
			super.onPreExecute();
			if(mProgressDialog != null) {
				mProgressDialog.setMessage(getString(R.string.checking_version));
				mProgressDialog.setIndeterminate(true);
			}
		}
		
		@Override
	    protected void onPostExecute(Void v) {
			if(mProgressDialog != null) {
				mProgressDialog.dismiss();
			}
			if(latestVersion.isEmpty()) {
				Toast.makeText(context, R.string.settings_version_cant_connect, Toast.LENGTH_LONG).show();
			} else {
				Boolean versionUpToDate = latestVersionCode <= Utils.getVersionCode(context);
				Editor editor = context.getSharedPreferences("version_check",Context.MODE_PRIVATE).edit();
				
				editor.putLong("latest_version_check", new Date().getTime());
				editor.putString("latest_version_url", latestVersionURL);
				editor.putString("latest_version", latestVersion);
				editor.putInt("latest_version_code", latestVersionCode);
				
				editor.commit();

				if(versionUpToDate) {
					Toast.makeText(context, R.string.settings_version_up_to_date, Toast.LENGTH_LONG).show();
				} else if(mUpdateAfterVersionCheck) {
					final GetUpdateTask updateTask = new GetUpdateTask(context);
					updateTask.execute();
						
					mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				    	@Override
				    	public void onCancel(DialogInterface dialog) {
				        	updateTask.cancel(true);
				    	}
					});
				}
			}
			mUpdateAfterVersionCheck=false;
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
			
			if(str.isEmpty())
			{
				return;
			}
			
			str = str.replace("\n", ";");
			//First URL after "Ladda ned"
			latestVersionURL = str.replaceAll(".*Ladda ned.*?(https://.*?);.*", "$1");
			latestVersion = str.replaceAll(".*Ladda ned.*[*]+Fejkbiljett ([0-9.]+)[*]+.*", "$1");
			latestVersionCode = (int) (Float.parseFloat(latestVersion)*100);
		}
	}
}
