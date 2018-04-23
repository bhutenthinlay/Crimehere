package com.csform.android.uiapptemplate;

import java.net.URI;
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
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import tabs.SlidingTabLayout;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.csform.android.uiapptemplate.adapter.DrawerAdapter;
import com.csform.android.uiapptemplate.model.DrawerItem;
import com.csform.android.uiapptemplate.util.ImageUtil;

public class LeftMenusActivity extends ActionBarActivity implements
		OnQueryTextListener,Communicate {

	public static final String LEFT_MENU_OPTION = "com.csform.android.uiapptemplate.LeftMenusActivity";
	public static final String LEFT_MENU_OPTION_1 = "Left Menu Option 1";
	public static final String LEFT_MENU_OPTION_2 = "Left Menu Option 2";

	View headerView = null;
	private ListView mDrawerList;
	private List<DrawerItem> mDrawerItems;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	// my added
	ViewPager mPager;
	SlidingTabLayout mTabs;

	private Handler mHandler;
	static private ArrayList<HashMap<String, String>> mylist;
	public static final int GOOGLEMAP = 0;
	public static final int LISTVIEW = 1;
	static String searchAddress = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		
		mylist = new ArrayList<HashMap<String, String>>();
		
		try {
			Bundle b = getIntent().getExtras();
			searchAddress = b.getString("searchAddress");
			Log.e("left menu", searchAddress);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (savedInstanceState != null) {
			Bundle b = getIntent().getExtras();
			searchAddress = b.getString("searchAddress");
			Log.e("left menu", searchAddress);
		}
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
		mTabs = (SlidingTabLayout) findViewById(R.id.tabs);
		mTabs.setDistributeEvenly(true);

		mTabs.setViewPager(mPager);
		mPager.setCurrentItem(0);
		
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mTitle = mDrawerTitle = getTitle();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_view);

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		prepareNavigationDrawerItems();
		setAdapter();
		// mDrawerList.setAdapter(new DrawerAdapter(this, mDrawerItems));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
				R.string.drawer_open, R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				getSupportActionBar().setTitle(mTitle);
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		/*
		 * if (savedInstanceState == null) {
		 * mDrawerLayout.openDrawer(mDrawerList); }
		 */
		
		mDrawerLayout.closeDrawers();
		android.support.v7.app.ActionBar actionBar=getSupportActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
	    actionBar.setIcon(android.R.color.transparent);
	   
	    getCategory();
		retrieveCrime();
		
	}

	private void retrieveCrime() {
		// TODO Auto-generated method stub
		new AsyncTask<Void, Long, Boolean>() {

			Boolean status;
			String url;

			protected void onPreExecute() {

				url = getResources().getString(R.string.URL_GET_ALL_CRIME);

			};

			protected void onPostExecute(Boolean result) {

				
				DatabaseOperations dop=new DatabaseOperations(getApplicationContext());
				
				for (int i = 0; i < mylist.size(); i++) {
					String id = mylist.get(i).get("type_id");
					String identifier = mylist.get(i).get("type_name");
					String title = mylist.get(i).get("title");
					String description = mylist.get(i).get("description");
					String type_name = mylist.get(i).get("type_name");
					String address = mylist.get(i).get("address");
					String lat = mylist.get(i).get("lat");
					String lng = mylist.get(i).get("lng");
					String image0 = mylist.get(i).get("image0");
					String image1 = mylist.get(i).get("image1");
					String image2 = mylist.get(i).get("image2");
					String user_login = mylist.get(i).get("user_login");
					String date_occured = mylist.get(i).get("date_occured");
					try{
			//		dop.putInformation(dop, id, identifier, title, description, type_name, address, Double.parseDouble(lat),Double.parseDouble(lng), image0, image1, image2, user_login, date_occured);
					}catch(Exception ex)
					{
						ex.printStackTrace();
					}
					}
				dop.close();
				


			};

			@Override
			protected Boolean doInBackground(Void... added_item) {
				// TODO Auto-generated method stub
				status = retrieveData(url);
				return status;
			}

		}.execute(null, null, null);

	}

	protected Boolean retrieveData(String url) {
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

			HttpClient httpclient = new DefaultHttpClient();

			HttpGet request = new HttpGet();
			URI website = new URI(url);
			request.setURI(website);
			HttpResponse response = httpclient.execute(request);
			String json = null;
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				json = EntityUtils.toString(entity);
			}
			JSONArray jArray = new JSONArray(json);
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

				return true;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			return false;

		}

	}

	private void setAdapter() {
		String option = LEFT_MENU_OPTION_1;
		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.containsKey(LEFT_MENU_OPTION)) {
			option = extras.getString(LEFT_MENU_OPTION, LEFT_MENU_OPTION_1);
		}

		boolean isFirstType = true;

		
		if (option.equals(LEFT_MENU_OPTION_1)) {
			headerView = prepareHeaderView(R.layout.header_navigation_drawer_1,
					"http://simpleicon.com/wp-content/uploads/user1.png",
					"");
		} else if (option.equals(LEFT_MENU_OPTION_2)) {
			headerView = prepareHeaderView(R.layout.header_navigation_drawer_2,
					"http://pengaja.com/uiapptemplate/avatars/0.jpg",
					"dev@csform.com");
			isFirstType = false;
		}

		BaseAdapter adapter = new DrawerAdapter(this, mDrawerItems, isFirstType);

		mDrawerList.addHeaderView(headerView);// Add header before adapter (for
												// pre-KitKat)
		mDrawerList.setAdapter(adapter);
	}

	private View prepareHeaderView(int layoutRes, String url, String email) {
		View headerView = getLayoutInflater().inflate(layoutRes, mDrawerList,
				false);
		ImageView iv = (ImageView) headerView.findViewById(R.id.image);
		TextView tv = (TextView) headerView.findViewById(R.id.email);

		UserSessionManager session=new UserSessionManager(getApplicationContext());
		String phone=session.getPhone();
		ImageUtil.displayRoundImage(iv, url, null);
		tv.setText(phone);

		return headerView;
	}

	private void prepareNavigationDrawerItems() {
		mDrawerItems = new ArrayList<>();
		mDrawerItems.add(new DrawerItem(R.string.fontello_user,
				R.string.drawer_title_home,
				DrawerItem.DRAWER_ITEM_TAG_LINKED_IN));
		mDrawerItems.add(new DrawerItem(R.string.fontello_drag_and_drop,
				R.string.drawer_title_tips, DrawerItem.DRAWER_ITEM_TAG_BLOG));
		/*mDrawerItems.add(new DrawerItem(R.string.drawer_icon_search_bars,
				R.string.drawer_title_search_by_category,
				DrawerItem.DRAWER_ITEM_TAG_GIT_HUB));
		mDrawerItems.add(new DrawerItem(R.string.fontello_search,
				R.string.drawer_title_advance_search,
				DrawerItem.DRAWER_ITEM_TAG_GIT_HUB));
		mDrawerItems.add(new DrawerItem(R.string.drawer_icon_splash_screens,
				R.string.drawer_title_create_alreat,
				DrawerItem.DRAWER_ITEM_TAG_GIT_HUB));*/
		
		
		mDrawerItems.add(new DrawerItem(R.string.drawer_icon_login_page,
				R.string.drawer_title_logout,
				DrawerItem.DRAWER_ITEM_TAG_INSTAGRAM));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main, menu);

		SearchView search = (SearchView) menu.findItem(R.id.search)
				.getActionView();
		search.setQueryHint("enter address...");
		search.setOnQueryTextListener(this);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position/* , mDrawerItems.get(position - 1).getTag() */);
		}
	}

	private void selectItem(int position/* , int drawerTag */) {
		// minus 1 because we have header that has 0 position
		if (position < 1) { // because we have header, we skip clicking on it
			return;
		}

		Intent i;
		switch (position) {
		case 1:
			break;
		case 2:
			 i = new Intent(getApplicationContext(), Map.class);
			startActivity(i);
			finish();
			break;
		/*case 3:
			 i = new Intent(getApplicationContext(), MapCategory.class);
				startActivity(i);
				finish();
			break;
		case 4:
			break;
		case 5:
			break;*/
		case 3:
			 new AlertDialog.Builder(LeftMenusActivity.this)
             .setIcon(android.R.drawable.ic_dialog_alert)
             .setTitle("Log out")
             .setMessage("Are you sure you want to log out?")
             .setPositiveButton("Yes",
                     new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog,
                                             int which) {
                        	 UserSessionManager session=new UserSessionManager(getApplicationContext());
                 			session.logoutUserAgain();
                 			Intent intent = new Intent(getApplicationContext(), LogInPageActivity.class);
                 			startActivity(intent);
                             finish();
                         }

                     }).setNegativeButton("No", null).show();
			break;
		default:
			String drawerTitle = getString(mDrawerItems.get(position - 1)
					.getTitle());
			Toast.makeText(
					this,
					"You selected " + drawerTitle + " at position: " + position,
					Toast.LENGTH_SHORT).show();

		}
		mDrawerList.setItemChecked(position, true);
		setTitle(mDrawerItems.get(position - 1).getTitle());
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	public void setTitle(int titleId) {
		setTitle(getString(titleId));
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	class MyPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {
		String[] tabs;

		public MyPagerAdapter(android.support.v4.app.FragmentManager fm) {
			super(fm);
			tabs = getResources().getStringArray(R.array.tabs);
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			// TODO Auto-generated method stub
			return tabs[position];
		}

		@Override
		public android.support.v4.app.Fragment getItem(int arg0) {
			// TODO Auto-generated method stubf
			android.support.v4.app.Fragment fragment = null;
			// MapFragmentDetail mf=null;

			switch (arg0) {
			case GOOGLEMAP:
				headerView = prepareHeaderView(R.layout.header_navigation_drawer_1,
						"http://simpleicon.com/wp-content/uploads/user1.png",
						"");
				fragment = new GoogleMapFragment(searchAddress);

				// mf = new MapFragmentDetail();
				// mf.MapFragmentDetail1("0", "0", "0", "All");
				// FragmentManager manager = getSupportFragmentManager();
				// FragmentTransaction transaction = manager.beginTransaction();
				// transaction.add(R.id.main_activity23, fragment,
				// "mapfragment");
				// transaction.commit();

				break;
				
			case LISTVIEW:
				headerView = prepareHeaderView(R.layout.header_navigation_drawer_1,
						"drawable\\crimehere.png",
						"");
				fragment = StickyListHeadersActivity.newInstance(GoogleMapFragment.bottom,GoogleMapFragment.top,GoogleMapFragment.left,GoogleMapFragment.right);

				break;
			}

			return fragment;
		}
	}

	public class LocationManager_check {

		LocationManager locationManager;
		Boolean locationServiceBoolean = false;
		int providerType = 0;
		AlertDialog alert;

		public LocationManager_check(Context context) {
			locationManager = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
			boolean gpsIsEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
			boolean networkIsEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (networkIsEnabled == true && gpsIsEnabled == true) {
				locationServiceBoolean = true;
				providerType = 1;

			} else if (networkIsEnabled != true && gpsIsEnabled == true) {
				locationServiceBoolean = true;
				providerType = 2;

			} else if (networkIsEnabled == true && gpsIsEnabled != true) {
				locationServiceBoolean = true;
				providerType = 1;
			}

		}

		public Boolean isLocationServiceAvailable() {
			return locationServiceBoolean;
		}

		public int getProviderType() {
			return providerType;
		}

		public void createLocationServiceError(final Activity activityObj) {

			// show alert dialog if Internet is not connected
			AlertDialog.Builder builder = new AlertDialog.Builder(activityObj);

			builder.setMessage(
					"You need to activate location service to use this feature. Please turn on network or GPS mode in location settings")
					.setTitle("LostyFound")
					.setCancelable(false)
					.setPositiveButton("Settings",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// Intent intent = new
									// Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
									// activityObj.startActivity(intent);
									// alert.dismiss();
								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									alert.dismiss();
								}
							});
			alert = builder.create();
			alert.show();
		}

	}

	@Override
	public boolean onQueryTextChange(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String arg0) {
		// TODO Auto-generated method stub
		Log.e("hey dude", "hey dude");
		gotoMap(arg0);
		return false;
	}

	private void gotoMap(String arg0) {
		// TODO Auto-generated method stub
		Intent i = new Intent(getApplicationContext(), SearchableActivity.class);
		Bundle b = new Bundle();
		b.putString("searchAddress", arg0);
		i.putExtras(b);
		startActivity(i);
		finish();
	}

	@Override
	public void sendData(double a, double b, double c, double d) {
		// TODO Auto-generated method stub
		StickyListHeadersActivity fragment = new StickyListHeadersActivity(a,b,c,d);
	     final Bundle bundle = new Bundle();
	     bundle.putString("a", String.valueOf(a));
	     bundle.putString("b", String.valueOf(b));
	     bundle.putString("c", String.valueOf(c));
	     bundle.putString("d", String.valueOf(d));
	     Log.i("BUNDLE", bundle.toString());
	     fragment.setArguments(bundle); 
	}
	
	
	private void getCategory() {
		// TODO Auto-generated method stub
	
		new AsyncTask<Void, Long, Boolean>() {

			 Boolean status;	
			String url;

			protected void onPreExecute() {

				url = getResources().getString(R.string.URL_GET_CATEGORY);
			};

			protected void onPostExecute(Boolean result) {
				DatabaseOperations dop=new DatabaseOperations(getApplicationContext());
				
				for(int i=0;i<mylist.size();i++)
				{
					try{
				dop.putInformationCategory(dop, mylist.get(i).get("type_id"), mylist.get(i).get("type_name"));
					}catch(Exception ex)
					{
						ex.printStackTrace();
					}
				
				}
				dop.close();
			};

			@Override
			protected Boolean doInBackground(Void... added_item) {
				// TODO Auto-generated method stub
				status = retrieveData1(url);
				return status;
			}

		}.execute(null, null, null);
		


	}

	protected Boolean retrieveData1(String url) {
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

			HttpClient httpclient = new DefaultHttpClient();

			HttpGet request = new HttpGet();
			URI website = new URI(url);
			request.setURI(website);
			HttpResponse response = httpclient.execute(request);
			String json = null;
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				json = EntityUtils.toString(entity);
			}
			Log.i("category", json);
			JSONArray jArray = new JSONArray(json);
			if (jArray.length() < 1) {
				return false;
			} else {
				for (int i = 0; i < jArray.length(); i++) {
					HashMap<String, String> map = new HashMap<String, String>();
					JSONObject e = jArray.getJSONObject(i);
					map.put("type_id", e.getString("type_id"));
					map.put("type_name", e.getString("type_name"));
					mylist.add(map);
				}

				return true;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			return false;

		}

	}

	

}
