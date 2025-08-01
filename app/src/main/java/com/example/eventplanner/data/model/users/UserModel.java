package com.example.eventplanner.data.model.users;


public class UserModel {
    public static final String ROLE_ORGANIZER = "EventOrganizer", ROLE_PROVIDER = "ServiceAndProductProvider", ROLE_ADMIN = "ADMIN", ROLE_USER = "User";

    private Integer id;
    private String email;
    private String password;
    private String role;
    private String city;

    private boolean muted;

    public UserModel() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public boolean getMuted(){return muted;}
    public void setMuted(boolean muted){ this.muted = muted; }
}
