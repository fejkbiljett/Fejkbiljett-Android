package com.fejkbiljett.android.generators;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

		{
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(this);
			Editor editor = prefs.edit();
			long time = System.currentTimeMillis() / 1000;

			if (time - 600 > prefs.getLong("news_updated", 0)) {
				editor.putString("news_text", "-");
				editor.putString("news_url", "-");
				editor.putLong("news_updated", 0);
				editor.commit();
			}
		}

		final TextView text_news = (TextView) findViewById(R.id.text_news);

		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					Utils.showAlert(
							TicketGeneratorActivity.this,
							"Obsolete",
							"Denna applikation börjar bli gammal och kommer snart sluta fungera. "
									+ "Besök http://fejkbiljett.com för senaste versionen.");
					break;
				case 2:
					new AlertDialog.Builder(TicketGeneratorActivity.this)
							.setTitle("Out of Date")
							.setMessage(
									"Denna applikation är gammal och fungerar inte längre. "
											+ "Besök http://fejkbiljett.com för att uppdatera!")
							.setNeutralButton("OK!",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											TicketGeneratorActivity.this
													.finish();
										}
									}).show();
					return;
				case 4:
					text_news.setText("Could not fetch news from server...");
					return;
				}

				Bundle data = msg.getData();
				String text = data.getString("text");
				String url = data.getString("url");

				text_news.setText(text);

				Editor prefs = PreferenceManager.getDefaultSharedPreferences(
						TicketGeneratorActivity.this).edit();
				prefs.putString("news_text", text);
				prefs.putString("news_url", url);
				prefs.putString("news_version",
						Utils.getVersion(TicketGeneratorActivity.this));
				prefs.putLong("news_updated", System.currentTimeMillis() / 1000);
				prefs.commit();
			}
		};

		if (text_news != null) {
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(TicketGeneratorActivity.this);
			String text = prefs.getString("news_text", "-");

			if (!text.equals("-")
					&& prefs.getString("news_version", "0") == Utils
							.getVersion(this)) {
				text_news.setText(text);
				return;
			}

			Thread t = new Thread() {
				public void run() {
					String news = Utils.getNews(TicketGeneratorActivity.this);
					if (news == null) {
						handler.sendEmptyMessage(4);
						return;
					}

					String[] aStr = news.split("\r");
					String text = "", url = "";
					int what;

					if (aStr[0].equals("obsolete")) {
						if (aStr.length >= 2) {
							text = aStr[1];
						}
						if (aStr.length >= 3) {
							url = aStr[2];
						}

						what = 1;
					} else if (aStr[0].equals("outofdate")) {
						what = 2;
					} else {
						text = aStr[0];

						if (aStr.length >= 2) {
							url = aStr[1];
						}

						what = 3;
					}

					Bundle bundle = new Bundle();
					bundle.putString("text", text);
					bundle.putString("url", url);
					Message msg = new Message();
					msg.what = what;
					msg.setData(bundle);
					msg.setTarget(handler);
					msg.sendToTarget();
				}
			};
			t.start();
		}
	}

	public void click_news(View v) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		String url = prefs.getString("news_url", "");

		if (url.equals("")) {
			return;
		}

		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		startActivity(i);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.city, menu);

		return true;
	}
}
