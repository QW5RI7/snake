package com.test.swing.snake;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

/**
 * SQLite 数据库操作工具类
 * 负责初始化数据库表，以及用户相关操作
 */
public class Database {
    private static final File DB_FILE = new File(System.getProperty("user.home") + "/.snakegame/users.db");
    // 数据库文件存放位置：用户主目录下的 .snakegame 文件夹
    private static final String DB_PATH = DB_FILE.getPath();
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;

    // 静态初始化块：确保表存在
    static {
        if (!DB_FILE.exists()) {
            DB_FILE.getParentFile().mkdirs();
        }
        init();
    }

    /**
     * 初始化数据库和表
     */
    private static void init() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "username TEXT PRIMARY KEY," +
                "password TEXT NOT NULL," +
                "highScore INTEGER DEFAULT 0)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查用户名是否存在
     */
    public static boolean isUsernameExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 验证登录
     * @return true 如果用户名和密码匹配
     */
    public static boolean authenticate(User user) {
        String sql = "SELECT password FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // 验证密码是否匹配数据库中的哈希值
                String hashed = rs.getString("password");
                return BCrypt.checkpw(user.getPassword(), hashed);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 注册新用户
     * @return true 如果注册成功
     */
    public static boolean registerUser(String username, String password) {
        if (isUsernameExists(username)) {
            return false;
        }
        // 对密码进行哈希处理
        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
        String sql = "INSERT INTO users(username, password, highScore) VALUES(?, ?, 0)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashed);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 更新用户最高分（只有当新分数高于旧分数时才更新）
     */
    public static void updateHighScore(String username, int newScore) {
        String sql = "UPDATE users SET highScore = ? WHERE username = ? AND highScore < ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newScore);
            pstmt.setString(2, username);
            pstmt.setInt(3, newScore);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取所有用户按最高分降序排序（用于排行榜）
     */
    public static List<User> getAllUsersSortedByScore() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT username, password, highScore FROM users ORDER BY highScore DESC";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User u = new User(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getInt("highScore")
                );
                list.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public static int getHighScore(String username) {
        // 可以写一个 Database.getHighScore(username) 方法，或者简单地从排行榜列表中获取
        // 为了简洁，直接查询数据库
        String sql = "SELECT highScore FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(Database.DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("highScore");
        } catch (SQLException ex) { ex.printStackTrace(); }
        return 0;
    }
}
