package com.codepath.apps.twitterapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.codepath.apps.twitterapp.TwitterClient.TimelineParams;
import com.codepath.apps.twitterapp.models.Tweet;
import com.codepath.apps.twitterapp.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import eu.erikw.PullToRefreshListView;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class TimelineActivity extends Activity {

	private PullToRefreshListView lvTweets;
	private List<Tweet> tweets = new ArrayList<Tweet>();
	private User currentUser;
	private TweetsAdapter tweetsAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		
		tweetsAdapter = new TweetsAdapter(getApplicationContext(), tweets);
		lvTweets = (PullToRefreshListView) findViewById(R.id.lvTweets);
		lvTweets.setAdapter(tweetsAdapter);
		lvTweets.setOnScrollListener(new EndlessScrollListener() {
			
			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				Log.d("DEBUG", "tweets size="+tweets.size()+", page="+page+", totalItemsCount="+totalItemsCount);
				// get the last tweet to get the max id
				if (tweets.size() > 0) {
					Tweet lastTweet = tweets.get(tweets.size() - 1);
					// subtract one to not include the last tweet
					Map<TwitterClient.TimelineParams, String> params = new HashMap<TwitterClient.TimelineParams, String>();
					params.put(TwitterClient.TimelineParams.MAXID, String.valueOf(lastTweet.getTweetId() - 1));
					updateTimeline(null, params, true);
				}
			}
		});
		
		lvTweets.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				
				Map<TwitterClient.TimelineParams, String> params = new HashMap<TwitterClient.TimelineParams, String>();
				// get the latest tweet to get the since id
				if (tweets.size() > 0) {
					Tweet latestTweet = tweets.get(0);
					params.put(TwitterClient.TimelineParams.SINCEID, String.valueOf(latestTweet.getTweetId()));
				}
				
				updateTimeline(null, params, false);
				lvTweets.onRefreshComplete();
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
    		if (data == null) {
    			return;
    		}
    		if (resultCode == RESULT_OK) {
    			Tweet tweet = (Tweet) data.getSerializableExtra("composedTweet");
    			Log.d("DEBUG", "composed tweet data = " + tweet);
    			long latestTweetId = 0;
    			
    			if (tweets.size() > 0) {
    				latestTweetId= tweets.get(tweets.size() - 1).getTweetId();
    			}
    			Map<TwitterClient.TimelineParams, String> params = new HashMap<TwitterClient.TimelineParams, String>();
    			if (latestTweetId > 0) {
    				params.put(TimelineParams.SINCEID, String.valueOf(latestTweetId));
    			}
    			updateTimeline(tweet, params, false);
    		}
    		
    		if (resultCode == RESULT_CANCELED) {
    			Log.d("DEBUG", "Result cancelled");
    			String errorMessage = data.getStringExtra("errorMessage");
    			if (errorMessage != null) {
    				Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
    			}
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
		updateTimeline(null, new HashMap<TwitterClient.TimelineParams, String>(), true);
	}

	
	/**
	 * Update the timeline with new tweets
	 * @param currentTweet tweet that was recently composed. It may not make it to the timeline yet. 
	 * @param params parameters for the timeline call
	 * @param append boolean flag indicating whether to append or prepend the retrieved tweets. true to append
	 */
	private void updateTimeline(final Tweet currentTweet, final Map<TwitterClient.TimelineParams, String> params, final boolean append) {
		
		params.put(TwitterClient.TimelineParams.COUNT, "25");
		TwitterClientApp.getRestClient().getHomeTimeline(new JsonHttpResponseHandler() {
			
			@Override
			public void onSuccess(JSONArray jsonTweets) {
				List<Tweet> latestTweets = Tweet.fromJson(jsonTweets);
				boolean saveTweets = false;
				if (currentTweet != null && !latestTweets.contains(currentTweet)) {
					// TODO: should sort by tweet id
					latestTweets.add(currentTweet);
				}
				
				if (append == true) {
					if (tweets.size() == 0) {
						saveTweets = true;
					}
					tweets.addAll(latestTweets);
				} else {
					tweets.addAll(0, latestTweets);
					saveTweets = true;
				}
				if (saveTweets && latestTweets != null && latestTweets.size() > 0) {
					Log.d("DEBUG", "Saving "+latestTweets.size()+" tweets to the db");
					DBManager.saveTweets(latestTweets);
				}
				tweetsAdapter.notifyDataSetChanged();
			}
			
			@Override
			public void onFailure(Throwable t, JSONArray response) {
				Log.d("DEBUG", "Error retrieving tweets. Response="+response.toString(), t);
				Toast.makeText(getApplicationContext(), "Error retrieving tweets", Toast.LENGTH_LONG).show();
			}
			
			@Override
			public void onFinish() {
				if (tweets.size() == 0) {
					tweets.addAll(DBManager.getStoredTweets());
					Log.d("DEBUG", "Retrieved "+tweets.size()+" tweets from DB storage");
					tweetsAdapter.notifyDataSetChanged();
				}
				super.onFinish();
			}
		}, params);
		
		if (this.currentUser == null) {
			TwitterClientApp.getRestClient().verifyCredentials(new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(JSONObject jsonCredentials) {
					currentUser = new User(jsonCredentials);
					setTitle("@"+currentUser.getScreenName());
				}
				
				@Override
				public void onFailure(Throwable t, JSONArray response) {
					Log.e("DEBUG", "Error getting current user data. Response="+response.toString(), t);
				}
			});
		}
	}

}
