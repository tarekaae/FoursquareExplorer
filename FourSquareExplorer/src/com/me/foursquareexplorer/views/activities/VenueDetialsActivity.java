package com.me.foursquareexplorer.views.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.foursquare.android.nativeoauth.FoursquareCancelException;
import com.foursquare.android.nativeoauth.FoursquareDenyException;
import com.foursquare.android.nativeoauth.FoursquareInvalidRequestException;
import com.foursquare.android.nativeoauth.FoursquareOAuth;
import com.foursquare.android.nativeoauth.FoursquareOAuthException;
import com.foursquare.android.nativeoauth.FoursquareUnsupportedVersionException;
import com.foursquare.android.nativeoauth.model.AccessTokenResponse;
import com.foursquare.android.nativeoauth.model.AuthCodeResponse;
import com.me.foursquareexplorer.R;
import com.me.foursquareexplorer.controller.AppController;
import com.me.foursquareexplorer.models.beans.Venue;
import com.me.foursquareexplorer.models.cache.Storage;
import com.me.foursquareexplorer.models.constants.Constants;

public class VenueDetialsActivity extends Activity {
	private static final int REQUEST_CODE_FSQ_CONNECT = 200;
	private static final int REQUEST_CODE_FSQ_TOKEN_EXCHANGE = 201;
	private Venue venue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_venue_detials);

		venue = getVenueDetials();

		TextView tv1 = (TextView) findViewById(R.id.textView1);

		tv1.setText(venue.getName());

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_FSQ_CONNECT:
			onCompleteConnect(resultCode, data);
			break;

		case REQUEST_CODE_FSQ_TOKEN_EXCHANGE:
			onCompleteTokenExchange(resultCode, data);
			break;

		default:
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	private void onCompleteConnect(int resultCode, Intent data) {
		AuthCodeResponse codeResponse = FoursquareOAuth.getAuthCodeFromResult(
				resultCode, data);
		Exception exception = codeResponse.getException();

		if (exception == null) {
			// Success.
			String code = codeResponse.getCode();
			performTokenExchange(code);

		} else {
			if (exception instanceof FoursquareCancelException) {
				// Cancel.
				toastMessage(this, "Canceled");

			} else if (exception instanceof FoursquareDenyException) {
				// Deny.
				toastMessage(this, "Denied");

			} else if (exception instanceof FoursquareOAuthException) {
				// OAuth error.
				String errorMessage = exception.getMessage();
				String errorCode = ((FoursquareOAuthException) exception)
						.getErrorCode();
				toastMessage(this, errorMessage + " [" + errorCode + "]");

			} else if (exception instanceof FoursquareUnsupportedVersionException) {
				// Unsupported Fourquare app version on the device.
				toastError(this, exception);

			} else if (exception instanceof FoursquareInvalidRequestException) {
				// Invalid request.
				toastError(this, exception);

			} else {
				// Error.
				toastError(this, exception);
			}
		}
	}

	private void onCompleteTokenExchange(int resultCode, Intent data) {
		AccessTokenResponse tokenResponse = FoursquareOAuth.getTokenFromResult(
				resultCode, data);
		Exception exception = tokenResponse.getException();

		if (exception == null) {
			String accessToken = tokenResponse.getAccessToken();
			// Success.
			// toastMessage(this, "Access token: " + accessToken);

			// Persist the token for later use. In this example, we save
			// it to shared prefs.
			// ExampleTokenStore.get().setToken(accessToken);
			Storage.getInstance(this).getLruCache()
					.put(Constants.ACCESS_TOKEN, accessToken);

			Log.i("___token", accessToken);

			doCheckin(accessToken);

		} else {
			if (exception instanceof FoursquareOAuthException) {
				// OAuth error.
				String errorMessage = ((FoursquareOAuthException) exception)
						.getMessage();
				String errorCode = ((FoursquareOAuthException) exception)
						.getErrorCode();
				toastMessage(this, errorMessage + " [" + errorCode + "]");

			} else {
				// Other exception type.
				toastError(this, exception);
			}
		}
	}

	private void doCheckin(String token) {
		String tag_string_req = "str";
		String venueID = venue.getId();

		String checkin_url = Constants.CHECK_IN_URL + "?oauth_token=" + token
				+ "&venueId=" + venueID + "&v=20130822";

		try {
			StringRequest strReq = new StringRequest(Method.POST, checkin_url,
					new Response.Listener<String>() {

						@Override
						public void onResponse(String response) {
							Log.d("onResponse", response.toString());

							toastMessage(VenueDetialsActivity.this,
									"checked in successfully");
						}
					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							VolleyLog.d("onErrorResponse",
									"Error: " + error.getMessage());
							toastMessage(VenueDetialsActivity.this,
									"checked in Failed");

						}
					});

			// Adding request to request queue
			AppController.getInstance().addToRequestQueue(strReq,
					tag_string_req);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void performTokenExchange(String code) {
		Intent intent = FoursquareOAuth.getTokenExchangeIntent(this,
				getResources().getString(R.string.FourSquare_Client_id),
				getResources().getString(R.string.FourSquare_Client_secret),
				code);
		startActivityForResult(intent, REQUEST_CODE_FSQ_TOKEN_EXCHANGE);
	}

	private Venue getVenueDetials() {

		Venue selectedVenue = null;

		LruCache<String, Object> cache = Storage.getInstance(this)
				.getLruCache();

		String venueName = (String) cache.get(Constants.SELECTED_PLACE_KEY);

		Object allPlaces = cache.get(Constants.ALL_PLACES_KEY);

		ArrayList<Venue> venues = (ArrayList<Venue>) allPlaces;

		Venue venue = null;
		for (int i = 0; i < venues.size(); i++) {

			venue = venues.get(i);
			if (venue != null) {

				String name = venue.getName();

				if (name.equalsIgnoreCase(venueName)) {

					selectedVenue = venue;

					break;
				}
			}

		}
		return selectedVenue;
	}

	public void checkin(View v) {
		// Start the native auth flow.
		Intent intent = FoursquareOAuth.getConnectIntent(
				VenueDetialsActivity.this,
				getResources().getString(R.string.FourSquare_Client_id));

		// If the device does not have the Foursquare app installed, we'd
		// get an intent back that would open the Play Store for download.
		// Otherwise we start the auth flow.
		if (FoursquareOAuth.isPlayStoreIntent(intent)) {
			toastMessage(VenueDetialsActivity.this,
					getResources()
							.getString(R.string.app_not_installed_message));
			startActivity(intent);
		} else {
			startActivityForResult(intent, REQUEST_CODE_FSQ_CONNECT);
		}
	}

	public static void toastMessage(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	public static void toastError(Context context, Throwable t) {
		Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
	}

}
