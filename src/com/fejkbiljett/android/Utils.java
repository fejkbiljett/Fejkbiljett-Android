package com.fejkbiljett.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;

public class Utils {
	public static String[] gAlphabet = { "A", "B", "C", "D", "E", "F", "G",
			"H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
			"U", "V", "W", "X", "Y", "Z" };

	public static String getNews(Activity context) {
		try {
			String version = getVersion(context);
			String sId = PreferenceManager.getDefaultSharedPreferences(context)
					.getString("uniqid", "NaN");
			String url = "http://smsticket.fejkbiljett.com/" + version
					+ "/getnews?id=" + sId;

			HttpURLConnection conn = (HttpURLConnection) new URL(url)
					.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));

			char[] buf = new char[1024];
			int read = br.read(buf, 0, 1024);

			if (read < 0) {
				return null;
			}

			String str = new String(buf);
			return str.substring(0, read);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String generateRandomString() {
		return Utils.generateRandomString(9, false);
	}

	public static String generateRandomString(int length, boolean bLetters) {
		String letters = "";

		for (int i = 0; i < length; i++) {
			if (bLetters == true && Math.random() > 0.5) {
				letters += gAlphabet[(int) Math.floor(Math.random() * 26)];
			} else {
				letters += Math.round(Math.random() * 9);
			}
		}

		return letters;
	}

	public static void showAlert(Activity activity, String title, String message) {
		Utils.showAlert(activity, title, message, "OK!");
	}

	public static void showAlert(Activity activity, String title,
			String message, String button) {
		new AlertDialog.Builder(activity)
				.setTitle(title)
				.setMessage(message)
				.setNeutralButton(button,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
							}
						}).show();
	}

	public static String getVersion(Activity context) {
		String version = "";

		try {
			version = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return version;
	}
}
