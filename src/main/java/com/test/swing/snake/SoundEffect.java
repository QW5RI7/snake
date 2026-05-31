package com.test.swing.snake;

import javax.sound.sampled.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 音效播放工具类（单次播放，不循环）
 * 支持预加载和缓存
 */
public class SoundEffect {
    private static final Map<String, Clip> cache = new HashMap<>();
    private static boolean muted = false;  // 全局静音开关

    /**
     * 播放指定资源路径的音效（.wav 格式）
     * @param resourcePath 资源路径，如 "/sounds/eat.wav"
     */
    public static void play(String resourcePath) {
        if (muted) return;
        try {
            Clip clip = cache.get(resourcePath);
            if (clip == null) {
                AudioInputStream ais = AudioSystem.getAudioInputStream(
                        SoundEffect.class.getResource(resourcePath)
                );
                clip = AudioSystem.getClip();
                clip.open(ais);
                cache.put(resourcePath, clip);
            }
            clip.setFramePosition(0);   // 重置到开头，允许重叠播放或连续触发
            clip.start();
        } catch (Exception e) {
            // 避免因音效问题导致游戏崩溃，仅打印日志
            e.printStackTrace();
        }
    }

    /** 静音开关 */
    public static void setMuted(boolean mute) {
        muted = mute;
    }

    /** 获取当前静音状态 */
    public static boolean isMuted() {
        return muted;
    }
}
