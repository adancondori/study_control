package com.acc.study_control.models;

import com.orm.SugarRecord;

import java.util.List;

public class User extends SugarRecord {

    public String name;
    public String email;
    public String phone;
    public String token;
    public String uid;
    public String provider;

    public User() {
    }

    public static User findUser() {
        User user = null;
        List<User> users = User.listAll(User.class);
        if (users.size() > 0) user = users.get(0);
        return user;
    }

    public static void cleanUser() {
        User.deleteAll(User.class);
        User.executeQuery("DELETE FROM SQLITE_SEQUENCE WHERE NAME = 'USER'");
    }
}
