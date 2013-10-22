package com.codepath.apps.twitterapp.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class Tweet implements Serializable {

	private static final long serialVersionUID = 3749965112128459596L;
	private User user;
	private String body;
	private long id;
	private boolean isFavorited;
	private boolean isRetweeted;

    public User getUser() {
        return user;
    }

    public String getBody() {
        return body;
    }

    public long getId() {
        return id;
    }

    public boolean isFavorited() {
        return isFavorited;
    }

    public boolean isRetweeted() {
        return isRetweeted;
    }
    
    @Override
    public String toString() {
    	return String.format("Tweet: { id: %d, body: %s, isFavorited: %s, isRetweeted: %s, %s }%n", 
    			id, body, isFavorited, isRetweeted, user);
    	
    }
    
    @Override
    public boolean equals(Object o) {
    	
    	if (o == null) {
    		return false;
    	}
    	
    	if (this == o) {
    		return true;
    	}
    	
    	if (o instanceof Tweet) {	
    		Tweet other = (Tweet) o;
    		
    		return (getId() == other.getId() && getUser().equals(other.getUser()));
    	}
    	
    	return false;
    }

    public static Tweet fromJson(JSONObject jsonObject) {
        Tweet tweet = new Tweet();
        try {
            tweet.body = jsonObject.getString("text");
            tweet.id = jsonObject.getLong("id");
            tweet.isFavorited = jsonObject.getBoolean("favorited");
            tweet.isRetweeted = jsonObject.getBoolean("retweeted");
            
            tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return tweet;
    }

    public static List<Tweet> fromJson(JSONArray jsonArray) {
        List<Tweet> tweets = new ArrayList<Tweet>(jsonArray.length());

        for (int i=0; i < jsonArray.length(); i++) {
            JSONObject tweetJson = null;
            try {
                tweetJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            Tweet tweet = Tweet.fromJson(tweetJson);
            if (tweet != null) {
                tweets.add(tweet);
            }
        }

        return tweets;
    }
}