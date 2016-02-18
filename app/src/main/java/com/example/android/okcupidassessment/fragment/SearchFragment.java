package com.example.android.okcupidassessment.fragment;

import android.content.IntentFilter;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.okcupidassessment.R;
import com.example.android.okcupidassessment.adapter.FeedRecyclerViewAdapter;
import com.example.android.okcupidassessment.presenter.SearchPresenter;
import com.example.android.okcupidassessment.util.NetworkStateReceiver;
import com.example.android.okcupidassessment.util.Utils;


public class SearchFragment extends Fragment implements NetworkStateReceiver.NetworkAvailableListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private FeedRecyclerViewAdapter feedRecyclerViewAdapter;
    private NetworkStateReceiver networkStateReceiver;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // create an instance of the Broadcast Receiver to start listening to network state changes
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.networkAvailableListener = this;

        // create RecyclerView, attach adapter, grid layout and item decoration
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.feed_recycler_view);
        feedRecyclerViewAdapter = new FeedRecyclerViewAdapter(getActivity());
        recyclerView.setAdapter(feedRecyclerViewAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.addItemDecoration(
                new FeedItemDecoration(getResources().getDimensionPixelSize(R.dimen.feed_item_spacing)));

        // once the adapter is available call on the presenter to start fetching data
        SearchPresenter.getInstance(). new FetchDataTask(feedRecyclerViewAdapter, this, false).execute();

        // pull to refresh feature
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);

        return view;
    }

    /**
     * callback method from {@link NetworkStateReceiver}
     * When network comes back alive, a background thread is spun to update data
     */
    @Override
    public void networkAvailable() {
        SearchPresenter.getInstance(). new FetchDataTask(feedRecyclerViewAdapter, SearchFragment.this, true).execute();
    }

    /**
     * starting the broadcast receiver when app starts
     */
    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(networkStateReceiver, intentFilter);

        // checks if network is available and sets isConnected flag to true
        if (Utils.isNetworkAvailable(getActivity())) Utils.setIsConnected(true);
    }

    /**
     * stopping the broadcast receiver when app stops so app is not woken up
     * when network state changes and app is not active
     */
    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(networkStateReceiver);
    }

    /**
     * Listens for pull refresh action and performs tasks accordingly to
     * fetch updated data if network is available
     */
    private SwipeRefreshLayout.OnRefreshListener onRefreshListener =
            new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (Utils.isConnected()) {
                SearchPresenter.getInstance(). new FetchDataTask(feedRecyclerViewAdapter, SearchFragment.this, true).execute();

            } else {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), "Not Connected to internet", Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * called by {@link SearchPresenter.FetchDataTask} to stop the swipe animation
     * after updated data is passed on to the adapter to populate
     */
    public void setSwipeRefreshLayoutFalse() {
        if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * Item decorator for recycler view to equally space each item in the grid
     */
    private class FeedItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public FeedItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

            outRect.bottom = space;
            outRect.right = space;

            if (parent.getChildLayoutPosition(view) % 2 == 0) outRect.left = space;
            else outRect.left = 0;

            if (parent.getChildLayoutPosition(view) == 0 || parent.getChildLayoutPosition(view) == 1)
                outRect.top = space;
            else
                outRect.top = 0;
        }
    }



}
