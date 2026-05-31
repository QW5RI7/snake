package com.test.swing.snake;

import javax.swing.SwingUtilities;

/**
 * 贪吃蛇游戏测试类
 * 负责测试游戏的登录窗口和主窗口
 * @author 王鑫
 * @version 1.0
 * @since 2026-5-23
 */
public class App {
    /**测试登录窗口*/
    public static void main(String[] args) {
        // 使用EDT线程创建Swing组件
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}