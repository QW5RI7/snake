package com.test.swing.snake;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Random;
/**验证码图片工具类*/
public class ImageCodeUtil {
    /** 私有构造方法，防止外部实例化 */
    private ImageCodeUtil() {}
    /** 随机数工具类 */
    private static final Random random = new Random();

    /**
     * 生成验证码图片
     * @param width 图片宽度
     * @param height 图片高度
     * @param code 验证码字符串（通常4~6位）
     * @return BufferedImage 图片对象
     */
    public static BufferedImage generateImage(int width, int height, String code) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // 1. 设置背景色（浅灰色/白色）
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        // 2. 绘制干扰线
        drawInterferenceLines(g2d, width, height);

        // 3. 绘制字符（逐个绘制，随机倾斜）
        drawChars(g2d, code, width, height);

        // 4. 添加噪点
        drawNoise(g2d, width, height);

        g2d.dispose();
        return image;
    }

    /** 绘制随机干扰线 */
    private static void drawInterferenceLines(Graphics2D g2d, int width, int height) {
        g2d.setColor(Color.LIGHT_GRAY);
        int lineCount = 6 + random.nextInt(7); // 6~12条线
        for (int i = 0; i < lineCount; i++) {
            int x1 = random.nextInt(width);
            int y1 = random.nextInt(height);
            int x2 = random.nextInt(width);
            int y2 = random.nextInt(height);
            g2d.drawLine(x1, y1, x2, y2);
        }
    }

    /** 绘制每个字符，随机倾斜 */
    private static void drawChars(Graphics2D g2d, String code, int width, int height) {
        int len = code.length();
        int fontSize = height - 8;      // 字体稍小，留出边距
        Font font = new Font("微软雅黑", Font.BOLD, fontSize);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();

        // 获取所有字符中最大的宽度（确保每个字符占位足够）
        int maxCharWidth = 0;
        for (int i = 0; i < len; i++) {
            int w = fm.charWidth(code.charAt(i));
            if (w > maxCharWidth) maxCharWidth = w;
        }
        // 每个字符占用的宽度 = 最大字符宽度 + 固定额外间距
        int charStep = maxCharWidth;
        // 总宽度
        int totalWidth = charStep * len;
        // 起始X坐标（居中）
        int startX = (width - totalWidth) / 2;
        // 垂直基线（居中）
        int yBase = (height - fm.getHeight()) / 2 + fm.getAscent();

        for (int i = 0; i < len; i++) {
            char c = code.charAt(i);
            // 随机颜色
            g2d.setColor(new Color(
                    50 + random.nextInt(100),
                    50 + random.nextInt(100),
                    50 + random.nextInt(100)
            ));

            // 计算当前字符的X坐标
            int x = startX + i * charStep;
            // 可选：微调每个字符的偏移（例如上下浮动），避免过于整齐呆板
            int yOffset = random.nextInt(6) - 3;
            int y = yBase + yOffset;

            // 轻微倾斜（可选，但避免过度导致重叠）
            double angle = (random.nextDouble() - 0.5) * Math.toRadians(30); // 最大30度

            AffineTransform oldTransform = g2d.getTransform();
            AffineTransform transform = new AffineTransform();
            transform.translate(x, y);
            transform.rotate(angle);
            g2d.setTransform(transform);
            g2d.drawString(String.valueOf(c), 0, 0);
            g2d.setTransform(oldTransform);
        }
    }

    /** 添加随机噪点 */
    private static void drawNoise(Graphics2D g2d, int width, int height) {
        int noiseCount = 180;
        for (int i = 0; i < noiseCount; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            g2d.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            g2d.fillRect(x, y, 1, 1);
        }
    }
}
