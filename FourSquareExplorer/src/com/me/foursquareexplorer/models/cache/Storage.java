package com.me.foursquareexplorer.models.cache;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.LruCache;

public class Storage {

	private static Storage instance;
	private Activity activity;
	private LruCache<String, Object> lruCache;

	private Storage(Activity activity) {

		super();

		this.activity = activity;

		lruCache = new LruCache<String, Object>(1024);
	}

	public static Storage getInstance(Activity activity) {

		if (instance == null) {
			instance = new Storage(activity);

		}

		return instance;
	}

	public LruCache<String, Object> getLruCache() {
		return lruCache;
	}

	public void saveToSharedPrefrence(String key, String value) {

		SharedPreferences sharedPref = activity
				.getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public String getFromSharedPrefrence(String key) {

		SharedPreferences sharedPref = activity
				.getPreferences(Context.MODE_PRIVATE);

		return sharedPref.getString(key, "");

	}

}
