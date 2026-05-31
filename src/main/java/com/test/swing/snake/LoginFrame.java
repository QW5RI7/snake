package com.test.swing.snake;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;

/**登录界面*/
public class LoginFrame extends JFrame implements MouseListener {


    /** 登录按钮 */
    private JButton login = new JButton();
    /** 注册按钮 */
    private JButton register = new JButton();
    /** 用户名输入框 */
    private JTextField username = new JTextField();
    /** 密码输入框 */
    private JPasswordField password = new JPasswordField();
    /** 验证码输入框 */
    private JTextField code = new JTextField();
    /** 验证码图片 */
    private JLabel rightCode = new JLabel();
    /** 验证码字符串 */
    private String codeStr;

    /** 登录界面构造方法 */
    public LoginFrame() {
        // 检查版本号
        Version.checkVersion();

        //初始化界面
        initJFrame();

        //在这个界面中添加内容
        initView();

        //添加窗口监听
        addWindowListener();

        //让当前界面显示出来
        setVisible(true);
    }
    /** 初始化界面 */
    public void initView() {
        //1. 添加用户名文字
        JLabel usernameText = new JLabel(new ImageIcon(Objects.requireNonNull(getClass().getResource("/image/login/用户名.png"))));
        usernameText.setBounds(116, 135, 47, 17);
        getContentPane().add(usernameText);

        //2.添加用户名输入框

        username.setBounds(195, 134, 200, 30);
        getContentPane().add(username);

        //3.添加密码文字
        JLabel passwordText = new JLabel(new ImageIcon(Objects.requireNonNull(getClass().getResource("/image/login/密码.png"))));
        passwordText.setBounds(130, 195, 32, 16);
        getContentPane().add(passwordText);

        //4.密码输入框
        password.setBounds(195, 195, 200, 30);
        getContentPane().add(password);


        //验证码提示
        JLabel codeText = new JLabel(new ImageIcon(Objects.requireNonNull(getClass().getResource("/image/login/验证码.png"))));
        codeText.setBounds(133, 256, 50, 30);
        getContentPane().add(codeText);

        //验证码的输入框
        code.setBounds(195, 256, 100, 30);
        getContentPane().add(code);


        codeStr = CodeUtil.getCode();
        //设置内容
        ImageIcon image = new ImageIcon(ImageCodeUtil.generateImage(120, 30, codeStr));
        rightCode.setIcon(image);
        //绑定鼠标事件
        rightCode.addMouseListener(this);
        //位置和宽高
        rightCode.setBounds(300, 256, 120, 30);
        //添加到界面
        getContentPane().add(rightCode);

        //5.添加登录按钮
        login.setBounds(123, 310, 128, 47);
        login.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/image/login/登录按钮.png"))));
        //去除按钮的边框
        login.setBorderPainted(false);
        //去除按钮的背景
        login.setContentAreaFilled(false);
        //给登录按钮绑定鼠标事件
        login.addMouseListener(this);
        getContentPane().add(login);

        //6.添加注册按钮
        register.setBounds(256, 310, 128, 47);
        register.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/image/login/注册按钮.png"))));
        //去除按钮的边框
        register.setBorderPainted(false);
        //去除按钮的背景
        register.setContentAreaFilled(false);
        //给注册按钮绑定鼠标事件
        register.addMouseListener(this);
        getContentPane().add(register);


        //7.添加背景图片
        JLabel background = new JLabel(new ImageIcon(Objects.requireNonNull(getClass().getResource("/image/login/background.png"))));
        background.setBounds(0, 0, 470, 390);
        getContentPane().add(background);

    }

    /** 初始化窗口 */
    public void initJFrame() {
        setSize(488, 430);//设置宽高
        setTitle("贪吃蛇游戏 V1.1登录");//设置标题
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);//设置关闭模式
        setLocationRelativeTo(null);//居中
        setLayout(null);//取消内部默认布局
        // 禁止调整大小
        setResizable(false);
    }



    /** 处理鼠标点击事件
     * @param e 鼠标事件
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == login) {
            System.out.println("点击了登录按钮");
        } else if (e.getSource() == register) {
            System.out.println("点击了注册按钮");
        }
    }



    /** 处理鼠标按下事件
     * @param e 鼠标事件
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getSource() == login) {
            login.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/image/login/登录按下.png"))));
        } else if (e.getSource() == register) {
            register.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/image/login/注册按下.png"))));
        }
    }


    /** 处理鼠标松开事件
     * @param e 鼠标事件
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getSource() == login) {
            login.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/image/login/登录按钮.png"))));
            //获取两个文本输入框中的内容
            String usernameInput = username.getText();
            String passwordInput = password.getText();
            //获取用户输入的验证码
            String codeInput = code.getText();

            //创建一个User对象
            User userInfo = new User(usernameInput, passwordInput, 0);

            if (codeInput.length() == 0) {
                DialogUtil.showDialog("验证码不能为空");
            } else if (usernameInput.length() == 0 || passwordInput.length() == 0) {

                //调用showJDialog方法并展示弹框
                DialogUtil.showDialog("用户名或者密码为空");

            } else if (!codeInput.equalsIgnoreCase(codeStr)) {
                DialogUtil.showDialog("验证码输入错误");
            } else if (Database.authenticate(userInfo)) {
                userInfo.setHighScore(Database.getHighScore(userInfo.getUsername()));
                System.out.println("用户名和密码正确可以开始玩游戏了");
                //关闭当前登录界面
                dispose();
                //打开游戏的主界面
                new SnakeGame(userInfo);
            } else {
                DialogUtil.showDialog("用户名或密码错误");
            }
        } else if (e.getSource() == register) {
            register.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("/image/login/注册按钮.png"))));
            new RegisterFrame();
            dispose();
        } else if (e.getSource() == rightCode) {
            System.out.println("更换验证码");
            //获取一个新的验证码
            codeStr = CodeUtil.getCode();
            ImageIcon image = new ImageIcon(ImageCodeUtil.generateImage(120, 30, codeStr));
            rightCode.setIcon(image);
            repaint();
        }
    }

    /** 处理鼠标划入事件
     * @param e 鼠标事件
     */
    @Override
    public void mouseEntered(MouseEvent e) {

    }

    /** 处理鼠标划出事件
     * @param e 鼠标事件
     */
    @Override
    public void mouseExited(MouseEvent e) {

    }

    /** 添加窗口关闭事件监听器 */
    public void addWindowListener(){
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int result = DialogUtil.showDialog("确定要关闭登录窗口吗？");
                if(result == DialogUtil.CONFIRM){
                    System.exit(0);
                }
            }
        });
    }
}