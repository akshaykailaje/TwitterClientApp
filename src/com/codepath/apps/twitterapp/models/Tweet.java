package com.codepath.apps.twitterapp.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Column.ForeignKeyAction;
import com.activeandroid.annotation.Column.NullConflictAction;
import com.activeandroid.annotation.Table;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;


@Table(name="Tweets")
public class Tweet extends Model implements Serializable {

	private static final long serialVersionUID = 3749965112128459596L;
	@Column(name="User",onDelete=ForeignKeyAction.CASCADE,onUpdate=ForeignKeyAction.CASCADE)
	private User user;
	@Column(name="body")
	private String body;
	@Column(name="tweetId")
	private long tweetId;
	@Column(name="isFavorited")
	private boolean isFavorited;
	@Column(name="isRetweeted")
	private boolean isRetweeted;
	
	public Tweet() {
		super();
	}
	
	public Tweet(JSONObject jsonTweet) {
        try {
            body = jsonTweet.getString("text");
            tweetId = jsonTweet.getLong("id");
            isFavorited = jsonTweet.getBoolean("favorited");
            isRetweeted = jsonTweet.getBoolean("retweeted");
            
            user = new User(jsonTweet.getJSONObject("user"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
	}

    public User getUser() {
        return user;
    }

    public String getBody() {
        return body;
    }

    public long getTweetId() {
        return tweetId;
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
    			tweetId, body, isFavorited, isRetweeted, user);
    	
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
    		
    		return (getTweetId() == other.getTweetId() && getUser().equals(other.getUser()));
    	}
    	
    	return false;
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

            Tweet tweet = new Tweet(tweetJson);
            if (tweet != null && tweet.getTweetId() > 0) {
            	
                tweets.add(tweet);
            }
        }

        return tweets;
    }
}