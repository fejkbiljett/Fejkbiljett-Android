package com.fejkbiljett.android;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.fejkbiljett.android.generators.GoteborgActivity;
import com.fejkbiljett.android.generators.StockholmActivity;
import com.fejkbiljett.android.generators.UmeaActivity;
import com.fejkbiljett.android.generators.UppsalaActivity;

public class FejkbiljettActivity extends SherlockListActivity {
	@SuppressWarnings("rawtypes")
	Class[] classes = { StockholmActivity.class, UppsalaActivity.class,
			GoteborgActivity.class, UmeaActivity.class, };
	

	Integer[] images = new Integer[] { R.drawable.stockholm,
			R.drawable.uppsala };

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		ActionBar ab = getSupportActionBar();
		ab.setTitle(getString(R.string.app_name) + " " + Utils.getVersion(this));

		ImageAdapter adapter = new ImageAdapter(this, images);
		setListAdapter(adapter);

		init();
	}

	public void init() {
		int selected_city = getPreferences(0).getInt("selected_city", -1);

		if (selected_city >= 0 && selected_city < classes.length) {
			Intent intent = new Intent(this, classes[selected_city]);
			startActivityForResult(intent, 0);
		}
	}

	public void onListItemClick(ListView lv, View v, int pos, long id) {
		if (pos >= classes.length) {
			return;
		}

		Editor prefs = getPreferences(0).edit();
		prefs.putInt("selected_city", pos);
		prefs.commit();

		Intent intent = new Intent(this, classes[pos]);
		// startActivityForResult(intent, 0);
		startActivity(intent);
	}

	public void onActivityResult(int request, int result, Intent data) {
		switch (request) {
		case 0:
			switch (result) {
			case 1:
				break;
			default:
				finish();
			}
			break;
		}
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuitem_settings:
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.main, menu);

		return true;
	}
}