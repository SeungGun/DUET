package com.example.duet.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * User data model
 *
 * @author Seunggun Sin, 2022-05-01
 */
public class User {
    private String uid;
    private String email;
    private String nickname;
    private String registerDate;
    private String userName;
    private int level;
    private int exp;
    private int reliability;

    public User() {
        // 데이터에서 클래스로 deserialize 할 때 default constructor 필요
    }

    public User(String uid, String email, String nickname, String registerDate, String userName, int level, int exp, int reliability) {
        this.uid = uid;
        this.email = email;
        this.nickname = nickname;
        this.registerDate = registerDate;
        this.userName = userName;
        this.level = level;
        this.exp = exp;
        this.reliability = reliability;
    }

    /**
     * 사용자 회원가입 시, UserData 에 데이터를 추가하기 위한 신규 유저 데이터 format 을 위한 Constructor
     *
     * @param uid
     * @param email
     * @param nickname
     * @param userName
     */
    public User(String uid, String email, String nickname, String userName) {
        this.uid = uid;
        this.email = email;
        this.nickname = nickname;
        this.userName = userName;
        this.registerDate = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()); // 회원가입 날짜
        this.level = 1; // 유저 레벨 = 1
        this.exp = 0; // 유저 경험치 = 0
        this.reliability = 0; // 유저 신뢰도 0
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uID) {
        this.uid = uID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getReliability() {
        return reliability;
    }

    public void setReliability(int reliability) {
        this.reliability = reliability;
    }
}
