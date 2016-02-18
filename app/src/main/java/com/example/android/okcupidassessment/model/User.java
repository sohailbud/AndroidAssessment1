package com.example.android.okcupidassessment.model;

/**
 * Created by Sohail on 2/12/16.
 */
public class User {

    private String userid;
    private Integer match;
    private String state_code;
    private Photo photo;
    private Integer age;
    private String username;
    private String city_name;
    private int[] gender_tags;
    private Boolean liked;
    private Integer enemy;
    private Double relative;
    private Integer last_login;
    private Integer gender;
    private Location location;
    private Integer orientation;
    private String country_name;
    private String state_name;
    private String country_code;
    private Integer friend;
    private Integer is_online;
    private String stoplight_color;
    private int[] last_contact_time;
    private String[] orientation_tags;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Integer getMatch() {
        return match;
    }

    public void setMatch(Integer match) {
        this.match = match;
    }

    public String getState_code() {
        return state_code;
    }

    public void setState_code(String state_code) {
        this.state_code = state_code;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }
}
