package com.wordgamers.rhymbox.entities;

import androidx.annotation.Nullable;

import java.util.Objects;

public class User {

    private final String username;
    private final String id;

    private User(String username, String id) {
        this.username = username;
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public static User with(String userName, String id) {
        return new User(userName, id);
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username) && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, id);
    }
}