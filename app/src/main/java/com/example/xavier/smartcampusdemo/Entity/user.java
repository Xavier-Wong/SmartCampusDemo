package com.example.xavier.smartcampusdemo.Entity;

/**
 * Created by Xavier on 4/19/2017.
 *
 */

public class user {
    private int u_id;			// 标识
    private int s_id;
    private String username;// 用户名
    private String password;// 密码
    private int sex;		// 性别
    private String tel;		// 电话
    private String avatar;	// 头像
    private String email;	// 电子邮箱
    public int getU_Id() {
        return u_id;
    }
    public void setU_Id(int u_id) {
        this.u_id = u_id;
    }
    public int getS_Id() {
        return s_id;
    }
    public void setS_Id(int s_id) {
        this.s_id = s_id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public int getSex() {
        return sex;
    }
    public void setSex(int sex) {
        this.sex = sex;
    }
    public String getTel() {
        return tel;
    }
    public void setTel(String tel) {
        this.tel = tel;
    }
    public String getAvatar() {
        return avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
