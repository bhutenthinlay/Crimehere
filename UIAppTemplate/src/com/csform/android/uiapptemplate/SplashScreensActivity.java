package com.csform.android.uiapptemplate;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
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
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import com.csform.android.uiapptemplate.font.FontelloTextView;
import com.csform.android.uiapptemplate.view.kbv.KenBurnsView;

public class SplashScreensActivity extends Activity {

	public static final String SPLASH_SCREEN_OPTION = "com.csform.android.uiapptemplate.SplashScreensActivity";
	public static final String SPLASH_SCREEN_OPTION_1 = "Option 1";
	public static final String SPLASH_SCREEN_OPTION_2 = "Option 2";
	public static final String SPLASH_SCREEN_OPTION_3 = "Option 3";
	
	private KenBurnsView mKenBurns;
	private FontelloTextView mLogo;
	private TextView welcomeText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE); //Removing ActionBar
		setContentView(R.layout.activity_splash_screen);
		
		mKenBurns = (KenBurnsView) findViewById(R.id.ken_burns_images);
		mLogo = (FontelloTextView) findViewById(R.id.logo);
		welcomeText = (TextView) findViewById(R.id.welcome_text);
		mKenBurns.setImageResource(R.drawable.splash_screen_background);
		
		String category = SPLASH_SCREEN_OPTION_3;
		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.containsKey(SPLASH_SCREEN_OPTION)) {
			category = extras.getString(SPLASH_SCREEN_OPTION, SPLASH_SCREEN_OPTION_3);
		}
		setAnimation(category);
	}
	
	/** Animation depends on category.
	 * */
	private void setAnimation(String category) {
		if (category.equals(SPLASH_SCREEN_OPTION_1)) {
			animation1();
		} else if (category.equals(SPLASH_SCREEN_OPTION_2)) {
			animation2();
		} else if (category.equals(SPLASH_SCREEN_OPTION_3)) {
			animation2();
			animation3();
			 Thread t= new Thread(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try{
							UserSessionManager session=new UserSessionManager(getApplicationContext());
							String token = session.getToken();
							String id=session.getID();
							backGroundLogin(token,id);
						}catch(Exception e)
						{
						   System.out.println(e);
						}finally
						{
							
						}
						
					}

					
				 };t.start();

		}
	}

	protected void backGroundLogin(final String token,final String id) {
		// TODO Auto-generated method stub
		new AsyncTask<Void, Long, Boolean>() {

			Boolean status;
			String url;

			protected void onPreExecute() {

				url = getResources().getString(R.string.URL_AUTO_LOGIN);

			};

			protected void onPostExecute(Boolean result) {
				
				if(result)
				{
					startActivity(new Intent(getApplicationContext(), LeftMenusActivity.class));
					finish();
				}
				else{
					startActivity(new Intent(getApplicationContext(), LogInPageActivity.class));
					finish();
				}
				
			};

			@Override
			protected Boolean doInBackground(Void... added_item) {
				// TODO Auto-generated method stub
				status = retrieveData(url, token,id);
				return status;
			}

			

		}.execute(null, null, null);

	}

	
	private void animation1() {
		ObjectAnimator scaleXAnimation = ObjectAnimator.ofFloat(mLogo, "scaleX", 5.0F, 1.0F);
		scaleXAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
		scaleXAnimation.setDuration(1200);
		ObjectAnimator scaleYAnimation = ObjectAnimator.ofFloat(mLogo, "scaleY", 5.0F, 1.0F);
		scaleYAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
		scaleYAnimation.setDuration(1200);
		ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(mLogo, "alpha", 0.0F, 1.0F);
		alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
		alphaAnimation.setDuration(1200);
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.play(scaleXAnimation).with(scaleYAnimation).with(alphaAnimation);
		animatorSet.setStartDelay(500);
		animatorSet.start();
	}
	
	private void animation2() {
		mLogo.setAlpha(1.0F);
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.translate_top_to_center);
		mLogo.startAnimation(anim);
	}
	
	private void animation3() {
		ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(welcomeText, "alpha", 0.0F, 1.0F);
		alphaAnimation.setStartDelay(1700);
		alphaAnimation.setDuration(500);
		alphaAnimation.start();
	}
	
	protected Boolean retrieveData(String url, String token,
			String id) {
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

			final List<NameValuePair> params = new ArrayList<NameValuePair>();
			
			HttpPost httpPost = new HttpPost(url);
			httpPost.setHeader("Authorization",token);
			httpPost.setHeader("id",id);
			
			httpPost.setEntity(new UrlEncodedFormEntity(params));
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResponse = httpClient.execute(httpPost);
			httpEntity = httpResponse.getEntity();
			String entityResponse = EntityUtils.toString(httpEntity);
			Log.e("entity response", entityResponse);

			
			JSONObject json=new JSONObject(entityResponse);
							
			if(json.getString("Status ").equalsIgnoreCase("Success"))
			{
			
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

	
	}


}
