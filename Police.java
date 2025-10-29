import java.awt.*;

public abstract class Police {
    protected int x, y;         
    protected int width, height; 
    protected int speed;      

    public Police(int x, int speed) {
        this.x = x;
        this.speed = speed;
    }

    
    public abstract void update();        
    public abstract void draw(Graphics g); 


    public Rectangle getBounds() {
        //เริ่มจากตำแหน่ง x, y โดยเลื่อนขึ้นจากพื้น120px
        return new Rectangle(x, y - height + 120, width, height);
    }

    /*คืนค่าพิกัด X ปัจจุบันของศัตรู */
    public int getX() { 
        return x; 
    }

    /* เพิ่มความเร็ว (ใช้ตอนเลเวลเพิ่มใน GamePanel) */
    public void increaseSpeed(int add) {
        speed += add;
    }
}
