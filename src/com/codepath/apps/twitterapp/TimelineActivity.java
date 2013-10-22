package com.codepath.apps.twitterapp;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.codepath.apps.twitterapp.models.Tweet;
import com.codepath.apps.twitterapp.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

public class TimelineActivity extends Activity {

	private ListView lvTweets;
	private List<Tweet> tweets = new ArrayList<Tweet>();
	private User currentUser;
	private TweetsAdapter tweetsAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		tweetsAdapter = new TweetsAdapter(getApplicationContext(), tweets);
		lvTweets = (ListView) findViewById(R.id.lvTweets);
		lvTweets.setAdapter(tweetsAdapter);
		
		lvTweets.setOnScrollListener(new EndlessScrollListener() {
			
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				Log.d("DEBUG", "tweets size="+tweets.size()+", page="+page+", totalItemsCount="+totalItemsCount);
				// get the latest tweet to get the max id
				Tweet lastTweet = tweets.get(tweets.size() - 1);
				// subtract one to not include the last tweet
				updateTimeline(lastTweet.getId() - 1);
			}
		});
		
		updateTimeline();
	}
	
	public void onComposeAction(MenuItem mi) {
		
		// new intent to compose activity
		Intent i = new Intent(this, ComposeActivity.class);
		i.putExtra("currentUser", this.currentUser);
		startActivityForResult(i, 0);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == 0) {
    		if (resultCode == RESULT_OK) {
    			Tweet tweet = (Tweet) data.getSerializableExtra("composedTweet");
    			Log.d("DEBUG", "composed tweet data = " + tweet);
    			tweets.clear();
    			updateTimeline(tweet);
    		}
    	}
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.timeline, menu);
		return true;
	}
	
	private void updateTimeline() {
		updateTimeline(null, 0);
	}
	
	private void updateTimeline(final Tweet currentTweet) {
		updateTimeline(currentTweet, 0);
	}
	
	private void updateTimeline(final long maxId) {
		updateTimeline(null, maxId);
	}
	
	private void updateTimeline(final Tweet currentTweet, final long maxId) {
		
		lvTweets = (ListView) findViewById(R.id.lvTweets);
		
		TwitterClientApp.getRestClient().getHomeTimeline(new JsonHttpResponseHandler() {
			
			@Override
			public void onSuccess(JSONArray jsonTweets) {
				List<Tweet> latestTweets = Tweet.fromJson(jsonTweets);
				
				if (currentTweet != null && !latestTweets.contains(currentTweet)) {
					latestTweets.add(0, currentTweet);
				}
				tweets.addAll(latestTweets);
				tweetsAdapter.notifyDataSetChanged();
				
			}
			
			@Override
			public void onFailure(Throwable t, JSONArray response) {
				Log.e("DEBUG", "Error retrieving tweets. Response="+response.toString(), t);
				Toast.makeText(getApplicationContext(), "Error retrieving tweets", Toast.LENGTH_LONG).show();
			}
		}, maxId);
		
		if (this.currentUser == null) {
			TwitterClientApp.getRestClient().verifyCredentials(new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(JSONObject jsonCredentials) {
					currentUser = User.fromJson(jsonCredentials);
				}
			});
		}
	}

}
