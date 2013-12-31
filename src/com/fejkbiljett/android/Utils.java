package com.fejkbiljett.android;

import java.util.Arrays;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager.NameNotFoundException;

public class Utils {
	public static String gAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static String gNumbers = "0123456789";

	public static String generateRandomString() {
		return Utils.generateRandomString(9, false);
	}

	public static String generateRandomString(int length, boolean bLetters) {
		return bLetters ? 
				getRandChars(gAlphabet+gNumbers, length) : 
					getRandChars(gNumbers, length);
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
	
	public static String[] hexAEOX = { "OO", "OX", "OA", "OE", 
		                               "XO", "XX", "XA", "XE", 
		                               "AO", "AX", "AA", "AE", 
		                               "EO", "EX", "EA", "EE" };

	public static String getRandChars(String chars, int length) {
		String randChars = "";
		char[] charArray = chars.toCharArray();
		
		for (int i = 0; i < length; i++) {
			randChars += charArray[(int) Math.floor(Math.random() * charArray.length)];
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
	
	/* For test purposes *
	public static String aeoxToHex(String aeox) {
		String hex = "";
		for (int i=0; i<aeox.length(); i=i+2) {
			for (int j=0; i<hexAEOX.length; j++) {
				if (aeox.substring(i, i+1).equals(hexAEOX[j]))
						hex += hexAEOX[j];
			}
		}
		return hex;
	}
	*/
	
}