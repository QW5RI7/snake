import com.test.swing.snake.*;

public class Test2 {
    public static void main(String[] args) {
        BackgroundMusic.start();
        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
