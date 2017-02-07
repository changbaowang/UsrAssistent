package com.usr.assistent;

import android.content.Context;
import android.content.SharedPreferences;

public class AndroidSharedPreferences {
	private static boolean isInit = false ;
	private static SharedPreferences sharePreference;
	private static SharedPreferences.Editor editor;
	public final static String PREF_NAME = "setting";
    
	/**
	 * init AndroidSharedPreferences ,must be called before use
	 * AndroidSharedPreferences
	 * @param context
	 */
	public static void init(Context context) {
		if(isInit)
			return ;
		sharePreference = context.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
		editor = sharePreference.edit();
		isInit = true ;
	}

	public static void putBoolean(String key, boolean value) {
		editor.putBoolean(key, value);
		editor.commit();
	}

	public static void putString(String key, String value) {
		editor.putString(key, value);
		editor.commit();
	}
	
	public static void putLong(String key ,long value){
		editor.putLong(key, value);
		editor.commit();
	}

	public static void putInt(String key, int value) {
		editor.putInt(key, value);
		editor.commit();
	}
	
	public static boolean getBoolean(String key,boolean defaultValue){
		 return sharePreference.getBoolean(key, defaultValue);
	}
	
	public static String getString(String key,String defValue){
		return sharePreference.getString(key, defValue);
	}
	
	public static int getInt(String key,int defValue){
		return sharePreference.getInt(key, defValue);
	}
	
	public static long getLong(String key,long defValue){
		return sharePreference.getLong(key, defValue);
	}
}
