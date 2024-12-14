import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Planner planner = new Planner();
            planner.setVisible(true);
        });
        try {
            Thread.sleep(150);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

    }
}