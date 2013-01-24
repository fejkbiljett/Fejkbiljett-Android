package com.iodgram.smsticket.generators;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.iodgram.smsticket.FragmentAdapter;
import com.iodgram.smsticket.R;
import com.iodgram.smsticket.tickets.Ticket;
import com.viewpagerindicator.TabPageIndicator;

public class UppsalaActivity extends TicketGeneratorActivity {
	protected ViewPager mPager;
	protected FragmentAdapter mAdapter;
	
	int ticket_type = 0;

	public void onCreate(Bundle savedInstanceState) {
		mCityName = "Uppsala";
		
		setContentView(R.layout.ticket_uppsala);
		super.onCreate(savedInstanceState);
		
		mPager = (ViewPager) findViewById(R.id.pager);
		mAdapter = new FragmentAdapter(
				getSupportFragmentManager());
		mPager.setAdapter(mAdapter);

		mAdapter.addPage("Stadsbussar", new UppsalaFragment());
		mAdapter.addPage("Regiontrafik", new UppsalaRegionFragment());

		TabPageIndicator pi = (TabPageIndicator) findViewById(R.id.indicator);
		pi.setViewPager(mPager);

		SharedPreferences prefs = getPreferences(MODE_PRIVATE);

		/*
		if (prefs.getBoolean("price_reduced", false)) {
			((RadioButton) findViewById(R.id.price_reduced)).setChecked(true);
		}
		if (prefs.getBoolean("price_reduced_region", false)) {
			((RadioButton) findViewById(R.id.price_reduced_region))
					.setChecked(true);
		}

		String zone = prefs.getString("zone", "");
		if (zone.equals("urban")) {
			((RadioButton) findViewById(R.id.region_zone_urban))
					.setChecked(true);
		} else if (zone.equals("1")) {
			((RadioButton) findViewById(R.id.region_zone_1)).setChecked(true);
		} else if (zone.equals("2")) {
			((RadioButton) findViewById(R.id.region_zone_2)).setChecked(true);
		} else if (zone.equals("2p")) {
			((RadioButton) findViewById(R.id.region_zone_2p)).setChecked(true);
		} else if (zone.equals("12")) {
			((RadioButton) findViewById(R.id.region_zone_12)).setChecked(true);
		} else if (zone.equals("12p")) {
			((RadioButton) findViewById(R.id.region_zone_12p)).setChecked(true);
		} else if (zone.equals("p")) {
			((RadioButton) findViewById(R.id.region_zone_p)).setChecked(true);
		} else if (zone.equals("ulsl")) {
			((RadioButton) findViewById(R.id.region_zone_ulsl))
					.setChecked(true);
		} else if (zone.equals("bike")) {
			((RadioButton) findViewById(R.id.region_zone_bike))
					.setChecked(true);
		}
		*/
	}

	@Override
	public Bundle getParams() {
		return ((ITicketGenerator) mAdapter.getItem(mPager.getCurrentItem())).getParams();

		/*
		Editor editor = getPreferences(MODE_PRIVATE).edit();

		boolean bReduced = false;
		switch (ticket_type) {
		case 0:
			
			break;
		case 1:
			bReduced = ((RadioButton) findViewById(R.id.price_reduced_region))
					.isChecked();

			String zone;
			

			editor.putString("zone", zone);
			data.putString("zone", zone);
			break;
		}

		editor.putBoolean("price_reduced_region", bReduced);
		data.putBoolean("price_reduced", bReduced);
		editor.commit();

		return data;
		*/
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		
		mPager.setCurrentItem(prefs.getInt("tab_position", 0));
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		Editor editor = getPreferences(MODE_PRIVATE).edit();
		
		editor.putInt("tab_position", mPager.getCurrentItem());

		editor.commit();
	}

	@Override
	public Ticket getTicket() {
		return ((ITicketGenerator) mAdapter.getItem(mPager.getCurrentItem())).getTicket();
	}
}
