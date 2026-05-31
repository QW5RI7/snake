package com.test.swing.snake;
/** 用户类 */
public class User {
    /** 用户名 */
    private String username;
    /** 密码 */
    private String password;
    /** 最高分 */
    private int highScore;

    /** 默认构造方法 */
    public User() {
    }

    /** 构造方法
     * @param username 用户名
     * @param password 密码
     * @param highScore 最高分
     */
    public User(String username, String password, int highScore) {
        this.username = username;
        this.password = password;
        this.highScore = highScore;
    }

    /**
     * 获取用户名
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取密码
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }
    /** 重写toString方法，返回用户信息 */
    @Override
    public String toString() {
        return "User{" + "username=" + username + ", password=" + password + '}';
    }
    /** 重写equals方法，判断用户名是否相等 */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return username.equals(user.username);
    }


    /**
     * 获取最高分
     * @return highScore
     */
    public int getHighScore() {
        return highScore;
    }

    /**
     * 设置最高分
     * @param highScore
     */
    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }
}
