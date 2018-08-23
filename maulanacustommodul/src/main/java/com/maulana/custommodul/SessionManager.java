package com.maulana.custommodul;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

public class SessionManager {
	// Shared Preferences
	SharedPreferences pref;
	
	// Editor for Shared preferences
	Editor editor;
	
	// Context
	Context context;
	
	// Shared pref mode
	int PRIVATE_MODE = 0;
	
	// Sharedpref file name
	private static final String PREF_NAME = "GmediaUserPSPReseller";
	
	// All Shared Preferences Keys
	private static final String IS_LOGIN = "IsLoggedIn";
	public static final String TAG_USERNAME = "username"; // nomor
	public static final String TAG_KDCUS = "kdcus";
	public static final String TAG_NAMA = "nama";
	public static final String TAG_ALAMAT = "alamat";
	public static final String TAG_IMAGE = "image";
	public static final String TAG_SAVED = "saved";

	// Constructor
	public SessionManager(Context context){
		this.context = context;
		pref = this.context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}
	
	/**
	 * Create login session
	 * */
	public void createLoginSession(String kdcus, String username, String nama, String alamat, String image, String saved){

		editor.putBoolean(IS_LOGIN, true);
		
		editor.putString(TAG_USERNAME, username);

		editor.putString(TAG_KDCUS, kdcus);

		editor.putString(TAG_NAMA, nama);

		editor.putString(TAG_ALAMAT, alamat);

		editor.putString(TAG_IMAGE, image);

		editor.putString(TAG_SAVED, saved); // value is 0 or 1

		// commit changes
		editor.commit();
	}
	
	/**
	 * Get stored session data
	 * */
	public HashMap<String, String> getUserDetails(){
		HashMap<String, String> user = new HashMap<String, String>();

		user.put(TAG_USERNAME, pref.getString(TAG_USERNAME, ""));

		user.put(TAG_KDCUS, pref.getString(TAG_KDCUS, ""));

		user.put(TAG_NAMA, pref.getString(TAG_NAMA, ""));

		user.put(TAG_ALAMAT, pref.getString(TAG_ALAMAT, ""));

		user.put(TAG_IMAGE, pref.getString(TAG_IMAGE, ""));

		user.put(TAG_SAVED, pref.getString(TAG_SAVED, ""));

		// return user
		return user;
	}

	public String getUserInfo(String key){
		return pref.getString(key, "");
	}

	public String getUsername(){
		return pref.getString(TAG_USERNAME, "");
	}

	public String getKdcus(){
		return pref.getString(TAG_KDCUS, "");
	}

	public String getNama(){
		return pref.getString(TAG_NAMA, "");
	}

	public String getAlamat(){
		return pref.getString(TAG_ALAMAT, "");
	}

	public String getImage(){
		return pref.getString(TAG_IMAGE, "");
	}

	/**
	 * Clear session details
	 * */
	public void logoutUser(Intent logoutIntent){

		// Clearing all data from Shared Preferences
		try {
			editor.clear();
			editor.commit();
		}catch (Exception e){
			e.printStackTrace();
		}

		logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(logoutIntent);
		((Activity)context).finish();
		((Activity)context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}
	
	/**
	 * Quick check for login
	 * **/
	// Get Login State
	public boolean isLoggedIn(){
		if(getUserDetails().get(TAG_KDCUS) != null && !getUserDetails().get(TAG_KDCUS).equals("")){
			return true;
		}else{
			return false;
		}
		/*return pref.getBoolean(IS_LOGIN, false);*/
	}

	public boolean isSaved(){
		if(getUserDetails().get(TAG_SAVED) != null && getUserDetails().get(TAG_SAVED).equals("1")){

			return true;
		}else{
			return false;
		}
	}

}
