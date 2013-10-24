package com.codepath.apps.twitterapp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.codepath.apps.twitterapp.models.Tweet;
import com.codepath.apps.twitterapp.models.User;

public class DBManager {
	
	// data cache. if the data exists in cache. It means we don't need to add to db
	// TODO: fThis is a work around. Find a better way to map existing data to data that's being saved
	private static Map<Long, Long> tweetCache = new HashMap<Long, Long>();
	
	static {
		List<Tweet> storedTweets = getStoredTweets();
		for(Tweet storedTweet : storedTweets) {
			tweetCache.put(storedTweet.getTweetId(), storedTweet.getId());
		}
	}
	
	public static void saveTweets(List<Tweet> tweets) {
		
		Map<Long, Long> tweetMap = new HashMap<Long, Long>();
		ActiveAndroid.beginTransaction();
		try {
			int tweetsPersisted = new Select()
									.from(Tweet.class)
									.orderBy("tweetId DESC")
									.execute().size();
			if (tweetsPersisted > 25 && tweets.size() > 0) {
				long medianTweetId = tweets.get(tweets.size() / 2).getTweetId();
				List<Tweet> deletedTweets  = new Delete()
												.from(Tweet.class)
												.where("tweetId <= ?", medianTweetId)
												.execute();
				
				for(Tweet deletedTweet : deletedTweets) {
					tweetCache.remove(deletedTweet.getTweetId());
				}
			}
			
			for(Tweet tweet : tweets) {
				
				
				// save user
				tweet.getUser().save();
				if (!tweetCache.containsKey(tweet.getTweetId())) {
					// save tweet
					tweet.save();
					tweetMap.put(tweet.getTweetId(), tweet.getId());
				}
				
			}
			ActiveAndroid.setTransactionSuccessful();
			tweetCache.putAll(tweetMap);
			Log.d("DEBUG","Done saving tweets to the DB");
		} finally {
			ActiveAndroid.endTransaction();
		}
		
	}
	
	public static List<Tweet> getStoredTweets() {		
		return new Select()
				.from(Tweet.class)
				.orderBy("tweetId DESC")
				.execute();
	}
	
	public static void logTableSize() {
		int tweetsPersisted = new Select()
		.from(Tweet.class)
		.orderBy("tweetId DESC")
		.execute().size();
		
		int users = new Select()
		.from(User.class)
		.execute().size();
		Log.d("DEBUG", "tweet count="+tweetsPersisted+", user count="+users);
	}

}
