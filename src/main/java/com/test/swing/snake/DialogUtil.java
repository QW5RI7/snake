package com.test.swing.snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URI;

/**
 * 弹窗工具类
 * 负责创建和管理游戏中的弹窗
 * @author 王鑫
 * @version 1.0
 * @since 2026-5-23
 */
public class DialogUtil {
    /**关闭*/
    public static final int CLOSE = -1;
    /**确认*/
    public static final int CONFIRM = 0;
    /**取消*/
    public static final int CANCEL = 1;
    /**私有化构造方法*/
    private DialogUtil() {
    }
    /**确保当前线程是EDT线程*/
    private static void ensureEDT() {
        if (!SwingUtilities.isEventDispatchThread()) {
            throw new IllegalStateException("当前线程不是EDT线程，不能调用DialogUtil方法");
        }
    }
    /**
     * 显示确认弹窗
     * @param message 弹窗内容
     * @return 用户选择的按钮索引，0表示确认，1表示取消，-1表示关闭
     */
    public static int showDialog(String message) {
        return showDialog("提示", message);
    }

    /**
     * 显示确认弹窗
     * 父窗口为null时，弹窗为独立窗口，否则弹窗为子窗口
     * @param title 弹窗标题
     * @param message 弹窗内容
     * @return 用户选择的按钮索引，0表示确认，1表示取消，-1表示关闭
     */
    public static int showDialog(String title, String message) {
        return showDialog(title, message, null, "确认", "取消");
    }

    /**
     * 显示确认弹窗
     * 父窗口为null时，弹窗为独立窗口，否则弹窗为子窗口
     * @param title 弹窗标题
     * @param message 弹窗内容
     * @param parent 父窗口，用于创建子窗口
     * @param buttons 按钮文本数组
     * @return 用户选择的按钮索引，-1表示关闭
     */
    public static int showDialog(String title, String message, Frame parent, String... buttons) {
        int[] result = {0};
        //创建Jlabel对象管理文字并添加到弹框当中
        JLabel warning = new JLabel(message);
        warning.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        warning.setHorizontalAlignment(SwingConstants.CENTER);
        FontMetrics fm = warning.getFontMetrics(warning.getFont());
        int textWidth = fm.stringWidth(message);
        int width = Math.min(1200, Math.max(300, textWidth + 80));
        JDialog dialog = initDialog(title, width, 150, parent);
        dialog.add(warning, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        for (int i = 0; i < buttons.length; i++) {
            JButton button = new JButton(buttons[i]);
            buttonPanel.add(button);
            int temp = i;
            button.addActionListener(e -> {
                result[0] = temp;
                dialog.dispose();
            });
        }
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                result[0] = CLOSE;
                dialog.dispose();
            }
        });
        dialog.setVisible(true);
        return result[0];
    }

    /**
     * 显示输入弹窗
     * @param title 弹窗标题
     * @param message 弹窗内容
     * @return 用户输入的字符串，如果用户点击取消按钮，返回null
     */
    public static String showInputDialog(String title, String message) {
        String[] result = {null};
        JDialog dialog = initDialog(title, 300, 150, null);
        JLabel warning = new JLabel(message);
        warning.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        warning.setHorizontalAlignment(SwingConstants.CENTER);
        dialog.add(warning, BorderLayout.NORTH);
        JPanel textFieldPanel = new JPanel();
        textFieldPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        dialog.add(textFieldPanel, BorderLayout.CENTER);
        JTextField textField = new JTextField(16);
        textField.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        textField.setPreferredSize(new Dimension(200, 35));
        textField.setHorizontalAlignment(SwingConstants.CENTER);
        textFieldPanel.add(textField);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        JButton confirm = new JButton("确认");
        buttonPanel.add(confirm);
        confirm.addActionListener(e -> {
            result[0] = textField.getText();
            dialog.dispose();
        });
        JButton cancel = new JButton("取消");
        buttonPanel.add(cancel);
        cancel.addActionListener(e -> dialog.dispose());
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
        return result[0];
    }
    private static JDialog initDialog(String title, int width, int height, Frame parent) {
        ensureEDT();
        JDialog dialog = new JDialog(parent, title);
        dialog.setSize(width, height);
        dialog.setAlwaysOnTop(true);
        dialog.setLocationRelativeTo(null);
        dialog.setModal(true);
        return dialog;
    }
    /**
     * 显示确认弹窗
     * @param title 弹窗标题
     * @param message 弹窗内容
     * @return 用户是否点击了确认按钮
     */
    public static boolean showConfirmDialog(String title, String message) {
        int idx = showDialog(title, message, null, "确认", "取消");
        return idx == CONFIRM;
    }
    /**
     * 打开浏览器访问指定URL
     * @param url 要访问的URL
     */
    public static void openWebPage(String url) {
        try {
            // 检查当前平台是否支持 Desktop
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(new URI(url));
                    return;
                }
            }
            // 降级方案：针对某些 Linux 环境
            Runtime.getRuntime().exec("xdg-open " + url);
        } catch (Exception e) {
            e.printStackTrace();
            // 可在此弹窗提示用户手动打开
            showDialog("错误" ,"无法打开浏览器，请手动访问：" + url);
        }
    }
}