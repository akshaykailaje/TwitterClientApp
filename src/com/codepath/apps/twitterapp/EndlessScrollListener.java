package com.codepath.apps.twitterapp;

import android.util.Log;
import android.widget.AbsListView;

public abstract class EndlessScrollListener implements AbsListView.OnScrollListener  {
	private int visibleThreshold = 1;
	private int currentPage = 0;
	private int previousTotalItemCount = 0;
	private boolean loading = true;
	private int startingPageIndex = 0;
	
	public EndlessScrollListener() {
		
	}
	
	public EndlessScrollListener(int visibleThreshold) {
		this.visibleThreshold = visibleThreshold;
	}
	
	public EndlessScrollListener(int visibleThreshold, int startPage) {
		this.visibleThreshold = visibleThreshold;
		this.startingPageIndex = startPage;
		this.currentPage = startPage;
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		
		if (!loading && (totalItemCount < previousTotalItemCount)) {
			this.currentPage = this.startingPageIndex;
			this.previousTotalItemCount = totalItemCount;
			if (totalItemCount == 0) {
				this.loading = true;
			}
		}
		
		if (loading) {
			if (totalItemCount > previousTotalItemCount) {
				loading = false;
				previousTotalItemCount = totalItemCount;
				currentPage++;
			}
		}
		
		if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
			onLoadMore(currentPage + 1, totalItemCount);
			loading = true;
		}
	}
	
	public abstract void onLoadMore(int page, int totalItemsCount);
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
	}

}
