package com.codepath.apps.twitterapp.models;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class User implements Serializable {
	
	private static final long serialVersionUID = -3873718438635035932L;
	private String name;
	private long id;
	private String screenName;
	private String profileImageUrl;
	private String profileBackgroundUrl;
	private int numTweets;
	private int followersCount;
	private int friendsCount;

	public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public String getScreenName() {
        return screenName;
    }
    
    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getProfileBackgroundImageUrl() {
        return profileBackgroundUrl;
    }

    public int getNumTweets() {
        return numTweets;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public int getFriendsCount() {
        return friendsCount;
    }
    
    @Override
    public boolean equals(Object o) {
    	
    	if (o == null) {
    		return false;
    	}
    	
    	if (this == o) {
    		return true;
    	}
    	
    	if (o instanceof User) {	
    		User other = (User) o;
    		
    		return (getId() == other.getId());
    	}
    	
    	return false;
    }
    
    @Override
    public String toString() {
    	return String.format("User: { id: %d, screenName: %s, name: %s, numTweets: %d, followers: %d, friends: %d, profileImageUrl: %s, profileBackgroundImageUrl: %s }%n", 
    			id, screenName, name, numTweets, followersCount, friendsCount, profileImageUrl, profileBackgroundUrl);
    	
    }

    public static User fromJson(JSONObject jsonUser) {
        User u = new User();      
        try {
        	u.id = jsonUser.getLong("id");
        	u.name = jsonUser.getString("name");
        	u.screenName = jsonUser.getString("screen_name");
        	u.profileImageUrl = jsonUser.getString("profile_image_url");
        	u.profileBackgroundUrl = jsonUser.getString("profile_background_image_url");
        	u.numTweets = jsonUser.getInt("statuses_count");
        	u.followersCount = jsonUser.getInt("followers_count");
        	u.friendsCount = jsonUser.getInt("friends_count");
        } catch (JSONException e) {
        	e.printStackTrace();
        	u = null;
        }

        return u;
    }


}