package com.codepath.apps.twitterapp;

import org.json.JSONObject;

import com.codepath.apps.twitterapp.models.Tweet;
import com.codepath.apps.twitterapp.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ComposeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compose);
		DBManager.logTableSize();
		User currentUser = (User) getIntent().getSerializableExtra("currentUser");
		if (currentUser == null) {
			cancelRequest("Current user details could not be retrieved. Please try again later.");
		} else {
			ImageView imageView = (ImageView) findViewById(R.id.ivComposeProfile);
			ImageLoader.getInstance().displayImage(currentUser.getProfileImageUrl(), imageView);

			TextView tvFullName = (TextView) findViewById(R.id.tvFullName);
			tvFullName.setText(Html.fromHtml("<b>" + currentUser.getName() + "</b>"));

			TextView tvHandle = (TextView) findViewById(R.id.tvHandle);
			tvHandle.setText(Html.fromHtml("<font color='#777777'>@" + currentUser.getScreenName() + "</font>"));
		}
		
	}
	
	private void cancelRequest(String msg) {
		Intent i = new Intent();
		i.putExtra("errorMessage", msg);
		setResult(RESULT_CANCELED, i);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.compose, menu);
		return true;
	}
	
	public void onPostClick(View v) {
		EditText etTweet = (EditText) findViewById(R.id.etTweet);
		final String tweetText = etTweet.getText().toString().trim();
		if (tweetText.isEmpty()) {
			Toast.makeText(getApplicationContext(), "Tweet cannot be empty", Toast.LENGTH_SHORT).show();
			return;
		}
		
		TwitterClientApp.getRestClient().postTweet(tweetText, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject jsonResponse) {
				// post successful, create tweet object and return
				Tweet tweet = new Tweet(jsonResponse);
				
				Intent i = new Intent();
				i.putExtra("composedTweet", tweet);
				setResult(RESULT_OK, i);
				finish();
			}
			
			@Override
			public void onFailure(Throwable t, JSONObject responseObject) {
				Log.e("ERROR", "Error posting tweet", t);
				cancelRequest("Could not post tweet. Please try again later.");
			}
			
		});
	}

}
