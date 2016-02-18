package com.example.android.okcupidassessment.model;

/**
 * Created by Sohail on 2/12/16.
 */
public class Photo {

    private String base_path;
    private Integer ordinal;
    private String id;
    private String caption;
    private FullPaths full_paths;
    private OriginalSize original_size;
    private CropRect crop_rect;
    private ThumbPaths thumb_paths;

    public class FullPaths {
        private String large;
        private String small;
        private String medium;
        private String original;

        public String getLarge() {
            return large;
        }

        public void setLarge(String large) {
            this.large = large;
        }
    }

    public class OriginalSize {
        private Integer width;
        private Integer height;
    }

    public class CropRect {
        private Integer height;
        private Integer y;
        private Integer width;
        private Integer x;
    }

    public class ThumbPaths {
        private String desktop_match;
        private String large;
        private String medium;
        private String small;
    }


    public FullPaths getFull_paths() {
        return full_paths;
    }

    public void setFull_paths(FullPaths full_paths) {
        this.full_paths = full_paths;
    }
}
