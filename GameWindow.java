import javax.swing.*;
public class GameWindow extends JFrame {
    public GameWindow() {
        setTitle("Robber Run"); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(GamePanel.WIDTH, GamePanel.HEIGHT);     
        setLocationRelativeTo(null);      
        setResizable(false);                   
        add(new GamePanel());                         
        setVisible(true);                       
    }
    public static void main(String[] args) {
        new GameWindow();
    }
}
