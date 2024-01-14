import javax.swing.JFrame;

public class Game {
  public static void main(String[] args) {
    JFrame window = new JFrame("Mazes & Moles");
    window.setDefaultCloseOperation(3);
    window.setContentPane(new GamePanel());
    window.pack();
    window.setVisible(true);
    window.setResizable(false);
    window.setExtendedState(6);
  }
}
