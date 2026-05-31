package com.test.swing.snake;

import javax.swing.*;
import java.awt.event.*;
import java.util.Objects;

/** 注册窗口 */
public class RegisterFrame extends JFrame {
    /** 注册按钮*/
    private JButton register = new JButton();
    /** 重置按钮*/
    private JButton reset = new JButton();
    /** 用户名文本框*/
    private JTextField username = new JTextField();
    /** 密码文本框*/
    private JPasswordField password = new JPasswordField();
    /** 确认密码文本框*/
    private JPasswordField confirmPassword = new JPasswordField();
    /** 注册窗口构造方法，负责初始化窗口*/
    public RegisterFrame(){
        this.setSize(488,430);
        //设置界面的标题
        this.setTitle("拼图 注册");
        //设置界面居中
        this.setLocationRelativeTo(null);
        // 初始化界面
        initView();
        // 添加窗口监听器
        addWindowListener();
        // 设置关闭模式
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        // 禁止调整大小
        this.setResizable(false);
        //让显示显示出来，建议写在最后
        this.setVisible(true);

        getContentPane();
    }
    /** 初始化界面*/
    private void initView() {
        //1. 添加用户名文字
        JLabel usernameText = new JLabel(new ImageIcon(Objects.requireNonNull(getClass().getResource("/image/register/注册用户名.png"))));
        usernameText.setBounds(106, 135, 79, 17);
        add(usernameText);

        //2.添加用户名输入框

        username.setBounds(195, 134, 200, 30);
        add(username);

        //3.添加密码文字
        JLabel passwordText = new JLabel(new ImageIcon(Objects.requireNonNull(getClass().getResource("/image/register/注册密码.png"))));
        passwordText.setBounds(120, 195, 64, 16);
        add(passwordText);

        //4.密码输入框
        password.setBounds(195, 195, 200, 30);
        add(password);

        //5.添加确认密码文字
        JLabel confirmPasswordText = new JLabel(new ImageIcon(Objects.requireNonNull(getClass().getResource("/image/register/再次输入密码.png"))));
        confirmPasswordText.setBounds(90, 255, 96, 16);
        add(confirmPasswordText);

        //6.确认密码输入框
        confirmPassword.setBounds(195, 255, 200, 30);
        add(confirmPassword);

        //7.添加注册按钮
        register.setBounds(105, 310, 128, 47);
        register.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/image/register/注册按钮.png"))));
        //去除按钮的边框
        register.setBorderPainted(false);
        //去除按钮的背景
        register.setContentAreaFilled(false);
        register.addMouseListener(new MouseAdapter() {
            /** 注册按钮点击事件处理*/
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("注册按钮被点击了");
            }
            /** 注册按钮按下事件处理*/
            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println("注册按钮被按下");
                register.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/image/register/注册按下.png"))));
            }
            /** 注册按钮松开事件处理*/
            @Override
            public void mouseReleased(MouseEvent e) {
                System.out.println("注册按钮被松开");
                register.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/image/register/注册按钮.png"))));
                if (check()) {
                    dispose();
                    new LoginFrame();
                }
            }
        });
        add(register);

        //8.添加重置按钮
        reset.setBounds(256, 310, 128, 47);
        reset.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/image/register/重置按钮.png"))));
        //去除按钮的边框
        reset.setBorderPainted(false);
        //去除按钮的背景
        reset.setContentAreaFilled(false);
        reset.addMouseListener(new MouseAdapter() {
            /** 重置按钮点击事件处理*/
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("重置按钮被点击了");
            }
            /** 重置按钮按下事件处理*/
            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println("重置按钮被按下");
                reset.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/image/register/重置按下.png"))));
            }
            /** 重置按钮松开事件处理*/
            @Override
            public void mouseReleased(MouseEvent e) {
                System.out.println("重置按钮被松开");
                reset.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/image/register/重置按钮.png"))));
                username.setText("");
                password.setText("");
                confirmPassword.setText("");
            }
        });
        add(reset);

        //9.添加背景图片
        JLabel background = new JLabel(new ImageIcon(Objects.requireNonNull(getClass().getResource("/image/register/background.png"))));
        background.setBounds(10, 10, 470, 390);
        add(background);
    }
    /** 检查注册信息是否有效*/
    private boolean check() {
         String usernameInput = username.getText();
         String passwordInput = password.getText();
         String confirmPasswordInput = confirmPassword.getText();
         if (usernameInput.isEmpty() || passwordInput.isEmpty() || confirmPasswordInput.isEmpty()) {
             DialogUtil.showDialog("请输入用户名、密码、确认密码");
             return false;
         }
         if (!checkUsername(usernameInput)) {
             return false;
         }
         if (!checkPassword(passwordInput)) {
             return false;
         }
         if (!confirmPasswordInput.equals(passwordInput)) {
             DialogUtil.showDialog("两次输入密码不一致");
             return false;
         }
         if (!Database.registerUser(usernameInput, passwordInput)) {
             DialogUtil.showDialog("注册失败，用户名是否已存在");
             return false;
         }
         return true;
    }
    /** 检查用户名是否有效*/
    private boolean checkUsername(String usernameInput) {
        if (!usernameInput.matches("^\\w+$")){
            DialogUtil.showDialog("用户名只能包含字母、数字、下划线");
            return false;
        }
        if (Database.isUsernameExists(usernameInput)) {
            DialogUtil.showDialog("用户名已存在");
            return false;
        }
        return true;
    }
    /** 检查密码是否有效*/
    private boolean checkPassword(String passwordInput) {
        if (!passwordInput.matches("^\\w+$")){
            DialogUtil.showDialog("密码只能包含字母、数字、下划线");
            return false;
        }
        if (passwordInput.length() < 8 || passwordInput.length() > 16) {
            DialogUtil.showDialog("密码长度必须在8到16位之间");
            return false;
        }
        return true;
    }

    /** 添加窗口关闭事件监听*/
    private void addWindowListener() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int result = DialogUtil.showDialog("确定要关闭注册窗口吗？");
                if (result == DialogUtil.CONFIRM) {
                    System.exit(0);
                }
            }
        });
    }
}
