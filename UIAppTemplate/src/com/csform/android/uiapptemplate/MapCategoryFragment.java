package com.csform.android.uiapptemplate;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.gms.internal.mm;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MapCategoryFragment extends Fragment implements LocationListener,
		OnCameraChangeListener {
	public static final String LOCATION_SERVICE = "location";

	double lat, lon;
	String latString, lonString;
	String markerString;
	LatLng latlng;
	TextView textviewMap;
	Boolean longpress = true;
	LocationManager locationManager;
	static String ID="";
	VisibleRegion vr;
	double bottom, top, left, right;
	ProgressBar progressBarMapFragmentDetail;
	Spinner spiner;
	GoogleMap mMap;
	static public ArrayList<HashMap<String, String>> mylist;
	static public ArrayList<HashMap<String, String>> mylist1;
	ArrayAdapter<String> adapter;
	Marker myMarker;
	static ArrayList<Marker> myMarkerArray;

	MapCategoryFragment(boolean longpress) {
		this.longpress = longpress;

	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		myMarkerArray=new ArrayList<Marker>();
		mylist = new ArrayList<HashMap<String, String>>();
		mylist1 = new ArrayList<HashMap<String, String>>();
		UserSessionManager session = new UserSessionManager(getActivity());
		View v = inflater.inflate(R.layout.map_category_fragment, container,
				false);
		progressBarMapFragmentDetail = (ProgressBar) v
				.findViewById(R.id.progressBarMapFragmentDetailCategory);

		spiner = (Spinner) v.findViewById(R.id.spinnerCategory);
		List<String> spinnerArray = new ArrayList<String>();
		DatabaseOperations dop = new DatabaseOperations(getActivity());
		Cursor CR = dop.getInformationCategory(dop);

		if (CR.moveToFirst()) {
			CR.moveToFirst();
			do {
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("id", CR.getString(0));
				map.put("type_name", CR.getString(1));
				mylist1.add(map);

			} while (CR.moveToNext());
		}

		for (int i = 0; i < mylist1.size(); i++) {
			spinnerArray.add(mylist1.get(i).get("type_name"));
		}

		adapter = new ArrayAdapter<String>(getActivity(),
				R.layout.my_spinner_style, spinnerArray) {

			public View getView(int position, View convertView, ViewGroup parent) {

				View v = super.getView(position, convertView, parent);

				((TextView) v).setTextSize(16);

				return v;

			}

			public View getDropDownView(int position, View convertView,
					ViewGroup parent) {

				View v = super.getDropDownView(position, convertView, parent);

				((TextView) v).setGravity(Gravity.LEFT);

				return v;

			}

		};

		spiner.setAdapter(adapter);

		spiner.setOnItemSelectedListener(new OnItemSelectedListener() {

		
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				ID=String.valueOf(position+1);

				for(int i=0;i<myMarkerArray.size();i++)
				{
					myMarkerArray.get(i).remove();
					mMap.clear();
				}
				
				getCrime(bottom, top, left, right);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}

		});

		mMap = ((SupportMapFragment) this.getChildFragmentManager()
				.findFragmentById(R.id.mapfragmentidCategory)).getMap();

		locationManager = (LocationManager) getActivity().getSystemService(
				LOCATION_SERVICE);
		Location location = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		mMap.getUiSettings().setMapToolbarEnabled(false);

		if (longpress) {

			markerString = "your current location";
		}

		else {

			markerString = "crime scene";

		}
		try {
			lat = location.getLatitude();
			lon = location.getLongitude();
			Log.e("lat n lon",
					String.valueOf(lat) + "    " + String.valueOf(lon));
			LatLng latlng = new LatLng(lat, lon);
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15f));
			mMap.addMarker(new MarkerOptions().position(latlng).title(
					markerString));
		} catch (Exception ex) {

			try {
				mMap.setMyLocationEnabled(true);
				// Check if we were successful in obtaining the map.
				if (mMap != null) {

					mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

						@Override
						public void onMyLocationChange(Location arg0) {
							// TODO Auto-generated method stub

							lat = arg0.getLatitude();
							lon = arg0.getLongitude();
							latlng = new LatLng(lat, lon);
							mMap.addMarker(new MarkerOptions().position(
									new LatLng(arg0.getLatitude(), arg0
											.getLongitude())).title(
									"My current Location"));
							mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
									latlng, 15f));
						}
					});
				}

			} catch (Exception ex1) {
				Log.e("lat n lon",
						String.valueOf(lat) + "    " + String.valueOf(lon));
				lat = 24.821387;
				lon = 78.060060;
				markerString = "unable to locate exactly";
				LatLng latlng = new LatLng(lat, lon);
				mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 6f));
				mMap.addMarker(new MarkerOptions().position(latlng).title(
						markerString));

			}
		}

		mMap.setOnCameraChangeListener(this);
		vr = mMap.getProjection().getVisibleRegion();

		left = vr.latLngBounds.southwest.longitude;
		top = vr.latLngBounds.northeast.latitude;
		right = vr.latLngBounds.northeast.longitude;
		bottom = vr.latLngBounds.southwest.latitude;
		for(int i=0;i<myMarkerArray.size();i++)
		{
			myMarkerArray.get(i).remove();
		}
		mMap.clear();
		getCrime(bottom, top, left, left);
		return v;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

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

	@Override
	public void onCameraChange(CameraPosition arg0) {
		// TODO Auto-generated method stub

		vr = mMap.getProjection().getVisibleRegion();
		left = vr.latLngBounds.southwest.longitude;
		top = vr.latLngBounds.northeast.latitude;
		right = vr.latLngBounds.northeast.longitude;
		bottom = vr.latLngBounds.southwest.latitude;
		Log.i("google zoooooom", String.valueOf(left));
		Log.i("google zoooooom", String.valueOf(arg0.zoom));
		getCrime(bottom, top, left, right);
	    
       
	}

	private void getCrime(final double a, final double b, final double c,
			final double d) {
		// TODO Auto-generated method stub
		new AsyncTask<Void, Long, Boolean>() {

			Boolean status;
			String url;

			protected void onPreExecute() {

				mMap.clear();
				Log.e("id", ID);
				url = getResources().getString(R.string.URL_CRIME_BY_TYPE)+"/"+ID;

				progressBarMapFragmentDetail.setVisibility(View.VISIBLE);
			};

			protected void onPostExecute(Boolean result) {
				progressBarMapFragmentDetail.setVisibility(View.INVISIBLE);
				DatabaseOperations dop = new DatabaseOperations(getActivity());
				for (int i = 0; i < mylist.size(); i++) {
					String id = mylist.get(i).get("id");
					String identifier = mylist.get(i).get("identifier");
					String title = mylist.get(i).get("title");
					String description = mylist.get(i).get("description");
					String type_name = mylist.get(i).get("type_name");
					String address = mylist.get(i).get("address");
					double lat = Double.parseDouble(mylist.get(i).get("lat"));
					double lng = Double.parseDouble(mylist.get(i).get("lng"));
					String image0 = mylist.get(i).get("image0");
					String image1 = mylist.get(i).get("image1");
					String image2 = mylist.get(i).get("image2");
					String user_login = mylist.get(i).get("user_login");
					String date_occured = mylist.get(i).get("date_occured");

					LatLng latlng = new LatLng(Double.parseDouble(mylist.get(i)
							.get("lat")), Double.parseDouble(mylist.get(i).get(
							"lng")));
					mMap.addMarker(new MarkerOptions()
							.position(latlng)
							.title(mylist.get(i).get("title"))
							.snippet(
									mylist.get(i).get("description") + " "
											+ mylist.get(i).get("date_occured")));
					myMarkerArray.add(mMap.addMarker(new MarkerOptions()
					.position(latlng)
					.title(mylist.get(i).get("title"))
					.snippet(
							mylist.get(i).get("description") + " "
									+ mylist.get(i).get("date_occured"))));
					try {
						dop.putInformation(dop, id, identifier, title,
								description, type_name, address, lat, lng,
								image0, image1, image2, user_login,
								date_occured);
					} catch (Exception ex) {
						ex.printStackTrace();
					} finally {
						dop.close();
					}
					// .setSnippet(CR.getString(3)

				}

			};

			@Override
			protected Boolean doInBackground(Void... added_item) {
				// TODO Auto-generated method stub
				status = retrieveData(url, a, b, c, d);
				return status;
			}

		}.execute(null, null, null);

	}

	protected Boolean retrieveData(String url, double a, double b, double c,
			double d) {
		// TODO Auto-generated method stub
		SSLContext ctx = null;
		try {
			ctx = SSLContext.getInstance("TLS");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			ctx.init(null, new TrustManager[] { new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) {
				}

				public void checkServerTrusted(X509Certificate[] chain,
						String authType) {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[] {};
				}
			} }, null);
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

			@Override
			public boolean verify(String hostname, SSLSession session) {
				// TODO Auto-generated method stub
				return true;
			}

		});
		HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
		HttpEntity httpEntity = null;
		// TODO Auto-generated method stub
		try {

			try
			{
			mylist.clear();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			final List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("a", String.valueOf(a)));
			params.add(new BasicNameValuePair("b", String.valueOf(b)));
			params.add(new BasicNameValuePair("c", String.valueOf(c)));
			params.add(new BasicNameValuePair("d", String.valueOf(d)));
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new UrlEncodedFormEntity(params));
			HttpResponse httpResponse = httpClient.execute(httpPost);
			httpEntity = httpResponse.getEntity();
			String entityResponse = EntityUtils.toString(httpEntity);
			Log.e("entity response", entityResponse);

			try{
			JSONArray jArray = new JSONArray(entityResponse);
			if (jArray.length() < 1) {
				return false;
			} else {
				for (int i = 0; i < jArray.length(); i++) {
					HashMap<String, String> map = new HashMap<String, String>();
					JSONObject e = jArray.getJSONObject(i);
					map.put("id", e.getString("id"));
					map.put("identifier", e.getString("identifier"));
					map.put("title", e.getString("title"));
					map.put("description", e.getString("description"));
					map.put("type_name", e.getString("type_name"));
					map.put("address", e.getString("address"));
					map.put("lat", e.getString("lat"));
					map.put("lng", e.getString("lng"));
					map.put("image0", e.getString("image0"));
					map.put("image1", e.getString("image1"));
					map.put("image2", e.getString("image2"));
					map.put("user_login", e.getString("user_login"));
					map.put("date_occured", e.getString("date_occured"));

					mylist.add(map);
				}

			}

			return true;
		}catch(Exception ex)
			{
			try{
				JSONObject e=new JSONObject(entityResponse);
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("id", e.getString("id"));
				map.put("identifier", e.getString("identifier"));
				map.put("title", e.getString("title"));
				map.put("description", e.getString("description"));
				map.put("type_name", e.getString("type_name"));
				map.put("address", e.getString("address"));
				map.put("lat", e.getString("lat"));
				map.put("lng", e.getString("lng"));
				map.put("image0", e.getString("image0"));
				map.put("image1", e.getString("image1"));
				map.put("image2", e.getString("image2"));
				map.put("user_login", e.getString("user_login"));
				map.put("date_occured", e.getString("date_occured"));

				mylist.add(map);
				return true;
			}catch(Exception e)
			{
				e.printStackTrace();
				return false;
			}
			
				
			}
			

		} catch (Exception ex) {
			ex.printStackTrace();
			return false;

		}
		

	}


}
