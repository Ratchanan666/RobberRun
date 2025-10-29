import java.awt.*;
import javax.swing.*;

public class Potion {
    private int x, y, speed;        
    private int width = 63, height = 63; 
    private boolean collected = false;  
    private Image img;

    public Potion(int x, int y, int speed) {
        this.x = x;
        this.y = y;
        this.speed = speed;

        img = new ImageIcon("assets/ui/Potionspeed.png").getImage();

    }

    public void update() {
        x -= speed;
    }

    public void draw(Graphics g) {
        if (!collected) {
            g.drawImage(img, x, y, width, height, null);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void collect() {
        collected = true;
    }

    public boolean isCollected() {
        return collected;
    }

    public int getX() {
        return x;
    }
}
