package com.codepath.apps.twitterapp;

import java.util.Map;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.FlickrApi;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.text.SpannedString;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
    public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
    public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
    public static final String REST_CONSUMER_KEY = "uzyT6X2c4g2iMi4JsOZ03A";       // Change this
    public static final String REST_CONSUMER_SECRET = "Q69hlmZTHIgFnbcNijW90JVwqWeTEaijpiOTID4"; // Change this
    public static final String REST_CALLBACK_URL = "oauth://twitterapp"; // Change this (here and in manifest)
    
    public enum TimelineParams { 
    	COUNT ("count"),
    	MAXID ("max_id"),
    	SINCEID ("since_id");
    	
    	private final String name;
    	TimelineParams(String name) {
			this.name = name;
		}
    	
    	String getName() {
    		return this.name;
    	}
    	
    }; 
    
    public TwitterClient(Context context) {
        super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
    }
    
    public void getHomeTimeline(AsyncHttpResponseHandler handler, Map<TwitterClient.TimelineParams, String> callParams) {
    	String url = getApiUrl("statuses/home_timeline.json");
    	RequestParams params = new RequestParams();
    	for (TwitterClient.TimelineParams key : callParams.keySet()) {
    		params.put(key.getName(), callParams.get(key));
    	}
    	client.get(url, params, handler);
    }
    
    public void verifyCredentials(AsyncHttpResponseHandler handler) {
    	String url = getApiUrl("account/verify_credentials.json");
    	client.get(url, null, handler);
    }
    
    public void postTweet(String tweetText, AsyncHttpResponseHandler handler) {
    	String url = getApiUrl("statuses/update.json");
    	RequestParams params = new RequestParams();
    	params.put("status", tweetText);
    	client.post(url, params, handler);
    }
    
    /* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
     * 	  i.e getApiUrl("statuses/home_timeline.json");
     * 2. Define the parameters to pass to the request (query or body)
     *    i.e RequestParams params = new RequestParams("foo", "bar");
     * 3. Define the request method and make a call to the client
     *    i.e client.get(apiUrl, params, handler);
     *    i.e client.post(apiUrl, params, handler);
     */
}