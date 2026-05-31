package com.test.swing.snake;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Arrays;

/** 排行榜弹窗 */
public class RankingDialog extends JDialog {
    /** 排行榜弹窗构造方法
     * @param parent 父窗口
     * @param allUsers 所有用户
     */
    public RankingDialog(JFrame parent, List<User> allUsers) {
        super(parent, "排行榜", true);
        setSize(300, 400);
        setLocationRelativeTo(parent);


        String[] columnNames = {"名次", "用户名", "最高分"};
        Object[][] data = new Object[allUsers.size()][3];
        for (int i = 0; i < allUsers.size(); i++) {
            User u = allUsers.get(i);
            data[i][0] = i + 1;
            data[i][1] = u.getUsername();
            data[i][2] = u.getHighScore();
        }
        Arrays.sort(data, (o1, o2) -> (Integer) o2[2] - (Integer) o1[2]);
        JTable table = new JTable(data, columnNames);
        table.setFillsViewportHeight(true);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton close = new JButton("关闭");
        close.addActionListener(e -> dispose());
        add(close, BorderLayout.SOUTH);
        setVisible(true);
    }
}
