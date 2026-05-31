package com.test.swing.snake;

import javax.swing.*;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadGame {
    private JProgressBar progressBar;
    private JFrame frame;
    private JLabel label;

    public void downloadWithGUI(String fileUrl, String savePath) {
        frame = new JFrame("文件下载");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 150);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        frame.add(progressBar, BorderLayout.CENTER);
        label = new JLabel("下载进度：" + 0 + "MB");
        frame.add(label, BorderLayout.SOUTH);
        frame.setVisible(true);

        new SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground(){
                try {
                    URL url = new URL(fileUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    long contentLength = conn.getContentLengthLong();

                    try (InputStream in = conn.getInputStream();
                         FileOutputStream out = new FileOutputStream(savePath)) {
                        byte[] buffer = new byte[8192];
                        int len;
                        long totalRead = 0;
                        while ((len = in.read(buffer)) != -1) {
                            out.write(buffer, 0, len);
                            totalRead += len;
                            if (contentLength > 0) {
                                int percent = (int) (totalRead * 100 / contentLength);
                                publish(percent); // 更新进度
                                double totalMb = Math.round(totalRead * 100 / (1024.0 * 1024)) / 100.0;
                                double fileMb = Math.round(contentLength * 100 / (1024.0 * 1024)) / 100.0;
                                SwingUtilities.invokeLater(() -> label.setText("下载进度：" + totalMb + "MB / " + fileMb + "MB"));
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void process(java.util.List<Integer> chunks) {
                int percent = chunks.get(chunks.size() - 1);
                progressBar.setValue(percent);
            }

            @Override
            protected void done() {
                DialogUtil.showDialog("提示", "下载完成！");
                frame.dispose();
            }
        }.execute();
    }
}
