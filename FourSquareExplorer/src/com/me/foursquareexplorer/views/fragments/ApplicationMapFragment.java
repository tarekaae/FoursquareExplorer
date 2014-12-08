package com.me.foursquareexplorer.views.fragments;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Cache.Entry;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.LocationSource.OnLocationChangedListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.me.foursquareexplorer.R;
import com.me.foursquareexplorer.controller.AppController;
import com.me.foursquareexplorer.models.beans.Category;
import com.me.foursquareexplorer.models.beans.Icon;
import com.me.foursquareexplorer.models.beans.Location;
import com.me.foursquareexplorer.models.beans.NearByPlacesResponse;
import com.me.foursquareexplorer.models.beans.Venue;
import com.me.foursquareexplorer.models.cache.Storage;
import com.me.foursquareexplorer.models.constants.Constants;
import com.me.foursquareexplorer.models.data.FoursquareData;
import com.me.foursquareexplorer.views.activities.VenueDetialsActivity;

public class ApplicationMapFragment extends MapFragment implements
		OnMyLocationButtonClickListener, OnInfoWindowClickListener,
		OnLocationChangedListener, LocationListener,
		android.location.LocationListener {

	private LocationManager locationManager;
	private FragmentActivity hostActivity;
	private GoogleMap appMap;
	private ProgressDialog pDialog;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		hostActivity = (FragmentActivity) getActivity();
		appMap = getMap();
		appMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
		appMap.setMyLocationEnabled(true);
		appMap.setOnMyLocationButtonClickListener(this);
		appMap.setOnInfoWindowClickListener(this);

		android.location.Location location = getLastKnownLocation();
		if (location != null) {
			appMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
					location.getLatitude(), location.getLongitude()), 14.0f));
		}
		//
		pDialog = new ProgressDialog(hostActivity);
		pDialog.setMessage(getResources().getString(R.string.loading));
		pDialog.setCancelable(false);

		checkIfLocationEnabled();

	}

	private void checkIfLocationEnabled() {
		locationManager = (LocationManager) hostActivity
				.getSystemService(Context.LOCATION_SERVICE);
		boolean enabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		// check if enabled and if not send user to the GSP settings
		// Better solution would be to display a dialog and suggesting to
		// go to the settings
		if (!enabled) {

			showOpenLocationDialog();

		} else {
			// can get location
			// show loading and get location
			showProgressDialog();

			android.location.Location location = getLastKnownLocation();

			if (location != null) {

				double lat = location.getLatitude();
				double lng = location.getLongitude();

				refershData(lat, lng);
			} else {

			}

		}
	}

	@Override
	public boolean onMyLocationButtonClick() {

		// show loading and get location

		showProgressDialog();
		android.location.Location location = getLastKnownLocation();

		if (location != null) {

			double lat = location.getLatitude();
			double lng = location.getLongitude();

			refershData(lat, lng);
		} else {
			checkIfLocationEnabled();
		}
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	 
		showProgressDialog();

		// show loading and get location
		showProgressDialog();
		Criteria criteria = new Criteria();
		locationManager = (LocationManager) hostActivity
				.getSystemService(Context.LOCATION_SERVICE);
		String provider = locationManager.getBestProvider(criteria, false);

		// Register the listener with the Location Manager to receive location
		// updates
		locationManager.requestLocationUpdates(provider, 0, 0, this);

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void showProgressDialog() {
		if (!pDialog.isShowing())
			pDialog.show();
	}

	private void hideProgressDialog() {
		if (pDialog.isShowing())
			pDialog.hide();
	}

	private void refershData(double lat, double lng) {

		appMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,
				lng), 15.0f));

		Activity currentActivity = AppController.getInstance()
				.getCurrentActivity();
		Storage preferences = Storage.getInstance(currentActivity);
		String cachedURl = preferences
				.getFromSharedPrefrence(Constants.SEARCH_API_URL_KEY);

		Cache cache = AppController.getInstance().getRequestQueue().getCache();
		Entry entry = cache.get(cachedURl);
		if (entry != null) {
			try {
				String data = new String(entry.data, "UTF-8");
				// handle data, like converting it to json

				handleNearByResponse(data);

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

		} else {
			// Cached response doesn't exists. Make network call here

			FoursquareData.getInstance(hostActivity).getNearByPlaces(lat, lng,
					new Response.Listener<String>() {

						@Override
						public void onResponse(String response) {
							Log.d("onResponse", response.toString());
							handleNearByResponse(response);

						}
					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							VolleyLog.d("onErrorResponse",
									"Error: " + error.getMessage());
							hideProgressDialog();
						}
					});

		}

	}

	private void handleNearByResponse(String response) {

		Gson gson = new Gson();

		NearByPlacesResponse nearByPlacesResponse = gson.fromJson(response,
				NearByPlacesResponse.class);

		ArrayList<Venue> venues = (ArrayList<Venue>) nearByPlacesResponse
				.getResponse().getVenues();

		drawAllVenues(venues);

		Storage.getInstance(hostActivity).getLruCache()
				.put(Constants.ALL_PLACES_KEY, venues);
	}

	private void drawAllVenues(ArrayList<Venue> Venues) {

		Venue venue = null;
		for (int i = 0; i < Venues.size(); i++) {

			venue = Venues.get(i);
			if (venue != null) {
				try {
					drawVenue(venue);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		hideProgressDialog();
	}

	private void drawVenue(Venue venue) throws Exception {

		final MarkerOptions options = new MarkerOptions();
		String name = "";
		if (venue.getName() != null) {
			name = venue.getName();
		}
		options.title(name);

		Location location = venue.getLocation();

		if (location != null) {

			double lat = location.getLat();
			double lng = location.getLng();
			LatLng latLng = new LatLng(lat, lng);
			options.position(latLng);

		}
		// get icon url
		String iconURL = null;

		ArrayList<Category> categories = (ArrayList<Category>) venue
				.getCategories();
		if (categories != null) {
			Category category = categories.get(0);
			if (category != null) {
				Icon icon = category.getIcon();
				iconURL = icon.getPrefix() + "bg_32" + icon.getSuffix();
				// bg_32 is work around because foursquare apis has something
				// couldn't be
				// understood
				// https://github.com/rigoneri/syte/issues/231
				if (iconURL != null) {

					ImageLoader imageLoader = AppController.getInstance()
							.getImageLoader();

					imageLoader.get(iconURL, new ImageListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
						}

						@Override
						public void onResponse(ImageContainer response,
								boolean arg1) {
							if (response.getBitmap() != null) {

								Bitmap bitmap = response.getBitmap();

								options.icon(BitmapDescriptorFactory
										.fromBitmap(bitmap));
								appMap.addMarker(options);

							}
						}
					});

				}

			}

		}

	}

	@Override
	public void onInfoWindowClick(Marker arg0) {
		displayToast(arg0.getTitle() + "clicked");

		Storage.getInstance(hostActivity).getLruCache()
				.put(Constants.SELECTED_PLACE_KEY, arg0.getTitle());

		startActivity(new Intent(hostActivity, VenueDetialsActivity.class));

	}

	private void displayToast(String str) {
		Toast.makeText(hostActivity, str, Toast.LENGTH_SHORT).show();
	}

	private void showOpenLocationDialog() {

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				hostActivity);

		// set title
		alertDialogBuilder.setTitle("Turn Location on");

		// set dialog message
		alertDialogBuilder
				.setMessage(
						"please turn on location services and make sure you are connected to the internet to start getting places around")
				.setCancelable(false)
				.setPositiveButton("Settings",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// if this button is clicked, close
								// current activity
								openSettingsPage();
								
							}
						})
				.setNegativeButton("Exit the App",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// if this button is clicked, just close
								// the dialog box and do nothing
								dialog.cancel();
								hostActivity.finish();

							}
						});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();

	}
	
	private void openSettingsPage() {
		Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivityForResult(intent, getTargetRequestCode());

	}

	@Override
	public void onLocationChanged(android.location.Location arg0) {
		showProgressDialog();

		refershData(arg0.getLatitude(), arg0.getLongitude());

	}

	private android.location.Location getLastKnownLocation() {
		Criteria criteria = new Criteria();
		locationManager = (LocationManager) hostActivity
				.getSystemService(Context.LOCATION_SERVICE);
		String provider = locationManager.getBestProvider(criteria, false);
		android.location.Location lastKnownLocation = locationManager
				.getLastKnownLocation(provider);

		return lastKnownLocation;
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}
}
