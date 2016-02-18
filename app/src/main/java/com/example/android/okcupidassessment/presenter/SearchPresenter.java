package com.example.android.okcupidassessment.presenter;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.example.android.okcupidassessment.adapter.FeedRecyclerViewAdapter;
import com.example.android.okcupidassessment.fragment.SearchFragment;
import com.example.android.okcupidassessment.interactor.FetchDataInteractor;
import com.example.android.okcupidassessment.model.Photo;
import com.example.android.okcupidassessment.model.User;
import com.example.android.okcupidassessment.model.database.DataHandler;
import com.example.android.okcupidassessment.model.database.DataContract.UserEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Sohail on 2/12/16.
 *
 * This class creates a communication between the UI and database
 * while making necessary networking calls via {@link FetchDataInteractor}
 */
public class SearchPresenter {

    private static SearchPresenter searchPresenter = null;

    public static SearchPresenter getInstance() {
        if (searchPresenter == null) searchPresenter = new SearchPresenter();
        return searchPresenter;
    }

    private SearchPresenter() {

    }

    /**
     * This background task gets called from the android UI and performs following tasks
     * 1. starts asyncTask to get remote data
     * 2. get current data from sql and load it to the adapter
     */
    public class FetchDataTask extends AsyncTask<Void, Void, List<User>> {
        private FeedRecyclerViewAdapter feedRecyclerViewAdapter;
        private SearchFragment searchFragment;
        private boolean isPullRefresh;

        public FetchDataTask(
                FeedRecyclerViewAdapter feedRecyclerViewAdapter,
                SearchFragment searchFragment,
                boolean isPullRefresh) {

            this.feedRecyclerViewAdapter = feedRecyclerViewAdapter;
            this.searchFragment = searchFragment;
            this.isPullRefresh = isPullRefresh;
        }

        @Override
        protected List<User> doInBackground(Void... params) {

            // get sql data
            final List<User> sqlUserData = getSqlData(searchFragment);

            // if sql data exists and this is not an update data call (via pull to refresh)
            // then insert the data through the adapter on an UI thread
            if (!sqlUserData.isEmpty() && !isPullRefresh) {
                searchFragment.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        feedRecyclerViewAdapter.insertData(sqlUserData);
                    }
                });
            }

            // request server data
            List<User> serverUserData = FetchDataInteractor.getInstance().requestData();

            // create a list of unique data by comparing sqldata and server data
            // usually we use time stamps or some other factor to only fetch newer data
            List<User> uniqueData = returnUniqueUsers(serverUserData, sqlUserData);

            // insert new data in database
            insertInSqlDatabase(uniqueData, searchFragment);

            return uniqueData;
        }

        @Override
        protected void onPostExecute(List<User> uniqueData) {
            super.onPostExecute(uniqueData);

            // insert new data through the adapter
            feedRecyclerViewAdapter.insertData(uniqueData);

            // cancel pull refresh animation if data request was send using that feature
            if(isPullRefresh) searchFragment.setSwipeRefreshLayoutFalse();

        }
    }

    /**
     * Download the image using the Url
     */
    public Bitmap fetchImage(String path) {
        return FetchDataInteractor.getInstance().downloadImage(path);
    }

    /**
     * Loops through cursor and converts sql data to java objects
     */
    private List<User> convertSqlToPOJO(Cursor cursor) {
        List<User> userList = new ArrayList<>();

        while (cursor.moveToNext()) {
            User user = new User();
            user.setUserid(cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_USER_ID)));
            user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_USERNAME)));
            user.setAge(cursor.getInt(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_AGE)));
            user.setCity_name(cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_CITY_NAME)));
            user.setState_code(cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_STATE_CODE)));
            user.setMatch(cursor.getInt(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_MATCH)));

            String path = cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_LARGE_IMAGE_PATH));

            Photo photo = new Photo();
            Photo.FullPaths fullPaths = new Photo().new FullPaths();
            fullPaths.setLarge(path);
            photo.setFull_paths(fullPaths);
            user.setPhoto(photo);

            userList.add(user);
        }
        cursor.close();

        return userList;
    }

    /**
     * compares two list by userid and returns list of differences
     */
    private List<User> returnUniqueUsers(List<User> serverData, List<User> sqlData) {
        List<User> uniqueUsers = new ArrayList<>();

        List<String> sqlUserIDList = new ArrayList<>();
        for (User sqlUser : sqlData) {
            sqlUserIDList.add(sqlUser.getUserid());
        }

        for (User serverUser : serverData) {
            if (!sqlUserIDList.contains(serverUser.getUserid()))
                uniqueUsers.add(serverUser);
        }
        return uniqueUsers;
    }

    /**
     * loops over the list and insert each User object into the database
     */
    private void insertInSqlDatabase(List<User> userList, SearchFragment searchFragment) {

        DataHandler dataHandler = new DataHandler(searchFragment.getActivity());
        dataHandler.open();
        for (User user : userList) {
            dataHandler.insertUserData(user);
        }

        // close after used
        dataHandler.close();
    }

    /**
     * gets all available user data from database,
     * converts it to java objects and returns a list of {@link User}
     */
    private List<User> getSqlData(SearchFragment searchFragment) {
        DataHandler dataHandler = new DataHandler(searchFragment.getActivity());
        dataHandler.open();

        Cursor userCursor = dataHandler.returnUserData();

        List<User> data = convertSqlToPOJO(userCursor);

        dataHandler.close();

        return data;
    }


}
