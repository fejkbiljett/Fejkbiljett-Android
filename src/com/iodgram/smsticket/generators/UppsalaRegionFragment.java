package com.iodgram.smsticket.generators;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.actionbarsherlock.app.SherlockFragment;
import com.iodgram.smsticket.R;
import com.iodgram.smsticket.tickets.Ticket;
import com.iodgram.smsticket.tickets.UppsalaRegionTicket;

public class UppsalaRegionFragment extends SherlockFragment implements
		ITicketGenerator {

	protected String mSharedPrefs = "UppsalaRegionFragment";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.ticket_fragment_uppsalaregion, null);
	}

	@Override
	public Bundle getParams() {
		Bundle data = new Bundle();

		boolean bReduced = ((RadioButton) getView().findViewById(
				R.id.price_reduced)).isChecked();
		data.putBoolean("price_reduced", bReduced);

		int zones[] = { R.id.region_zone_urban, R.id.region_zone_1,
				R.id.region_zone_2, R.id.region_zone_2p, R.id.region_zone_12,
				R.id.region_zone_12p, R.id.region_zone_p,
				R.id.region_zone_ulsl, R.id.region_zone_bike, };
		int zone = 0;

		for (int i = 0; i < zones.length; i++) {
			if (((RadioButton) getView().findViewById(zones[i])).isChecked()) {
				zone = zones[i];
				break;
			}
		}

		data.putInt("zone", zone);

		return data;
	}

	@Override
	public void onResume() {
		super.onResume();

		SharedPreferences prefs = getActivity().getSharedPreferences(
				mSharedPrefs, Context.MODE_PRIVATE);

		if (prefs.getBoolean("price_reduced", false)) {
			((RadioButton) getView().findViewById(R.id.price_reduced))
					.setChecked(true);
		}

		int zone = prefs.getInt("zone", 0);
		if (zone > 0) {
			View view = getView().findViewById(zone);
			if (view != null && view instanceof RadioButton) {
				((RadioButton) view).setChecked(true);
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		Editor editor = getActivity().getSharedPreferences(mSharedPrefs,
				Context.MODE_PRIVATE).edit();
		Bundle data = getParams();

		editor.putBoolean("price_reduced", data.getBoolean("price_reduced"));
		editor.putInt("zone", data.getInt("zone"));

		editor.commit();
	}

	@Override
	public Ticket getTicket() {
		return new UppsalaRegionTicket();
	}
}
