package com.example.android.okcupidassessment.model.database;

import android.provider.BaseColumns;

/**
 * Created by Sohail on 2/16/16.
 *
 * A contract class that defines the database tables
 */
public class DataContract {

    /**
     * user_entry table to store necessary data needed to populate the feed
     */
    public static final class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "user_entry";
        public static final String COLUMN_NAME_USER_ID = "userid";
        public static final String COLUMN_NAME_USERNAME = "username";
        public static final String COLUMN_NAME_AGE = "age";
        public static final String COLUMN_NAME_CITY_NAME = "city_name";
        public static final String COLUMN_NAME_STATE_CODE = "state_code";
        public static final String COLUMN_NAME_MATCH = "match";
        public static final String COLUMN_NAME_LARGE_IMAGE_PATH = "large_image_path";

    }

}
