package com.fejkbiljett.android;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.Context;

public class Utils {
	public static String gAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static String gNumbers = "0123456789";
	private static Random rnd = new Random();

	public static String generateRandomString() {
		return Utils.generateRandomString(9, false);
	}

	public static String generateRandomString(int length, boolean bLetters) {
		return bLetters ? 
				getRandChars(gAlphabet+gNumbers, length) : 
					getRandChars(gNumbers, length);
	}

	/* Generates a number sequence between 1 000 000 000 000
	 * and 280 999 999 999 999, where lowest number is 13 chars
	 * and highest number does not overflow 12 char hexcode.
	 * ( 280 999 999 999 999 = ff91692e8fff )
	 */
	public static String generateNumberSequence() {
		return (rnd.nextInt(280)+1) + getRandChars(gNumbers,12);		
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
	
	public static int getVersionCode(Context context) {
		int version=0;

		try {
			version = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return version;
	}
	
	public static String[] hexAEOX = { "OO", "OX", "OA", "OE", 
		                               "XO", "XX", "XA", "XE", 
		                               "AO", "AX", "AA", "AE", 
		                               "EO", "EX", "EA", "EE" };

	public static String getRandChars(String chars, int length) {
		String randChars = "";
		char[] charArray = chars.toCharArray();
		
		for (int i = 0; i < length; i++) {
			randChars += charArray[rnd.nextInt(charArray.length)]; //nextInt is non-inclusive of top value
		}
		return randChars;
	}
	
	public static String decToHex(String dec) {
		return decToHexLen(dec, 0);
	}
	
	public static String decToHexLen(String dec, int len) {
		String hex = Long.toHexString((Long.parseLong(dec)));

		//Expect a certain length of the hex-string
		if(len > 0) {
			if(hex.length() > len) {
				throw new StringIndexOutOfBoundsException();
			}
		
		    while(hex.length() < len) {
				hex = "0" + hex;
			}
		}
		return hex;
	}
	
	public static String hexToAEOX(String hex) {
		String AEOX = "";
		for (int i = 0; i < hex.length(); i++) {
			AEOX += hexAEOX[Integer.parseInt(String.valueOf(hex.charAt(i)), 16)];
		}
		return AEOX;
	}
	
	public static String aeoxScannerBlock(String dec) {
		String sAEOX = hexToAEOX(decToHexLen(dec,12));
		
		return    "E" + sAEOX.substring(0, 9) + "\n"
				+ "E" + sAEOX.substring(9, 18) + "\n"
				+ "E" + sAEOX.substring(18, 24) + getRandChars("AEOX", 3) + "\n"
				+ "EEEEEEEEEE";
	}
	
	/* For test purposes */
	public static String aeoxToHex(String aeox) {
		String hex = "";
		for (int i=0; i<aeox.length(); i=i+2) {
			for (int j=0; j<hexAEOX.length; j++) {
				if (aeox.substring(i, i+2).equals(hexAEOX[j]))
						hex += (gNumbers+gAlphabet).charAt(j);
			}
		}
		return hex;
	}
	
	public static Intent getAppOpsIntent() {
		Intent intent = new Intent();
		intent.setClassName("com.android.settings", "com.android.settings.Settings");
		intent.setAction(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		intent.putExtra(":android:show_fragment", "com.android.settings.applications.AppOpsSummary");
		return intent;
	}
}