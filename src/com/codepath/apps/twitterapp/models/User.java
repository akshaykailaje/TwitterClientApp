package com.codepath.apps.twitterapp.models;

import java.io.Serializable;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.annotation.Column.NullConflictAction;

@Table(name = "Users")
public class User extends Model implements Serializable {
	
	private static final long serialVersionUID = -3873718438635035932L;
	@Column(name="name")
	private String name;
	@Column(name="userId", notNull=true)
	private long userId;
	@Column(name="screenName")
	private String screenName;
	@Column(name="profileImageUrl")
	private String profileImageUrl;
	@Column(name="profileBackgroundUrl")
	private String profileBackgroundUrl;
	@Column(name="numTweets")
	private int numTweets;
	@Column(name="followersCount")
	private int followersCount;
	@Column(name="friendsCount")
	private int friendsCount;

	public User() {
		super();
	}
	
	public User(JSONObject jsonUser) {
		super();   
		
        try {
        	userId = jsonUser.getLong("id");
        	name = jsonUser.getString("name");
        	screenName = jsonUser.getString("screen_name");
        	profileImageUrl = jsonUser.getString("profile_image_url");
        	profileBackgroundUrl = jsonUser.getString("profile_background_image_url");
        	numTweets = jsonUser.getInt("statuses_count");
        	followersCount = jsonUser.getInt("followers_count");
        	friendsCount = jsonUser.getInt("friends_count");
        } catch (JSONException e) {
        	e.printStackTrace();
        }
	}
	
	public String getName() {
        return name;
    }

    public long getUserId() {
        return userId;
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
    
    public List<Tweet> tweets() {
    	return getMany(Tweet.class, "User");
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
    			userId, screenName, name, numTweets, followersCount, friendsCount, profileImageUrl, profileBackgroundUrl);
    	
    }

}