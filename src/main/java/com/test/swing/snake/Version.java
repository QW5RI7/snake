package com.test.swing.snake;

import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.swing.*;
import javax.json.Json;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Version {
    private static boolean forceUpdate;
    /** 检查版本 */
    private static boolean checkVersion(String DownloadPath) {
        // 检查版本是否最新
        try (JsonReader reader = Json.createReader(new FileReader("version.json"))) {
            JsonObject object = reader.readObject();
            String version = object.getString("version");
            try (JsonReader reader2 = Json.createReader(new FileReader(DownloadPath))) {
                JsonObject object2 = reader2.readObject();
                String version2 = object2.getString("version");
                forceUpdate = object2.getBoolean("force_update");
                if (CheckVersionNumber(version, version2)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static void checkVersion() {
        String DownloadJson = System.getProperty("user.home") + "/.snakegame/version.json";
        String DownloadGame = System.getProperty("user.home") + "/.snakegame/GameSetup.exe";
        // 检查安装包是否存在
        File file = new File(DownloadGame);
        if (file.exists()) {
            file.delete();
        }
        // 检查目录是否存在
        if (!new File(DownloadJson).exists()) {
            // 创建安装包目录
            new File(System.getProperty("user.home") + "/.snakegame").mkdirs();
        }
        // 操作在后台线程中
        new Thread(() -> {
            // 下载版本文件
            try (InputStream is = new URL("https://gitee.com/wang32412345/snake-game/releases/download/latest/version.json").openStream()) {
                Path path = Paths.get(DownloadJson);
                Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
                // 检查版本是否最新
                if (checkVersion(DownloadJson)) {
                    int[] result = {JOptionPane.YES_OPTION};
                    // 如果不是强制更新，弹窗提示用户更新游戏
                    if (!forceUpdate) {
                        SwingUtilities.invokeAndWait(() -> result[0] = DialogUtil.showDialog("版本更新","发现新版本，是否更新？"));
                    }
                    if (result[0] == JOptionPane.YES_OPTION) {
                        // 创建安装包目录
                        new File(System.getProperty("user.home") + "/.snakegame").mkdirs();
                        // 更新游戏安装包
                        SwingUtilities.invokeAndWait(() -> new DownloadGame().downloadWithGUI("https://gitee.com/wang32412345/snake-game/releases/download/latest/GameSetup.exe", DownloadGame));
                        // 打开安装程序
                        ProcessBuilder pb = new ProcessBuilder(DownloadGame);
                        pb.start();
                        // 关闭游戏
                        System.exit(0);
                    }
                }
            } catch (IOException | InvocationTargetException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    private static boolean CheckVersionNumber(String version, String newVersion) {
        String[] parts1 = version.split("\\.");
        String[] parts2 = newVersion.split("\\.");
        int maxLen = Math.max(parts1.length, parts2.length);
        for (int i = 0; i < maxLen; i++) {
            int num1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
            int num2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;
            if (num1 != num2) {
                return num1 < num2;
            }
        }
        return false;
    }
}
