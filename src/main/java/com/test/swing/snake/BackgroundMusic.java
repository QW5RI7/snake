package com.test.swing.snake;

import javazoom.jl.player.Player;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Random;

public class BackgroundMusic {
    private static final List<String> MUSIC_LIST = List.of(
            "/sound/1.mp3",
            "/sound/2.mp3",
            "/sound/3.mp3",
            "/sound/4.mp3"
    );
    private static final Random random = new Random();

    private static Player player;
    private static Thread playThread;
    private static boolean muted = false;
    private static boolean playing = false;

    public static synchronized void start() {
        if (muted || playing) return;
        stop(); // 确保干净状态
        playRandom();
    }

    private static void playRandom() {
        if (muted) return;
        String path = MUSIC_LIST.get(random.nextInt(MUSIC_LIST.size()));
        playLoop(path);
    }

    private static void playLoop(String musicPath) {
        playThread = new Thread(() -> {
            // 注意：不能使用 try-with-resources 自动关闭流，Player 会负责关闭
            InputStream is;
            try {
                is = BackgroundMusic.class.getResourceAsStream(musicPath);
                if (is == null) {
                    System.err.println("音乐文件不存在: " + musicPath);
                    return;
                }
                BufferedInputStream bis = new BufferedInputStream(is);
                player = new Player(bis);
                playing = true;
                player.play();  // 阻塞，播放完毕或异常时退出
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 确保资源释放
                if (player != null) {
                    player.close();
                    player = null;
                }
                // 注意：不要关闭 is 或 bis！Player.close() 已经做了。
                playing = false;
                // 如果未被静音且未被打断，则继续随机下一首
                if (!muted && playThread != null && !playThread.isInterrupted()) {
                    playRandom();
                }
            }
        });
        playThread.setDaemon(true);
        playThread.start();
    }

    public static synchronized void stop() {
        if (player != null) {
            player.close();
            player = null;
        }
        if (playThread != null) {
            playThread.interrupt();
            playThread = null;
        }
        playing = false;
    }

    public static void setMuted(boolean mute) {
        if (muted == mute) return;
        muted = mute;
        if (muted) {
            stop();
        } else {
            start();
        }
    }

    public static boolean isMuted() {
        return muted;
    }
}