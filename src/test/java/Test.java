import com.test.swing.snake.*;
public class Test {
    public static void main(String[] args) throws Exception {
        DownloadGame downloadGame = new DownloadGame();
        downloadGame.downloadWithGUI("https://gitee.com/wang32412345/snake-game/releases/download/v1.0/GameSetup.exe", "src/test/java/GameSetup.exe");
    }
}
