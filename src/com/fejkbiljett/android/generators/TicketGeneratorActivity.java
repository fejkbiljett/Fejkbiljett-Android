package com.fejkbiljett.android.generators;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

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
		implements ITicketGenerator {
	protected String mCityName = "SMS-Ticket";

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
		}

		String message = mTicket.getMessage();
		String sender = mTicket.getSender();

		ContentValues values = new ContentValues();
		values.put("address", sender);
		values.put("body", message);
		getContentResolver().insert(Uri.parse("content://sms/inbox"), values);

		Intent smsIntent = new Intent(Intent.ACTION_MAIN);
		smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
		smsIntent.setType("vnd.android-dir/mms-sms");
		startActivity(smsIntent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuitem_create:
			onGenerate();
			return true;
		case R.id.menuitem_changecity:
		case android.R.id.home:
			setResult(1);
			finish();
			return true;
		case R.id.menuitem_settings:
			Intent intent = new Intent(this, SettingsActivity.class);
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.city, menu);

		return true;
	}
}
