package com.test.swing.snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.sql.*;


/**
 * 贪吃蛇游戏主窗口
 * 负责初始化游戏窗口，设置窗口标题、大小、位置、布局等属性
 * 包含游戏面板、菜单栏、状态栏等组件
 * 使用Swing框架实现游戏窗口的创建和管理，Timer类负责游戏的定时更新
 * @author 王鑫
 * @version 1.0
 * @since 2026-5-23
 */
public class SnakeGame extends JFrame {
    /**
     * 游戏定时器
     * 负责游戏的定时更新，包括蛇的移动、食物的生成、碰撞检测等
     */
    private Timer timer;
    /**
     * 当前用户
     * 用于存储当前登录的用户信息，用于游戏结束后的用户信息存储
     */
    private final User thisUser;
    /**
     * 游戏面板
     * 用于绘制游戏界面，包括蛇、食物、边界等元素
     */
    private CellType[][] game = new CellType[GRID_SIZE][GRID_SIZE];

    // 网格相关常量
    /**每个格子大小（像素）*/
    private static final int CELL_SIZE = 20;
    /** 网格数量*/
    private static final int GRID_SIZE = 30;

    /** 蛇的初始长度*/
    private int snakeLength = 3;
    /** 随机数生成器*/
    private Random random = new Random();
    /**
     * 蛇的方向
     * 默认方向为向右
     * @see Direction 蛇的方向
     */
    private Direction direction = Direction.RIGHT;
    /** 游戏是否正在运行*/
    private boolean isRunning = true;
    /** 蛇的头行索引*/
    private int snakeHeadRow;
    /** 蛇的头列索引*/
    private int snakeHeadCol;
    /** 蛇的身体队列*/
    private Queue<int[]> snakeBody = new LinkedList<>();
    /** 蛇的速度*/
    private int speed = 100;
    /**
     * 游戏主窗口
     * 负责初始化游戏窗口，设置窗口标题、大小、位置、布局等属性
     * @see JFrame 游戏主窗口
     * @see Timer 游戏定时更新
     * @param thisUser 当前用户
     */
    public SnakeGame(User thisUser) {
        this.thisUser = thisUser;
        setTitle("贪吃蛇游戏");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(612, 673);
        setLocationRelativeTo(null);
        setLayout(null);
        setSpeed();
        addJMenuBar();
        initGame();
        GamePanel gamePanel = new GamePanel();
        gamePanel.setBounds(0, 30, GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE);
        add(gamePanel);
        addKeyListener();
        JLabel scoreLabel = new JLabel("Score: " + (snakeLength - 3));
        scoreLabel.setBounds(5, 5, 100, 20);
        scoreLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        add(scoreLabel);
        timer = new Timer(speed, e -> {
            if (isRunning) {
                moveSnakeBody();
                scoreLabel.setText("Score: " + (snakeLength - 3));
                gamePanel.repaint();
            }
        });
        timer.start();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                timer.stop();
                int result = DialogUtil.showDialog("确定要退出游戏吗？");
                if (result == DialogUtil.CONFIRM) {
                    System.exit(0);
                }
                timer.start();
            }
        });
        setVisible(true);
    }
    /**
     * 添加菜单栏
     * 包含游戏菜单、设置菜单、帮助菜单等
     * @see JMenuBar 菜单栏
     */
    private void addJMenuBar(){
        JMenuBar menuBar = new JMenuBar();
        // 1. 游戏菜单
        JMenu gameMenu = new JMenu("游戏");
        JMenuItem restartItem = new JMenuItem("重新开始 (R)");
        JMenuItem pauseItem = new JMenuItem("暂停/继续 (P)");
        JMenuItem exitItem = new JMenuItem("退出 (ESC)");
        gameMenu.add(restartItem);
        gameMenu.add(pauseItem);
        gameMenu.addSeparator();  // 分隔线
        gameMenu.add(exitItem);

        // 2. 设置菜单
        JMenu settingsMenu = new JMenu("设置");
        JMenuItem speedItem = new JMenuItem("设置速度 (E)");
        settingsMenu.add(speedItem);

        // 3. 帮助菜单
        JMenu helpMenu = new JMenu("帮助");
        JMenuItem rulesItem = new JMenuItem("游戏规则 (F1)");
        JMenuItem rankItem = new JMenuItem("排行榜 (F2)"); // 如果你实现了排行榜
        helpMenu.add(rulesItem);
        helpMenu.add(rankItem);

        // 4. 关于菜单
        JMenu aboutMenu = new JMenu("关于");
        JMenuItem aboutItem1 = new JMenuItem("Bilibili视频");
        JMenuItem aboutItem2 = new JMenuItem("微博");
        JMenuItem aboutItem3 = new JMenuItem("QQ群");
        JMenuItem aboutItem4 = new JMenuItem("Gitee仓库（仅包含发布版本）");
        JMenuItem aboutItem5 = new JMenuItem("GitHub仓库（包含最新代码）");
        aboutMenu.add(aboutItem1);
        aboutMenu.add(aboutItem2);
        aboutMenu.add(aboutItem3);
        aboutMenu.add(aboutItem4);
        aboutMenu.add(aboutItem5);

        // 添加菜单项监听器
        restartItem.addActionListener(e -> restartGame());
        pauseItem.addActionListener(e -> togglePause());
        exitItem.addActionListener(e -> System.exit(0));
        speedItem.addActionListener(e -> changeSpeed());
        rulesItem.addActionListener(e -> showRules());
        rankItem.addActionListener(e -> new RankingDialog(this, Database.getAllUsersSortedByScore()));
        aboutItem1.addActionListener(e -> DialogUtil.openWebPage("https://space.bilibili.com/1357667406?spm_id_from=333.788.0.0"));
        aboutItem2.addActionListener(e -> DialogUtil.openWebPage("https://weibo.com/u/8494970012"));
        aboutItem3.addActionListener(e -> DialogUtil.showDialog("QQ群：1083262649"));
        aboutItem4.addActionListener(e -> DialogUtil.openWebPage("https://gitee.com/wang32412345/snake-game"));
        aboutItem5.addActionListener(e -> DialogUtil.openWebPage("https://github.com/QW5RI7/snake"));

        // 组装菜单栏
        menuBar.add(gameMenu);
        menuBar.add(settingsMenu);
        menuBar.add(helpMenu);
        menuBar.add(aboutMenu);
        setJMenuBar(menuBar);
    }
    /**
     * 显示游戏规则
     * 包含游戏规则的详细说明，包括暂停/继续游戏、重新开始游戏、退出游戏、设置速度、显示游戏规则、控制蛇的方向等
     * 弹窗时，游戏定时器会暂停，用户确认后才会继续
     * @see DialogUtil 游戏规则弹窗
     */
    private void showRules() {
        timer.stop();
        DialogUtil.showDialog("游戏规则：1. P键：暂停/继续游戏 2. R键：重新开始游戏 3. ESC键：退出游戏");
        DialogUtil.showDialog("4. E键：设置速度 5. F1键：显示游戏规则 6. wasd键：控制蛇的方向");
        timer.start();
    }
    /**
     * 设置游戏速度
     * 弹窗时，游戏定时器会暂停，用户确认后才会继续
     * @see DialogUtil 游戏速度弹窗
     */
    private void changeSpeed() {
        timer.stop();
        setSpeed();
        timer.setDelay(speed);
        timer.start();
    }

    /**暂停/继续游戏*/
    private void togglePause() {
        isRunning = !isRunning;
        if (isRunning){
            timer.start();
        }
    }
    /**
     * 添加键盘监听器
     * 监听用户按键事件，根据按键更新蛇的方向、暂停/继续游戏、重新开始游戏、退出游戏、设置速度、显示游戏规则、控制蛇的方向等
     * @see KeyEvent 键盘事件
     * @see Direction 蛇的方向
     * @see CellType 游戏格子类型
     */
    private void addKeyListener() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) && direction != Direction.RIGHT) {
                    direction = Direction.LEFT;
                } else if ((e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) && direction != Direction.LEFT) {
                    direction = Direction.RIGHT;
                } else if ((e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) && direction != Direction.DOWN) {
                    direction = Direction.UP;
                } else if ((e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) && direction != Direction.UP) {
                    direction = Direction.DOWN;
                } else if (e.getKeyCode() == KeyEvent.VK_P) {
                    togglePause();
                } else if (e.getKeyCode() == KeyEvent.VK_R) {
                    restartGame();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dispose();
                } else if (e.getKeyCode() == KeyEvent.VK_E) {
                    changeSpeed();
                } else if (e.getKeyCode() == KeyEvent.VK_F1) {
                    showRules();
                } else if (e.getKeyCode() == KeyEvent.VK_F2) {
                    new RankingDialog(SnakeGame.this, Database.getAllUsersSortedByScore());
                }
            }
        });
    }
    /**
     * 设置游戏速度
     * 弹窗时，游戏定时器会暂停，用户确认后才会继续
     * 输入速度时，会自动校验，确保速度在10-1000毫秒之间
     * @see DialogUtil 游戏速度弹窗
     */
    private void setSpeed(){
        String speedStr = DialogUtil.showInputDialog("请输入蛇的速度（毫秒）", "请输入蛇的速度（1-1000）");
        if (speedStr == null || speedStr.isEmpty()) {
            DialogUtil.showDialog("速度输入错误，启用默认速度100ms");
        } else if (!speedStr.matches("^[1-9]\\d{0,4}$")) {
            DialogUtil.showDialog("速度输入错误，启用默认速度100ms");
        } else {
            speed = Integer.parseInt(speedStr);
            if (speed < 10 || speed > 1000) {
                DialogUtil.showDialog("速度输入错误，启用默认速度100ms");
                speed = 100;
            }
        }
    }
    /**
     * 初始化游戏
     * 负责初始化游戏的组件，包括蛇、食物、边界等
     */
    private void initGame() {
        // 初始化边界
        for (int i = 0; i < GRID_SIZE; i++) {
            game[i][0] = CellType.BORDER;  // 左边界
            game[i][GRID_SIZE - 1] = CellType.BORDER;  // 右边界
        }
        for (int i = 0; i < GRID_SIZE; i++) {
            game[0][i] = CellType.BORDER;  // 顶部边界
            game[GRID_SIZE - 1][i] = CellType.BORDER;  // 底部边界
        }
        // 初始化蛇
        snakeHeadRow = GRID_SIZE / 2;
        snakeHeadCol = GRID_SIZE / 2;
        game[snakeHeadRow][snakeHeadCol] = CellType.SNAKE;  // 蛇头
        game[GRID_SIZE / 2][GRID_SIZE / 2 - 1] = CellType.SNAKE;  // 蛇身体
        game[GRID_SIZE / 2][GRID_SIZE / 2 - 2] = CellType.SNAKE;  // 蛇身体
        // 初始化蛇身体队列（从尾巴到头部）
        snakeBody.add(new int[]{GRID_SIZE / 2, GRID_SIZE / 2 - 2});
        snakeBody.add(new int[]{GRID_SIZE / 2, GRID_SIZE / 2 - 1});
        snakeBody.add(new int[]{GRID_SIZE / 2, GRID_SIZE / 2});
        // 初始化食物，避免生成在蛇上
        setFood();
    }

    private void setFood() {
        int row, col;
        do {
            row = random.nextInt(GRID_SIZE - 2) + 1;
            col = random.nextInt(GRID_SIZE - 2) + 1;
        } while (game[row][col] == CellType.SNAKE);
        game[row][col] = CellType.FOOD;
    }

    /**
     * 游戏核心逻辑，负责根据当前方向移动蛇身体，检查是否撞墙或撞到自己
     * @see Direction 蛇的方向
     * @see CellType 游戏格子类型
     */
    private void moveSnakeBody() {
        if (snakeLength == 500) {
            Database.updateHighScore(thisUser.getUsername(), snakeLength - 3);
            int result = DialogUtil.showDialog("游戏胜利", "蛇长度到达500，分数：" + (snakeLength - 3));
            isRunning = false;
            if (result == DialogUtil.CONFIRM) {
                restartGame();
            }
            return;
        }
        // 1. 根据方向计算新蛇头位置
        int newHeadRow = snakeHeadRow;
        int newHeadCol = snakeHeadCol;
        switch (direction) {
            case LEFT:
                newHeadCol--;
                break;
            case RIGHT:
                newHeadCol++;
                break;
            case UP:
                newHeadRow--;
                break;
            case DOWN:
                newHeadRow++;
                break;
            default:
                break;
        }

        // 2. 检查是否撞墙
        if (newHeadRow <= 0 || newHeadRow >= GRID_SIZE - 1 ||
                newHeadCol <= 0 || newHeadCol >= GRID_SIZE - 1) {
            Database.updateHighScore(thisUser.getUsername(), snakeLength - 3);
            int result = DialogUtil.showDialog("游戏结束", "蛇撞墙了（按R键重新开始），分数：" + (snakeLength - 3));
            isRunning = false;
            if (result == DialogUtil.CONFIRM) {
                restartGame();
            }
            return;
        }

        // 3. 检查是否撞到自己
        if (game[newHeadRow][newHeadCol] == CellType.SNAKE) {
            Database.updateHighScore(thisUser.getUsername(), snakeLength - 3);
            int result =  DialogUtil.showDialog("游戏结束", "蛇撞自己了（按R键重新开始），分数：" + (snakeLength - 3));
            isRunning = false;
            if (result == DialogUtil.CONFIRM) {
                restartGame();
            }
            return;
        }

        // 4. 检查是否吃到食物
        boolean ateFood = game[newHeadRow][newHeadCol] == CellType.FOOD;

        // 5. 更新蛇头位置
        snakeHeadRow = newHeadRow;
        snakeHeadCol = newHeadCol;
        game[snakeHeadRow][snakeHeadCol] = CellType.SNAKE;
        snakeBody.add(new int[]{snakeHeadRow, snakeHeadCol});

        // 6. 如果没吃到食物，删除蛇尾
        if (!ateFood) {
            int[] tail = snakeBody.poll();
            if (tail != null) {
                game[tail[0]][tail[1]] = null;
            }
        } else {
            snakeLength++;
            // 吃到食物，生成新的食物
            setFood();
        }
    }
    /**重新开始游戏*/
    private void restartGame() {
        timer.stop();
        direction = Direction.RIGHT;
        game = new CellType[GRID_SIZE][GRID_SIZE];
        snakeLength = 3;
        snakeBody.clear();
        initGame();
        isRunning = true;
        timer.start();
    }
    /**游戏面板内部类，负责绘制游戏元素（蛇、食物等）*/
    private class GamePanel extends JPanel {
        /**绘制背景*/
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // 1. 绘制背景
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());

            // 2. 绘制网格线
            g.setColor(Color.DARK_GRAY);
            // 绘制垂直线
            for (int x = 0; x <= GRID_SIZE * CELL_SIZE; x += CELL_SIZE) {
                g.drawLine(x, 0, x, GRID_SIZE * CELL_SIZE);
            }
            // 绘制水平线
            for (int y = 0; y <= GRID_SIZE * CELL_SIZE; y += CELL_SIZE) {
                g.drawLine(0, y, GRID_SIZE * CELL_SIZE, y);
            }

            // 3. 绘制游戏元素（蛇、食物等）
            drawGameElements(g);
        }
        /**绘制游戏元素（蛇、食物等）*/
        private void drawGameElements(Graphics g) {
            for (int i = 0; i < GRID_SIZE; i++) {
                for (int j = 0; j < GRID_SIZE; j++) {
                    CellType cell = game[i][j];
                    if (cell == null) {
                        continue;
                    }
                    switch (cell) {
                        case SNAKE ->{ // 蛇
                            g.setColor(Color.GREEN);
                            g.fillRect(j * CELL_SIZE + 1, i * CELL_SIZE + 1, CELL_SIZE - 2, CELL_SIZE - 2);
                        }
                        case FOOD ->{ // 食物
                            g.setColor(Color.RED);
                            g.fillRect(j * CELL_SIZE + 1, i * CELL_SIZE + 1, CELL_SIZE - 2, CELL_SIZE - 2);
                        }
                        case BORDER ->{ // 边界
                            g.setColor(Color.BLUE);
                            g.fillRect(j * CELL_SIZE + 1, i * CELL_SIZE + 1, CELL_SIZE - 2, CELL_SIZE - 2);
                        }
                    }
                }
            }
        }
    }
}