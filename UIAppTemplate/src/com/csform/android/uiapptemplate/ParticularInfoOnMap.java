package com.csform.android.uiapptemplate;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.squareup.picasso.Picasso;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ParticularInfoOnMap extends ActionBarActivity {
	String image0,image1,image2;

	private LinearLayout linearLayoutImage;
	private Bitmap resized;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.particular_info_on_map);
		linearLayoutImage = (LinearLayout) findViewById(R.id.linearLayoutForImages);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		Bundle b = getIntent().getExtras();
		Double lat = b.getDouble("lat");
		Double lon = b.getDouble("lng");
		image0 = b.getString("image0");
		image1 = b.getString("image1");
		image2 = b.getString("image2");
		
		Log.i("image0 Path",image0) ;
		
		
			if(image0==null || image0.equalsIgnoreCase("null"))
			{
				Toast.makeText(getApplicationContext(), "no image for this crime", 0).show();
				//ImageView imageView = (ImageView)findViewById(R.id.imageViewParticular1);
				ImageView imageView = (ImageView)findViewById(R.id.imageViewParticular1);
				//ImageView imageView2 = (ImageView)findViewById(R.id.imageViewParticular3);
				
				//imageView.setBackgroundResource(R.drawable.noimage);
				imageView.setBackgroundResource(R.drawable.noimage);
				//imageView2.setBackgroundResource(R.drawable.noimage);
				
				
			}
			else
			{
				
				ImageView imageView = (ImageView)findViewById(R.id.imageViewParticular1);
				//Picasso.with(getApplicationContext()).load(image0).into(imageView);
		
				Picasso.with(getApplicationContext()).load(image0).placeholder(getApplicationContext().getResources().getDrawable(R.drawable.noimage)).error(getApplicationContext().getResources().getDrawable(R.drawable.noimage)).into(imageView);

				if(image1==null || image1.equalsIgnoreCase(null))
				{
					
				}
				else
				{

					ImageView imageView1 = (ImageView)findViewById(R.id.imageViewParticular2);
					//Picasso.with(getApplicationContext()).load(image1).into(imageView1);
					Picasso.with(getApplicationContext()).load(image1).placeholder(getApplicationContext().getResources().getDrawable(R.drawable.noimage)).error(getApplicationContext().getResources().getDrawable(R.drawable.noimage)).into(imageView1);
				
					if(image2==null || image2.equalsIgnoreCase(null))
					{
						
					}
					else
					{

						ImageView imageView2 = (ImageView)findViewById(R.id.imageViewParticular3);
						//Picasso.with(getApplicationContext()).load(image2).into(imageView2);
						Picasso.with(getApplicationContext()).load(image2).placeholder(getApplicationContext().getResources().getDrawable(R.drawable.noimage)).error(getApplicationContext().getResources().getDrawable(R.drawable.noimage)).into(imageView2);
					}
				}
				
			}
			
		String title = b.getString("title");
		String description = b.getString("description");
		String address = b.getString("address");
		String dateoccured = b.getString("date_occured");
		String crimetype = b.getString("type_name");
		String user = b.getString("user_login");

		TextView type = (TextView) findViewById(R.id.crime_type);
		// Getting reference to the TextView to set longitude
		TextView desc = (TextView) findViewById(R.id.crime_description);
		TextView address1 = (TextView) findViewById(R.id.crime_address);
		// Getting reference to the TextView to set longitude
		TextView date = (TextView) findViewById(R.id.crime_date_occured);
		TextView userTextView = (TextView) findViewById(R.id.crime_user);
		TextView titleTextView = (TextView) findViewById(R.id.crime_title);


		//Picasso.with(getApplicationContext()).load(image0).into(image);
		// Setting the latitude
		titleTextView.setText(title);
		userTextView.setText(user);
		// Setting the longitude
		desc.setText(description);
		address1.setText(address);
		date.setText(dateoccured);
		type.setText(crimetype);

	}

	
}