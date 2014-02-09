package com.fejkbiljett.android.generators;

import java.util.Date;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.fejkbiljett.android.R;
import com.fejkbiljett.android.SettingsActivity;
import com.fejkbiljett.android.Utils;
import com.fejkbiljett.android.tickets.Ticket;
import com.fejkbiljett.android.tickets.Ticket.TicketException;

public abstract class TicketGeneratorActivity extends SherlockFragmentActivity
		implements ITicketGenerator, OnSharedPreferenceChangeListener {
	protected String mCityName = "Fejkbiljett";

	public void onGenerate() {
		Ticket mTicket = getTicket();

		try {
			mTicket.create(getParams());
		} catch (TicketException e) {
			// Utils.showAlert(this, "Oops!", e.getClass() +"\n"+ e.getMessage()
			// +"\n"+
			// e.getStackTrace()[0].getFileName()+":"+e.getStackTrace()[0].getLineNumber());
			Utils.showAlert(this, "Oops!", e.getMessage());
			e.printStackTrace();
			return;
		}

		if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
				"create_sms_out", false)) {
			String message = mTicket.getMessageOut();
			String number = mTicket.getNumberOut();

			ContentValues values = new ContentValues();
			values.put("address", number);
			values.put("body", message);

			// x/1000 seconds ago
			values.put("date", new java.util.Date().getTime() - (60)*1000);

			getContentResolver()
					.insert(Uri.parse("content://sms/sent"), values);
			
			/* 
			 * Fix for incorrect thread timestamp.
			 * "when you delete an SMS from a con­ver­sa­tion, 
			 * it has to go up­date the time­stamp based on the most re­cent mes­sage."
			 *  -http://www.tbray.org/ongoing/When/201x/2012/02/29/Undocumentedness#p-3
			 */
			
			//insert duplicate message, and delete it.
			getContentResolver().delete(
					getContentResolver().insert(
							Uri.parse("content://sms/sent"), values), null, null);
		}

		String message = mTicket.getMessage();
		String sender = mTicket.getSender();

		ContentValues values = new ContentValues();
		values.put("address", sender);
		values.put("body", message);
		getContentResolver().insert(Uri.parse("content://sms/inbox"), values);

		if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
				"open_sms_on_create", false)) {
			Intent smsIntent = new Intent(Intent.ACTION_MAIN);
			smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
			smsIntent.setType("vnd.android-dir/mms-sms");
			startActivity(smsIntent);
		}
		
		//Notification to user
		Toast.makeText(getApplicationContext(), R.string.ticket_created, Toast.LENGTH_SHORT).show();
		
		finish(); //exit
		}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case R.id.menuitem_update:
			intent = new Intent(this, SettingsActivity.class);
			intent.putExtra("update", true);
			startActivity(intent);
			return true;
		case R.id.menuitem_create:
			onGenerate();
			return true;
		case R.id.menuitem_changecity:
		case android.R.id.home:
			setResult(1);
			finish();
			return true;
		case R.id.menuitem_settings:
			intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActionBar ab = getSupportActionBar();
		ab.setTitle(mCityName);

		ab.setDisplayHomeAsUpEnabled(true);
		ab.setDisplayShowHomeEnabled(true);
		
		getSharedPreferences("version_check",MODE_PRIVATE).registerOnSharedPreferenceChangeListener(this);
	}

	@SuppressWarnings("deprecation")
	protected void onResume() {
		super.onResume();
		
		/* Check for updates */
		if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
				"auto_update", false))
		{
			Date lastCheck = new Date(getSharedPreferences("version_check",MODE_PRIVATE)
					 				  .getLong("latest_version_check", 0));
			Date todayMidnight = new Date();
			todayMidnight.setHours(0);
			todayMidnight.setMinutes(0);
			todayMidnight.setSeconds(0);
			if(lastCheck.before(todayMidnight)) {
				new SettingsActivity().update(getApplicationContext());
			}				
		}	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.city, menu);
		if(!SettingsActivity.markOutOfDate(getApplicationContext(), getActionBar())) {
			menu.findItem(R.id.menuitem_update).setEnabled(false).setVisible(false);
		}
		return true;
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
		if(!SettingsActivity.isVersionUpToDate(getApplicationContext())) {
			invalidateOptionsMenu();
		}
	}	
}
