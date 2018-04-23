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
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.csform.android.uiapptemplate.view.FloatLabeledEditText;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class LogInPageActivity extends Activity implements OnClickListener {

	public static final String LOGIN_PAGE_AND_LOADERS_CATEGORY = "com.csform.android.uiapptemplate.LogInPageAndLoadersActivity";
	public static final String DARK = "Dark";
	public static final String LIGHT = "Light";

	private com.csform.android.uiapptemplate.view.FloatLabeledEditText phone;
	private com.csform.android.uiapptemplate.view.FloatLabeledEditText password;
	
	ProgressBar progressBarMapFragmentDetail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE); //Removing ActionBar
	
		String category = LIGHT;
		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.containsKey(LOGIN_PAGE_AND_LOADERS_CATEGORY)) {
			category = extras.getString(LOGIN_PAGE_AND_LOADERS_CATEGORY, LIGHT);
		}
		setContentView(category);
	}
	
	private void setContentView(String category) {
		if (category.equals(DARK)) {
			setContentView(R.layout.activity_login_page_dark);
		} else if (category.equals(LIGHT)) {
			setContentView(R.layout.activity_login_page_light);
			
		}
		
		
		 progressBarMapFragmentDetail = (ProgressBar) 
					findViewById(R.id.progressBarMapFragmentDetail1);
		TextView login, register,skip;
		phone=(FloatLabeledEditText) findViewById(R.id.editTextLoginPhone);
		password=(FloatLabeledEditText) findViewById(R.id.editTextLoginPassword);
		login = (TextView) findViewById(R.id.login);
		register = (TextView) findViewById(R.id.register);
		skip=(TextView) findViewById(R.id.skip);
		
		login.setOnClickListener(this);
		register.setOnClickListener(this);
		skip.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		/*if (v instanceof TextView) {
			TextView tv = (TextView) v;
			Toast.makeText(this, tv.getText(), Toast.LENGTH_SHORT).show();
		}*/
		if(v.getId()==R.id.login)
		{
		    String stringPhone=phone.getText().toString().trim();
		    String stringPassword=password.getText().toString().trim();
		    
			login(stringPhone,stringPassword);
		}
		if(v.getId()==R.id.register)
		{
			startActivity(new Intent(getApplicationContext(), Registration.class));
			finish();
		}
		if(v.getId()==R.id.skip)
		{
			startActivity(new Intent(getApplicationContext(), ActivationCode.class));
			finish();
		}
	}
	
	

	private void login(final String stringPhone, final String stringPassword) {
		// TODO Auto-generated method stub
		new AsyncTask<Void, Long, Boolean>() {

			Boolean status;
			String url;

			protected void onPreExecute() {

				url = getResources().getString(R.string.URL_LOGIN);

				progressBarMapFragmentDetail.setVisibility(View.VISIBLE);
			};

			protected void onPostExecute(Boolean result) {
				progressBarMapFragmentDetail.setVisibility(View.INVISIBLE);
				if(result)
				{
					startActivity(new Intent(getApplicationContext(), LeftMenusActivity.class));
					finish();
				}
				else{
				Toast.makeText(getApplicationContext(), "login error", 0).show();
				}
				
			};

			@Override
			protected Boolean doInBackground(Void... added_item) {
				// TODO Auto-generated method stub
				status = retrieveData(url, stringPhone,stringPassword);
				return status;
			}

			

		}.execute(null, null, null);


	}

	protected Boolean retrieveData(String url, String stringPhone,
			String stringPassword) {
		// TODO Auto-generated method stub
		
		HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

		DefaultHttpClient client = new DefaultHttpClient();

		SchemeRegistry registry = new SchemeRegistry();
		SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
		socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
		registry.register(new Scheme("https", socketFactory, 443));
		SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
		DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());

		// Set verifier     
		HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
		HttpEntity httpEntity = null;
		try {

			final List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("User_Phone", String.valueOf(stringPhone)));
			params.add(new BasicNameValuePair("User_Passwd", String.valueOf(stringPassword)));
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new UrlEncodedFormEntity(params));
			HttpResponse httpResponse = httpClient.execute(httpPost);
			httpEntity = httpResponse.getEntity();
			String entityResponse = EntityUtils.toString(httpEntity);
			Log.e("entity response", entityResponse);

			
			JSONObject json=new JSONObject(entityResponse);
							
			if(json.getString("Status").equalsIgnoreCase("Success"))
			{
			
				UserSessionManager session=new UserSessionManager(getApplicationContext());
				session.setID(json.getString("Id"));
				session.setToken(json.getString("Token"));
				return true;
			}
			else
			{
				return false;
			}
			


		}catch(Exception ex)
		{
			return false;
		}
		/*SSLContext ctx = null;
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

			final List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("User_Phone", String.valueOf(stringPhone)));
			params.add(new BasicNameValuePair("User_Passwd", String.valueOf(stringPassword)));
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new UrlEncodedFormEntity(params));
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResponse = httpClient.execute(httpPost);
			httpEntity = httpResponse.getEntity();
			String entityResponse = EntityUtils.toString(httpEntity);
			Log.e("entity response", entityResponse);

			
			JSONObject json=new JSONObject(entityResponse);
							
			if(json.getString("Status").equalsIgnoreCase("Success"))
			{
			
				UserSessionManager session=new UserSessionManager(getApplicationContext());
				session.setID(json.getString("Id"));
				session.setToken(json.getString("Token"));
				return true;
			}
			else
			{
				return false;
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;

		}

*/	
	}
	
	
}
