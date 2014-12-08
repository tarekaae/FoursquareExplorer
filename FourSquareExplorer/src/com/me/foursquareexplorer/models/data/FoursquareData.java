package com.me.foursquareexplorer.models.data;

import android.content.Context;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.me.foursquareexplorer.R;
import com.me.foursquareexplorer.controller.AppController;
import com.me.foursquareexplorer.models.cache.Storage;
import com.me.foursquareexplorer.models.constants.Constants;

public class FoursquareData {

	private static FoursquareData instance;
	private String CLIENT_ID;
	private String CLIENT_SECRET;
	private Context context;

	private FoursquareData(Context context) {
		super();
		this.context = context;
		CLIENT_ID = context.getResources().getString(
				R.string.FourSquare_Client_id);
		CLIENT_SECRET = context.getResources().getString(
				R.string.FourSquare_Client_secret);

	}

	public static FoursquareData getInstance(Context context) {

		if (instance == null) {
			instance = new FoursquareData(context);
		}

		return instance;
	}

	public void getNearByPlaces(double lat, double lng,
			Response.Listener<String> listener,
			Response.ErrorListener errorListener) {

		String search_url = Constants.SEARCH_API_URL + "?ll=" + lat + "," + lng
				+ "&client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET
				+ "&v=20140806&m=foursquare";

		Storage preferences = Storage.getInstance(AppController
				.getInstance().getCurrentActivity());

		preferences.saveToSharedPrefrence(Constants.SEARCH_API_URL_KEY,
				search_url);

		// This tag will be used to cancel the request
		String tag_string_req = "string_req";

		//
		try {
			StringRequest strReq = new StringRequest(Method.GET, search_url,
					listener, errorListener);

			// Adding request to request queue
			AppController.getInstance().addToRequestQueue(strReq,
					tag_string_req);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
