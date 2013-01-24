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
import com.iodgram.smsticket.tickets.UppsalaTicket;

public class UppsalaFragment extends SherlockFragment implements
		ITicketGenerator {

	protected String mSharedPrefs = "UppsalaFragment";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.ticket_fragment_uppsala, null);
	}

	@Override
	public Bundle getParams() {
		Bundle data = new Bundle();

		View view = getView();
		RadioButton radio = (RadioButton) view.findViewById(R.id.price_reduced);

		data.putBoolean("price_reduced", radio.isChecked());

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
	}

	@Override
	public void onPause() {
		super.onPause();

		Editor editor = getActivity().getSharedPreferences(mSharedPrefs,
				Context.MODE_PRIVATE).edit();
		Bundle data = getParams();

		editor.putBoolean("price_reduced", data.getBoolean("price_reduced"));

		editor.commit();
	}

	@Override
	public Ticket getTicket() {
		return new UppsalaTicket();
	}
}
